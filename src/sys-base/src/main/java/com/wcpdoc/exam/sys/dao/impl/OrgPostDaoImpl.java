package com.wcpdoc.exam.sys.dao.impl;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.core.dao.impl.BaseDaoImpl;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.sys.dao.OrgPostDao;
import com.wcpdoc.exam.sys.entity.OrgPost;

/**
 * 机构岗位数据访问层实现
 * 
 * v1.0 zhanghc 2016-5-19下午9:32:43
 */
@Repository
public class OrgPostDaoImpl extends BaseDaoImpl<OrgPost> implements OrgPostDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return null;
	}

	@Override
	public void delByPostId(Integer postId) {
		String sql = "DELETE FROM SYS_ORG_POST WHERE POST_ID = ?";
		update(sql, postId);
	}
}