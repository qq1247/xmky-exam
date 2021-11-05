package com.wcpdoc.exam.core.dao;

import java.util.List;
import java.util.Map;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.QuestionComment;

/**
 * 试题评论
 * 
 * v1.0 chenyun 2021年8月31日上午9:59:36
 */
public interface QuestionCommentDao extends BaseDao<QuestionComment>{

	/**
	 * 获取试题评论列表
	 * 
	 * v1.0 chenyun 2021年8月31日上午10:18:42
	 * @param parentId
	 * @return List<QuestionComment>
	 */
	List<Map<String, Object>> getList(Integer parentId);
}
