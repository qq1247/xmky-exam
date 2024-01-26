package com.wcpdoc.base.service;

import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.core.service.BaseService;

/**
 * 参数服务层接口
 * 
 * v1.0 chenyun 2021-03-04 15:02:18
 */
public interface ParmExService extends BaseService<Parm> {

	/**
	 * 修改参数
	 * 
	 * v1.0 zhanghc 2021年3月5日上午11:29:10
	 * 
	 * @param parm
	 * @throws EmailException
	 */
	void emailUpdate(Parm parm) throws Exception;

	/**
	 * 企业修改
	 * 
	 * v1.0 chenyun 2021年11月15日下午4:30:11
	 * 
	 * @param logoFileId void
	 */
	void entUpdate(Integer logoFileId);

	/**
	 * 推送邮件
	 * 
	 * v1.0 zhanghc 2022年5月9日下午1:22:15
	 * 
	 * @param userName
	 * @param to
	 * @param title
	 * @param content  void
	 */
	void pushEmail(String from, String to, String title, String content);
}
