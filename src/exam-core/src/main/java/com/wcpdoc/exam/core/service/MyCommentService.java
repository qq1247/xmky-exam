package com.wcpdoc.exam.core.service;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.MyComment;

/**
 * 我的评论服务层接口
 * 
 * v1.0 chenyun 2021年8月31日上午9:54:12
 */
public interface MyCommentService extends BaseService<MyComment> {

	/**
	 * 我的评论添加
	 * 
	 * v1.0 zhanghc 2025年12月3日下午4:50:35
	 * 
	 * @param questionId
	 * @param content    void
	 */
	void add(Integer questionId, String content);

	/**
	 * 我的评论回复
	 * 
	 * v1.0 zhanghc 2025年12月3日下午8:36:53
	 * 
	 * @param questionId
	 * @param content
	 * @param parentId   void
	 */
	void reply(Integer questionId, String content, Integer parentId);

	/**
	 * 点赞
	 * 
	 * v1.0 zhanghc 2023年4月17日下午7:53:32
	 * 
	 * @param id
	 * @param questionId 用于清除缓存 void
	 */
	void like(Integer id, Integer questionId);

}
