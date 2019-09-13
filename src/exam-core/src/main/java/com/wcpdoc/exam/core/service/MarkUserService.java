package com.wcpdoc.exam.core.service;

import com.wcpdoc.exam.core.entity.MarkUser;
import com.wcpdoc.exam.core.service.BaseService;
/**
 * 判卷用户服务层接口
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
public interface MarkUserService extends BaseService<MarkUser>{

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
	 * 获取判卷用户信息
	 * 
	 * v1.0 zhanghc 2017年7月3日上午9:41:59
	 * @param examId
	 * @param userId
	 * @return ExamUser
	 */
	MarkUser getEntity(Integer examId, Integer userId);

}
