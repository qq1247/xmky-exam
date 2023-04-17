package com.wcpdoc.exam.core.dao;

import java.util.List;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.Question;

/**
 * 试题数据访问层接口
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
public interface QuestionDao extends BaseDao<Question>{

	/**
	 * 获取试题列表
	 * 
	 * v1.0 zhanghc 2021年12月28日下午2:56:17
	 * @param questionTypeId
	 * @return List<Integer>
	 */
	List<Integer> getIds(Integer questionTypeId);
	
	/**
	 * 获取试题列表
	 * 
	 * v1.0 zhanghc 2021年12月28日下午2:56:17
	 * @param questionTypeId
	 * @return List<Integer>
	 */
	List<Question> getList(Integer questionTypeId);
	
	/**
	 * 物理删除试题
	 * 
	 * v1.0 chenyun 2022年4月22日上午11:22:25
	 * @return List<Question>
	 */
	List<Question> getListByDel();

}
