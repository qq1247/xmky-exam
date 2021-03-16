package com.wcpdoc.exam.base.service;

import org.springframework.web.multipart.MultipartFile;

import com.wcpdoc.exam.core.service.BaseService;

/**
 * 用户表格服务层接口
 * 
 * v1.0 zhanghc 2016-5-8上午11:00:00
 */
public interface UserXlsxService extends BaseService<Object> {

	/**
	 * 导入用户表
	 * 
	 * v1.0 chenyun 2021年3月4日下午5:29:49 void
	 */
	void inputUserXlsx(MultipartFile file);
	
	/**
	 * 导出用户表
	 * 
	 * v1.0 chenyun 2021年3月4日下午5:29:49 void
	 */
	void exportUserXlsx(String ids);
	
	/**
	 * 导出模板
	 * 
	 * v1.0 chenyun 2021年3月4日下午5:29:49 void
	 */
	void templateUserXlsx();
}
