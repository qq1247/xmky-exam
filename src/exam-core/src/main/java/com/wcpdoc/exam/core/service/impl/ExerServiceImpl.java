package com.wcpdoc.exam.core.service.impl;

import java.util.Date;

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
import com.wcpdoc.exam.core.entity.QuestionBank;
import com.wcpdoc.exam.core.service.ExerRmkService;
import com.wcpdoc.exam.core.service.ExerService;
import com.wcpdoc.exam.core.service.QuestionAnswerService;
import com.wcpdoc.exam.core.service.QuestionBankService;
import com.wcpdoc.exam.core.service.QuestionOptionService;
import com.wcpdoc.exam.core.service.QuestionService;

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
	private QuestionBankService questionBankService;
	@Resource
	private BaseCacheService baseCacheService;

	@Override
	public RBaseDao<Exer> getDao() {
		return exerDao;
	}

	@Override
	public void add(Exer exer) {
		// 数据校验
		addValid(exer);

		// 练习添加
		exer.setUpdateUserId(getCurUser().getId());
		exer.setUpdateTime(new Date());
		exer.setState(1);
		exer.setRmkState(1);
		save(exer);
	}

	@Override
	public void updateEx(Exer exer) {
		// 数据校验
		Exer entity = getById(exer.getId());
		updateValid(exer, entity);

		// 练习修改
		entity.setName(exer.getName());
		// entity.setQuestionBankId(exer.getQuestionBankId());// 不允许修改
		entity.setUserIds(exer.getUserIds());
		entity.setOrgIds(exer.getOrgIds());
		// entity.setState(exer.getState());// 单独接口控制
		// entity.setRmkState(exer.getRmkState());// 单独接口控制
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
		// if (!ValidateUtil.isValid(exer.getQuestionBankId())) {
		// 	throw new MyException("参数错误：questionBankId");
		// }// 不修改，无须校验
		if (!ValidateUtil.isValid(exer.getRmkState())) {
			throw new MyException("参数错误：rmkState");
		}
		if (exer.getRmkState().intValue() != 1 && exer.getRmkState().intValue() != 2) {
			throw new MyException("值非法：rmkState");
		}
		if (!ValidateUtil.isValid(exer.getOrgIds()) && !ValidateUtil.isValid(exer.getUserIds())) {
			throw new MyException("参数错误：userIds");
		}

		QuestionBank questionBank = questionBankService.getById(exer.getQuestionBankId());// 只能练习自己的题库
		if (!(CurLoginUserUtil.isSelf(questionBank.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {
			throw new MyException("无操作权限");
		}
		if (!CurLoginUserUtil.isAdmin()) {// 只能管理自己的用户
			User curUser = baseCacheService.getUser(getCurUser().getId());
			if (ValidateUtil.isValid(exer.getUserIds())) {
				exer.getUserIds().forEach(userId -> {
					User user = baseCacheService.getUser(userId);
					if (!curUser.getUserIds().contains(user.getId())
							&& !curUser.getOrgIds().contains(user.getOrgId())) {
						throw new MyException("无用户操作权限");
					}
				});
			}
			if (ValidateUtil.isValid(exer.getOrgIds())) {
				if (!curUser.getOrgIds().containsAll(exer.getOrgIds())) {
					throw new MyException("无机构操作权限");
				}
			}
		}
	}

	private void addValid(Exer exer) {
		if (!ValidateUtil.isValid(exer.getName())) {
			throw new MyException("参数错误：name");
		}
		if (!ValidateUtil.isValid(exer.getQuestionBankId())) {
			throw new MyException("参数错误：questionBankId");
		}
		if (!ValidateUtil.isValid(exer.getRmkState())) {
			throw new MyException("参数错误：rmkState");
		}
		if (exer.getRmkState().intValue() != 1 && exer.getRmkState().intValue() != 2) {
			throw new MyException("值非法：rmkState");
		}
		if (!ValidateUtil.isValid(exer.getOrgIds()) && !ValidateUtil.isValid(exer.getUserIds())) {
			throw new MyException("参数错误：userIds");
		}
		Exer exerDb = exerDao.getExer(exer.getQuestionBankId());
		if (exerDb != null) {
			QuestionBank questionBank = questionBankService.getById(exer.getQuestionBankId());
			throw new MyException(String.format("题库【%s】已存在", questionBank.getName()));
		}

		QuestionBank questionBank = questionBankService.getById(exer.getQuestionBankId());// 只能练习自己的题库
		if (!(CurLoginUserUtil.isSelf(questionBank.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {
			throw new MyException("无操作权限");
		}
		if (!CurLoginUserUtil.isAdmin()) {// 只能管理自己的用户
			User curUser = baseCacheService.getUser(getCurUser().getId());
			if (ValidateUtil.isValid(exer.getUserIds())) {
				exer.getUserIds().forEach(userId -> {
					User user = baseCacheService.getUser(userId);
					if (!curUser.getUserIds().contains(user.getId())
							&& !curUser.getOrgIds().contains(user.getOrgId())) {
						throw new MyException("无用户操作权限");
					}
				});
			}
			if (ValidateUtil.isValid(exer.getOrgIds())) {
				if (!curUser.getOrgIds().containsAll(exer.getOrgIds())) {
					throw new MyException("无机构操作权限");
				}
			}
		}
	}
}
