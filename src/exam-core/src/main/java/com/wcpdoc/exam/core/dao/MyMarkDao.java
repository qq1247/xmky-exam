package com.wcpdoc.exam.core.dao;

import java.util.List;
import java.util.Map;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.MyMark;

/**
 * 我的阅卷数据访问层接口
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
public interface MyMarkDao extends BaseDao<MyMark>{

	/**
	 * 获取我的阅卷列表
	 * 
	 * v1.0 zhanghc 2020年9月30日上午11:03:04
	 * @param examId
	 * @return List<MyMark>
	 */
	List<MyMark> getList(Integer examId);
	
	/**
	 * 阅卷考生
	 * 
	 * v1.0 chenyun 2021年12月3日下午2:01:49
	 * @param examId
	 * @param markUserId
	 * @param examUserName
	 * @return List<Map<String,String>>
	 */
	List<Map<String, Object>> getUserList(Integer examId, Integer markUserId, String examUserName, Integer examUserId);
}
