package com.wcpdoc.exam.core.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.wcpdoc.core.util.BigDecimalUtil;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.cache.AutoMarkCache;
import com.wcpdoc.exam.core.dao.ExamDao;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.ExamQuestion;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyMark;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.entity.ex.Chapter;
import com.wcpdoc.exam.core.entity.ex.ExamAnswerEx;
import com.wcpdoc.exam.core.entity.ex.MyExamChapter;
import com.wcpdoc.exam.core.entity.ex.MyQuestion;
import com.wcpdoc.exam.core.service.ExamExService;
import com.wcpdoc.exam.core.service.ExamQuestionService;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyMarkService;
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
	private QuestionService questionService;
	@Resource
	private UserService userService;
	@Resource
	private ExamQuestionService examQuestionService;
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
	public void addAndUpdate(Exam exam) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(exam.getName())) {
			throw new MyException("参数错误：name");
		}
		if (exam.getTimeType() == 1 && !ValidateUtil.isValid(exam.getStartTime())) {
			throw new MyException("参数错误：startTime");
		}
		if (exam.getTimeType() == 1 && !ValidateUtil.isValid(exam.getEndTime())) {
			throw new MyException("参数错误：endTime");
		}
		// if (exam.getStartTime().getTime() <= new Date().getTime()) {
		// 	throw new MyException("考试开始时间必须大于当前时间");// 页面上选的当前时间，添加时就过期了，体验差
		// }
		if (exam.getTimeType() == 1 && exam.getEndTime().getTime() <= new Date().getTime()) {
			throw new MyException("考试结束时间必须大于当前时间");
		}
		if (exam.getTimeType() == 1 && exam.getStartTime().getTime() >= exam.getEndTime().getTime()) {
			throw new MyException("考试结束时间必须大于考试开始时间");
		}
		if (exam.getState() != 1) {
			throw new MyException("试卷未发布");
		}
		
		if (exam.getTimeType() == 1 && exam.getMarkType() == 2) {// 如果是自动阅卷类型，没有阅卷开始时间和阅卷结束时间
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
		exam.setUpdateUserId(getCurUser().getId());
		exam.setUpdateTime(new Date());
		exam.setState(2);// 草稿
		exam.setMarkState(1);// 标记为未阅卷（考试时间结束，定时任务自动阅卷，标记为已阅）
		exam.setMarkStartTime(exam.getMarkType() == 1 ? null : exam.getMarkStartTime());
		exam.setMarkEndTime(exam.getMarkType() == 1 ? null : exam.getMarkEndTime());
		exam.setScoreState(2);
		exam.setRankState(2);
		exam.setAnonState(2);
		add(exam);
	}
	
	@Override
	public void updateAndUpdate(Exam exam) {
		//校验数据有效性
		if (!ValidateUtil.isValid(exam.getName())) {
			throw new MyException("参数错误：name");
		}
		if (exam.getTimeType() == 1 && !ValidateUtil.isValid(exam.getStartTime())) {
			throw new MyException("参数错误：startTime");
		}
		if (exam.getTimeType() == 1 && !ValidateUtil.isValid(exam.getEndTime())) {
			throw new MyException("参数错误：endTime");
		}
//		if (exam.getStartTime().getTime() <= new Date().getTime()) {
//			throw new MyException("考试开始时间必须大于当前时间");
//		}
		if (exam.getTimeType() == 1 && exam.getEndTime().getTime() <= new Date().getTime()) {
			throw new MyException("考试结束时间必须大于当前时间");
		}
		if (exam.getTimeType() == 1 && exam.getStartTime().getTime() >= exam.getEndTime().getTime()) {
			throw new MyException("考试结束时间必须大于考试开始时间");
		}

		if (exam.getState() != 1) {
			throw new MyException("试卷未发布");
		}
		if (exam.getTimeType() == 1 && exam.getMarkType() == 2) {// 如果是自动阅卷类型，没有阅卷开始时间和阅卷结束时间
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
		entity.setMarkStartTime(exam.getMarkType() == 1 ? null : exam.getMarkStartTime());
		entity.setMarkEndTime(exam.getMarkType() == 1 ? null : exam.getMarkEndTime());
		//exam.setMarkState(1);// 不处理
		//entity.setScoreState(exam.getScoreState());
		//entity.setRankState(exam.getRankState());
		//entity.setLoginType(exam.getLoginType());
		//entity.setDescription(exam.getDescription());
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		//entity.setState(null);
		//exam.setExamTypeId(null);// 分类不变
		update(entity);
	}
	
	@Override
	public void delAndUpdate(Integer id) {
		// 校验数据有效性（只要有权限就删，不管是否考试中，如创建了一个超长的结束时间）
		Exam exam = getEntity(id);
		
		// 删除考试
		exam.setState(0);
		exam.setUpdateTime(new Date());
		exam.setUpdateUserId(getCurUser().getId());
		update(exam);
	}
	
	@Override
	public void publish(Integer id) {
		// 校验数据有效性
		Exam exam = getEntity(id);
		publishValid(exam);
		
		// 发布考试
		exam.setState(1);
		exam.setUpdateUserId(getCurUser().getId());
		exam.setUpdateTime(new Date());
		update(exam);
		
		// 发布扩展
		if (exam.getGenType() == 1) {
			examExService.publish(exam);
		} else if (exam.getGenType() == 2) {
			examExService.publishOfRand(exam);
		}
		
		// 标记为需要监听的考试（考试结束自动阅卷）
		AutoMarkCache.put(exam.getId(), exam);
	}

	private void publishValid(Exam exam) {
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
		if (exam.getTimeType() == 1 && exam.getEndTime().getTime() <= new Date().getTime()) {
			throw new MyException("考试结束时间必须大于当前时间");
		}
		if (exam.getTimeType() == 1 && exam.getStartTime().getTime() >= exam.getEndTime().getTime()) {
			throw new MyException("考试结束时间必须大于考试开始时间");
		}
		if (exam.getTimeType() == 1 && exam.getMarkType() == 2) {// 如果是自动阅卷类型，没有阅卷开始时间和阅卷结束时间
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
		content = content.replace("【考试名称】", exam.getName());
		
		if (exam.getTimeType() == 1) { //限时
			content.replace("【考试开始时间】", DateUtil.formatDateTime(exam.getStartTime()))
				   .replace("【考试结束时间】", DateUtil.formatDateTime(exam.getEndTime()));
		
			if (exam.getMarkStartTime() != null) {
				content = content.replace("【阅卷开始时间】", DateUtil.formatDateTime(exam.getMarkStartTime()))
				.replace("【阅卷结束时间】", DateUtil.formatDateTime(exam.getMarkEndTime()));
			}
		}
		if (exam.getTimeType() == 2) { //不限时
			content.replace("【考试开始时间】", "不限时间")
			   .replace("【考试结束时间】", "不限时间");
	
			if (exam.getMarkStartTime() != null) {
				content = content.replace("【阅卷开始时间】", "不限时间")
				.replace("【阅卷结束时间】", "不限时间");
			}
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
		userAddValid(exam, examUserIds, markUserIds);
		
		// 添加用户
		if (exam.getGenType() == 1) {
			examExService.userAdd(exam, examUserIds, markUserIds);
		} else if (exam.getGenType() == 2) {
			examExService.userAddOfRand(exam, examUserIds);
		}
	}

	private void userAddValid(Exam exam, String[] examUserIds, Integer[] markUserIds) {
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
		
		if (exam.getMarkType() == 2) {// 如果主观题试卷，有阅卷用户
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
	
	@Override
	public MyExamChapter getExamChapter(Integer id) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(id)) {
			throw new MyException("参数错误：id");
		}
		Exam exam = getEntity(id);
		if (exam.getGenType() != 1) {
			throw new MyException("参数错误：id");
		}
		
		// 组合成需要的格式
		List<ExamQuestion> chapterList = examQuestionService.getChapterList(id);
		Map<Integer, Question> questionCache = getQuestionCache(id);
		Map<Integer, List<QuestionOption>> questionOptionCache = getQuestionOptionCache(id);
		Map<Integer, List<ExamAnswerEx>> examAnswerCache = getExamAnswerExCache(id);
		
		return generatePaper(exam, chapterList, questionCache, questionOptionCache, examAnswerCache);
	}
	
	private Map<Integer, Question> getQuestionCache(Integer id) {
		List<Question> questionList = examDao.getQuestionList(id);
		Map<Integer, Question> questionCache = new HashMap<>();
		for (Question question : questionList) {
			questionCache.put(question.getId(), question);
		}
		return questionCache;
	}
	
	private Map<Integer, List<QuestionOption>> getQuestionOptionCache(Integer id) {
		List<QuestionOption> questionOptionList = examDao.getQuestionOptionList(id);
		Map<Integer, List<QuestionOption>> questionOptionCache = new HashMap<>();
		for (QuestionOption questionOption : questionOptionList) {
			if (questionOptionCache.get(questionOption.getQuestionId()) == null) {
				questionOptionCache.put(questionOption.getQuestionId(), new ArrayList<>());
			}
			
			questionOptionCache.get(questionOption.getQuestionId()).add(questionOption);
		}
		
		return questionOptionCache;
	}
	
	private Map<Integer, List<ExamAnswerEx>> getExamAnswerExCache(Integer id) {//考试试题答案   分数来自考试试题  答案来自试题答案
		List<Map<String, Object>> examAnswerList = examDao.getExamAnswerList(id);
		Map<Integer, List<ExamAnswerEx>> examAnswerCache = new HashMap<>();
		for (Map<String, Object> examAnswerMap : examAnswerList) {
			int questionId = Integer.parseInt(examAnswerMap.get("questionId").toString());
			if (examAnswerCache.get(questionId) == null) {
				examAnswerCache.put(questionId, new ArrayList<>());
			}
			
			String[] split = examAnswerMap.get("scores").toString().split(",");
			ExamAnswerEx examAnswerEx = new ExamAnswerEx();
			examAnswerEx.setAnswer(examAnswerMap.get("answer").toString());
			examAnswerEx.setScore(new BigDecimal(split[Integer.parseInt(examAnswerMap.get("no").toString()) - 1]));
			examAnswerCache.get(questionId).add(examAnswerEx);
		}
		
		return examAnswerCache;
	}
	
	private MyExamChapter generatePaper(Exam exam, List<ExamQuestion> chapterList, Map<Integer, Question> questionCache,
			Map<Integer, List<QuestionOption>> questionOptionCache,
			Map<Integer, List<ExamAnswerEx>> examAnswerCache) {
		MyExamChapter myExamChapter = new MyExamChapter();
		for (ExamQuestion chapter : chapterList) {
			Chapter _chapter = new Chapter(chapter);
			myExamChapter.getChapterList().add(_chapter);
			List<ExamQuestion> chapterDetailList = examQuestionService.getChapterDetailList(exam.getId(), chapter.getNo());
			for (ExamQuestion questionAttr : chapterDetailList) {
				if (questionAttr.getType() == 1) {//从章节到下一个章节之间
					break;
				}
				
				MyQuestion myQuestion = new MyQuestion(
						questionCache.get(questionAttr.getQuestionId()), 
						questionOptionCache.get(questionAttr.getQuestionId()),
						examAnswerCache.get(questionAttr.getQuestionId()),
						questionAttr );
				_chapter.getMyQuestionList().add(myQuestion);
			}
		}
		return myExamChapter;
	}
	
	@Override
	public MyExamChapter getPaperOfRand(Integer examId, Integer userId) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(examId)) {
			throw new MyException("参数错误：id");
		}
		Exam exam = examDao.getEntity(examId);
		if (exam.getGenType() != 2) {
			throw new MyException("参数错误：examId");
		}
		
		// 组合成需要的格式
		List<ExamQuestion> chapterList = examQuestionService.getChapterList(exam.getId());
		Map<Integer, Question> questionCache = getQuestionCache(examId, userId);
		Map<Integer, List<QuestionOption>> questionOptionCache = getQuestionOptionCache(examId, userId);
		Map<Integer, List<ExamAnswerEx>> examAnswerCache = getExamAnswerExCache(examId, userId);
		return generatePaperOfRand(exam, userId, chapterList, questionCache, questionOptionCache, examAnswerCache);
	}
	
	private Map<Integer, Question> getQuestionCache(Integer examId, Integer userId) {
		List<Question> questionList = examDao.getQuestionList(examId, userId);
		Map<Integer, Question> questionCache = new HashMap<>();
		for (Question question : questionList) {
			questionCache.put(question.getId(), question);
		}
		return questionCache;
	}
	
	private Map<Integer, List<QuestionOption>> getQuestionOptionCache(Integer examId, Integer userId) {
		List<QuestionOption> questionOptionList = examDao.getQuestionOptionList(examId, userId);
		Map<Integer, List<QuestionOption>> questionOptionCache = new HashMap<>();
		for (QuestionOption questionOption : questionOptionList) {
			if (questionOptionCache.get(questionOption.getQuestionId()) == null) {
				questionOptionCache.put(questionOption.getQuestionId(), new ArrayList<>());
			}
			
			questionOptionCache.get(questionOption.getQuestionId()).add(questionOption);
		}
		
		return questionOptionCache;
	}
	
	private Map<Integer, List<ExamAnswerEx>> getExamAnswerExCache(Integer examId, Integer userId) {
		List<Map<String, Object>> examAnswerList = examDao.getExamAnswerList(examId, userId);
		Map<Integer, List<ExamAnswerEx>> examAnswerCache = new HashMap<>();
		for (Map<String, Object> examAnswerMap : examAnswerList) {
			int questionId = Integer.parseInt(examAnswerMap.get("questionId").toString());
			if (examAnswerCache.get(questionId) == null) {
				examAnswerCache.put(questionId, new ArrayList<>());
			}
			
			String[] split = examAnswerMap.get("scores").toString().split(",");
			ExamAnswerEx examAnswerEx = new ExamAnswerEx();
			examAnswerEx.setAnswer(examAnswerMap.get("answer").toString());
			examAnswerEx.setScore(new BigDecimal(split[Integer.parseInt(examAnswerMap.get("no").toString()) - 1]));
			examAnswerCache.get(questionId).add(examAnswerEx);
		}
		
		return examAnswerCache;
	}
	
	private MyExamChapter generatePaperOfRand(Exam exam, Integer userId, List<ExamQuestion> chapterList,
			Map<Integer, Question> questionCache, Map<Integer, List<QuestionOption>> questionOptionCache,
			Map<Integer, List<ExamAnswerEx>> examAnswerCache) {
		MyExamChapter myExamChapter = new MyExamChapter();
		List<ExamQuestion> chapterDetailList = examQuestionService.getList(exam.getId(), userId);// 这里找到是所有的，包含章节
		for (ExamQuestion chapter : chapterList) {
			Chapter _chapter = new Chapter(chapter);
			myExamChapter.getChapterList().add(_chapter);
			for (ExamQuestion questionAttr : chapterDetailList) {
				if (questionAttr.getType().intValue() == 1) {// 过滤掉非当前章节的
					continue;
				}
				MyQuestion myQuestion = new MyQuestion(
						questionCache.get(questionAttr.getQuestionId()), 
						questionOptionCache.get(questionAttr.getQuestionId()),
						examAnswerCache.get(questionAttr.getQuestionId()),
						questionAttr );
				_chapter.getMyQuestionList().add(myQuestion);
			}
		}
		return myExamChapter;
	}
	
	@Override
	public List<Question> getQuestionList(Integer id) {
		return examDao.getQuestionList(id);
	}
	
	@Override
	public void scoreUpdate(Integer id, Integer questionId, BigDecimal score, BigDecimal[] subScores, Integer[] markOptions) {
		// 校验数据有效性
		if (id == null) {
			throw new MyException("参数错误：id");
		}
		if (score == null) {
			throw new MyException("参数错误：score");
		}
		Exam exam = getEntity(id);
		if (exam.getState() == 0) {
			throw new MyException("试卷已删除");
		}
		if (exam.getState() == 1) {
			throw new MyException("试卷已发布");
		}
		if (exam.getState() == 3) {
			throw new MyException("试卷已归档");
		}

		//设置分数
		String markOptionsTemp = ValidateUtil.isValid(markOptions) ? StringUtil.join(markOptions) : "";
		Question question = questionService.getEntity(questionId);
		ExamQuestion eq = examQuestionService.getEntity(id, questionId);
		if (question.getType() == 2 && markOptionsTemp.contains("1")) {//智能选项（1：漏选得分；2：答案无顺序；3：大小写不敏感；)
			// pq.setMarkOptions("1");// 默认就是不需要设置
		} else if (question.getType() == 3 && question.getMarkType() == 1 ) {
			if (markOptionsTemp.contains("2") && markOptionsTemp.contains("3")) {
				eq.setMarkOptions("2,3");
			} else if (markOptionsTemp.contains("2")) {
				eq.setMarkOptions("2");
			} else if (markOptionsTemp.contains("2")) {
				eq.setMarkOptions("3");
			}
		} else if (question.getType() == 5 && question.getMarkType() == 1) {
			if (markOptionsTemp.contains("3")) {
				eq.setMarkOptions("3");
			}
		}
		eq.setScore(score);
		
		//设置子分数
		if (question.getType() == 1 || question.getType() == 4) { //单选 判断
				eq.setScores("0");
		} else if (question.getType() == 2) {// 多选
			if (markOptionsTemp.contains("1")) {
				eq.setScores(subScores[0].toString());
			} else {
				eq.setScores(BigDecimalUtil.newInstance(score).div(2, 2).getResult().toString());
			}
		} else if ((question.getType() == 3 && question.getMarkType() == 1) 
				|| (question.getType() == 5 && question.getMarkType() == 1)) { // 智能填空 智能问答
			BigDecimal scoreSum = new BigDecimal(0);
			String scores = "0";
			for (int i = 0; i < subScores.length; i++) {
				scoreSum = scoreSum.add(subScores[i]);
				if (i == 0) {
					scores = subScores[i].toString();
				}
				scores = String.format("%s,%s", scores, subScores[i]);
				
			}
			if (scoreSum.compareTo(score) != 0) {
				throw new MyException("分值错误");
			}
			eq.setScores(scores);
		}
		
		examQuestionService.update(eq);
	}
	
	@Override
	public void batchScoreUpdate(Integer id, BigDecimal score, BigDecimal subScores, Integer[] markOptions) {
		// 校验数据有效性
		if (id == null) {
			throw new MyException("参数错误：id");
		}
		if (score == null) {
			throw new MyException("参数错误：score");
		}
		ExamQuestion entity = examQuestionService.getEntity(id);//章节
		Exam exam = getEntity(entity.getExamId());
		if (exam.getState() == 0) {
			throw new MyException("试卷已删除");
		}
		if (exam.getState() == 1) {
			throw new MyException("试卷已发布");
		}
		if (exam.getState() == 3) {
			throw new MyException("试卷已归档");
		}

		// 更新试卷分数
		String markOptionsTemp = ValidateUtil.isValid(markOptions) ? StringUtil.join(markOptions) : "";
		List<ExamQuestion> chapterDetailList = examQuestionService.getChapterDetailList(exam.getId(), entity.getNo());
		for(ExamQuestion chapterDetai : chapterDetailList){
			if (chapterDetai.getType() == 1) {//从章节到下一个章节
				break;
			}
			
			Question question = questionService.getEntity(chapterDetai.getQuestionId());
			if (question.getType() == 2 && markOptionsTemp.contains("1")) {//智能选项（1：漏选得分；2：答案无顺序；3：大小写不敏感；)
				// pq.setMarkOptions("1");// 默认就是不需要设置
			} else if (question.getType() == 3 && question.getMarkType() == 1 ) {
				if (markOptionsTemp.contains("2") && markOptionsTemp.contains("3")) {
					chapterDetai.setMarkOptions("2,3");
				} else if (markOptionsTemp.contains("2")) {
					chapterDetai.setMarkOptions("2");
				} else if (markOptionsTemp.contains("2")) {
					chapterDetai.setMarkOptions("3");
				}
			} else if (question.getType() == 5 && question.getMarkType() == 1) {
				if (markOptionsTemp.contains("3")) {
					chapterDetai.setMarkOptions("3");
				}
			}
			chapterDetai.setScore(score);
			
			//设置子分数
			if (question.getType() == 1 || question.getType() == 4) { //单选 判断
				chapterDetai.setScores("0");
			} else if (question.getType() == 2) {// 多选
				if (markOptionsTemp.contains("1")) {
					chapterDetai.setScores(subScores.toString());
				} else {
					chapterDetai.setScores(BigDecimalUtil.newInstance(score).div(2, 2).getResult().toString());
				}
			} else if ((question.getType() == 3 && question.getMarkType() == 1) 
					|| (question.getType() == 5 && question.getMarkType() == 1)) { // 智能填空 智能问答
				String[] answerScores = chapterDetai.getScores().split(",");
				
				String scores = "0";
				BigDecimal singleScore = BigDecimalUtil.newInstance(score).div(new BigDecimal(answerScores.length), 2).getResult();
				for (int i = 0; i < answerScores.length - 1 ; i++) {
					if (i == 0) {
						scores = singleScore.toString();
					}
					scores = String.format("%s,%s", scores, singleScore);
				}
				BigDecimal result = BigDecimalUtil.newInstance(singleScore).mul(answerScores.length - 1).sub(score).mul(-1).getResult();
				if (answerScores.length > 1) {
					scores = String.format("%s,%s", scores, result);
				} else if (answerScores.length == 1) {
					scores = result.toString();
				}
				
				chapterDetai.setScores(scores);
			}
			
			examQuestionService.update(chapterDetai);
		}	
	}
	
	@Override
	public void sxe(Integer id, Integer[] options) {
		// 校验数据有效性
		Exam exam = getEntity(id);
		if(exam.getState() == 0) {
			throw new MyException("已删除");
		}
		if (exam.getState() == 1) {
			throw new MyException("已发布");
		}
		if(exam.getState() == 3){
			throw new MyException("已归档");
		}
		if (exam.getGenType() == 2) {
			throw new MyException("随机考试无效");
		}
		
		// 更新反作弊
		exam.setSxes(ValidateUtil.isValid(options) ? StringUtil.join(options) : null);
	}
}
