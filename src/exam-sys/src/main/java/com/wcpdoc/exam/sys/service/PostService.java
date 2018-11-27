package com.wcpdoc.exam.sys.service;

import java.util.List;
import java.util.Map;

import com.wcpdoc.exam.core.service.BaseService;
import com.wcpdoc.exam.sys.entity.Org;
import com.wcpdoc.exam.sys.entity.Post;
/**
 * 岗位服务层接口
 * 
 * v1.0 zhanghc 2016-5-19下午9:32:43
 */
public interface PostService extends BaseService<Post>{

	/**
	 * 保存岗位
	 * v1.0 zhanghc 2016-5-19下午9:32:43
	 * @param post
	 * @param orgId
	 * void
	 */
	void saveAndUpdate(Post post, Integer orgId);

	/**
	 * 修改岗位
	 * v1.0 zhanghc 2016年6月15日上午11:25:39
	 * @param post
	 * void
	 */
	void editAndUpdate(Post post);
	
	/**
	 * 删除岗位
	 * v1.0 zhanghc 2016-5-19下午9:32:43
	 * @param ids 
	 * void
	 */
	void delAndUpdate(Integer[] ids);
	
	/**
	 * 获取组织机构
	 * v1.0 zhanghc 2016-5-19下午9:32:43
	 * @param id 岗位id
	 * @return Org
	 */
	Org getOrg(Integer id);

	/**
	 * 获取岗位列表
	 * v1.0 zhanghc 2016-5-19下午9:32:43
	 * @param orgId 
	 * @return List<Post>
	 */
	List<Post> getList(Integer orgId);

	/**
	 * 获取机构岗位树
	 * v1.0 zhanghc 2016-5-19下午9:32:43
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getOrgPostTreeList();

	/**
	 * 获取设置权限树
	 * v1.0 zhanghc 2016-5-19下午9:32:43
	 * @param id 岗位ID
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getResUpdateResTreeList(Integer id);

	/**
	 * 完成设置权限
	 * v1.0 zhanghc 2016年6月15日上午11:25:39
	 * @param id
	 * @param resIds
	 * void
	 */
	void doResUpdate(Integer id, Integer[] resIds);
}
