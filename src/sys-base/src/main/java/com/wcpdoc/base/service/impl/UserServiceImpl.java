package com.wcpdoc.base.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.wcpdoc.base.constant.BaseConstant;
import com.wcpdoc.base.dao.UserDao;
import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.base.util.CurLoginUserUtil;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.EncryptUtil;
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.core.util.ValidateUtil;

import lombok.RequiredArgsConstructor;

/**
 * 用户服务层实现
 * 
 * v1.0 zhanghc 2016-6-15下午17:24:19
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends BaseServiceImp<User> implements UserService {
	private final UserDao userDao;
	private final BaseCacheService baseCacheService;

	@Override
	public RBaseDao<User> getDao() {
		return userDao;
	}

	@Override
	public void add(User user) {
		// 数据校验
		addValid(user);

		// 用户添加
		Date curTime = new Date();
		user.setRegistTime(curTime);
		user.setSource("管理员添加");
		user.setUpdateTime(curTime);
		user.setUpdateUserId(getCurUser().getId());
		user.setState(1);
		if (CurLoginUserUtil.isAdmin() && user.hasExamUser()) {// 如果是管理员添加考试用户
			user.setOrgId(ValidateUtil.isValid(user.getOrgId()) ? user.getOrgId() : 1);// 页面没选机构，默认根机构
			user.setParentId(getCurUser().getId());// 考试用户归管理员管
		} else if (CurLoginUserUtil.isAdmin() && user.hasSubAdmin()) {// 如果是管理员添加子管理员
			user.setOrgId(0);// 不属于任何机构
			user.setParentId(getCurUser().getId());// 子管理员归管理员管
		} else if (CurLoginUserUtil.isAdmin() && user.hasMarkUser()) {// 如果是管理员添加阅卷用户
			user.setOrgId(0);// 不属于任何机构
			user.setParentId(getCurUser().getId());// 阅卷用户归管理员管
		} else if (CurLoginUserUtil.isSubAdmin()) {// 如果是子管理员添加阅卷用户
			user.setOrgId(0);// 不属于任何机构
			user.setParentId(getCurUser().getId());// 阅卷用户归子管理员管
		}
		save(user);
	}

	@Override
	@CacheEvict(value = BaseConstant.USER_CACHE, key = BaseConstant.USER_KEY_PRE + "#user.id")
	public void edit(User user) {
		// 数据校验
		addValid(user);

		// 用户修改
		User entity = baseCacheService.getUser(user.getId());
		if (CurLoginUserUtil.isAdmin() && user.hasExamUser()) {// 如果是管理员修改考试用户，更新机构信息
			entity.setOrgId(ValidateUtil.isValid(user.getOrgId()) ? user.getOrgId() : 1);// 页面没选机构，默认根机构
		} else if (CurLoginUserUtil.isAdmin() && user.hasSubAdmin()) {// 如果是管理员修改子管理员
			entity.setUserIds(user.getUserIds());// 更新可管理的用户
			entity.setOrgIds(user.getOrgIds());// 更新可管理的机构
		} else if (CurLoginUserUtil.isSubAdmin()) {// 如果是子管理员，
			// 没有特殊需要处理的
		}
		// entity.setType(null); // 不允许修改类型
		entity.setName(user.getName());
		entity.setLoginName(user.getLoginName());
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		updateById(entity);
	}

	@Override
	@CacheEvict(value = BaseConstant.USER_CACHE, key = BaseConstant.USER_KEY_PRE + "#id")
	public void del(Integer id) {
		// 数据校验
		delValid(id);

		// 用户删除
		User user = baseCacheService.getUser(id);
		user.setState(0);
		user.setUpdateTime(new Date());
		user.setUpdateUserId(getCurUser().getId());
		updateById(user);

		// 如果是子管理，删除他的阅卷用户
		if ("SUB_ADMIN".equals(user.getRole())) {
			List<User> markUserList = userDao.getMarkUserlist(user.getId());
			for (User markUser : markUserList) {
				markUser.setState(0);
				markUser.setUpdateTime(new Date());
				markUser.setUpdateUserId(getCurUser().getId());
				updateById(user);
			}
		}
	}

	@Override
	public User getUser(String loginName) {
		return userDao.getUser(loginName);
	}

	@Override
	@CacheEvict(value = BaseConstant.USER_CACHE, key = BaseConstant.USER_KEY_PRE + "#id")
	public String pwdInit(Integer id) {
		// 数据校验
		pwdValid(id);

		// 修改密码
		User user = baseCacheService.getUser(id);
		Parm parm = baseCacheService.getParm();
		String newPwd = parm.getPwdType() == 1 ? StringUtil.getRandom(8) : parm.getPwdValue();
		user.setPwd(getEncryptPwd(user.getLoginName(), newPwd));
		user.setUpdateUserId(getCurUser().getId());
		user.setUpdateTime(new Date());
		updateById(user);
		return newPwd;
	}

	@Override
	@CacheEvict(value = BaseConstant.USER_CACHE, key = BaseConstant.USER_KEY_PRE + "#id")
	public void frozen(Integer id, Integer state) {
		// 数据校验
		frozenValid(id, state);

		// 用户冻结
		User user = baseCacheService.getUser(id);
		user.setState(state);
		user.setUpdateUserId(getCurUser().getId());
		user.setUpdateTime(new Date());
		updateById(user);
	}

	@Override
	@CacheEvict(value = BaseConstant.USER_CACHE, key = BaseConstant.USER_KEY_PRE + "#id")
	public void avatar(Integer avatarFileId) {
		// 数据校验
		avatarValid(avatarFileId);

		// 头像更新
		User user = baseCacheService.getUser(getCurUser().getId());
		user.setAvatarFileId(avatarFileId);
		user.setUpdateUserId(getCurUser().getId());
		user.setUpdateTime(new Date());
		updateById(user);
	}

	@Override
	public String getEncryptPwd(String loginName, String pwd) {
		// 数据校验
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
	public List<User> getList() {
		return userDao.getList();
	}

	@Override
	public List<User> getMarkUserList(Integer parentId) {
		return userDao.getMarkUserlist(parentId);
	}

	private void addValid(User user) {
		if (!ValidateUtil.isValid(user.getLoginName())) {
			throw new MyException("参数错误：loginName");
		}
		if (!"EXAM_USER".equals(user.getRole()) && !"SUB_ADMIN".equals(user.getRole())
				&& !"MARK_USER".equals(user.getRole())) {
			throw new MyException("参数错误：role");
		}
		// if (CurLoginUserUtil.isAdmin() && ("MARK_USER".equals(user.getRole()))) {//
		// 当前用户是管理员，不能直接添加阅卷用户
		// throw new MyException("管理员不能直接添加阅卷用户");// 相对简单不启动子管理的情况下，管理员也能添加
		// }
		if (CurLoginUserUtil.isExamUser() || CurLoginUserUtil.isMarkUser()) {// 当前用户是考试用户或阅卷用户，不能添加用户
			throw new MyException("无权限");
		}
		if (CurLoginUserUtil.isSubAdmin() && !"MARK_USER".equals(user.getRole())) {// 当前用户是子管理员，只能添加阅卷用户
			throw new MyException("子管理员只能添加阅卷用户");
		}
		if (!CurLoginUserUtil.isAdmin()) { // 不是管理员，设置管理用户或机构无效
			if (ValidateUtil.isValid(user.getOrgIds())) {
				throw new MyException("非管理员，设置管理用户无效");
			}
			if (ValidateUtil.isValid(user.getUserIds())) {
				throw new MyException("非管理员，设置机构无效");
			}
		}
		if (userDao.existLoginName(user.getLoginName(), user.getId())) {
			throw new MyException("登录账号已存在");
		}
	}

	private void delValid(Integer id) {
		if (!ValidateUtil.isValid(id)) {
			throw new MyException("参数错误：id");
		}
		if (!(CurLoginUserUtil.isAdmin() || CurLoginUserUtil.isSubAdmin())) {// 管理员和子管理才能删除用户
			throw new MyException("参数错误：type");
		}
		User user = baseCacheService.getUser(id);
		if (CurLoginUserUtil.isAdmin()) {
			if ("ADMIN".equals(user.getRole())) {
				throw new MyException("管理员不能删除管理员");
			}
//			if ("MARK_USER".equals(user.getRole())) {// bug修复：自己创建的自己不能删除
//				throw new MyException("管理员不能删除阅卷用户");
//			}
		}
		if (CurLoginUserUtil.isSubAdmin()) {
			if ("ADMIN".equals(user.getRole())) {
				throw new MyException("子管理员不能删除管理员");
			}
			if ("EXAM_USER".equals(user.getRole())) {
				throw new MyException("子管理员不能删除考试用户");
			}
			if ("SUB_ADMIN".equals(user.getRole())) {
				throw new MyException("子管理员不能删除子管理员");
			}
		}
	}

	private void frozenValid(Integer id, Integer state) {
		if (!ValidateUtil.isValid(id)) {
			throw new MyException("参数错误：id");
		}
		if (!ValidateUtil.isValid(state)) {
			throw new MyException("参数错误：state");
		}
		if (!(CurLoginUserUtil.isAdmin() || CurLoginUserUtil.isSubAdmin())) {// 类型（0：管理员；1：考试用户；2：子管理员；3：阅卷用户）
			throw new MyException("参数错误：type");
		}
		User user = baseCacheService.getUser(id);
		if (CurLoginUserUtil.isAdmin()) {
			if ("ADMIN".equals(user.getRole())) {
				throw new MyException("管理员不能冻结管理员");
			}
			// if ("MARK_USER".equals(user.getRole())) {
			// throw new MyException("管理员不能冻结阅卷用户");
			// }
		}
		if (CurLoginUserUtil.isSubAdmin()) {
			if ("ADMIN".equals(user.getRole())) {
				throw new MyException("子管理员不能冻结管理员");
			}
			if ("EXAM_USER".equals(user.getRole())) {
				throw new MyException("子管理员不能冻结考试用户");
			}
			if ("SUB_ADMIN".equals(user.getRole())) {
				throw new MyException("子管理员不能冻结子管理员");
			}
		}

		if (user.getState() != 1 && user.getState() != 2) {
			throw new MyException("参数错误：id");
		}

		if (state != 1 && state != 2) {
			throw new MyException("参数错误：state");
		}
		if (state == 1 && user.getState() == 1) {
			throw new MyException("用户已正常");
		}
		if (state == 2 && user.getState() == 2) {
			throw new MyException("用户已冻结");
		}
	}

	private void avatarValid(Integer avatarFileId) {
		if (!ValidateUtil.isValid(avatarFileId)) {
			throw new MyException("参数错误：avatarFileId");
		}
	}

	private void pwdValid(Integer id) {
		if (!ValidateUtil.isValid(id)) {
			throw new MyException("参数错误：id");
		}
		if (!(CurLoginUserUtil.isAdmin() || CurLoginUserUtil.isSubAdmin())) {// 类型（0：管理员；1：考试用户；2：子管理员；3：阅卷用户）
			throw new MyException("参数错误：type");
		}
		User user = baseCacheService.getUser(id);
		if (CurLoginUserUtil.isAdmin()) {
			if ("ADMIN".equals(user.getRole())) {
				throw new MyException("管理员不能初始化管理员密码");
			}
			// if ("MARK_USER".equals(user.getRole())) {
			// throw new MyException("管理员不能初始化阅卷用户密码");
			// }
		}
		if (CurLoginUserUtil.isSubAdmin()) {
			if ("ADMIN".equals(user.getRole())) {
				throw new MyException("子管理员不能初始化管理员密码");
			}
			if ("EXAM_USER".equals(user.getRole())) {
				throw new MyException("子管理员不能初始化考试用户密码");
			}
			if ("SUB_ADMIN".equals(user.getRole())) {
				throw new MyException("子管理员不能初始化子管理员密码");
			}
		}
	}

	@Override
	public boolean existLoginName(String loginName, Integer excludeId) {
		return userDao.existLoginName(loginName, excludeId);
	}

}
