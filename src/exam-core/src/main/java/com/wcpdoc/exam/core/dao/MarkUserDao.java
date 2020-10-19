package com.wcpdoc.exam.core.dao;

import java.util.List;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.MarkUser;

/**
 * 阅卷用户数据访问层接口
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
public interface MarkUserDao extends BaseDao<MarkUser>{

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
	 * 获取阅卷用户信息
	 * 
	 * v1.0 zhanghc 2017年7月3日上午9:42:55
	 * @param examId
	 * @param userId
	 * @return ExamUser
	 */
	MarkUser getEntity(Integer examId, Integer userId);

	/**
	 * 获取阅卷用户列表
	 * 
	 * v1.0 zhanghc 2020年9月30日上午11:03:04
	 * @param examId
	 * @return List<MarkUser>
	 */
	List<MarkUser> getList(Integer examId);
	
}
