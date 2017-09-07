package com.wcpdoc.exam.sys.dao;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.sys.entity.OrgPost;

/**
 * 机构岗位数据访问层接口
 * 
 * v1.0 zhanghc 2016-5-19下午9:32:43
 */
public interface OrgPostDao extends BaseDao<OrgPost>{

	/**
	 * 根据岗位id删除机构岗位
	 * v1.0 zhanghc 2016-5-19下午9:32:43
	 * @param postId
	 * void
	 */
	void deleteByPostId(Integer postId);
}
