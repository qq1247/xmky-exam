package com.wcpdoc.exam.exam.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.ui.Model;

import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.service.BaseService;
import com.wcpdoc.exam.exam.entity.Exam;
import com.wcpdoc.exam.exam.entity.ExamType;
import com.wcpdoc.exam.exam.entity.ExamUser;
import com.wcpdoc.exam.exam.entity.MarkUser;
import com.wcpdoc.exam.exam.entity.Paper;
/**
 * 考试服务层接口
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
public interface ExamService extends BaseService<Exam>{
	
	/**
	 * 添加考试
	 * 
	 * v1.0 zhanghc 2017-06-11 09:13:23
	 * @param exam
	 * void
	 * @param user 
	 */
	void saveAndUpdate(Exam exam, LoginUser user);
	
	/**
	 * 修改考试
	 * 
	 * v1.0 zhanghc 2017年8月14日下午2:08:37
	 * @param exam
	 * @param user 
	 * void
	 */
	void updateAndUpdate(Exam exam, LoginUser user);

	/**
	 * 获取试卷分类数据
	 * 
	 * v1.0 zhanghc 2017年6月11日下午5:54:09
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getPaperAddListTypeTreeList();

	/**
	 * 获取试卷分类数据
	 * 
	 * v1.0 zhanghc 2017年6月11日下午5:54:58
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getPaperListpage(PageIn pageIn);

	/**
	 * 获取试卷
	 * 
	 * v1.0 zhanghc 2017年6月12日下午11:07:40
	 * @param paperId
	 * @return Paper
	 */
	Paper getPaper(Integer paperId);

	/**
	 * 获取考试用户列表 
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getExamUserListpage(PageIn pageIn);

	/**
	 * 获取组织机构树
	 * 
	 * v1.0 zhanghc 2017年6月19日上午6:12:24
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getOrgTreeList();

	/**
	 * 获取考试用户添加列表 
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getExamUserAddListpage(PageIn pageIn);

	/**
	 * 获取组织机构树
	 * 
	 * v1.0 zhanghc 2017年6月19日上午6:12:24
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getExamUserAddOrgTreeList();

	/**
	 * 完成添加用户
	 * 
	 * v1.0 zhanghc 2017年6月19日下午3:10:24
	 * @param user 
	 * @param id
	 * @param userIds
	 * void
	 */
	void doExamUserAdd(LoginUser user, Integer id, Integer[] userIds);

	/**
	 * 完成删除用户
	 * 
	 * v1.0 zhanghc 2017年6月19日下午8:13:46
	 * @param examUserIds
	 * void
	 */
	void doExamUserDel(Integer[] examUserIds);

	/**
	 * 获取未结束的考试列表
	 * 
	 * v1.0 zhanghc 2017年6月22日下午10:30:34
	 * @param userId
	 * @return List<Map<String, Object>>
	 */
	List<Map<String, Object>> getUnFinishList(Integer userId);

	/**
	 * 获取考试分类树
	 * 
	 * v1.0 zhanghc 2017年6月29日上午7:24:13
	 * @param userId 
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getExamTypeTreeList(Integer userId);
	
	/**
	 * 获取考试分类
	 * 
	 * v1.0 zhanghc 2017年6月29日上午7:33:33
	 * @param id
	 * @return ExamType
	 */
	ExamType getExamType(Integer id);

	/**
	 * 更新答案
	 * 
	 * v1.0 zhanghc 2017年7月3日下午1:44:12
	 * @param user
	 * @param examUserQuestionId
	 * @param answer
	 * void
	 */
	void updateAnswer(LoginUser user, Integer examUserQuestionId, String answer);

	/**
	 * 完成试卷
	 * 
	 * v1.0 zhanghc 2017年7月3日下午11:17:50
	 * @param user
	 * @param examUserId
	 * void
	 */
	void doPaper(LoginUser user, Integer examUserId);
	
	/**
	 * 完成强制交卷
	 * 
	 * v1.0 zhanghc 2017年8月28日上午10:37:27
	 * @param user
	 * @param examUserId
	 * void
	 */
	void doForcePaper(LoginUser user);
	
	/**
	 * 完成强制交卷
	 * 
	 * v1.0 zhanghc 2017年8月28日上午10:37:27
	 * @param user
	 * @param examUserId
	 * void
	 */
	void doForcePaper();

	/**
	 * 获取判卷用户列表
	 * 
	 * v1.0 zhanghc 2017年7月4日下午3:05:47
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getMarkUserListpage(PageIn pageIn);

	/**
	 * 更新判卷分数
	 * 
	 * v1.0 zhanghc 2017年7月4日下午5:47:22
	 * @param user
	 * @param examUserQuestionId
	 * @param score
	 * void
	 */
	void updateMarkScore(LoginUser user, Integer examUserQuestionId, BigDecimal score);

	/**
	 * 完成判卷
	 * 
	 * v1.0 zhanghc 2017年7月4日下午9:53:24
	 * @param user 
	 * @param examUserId
	 * void
	 */
	void doMark(LoginUser user, Integer examUserId);

	/**
	 * 获取考试列表
	 * 
	 * v1.0 zhanghc 2017年8月6日下午10:03:09
	 * @param examTypeId
	 * @return List<Exam>
	 */
	List<Exam> getList(Integer examTypeId);

	/**
	 * 删除考试
	 * 
	 * v1.0 zhanghc 2017年8月6日下午11:09:53
	 * @param ids
	 * void
	 */
	void delAndUpdate(Integer[] ids);

	/**
	 * 获取我的考试列表
	 * 
	 * v1.0 zhanghc 2017年8月11日下午5:24:52
	 * @param pageIn
	 * @return PageOut
	 */
	@Deprecated
	PageOut getMyExamListpage(PageIn pageIn);

	/**
	 * 获取判卷用户添加列表 
	 * 
	 * v1.0 zhanghc 2017年8月14日上午11:15:15
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getMarkUserAddListpage(PageIn pageIn);

	/**
	 * 完成添加用户
	 * 
	 * v1.0 zhanghc 2017年6月19日下午3:10:24
	 * @param id
	 * @param userIds
	 * void
	 */
	void doMarkUserAdd(Integer id, Integer[] userIds);

	/**
	 * 完成删除用户
	 * 
	 * v1.0 zhanghc 2017年6月19日下午3:10:24
	 * @param id
	 * @param userIds
	 * void
	 */
	void doMarkUserDel(Integer[] markUserIds);

	/**
	 * 获取判卷列表
	 * 
	 * v1.0 zhanghc 2017年8月23日下午4:06:13
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getMarkListpage(PageIn pageIn);

	/**
	 * 获取判卷用户列表
	 * 
	 * v1.0 zhanghc 2017年8月28日下午1:51:49
	 * @param id
	 * @return List<MarkUser>
	 */
	List<MarkUser> getMarkUserList(Integer id);

	/**
	 * 获取成绩列表
	 * 
	 * v1.0 zhanghc 2017年8月29日下午3:16:12
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getGradeListpage(PageIn pageIn);

	/**
	 * 获取考试分类
	 * 
	 * v1.0 zhanghc 2018年10月26日下午2:37:02
	 * @param examTypeId
	 * @return Object
	 */
	ExamType getExamType2(Integer examTypeId);

	/**
	 * 获取试卷分类树形列表
	 * 
	 * v1.0 zhanghc 2018年10月27日上午9:26:18
	 * @param userId
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getPaperTypeTreeList(Integer userId);

	/**
	 * 到达试卷页面
	 * 
	 * v1.0 zhanghc 2017年8月28日上午8:59:40
	 * @param model
	 * @param loginUser 
	 * @param examId
	 * void
	 */
	void toPaper(Model model, LoginUser loginUser, Integer examId);

	/**
	 * 到达判卷页面
	 * 
	 * v1.0 zhanghc 2017年8月28日上午11:04:18
	 * @param model
	 * @param user 
	 * @param examUserId
	 * void
	 */
	void toMark(Model model, LoginUser currentUser, Integer examUserId);

	/**
	 * 获取考试用户信息
	 * 
	 * v1.0 zhanghc 2017年7月3日上午9:41:59
	 * @param examId
	 * @param userId
	 * @return ExamUser
	 */
	ExamUser getExamUser(Integer examId, Integer userId);

	/**
	 * 完成发布
	 * 
	 * v1.0 zhanghc 2018年11月24日上午9:23:22
	 * @param id 
	 * void
	 */
	void doPublish(Integer id);

}
