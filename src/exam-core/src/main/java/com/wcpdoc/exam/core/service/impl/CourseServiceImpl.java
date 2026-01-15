package com.wcpdoc.exam.core.service.impl;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.base.util.CurLoginUserUtil;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.CourseDao;
import com.wcpdoc.exam.core.entity.Course;
import com.wcpdoc.exam.core.service.CourseService;
import com.wcpdoc.exam.core.util.CourseUtil;

import lombok.RequiredArgsConstructor;

/**
 * 课程服务层实现
 * 
 * v1.0 zhanghc 2026-01-12 10:03:53
 */
@Service
@RequiredArgsConstructor
public class CourseServiceImpl extends BaseServiceImp<Course> implements CourseService {
	private final CourseDao courseDao;
	private final BaseCacheService baseCacheService;

	@Override
	public RBaseDao<Course> getDao() {
		return courseDao;
	}

	@Override
	public void add(Course course) {
		// 数据校验
		addValid(course);

		// 课程添加
		course.setShareAuth(1);
		course.setState(1);
		course.setCreateUserId(getCurUser().getId());
		course.setUpdateTime(new Date());
		course.setUpdateUserId(getCurUser().getId());
		save(course);
	}

	@Override
	public void update(Course course) {
		// 数据校验
		updateValid0(course);
		Course entity = getById(course.getId());
		updateValid(entity);

		// 课程修改
		entity.setName(course.getName());
		entity.setContent(course.getContent());
		entity.setOrgIds(course.getOrgIds());
		entity.setUserIds(course.getUserIds());
		course.setUpdateUserId(getCurUser().getId());
		course.setUpdateTime(new Date());
		updateById(entity);
	}

	private void addValid(Course course) {
		if (!ValidateUtil.isValid(course.getName())) {
			throw new MyException("参数错误：name");
		}

		if (!ValidateUtil.isValid(course.getOrgIds()) && !ValidateUtil.isValid(course.getUserIds())) {
			throw new MyException("参数错误：userIds");
		}
		if (!CurLoginUserUtil.isAdmin()) {// 只能管理自己的用户
			User curUser = baseCacheService.getUser(getCurUser().getId());
			if (ValidateUtil.isValid(course.getUserIds())) {
				course.getUserIds().forEach(userId -> {
					User user = baseCacheService.getUser(userId);
					if (!curUser.getUserIds().contains(user.getId())
							&& !curUser.getOrgIds().contains(user.getOrgId())) {
						throw new MyException("无用户操作权限");
					}
				});
			}
			if (ValidateUtil.isValid(course.getOrgIds())) {
				if (!curUser.getOrgIds().containsAll(course.getOrgIds())) {
					throw new MyException("无机构操作权限");
				}
			}
		}
	}

	private void updateValid0(Course course) {
		if (!ValidateUtil.isValid(course.getId())) {
			throw new MyException("参数错误：id");
		}
		if (!ValidateUtil.isValid(course.getName())) {
			throw new MyException("参数错误：name");
		}

		if (!ValidateUtil.isValid(course.getOrgIds()) && !ValidateUtil.isValid(course.getUserIds())) {
			throw new MyException("参数错误：userIds");
		}
		if (!CurLoginUserUtil.isAdmin()) {// 只能管理自己的用户
			User curUser = baseCacheService.getUser(getCurUser().getId());
			if (ValidateUtil.isValid(course.getUserIds())) {
				course.getUserIds().forEach(userId -> {
					User user = baseCacheService.getUser(userId);
					if (!curUser.getUserIds().contains(user.getId())
							&& !curUser.getOrgIds().contains(user.getOrgId())) {
						throw new MyException("无用户操作权限");
					}
				});
			}
			if (ValidateUtil.isValid(course.getOrgIds())) {
				if (!curUser.getOrgIds().containsAll(course.getOrgIds())) {
					throw new MyException("无机构操作权限");
				}
			}
		}
	}

	private void updateValid(Course entity) {
		if (!(CurLoginUserUtil.isSelf(entity.getCreateUserId()) || CurLoginUserUtil.isAdmin()
				|| CourseUtil.hasWrite(entity))) {
			throw new MyException("无操作权限");
		}
	}

}
