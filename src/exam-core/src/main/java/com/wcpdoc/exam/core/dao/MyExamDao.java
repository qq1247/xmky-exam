package com.wcpdoc.exam.core.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.MyExam;

/**
 * 我的考试数据访问层接口
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
public interface MyExamDao extends BaseDao<MyExam>{

	/**
	 * 删除实体
	 * 
	 * v1.0 zhanghc 2017年6月26日下午2:11:44
	 * @param examId
	 * @param userId
	 * void
	 */
	void del(Integer examId, Integer userId);

	/**
	 * 获取我的考试
	 * 
	 * v1.0 zhanghc 2020年9月30日上午11:00:50
	 * @param examId
	 * @return List<MyExam>
	 */
	List<MyExam> getList(Integer examId);

	/**
	 * 获取我的考试列表
	 * 
	 * v1.0 chenyun 2021年7月30日下午3:49:53
	 * @param examId
	 * @param userId
	 * @return MyExam
	 */
	MyExam getEntity(Integer examId, Integer userId);
	
	/**
	 * 考试时间表
	 * 
	 * v1.0 chenyun 2021年3月23日上午11:14:46
	 * @param userId
	 * @param startTime
	 * @param endTime
	 * @return List<MyExam>
	 */
	List<MyExam> kalendar(Integer userId, Date startTime, Date endTime);
	
	/**
	 * 成绩排名
	 * 
	 * v1.0 chenyun 2021年3月23日下午3:14:01
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getRankingPage(PageIn pageIn);

	/**
	 * 获取用户列表
	 * 
	 * v1.0 zhanghc 2021年10月28日下午1:56:20
	 * @param id
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getUserList(Integer id);
}
