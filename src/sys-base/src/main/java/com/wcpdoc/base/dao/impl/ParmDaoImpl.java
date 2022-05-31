package com.wcpdoc.base.dao.impl;

import org.springframework.stereotype.Repository;

import com.wcpdoc.base.dao.ParmDao;
import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;

/**
 * 参数数据访问层实现
 * 
 * v1.0 chenyun 2021-03-04 15:02:18
 */
@Repository
public class ParmDaoImpl extends RBaseDaoImpl<Parm> implements ParmDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return null;
	}
}