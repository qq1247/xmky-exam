package com.wcpdoc.base.dao;

import java.util.List;

import com.wcpdoc.base.entity.Dict;
import com.wcpdoc.core.dao.RBaseDao;

/**
 * 数据字典数据访问层接口
 * 
 * v1.0 zhanghc 2016-11-3下午9:03:40
 */
public interface DictDao extends RBaseDao<Dict> {

	/**
	 * 获取列表
	 * 
	 * v1.0 zhanghc 2017年5月23日下午5:18:29
	 * 
	 * @return List<Dict>
	 */
	List<Dict> getListByIndex(String index);
}
