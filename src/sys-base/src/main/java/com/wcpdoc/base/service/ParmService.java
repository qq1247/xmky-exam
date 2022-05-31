package com.wcpdoc.base.service;

import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.core.service.BaseService;

/**
 * 参数服务层接口
 * 
 * v1.0 chenyun 2021-03-04 15:02:18
 */
public interface ParmService extends BaseService<Parm> {
	/**
	 * 修改邮件
	 * 
	 * v1.0 zhanghc 2021年3月5日上午11:21:35
	 * @param parm void
	 */
	void emailUpdate(String host, String userName, String pwd, String protocol, String encode);
	
	/**
	 * 修改logo
	 * 
	 * v1.0 chenyun 2021年10月8日下午2:30:00
	 * @param parm void
	 */
	void logoUpdate(Parm parm) throws Exception;
	
	/**
	 * 系统参数密码初始化
	 * 
	 * v1.0 chenyun 2021年11月12日上午10:09:26
	 * @param type 密码类型（1：随机；2：固定）
	 * @param value void
	 */
	void pwdUpdate(Integer type, String value);
	
	/**
	 * 系统参数上传附件目录
	 * 
	 * v1.0 chenyun 2021年11月12日上午11:15:48
	 * @param uploadDir void
	 */
	void fileUpdate(String uploadDir);
	
	/**
	 * 系统参数数据库备份目录
	 * 
	 * v1.0 chenyun 2021年11月12日上午10:34:57
	 * @param bakDir void
	 */
	void dbUpdate(String bakDir);

}
