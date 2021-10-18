package com.wcpdoc.exam.base.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.base.dao.UserDao;
import com.wcpdoc.exam.base.entity.User;
import com.wcpdoc.exam.base.service.UserExService;
import com.wcpdoc.exam.base.service.UserService;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.util.EncryptUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 用户服务层实现
 * 
 * v1.0 zhanghc 2016-6-15下午17:24:19
 */
@Service
public class UserServiceImpl extends BaseServiceImp<User> implements UserService {
	@Resource
	private UserDao userDao;
	@Resource
	private UserExService userExService;

	@Override
	@Resource(name = "userDaoImpl")
	public void setDao(BaseDao<User> dao) {
		super.dao = dao;
	}
	
	@Override
	public boolean existLoginName(User user) {
		return userDao.existLoginName(user.getLoginName(), user.getId());
	}

	@Override
	public User getUser(String loginName) {
		return userDao.getUser(loginName);
	}

	@Override
	public void doPwdUpdate(Integer id, String newPwd) {
		// 校验数据有效性
		if (id == null) {
			throw new MyException("参数错误：id");
		}
		if (!ValidateUtil.isValid(newPwd)) {
			throw new MyException("参数错误：newPwd");
		}

		// 初始化密码
		User user = getEntity(id);
		user.setPwd(getEncryptPwd(user.getLoginName(), newPwd));
		update(user);
	}

	@Override
	public void pwdUpdate(String oldPwd, String newPwd) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(oldPwd)) {
			throw new MyException("参数错误：oldPwd");
		}
		if (!ValidateUtil.isValid(newPwd)) {
			throw new MyException("参数错误：newPwd");
		}
		User user = getEntity(getCurUser().getId());
		String oldEncryptPwd = getEncryptPwd(user.getLoginName(), oldPwd);
		if (!user.getPwd().equals(oldEncryptPwd)) {
			throw new MyException("原始密码错误");
		}

		// 修改密码
		user.setPwd(getEncryptPwd(user.getLoginName(), newPwd));
		update(user);
	}

	@Override
	public String getEncryptPwd(String loginName, String pwd) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(loginName)) {
			throw new MyException("参数错误：loginName");
		}
		if (!ValidateUtil.isValid(pwd)) {
			throw new MyException("参数错误：pwd");
		}

		return EncryptUtil.md52Base64(loginName + pwd);
	}

	@Override
	public List<User> getList(Integer orgId) {
		return userDao.getList(orgId);
	}

	@Override
	public void roleUpdate(Integer id, String roles) {
		// 校验数据有效性
		if (id == null) {
			throw new MyException("参数错误：id");
		}
		User user = userDao.getEntity(id);
		if(user == null){
			throw new MyException("参数错误：id");
		}
		
		if (!ValidateUtil.isValid(roles)) {
			throw new MyException("参数错误：role");
		}
		
		// 更新角色
		user.setRoles(String.format("user,%s", roles));
		user.setUpdateTime(new Date());
		user.setUpdateUserId(getCurUser().getId());
		userDao.update(user);

		// 授权立即生效
		userExService.roleUpdate(user.getId());
	}

	@Override
	public void syncUser(List<User> user, Integer orgId) {
		Date date = new Date();
		for(User entity : user){
			entity.setPwd(getEncryptPwd(entity.getLoginName(), entity.getPwd()));
			entity.setRegistTime(date);
			entity.setOrgId(orgId);
			entity.setUpdateUserId(getCurUser().getId());
			entity.setUpdateTime(date);
			entity.setState(1);
			userDao.add(entity);
		}
	}
}
