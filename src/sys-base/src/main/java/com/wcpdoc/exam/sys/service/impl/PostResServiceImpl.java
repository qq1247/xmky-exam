package com.wcpdoc.exam.sys.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.sys.dao.PostResDao;
import com.wcpdoc.exam.sys.entity.PostRes;
import com.wcpdoc.exam.sys.service.PostResService;

/**
 * 岗位资源服务层实现
 * 
 * v1.0 zhanghc 2016-6-11下午8:57:40
 */
@Service
public class PostResServiceImpl extends BaseServiceImp<PostRes> implements PostResService {
	@Resource
	private PostResDao postResDao;

	@Override
	@Resource(name = "postResDaoImpl")
	public void setDao(BaseDao<PostRes> dao) {
		super.dao = dao;
	}

	@Override
	public void delByPostId(Integer postId) {
		postResDao.delByPostId(postId);
	}

	@Override
	public void delByResId(Integer resId) {
		postResDao.delByResId(resId);
	}
}
