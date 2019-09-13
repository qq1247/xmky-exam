package com.wcpdoc.exam.file.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.service.BaseService;
import com.wcpdoc.exam.file.entity.File;
import com.wcpdoc.exam.file.entity.FileEx;

/**
 * 附件服务层接口
 * 
 * v1.0 zhanghc 2016-11-16下午10:13:48
 */
public interface FileService extends BaseService<File> {
	/**
	 * 完成临时上传附件
	 * 
	 * v1.0 zhanghc 2016-11-16下午10:13:48
	 * 
	 * @param files 上传附件
	 * @param allowTypes 允许上传的类型：zip,doc[,type]
	 * @param user 上传用户
	 * @param ip 上传ip
	 * @return String 更新到数据库后的附件ID，如果是多个，用逗号分隔。
	 */
	String doTempUpload(MultipartFile[] files, String[] allowTypes, LoginUser user, String ip);

	/**
	 * 完成上传附件
	 * 
	 * v1.0 zhanghc 2016-11-16下午10:13:48
	 * 
	 * @param id
	 * void
	 */
	void doUpload(Integer id, LoginUser user, String ip) throws Exception;

	/**
	 * 删除附件 只负责逻辑删除，具体的由定时任务处理。
	 * 
	 * v1.0 zhanghc 2017年3月23日下午11:34:20
	 * 
	 * @param ids
	 * void
	 */
	void delAndUpdate(Integer[] ids);

	/**
	 * 获取附件实体 v1.0 zhanghc 2017年4月5日下午11:36:17
	 * 
	 * @param id
	 * @return FileEx
	 */
	FileEx getEntityEx(Integer id);

	/**
	 * 获取需要删除的列表 
	 * 
	 * v1.0 zhanghc 2017年4月5日下午11:36:17
	 * 
	 * @param id
	 * @return FileEx
	 */
	List<File> getDelList();

}
