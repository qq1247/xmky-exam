package com.wcpdoc.exam.core.service;

import java.util.List;
import java.util.Map;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.QuestionComment;

/**
 * 试题评论服务层接口
 * 
 * v1.0 chenyun 2021年8月31日上午9:54:12
 */
public interface QuestionCommentService extends BaseService<QuestionComment> {

	/**
	 * 添加试题评论
	 * 
	 * v1.0 chenyun 2021年8月31日下午1:55:07
	 * @param questionComment
	 * @param anonymity void
	 */
	void addEx(QuestionComment questionComment, Integer anonymity);

	/**
	 * 删除试题评论 
	 * 
	 * v1.0 chenyun 2021年8月31日上午9:54:28
	 * @param id void
	 */
	void delEx(Integer id);
	
	/**
	 * 获取试题评论列表
	 * 
	 * v1.0 chenyun 2021年8月31日上午10:18:42
	 * @param parentId
	 * @return List<QuestionComment>
	 */
	List<Map<String, Object>> getList(Integer parentId);
}
