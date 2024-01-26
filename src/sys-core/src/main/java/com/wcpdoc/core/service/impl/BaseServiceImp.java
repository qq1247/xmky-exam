package com.wcpdoc.core.service.impl;

import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.wcpdoc.core.context.UserContext;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.LoginUser;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.service.BaseService;

/**
 * 基础服务层实现
 * 
 * v1.0 zhanghc 2015-6-19下午08:30:16
 * 
 * @param <T>
 */
public abstract class BaseServiceImp<T> extends MPJBaseServiceImpl<MPJBaseMapper<T>, T> implements BaseService<T> {

	public abstract RBaseDao<T> getDao();

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return getDao().getListpage(pageIn);
	}

	@Override
	public LoginUser getCurUser() {
		return UserContext.get();
	}
}
