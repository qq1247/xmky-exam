package com.wcpdoc.exam.exam.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.service.BaseService;
import com.wcpdoc.exam.exam.entity.Paper;
import com.wcpdoc.exam.exam.entity.PaperQuestion;
import com.wcpdoc.exam.exam.entity.PaperQuestionEx;
import com.wcpdoc.exam.exam.entity.PaperType;
import com.wcpdoc.exam.exam.entity.Question;
/**
 * 试卷服务层接口
 * 
 * v1.0 zhanghc 2017-05-25 16:34:59
 */
public interface PaperService extends BaseService<Paper>{

	/**
	 * 获取试卷分类树型列表
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getPaperTypeTreeList();

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
	 * 获取配置试卷树型列表
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
	void initRootPaperQuestion(Integer id, LoginUser user);

	/**
	 * 完成添加章节
	 * 
	 * v1.0 zhanghc 2017年5月27日上午9:42:57
	 * @param paperQuestion
	 * @param user
	 * void
	 */
	void doPaperCfgAdd(PaperQuestion paperQuestion, LoginUser user);

	/**
	 * 完成修改章节
	 * 
	 * v1.0 zhanghc 2017年5月27日上午11:05:42
	 * @param paperQuestion
	 * @param user
	 * void
	 */
	void doPaperCfgEdit(PaperQuestion paperQuestion, LoginUser user);

	/**
	 * 试卷配置列表
	 * 
	 * v1.0 zhanghc 2017年5月27日下午2:41:06
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getPaperCfgListpage(PageIn pageIn);

	/**
	 * 完成添加试题
	 * 
	 * v1.0 zhanghc 2017年5月27日下午3:32:43
	 * @param paperId
	 * @param parentPaperQuestionId
	 * @param questionIds
	 * @param user
	 * void
	 */
	void doPaperCfgListAdd(Integer paperId, Integer parentPaperQuestionId, Integer[] questionIds, LoginUser user);

	/**
	 * 完成删除试题
	 * 
	 * v1.0 zhanghc 2017年5月27日下午5:58:58
	 * @param paperQuestionId
	 * void
	 */
	void doPaperCfgDel(Integer paperQuestionId);
	
	/**
	 * 完成试题排序
	 * 
	 * v1.0 zhanghc 2017年5月31日上午8:58:09
	 * @param paperQuestionId
	 * @param no
	 * void
	 */
	void doPaperCfgSort(Integer paperQuestionId, Integer no);

	/**
	 * 获取试题列表
	 * 
	 * v1.0 zhanghc 2017年6月1日下午3:46:36
	 * @param paperQuestionId
	 * @return List<PaperQuestion>
	 */
	List<PaperQuestion> getPaperQuestionList(Integer paperQuestionId);

	/**
	 * 获取试题分类树型列表
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
	 * @param paperQuestionIds
	 * @param scores
	 * void
	 */
	void doPaperCfgScoreUpdate(Integer[] paperQuestionIds, BigDecimal[] scores);

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
	void deleteAndUpdate(Integer[] ids);
	
}
