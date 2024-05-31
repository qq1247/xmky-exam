package com.wcpdoc.exam.core.service;

import java.math.BigDecimal;
import java.util.List;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.Question;

/**
 * 试题服务层接口
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
public interface QuestionService extends BaseService<Question> {

	/**
	 * 试题添加
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * 
	 * @param question 试题
	 * @param options  选项（单选多选时有效）
	 * @param answers  答案
	 * @param scores   答案分数（填空或智能问答有多项）
	 * @return PageResult
	 */
	void addEx(Question question, List<String> options, List<String> answers, List<BigDecimal> scores);

	/**
	 * 试题修改
	 * 
	 * v1.0 zhanghc 2018年10月12日下午7:30:02
	 * 
	 * @param question 试题
	 * @param options  选项（单选多选时有效）
	 * @param answers  答案
	 * @param scores   答案分数（填空或智能问答有多项）
	 * @return PageResult
	 */
	void updateEx(Question question, List<String> options, List<String> answers, List<BigDecimal> scores);

	/**
	 * 试题删除
	 * 
	 * v1.0 chenyun 2021年8月25日下午5:28:51
	 * 
	 * @param id void
	 */
	void delEx(Integer id);

	/**
	 * 试题复制
	 * 
	 * v1.0 chenyun 2021年8月19日下午2:32:57
	 * 
	 * @param id void
	 */
	void copy(Integer id) throws Exception;

	/**
	 * 获取试题列表
	 * 
	 * v1.0 chenyun 2022年4月22日上午11:22:25
	 * 
	 * @return List<Integer>
	 */
	List<Question> getListByDel();

	/**
	 * 获取试题ids
	 * 
	 * v1.0 zhanghc 2022年5月23日下午1:39:14
	 * 
	 * @param questionTypeId
	 * @return List<Integer>
	 */
	List<Integer> getIds(Integer questionTypeId);

	/**
	 * 获取试题列表
	 * 
	 * v1.0 zhanghc 2022年5月23日下午1:39:14
	 * 
	 * @param questionTypeId
	 * @return List<Question>
	 */
	List<Question> getList(Integer questionTypeId);
}
