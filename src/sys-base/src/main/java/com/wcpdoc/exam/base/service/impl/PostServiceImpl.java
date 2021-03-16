package com.wcpdoc.exam.base.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.base.dao.PostDao;
import com.wcpdoc.exam.base.entity.Post;
import com.wcpdoc.exam.base.entity.Res;
import com.wcpdoc.exam.base.service.PostService;
import com.wcpdoc.exam.base.service.ResService;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;

/**
 * 岗位服务层实现
 * 
 * v1.0 zhanghc 2016-5-19下午9:32:43
 */
@Service
public class PostServiceImpl extends BaseServiceImp<Post> implements PostService {
	@Resource
	private PostDao postDao;
	@Resource
	private ResService resService;

	@Override
	@Resource(name = "postDaoImpl")
	public void setDao(BaseDao<Post> dao) {
		super.dao = dao;
	}

	@Override
	public List<Post> getOrgPostList(Integer orgId) {
		return postDao.getOrgPostList(orgId);
	}

	@Override
	public boolean existName(Post post) {
		return postDao.existName(post.getName(), post.getId());
	}

	@Override
	public boolean existCode(Post post) {
		return postDao.existCode(post.getCode(), post.getId());
	}

	@Override
	public List<Res> getResList(Integer id) {
		return postDao.getResList(id);
	}

	@Override
	public List<Post> getResPostList(Integer resId) {
		return postDao.getResPostList(resId);
	}

	@Override
	public List<Post> getList(Integer orgId) {
		return postDao.getList(orgId);
	}

	@Override
	public Post getPost(String name) {
		return postDao.getPost(name);
	}
}
