package com.wcpdoc.exam.core.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.ExerRmkDao;
import com.wcpdoc.exam.core.entity.Exer;
import com.wcpdoc.exam.core.entity.ExerRmk;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.service.ExamCacheService;
import com.wcpdoc.exam.core.service.ExerRmkService;
import com.wcpdoc.exam.core.service.ExerService;

/**
 * 练习评论服务层实现
 * 
 * v1.0 zhanghc 2016-5-8上午11:00:00
 */
@Service
public class ExerRmkServiceImpl extends BaseServiceImp<ExerRmk> implements ExerRmkService {
	@Resource
	private ExerRmkDao exerRmkDao;
	@Resource
	private ExamCacheService examCacheService;
	@Resource
	@Lazy
	private ExerService exerService;

	@Override
	public RBaseDao<ExerRmk> getDao() {
		return exerRmkDao;
	}

	@Override
	public void rmk(ExerRmk exerRmk, Boolean anon) {
		// 数据有效性校验
		if (!ValidateUtil.isValid(exerRmk.getExerId())) {
			throw new MyException("参数错误：exerId");
		}
		if (!ValidateUtil.isValid(exerRmk.getQuestionId())) {
			throw new MyException("参数错误：questionId");
		}
		if (!ValidateUtil.isValid(exerRmk.getContent())) {
			throw new MyException("参数错误：content");
		}
		if (!ValidateUtil.isValid(anon)) {
			throw new MyException("参数错误：anon");
		}
		Exer exer = exerService.getById(exerRmk.getExerId());
		if (exer.getState() == 0) {
			throw new MyException("无权限");
		}
		long curTime = System.currentTimeMillis();
		if (!(exer.getStartTime().getTime() < curTime && curTime < exer.getEndTime().getTime())) {
			throw new MyException("时间已过期");
		}
		if (!exer.getUserIds().contains(getCurUser().getId())) {
			throw new MyException("无权限");
		}
		Question question = examCacheService.getQuestion(exerRmk.getQuestionId());
		if (question.getQuestionTypeId().intValue() != exer.getQuestionTypeId().intValue()) {
			throw new MyException("无权限");
		}
		if (exer.getRmkState() == 2) {
			throw new MyException("评论已关闭");
		}

		// 评论添加
		exerRmk.setLikeNum(0);
		exerRmk.setState(1);
		exerRmk.setLikeUserIds(null);
		if (!anon) {// 如果匿名评论，不记录评论用户
			exerRmk.setUpdateUserId(getCurUser().getId());
		}
		exerRmk.setUpdateTime(new Date());
		save(exerRmk);
	}

	@Override
	public void like(Integer id) {
		ExerRmk exerRmk = getById(id);
		List<Integer> likeUserIdList = exerRmk.getLikeUserIds();
		if (likeUserIdList.contains(getCurUser().getId())) {// 点过赞，取消
			likeUserIdList.remove(getCurUser().getId());
		} else {// 没点赞，添加
			likeUserIdList.add(getCurUser().getId());
		}

		exerRmk.setLikeUserIds(likeUserIdList);
		exerRmk.setLikeNum(likeUserIdList.size());// 点赞影响页面排序，可能漏数据，因为只是评论，不影响整体效果，不在做定时任务延时处理
		updateById(exerRmk);
	}
}
