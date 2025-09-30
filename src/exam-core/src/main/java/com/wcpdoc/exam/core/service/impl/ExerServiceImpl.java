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
		exer.setCreateUserId(getCurUser().getId());
		exer.setUpdateUserId(getCurUser().getId());
		exer.setUpdateTime(new Date());
		exer.setState(1);
		save(exer);
	}

	@Override
	public void update(Exer exer) {
		// 数据校验
		updateValid(exer);

		// 练习修改
		Exer entity = getById(exer.getId());
		entity.setName(exer.getName());
		// entity.setQuestionBankIds(exer.getQuestionBankIds());// 不允许修改
		entity.setUserIds(exer.getUserIds());
		entity.setOrgIds(exer.getOrgIds());
		// entity.setState(exer.getState());// 单独接口控制
		entity.setUpdateUserId(getCurUser().getId());
		entity.setUpdateTime(new Date());
		updateById(entity);
	}

	private void addValid(Exer exer) {
		if (!ValidateUtil.isValid(exer.getName())) {
			throw new MyException("参数错误：name");
		}
		if (!ValidateUtil.isValid(exer.getQuestionBankIds())) {
			throw new MyException("参数错误：questionBankIds");
		}
		if (!ValidateUtil.isValid(exer.getOrgIds()) && !ValidateUtil.isValid(exer.getUserIds())) {
			throw new MyException("参数错误：userIds");
		}
		for (Integer questionBankId : exer.getQuestionBankIds()) {
			QuestionBank questionBank = questionBankService.getById(questionBankId);// 只能练习自己的题库
			if (!(CurLoginUserUtil.isSelf(questionBank.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {
				throw new MyException("无操作权限");
			}
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

	private void updateValid(Exer exer) {
		if (!ValidateUtil.isValid(exer.getId())) {
			throw new MyException("参数错误：id");
		}

		if (!ValidateUtil.isValid(exer.getName())) {
			throw new MyException("参数错误：name");
		}
		// if (!ValidateUtil.isValid(exer.getQuestionBankIds())) {
		// throw new MyException("参数错误：questionBankIds");
		// }// 不修改，无须校验
		if (!ValidateUtil.isValid(exer.getOrgIds()) && !ValidateUtil.isValid(exer.getUserIds())) {
			throw new MyException("参数错误：userIds");
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
