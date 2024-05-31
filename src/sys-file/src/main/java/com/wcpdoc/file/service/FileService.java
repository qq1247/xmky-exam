package com.wcpdoc.file.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.file.entity.File;
import com.wcpdoc.file.entity.FileEx;

/**
 * 附件服务层接口
 * 
 * v1.0 zhanghc 2016-11-16下午10:13:48
 */
public interface FileService extends BaseService<File> {
	/**
	 * 临时上传附件
	 * 
	 * v1.0 zhanghc 2016-11-16下午10:13:48
	 * 
	 * @param files      上传附件
	 * @param allowTypes 允许上传的类型：zip,doc等
	 * @param uuid       二维码传图使用
	 * @return String 附件ID，如果是多个用英文逗号分隔
	 */
	String tempUpload(MultipartFile[] files, String[] allowTypes, String uuid);

	/**
	 * 上传附件
	 * 
	 * v1.0 zhanghc 2016-11-16下午10:13:48
	 * 
	 * @param id void
	 */
	void upload(Integer id);

	/**
	 * 获取附件实体
	 * 
	 * v1.0 zhanghc 2017年4月5日下午11:36:17
	 * 
	 * @param id
	 * @return FileEx
	 */
	FileEx getFileEx(Integer id);

	/**
	 * 获取需要删除的列表
	 * 
	 * v1.0 zhanghc 2017年4月5日下午11:36:17
	 * 
	 * @param id
	 * @return FileEx
	 */
	List<File> getDelList();

	/**
	 * 模板导出
	 * 
	 * v1.0 chenyun 2021年8月23日上午9:52:56
	 * 
	 * @param templateName void
	 */
	void exportTemplate(String templateName);

	/**
	 * 获取上传目录 如果配置文件为相对路径，上传目录为当前启动程序目录+配置文件目录
	 * 
	 * v1.0 zhanghc 2021年10月14日下午5:03:01
	 * 
	 * @return String
	 */
	String getFileUploadDir();

	/**
	 * 拷贝附件
	 * 
	 * v1.0 chenyun 2022年4月22日下午1:41:50
	 * 
	 * @param id
	 * @return Integer
	 */
	Integer copyFile(Integer id);
}
