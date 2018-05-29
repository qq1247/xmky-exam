package com.wcpdoc.exam.sys.dao.impl;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.core.dao.impl.BaseDaoImpl;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.sys.dao.PostResDao;
import com.wcpdoc.exam.sys.entity.PostRes;

/**
 * 岗位资源数据访问层实现
 * 
 * v1.0 zhanghc 2016-6-11下午8:57:40
 */
@Repository
public class PostResDaoImpl extends BaseDaoImpl<PostRes> implements PostResDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return null;
	}

	@Override
	public void delByPostId(Integer postId) {
		String sql = "DELETE FROM SYS_POST_RES WHERE POST_ID = ?";
		update(sql, postId);
	}

	@Override
	public void delByResId(Integer resId) {
		String sql = "DELETE FROM SYS_POST_RES WHERE RES_ID = ?";
		update(sql, resId);
	}
}