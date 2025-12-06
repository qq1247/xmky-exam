package com.wcpdoc.exam.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.constant.ExamConstant;
import com.wcpdoc.exam.core.dao.MyCommentDao;
import com.wcpdoc.exam.core.entity.MyComment;
import com.wcpdoc.exam.core.service.MyCommentService;

import lombok.RequiredArgsConstructor;

/**
 * 我的评论服务层实现
 * 
 * v1.0 zhanghc 2016-5-8上午11:00:00
 */
@Service
@RequiredArgsConstructor
public class MyCommentServiceImpl extends BaseServiceImp<MyComment> implements MyCommentService {
	private final MyCommentDao questionCommentDao;

	@Override
	public RBaseDao<MyComment> getDao() {
		return questionCommentDao;
	}

	@Override
	@CacheEvict(value = ExamConstant.MYCOMMENT_CACHE, key = ExamConstant.MYCOMMENT_LIST_KEY_PRE + "#questionId")
	public void add(Integer questionId, String content) {
		if (!ValidateUtil.isValid(questionId)) {
			throw new MyException("参数错误：questionId");
		}
		if (!ValidateUtil.isValid(content)) {
			throw new MyException("参数错误：content");
		}

		save(MyComment.builder()//
				.userId(getCurUser().getId())//
				.questionId(questionId)//
				.content(content)//
				.state(1)//
				.parentId(0)//
				.rootId(0)//
				.likeNum(0)//
				.updateUserId(getCurUser().getId())//
				.updateTime(new Date())//
				.build()//
		);
	}

	@Override
	@CacheEvict(value = ExamConstant.MYCOMMENT_CACHE, key = ExamConstant.MYCOMMENT_LIST_KEY_PRE + "#questionId")
	public void reply(Integer questionId, String content, Integer parentId) {
		if (!ValidateUtil.isValid(questionId)) {
			throw new MyException("参数错误：questionId");
		}
		if (!ValidateUtil.isValid(content)) {
			throw new MyException("参数错误：content");
		}
		if (!ValidateUtil.isValid(parentId)) {
			throw new MyException("参数错误：parentId");
		}
		MyComment parentMyComment = getById(parentId);
		if (parentMyComment == null) {
			throw new MyException("参数错误：parentId");
		}

		save(MyComment.builder()//
				.userId(getCurUser().getId())//
				.questionId(questionId)//
				.content(content)//
				.likeNum(0)//
				.state(1)//
				.replyUserId(parentMyComment.getUserId())//
				.parentId(parentId)//
				.rootId(parentMyComment.getRootId() == 0 ? parentMyComment.getId() : parentMyComment.getRootId())//
				.updateUserId(getCurUser().getId())//
				.updateTime(new Date())//
				.build()//
		);
	}

	@Override
	@CacheEvict(value = ExamConstant.MYCOMMENT_CACHE, key = ExamConstant.MYCOMMENT_LIST_KEY_PRE + "#questionId")
	public void like(Integer id, Integer questionId) {
		MyComment myComment = getById(id);
		List<Integer> likeUserIdList = myComment.getLikeUserIds();
		if (likeUserIdList.contains(getCurUser().getId())) {// 点过赞，取消
			likeUserIdList.remove(getCurUser().getId());
		} else {// 没点赞，添加
			likeUserIdList.add(getCurUser().getId());
		}

		myComment.setLikeUserIds(likeUserIdList);
		myComment.setLikeNum(likeUserIdList.size());// 点赞影响页面排序，可能漏数据，因为只是评论，不影响整体效果，不在做定时任务延时处理
		updateById(myComment);
	}
}
