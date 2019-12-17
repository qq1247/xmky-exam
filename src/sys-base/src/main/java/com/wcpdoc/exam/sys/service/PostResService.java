package com.wcpdoc.exam.sys.service;

import com.wcpdoc.exam.core.service.BaseService;
import com.wcpdoc.exam.sys.entity.PostRes;

/**
 * 岗位资源服务层接口
 * 
 * v1.0 zhanghc 2016-6-11下午8:57:40
 */
public interface PostResService extends BaseService<PostRes> {

	/**
	 * 根据岗位ID删除实体 
	 * 
	 * v1.0 zhanghc 2016-6-11下午8:57:40
	 * 
	 * @param postId
	 * void
	 */
	void delByPostId(Integer postId);

	/**
	 * 根据资源ID删除实体 
	 * 
	 * v1.0 zhanghc 2016-6-11下午8:57:40
	 * 
	 * @param resId
	 * void
	 */
	void delByResId(Integer resId);

}
