package com.wcpdoc.exam.base.service;

import java.util.List;

import com.wcpdoc.exam.base.entity.Post;
import com.wcpdoc.exam.base.entity.Res;
import com.wcpdoc.exam.core.service.BaseService;

/**
 * 岗位服务层接口
 * 
 * v1.0 zhanghc 2016-5-19下午9:32:43
 */
public interface PostService extends BaseService<Post> {

	/**
	 * 获取岗位列表 
	 * 
	 * v1.0 zhanghc 2016-5-19下午9:32:43
	 * 
	 * @param orgId
	 * @return List<Post>
	 */
	List<Post> getOrgPostList(Integer orgId);

	/**
	 * 名称是否重复
	 * 
	 * v1.0 zhanghc 2020年8月24日下午1:56:20
	 * @param post
	 * @return boolean
	 */
	boolean existName(Post post);

	/**
	 * 编码是否重复
	 * 
	 * v1.0 zhanghc 2020年8月24日下午1:56:20
	 * @param post
	 * @return boolean
	 */
	boolean existCode(Post post);

	/**
	 * 获取资源列表
	 * 
	 * v1.0 zhanghc 2020年8月26日下午2:26:01
	 * @param id
	 * @return List<Res>
	 */
	List<Res> getResList(Integer id);

	/**
	 * 获取岗位列表
	 * 
	 * v1.0 zhanghc 2020年8月26日下午5:29:05
	 * @return List<Post>
	 */
	List<Post> getResPostList(Integer resId);

	/**
	 * 获取岗位列表
	 * 
	 * v1.0 zhanghc 2020年8月26日下午6:34:14
	 * @param orgId
	 * @return List<Post>
	 */
	List<Post> getList(Integer orgId);

}
