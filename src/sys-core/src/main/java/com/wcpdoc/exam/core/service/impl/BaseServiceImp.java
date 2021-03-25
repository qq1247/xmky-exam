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

import io.jsonwebtoken.Claims;

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
		JwtResult parse = JwtUtil.getInstance().parse(jwt);
		Claims claims = parse.getClaims();
		LoginUser LoginUser = new LoginUser() {
			@Override
			public String getLoginName() {
				return (String) claims.get("loginName");
			}
			
			@Override
			public Integer getId() {
				return Integer.valueOf(claims.getId());
			}
		};
		return parse == null ? null : LoginUser;
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
