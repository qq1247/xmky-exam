package com.wcpdoc.api.service.impl;

import java.util.Date;

import javax.annotation.Resource;
import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Service;

import com.wcpdoc.api.entity.UserToken;
import com.wcpdoc.api.service.LoginService;
import com.wcpdoc.auth.cache.TokenCache;
import com.wcpdoc.auth.util.JwtUtil;
import com.wcpdoc.base.constant.BaseConstant;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.base.service.UserExService;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.LoginUser;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.OnlineUserService;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.RsaUtil;
import com.wcpdoc.core.util.SpringUtil;
import com.wcpdoc.core.util.ValidateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 登陆服务层实现
 * 
 * v1.0 zhanghc 2017-06-22 22:18:46
 */
@Service
@Slf4j
public class LoginServiceImpl extends BaseServiceImp<Object> implements LoginService {

	@Resource
	private UserService userService;
	@Resource
	private UserExService userExService;
	@Resource
	private OnlineUserService onlineUserService;
	@Resource
	private BaseCacheService baseCacheService;

	@Value("${token.expireMinute}")
	private Integer tokenExpireMinute;

	@Override
	public RBaseDao<Object> getDao() {
		return null;
	}

	@Override
	public UserToken in(String loginName, String pwd) throws LoginException {
		// 数据校验
		if (!ValidateUtil.isValid(loginName)) {
			throw new LoginException("参数错误：loginName");
		}
		if (!ValidateUtil.isValid(pwd)) {
			throw new LoginException("参数错误：pwd");
		}

		String plainText = RsaUtil.decrypt(pwd);
		String[] plainTexts = plainText.split(":"); // loginName:nonce:pwd
		if (plainTexts.length != 3) {
			throw new LoginException("数据格式错误");
		}
		if (!plainTexts[0].equals(loginName)) {
			throw new LoginException("数据格式错误");
		}

		Cache nonceCache = SpringUtil.getBean(EhCacheCacheManager.class).getCache(BaseConstant.NONCE_CACHE);
		ValueWrapper valueWrapper = nonceCache.get(String.format("%s:%s", plainTexts[0], plainTexts[1]));
		if (valueWrapper == null) {// 防重放，使用一次后失效
			throw new LoginException("请求已过期或重复提交");
		}
		Long timestamp = (Long) valueWrapper.get();
		if (System.currentTimeMillis() - timestamp > 3000) { // 作用：弱网、服务器繁忙，攻击者延迟重放的请求先到达。不防止毫秒级重放。
			throw new LoginException("nonce超时");
		}
		nonceCache.evict(String.format("%s:%s", plainTexts[0], plainTexts[1]));

		User user = userService.getUser(loginName);
		Cache userLockCache = SpringUtil.getBean(EhCacheCacheManager.class).getCache(BaseConstant.USER_LOCK_CACHE);
		ValueWrapper lockWrapper = userLockCache.get(loginName);
		if (lockWrapper != null) {
			Long failCount = (Long) lockWrapper.get();
			if (failCount >= 5) {
				throw new LoginException("账号已被锁定，请1分钟后重试");
			}
		}

		if (user == null || !user.getPwd().equals(userService.getEncryptPwd(loginName, plainTexts[2]))) {
			Long currentCount = lockWrapper != null ? (Long) lockWrapper.get() : 0L;
			currentCount++;
			userLockCache.put(loginName, currentCount);

			throw new LoginException("用户名或密码错误");
		}
		if (user.getState() == 2) {
			throw new LoginException("账号已被冻结");
		}

		// 生成令牌（登陆由shiro接收令牌控制）
		Date curTime = new Date();
		Long tokenId = curTime.getTime();
		Date expTime = DateUtil.getNextMinute(curTime, tokenExpireMinute);
		String accessToken = JwtUtil.getInstance().createToken(tokenId.toString(), "default", expTime)
				.addAttr("userId", user.getId()).addAttr("loginName", user.getLoginName())
				.addAttr("type", user.getType()).build();
		if (log.isDebugEnabled()) {
			log.debug("shiro权限：用户【{}】登陆，旧令牌创建时间【{}】，当前令牌创建时间【{}】", user.getLoginName(), null,
					DateUtil.formatDateTime(new Date(tokenId)));
		}

		TokenCache.put(user.getId(), accessToken);// 缓存刷新令牌（用于续租登陆）

		// 更新用户登录时间
		user.setLastLoginTime(new Date());
		userService.updateById(user);

		// 返回响应数据
		UserToken userToken = new UserToken();
		userToken.setUserName(user.getName());
		userToken.setAccessToken(accessToken);
		userToken.setUserId(user.getId());
		userToken.setType(user.getType());
		return userToken;
	}

	@Override
	public UserToken noLogin(String name) throws LoginException {
		// 数据校验
		if (!ValidateUtil.isValid(name)) {
			throw new LoginException("参数错误：name");
		}
		if (name.length() < 2) {
			throw new LoginException("参数错误：name必须大于2位");
		}
		if (name.length() > 16) {
			throw new LoginException("参数错误：name必须小于16位");
		}

//		if (!Pattern.compile("^1[3-9]\\d{9}$").matcher(phone).matches()) {
//			throw new LoginException("手机号格式错误");
//		}

		// 生成匿名用户
		User user = new User();
		user.setName(name);
		user.setLoginName(null);// 无登录账号
		user.setType(4);
		user.setRegistTime(new Date());
		user.setOrgId(0);
		user.setState(1);
		user.setUpdateUserId(1);
		user.setUpdateTime(new Date());
		userService.save(user);

		// 生成令牌（登陆由shiro接收令牌控制）
		Date curTime = new Date();
		Long tokenId = curTime.getTime();
		Date expTime = DateUtil.getNextMinute(curTime, tokenExpireMinute);
		String accessToken = JwtUtil.getInstance().createToken(tokenId.toString(), "default", expTime)
				.addAttr("userId", user.getId()).addAttr("loginName", user.getLoginName())
				.addAttr("type", user.getType()).build();
		if (log.isDebugEnabled()) {
			log.debug("shiro权限：用户【{}】免登陆，旧令牌创建时间【{}】，当前令牌创建时间【{}】", user.getName(), null,
					DateUtil.formatDateTime(new Date(tokenId)));
		}

		TokenCache.put(user.getId(), accessToken);// 缓存刷新令牌（用于续租登陆）

		// 更新用户登录时间
		user.setLastLoginTime(new Date());
		userService.updateById(user);

		// 返回响应数据
		UserToken userToken = new UserToken();
		userToken.setUserName(user.getName());
		userToken.setAccessToken(accessToken);
		userToken.setUserId(user.getId());
		userToken.setType(user.getType());
		return userToken;
	}

	@Override
	public void out() {
		LoginUser curUser = getCurUser();
		if (curUser == null) {
			return;
		}
		onlineUserService.out(getCurUser().getId());
	}

	@Override
	public void pwdUpdate(String oldPwd, String newPwd) {
		// 数据校验
		if (!ValidateUtil.isValid(oldPwd)) {
			throw new MyException("参数错误：oldPwd");
		}
		if (!ValidateUtil.isValid(newPwd)) {
			throw new MyException("参数错误：newPwd");
		}
		User user = baseCacheService.getUser(getCurUser().getId());
		String oldEncryptPwd = userService.getEncryptPwd(user.getLoginName(), oldPwd);
		if (!user.getPwd().equals(oldEncryptPwd)) {
			throw new MyException("原始密码错误");
		}

		// 修改密码
		user.setPwd(userService.getEncryptPwd(user.getLoginName(), newPwd));
		userService.updateById(user);
	}

}
