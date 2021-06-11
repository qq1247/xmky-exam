package com.wcpdoc.exam.core.service.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.JwtResult;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.service.BaseService;
import com.wcpdoc.exam.core.util.JwtUtil;

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
		String jwt = request.getHeader("Authorization");
		JwtResult jwtResult = JwtUtil.getInstance().parse(jwt);

		LoginUser LoginUser = new LoginUser() {
			@Override
			public String getLoginName() {
				return jwtResult.getClaims().get("loginName", String.class);
			}
			
			@Override
			public Integer getId() {
				return jwtResult.getClaims().get("userId", Integer.class);
			}
		};
		return jwtResult == null ? null : LoginUser;
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
