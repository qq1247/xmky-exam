package com.wcpdoc.exam.core.dao;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.MyFav;

/**
 * 我的收藏数据访问层接口
 * 
 * v1.0 zhanghc 2025年6月8日下午9:22:47
 */
public interface MyFavDao extends RBaseDao<MyFav> {

	@Override
	default PageOut getListpage(PageIn pageIn) {
		return null;
	}
}
