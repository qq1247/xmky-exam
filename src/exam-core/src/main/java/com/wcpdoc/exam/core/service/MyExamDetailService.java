package com.wcpdoc.exam.core.service;

import java.util.List;
import java.util.Map;

import com.wcpdoc.exam.core.entity.MyExamDetail;
import com.wcpdoc.exam.core.entity.LoginUser;
/**
 * 我的考试详细服务层接口
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
public interface MyExamDetailService extends BaseService<MyExamDetail>{

	/**
	 * 获取我的考试详细列表
	 * 
	 * v1.0 zhanghc 2017年7月3日上午9:44:45
	 * @param myExamId
	 * @return List<MyExamDetail>
	 */
	List<MyExamDetail> getList(Integer myExamId);

	/**
	 * 获取我的考试详细列表
	 * 
	 * v1.0 zhanghc 2017年7月3日上午9:44:45
	 * @param myExamId
	 * @return List<Map<String, Object>>
	 */
	List<Map<String, Object>> getAnswerList(Integer myExamId);

	/**
	 * 阅卷获取我的考试详细列表
	 * 
	 * v1.0 chenyun 2021年7月29日下午6:01:59
	 * @param userId
	 * @param examId
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getMarkAnswerList(Integer userId, Integer examId);
	
	/**
	 * 删除我的考试详细
	 * 
	 * v1.0 zhanghc 2020年10月12日下午4:03:42
	 * @param myExamId void
	 */
	void delByMyExamId(Integer myExamId);

	/**
	 * 完成阅卷
	 * 
	 * v1.0 zhanghc 2020年10月13日下午1:13:52
	 * @param examId
	 * @param curUser
	 * @param processBarId 
	 * void
	 */
	void autoMark(Integer examId, LoginUser curUser, String processBarId);
}
