package com.wcpdoc.exam.core.service;

import java.math.BigDecimal;
import java.util.List;

import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.entity.PaperQuestionEx;
import com.wcpdoc.exam.core.entity.Question;

/**
 * 试卷服务层接口
 * 
 * zhanghc 2018年10月21日上午8:16:06
 */
public interface PaperService extends BaseService<Paper> {
	/**
	 * 完成添加章节
	 * 
	 * v1.0 zhanghc 2017年5月27日上午9:42:57
	 * @param chapter
	 * @param user
	 * void
	 */
	void chapterAdd(PaperQuestion chapter);
	
	/**
	 * 完成修改章节
	 * 
	 * v1.0 zhanghc 2017年5月27日上午11:05:42
	 * @param chapter
	 * @param user
	 * void
	 */
	void chapterEdit(PaperQuestion chapter);
	
	/**
	 * 完成删除章节
	 * 
	 * v1.0 zhanghc 2017年5月27日下午5:58:58
	 * @param chapterId
	 * void
	 */
	void chapterDel(Integer chapterId);
	
	/**
	 * 获取试卷列表
	 * 
	 * v1.0 zhanghc 2017年6月6日上午8:52:39
	 * @param id
	 * @return List<PaperQuestionEx>
	 */
	List<PaperQuestionEx> getPaperList(Integer id);
	
	/**
	 * 获取试题列表
	 * 
	 * v1.0 zhanghc 2017年6月19日下午4:53:21
	 * @param paperId
	 * @return List<Question>
	 */
	List<Question> getQuestionList(Integer paperId);

	/**
	 * 完成添加试题
	 * 
	 * v1.0 zhanghc 2017年5月27日下午3:32:43
	 * @param chapterId
	 * @param questionIds
	 * void
	 */
	void questionAdd(Integer chapterId, Integer[] questionIds);
	
	/**
	 * 设置分数
	 * 
	 * v1.0 zhanghc 2018年10月21日下午3:10:37
	 * @param paperQuestionId 
	 * @param score 
	 * void
	 */
	void scoreUpdate(Integer paperQuestionId, BigDecimal score);
	
	/**
	 * 完成设置选项
	 * 
	 * v1.0 zhanghc 2018年10月21日下午3:10:37
	 * @param paperQuestionId 
	 * @param score 
	 * void
	 */
	void optionsUpdate(Integer paperQuestionId, Integer[] scoreOptions);

	/**
	 * 完成试题上移
	 * 
	 * v1.0 zhanghc 2018年10月21日下午4:15:35
	 * @param paperQuestionId void
	 */
	void questionUp(Integer paperQuestionId);

	/**
	 * 完成试题下移
	 * 
	 * v1.0 zhanghc 2018年10月21日下午4:15:43
	 * @param paperQuestionId void
	 */
	void questionDown(Integer paperQuestionId);

	/**
	 * 完成试题删除
	 * 
	 * v1.0 zhanghc 2018年10月21日下午10:43:15
	 * @param paperQuestionId void
	 */
	void questionDel(Integer paperQuestionId);
	
	/**
	 * 完成清空试题
	 * 
	 * v1.0 zhanghc 2018年10月21日上午8:18:04
	 * @param chapterId
	 * void
	 */
	void questionClear(Integer chapterId);

	/**
	 * 完成章节上下移
	 * 
	 * v1.0 chenyun 2021年7月9日下午3:22:49
	 * @param oldId
	 * @param newId void
	 */
	void chapterUp(Integer oldId, Integer newId);
	
	/**
	 * 完成章节下移
	 * 
	 * v1.0 zhanghc 2018年10月21日下午2:51:35
	 * @param chapterId void
	 */
	void chapterDown(Integer chapterId);

	/**
	 * 获取试卷列表
	 * 
	 * v1.0 zhanghc 2017年8月6日下午9:43:55
	 * @param paperTypeId
	 * @return List<Paper>
	 */
	List<Paper> getList(Integer paperTypeId);

	/**
	 * 完成批量更新分数
	 * 
	 * v1.0 zhanghc 2017年6月9日下午4:21:52
	 * @param chapterId
	 * @param scores
	 * @param options
	 * void
	 */
	void batchScoreUpdate(Integer chapterId, BigDecimal score, String options);
	
}
