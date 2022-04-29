package com.wcpdoc.exam.report.dao;

import java.util.List;
import java.util.Map;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;

/**
 * 统计数据访问层接口
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
public interface ReportDao extends RBaseDao<Object> {
	
	/**
	 * 首页user
	 * 
	 * v1.0 chenyun 2021年12月10日下午1:37:38
	 * @param userId
	 * @return Map<String,Object>
	 */
	List<Map<String, Object>> homeUser(Integer userId);
	
	/**
	 * 缺考数
	 * 
	 * v1.0 chenyun 2022年1月19日下午2:23:33
	 * @param userId
	 * @return Integer
	 */
	Integer homeUserMissNum(Integer userId);
	
	/**
	 * 首页subAdmin
	 * 
	 * v1.0 chenyun 2021年12月10日下午1:37:38
	 * @param userId
	 * @return Map<String,Object>
	 */
	List<Map<String, Object>> homeSubAdminExam(Integer userId);
	
	/**
	 * 首页subAdmin
	 * 
	 * v1.0 chenyun 2021年12月10日下午1:37:38
	 * @param userId
	 * @return Integer
	 */
	Integer homeSubAdminPaper(Integer userId);
	/**
	 * 首页subAdmin
	 * 
	 * v1.0 chenyun 2021年12月10日下午1:37:38
	 * @param userId
	 * @return Integer
	 */
	Integer homeSubAdminQuestion(Integer userId);
	/**
	 * 首页subAdmin
	 * 
	 * v1.0 chenyun 2021年12月10日下午1:37:38
	 * @param userId
	 * @return Integer
	 */
	Integer homeSubAdminMark(Integer userId);

	/**
	 * 首页admin
	 * 
	 * v1.0 chenyun 2021年12月10日下午1:37:38
	 * @return Map<String,Object>
	 */
	List<Map<String, Object>> homeAdminUser();
	
	/**
	 * 首页admin
	 * 
	 * v1.0 chenyun 2021年12月10日下午1:37:38
	 * @return Map<String,Object>
	 */
	List<Map<String, Object>> homeAdminBulletin();
	
	/**
	 * 试题统计
	 * 
	 * v1.0 chenyun 2021年12月15日下午4:16:58
	 * @param questionTypeId
	 * @return List<Map<String, Object>>
	 */
	List<Map<String, Object>> questionStatis(Integer questionTypeId);

	/**
	 * 考试统计-试题类型列表
	 * 
	 * v1.0 chenyun 2021年12月16日上午9:38:30
	 * @param paperId
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> examStatisType(Integer paperId);
	
	/**
	 * 我的考试列表
	 * 
	 * v1.0 chenyun 2021年12月16日上午9:38:30
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut myExamListpage(PageIn pageIn);

	/**
	 * 试卷列表
	 * 
	 * v1.0 chenyun 2021年12月16日上午9:38:30
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut questionListpage(PageIn pageIn);
	
	/**
	 * 分数统计
	 * 
	 * v1.0 chenyun 2021年3月24日上午10:04:05
	 * @param examId
	 * @return List<Map<String, Object>>
	 */
	List<Map<String, Object>> count(Integer examId);
}
