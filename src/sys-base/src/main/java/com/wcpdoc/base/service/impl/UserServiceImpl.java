package com.wcpdoc.base.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.wcpdoc.base.constant.BaseConstant;
import com.wcpdoc.base.dao.UserDao;
import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.core.dao.RBaseDao;
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
	private BaseCacheService baseCacheService;
	@Resource
	private OnlineUserService onlineUserService;

	@Override
	public RBaseDao<User> getDao() {
		return userDao;
	}

	@Override
	public void addEx(User user) {
		// 数据校验
		addValid(user);

		// 用户添加
		Date curTime = new Date();
		user.setRegistTime(curTime);
		user.setUpdateTime(curTime);
		user.setUpdateUserId(getCurUser().getId());
		user.setState(1);
		if (getCurUser().getType() == 0 && user.getType() == 1) {// 如果是管理员添加考试用户
			user.setOrgId(ValidateUtil.isValid(user.getOrgId()) ? user.getOrgId() : 1);// 页面没选机构，默认根机构
			user.setParentId(getCurUser().getId());// 考试用户归管理员管
		} else if (getCurUser().getType() == 0 && user.getType() == 2) {// 如果是管理员添加子管理员
			user.setOrgId(0);// 不属于任何机构
			user.setParentId(getCurUser().getId());// 子管理员归管理员管
		} else if (getCurUser().getType() == 0 && user.getType() == 3) {// 如果是管理员添加阅卷用户
			user.setOrgId(0);// 不属于任何机构
			user.setParentId(getCurUser().getId());// 阅卷用户归管理员管
		} else if (getCurUser().getType() == 2) {// 如果是子管理员添加阅卷用户
			user.setOrgId(0);// 不属于任何机构
			user.setParentId(getCurUser().getId());// 阅卷用户归子管理员管
		}
		save(user);
	}

	@Override
	@CacheEvict(value = BaseConstant.USER_CACHE, key = BaseConstant.USER_KEY_PRE + "#user.id")
	public void editEx(User user) {
		// 数据校验
		addValid(user);

		// 用户修改
		User entity = baseCacheService.getUser(user.getId());
		if (getCurUser().getType() == 0 && user.getType() == 1) {// 如果是管理员修改考试用户，更新机构信息
			entity.setOrgId(ValidateUtil.isValid(user.getOrgId()) ? user.getOrgId() : 1);// 页面没选机构，默认根机构
		} else if (getCurUser().getType() == 0 && user.getType() == 2) {// 如果是管理员修改子管理员
			entity.setUserIds(user.getUserIds());// 更新可管理的用户
		} else if (getCurUser().getType() == 2) {// 如果是子管理员，
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
	public void delEx(Integer id) {
		// 数据校验
		delValid(id);

		// 用户删除
		User user = baseCacheService.getUser(id);
		user.setState(0);
		user.setUpdateTime(new Date());
		user.setUpdateUserId(getCurUser().getId());
		updateById(user);

		// 如果是子管理，删除他的阅卷用户
		if (user.getType() == 2) {
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
		String newPwd = parm.getPwdType() == 1 ? StringUtil.getRandomStr(8) : parm.getPwdValue();
		user.setPwd(getEncryptPwd(user.getLoginName(), newPwd));
		updateById(user);
		return newPwd;
	}

	@Override
	@CacheEvict(value = BaseConstant.USER_CACHE, key = BaseConstant.USER_KEY_PRE + "#id")
	public void frozen(Integer id) {
		// 数据校验
		frozenValid(id);

		// 用户冻结
		User user = baseCacheService.getUser(id);
		user.setState(user.getState() == 1 ? 2 : 1);
		updateById(user);

		// 用户下线
		onlineUserService.out(id);
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
		if (!(user.getType() >= 1 && user.getType() <= 3)) {// 类型（0：管理员；1：考试用户；2：子管理员；3：阅卷用户）
			throw new MyException("参数错误：type");
		}
		// if (getCurUser().getType() == 0 && (user.getType() == 3)) {//
		// 当前用户是管理员，不能直接添加阅卷用户
		// throw new MyException("管理员不能直接添加阅卷用户");// 相对简单不启动子管理的情况下，管理员也能添加
		// }
		if (getCurUser().getType() == 1 || getCurUser().getType() == 3) {// 当前用户是考试用户或阅卷用户，不能添加用户
			throw new MyException("无权限");
		}
		if (getCurUser().getType() == 2 && user.getType() != 3) {// 当前用户是子管理员，只能添加阅卷用户
			throw new MyException("子管理员只能添加阅卷用户");
		}
		if (getCurUser().getType() != 0) { // 不是管理员，设置管理用户或机构无效
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
		if (!(getCurUser().getType() == 0 || getCurUser().getType() == 2)) {// 管理员和子管理才能删除用户
			throw new MyException("参数错误：type");
		}
		User user = baseCacheService.getUser(id);
		if (getCurUser().getType() == 0) {
			if (user.getType() == 0) {
				throw new MyException("管理员不能删除管理员");
			}
			if (user.getType() == 3) {
				throw new MyException("管理员不能删除阅卷用户");
			}
		}
		if (getCurUser().getType() == 2) {
			if (user.getType() == 0) {
				throw new MyException("子管理员不能删除管理员");
			}
			if (user.getType() == 1) {
				throw new MyException("子管理员不能删除考试用户");
			}
			if (user.getType() == 2) {
				throw new MyException("子管理员不能删除子管理员");
			}
		}
	}

	private void frozenValid(Integer id) {
		if (!ValidateUtil.isValid(id)) {
			throw new MyException("参数错误：id");
		}
		if (!(getCurUser().getType() == 0 || getCurUser().getType() == 2)) {// 类型（0：管理员；1：考试用户；2：子管理员；3：阅卷用户）
			throw new MyException("参数错误：type");
		}
		User user = baseCacheService.getUser(id);
		if (getCurUser().getType() == 0) {
			if (user.getType() == 0) {
				throw new MyException("管理员不能冻结管理员");
			}
			// if (user.getType() == 3) {
			// throw new MyException("管理员不能冻结阅卷用户");
			// }
		}
		if (getCurUser().getType() == 2) {
			if (user.getType() == 0) {
				throw new MyException("子管理员不能冻结管理员");
			}
			if (user.getType() == 1) {
				throw new MyException("子管理员不能冻结考试用户");
			}
			if (user.getType() == 2) {
				throw new MyException("子管理员不能冻结子管理员");
			}
		}

		if (user.getState() != 1 && user.getState() != 2) {
			throw new MyException("参数错误：id");
		}
	}

	private void pwdValid(Integer id) {
		if (!ValidateUtil.isValid(id)) {
			throw new MyException("参数错误：id");
		}
		if (!(getCurUser().getType() == 0 || getCurUser().getType() == 2)) {// 类型（0：管理员；1：考试用户；2：子管理员；3：阅卷用户）
			throw new MyException("参数错误：type");
		}
		User user = baseCacheService.getUser(id);
		if (getCurUser().getType() == 0) {
			if (user.getType() == 0) {
				throw new MyException("管理员不能初始化管理员密码");
			}
			// if (user.getType() == 3) {
			// throw new MyException("管理员不能初始化阅卷用户密码");
			// }
		}
		if (getCurUser().getType() == 2) {
			if (user.getType() == 0) {
				throw new MyException("子管理员不能初始化管理员密码");
			}
			if (user.getType() == 1) {
				throw new MyException("子管理员不能初始化考试用户密码");
			}
			if (user.getType() == 2) {
				throw new MyException("子管理员不能初始化子管理员密码");
			}
		}
	}
}
