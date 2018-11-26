package com.wcpdoc.exam.home.service.impl;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.constant.ConstantManager;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.exam.service.ExamService;
import com.wcpdoc.exam.home.service.HomeService;
import com.wcpdoc.exam.sys.entity.User;
import com.wcpdoc.exam.sys.service.UserService;

/**
 * 首页服务层实现
 * 
 * v1.0 zhanghc 2017-06-23 15:39:11
 */
@Service
public class HomeServiceImpl extends BaseServiceImp<Object> implements HomeService {
	@Resource
	private UserService userService;
	@Resource
	private ExamService examService;

	@Override
	public void setDao(BaseDao<Object> dao) {
		
	}

	@Override
	public Map<Integer, Long> getAuthSum(Integer userId) {
		return userService.getAuthSum(userId);
	}

	@Override
	public User doIn(String loginName, String pwd, HttpServletRequest request) {
		//校验数据有效性
		if(!ValidateUtil.isValid(loginName)){
			throw new RuntimeException("无法获取参数：loginName");
		}
		if(!ValidateUtil.isValid(pwd)){
			throw new RuntimeException("无法获取参数：pwd");
		}
		
		User user = userService.getUser(loginName);
		if(user == null || !user.getPwd().equals(userService.getEncryptPwd(loginName, pwd))){
			throw new RuntimeException("用户名或密码错误！");
		}
		
		//用户信息、权限信息写入session
		HttpSession session = request.getSession(true);//参数为false，第一次在页面直接访问login/doIn，session为null
		session.setAttribute(ConstantManager.USER, user);
		
		Map<Integer, Long> userAuthMap = userService.getAuthSum(user.getId());
		session.setAttribute(ConstantManager.USER_AUTH_MAP, userAuthMap);
		
		//更新用户登录时间
		user.setLastLoginTime(new Date());
		userService.update(user);
		return user;
	}

	@Override
	public void doOut(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if(session == null){
			return;
		}
		
		session.invalidate();
	}
}
