package com.wcpdoc.exam.base.service.impl;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wcpdoc.exam.base.entity.User;
import com.wcpdoc.exam.base.entity.UserToken;
import com.wcpdoc.exam.base.service.LoginService;
import com.wcpdoc.exam.base.service.OrgService;
import com.wcpdoc.exam.base.service.UserService;
import com.wcpdoc.exam.core.constant.ConstantManager;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.util.DateUtil;
import com.wcpdoc.exam.core.util.JwtUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 登陆服务层实现
 * 
 * v1.0 zhanghc 2017-06-22 22:18:46
 */
@Service
public class LoginServiceImpl extends BaseServiceImp<Object> implements LoginService {
	@Value("${spring.profiles.active")
	private String active;
	@Resource
	private UserService userService;
	@Resource
	private OrgService orgService;

	@Override
	public void setDao(BaseDao<Object> dao) {
		
	}

	@Override
	public UserToken in(String loginName, String pwd) throws LoginException{
		//校验数据有效性
		if(!ValidateUtil.isValid(loginName)) {
			throw new LoginException("参数错误：loginName");
		}
		if(!ValidateUtil.isValid(pwd)) {
			throw new LoginException("参数错误：pwd");
		}
		
		User user = userService.getUser(loginName);
		if(user == null /*|| !user.getPwd().equals(userService.getEncryptPwd(loginName, pwd))*/) {
			throw new LoginException("用户名或密码错误！");
		}
		
		// 生成令牌信息（登陆由shiro接收令牌控制）
		Date expTime = DateUtil.getNextMinute(new Date(), 60);
		String accessToken = JwtUtil.getInstance()
			.createToken(user.getId().toString(), active, expTime)
			.addAttr("loginName", loginName)
			.build();
		String refreshToken = JwtUtil.getInstance()
				.createToken(user.getId().toString(), active, expTime)
				.addAttr("loginName", loginName)
				.addAttr("refreshToken", true)
				.build();
		
		//用户信息、权限信息写入session
		HttpSession session = request.getSession(true);//参数为false，第一次在页面直接访问login/doIn，session为null
		session.setAttribute(ConstantManager.USER, user);
		
		Map<Integer, Long> userAuthMap = userService.getAuth(user.getId());
		session.setAttribute(ConstantManager.USER_AUTH_MAP, userAuthMap);
		
		//更新用户登录时间
		user.setLastLoginTime(new Date());
		userService.update(user);
		
		// 返回响应数据
		UserToken userToken = new UserToken();
		userToken.setId(user.getId());
		userToken.setLoginName(user.getLoginName());
		userToken.setName(user.getName());
		userToken.setAccessToken(accessToken);
		userToken.setRefreshToken(refreshToken);
		return userToken;
	}

	@Override
	public void out() {
		SecurityUtils.getSubject().logout();
	}
	
	@Override
	public void pwdUpdate(String oldPwd, String newPwd) {
		userService.doPwdUpdate(oldPwd, newPwd);
	}
}
