package com.wcpdoc.exam.core.service.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.wcpdoc.exam.core.constant.ConstantManager;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.service.BaseService;

/**
 * 基础服务层实现
 * 
 * v1.0 zhanghc 2015-6-19下午08:30:16
 * 
 * @param <T>
 */
public abstract class BaseServiceImp<T> implements BaseService<T> {
	@Resource
	protected HttpServletRequest request;
	@Resource
	protected HttpServletResponse response;
	protected BaseDao<T> dao;

	public abstract void setDao(BaseDao<T> dao);
	
	@Override
	public LoginUser getCurUser() {
		HttpSession session = request.getSession(false);
		return session == null ? null : (LoginUser) session.getAttribute(ConstantManager.USER);
	}

	@Override
	public /*final*/ void add(T entity) {
		dao.add(entity);
	}

	@Override
	public /*final*/ void update(T entity) {
		dao.update(entity);
	}

	@Override
	public /*final*/ void del(Object id) {
		dao.del(id);
	}

	@Override
	public /*final*/ void del(Object[] ids) {
		dao.del(ids);
	}

	@Override
	public /*final*/ T getEntity(Object id) {
		return dao.getEntity(id);
	}

	@Override
	public /*final*/ PageOut getListpage(PageIn pageIn) {
		return dao.getListpage(pageIn);
	}
}
