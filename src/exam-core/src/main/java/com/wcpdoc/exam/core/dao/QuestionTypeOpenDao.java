package com.wcpdoc.exam.core.dao;

import java.util.Date;
import java.util.List;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.exam.core.entity.QuestionTypeOpen;

/**
 * 试题分类开放数据访问层接口
 * 
 * v1.0 chenyun 2021-03-02 13:43:21
 */
public interface QuestionTypeOpenDao extends RBaseDao<QuestionTypeOpen> {
	
	/**
	 * 获取重复列表
	 * 
	 * v1.0 chenyun 2021年9月17日上午11:19:21
	 * @param startTime
	 * @param endTime
	 * @param questionTypeId
	 * @return List<QuestionTypeOpen>
	 */
	List<QuestionTypeOpen> getList(Date startTime, Date endTime, Integer questionTypeId );
}
