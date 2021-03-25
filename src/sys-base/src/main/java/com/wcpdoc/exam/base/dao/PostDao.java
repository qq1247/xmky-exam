package com.wcpdoc.exam.base.dao;

import java.util.List;

import com.wcpdoc.exam.base.entity.Post;
import com.wcpdoc.exam.base.entity.Res;
import com.wcpdoc.exam.core.dao.RBaseDao;

/**
 * 岗位数据访问层接口
 * 
 * v1.0 zhanghc 2016-5-19下午9:32:43
 */
public interface PostDao extends RBaseDao<Post> {

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
	 * 名称是否存在
	 * 
	 * v1.0 zhanghc 2020-06-10 12:40:00
	 * 
	 * @param name
	 * @param excludeId
	 * @return boolean
	 */
	boolean existName(String name, Integer excludeId);

	/**
	 * 编码是否存在
	 * 
	 * v1.0 zhanghc 2020-06-10 12:40:00
	 * 
	 * @param name
	 * @param excludeId
	 * @return boolean
	 */
	boolean existCode(String code, Integer excludeId);

	/**
	 * 获取资源列表
	 * 
	 * v1.0 zhanghc 2020年8月26日下午2:26:38
	 * @param id
	 * @return List<Res>
	 */
	List<Res> getResList(Integer id);

	/**
	 * 获取岗位列表
	 * 
	 * v1.0 zhanghc 2020年8月26日下午5:29:35
	 * @param resId
	 * @return List<Post>
	 */
	List<Post> getResPostList(Integer resId);

	/**
	 * 获取岗位列表
	 * 
	 * v1.0 zhanghc 2020年8月26日下午6:34:44
	 * @param orgId
	 * @return List<Post>
	 */
	List<Post> getList(Integer orgId);
	
	/**
	 * 获取岗位列表
	 * 
	 * v1.0 chenyun 2021年3月16日下午5:15:40
	 * @return List<Post>
	 */
	List<Post> getList();
}
