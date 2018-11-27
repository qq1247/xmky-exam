package com.wcpdoc.exam.sys.dao;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.sys.entity.PostUser;

/**
 * 岗位用户数据访问层接口
 * 
 * v1.0 zhanghc 2016-6-25下午10:47:45
 */
public interface PostUserDao extends BaseDao<PostUser>{

	/**
	 * 根据用户ID删除实体
	 * v1.0 zhanghc 2016-6-25下午10:47:45
	 * @param userId
	 * void
	 */
	void delByUserId(Integer userId);

	/**
	 * 根据用户ID删除实体
	 * v1.0 zhanghc 2016-6-25下午10:47:45
	 * @param postId
	 * void
	 */
	void delByPostId(Integer postId);
}
