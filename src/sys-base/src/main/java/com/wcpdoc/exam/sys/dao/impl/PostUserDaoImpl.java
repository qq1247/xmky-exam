package com.wcpdoc.exam.sys.dao.impl;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.core.dao.impl.BaseDaoImpl;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.sys.dao.PostUserDao;
import com.wcpdoc.exam.sys.entity.PostUser;

/**
 * 岗位用户数据访问层实现
 * 
 * v1.0 zhanghc 2016-6-25下午10:47:45
 */
@Repository
public class PostUserDaoImpl extends BaseDaoImpl<PostUser> implements PostUserDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return null;
	}

	@Override
	public void delByUserId(Integer userId) {
		String sql = "DELETE FROM SYS_POST_USER WHERE USER_ID = ?";
		update(sql, userId);
	}

	@Override
	public void delByPostId(Integer postId) {
		String sql = "DELETE FROM SYS_POST_USER WHERE POST_ID = ?";
		update(sql, postId);
	}
}