package com.wcpdoc.base.service;

import java.util.Date;

import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.core.service.BaseService;

/**
 * 参数服务层接口
 * 
 * v1.0 chenyun 2021-03-04 15:02:18
 */
public interface ParmService extends BaseService<Parm> {
	/**
	 * 邮件修改
	 * 
	 * v1.0 zhanghc 2021年3月5日上午11:21:35
	 * 
	 * @param parm void
	 */
	void email(String host, String userName, String pwd, String protocol, String encode);

	/**
	 * 企业修改
	 * 
	 * v1.0 chenyun 2021年10月8日下午2:30:00
	 * 
	 * @param logoFileId logo附件ID
	 * @param name       名称
	 */
	void ent(Integer logoFileId, String name) throws Exception;

	/**
	 * 密码修改
	 * 
	 * v1.0 chenyun 2021年11月12日上午10:09:26
	 * 
	 * @param type  密码类型（1：随机；2：固定）
	 * @param value void
	 */
	void pwd(Integer type, String value);

	/**
	 * 上传目录修改
	 * 
	 * v1.0 chenyun 2021年11月12日上午11:15:48
	 * 
	 * @param uploadDir void
	 */
	void file(String uploadDir);

	/**
	 * DB备份目录修改
	 * 
	 * v1.0 chenyun 2021年11月12日上午10:34:57
	 * 
	 * @param bakDir void
	 */
	void db(String bakDir);

	/**
	 * 服务支持修改
	 * 
	 * v1.0 zhanghc 2023年3月10日上午9:25:18
	 * 
	 * @param title
	 * @param content void
	 */
	void support(String title, String content);

	/**
	 * 备案信息修改
	 * 
	 * v1.0 zhanghc 2025年9月1日下午2:34:25
	 * 
	 * @param icp void
	 */
	void icp(String icp);

	/**
	 * 移动端设置
	 * 
	 * v1.0 zhanghc 2024年9月30日下午1:04:31
	 * 
	 * @param host void
	 */
	void m(String host);

	/**
	 * 应用设置
	 * 
	 * v1.0 zhanghc 2025年4月8日下午6:42:52
	 * 
	 * @param relVer
	 * @param relTime
	 * @param verhubUrl void
	 */
	void app(String relVer, Date relTime, String verhubUrl);

	/**
	 * 应用ID
	 * 
	 * v1.0 zhanghc 2025年9月30日下午4:49:46 void
	 */
	void appId();
}
