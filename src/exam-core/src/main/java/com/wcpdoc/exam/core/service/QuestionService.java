package com.wcpdoc.exam.core.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.service.BaseService;
import com.wcpdoc.exam.file.entity.FileEx;
/**
 * 试题服务层接口
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
public interface QuestionService extends BaseService<Question>{

	/**
	 * 获取试题分类树
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @param userId
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getQuestionTypeTreeList(Integer userId);

	/**
	 * 获取试题分类
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @param id 
	 * @return QuestionType
	 */
	QuestionType getQuestionType(Integer id);

	/**
	 * 完成设置试题分类
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @param ids 试题ID
	 * @param questionTypeId 试题分类ID
	 * @return PageResult
	 */
	void doQuestionTypeUpdate(Integer[] ids, Integer questionTypeId);

	/**
	 * 获取试题列表
	 * 
	 * v1.0 zhanghc 2017年8月6日下午9:43:55
	 * @param questionTypeId
	 * @return List<Question>
	 */
	List<Question> getList(Integer questionTypeId);

	/**
	 * 删除试题
	 * 
	 * v1.0 zhanghc 2017年8月6日下午9:43:55
	 * @param ids
	 * void
	 */
	void delAndUpdate(Integer[] ids);

	/**
	 * 获取试题分类
	 * 
	 * v1.0 zhanghc 2018年10月6日下午12:34:27
	 * @param questionTypeId
	 * @return Object
	 */
	QuestionType getQuestionType2(Integer questionTypeId);

	/**
	 * 完成临时上传附件
	 * 
	 * v1.0 zhanghc 2018年10月11日下午2:41:32
	 * @param files
	 * @param allowTypes
	 * @param user
	 * @param ip
	 * @return String
	 */
	String doTempUpload(MultipartFile[] files, String[] allowTypes, LoginUser user, String ip);

	/**
	 * 获取附件
	 * 
	 * v1.0 zhanghc 2018年10月11日下午5:45:10
	 * @param fileId
	 * @return FileEx
	 */
	FileEx getFileEx(Integer fileId);

	/**
	 * 完成试题添加
	 * 
	 * v1.0 zhanghc 2018年10月12日下午1:52:35
	 * @param question
	 * @param answer 
	 * @param user
	 * @param ip 
	 * void
	 */
	void addAndUpdate(Question question, String[] answer, LoginUser user, String ip);

	/**
	 * 完成试题修改
	 * 
	 * v1.0 zhanghc 2018年10月12日下午7:30:02
	 * @param question
	 * @param answer 答案
	 * @param newVer 新版本
	 * void 
	 */
	void updateAndUpdate(Question question, String[] answer, boolean newVer);

	/**
	 * 完成导入试题
	 * 
	 * v1.0 zhanghc 2019年8月10日下午5:12:53
	 * @param file
	 * @param questionTypeId
	 * @return PageResult
	 */
	void doWordImp(MultipartFile file, Integer questionTypeId);
}
