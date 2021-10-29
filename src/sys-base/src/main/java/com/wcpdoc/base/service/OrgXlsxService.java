package com.wcpdoc.base.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.wcpdoc.base.entity.OrgXlsx;
import com.wcpdoc.core.service.BaseService;

/**
 * 组织机构表格服务层接口
 * 
 * v1.0 zhanghc 2016-5-8上午11:00:00
 */
public interface OrgXlsxService extends BaseService<Object> {

	/**
	 * 导入组织机构表
	 * 
	 * v1.0 chenyun 2021年3月4日下午5:29:49 void
	 */
	void inputOrgXlsx(MultipartFile file);
	
	/**
	 * 导出组织机构表
	 * 
	 * v1.0 chenyun 2021年3月4日下午5:29:49 void
	 */
	List<OrgXlsx> exportOrgXlsx(String ids);
	
	/**
	 * 导出模板
	 * 
	 * v1.0 chenyun 2021年3月5日下午5:10:18 void
	 */
	void templateOrgXlsx();
}
