package com.wcpdoc.exam.core.dao;

import java.util.List;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionOption;

/**
 * 试卷数据访问层接口
 * 
 * zhanghc 2018年10月21日上午8:16:06
 */
public interface PaperDao extends RBaseDao<Paper> {
	/**
	 * 获取试卷列表
	 * 
	 * v1.0 zhanghc 2017年8月6日下午9:44:51
	 * @param paperTypeId
	 * @return List<Paper>
	 */
	List<Paper> getList(Integer paperTypeId);
	
	/**
	 * 获取试题列表
	 * 
	 * v1.0 zhanghc 2017年6月6日上午9:18:38
	 * @param id
	 * @return
	 * List<Question>
	 */
	List<Question> getQuestionList(Integer id);

	/**
	 * 获取试题列表
	 * 
	 * v1.0 zhanghc 2017年6月6日上午9:18:38
	 * @param id
	 * @return
	 * List<Question>
	 */
	List<Question> getQuestionList(Integer examId, Integer userId);
	
	/**
	 * 获取试题选项列表
	 * 
	 * v1.0 zhanghc 2022年5月20日下午1:32:45
	 * @param id
	 * @return List<QuestionOption>
	 */
	List<QuestionOption> getQuestionOptionList(Integer id);

	/**
	 * 获取试题选项列表
	 * 
	 * v1.0 zhanghc 2022年5月20日下午1:32:45
	 * @param id
	 * @return List<QuestionOption>
	 */
	List<QuestionOption> getQuestionOptionList(Integer examId, Integer userId);

}
