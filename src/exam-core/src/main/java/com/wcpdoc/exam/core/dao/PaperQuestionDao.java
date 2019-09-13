package com.wcpdoc.exam.core.dao;

import java.util.List;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.PaperQuestion;

/**
 * 试卷试题数据访问层接口
 * 
 * v1.0 zhanghc 2017-05-26 14:23:38
 */
public interface PaperQuestionDao extends BaseDao<PaperQuestion>{

	/**
	 * 获取试卷试题
	 * 
	 * v1.0 zhanghc 2017年5月27日上午9:48:15
	 * @param name
	 * @return
	 * PaperQuestion
	 */
	PaperQuestion getPaperQuestionByName(String name);

	/**
	 * 获取试卷试题列表
	 * 
	 * v1.0 zhanghc 2017年5月27日上午10:51:44
	 * @param parentId
	 * @return List<PaperQuestion>
	 */
	List<PaperQuestion> getList(Integer parentId);

	/**
	 * 获取试卷试题
	 * 
	 * v1.0 zhanghc 2017年6月5日下午6:31:32
	 * @param paperId
	 * @return
	 * PaperQuestion
	 */
	PaperQuestion getTopPaperQuestion(Integer paperId);

	/**
	 * 获取试卷试题列表
	 * 
	 * v1.0 zhanghc 2017年6月29日下午2:50:46
	 * @param paperId
	 * @return
	 * List<PaperQuestion>
	 */
	List<PaperQuestion> getPaperQuestionList(Integer paperId);

	/**
	 * 获取试卷试题
	 * 
	 * v1.0 zhanghc 2017年8月24日上午8:58:55
	 * @param paperId
	 * @param questionId
	 * @return PaperQuestion
	 */
	PaperQuestion getEntity(Integer paperId, Integer questionId);

	/**
	 * 获取试卷试题
	 * 
	 * v1.0 zhanghc 2018年10月20日下午2:31:55
	 * @param paperId
	 * @return List<PaperQuestion>
	 */
	List<PaperQuestion> getList2(Integer paperId);

	/**
	 * 获取章节列表
	 * 
	 * v1.0 zhanghc 2018年10月21日下午2:43:46
	 * @param paperId
	 * @return List<PaperQuestion>
	 */
	List<PaperQuestion> getChapterList(Integer paperId);
	
}
