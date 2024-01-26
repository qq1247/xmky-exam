package com.wcpdoc.base.service;

import java.util.List;

import com.wcpdoc.base.entity.Dict;
import com.wcpdoc.core.service.BaseService;

/**
 * 数据字典服务层接口
 * 
 * v1.0 zhanghc 2016-11-3下午9:03:40
 */
public interface DictService extends BaseService<Dict> {
	/**
	 * 获取数据字典列表
	 * 
	 * v1.0 zhanghc 2022年2月24日下午5:31:31
	 * 
	 * @param index
	 * @return List<Dict>
	 */
	List<Dict> getList(String index);
}
