package com.wcpdoc.exam.core.dao;

import java.util.List;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.exam.core.entity.Exer;

/**
 * 模拟练习数据访问层接口
 * 
 * v1.0 chenyun 2021-03-02 13:43:21
 */
public interface ExerDao extends RBaseDao<Exer> {

	/**
	 * 模拟练习列表
	 * 
	 * v1.0 chenyun 2021年9月17日上午11:19:21
	 * @param questionTypeId
	 * @return List<Exer>
	 */
	List<Exer> getList(Integer questionTypeId);
}
