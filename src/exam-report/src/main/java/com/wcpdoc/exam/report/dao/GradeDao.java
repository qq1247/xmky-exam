package com.wcpdoc.exam.report.dao;

import java.util.List;
import java.util.Map;

import com.wcpdoc.core.dao.RBaseDao;

/**
 * 公告数据访问层接口
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
public interface GradeDao extends RBaseDao<Object> {
	
	/**
	 * 分数统计
	 * 
	 * v1.0 chenyun 2021年3月24日上午10:04:05
	 * @param examId
	 * @return List<Map<String, Object>>
	 */
	List<Map<String, Object>> count(Integer examId);
}
