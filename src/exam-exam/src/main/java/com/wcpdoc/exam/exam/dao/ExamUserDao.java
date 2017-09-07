package com.wcpdoc.exam.exam.dao;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.exam.entity.ExamUser;

/**
 * 考试用户数据访问层接口
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
public interface ExamUserDao extends BaseDao<ExamUser>{

	/**
	 * 删除实体
	 * 
	 * v1.0 zhanghc 2017年6月26日下午2:11:44
	 * @param examId
	 * @param userId
	 * void
	 */
	void delete(Integer examId, Integer userId);

	/**
	 * 获取考试用户信息
	 * 
	 * v1.0 zhanghc 2017年7月3日上午9:42:55
	 * @param examId
	 * @param userId
	 * @return ExamUser
	 */
	ExamUser getEntity(Integer examId, Integer userId);
	
}
