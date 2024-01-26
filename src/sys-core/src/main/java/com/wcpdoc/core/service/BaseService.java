package com.wcpdoc.core.service;

import com.github.yulichang.base.MPJBaseService;
import com.wcpdoc.core.entity.LoginUser;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;

/**
 * 基础服务层接口
 * 
 * v1.0 zhanghc 2016年5月1日下午10:21:10
 * 
 * @param <T>
 */
public interface BaseService<T> extends MPJBaseService<T> {
	/**
	 * 获取分页列表。
	 * 
	 * v1.0 zhanghc 2016年5月2日上午10:26:49
	 * 
	 * @param pageIn
	 * @return pageOut
	 */
	public PageOut getListpage(PageIn pageIn);

	/**
	 * 获取当前登录用户
	 * 
	 * v1.0 zhanghc 2020年1月16日上午10:47:47
	 * 
	 * @return LoginUser
	 */
	LoginUser getCurUser();
}
