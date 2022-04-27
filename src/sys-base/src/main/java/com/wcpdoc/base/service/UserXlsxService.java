package com.wcpdoc.base.service;

import org.springframework.web.multipart.MultipartFile;

import com.wcpdoc.core.service.BaseService;

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
	 * 导出用户
	 * 
	 * v1.0 chenyun 2022年04月13日上午10:38:37
	 * @param ids
	 */
	void export(Integer[] ids);
	
	/**
	 * 导出模板
	 * 
	 * v1.0 chenyun 2021年3月4日下午5:29:49 void
	 */
	void templateUserXlsx();
}
