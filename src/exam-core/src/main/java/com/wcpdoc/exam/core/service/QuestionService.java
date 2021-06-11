package com.wcpdoc.exam.core.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.wcpdoc.exam.core.entity.Question;
/**
 * 试题服务层接口
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
public interface QuestionService extends BaseService<Question>{

	/**
	 * 获取试题列表
	 * 
	 * v1.0 zhanghc 2017年8月6日下午9:43:55
	 * @param questionTypeId
	 * @return List<Question>
	 */
	List<Question> getList(Integer questionTypeId);

	/**
	 * 完成试题添加
	 * 
	 * v1.0 zhanghc 2018年10月12日下午1:52:35
	 * @param question
	 * void
	 */
	void addAndUpdate(Question question, String[] options);

	/**
	 * 完成试题修改
	 * 
	 * v1.0 zhanghc 2018年10月12日下午7:30:02
	 * @param question
	 * @param answer 答案
	 * @param newVer 新版本
	 * void 
	 */
	void updateAndUpdate(Question question, boolean newVer, String[] options);
	
	/**
	 * 完成导入试题
	 * 
	 * v1.0 zhanghc 2019年8月10日下午5:12:53
	 * @param file
	 * @param questionTypeId
	 * @return PageResult
	 */
	void wordImp(MultipartFile file, Integer questionTypeId);
	
	/**
	 * 合并
	 * 
	 * v1.0 chenyun 2021年3月2日下午1:25:51
	 * @param id
	 * @param sourceId
	 * @param targetId void
	 */
	void move(Integer id, Integer sourceId, Integer targetId);
	
	/**
	 * 试题统计（类型和难易程度）
	 * 
	 * v1.0 chenyun 2021年3月23日下午2:53:07
	 * @param questionTypeId
	 * @return Map<String,Object>
	 */
	Map<String, Object> statisticsTypeDifficulty(Integer questionTypeId);
	
	/**
	 * 试题统计
	 * 
	 * v1.0 chenyun 2021年3月23日下午2:53:07
	 * @param examId
	 * @return List<Map<String, Object>>
	 */
	List<Map<String, Object>> accuracy(Integer examId);
}
