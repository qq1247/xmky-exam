package com.wcpdoc.exam.core.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.base.util.CurLoginUserUtil;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.ExerDao;
import com.wcpdoc.exam.core.entity.Exer;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.service.ExerRmkService;
import com.wcpdoc.exam.core.service.ExerService;
import com.wcpdoc.exam.core.service.QuestionAnswerService;
import com.wcpdoc.exam.core.service.QuestionOptionService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionTypeService;

/**
 * 练习服务层实现
 * 
 * v1.0 chenyun 2021-03-02 13:43:21
 */
@Service
public class ExerServiceImpl extends BaseServiceImp<Exer> implements ExerService {
	@Resource
	private ExerDao exerDao;
	@Resource
	private QuestionService questionService;
	@Resource
	private QuestionOptionService questionOptionService;
	@Resource
	private QuestionAnswerService questionAnswerService;
	@Resource
	private ExerRmkService exerRmkService;
	@Resource
	private QuestionTypeService questionTypeService;
	@Resource
	private BaseCacheService baseCacheService;

	@Override
	public RBaseDao<Exer> getDao() {
		return exerDao;
	}

	@Override
	public void addEx(Exer exer) {
		// 数据校验
		addValid(exer);

		// 练习添加
		exer.setCreateUserId(getCurUser().getId());
		exer.setUpdateUserId(getCurUser().getId());
		exer.setUpdateTime(new Date());
		exer.setState(1);
		save(exer);
	}

	@Override
	public void updateEx(Exer exer) {
		// 数据校验
		Exer entity = getById(exer.getId());
		updateValid(exer, entity);

		// 练习修改
		entity.setName(exer.getName());
		entity.setQuestionTypeId(exer.getQuestionTypeId());
		entity.setStartTime(exer.getStartTime());
		entity.setEndTime(exer.getEndTime());
		entity.setUserIds(exer.getUserIds());
		// entity.setState(exer.getState());
		entity.setRmkState(exer.getRmkState());
		entity.setUpdateUserId(getCurUser().getId());
		entity.setUpdateTime(new Date());
		updateById(entity);
	}

	private void updateValid(Exer exer, Exer entity) {
		if (!ValidateUtil.isValid(exer.getId())) {
			throw new MyException("参数错误：id");
		}
		if (!ValidateUtil.isValid(exer.getName())) {
			throw new MyException("参数错误：name");
		}
		if (!ValidateUtil.isValid(exer.getQuestionTypeId())) {
			throw new MyException("参数错误：questionTypeId");
		}
		if (!ValidateUtil.isValid(exer.getStartTime())) {
			throw new MyException("参数错误：startTime");
		}
		if (!ValidateUtil.isValid(exer.getEndTime())) {
			throw new MyException("参数错误：endTime");
		}
		if (!ValidateUtil.isValid(exer.getRmkState())) {
			throw new MyException("参数错误：rmkState");
		}
		if (entity.getState() == 0) {
			throw new MyException("已删除");
		}
		if (entity.getEndTime().getTime() <= System.currentTimeMillis()) {
			throw new MyException("练习已结束");
		}
//		if (entity.getStartTime().getTime() <= System.currentTimeMillis()) {
//			throw new MyException("练习已开始");// 考试结束放在考试开始前校验，可能的问题为考试已结束，提示的是考试已开始。
//		}
		if (!(CurLoginUserUtil.isSelf(entity.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {
			throw new MyException("无操作权限");
		}
		QuestionType questionType = questionTypeService.getById(exer.getQuestionTypeId());// 只能练习自己的题库
		if (!(CurLoginUserUtil.isSelf(questionType.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {
			throw new MyException("无操作权限");
		}
		if (getCurUser().getType() != 0) {
			User curUser = baseCacheService.getUser(getCurUser().getId());
			List<Integer> haveUsers = curUser.getUserIds();
			List<Integer> exerUsers = exer.getUserIds();
			if (!haveUsers.containsAll(exerUsers)) {// 只能练习自己的考试用户
				throw new MyException("无操作权限");
			}
		}

		List<Exer> exerList = exerDao.getList(exer.getQuestionTypeId());
		for (Exer cur : exerList) {

			if (cur.getId().intValue() == exer.getId().intValue()) {// 如果变更题库，该行无效；如果同一个题库，校验的时候排除自己。
				continue;
			}
			if (exer.getStartTime().getTime() <= cur.getStartTime().getTime()
					&& cur.getStartTime().getTime() <= exer.getEndTime().getTime()) {
				throw new MyException("该时间段已存在");
			}
			if (exer.getStartTime().getTime() <= cur.getEndTime().getTime()
					&& cur.getEndTime().getTime() <= exer.getEndTime().getTime()) {
				throw new MyException("该时间段已存在");
			}
			if (exer.getStartTime().getTime() >= cur.getStartTime().getTime()
					&& cur.getEndTime().getTime() >= exer.getEndTime().getTime()) {
				throw new MyException("该时间段已存在");
			}
		}
	}

	private void addValid(Exer exer) {
		if (!ValidateUtil.isValid(exer.getName())) {
			throw new MyException("参数错误：name");
		}
		if (!ValidateUtil.isValid(exer.getQuestionTypeId())) {
			throw new MyException("参数错误：questionTypeId");
		}
		if (!ValidateUtil.isValid(exer.getStartTime())) {
			throw new MyException("参数错误：startTime");
		}
		if (!ValidateUtil.isValid(exer.getEndTime())) {
			throw new MyException("参数错误：endTime");
		}
		if (!ValidateUtil.isValid(exer.getRmkState())) {
			throw new MyException("参数错误：rmkState");
		}

		QuestionType questionType = questionTypeService.getById(exer.getQuestionTypeId());// 只能练习自己的题库
		if (!(CurLoginUserUtil.isSelf(questionType.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {
			throw new MyException("无操作权限");
		}
		if (getCurUser().getType() != 0) {
			User curUser = baseCacheService.getUser(getCurUser().getId());
			List<Integer> haveUsers = curUser.getUserIds();
			List<Integer> exerUsers = exer.getUserIds();
			if (!haveUsers.containsAll(exerUsers)) {// 只能练习自己的考试用户
				throw new MyException("无操作权限");
			}
		}

		List<Exer> exerList = exerDao.getList(exer.getQuestionTypeId());
		for (Exer cur : exerList) {
			if (cur.getCreateUserId().intValue() != getCurUser().getId().intValue()) {// 管理员和子管理，同一时间创建同一个题库，提示了错误但是相互看不见。改为自己的不允许重复
				continue;
			}

			if (exer.getStartTime().getTime() <= cur.getStartTime().getTime()
					&& cur.getStartTime().getTime() <= exer.getEndTime().getTime()) {
				throw new MyException("该时间段已存在");
			}
			if (exer.getStartTime().getTime() <= cur.getEndTime().getTime()
					&& cur.getEndTime().getTime() <= exer.getEndTime().getTime()) {
				throw new MyException("该时间段已存在");
			}
			if (exer.getStartTime().getTime() >= cur.getStartTime().getTime()
					&& cur.getEndTime().getTime() >= exer.getEndTime().getTime()) {
				throw new MyException("该时间段已存在");
			}
		}
	}

	@Override
	public List<Exer> getList(Integer questionTypeId) {
		return exerDao.getList(questionTypeId);
	}
}
