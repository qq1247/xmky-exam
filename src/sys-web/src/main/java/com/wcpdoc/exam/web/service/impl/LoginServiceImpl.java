package com.wcpdoc.exam.web.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.security.auth.login.LoginException;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wcpdoc.exam.base.entity.User;
import com.wcpdoc.exam.base.service.OrgService;
import com.wcpdoc.exam.base.service.UserService;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.util.DateUtil;
import com.wcpdoc.exam.core.util.JwtUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.web.service.LoginService;

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
	public String in(String loginName, String pwd) throws LoginException{
		//校验数据有效性
		if(!ValidateUtil.isValid(loginName)) {
			throw new LoginException("参数错误：loginName");
		}
		if(!ValidateUtil.isValid(pwd)) {
			throw new LoginException("参数错误：pwd");
		}
		
		User user = userService.getUser(loginName);
		if(user == null || !user.getPwd().equals(userService.getEncryptPwd(loginName, pwd))) {
			throw new LoginException("用户名或密码错误！");
		}
		
		// 
		long tokenId = new Date().getTime();
		Map<String, Object> params = new HashMap<>();
		params.put("loginName", loginName);
		String accessToken = JwtUtil.createToken(tokenId + "", active, DateUtil.getNextMinute(new Date(), 60), params);
		
		//更新用户登录时间
		user.setLastLoginTime(new Date());
		userService.update(user);
		return accessToken;
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
