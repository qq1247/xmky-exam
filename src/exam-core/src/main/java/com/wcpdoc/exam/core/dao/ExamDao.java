package com.wcpdoc.exam.core.dao;

import java.util.List;
import java.util.Map;

import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;

/**
 * 考试数据访问层接口
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
public interface ExamDao extends BaseDao<Exam>{
	
	/**
	 * 获取我的考试列表 
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getUserListpage(PageIn pageIn);

	/**
	 * 获取考试分类列表
	 * 
	 * v1.0 zhanghc 2017年8月6日下午10:03:54
	 * @param examTypeId
	 * @return List<Exam>
	 */
	List<Exam> getList(Integer examTypeId);

	/**
	 * 完成强制交卷
	 * 
	 * v1.0 zhanghc 2017年8月28日上午10:42:34
	 * @param user
	 * void
	 */
	void doForcePaper(LoginUser user);

	/**
	 * 获取成绩列表
	 * 
	 * v1.0 zhanghc 2017年8月29日下午3:18:37
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getGradeListpage(PageIn pageIn);
	
	/**
	 * 获取考试列表
	 * 
	 * v1.0 zhanghc 2017年8月6日下午10:03:09
	 * @param paperId
	 * @return List<Exam>
	 */
	List<Exam> getExamList(Integer paperId);
	
	
	/**
	 * 考试试题列表
	 * 
	 * v1.0 zhanghc 2017年8月6日下午10:03:09
	 * @param id
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> questionList(Integer id); 
}
