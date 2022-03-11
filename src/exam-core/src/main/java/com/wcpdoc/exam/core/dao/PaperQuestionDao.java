package com.wcpdoc.exam.core.dao;

import java.util.List;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.entity.Question;

/**
 * 试卷试题数据访问层接口
 * 
 * v1.0 zhanghc 2017-05-26 14:23:38
 */
public interface PaperQuestionDao extends BaseDao<PaperQuestion>{
	/**
	 * 获取试卷试题列表
	 * 
	 * v1.0 zhanghc 2017年5月27日上午10:51:44
	 * @param parentId
	 * @param examId
	 * @param userId
	 * @return List<PaperQuestion>
	 */
	List<PaperQuestion> getQuestionList(Integer parentId, Integer examId, Integer userId);

	/**
	 * 获取试卷试题列表
	 * 
	 * v1.0 zhanghc 2017年6月29日下午2:50:46
	 * @param paperId
	 * @return
	 * List<PaperQuestion>
	 */
	List<PaperQuestion> getList(Integer paperId);

	/**
	 * 获取章节列表
	 * 
	 * v1.0 zhanghc 2018年10月21日下午2:43:46
	 * @param paperId
	 * @return List<PaperQuestion>
	 */
	List<PaperQuestion> getChapterList(Integer paperId);

	/**
	 * 获取试卷试题
	 * 
	 * v1.0 zhanghc 2020年10月15日下午4:39:23
	 * @param paperId
	 * @param questionId
	 * @return PaperQuestion
	 */
	PaperQuestion getEntity(Integer paperId, Integer questionId);
	
	/**
	 * 获取试卷试题
	 * 
	 * v1.0 chenyun 2022年3月11日下午3:22:39
	 * @param examId
	 * @param paperId
	 * @param questionId
	 * @param userId
	 * @return PaperQuestion
	 */
	PaperQuestion getEntity(Integer examId, Integer paperId, Integer questionId, Integer userId);
	
	/**
	 * 删除随机试题
	 * 
	 * v1.0 chenyun 2022年2月15日上午9:56:07
	 * @param userId
	 * @param paperId void
	 */
	void del(Integer examId, Integer userId);
	
	/**
	 * 获取随机试题列表
	 * 
	 * v1.0 chenyun 2022年2月15日下午1:24:04
	 * @param paperId
	 * @return List<Question>
	 */
	List<Question> getQuestionRandList(Integer examId, Integer paperId);
	
	/**
	 * 获取随机试题列表选择项
	 * 
	 * v1.0 chenyun 2022年2月16日下午3:44:18
	 * @param paperId
	 * @return List<PaperQuestion>
	 */
	List<PaperQuestion> getPaperQuestionList(Integer examId, Integer paperId);
	
	/**
	 * 获取试题试卷列表
	 * 
	 * v1.0 chenyun 2022年3月9日下午2:26:23
	 * @param questionId
	 * @return List<PaperQuestion>
	 */
	List<PaperQuestion> getPaperQuestionList(Integer questionId);
}
