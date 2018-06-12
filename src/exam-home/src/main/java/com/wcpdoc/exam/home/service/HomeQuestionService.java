package com.wcpdoc.exam.home.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.service.BaseService;
import com.wcpdoc.exam.file.entity.FileEx;
/**
 * 试题服务层接口
 * 
 * v1.0 zhanghc 2018年6月3日上午10:34:21
 */
public interface HomeQuestionService extends BaseService<Object>{

	/**
	 * 获取试题分类树
	 * 
	 * v1.0 zhanghc 2018年6月3日上午11:40:54
	 * @param userId
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getQuestionTypeTreeList(Integer userId);

	/**
	 * 完成临时上传附件
	 * 
	 * v1.0 zhanghc 2018年6月3日上午11:40:54
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
	 * v1.0 zhanghc 2018年6月3日上午11:40:54
	 * @param id
	 * void
	 */
	void doUpload(Integer id, LoginUser user, String ip) throws Exception;

	/**
	 * 获取附件
	 * 
	 * v1.0 zhanghc 2018年6月12日下午10:26:20
	 * @param fileId
	 * @return FileEx
	 */
	FileEx getFileEx(Integer fileId);

}
