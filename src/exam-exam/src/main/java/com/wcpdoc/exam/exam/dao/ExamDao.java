package com.wcpdoc.exam.exam.dao;

import java.util.List;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.exam.entity.Exam;
import com.wcpdoc.exam.exam.entity.ExamType;
import com.wcpdoc.exam.exam.entity.MarkUser;

/**
 * 考试数据访问层接口
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
public interface ExamDao extends BaseDao<Exam>{
	
	/**
	 * 获取考试用户列表 
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getExamUserListpage(PageIn pageIn);

	/**
	 * 获取用户列表 
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getExamUserAddListpage(PageIn pageIn);

	/**
	 * 获取考试用户试题
	 * 
	 * v1.0 zhanghc 2017年6月19日下午5:44:52
	 * @param examId
	 * @param userId
	 * @param questionId
	 * @return ExamUserQuestion
	 */
	//ExamUserQuestion getExamUserQuestion(Integer examId, Integer userId, Integer questionId);

	/**
	 * 完成删除用户
	 * 
	 * v1.0 zhanghc 2017年6月19日下午8:17:32
	 * @param id
	 * @param userId
	 * void
	 */
	void doUserDel(Integer id, Integer userId);

	/**
	 * 获取考试分类
	 * 
	 * v1.0 zhanghc 2017年6月29日上午7:37:44
	 * @param id
	 * @return ExamType
	 */
	ExamType getExamType(Integer id);

	/**
	 * 获取考试分类列表
	 * 
	 * v1.0 zhanghc 2017年8月6日下午10:03:54
	 * @param examTypeId
	 * @return List<Exam>
	 */
	List<Exam> getList(Integer examTypeId);

	/**
	 * 获取我的考试列表
	 * 
	 * v1.0 zhanghc 2017年8月11日下午5:31:36
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getMyExamListpage(PageIn pageIn);

	/**
	 * 获取判卷用户列表
	 * 
	 * v1.0 zhanghc 2017年8月14日上午11:00:01
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getMarkUserListpage(PageIn pageIn);
	

	/**
	 * 获取判卷用户添加列表 
	 * 
	 * v1.0 zhanghc 2017年8月14日上午11:15:15
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getMarkUserAddListpage(PageIn pageIn);

	/**
	 * 获取判卷列表
	 * 
	 * v1.0 zhanghc 2017年8月23日下午4:07:11
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getMarkListpage(PageIn pageIn);

	/**
	 * 完成强制交卷
	 * 
	 * v1.0 zhanghc 2017年8月28日上午10:42:34
	 * @param user
	 * void
	 */
	void doForcePaper(LoginUser user);

	/**
	 * 获取判卷用户列表
	 * 
	 * v1.0 zhanghc 2017年8月28日下午1:53:23
	 * @param id
	 * @return List<MarkUser>
	 */
	List<MarkUser> getMarkUserList(Integer id);

	/**
	 * 获取成绩列表
	 * 
	 * v1.0 zhanghc 2017年8月29日下午3:18:37
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getGradeListpage(PageIn pageIn);
}
