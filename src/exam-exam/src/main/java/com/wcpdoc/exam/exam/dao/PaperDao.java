package com.wcpdoc.exam.exam.dao;

import java.util.List;
import java.util.Map;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.exam.entity.Paper;
import com.wcpdoc.exam.exam.entity.PaperQuestion;
import com.wcpdoc.exam.exam.entity.PaperType;
import com.wcpdoc.exam.exam.entity.Question;

/**
 * 试卷数据访问层接口
 * 
 * v1.0 zhanghc 2017-05-25 16:34:59
 */
public interface PaperDao extends BaseDao<Paper>{

	/**
	 * 获取试卷分类
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @param id
	 * @return PaperType
	 */
	PaperType getPaperType(Integer id);

	/**
	 * 获取配置试卷树
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @param id
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getPaperCfgPaperTreeList(Integer id);

	/**
	 * 获取根试卷试题
	 * 
	 * v1.0 zhanghc 2017年5月26日下午5:11:33
	 * @param id
	 * @return
	 * PaperQuestion
	 */
	PaperQuestion getRootPaperQuestion(Integer id);

	/**
	 * 获取试题列表
	 * 
	 * v1.0 zhanghc 2017年6月4日下午5:13:57
	 * @param pageIn
	 * @return
	 * PageOut
	 */
	PageOut getPaperCfgListpage(PageIn pageIn);

	/**
	 * 获取试卷试题列表
	 * 
	 * v1.0 zhanghc 2017年6月6日上午9:07:59
	 * @param id
	 * @return List<PaperQuestion>
	 */
	List<PaperQuestion> getPaperQuestionList(Integer id);

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
	 * 获取试卷列表
	 * 
	 * v1.0 zhanghc 2017年8月6日下午9:56:04
	 * @param paperId
	 * @return List<Paper>
	 */
	List<Paper> getList(Integer paperId);
	
}
