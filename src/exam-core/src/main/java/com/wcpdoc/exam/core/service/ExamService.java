package com.wcpdoc.exam.core.service;

import java.util.List;
import java.util.Map;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.Exam;
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
	 * @param exam 
	 * void
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
	 * 获取考试用户列表
	 * 
	 * v1.0 zhanghc 2021年6月25日下午2:49:33
	 * @param id
	 * @param markUserId
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getExamUserList(Integer id, Integer markUserId);

	/**
	 * 获取考试列表
	 * 
	 * v1.0 zhanghc 2017年8月6日下午10:03:09
	 * @param examTypeId
	 * @return List<Exam>
	 */
	List<Exam> getList(Integer examTypeId);
	
	/**
	 * 获取考试列表
	 * 
	 * v1.0 chenyun 2022年3月9日下午4:36:27
	 * @return List<Exam>
	 */
	List<Exam> getList();
	
	/**
	 * 考试邮件通知
	 * 
	 * v1.0 chenyun 2022年3月28日下午2:24:28
	 * @param exam
	 * @param notifyType 1.发送考试人  2.发送阅卷人  3.发送当前登录人
	 * @param content void
	 */
	void mail(Exam exam, Integer notifyType, String content);

	/**
	 * 变更考试时间
	 * 
	 * v1.0 zhanghc 2022年4月17日下午6:52:08
	 * @param id 考试ID
	 * @param timeType 时间类型：1：考试开始时间；2：考试结束时间；3：阅卷开始时间；4：阅卷结束时间
	 * @param minute 分钟数
	 * @return PageResult
	 */
	void timeUpdate(Integer id, Integer timeType, Integer minute);

	/**
	 * 添加用户
	 * 
	 * v1.0 zhanghc 2017年6月19日下午3:10:24
	 * @param id
	 * @param examUserIds 考试用户IDS
	 * @param markUserIds 阅卷用户IDS
	 * void
	 */
	void userAdd(Integer id, String[] examUserIds, Integer[] markUserIds);
	
	/**
	 * 是否有写权限（只能操作自己创建的分类）
	 * 
	 * v1.0 zhanghc 2022年6月17日上午11:19:58
	 * @param exam
	 * @param userId 
	 * @return boolean
	 */
	boolean hasWriteAuth(Exam exam);
}
