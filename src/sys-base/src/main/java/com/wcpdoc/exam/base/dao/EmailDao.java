package com.wcpdoc.exam.base.dao;

import com.wcpdoc.exam.base.entity.Email;
import com.wcpdoc.exam.core.dao.RBaseDao;

/**
 * 邮箱数据访问层接口
 * 
 * v1.0 chenyun 2021-03-04 15:02:18
 */
public interface EmailDao extends RBaseDao<Email> {
	
	/**
	 * 获取邮箱
	 * 
	 * v1.0 chenyun 2021年3月4日下午3:17:05
	 * @param userId
	 * @return EmailDao
	 */
	Email getEmail(Integer userId);
}
