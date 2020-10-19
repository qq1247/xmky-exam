package com.wcpdoc.exam.core.service;

import java.util.List;

import com.wcpdoc.exam.core.entity.ExamUser;
import com.wcpdoc.exam.core.service.BaseService;
/**
 * 考试用户服务层接口
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
public interface ExamUserService extends BaseService<ExamUser>{

	/**
	 * 删除实体
	 * 
	 * v1.0 zhanghc 2017年6月26日下午2:09:55
	 * @param examId
	 * @param id
	 * void
	 */
	void del(Integer examId, Integer id);

	/**
	 * 获取考试用户列表
	 * 
	 * v1.0 zhanghc 2020年9月29日下午5:09:50
	 * @param examId
	 * @return List<ExamUser>
	 */
	List<ExamUser> getList(Integer examId);

}
