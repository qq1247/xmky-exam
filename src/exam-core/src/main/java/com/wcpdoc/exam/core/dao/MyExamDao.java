package com.wcpdoc.exam.core.dao;

import java.util.List;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.MyExam;

/**
 * 我的考试数据访问层接口
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
public interface MyExamDao extends BaseDao<MyExam>{

	/**
	 * 获取我的考试列表
	 * 
	 * v1.0 zhanghc 2020年9月30日上午11:00:50
	 * @param examId
	 * @return List<MyExam>
	 */
	List<MyExam> getList(Integer examId);
	
	/**
	 * 获取我的考试列表
	 * 
	 * v1.0 zhanghc 2022年6月2日上午9:51:25
	 * @param userId
	 * @return List<MyExam>
	 */
	List<MyExam> getListForUser(Integer userId);

	/**
	 * 获取我的考试列表
	 * 
	 * v1.0 chenyun 2021年7月30日下午3:49:53
	 * @param examId
	 * @param userId
	 * @return MyExam
	 */
	MyExam getMyExam(Integer examId, Integer userId);

	
}
