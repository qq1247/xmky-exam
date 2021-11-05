package com.wcpdoc.base.dao;

import java.util.List;

import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.core.dao.RBaseDao;

/**
 * 参数数据访问层接口
 * 
 * v1.0 chenyun 2021-03-04 15:02:18
 */
public interface ParmDao extends RBaseDao<Parm> {
	
	/**
	 * 获取实体
	 * 
	 * v1.0 chenyun 2021年9月14日上午11:27:20
	 * @param userId
	 * @return Parm
	 */
	List<Parm> getList();
}
