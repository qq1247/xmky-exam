package com.wcpdoc.exam.core.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.QuestionCommentDao;
import com.wcpdoc.exam.core.entity.QuestionComment;
import com.wcpdoc.exam.core.service.QuestionCommentService;

/**
 * 试题评论服务层实现
 * 
 * v1.0 zhanghc 2016-5-8上午11:00:00
 */
@Service
public class QuestionCommentServiceImpl extends BaseServiceImp<QuestionComment> implements QuestionCommentService {
	@Resource
	private QuestionCommentDao questionCommentDao;

	@Override
	@Resource(name = "questionCommentDaoImpl")
	public void setDao(BaseDao<QuestionComment> dao) {
		super.dao = dao;
	}

	@Override
	public void addEx(QuestionComment questionComment, Integer anonymity) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(questionComment.getContent())) {
			throw new MyException("参数错误：content");
		}
		if (questionComment.getQuestionId() == null || questionComment.getQuestionId() <= 0) {
			throw new MyException("参数错误：questionId");
		}
		if (anonymity == null) {
			throw new MyException("参数错误：anonymity");
		}
		
		// 添加试题评论
		if (questionComment.getParentId() == null) {
			questionComment.setParentId(0);
		}
		
		if (anonymity == 1) {// 0: 匿名  1： 不匿名
			questionComment.setCreateUserId(getCurUser().getId());
		}
		questionComment.setCreateTime(new Date());
		questionComment.setState(1);
		add(questionComment);
		
		// 更新父子关系
		if(questionComment.getParentId() == 0){
			questionComment.setParentSub("_" + questionComment.getId() + "_");
			questionComment.setLevel(questionComment.getParentSub().split("_").length - 1);
			update(questionComment);
			return;
		}
		QuestionComment parentQuestionComment = questionCommentDao.getEntity(questionComment.getParentId());
		questionComment.setParentSub(parentQuestionComment.getParentSub() + questionComment.getId() + "_");
		questionComment.setLevel(questionComment.getParentSub().split("_").length - 1);
		update(questionComment);
	}

	@Override
	public void delEx(Integer id) {
		// 校验数据有效性
		List<Map<String, Object>> list = questionCommentDao.getList(id);
		if (list.size() > 0) {
			throw new MyException("请先删除子试题评论");
		}
		
		// 删除试题评论
		QuestionComment questionComment = getEntity(id);
		questionComment.setState(0);
		questionComment.setUpdateTime(new Date());
		questionComment.setUpdateUserId(getCurUser().getId());
		update(questionComment);
	}

	@Override
	public List<Map<String, Object>> getList(Integer parentId) {
		List<Map<String, Object>> list = questionCommentDao.getList(parentId);
		for(Map<String, Object> map : list){
			if (map.get("createTime") != null) {
				map.put("createTime", DateUtil.formatDateTime( DateUtil.getDateTime(map.get("createTime").toString())));
			}
		}
		return list;
	}
}
