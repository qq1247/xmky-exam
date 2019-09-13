package com.wcpdoc.exam.sys.dao;

import java.util.List;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.sys.entity.Dict;

/**
 * 数据字典数据访问层接口
 * 
 * v1.0 zhanghc 2016-11-3下午9:03:40
 */
public interface DictDao extends BaseDao<Dict> {

	/**
	 * 获取列表
	 * 
	 * v1.0 zhanghc 2017年5月23日下午5:18:29
	 * 
	 * @return List<Dict>
	 */
	List<Dict> getList();
}
