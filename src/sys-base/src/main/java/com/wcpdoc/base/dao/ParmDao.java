package com.wcpdoc.base.dao;

import org.apache.ibatis.annotations.Mapper;

import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.core.dao.RBaseDao;

/**
 * 参数数据访问层接口
 * 
 * v1.0 chenyun 2021-03-04 15:02:18
 */
@Mapper
public interface ParmDao extends RBaseDao<Parm> {

}
