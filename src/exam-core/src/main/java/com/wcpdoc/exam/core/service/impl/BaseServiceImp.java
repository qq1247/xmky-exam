package com.wcpdoc.exam.core.service.impl;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.service.BaseService;

public abstract class BaseServiceImp<T> implements BaseService<T> {
	protected BaseDao<T> dao;

	public abstract void setDao(BaseDao<T> dao);

	@Override
	public final void save(T entity) {
		dao.save(entity);
	}

	@Override
	public final void update(T entity) {
		dao.update(entity);
	}

	@Override
	public final void delete(Integer id) {
		dao.del(id);
	}

	@Override
	public final void delete(Integer[] ids) {
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
