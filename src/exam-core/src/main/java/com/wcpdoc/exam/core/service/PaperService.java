package com.wcpdoc.exam.core.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.entity.PaperQuestionEx;
import com.wcpdoc.exam.core.entity.PaperType;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.service.BaseService;
/**
 * 试卷服务层接口
 * 
 * v1.0 zhanghc 2017-05-25 16:34:59
 */
public interface PaperService extends BaseService<Paper>{

	/**
	 * 获取试卷分类树
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @param userId 
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getPaperTypeTreeList(Integer userId);

	/**
	 * 获取试卷分类
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @param id 
	 * @return PaperType
	 */
	PaperType getPaperType(Integer id);

	/**
	 * 完成设置试卷分类
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @param ids 试题ID
	 * @param paperTypeId 试题分类ID
	 * @return PageResult
	 */
	void doPaperTypeUpdate(Integer[] ids, Integer paperTypeId);

	/**
	 * 获取配置试卷树
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getPaperCfgPaperTreeList(Integer id);

	/**
	 * 获取试卷试题实体
	 * 
	 * v1.0 zhanghc 2017年5月26日下午4:18:44
	 * @param paperQuestionId
	 * @return PaperQuestion
	 */
	PaperQuestion getPaperQuestion(Integer paperQuestionId);

	/**
	 * 初始化根试卷试题
	 * 
	 * v1.0 zhanghc 2017年5月26日下午4:59:09
	 * @param id
	 * @param user
	 * void
	 */
	@Deprecated
	void initRootPaperQuestion(Integer id, LoginUser user);

	/**
	 * 完成添加章节
	 * 
	 * v1.0 zhanghc 2017年5月27日上午9:42:57
	 * @param chapter
	 * @param user
	 * void
	 */
	void doChapterAdd(PaperQuestion chapter, LoginUser user);

	/**
	 * 完成修改章节
	 * 
	 * v1.0 zhanghc 2017年5月27日上午11:05:42
	 * @param chapter
	 * @param user
	 * void
	 */
	void doChapterEdit(PaperQuestion chapter, LoginUser user);
	
	/**
	 * 完成删除章节
	 * 
	 * v1.0 zhanghc 2017年5月27日下午5:58:58
	 * @param chapterId
	 * void
	 */
	void doChapterDel(Integer chapterId);

	/**
	 * 获取试题列表
	 * 
	 * v1.0 zhanghc 2017年5月27日下午2:41:06
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getQuestionListpage(PageIn pageIn);

	/**
	 * 完成添加试题
	 * 
	 * v1.0 zhanghc 2017年5月27日下午3:32:43
	 * @param chapterId
	 * @param questionIds
	 * @param user
	 * void
	 */
	void doQuestionAdd(Integer chapterId, Integer[] questionIds, LoginUser user);

	/**
	 * 获取试题列表
	 * 
	 * v1.0 zhanghc 2017年6月1日下午3:46:36
	 * @param paperQuestionId
	 * @return List<PaperQuestion>
	 */
	List<PaperQuestion> getPaperQuestionList(Integer paperQuestionId);

	/**
	 * 获取试题分类树
	 * 
	 * v1.0 zhanghc 2017年6月4日下午4:36:56
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getQuestionTypeTreeList();

	/**
	 * 获取试卷列表
	 * 
	 * v1.0 zhanghc 2017年6月6日上午8:52:39
	 * @param id
	 * @return List<PaperQuestionEx>
	 */
	List<PaperQuestionEx> getPaperList(Integer id);

	/**
	 * 完成更新试卷分数
	 * 
	 * v1.0 zhanghc 2017年6月9日下午4:21:52
	 * @param chapterId
	 * @param scores
	 * @param options
	 * void
	 */
	void doScoresUpdate(Integer chapterId, BigDecimal score, String options);

	/**
	 * 获取试题列表
	 * 
	 * v1.0 zhanghc 2017年6月19日下午4:53:21
	 * @param paperId
	 * @return List<Question>
	 */
	List<Question> getQuestionList(Integer paperId);

	/**
	 * 获取试卷列表
	 * 
	 * v1.0 zhanghc 2017年8月6日下午9:55:07
	 * @param paperId
	 * @return List<Paper>
	 */
	List<Paper> getList(Integer paperId);

	/**
	 * 删除试卷
	 * 
	 * v1.0 zhanghc 2017年8月6日下午10:42:19
	 * @param ids void
	 */
	void delAndUpdate(Integer[] ids);

	/**
	 * 获取试卷分类
	 * 
	 * v1.0 zhanghc 2018年10月13日上午11:24:11
	 * @param paperTypeId
	 * @return Papertype
	 */
	PaperType getPaperType2(Integer paperTypeId);

	/**
	 * 获取试题分类书列表
	 * 
	 * v1.0 zhanghc 2018年10月20日下午9:51:21
	 * @param userId
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getQuestionTypeTreeList(Integer userId);

	/**
	 * 完成清空试题
	 * 
	 * v1.0 zhanghc 2018年10月21日上午8:18:04
	 * @param chapterId
	 * @param currentUser void
	 */
	void doQuestionClear(Integer chapterId, LoginUser currentUser);

	/**
	 * 完成章节上移
	 * 
	 * v1.0 zhanghc 2018年10月21日下午2:51:35
	 * @param chapterId void
	 */
	void doChapterUp(Integer chapterId);
	
	/**
	 * 完成章节下移
	 * 
	 * v1.0 zhanghc 2018年10月21日下午2:51:35
	 * @param chapterId void
	 */
	void doChapterDown(Integer chapterId);

	/**
	 * 完成设置分数
	 * 
	 * v1.0 zhanghc 2018年10月21日下午3:10:37
	 * @param paperQuestionId 
	 * @param score 
	 * @param options 
	 * void
	 */
	void doScoreUpdate(Integer paperQuestionId, BigDecimal score, String options);

	/**
	 * 完成试题上移
	 * 
	 * v1.0 zhanghc 2018年10月21日下午4:15:35
	 * @param paperQuestionId void
	 */
	void doQuestionUp(Integer paperQuestionId);

	/**
	 * 完成试题下移
	 * 
	 * v1.0 zhanghc 2018年10月21日下午4:15:43
	 * @param paperQuestionId void
	 */
	void doQuestionDown(Integer paperQuestionId);

	/**
	 * 完成试题删除
	 * 
	 * v1.0 zhanghc 2018年10月21日下午10:43:15
	 * @param paperQuestionId void
	 */
	void doQuestionDel(Integer paperQuestionId);

	/**
	 * 获取试卷试题
	 * 
	 * v1.0 zhanghc 2018年10月27日上午9:28:40
	 * @param paperId
	 * @param questionId
	 * @return PaperQuestion
	 */
	PaperQuestion getPaperQuestion(Integer paperId, Integer questionId);

}
