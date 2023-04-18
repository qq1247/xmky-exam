package com.wcpdoc.exam.core.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.ExerRmkDao;
import com.wcpdoc.exam.core.entity.Exer;
import com.wcpdoc.exam.core.entity.ExerRmk;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.service.ExerRmkService;
import com.wcpdoc.exam.core.service.ExerService;
import com.wcpdoc.exam.core.service.QuestionService;

/**
 * 模拟练习评论服务层实现
 * 
 * v1.0 zhanghc 2016-5-8上午11:00:00
 */
@Service
public class ExerRmkServiceImpl extends BaseServiceImp<ExerRmk> implements ExerRmkService {
	@Resource
	private ExerRmkDao exerRmkDao;
	@Resource
	private QuestionService questionService;
	@Resource
	private ExerService exerService;

	@Override
	@Resource(name = "exerRmkDaoImpl")
	public void setDao(BaseDao<ExerRmk> dao) {
		super.dao = dao;
	}

	@Override
	public void addEx(ExerRmk exerRmk, Integer anon) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(exerRmk.getQuestionId())) {
			throw new MyException("参数错误：questionId");
		}
		if (!ValidateUtil.isValid(exerRmk.getContent())) {
			throw new MyException("参数错误：content");
		}
		if (ValidateUtil.isValid(exerRmk.getLikeUserIds())) {// 添加不添加点赞
			throw new MyException("参数错误：likeUserIds");
		}
		if (ValidateUtil.isValid(exerRmk.getLikeNum())) {// 添加不添加点赞
			throw new MyException("参数错误：likeUserIds");
		}
		Question question = questionService.getEntity(exerRmk.getQuestionId());
		List<Exer> exerList = exerService.getList(question.getQuestionTypeId());
		
		Date curTime = new Date();
		boolean hasExerTime = false;
		for (Exer exer : exerList) {
			if (exer.getStartTime().getTime() < curTime.getTime() && curTime.getTime() < exer.getEndTime().getTime()) {
				hasExerTime = true;
				break;
			}
		}
		if (!hasExerTime) {
			throw new MyException("无权限");
		}
		
		// 模拟练习评论添加
		if (anon != 1) {// 如果匿名评论，不记录评论用户
			exerRmk.setUpdateUserId(getCurUser().getId());
		}
		exerRmk.setUpdateTime(new Date());
		exerRmk.setState(1);
		add(exerRmk);
	}

	@Override
	public void like(Integer id) {
		// 数据有效性校验
		ExerRmk exerRmk = getEntity(id);
		Set<Integer> likeUserIdSet = new HashSet<Integer>(Arrays.asList(exerRmk.getLikeUserIds()));
		if (likeUserIdSet.contains(id)) {// 如果点过赞，不处理
			throw new MyException("已点赞");
		}
		
		// 点赞
		likeUserIdSet.add(id);
		exerRmk.setLikeUserIds(likeUserIdSet.toArray(new Integer[likeUserIdSet.size()]));
		exerRmk.setLikeNum(likeUserIdSet.size());// 点赞影响页面排序，可能漏数据，因为只是评论，不影响整体效果，不在做定时任务延时处理
		update(exerRmk);
	}
}
