package com.wcpdoc.exam.core.service;

import java.math.BigDecimal;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.Question;
/**
 * 试题服务层接口
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
public interface QuestionService extends BaseService<Question>{

	/**
	 *  添加试题
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @param question
	 * @param scoreOptions 分数选项
	 * @param options 选项（单选多选时有效）
	 * @param answers 答案
	 * @param answerScores 答案分数（填空或智能问答有多项）
	 * @return PageResult
	 */
	void addAndUpdate(Question question, Integer[] scoreOptions, String[] options, String[] answers, BigDecimal[] answerScores);

	/**
	 * 完成试题修改
	 * 
	 * v1.0 zhanghc 2018年10月12日下午7:30:02
	 * @param question
	 * @param scoreOptions 分数选项
	 * @param options 选项（单选多选时有效）
	 * @param answers 答案
	 * @param answerScores 答案分数（填空或智能问答有多项）
	 * @return PageResult
	 */
	void updateAndUpdate(Question question, Integer[] scoreOptions, String[] options, String[] answers, BigDecimal[] answerScores);
	
	/**
	 * 删除试题
	 * 
	 * v1.0 chenyun 2021年8月25日下午5:28:51
	 * @param questionTypeId 
	 * @param ids 
	 * void
	 */
	void delAndUpdate(Integer questionTypeId, Integer[] ids);
	
	/**
	 * 完成导入试题
	 * 
	 * v1.0 zhanghc 2019年8月10日下午5:12:53
	 * @param fileId
	 * @param questionTypeId
	 * @return PageResult
	 * @return processBarId
	 */
	void wordImp(Integer fileId, Integer questionTypeId, String processBarId);
	
	/**
	 * 合并
	 * 
	 * v1.0 chenyun 2021年3月2日下午1:25:51
	 * @param sourceId 试题分类源ID
	 * @param targetId 试题分类目标ID
	 * void
	 */
	void move(Integer sourceId, Integer targetId);
	
	/**
	 * 拷贝
	 * 
	 * v1.0 chenyun 2021年8月19日下午2:32:57
	 * @param id void
	 */
	void copy(Integer id) throws Exception;
	
	/**
	 * 发布
	 * 
	 * v1.0 chenyun 2021年8月19日下午2:32:57
	 * @param questionType
	 * @param ids
	 * @param id void
	 */
	void publish(Integer questionType, Integer[] ids) throws Exception;
}
