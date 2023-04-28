package com.wcpdoc.base.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.base.cache.ParmCache;
import com.wcpdoc.base.dao.UserDao;
import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.ParmService;
import com.wcpdoc.base.service.UserExService;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.OnlineUserService;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.EncryptUtil;
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.core.util.ValidateUtil;

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
	@Resource
	private ParmService parmService;
	@Resource
	private OnlineUserService onlineUserService;
	
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
	public String pwdUpdate(Integer id) {
		// 校验数据有效性
		if (id == null) {
			throw new MyException("参数错误：id");
		}
		
		// 修改密码
		Parm parm = ParmCache.get();
		String newPwd = parm.getPwdType() == 1 ? StringUtil.getRandomStr(8) : parm.getPwdValue();
		
		User user = getEntity(id);
		user.setPwd(getEncryptPwd(user.getLoginName(), newPwd));
		update(user);
		return newPwd;
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
	public List<User> getList(Integer[] ids) {
		return userDao.getList(ids);
	}

	@Override
	public List<User> getList() {
		return userDao.getList();
	}
	
	@Override
	public void frozen(Integer id) {
		// 校验数据有效性
		User user = getEntity(id);
		if (user.getState() != 1 && user.getState() != 2) {
			throw new MyException("参数错误：id");
		}
		
		// 冻结用户
		user.setState(user.getState() == 1 ? 2 : 1);
		userDao.update(user);
		
		// 用户下线
		onlineUserService.out(id);
	}
}
