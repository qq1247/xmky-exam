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

public abstract class BaseServiceImp<T> implements BaseService<T> {
	@Resource
	protected HttpServletRequest request;
	@Resource
	protected HttpServletResponse response;
	protected BaseDao<T> dao;
	public abstract void setDao(BaseDao<T> dao);
	
	public LoginUser getCurrentUser(){
		HttpSession session = request.getSession(false);
		if(session == null){
			return null;
		}
		
		return (LoginUser) session.getAttribute(ConstantManager.USER);
	}

	@Override
	public final void save(T entity) {
		dao.save(entity);
	}

	@Override
	public final void update(T entity) {
		dao.update(entity);
	}

	@Override
	public final void del(Integer id) {
		dao.del(id);
	}

	@Override
	public final void del(Integer[] ids) {
		dao.del(ids);
	}
	
	@Override
	public final T getEntity(Integer id) {
		return dao.getEntity(id);
	}

	@Override
	public final PageOut getListpage(PageIn pageIn) {
		return dao.getListpage(pageIn);
	}

	@Override
	public final void evict(Object obj) {
		dao.evict(obj);
	}
}
