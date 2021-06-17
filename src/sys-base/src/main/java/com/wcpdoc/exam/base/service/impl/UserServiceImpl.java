package com.wcpdoc.exam.base.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.auth.cache.TokenCache;
import com.wcpdoc.exam.auth.realm.JWTRealm;
import com.wcpdoc.exam.base.dao.UserDao;
import com.wcpdoc.exam.base.entity.User;
import com.wcpdoc.exam.base.service.UserService;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.PageOut;
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
	private JWTRealm jwtRealm;

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
		if (getCurUser().getId() != getCurUser().getId().intValue()) {
			throw new MyException("非法操作！");
		}
		User user = getEntity(getCurUser().getId());
		if (getCurUser().getId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("非法操作！");
		}

		String oldEncryptPwd = getEncryptPwd(user.getLoginName(), oldPwd);
		if (!user.getPwd().equals(oldEncryptPwd)) {
			throw new MyException("原始密码错误！");
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
		jwtRealm.clearAuth(user.getId());
	}

	@Override
	public PageOut onList() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Integer> tokenCacheList = TokenCache.getList();
		for(Integer id : tokenCacheList){
			User entity = userDao.getEntity(id);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", entity.getId());
			map.put("name", entity.getName());
			map.put("loginName", entity.getLoginName());
			list.add(map);
		}
		
		PageOut pageOut = new PageOut();
		pageOut.setList(list);
		pageOut.setTotal(tokenCacheList.size());
		return pageOut;
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
