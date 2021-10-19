package com.wcpdoc.exam.base.service.impl;

import java.util.Date;

import javax.annotation.Resource;
import javax.security.auth.login.LoginException;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.base.entity.Parm;
import com.wcpdoc.exam.base.entity.User;
import com.wcpdoc.exam.base.entity.UserToken;
import com.wcpdoc.exam.base.service.LoginService;
import com.wcpdoc.exam.base.service.ParmService;
import com.wcpdoc.exam.base.service.UserExService;
import com.wcpdoc.exam.base.service.UserService;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.service.OnlineUserService;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 登陆服务层实现
 * 
 * v1.0 zhanghc 2017-06-22 22:18:46
 */
@Service
public class LoginServiceImpl extends BaseServiceImp<Object> implements LoginService {
	private UserService userService;
	@Resource
	private UserExService userExService;
	@Resource
	private OnlineUserService onlineUserService;
	@Resource
	private ParmService parmService;
	
	@Override
	public void setDao(BaseDao<Object> dao) {
		
	}

	@Override
	public UserToken in(String loginName, String pwd) throws LoginException {
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
		
		// 生成令牌信息（登陆由shiro接收令牌控制）
		String accessToken = userExService.generateToken(user);
		
		//更新用户登录时间
		user.setLastLoginTime(new Date());
		userService.update(user);
		
		// 机构信息
		Parm parm = parmService.get();
		
		// 返回响应数据
		UserToken userToken = new UserToken();
		if (parm != null) {
			userToken.setOrgName(parm.getOrgName());
			userToken.setOrgLogo(parm.getOrgLogo());
		}
		userToken.setUserName(user.getName());
		userToken.setAccessToken(accessToken);
		userToken.setUserId(user.getId());
		if (user.getRoles() != null) {
			userToken.setRoles(user.getRoles().split(","));
		}
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
		userService.pwdUpdate(oldPwd, newPwd);
	}
}
