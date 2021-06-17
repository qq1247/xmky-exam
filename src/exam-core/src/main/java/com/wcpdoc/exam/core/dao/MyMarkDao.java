package com.wcpdoc.exam.core.dao;

import java.util.List;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.MyMark;

/**
 * 我的阅卷数据访问层接口
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
public interface MyMarkDao extends BaseDao<MyMark>{

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
	 * 获取我的阅卷信息
	 * 
	 * v1.0 zhanghc 2017年7月3日上午9:42:55
	 * @param examId
	 * @param userId
	 * @return MyExam
	 */
	MyMark getEntity(Integer examId, Integer userId);

	/**
	 * 获取我的阅卷列表
	 * 
	 * v1.0 zhanghc 2020年9月30日上午11:03:04
	 * @param examId
	 * @return List<MyMark>
	 */
	List<MyMark> getList(Integer examId);
	
}
