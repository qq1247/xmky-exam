package com.wcpdoc.exam.core.dao;

import java.util.List;
import java.util.Map;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.MyExamDetail;

/**
 * 我的考试详细数据访问层接口
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
public interface MyExamDetailDao extends BaseDao<MyExamDetail>{
	
	/**
	 * 获取我的考试详细列表
	 * 
	 * v1.0 zhanghc 2017年7月3日上午9:44:45
	 * @param myExamId
	 * @return List<MyExamDetail>
	 */
	List<MyExamDetail> getList(Integer examId, Integer userId);
	
	/**
	 * 获取我的考试详细列表
	 * 
	 * v1.0 zhanghc 2017年7月3日上午9:44:45
	 * @param myExamId
	 * @return List<Map<String, Object>>
	 */
	List<Map<String, Object>> getAnswerList(Integer myExamId, Integer curUserId);

	/**
	 * 获取我的考试详细
	 * 
	 * v1.0 zhanghc 2021年10月19日上午9:55:31
	 * @param examId
	 * @param userId
	 * @param questionId
	 * @return MyExamDetail
	 */
	MyExamDetail getEntity(Integer examId, Integer userId, Integer questionId);

	/**
	 * 删除我的考试详细
	 * 
	 * v1.0 zhanghc 2021年10月27日下午2:20:11
	 * @param examId
	 * @param userId void
	 */
	void del(Integer examId, Integer userId);
}
