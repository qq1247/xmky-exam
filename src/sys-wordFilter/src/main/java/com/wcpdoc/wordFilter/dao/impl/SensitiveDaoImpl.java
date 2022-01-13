package com.wcpdoc.wordFilter.dao.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.wcpdoc.base.dao.UserDao;
import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.wordFilter.dao.SensitiveDao;
import com.wcpdoc.wordFilter.entity.Sensitive;

/**
 * 公告数据访问层实现
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
@Repository
public class SensitiveDaoImpl extends RBaseDaoImpl<Sensitive> implements SensitiveDao {
	@Resource
	private UserDao userDao;

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return null;
	}
}