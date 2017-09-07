package com.wcpdoc.exam.sys.dao;

import java.util.List;
import java.util.Map;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.sys.entity.Org;
import com.wcpdoc.exam.sys.entity.Post;

/**
 * 岗位数据访问层接口
 * 
 * v1.0 zhanghc 2016-5-19下午9:32:43
 */
public interface PostDao extends BaseDao<Post>{
	/**
	 * 获取组织机构
	 * v1.0 zhanghc 2016-5-19下午9:32:43
	 * @param id
	 * @return Org
	 */
	Org getOrg(Integer id);
	
	/**
	 * 获取岗位列表
	 * v1.0 zhanghc 2016-5-19下午9:32:43
	 * @param orgId 
	 * @return List<Post>
	 */
	List<Post> getOrgPostList(Integer orgId);

	/**
	 * 获取机构岗位树型列表
	 * v1.0 zhanghc 2016-5-19下午9:32:43
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getOrgPostTreeList();

	/**
	 * 获取设置权限树型列表
	 * v1.0 zhanghc 2016-5-19下午9:32:43
	 * @param id 岗位ID
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getResUpdateResTreeList(Integer id);

	/**
	 * 获取组织机构
	 * v1.0 zhanghc 2016年7月9日下午11:42:15
	 * @param name
	 * @return Post
	 */
	Post getPostByName(String name);
}
