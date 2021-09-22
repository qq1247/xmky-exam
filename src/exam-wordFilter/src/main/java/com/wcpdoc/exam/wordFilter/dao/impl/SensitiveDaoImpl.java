package com.wcpdoc.exam.wordFilter.dao.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.base.dao.UserDao;
import com.wcpdoc.exam.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.wordFilter.dao.SensitiveDao;
import com.wcpdoc.exam.wordFilter.entity.Sensitive;

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