package com.wcpdoc.core.dao;

import com.github.yulichang.base.MPJBaseMapper;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;

/**
 * 关系型数据访问层接口
 * 
 * v1.0 zhanghc 2015-6-19下午08:30:16
 */
public interface RBaseDao<T> extends MPJBaseMapper<T> {
	/**
	 * 获取分页列表
	 * 
	 * v1.0 zhanghc 2016年5月2日上午10:26:49
	 * 
	 * @param pageIn
	 * @return pageOut
	 */
	public PageOut getListpage(PageIn pageIn);
}
