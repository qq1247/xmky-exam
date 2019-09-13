package com.wcpdoc.exam.sys.service;

import com.wcpdoc.exam.core.service.BaseService;
import com.wcpdoc.exam.sys.entity.PostUser;

/**
 * 岗位用户服务层接口
 * 
 * v1.0 zhanghc 2016-6-25下午10:47:45
 */
public interface PostUserService extends BaseService<PostUser> {

	/**
	 * 根据用户ID删除实体 
	 * 
	 * v1.0 zhanghc 2016-6-25下午10:47:45
	 * 
	 * @param userId
	 * void
	 */
	void delByUserId(Integer userId);

	/**
	 * 根据岗位ID删除实体 
	 * 
	 * v1.0 zhanghc 2016-6-25下午10:47:45
	 * 
	 * @param postId
	 * void
	 */
	void delByPostId(Integer postId);

}
