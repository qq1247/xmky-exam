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
	 * 获取列表
	 * 
	 * v1.0 zhanghc 2017年5月23日下午5:17:49
	 * 
	 * @return List<Dict>
	 */
	List<Dict> getList();

	/**
	 * 添加数据字典
	 * 
	 * v1.0 zhanghc 2017年7月5日下午4:25:15
	 * 
	 * @param dict
	 * void
	 */
	void addAndUpdate(Dict dict);

	/**
	 * 修改数据字典
	 * 
	 * v1.0 zhanghc 2017年7月5日下午4:33:24
	 * 
	 * @param dict
	 * void
	 */
	void updateAndUpdate(Dict dict);

	/**
	 * 删除数据字典
	 * 
	 * v1.0 zhanghc 2017年7月5日下午4:36:21
	 * 
	 * @param id
	 * void
	 */
	void delAndUpdate(Integer id);

	/**
	 * 获取数据字典列表
	 * 
	 * v1.0 zhanghc 2022年2月24日下午5:31:31
	 * @param index
	 * @return List<Dict>
	 */
	List<Dict> getList(String index);
}
