package com.wcpdoc.exam.sys.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.sys.dao.PostUserDao;
import com.wcpdoc.exam.sys.entity.PostUser;
import com.wcpdoc.exam.sys.service.PostUserService;
/**
 * 岗位用户服务层实现
 * 
 * v1.0 zhanghc 2016-6-25下午10:47:45
 */
@Service
public class PostUserServiceImpl extends BaseServiceImp<PostUser> implements PostUserService{
	@Resource
	private PostUserDao postUserDao;

	@Override
	@Resource(name = "postUserDaoImpl")
	public void setDao(BaseDao<PostUser> dao) {
		super.dao = dao;
	}

	@Override
	public void deleteByUserId(Integer userId) {
		postUserDao.deleteByUserId(userId);
	}

	@Override
	public void deleteByPostId(Integer postId) {
		postUserDao.deleteByPostId(postId);
	}
}
