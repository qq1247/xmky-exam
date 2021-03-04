package com.wcpdoc.exam.base.service;

import com.wcpdoc.exam.core.service.BaseService;
import com.wcpdoc.exam.base.entity.Email;

/**
 * 邮箱服务层接口
 * 
 * v1.0 chenyun 2021-03-04 15:02:18
 */
public interface EmailService extends BaseService<Email> {

	/**
	 * 删除邮箱
	 * 
	 * v1.0 chenyun 2021-03-04 15:02:18
	 * 
	 * @param id
	 * void
	 */
	void delAndUpdate(Integer id);
	
	/**
	 * 获取邮箱
	 * 
	 * v1.0 chenyun 2021年3月4日下午3:17:05
	 * @param userId
	 * @return EmailDao
	 */
	Email getEmail(Integer userId);
	
	/**
	 * 发送邮箱
	 * 
	 * v1.0 chenyun 2021年3月4日下午3:09:36 void
	 */
	void sendEmail(String receiverName, String receiverTitle, String receiverContent);
}
