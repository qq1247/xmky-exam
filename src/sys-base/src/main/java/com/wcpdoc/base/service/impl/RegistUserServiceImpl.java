package com.wcpdoc.base.service.impl;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.wcpdoc.base.dao.RegistUserDao;
import com.wcpdoc.base.entity.RegistUser;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.RegistUserService;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.ValidateUtil;

import lombok.RequiredArgsConstructor;

/**
 * 用户服务层实现
 * 
 * v1.0 zhanghc 2025年12月1日下午7:02:37
 */
@Service
@RequiredArgsConstructor
public class RegistUserServiceImpl extends BaseServiceImp<RegistUser> implements RegistUserService {
	private final RegistUserDao registUserDao;
	private final UserService userService;

	@Override
	public RBaseDao<RegistUser> getDao() {
		return registUserDao;
	}

	@Override
	public boolean existLoginName(String loginName) {
		return registUserDao.existLoginName(loginName);
	}

	@Override
	public void approve(Integer id, String remark) {
		// 数据校验
		if (!ValidateUtil.isValid(id)) {
			throw new MyException("参数错误：id");
		}
		if (!ValidateUtil.isValid(remark)) {
			throw new MyException("参数错误：remark");
		}
		RegistUser registUser = getById(id);
		if (registUser.getState() == 1 || registUser.getState() == 2) {
			throw new MyException("已审核");
		}
		if (userService.existLoginName(registUser.getLoginName(), null)) {
			throw new MyException("登录账号已存在");
		}

		// 同意
		registUser.setRemark(remark);
		registUser.setState(1);
		registUser.setUpdateUserId(1);
		registUser.setUpdateTime(new Date());
		updateById(registUser);

		// 用户添加
		userService.save(User.builder()//
				.loginName(registUser.getLoginName())//
				.pwd(registUser.getPwd())//
				.name(registUser.getName())//
				.orgId(registUser.getOrgId())//
				.registTime(new Date())//
				.role("EXAM_USER")//
				.source("用户注册")//
				.state(1)//
				.parentId(getCurUser().getId())//
				.updateTime(new Date())//
				.updateUserId(getCurUser().getId())//
				.build()//
		);

	}
}
