package com.wcpdoc.exam.sys.service;

import java.util.List;

import com.wcpdoc.exam.core.service.BaseService;
import com.wcpdoc.exam.sys.entity.Dict;
/**
 * 数据字典服务层接口
 * 
 * v1.0 zhanghc 2016-11-3下午9:03:40
 */
public interface DictService extends BaseService<Dict>{

	/**
	 * 获取列表
	 * 
	 * v1.0 zhanghc 2017年5月23日下午5:17:49
	 * @return List<Dict>
	 */
	List<Dict> getList();

	/**
	 * 保存数据字典
	 * 
	 * v1.0 zhanghc 2017年7月5日下午4:25:15
	 * @param dict
	 * void
	 */
	void saveAndUpdate(Dict dict);

	/**
	 * 修改数据字典
	 * 
	 * v1.0 zhanghc 2017年7月5日下午4:33:24
	 * @param entity
	 * void
	 */
	void updateAndUpdate(Dict dict);
	
	/**
	 * 删除数据字典
	 * 
	 * v1.0 zhanghc 2017年7月5日下午4:36:21
	 * @param ids
	 * void
	 */
	void delAndUpdate(Integer[] ids);
}
