package com.wcpdoc.exam.core.service;

import java.util.List;

import com.wcpdoc.exam.core.entity.MyMark;
/**
 * 我的阅卷服务层接口
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
public interface MyMarkService extends BaseService<MyMark>{

	/**
	 * 获取我的考试列表
	 * 
	 * v1.0 zhanghc 2020年9月29日下午5:09:50
	 * @param examId
	 * @return List<MyExam>
	 */
	List<MyMark> getList(Integer examId);
}
