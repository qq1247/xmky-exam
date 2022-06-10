package com.wcpdoc.exam.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Service;

import com.wcpdoc.base.cache.ParmCache;
import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.cache.AutoMarkCache;
import com.wcpdoc.exam.core.dao.ExamDao;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.ExamType;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyMark;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperType;
import com.wcpdoc.exam.core.service.ExamExService;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.ExamTypeService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyMarkService;
import com.wcpdoc.exam.core.service.PaperQuestionAnswerService;
import com.wcpdoc.exam.core.service.PaperQuestionRuleService;
import com.wcpdoc.exam.core.service.PaperQuestionService;
import com.wcpdoc.exam.core.service.PaperService;
import com.wcpdoc.exam.core.service.PaperTypeService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.notify.service.NotifyService;

/**
 * 考试服务层实现
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
@Service
public class ExamServiceImpl extends BaseServiceImp<Exam> implements ExamService {
	@Resource
	private ExamDao examDao;
	@Resource
	private PaperService paperService;
	@Resource
	private QuestionService questionService;
	@Resource
	private PaperTypeService paperTypeService;
	@Resource
	private ExamTypeService examTypeService;
	@Resource
	private UserService userService;
	@Resource
	private PaperQuestionService paperQuestionService;
	@Resource
	private PaperQuestionRuleService paperQuestionRuleService;
	@Resource
	private PaperQuestionAnswerService paperQuestionAnswerService;
	@Resource
	private NotifyService notifyService;
	@Resource
	private ExamExService examExService;
	@Resource
	private MyMarkService myMarkService;
	@Resource
	private MyExamService myExamService;
	
	@Override
	@Resource(name = "examDaoImpl")
	public void setDao(BaseDao<Exam> dao) {
		super.dao = dao;
	}

	@Override
	public Integer addAndUpdate(Exam exam) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(exam.getName())) {
			throw new MyException("参数错误：name");
		}
		if (!ValidateUtil.isValid(exam.getPaperId())) {
			throw new MyException("参数错误：paperId");
		}
		if (!ValidateUtil.isValid(exam.getStartTime())) {
			throw new MyException("参数错误：startTime");
		}
		if (!ValidateUtil.isValid(exam.getEndTime())) {
			throw new MyException("参数错误：endTime");
		}
		// if (exam.getStartTime().getTime() <= new Date().getTime()) {
		// 	throw new MyException("考试开始时间必须大于当前时间");// 页面上选的当前时间，添加时就过期了
		// }
		if (exam.getEndTime().getTime() <= new Date().getTime()) {
			throw new MyException("考试结束时间必须大于当前时间");
		}
		if (exam.getStartTime().getTime() >= exam.getEndTime().getTime()) {
			throw new MyException("考试结束时间必须大于考试开始时间");
		}

		Paper paper = paperService.getEntity(exam.getPaperId());
		if (paper.getState() != 1) {
			throw new MyException("试卷未发布");
		}
		PaperType paperType = paperTypeService.getEntity(paper.getPaperTypeId());
		if (!paperTypeService.hasReadAuth(paperType, getCurUser().getId())) {
			throw new MyException("无操作权限");
		}
		ExamType examType = examTypeService.getEntity(exam.getExamTypeId());
		if (!examTypeService.hasAuth(examType, getCurUser().getId())) {
			throw new MyException("无操作权限");
		}
		
		if (paper.getMarkType() == 2) {// 如果是自动阅卷类型，没有阅卷开始时间和阅卷结束时间
			if (!ValidateUtil.isValid(exam.getMarkStartTime())) {
				throw new MyException("参数错误：markStartTime");
			}
			if (!ValidateUtil.isValid(exam.getMarkEndTime())) {
				throw new MyException("参数错误：markEndTime");
			}
			if (exam.getMarkStartTime().getTime() <= exam.getEndTime().getTime()) {
				throw new MyException("阅卷开始时间必须大于考试结束时间");
			}
			if (exam.getMarkStartTime().getTime() >= exam.getMarkEndTime().getTime()) {
				throw new MyException("阅卷结束时间必须大于阅卷开始时间");
			}
		}

		// 添加考试
		exam.setCreateUserId(getCurUser().getId());
		exam.setCreateTime(new Date());
		exam.setUpdateUserId(getCurUser().getId());
		exam.setUpdateTime(new Date());
		exam.setState(2);// 草稿
		exam.setMarkState(1);// 标记为未阅卷（考试时间结束，定时任务自动阅卷，标记为已阅）
		exam.setMarkStartTime(paper.getMarkType() == 1 ? null : exam.getMarkStartTime());
		exam.setMarkEndTime(paper.getMarkType() == 1 ? null : exam.getMarkEndTime());
		exam.setScoreState(2);
		exam.setRankState(2);
		exam.setAnonState(2);
		add(exam);
		return exam.getId(); //快速创建考试需要用id查找信息
	}
	
	@Override
	public void updateAndUpdate(Exam exam) {
		//校验数据有效性
		if (!ValidateUtil.isValid(exam.getName())) {
			throw new MyException("参数错误：name");
		}
		if (!ValidateUtil.isValid(exam.getPaperId())) {
			throw new MyException("参数错误：paperId");
		}
		if (!ValidateUtil.isValid(exam.getStartTime())) {
			throw new MyException("参数错误：startTime");
		}
		if (!ValidateUtil.isValid(exam.getEndTime())) {
			throw new MyException("参数错误：endTime");
		}
//		if (exam.getStartTime().getTime() <= new Date().getTime()) {
//			throw new MyException("考试开始时间必须大于当前时间");
//		}
		if (exam.getEndTime().getTime() <= new Date().getTime()) {
			throw new MyException("考试结束时间必须大于当前时间");
		}
		if (exam.getStartTime().getTime() >= exam.getEndTime().getTime()) {
			throw new MyException("考试结束时间必须大于考试开始时间");
		}

		Paper paper = paperService.getEntity(exam.getPaperId());
		if (paper.getState() != 1) {
			throw new MyException("试卷未发布");
		}
		PaperType paperType = paperTypeService.getEntity(paper.getPaperTypeId());
		if (!paperTypeService.hasReadAuth(paperType, getCurUser().getId())) {
			throw new MyException("无操作权限");
		}
		ExamType examType = examTypeService.getEntity(exam.getExamTypeId());
		if (!examTypeService.hasAuth(examType, getCurUser().getId())) {
			throw new MyException("无操作权限");
		}
		if (paper.getMarkType() == 2) {// 如果是自动阅卷类型，没有阅卷开始时间和阅卷结束时间
			if (!ValidateUtil.isValid(exam.getMarkStartTime())) {
				throw new MyException("参数错误：markStartTime");
			}
			if (!ValidateUtil.isValid(exam.getMarkEndTime())) {
				throw new MyException("参数错误：markEndTime");
			}
			if (exam.getMarkStartTime().getTime() <= exam.getEndTime().getTime()) {
				throw new MyException("阅卷开始时间必须大于考试结束时间");
			}
			if (exam.getMarkStartTime().getTime() >= exam.getMarkEndTime().getTime()) {
				throw new MyException("阅卷结束时间必须大于阅卷开始时间");
			}
		}
		
		Exam entity = getEntity(exam.getId());
		if(entity.getState() == 0) {
			throw new MyException("已删除");
		}
		if(entity.getState() == 1) {
			throw new MyException("已发布");
		}
		if(entity.getState() == 3) {
			throw new MyException("已归档");
		}
		
		// 修改考试
		entity.setName(exam.getName());
		entity.setStartTime(exam.getStartTime());
		entity.setEndTime(exam.getEndTime());
		entity.setMarkStartTime(paper.getMarkType() == 1 ? null : exam.getMarkStartTime());
		entity.setMarkEndTime(paper.getMarkType() == 1 ? null : exam.getMarkEndTime());
		//exam.setMarkState(1);// 不处理
		//entity.setScoreState(exam.getScoreState());
		//entity.setRankState(exam.getRankState());
		//entity.setLoginType(exam.getLoginType());
		//entity.setDescription(exam.getDescription());
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		//entity.setState(null);
		entity.setPaperId(exam.getPaperId());
		//exam.setExamTypeId(null);// 分类不变
		update(entity);
	}
	
	@Override
	public void delAndUpdate(Integer id) {
		// 校验数据有效性（只要有权限就删，不管是否考试中）
		Exam exam = examDao.getEntity(id);
		ExamType examType = examTypeService.getEntity(exam.getExamTypeId());
		if(!examTypeService.hasAuth(examType, getCurUser().getId())) {
			throw new MyException("无操作权限");
		}
		
		// 删除考试
		exam.setState(0);
		exam.setUpdateTime(new Date());
		exam.setUpdateUserId(getCurUser().getId());
		update(exam);
	}
	
	@Override
	public void publish(Integer id) {
		// 校验数据有效性
		Exam exam = examDao.getEntity(id);
		Paper paper = paperService.getEntity(exam.getPaperId());
		publishValid(exam, paper);
		
		// 发布考试
		exam.setState(1);
		exam.setUpdateUserId(getCurUser().getId());
		exam.setUpdateTime(new Date());
		update(exam);
		
		// 发布扩展
		if (paper.getGenType() == 1) {
			examExService.publish(exam);
		} else if (paper.getGenType() == 2) {
			examExService.publishForRand(exam);
		}
		
		// 标记为需要监听的考试（考试结束自动阅卷）
		AutoMarkCache.put(exam.getId(), exam);
	}

	private void publishValid(Exam exam, Paper paper) {
		if(exam.getState() == 0) {
			throw new MyException("考试已删除");
		}
		if(exam.getState() == 1) {
			throw new MyException("考试已发布");
		}
		if(exam.getState() == 3) {
			throw new MyException("考试已归档");
		}
//		if (exam.getStartTime().getTime() <= new Date().getTime()) {
//			throw new MyException("考试开始时间必须大于当前时间");
//		}
		if (exam.getEndTime().getTime() <= new Date().getTime()) {
			throw new MyException("考试结束时间必须大于当前时间");
		}
		if (exam.getStartTime().getTime() >= exam.getEndTime().getTime()) {
			throw new MyException("考试结束时间必须大于考试开始时间");
		}
		PaperType paperType = paperTypeService.getEntity(paper.getPaperTypeId());
		if(paperType.getCreateUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("无操作权限");
		}
		ExamType examType = examTypeService.getEntity(exam.getExamTypeId());
		if(!examTypeService.hasAuth(examType, getCurUser().getId())) {
			throw new MyException("无操作权限");
		}
		if (paper.getMarkType() == 2) {// 如果是自动阅卷类型，没有阅卷开始时间和阅卷结束时间
			if (exam.getMarkStartTime().getTime() <= exam.getEndTime().getTime()) {
				throw new MyException("阅卷开始时间必须大于考试结束时间");
			}
			if (exam.getMarkStartTime().getTime() >= exam.getMarkEndTime().getTime()) {
				throw new MyException("阅卷结束时间必须大于阅卷开始时间");
			}
		}
	}
	
	@Override
	public List<Exam> getList(Integer examTypeId) {
		return examDao.getList(examTypeId);
	}

	@Override
	public List<Exam> getList() {
		return examDao.getList();
	}

	@Override
	public void mail(Exam exam, Integer notifyType, String content) {
		Parm parm = ParmCache.get();
		if (parm == null) {
			throw new MyException("管理员未配置系统参数");
		}
		if (!ValidateUtil.isValid(parm.getEmailUserName())) {
			throw new MyException("管理员未配置邮箱地址");
		}
		
		List<Integer> userList = new ArrayList<Integer>();
		content = content.replace("【考试名称】", exam.getName())
				.replace("【考试开始时间】", DateUtil.formatDateTime(exam.getStartTime()))
				.replace("【考试结束时间】", DateUtil.formatDateTime(exam.getEndTime()));
		if (exam.getMarkStartTime() != null) {
			content = content.replace("【阅卷开始时间】", DateUtil.formatDateTime(exam.getMarkStartTime()))
			.replace("【阅卷结束时间】", DateUtil.formatDateTime(exam.getMarkEndTime()));
		}
		content = StringEscapeUtils.unescapeXml(content);
		if (notifyType == 1) {
			List<MyExam> myExamList = myExamService.getList(exam.getId());// 所有考试人员
			for(MyExam myExam : myExamList){
				userList.add(myExam.getUserId());
			}
		} else if (notifyType == 2) {
			List<MyMark> myMarkList = myMarkService.getList(exam.getId());// 所有阅卷人
			for(MyMark myMark : myMarkList){
				userList.add(myMark.getMarkUserId());
			}
		} else {
			userList.add(getCurUser().getId());// 当前登录用户
		}
		
		String newContent;
		StringBuilder errMsg = new StringBuilder();
		for(Integer userId : userList){
			newContent = content;
			User user = userService.getEntity(userId);
			newContent = newContent.replace("【姓名】", user.getName());
			
			if (!ValidateUtil.isValid(user.getEmail())) {
				errMsg.append(String.format("【%s】未填写邮箱地址", user.getName())).append("<br/>");
				continue;
			}
			
			try {
				notifyService.pushEmail(parm.getEmailUserName(), user.getEmail(), exam.getName(), newContent);
			} catch (MyException e) {
				throw new MyException(e.getMessage());//发件人邮箱错误,所有邮件不能被发出
			} catch (Exception e) {
				errMsg.append(String.format("考试【%s】发送邮件给【%s】失败", exam.getName(), user.getName())).append("<br/>");
			} 
		}
		
		if (errMsg.length() > 0) {
			throw new MyException(errMsg.toString());
		}
	}

	@Override
	public void timeUpdate(Integer id, Integer timeType, Integer minute) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(id)) {
			throw new MyException("参数错误:id");
		}
		if (!(timeType >= 1 && timeType <= 4)) {
			throw new MyException("参数错误:timeType");
		}
		if (!ValidateUtil.isValid(minute)) {
			throw new MyException("参数错误:minute");
		}
		
		Exam exam = getEntity(id);
		Date curTime = new Date();
		if (timeType == 1) {//时间状态：1：考试开始时间；2：考试结束时间；3：阅卷开始时间；4：阅卷结束时间
			if (exam.getStartTime().getTime() < curTime.getTime()) {
				throw new MyException("考试已开始");
			}
			Date newStartTime = DateUtil.getNextMinute(exam.getStartTime(), minute);
			if (exam.getEndTime().getTime() < newStartTime.getTime()) {
				throw new MyException("超过考试结束时间");
			}
			if (exam.getMarkState() != 1) {// 未阅卷就可以
				throw new MyException("已阅卷");
			}
		}
		if (timeType == 2) {// 不限制考试结束时间必须大于考试开始时间，是因为可以设置一个很大的值，可以立马结束考试。不需要算还需要设置几分钟才能提前结束考试。
			if (exam.getEndTime().getTime() < curTime.getTime()) {
				throw new MyException("考试已结束");
			}
			if (ValidateUtil.isValid(exam.getMarkStartTime())) {// 需要人工阅卷
				Date newEndTime = DateUtil.getNextMinute(exam.getEndTime(), minute);
				if (exam.getMarkStartTime().getTime() < newEndTime.getTime()) {
					throw new MyException("超过阅卷开始时间");
				}
			}
			if (exam.getMarkState() != 1) {
				throw new MyException("已阅卷");
			}
		}
		if (timeType == 3) {
			if (!ValidateUtil.isValid(exam.getMarkStartTime())) {
				throw new MyException("无需阅卷");
			}
			if (exam.getMarkStartTime().getTime() < curTime.getTime()) {
				throw new MyException("阅卷已开始");
			}
			Date newMarkStartTime = DateUtil.getNextMinute(exam.getMarkStartTime(), minute);
			if (exam.getMarkEndTime().getTime() < newMarkStartTime.getTime()) {
				throw new MyException("超过阅卷结束时间");
			}
			if (exam.getMarkState() == 3) {// 未阅卷或阅卷中都可以
				throw new MyException("已阅卷");
			}
		}
		if (timeType == 4) {
			if (!ValidateUtil.isValid(exam.getMarkEndTime())) {
				throw new MyException("无需阅卷");
			}
			if (exam.getMarkEndTime().getTime() < curTime.getTime()) {
				throw new MyException("阅卷已结束");
			}
			if (exam.getMarkState() == 3) {
				throw new MyException("已阅卷");
			}
		}
		
		// 变更考试时间
		if (timeType == 1) {
			Date newStartTime = DateUtil.getNextMinute(exam.getStartTime(), minute);
			exam.setStartTime(newStartTime.getTime() < curTime.getTime() ? curTime : newStartTime);
		} else if (timeType == 2) {
			Date newEndTime = DateUtil.getNextMinute(exam.getEndTime(), minute);// 考试时间8点-10点，当前时间9点
			exam.setEndTime(newEndTime.getTime() < curTime.getTime() // 调整时间小于9点
					? curTime // 结束时间改为9点
					: (newEndTime.getTime() < exam.getStartTime().getTime() ? exam.getStartTime() : newEndTime));// 调整时间小于8点，改为8点。否则变更后的时间
		} else if (timeType == 3) {
			Date newMarkStartTime = DateUtil.getNextMinute(exam.getMarkStartTime(), minute);
			exam.setMarkStartTime(newMarkStartTime.getTime() < curTime.getTime()
					? curTime
					: (newMarkStartTime.getTime() < exam.getEndTime().getTime() ? exam.getEndTime() : newMarkStartTime));
		} else if (timeType == 4) {
			Date newMarkEndTime = DateUtil.getNextMinute(exam.getMarkEndTime(), minute);
			exam.setMarkEndTime(newMarkEndTime.getTime() < curTime.getTime()
					? curTime
					: (newMarkEndTime.getTime() < exam.getMarkStartTime().getTime() ? exam.getMarkStartTime() : newMarkEndTime));
		}
		exam.setUpdateTime(curTime);
		exam.setUpdateUserId(getCurUser().getId());
		update(exam);
		
		// 缓存放入新时间
		AutoMarkCache.put(exam.getId(), exam);
	}
	

	@Override
	public List<Map<String, Object>> getExamUserList(Integer id) {
		return examDao.getExamUserList(id);
	}

	@Override
	public List<Map<String, Object>> getExamUserList(Integer id, Integer markUserId) {
		return examDao.getExamUserList(id, markUserId);
	}

	@Override
	public void userAdd(Integer id, String[] examUserIds, Integer[] markUserIds) {
		// 校验数据有效性
		if (ValidateUtil.isValid(markUserIds) && markUserIds.length == 1) {// examUserIds=1,2&markUserIds=1，examUserIds会解析为数组，特殊处理一下
			examUserIds = new String[] { StringUtil.join(examUserIds) };
		}
		Exam exam = getEntity(id);
		Paper paper = paperService.getEntity(exam.getPaperId());
		userAddValid(exam, paper, examUserIds, markUserIds);
		
		// 添加用户
		if (paper.getGenType() == 1) {
			examExService.userAdd(exam, paper, examUserIds, markUserIds);
		} else if (paper.getGenType() == 2) {
			examExService.userAddForRand(exam, paper, examUserIds);
		}
	}

	private void userAddValid(Exam exam, Paper paper, String[] examUserIds, Integer[] markUserIds) {
		if (examUserIds == null) {
			throw new MyException("参数错误：examUserIds");
		}
		if (exam.getState() == 0) {
			throw new MyException("考试已删除");
		}
		//if (exam.getState() == 2) {// 未发布也可以加用户
		//	throw new MyException("考试未发布");
		//}
		if (exam.getState() == 3) {
			throw new MyException("考试已归档");
		}
		if (exam.getEndTime().getTime() < System.currentTimeMillis()) {
			throw new MyException("考试已结束");
		}
		
		//PaperType paperType = paperTypeService.getEntity(paper.getPaperTypeId());
		//if(paperType.getCreateUserId().intValue() != getCurUser().getId().intValue()) {
		//	throw new MyException("无操作权限");// 已发布就不在校验
		//}
		ExamType examType = examTypeService.getEntity(exam.getExamTypeId());
		if(!examTypeService.hasAuth(examType, getCurUser().getId())) {
			throw new MyException("无操作权限");
		}
		
		if (paper.getMarkType() == 2) {// 如果主观题试卷，有阅卷用户
			if (markUserIds == null) {
				throw new MyException("参数错误：markUserIds");
			}
			
			if (examUserIds.length != markUserIds.length) {
				throw new MyException("参数错误：markUserIds和examUserIds数量不等");
			} 
		}
		
		Set<Integer> examUserIdSet = new HashSet<>();
		for (String userIds : examUserIds) {
			for (String userId : userIds.split(",")) {
				int _userId = Integer.parseInt(userId);
				if (examUserIdSet.contains(_userId)) {
					throw new MyException("考试用户重复");
				}
			}
		}
		Set<Integer> markUserIdSet = new HashSet<>();
		for (Integer userId : markUserIdSet) {
			if (markUserIdSet.contains(userId)) {
				throw new MyException("阅卷用户重复");
			}
		}
	}
}
