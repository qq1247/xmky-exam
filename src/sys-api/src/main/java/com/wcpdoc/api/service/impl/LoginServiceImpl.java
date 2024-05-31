package com.wcpdoc.api.service.impl;

import java.util.Date;

import javax.annotation.Resource;
import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wcpdoc.api.entity.UserToken;
import com.wcpdoc.api.service.LoginService;
import com.wcpdoc.auth.cache.TokenCache;
import com.wcpdoc.auth.util.JwtUtil;
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

		User user = userService.getUser(loginName);
		if (user == null || !user.getPwd().equals(userService.getEncryptPwd(loginName, pwd))) {
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
