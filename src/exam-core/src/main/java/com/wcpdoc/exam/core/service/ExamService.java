package com.wcpdoc.exam.core.service;

import java.util.List;
import java.util.Map;

import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
/**
 * 考试服务层接口
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
public interface ExamService extends BaseService<Exam>{
	
	/**
	 * 添加考试
	 * 
	 * v1.0 chenyun 2021年8月25日下午5:58:55
	 * @param exam void
	 */
	void addAndUpdate(Exam exam);
	
	/**
	 * 修改考试
	 * 
	 * v1.0 chenyun 2021年8月25日下午6:01:30
	 * @param exam void
	 */
	void updateAndUpdate(Exam exam);
	
	/**
	 * 删除考试
	 * 
	 * v1.0 chenyun 2021年8月25日下午6:03:33
	 * @param id void
	 */
	void delAndUpdate(Integer id);
	
	/**
	 * 复制
	 * 
	 * v1.0 chenyun 2021年8月25日下午6:06:54
	 * @param id void
	 */
	void publish(Integer id);
	
	/**
	 * 获取考试用户列表 
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param id
	 * @return PageOut
	 */
	List<Map<String, Object>> getExamUserList(Integer id);

	/**
	 * 考试更新考试用户
	 * 
	 * v1.0 zhanghc 2017年6月19日下午3:10:24
	 * @param id
	 * @param userIds
	 * void
	 */
	void updateExamUser(Integer id, Integer[] userIds);
	
	/**
	 * 考试更新判卷用户
	 * 
	 * v1.0 zhanghc 2017年6月19日下午3:10:24
	 * @param id
	 * @param markUserIds
	 * @param examUserIds
	 * @param questionIds
	 * void
	 */
	void updateMarkUser(Integer id, Integer[] markUserIds, String[] examUserIds, String[] questionIds);
	
	/**
	 * 完成强制交卷
	 * 
	 * v1.0 zhanghc 2017年8月28日上午10:37:27
	 * @param user
	 * @param myExamId
	 * void
	 */
	void forcePaper(LoginUser user);

	/**
	 * 获取考试列表
	 * 
	 * v1.0 zhanghc 2017年8月6日下午10:03:09
	 * @param examTypeId
	 * @return List<Exam>
	 */
	List<Exam> getList(Integer examTypeId);

	/**
	 * 获取成绩列表
	 * 
	 * v1.0 zhanghc 2017年8月29日下午3:16:12
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
	 * 获取阅卷考试用户列表
	 * 
	 * v1.0 zhanghc 2021年6月25日下午2:49:33
	 * @param id
	 * @param markUserId
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getMarkExamUserList(Integer id, Integer markUserId);

	/**
	 * 获取阅卷试题列表
	 * 
	 * v1.0 zhanghc 2021年6月25日下午2:50:02
	 * @param id
	 * @param markUserId
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getMarkQuestionList(Integer id, Integer markUserId); 
}
