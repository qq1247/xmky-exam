package com.wcpdoc.exam.core.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.wcpdoc.base.cache.ProgressBarCache;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.base.util.CurLoginUserUtil;
import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.BigDecimalUtil;
import com.wcpdoc.core.util.CollectionUtil;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.cache.AutoMarkCache;
import com.wcpdoc.exam.core.cache.QuestionCache;
import com.wcpdoc.exam.core.dao.ExamDao;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.ExamQuestion;
import com.wcpdoc.exam.core.entity.ExamRule;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyQuestion;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.entity.ex.ExamInfo;
import com.wcpdoc.exam.core.entity.ex.ExamQuestionEx;
import com.wcpdoc.exam.core.service.ExamQuestionService;
import com.wcpdoc.exam.core.service.ExamRuleService;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyMarkService;
import com.wcpdoc.exam.core.service.MyQuestionService;
import com.wcpdoc.exam.core.service.QuestionAnswerService;
import com.wcpdoc.exam.core.service.QuestionOptionService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionTypeService;
import com.wcpdoc.exam.core.util.ExamUtil;
import com.wcpdoc.exam.core.util.QuestionUtil;
import com.wcpdoc.notify.service.NotifyService;

/**
 * 考试服务层实现
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
@Service
public class ExamServiceImpl extends BaseServiceImp<Exam> implements ExamService {
	private static final Logger log = LoggerFactory.getLogger(ExamServiceImpl.class);
	
	@Resource
	private ExamDao examDao;
	@Resource
	private QuestionService questionService;
	@Resource
	private QuestionTypeService questionTypeService;
	@Resource
	private UserService userService;
	@Resource
	private ExamQuestionService examQuestionService;
	@Resource
	private NotifyService notifyService;
	@Resource
	private MyMarkService myMarkService;
	@Resource
	private MyExamService myExamService;
	@Resource
	private MyQuestionService myQuestionService;
	@Resource
	private QuestionAnswerService questionAnswerService;
	@Resource
	private ExamRuleService examRuleService;
	@Resource
	private QuestionOptionService questionOptionService;
	
	@Override
	@Resource(name = "examDaoImpl")
	public void setDao(BaseDao<Exam> dao) {
		super.dao = dao;
	}

	/**
	 * 数据格式：
	 {
		"id": 1,
		"name": "考试-20221020",
		"paperName": "点击这里输入试卷名称",
		"startTime": "2022-10-20 08:00:00",
		"endTime": "2022-10-20 10:00:00",
		"markStartTime": "2022-10-20 14:00:00",
		"markEndTime": "2022-10-20 18:00:00"
		"genType": 1,
		"passScore": 14,
		"sxes": [1, 2],
		"showType": 1,
		"anonState": 1,
		"scoreState": 2,
		"rankState": 2,
		"state": 1,
		"markType": 2,
		"totalScore": 24,
		"examQuestions": [{ // 固定组卷使用
			"type": 2,// 类型 （1：章节；2：试题）
			"chapterName": "单选题",// type==2有效
			"chapterTxt": "每题2分，一共10题20分",// type==2有效
			"questionId": 1,
			"questionType": 1,
			"markType": 1,
			"title": "这是一道单选题的题干，简单写法",
			"options": ["单选题的A选项", "单选题的B选项", "单选题的C选项", "单选题的D选项"],
			"markOptions": [],
			"score": 2,
			"answers": ["B"],
			"scores": [],
			"analysis": "",
		}],
		"examRules": [
			"type": 2,
		    "chapterName": "单选题",// type==2有效
			"chapterTxt": "每题2分，一共10题20分",// type==2有效
		    "questionTypeId": 1,
		    "questionType": 2,
		    "markType": 1,
		    "markOptions": [],
		    "num": 10,
		    "score": 1,
		    "scores": [],
		]
		"examUserIds": [2, 3],
		"markUserId": 2,
	}
	 */
	@Override
	public void publish(ExamInfo examInfo, String processBarId) {
		/**
		 * 数据处理
		 * 1：如果是人工组卷，且从题库抽题，试题类型等重新查一遍数据库，不要依赖前端，用于检测包含主观题等
		 * 2：如果是随机组卷，提前返回需要的题库，用于随机抽题
		 */
		Double processLen = (examInfo.getExamUserIds().length + 5) * 1.0;// 说明参考控制层
		ProgressBarCache.setProgressBar(processBarId, 0.0, processLen, "校验前数据处理开始", HttpStatus.OK.value());
		Map<Integer, List<Question>> questionListCache = publishHandle(examInfo);
		ProgressBarCache.setProgressBar(processBarId, 1.0, processLen, "校验前数据处理完成", HttpStatus.OK.value());
		
		// 数据校验
		publishValid(examInfo, questionListCache);
		ProgressBarCache.setProgressBar(processBarId, 2.0, processLen, "校验数据完成", HttpStatus.OK.value());

		// 保存考试信息
		Exam exam = publishExam(examInfo);
		ProgressBarCache.setProgressBar(processBarId, 3.0, processLen, "生成考试完成", HttpStatus.OK.value());
		
		// 保存试卷信息
		publishPaper(examInfo);
		ProgressBarCache.setProgressBar(processBarId, 4.0, processLen, "生成试卷完成", HttpStatus.OK.value());
		
		// 分配试卷到考试用户
		publishUser(examInfo, questionListCache, processBarId);
		
		// 标记为需要监听的考试（考试结束自动阅卷）
		AutoMarkCache.put(exam.getId(), exam);
		log.info("考试核心加入：【{}-{}】加入监听，{}开始自动阅卷", 
				exam.getId(), 
				exam.getName(), // 未阅卷 取 考试结束时间；阅卷中 取 阅卷结束时间
				exam.getMarkState() == 1 ? DateUtil.formatDateTime(exam.getEndTime()) : DateUtil.formatDateTime(exam.getMarkEndTime()));
	}
	
	@Override
	public void delEx(Integer id) {
		// 校验数据有效性（只要有权限就删，不管是否考试中，如创建了一个超长的结束时间）
		Exam exam = getEntity(id);
		if (!(CurLoginUserUtil.isSelf(exam.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {
			throw new MyException("无操作权限");
		}
		
		// 删除考试
		exam.setState(0);
		exam.setUpdateTime(new Date());
		exam.setUpdateUserId(getCurUser().getId());
		update(exam);
		
		// 清除标记为需要监听的考试
		AutoMarkCache.del(exam.getId());
		log.info("考试核心清除：【{}-{}】清除监听", exam.getId(), exam.getName());
	}

	@Override
	public List<Exam> getList() {
		return examDao.getList();
	}

	@Override
	public void mail(Exam exam, Integer notifyType, String content) {
//		Parm parm = ParmCache.get();
//		if (parm == null) {
//			throw new MyException("管理员未配置系统参数");
//		}
//		if (!ValidateUtil.isValid(parm.getEmailUserName())) {
//			throw new MyException("管理员未配置邮箱地址");
//		}
//		
//		List<Integer> userList = new ArrayList<Integer>();
//		content = content.replace("【考试名称】", exam.getName());
//		
//		if (exam.getTimeType() == 1) { //限时
//			content.replace("【考试开始时间】", DateUtil.formatDateTime(exam.getStartTime()))
//				   .replace("【考试结束时间】", DateUtil.formatDateTime(exam.getEndTime()));
//		
//			if (exam.getMarkStartTime() != null) {
//				content = content.replace("【阅卷开始时间】", DateUtil.formatDateTime(exam.getMarkStartTime()))
//				.replace("【阅卷结束时间】", DateUtil.formatDateTime(exam.getMarkEndTime()));
//			}
//		}
//		if (exam.getTimeType() == 2) { //不限时
//			content.replace("【考试开始时间】", "不限时间")
//			   .replace("【考试结束时间】", "不限时间");
//	
//			if (exam.getMarkStartTime() != null) {
//				content = content.replace("【阅卷开始时间】", "不限时间")
//				.replace("【阅卷结束时间】", "不限时间");
//			}
//		}
//		
//		content = StringEscapeUtils.unescapeXml(content);
//		if (notifyType == 1) {
//			List<MyExam> myExamList = myExamService.getList(exam.getId());// 所有考试人员
//			for(MyExam myExam : myExamList){
//				userList.add(myExam.getUserId());
//			}
//		} else if (notifyType == 2) {
//			List<MyMark> myMarkList = myMarkService.getList(exam.getId());// 所有阅卷人
//			for(MyMark myMark : myMarkList){
//				userList.add(myMark.getMarkUserId());
//			}
//		} else {
//			userList.add(getCurUser().getId());// 当前登录用户
//		}
//		
//		String newContent;
//		StringBuilder errMsg = new StringBuilder();
//		for(Integer userId : userList){
//			newContent = content;
//			User user = userService.getEntity(userId);
//			newContent = newContent.replace("【姓名】", user.getName());
//			
//			if (!ValidateUtil.isValid(user.getEmail())) {
//				errMsg.append(String.format("【%s】未填写邮箱地址", user.getName())).append("<br/>");
//				continue;
//			}
//			
//			try {
//				notifyService.pushEmail(parm.getEmailUserName(), user.getEmail(), exam.getName(), newContent);
//			} catch (MyException e) {
//				throw new MyException(e.getMessage());//发件人邮箱错误,所有邮件不能被发出
//			} catch (Exception e) {
//				errMsg.append(String.format("考试【%s】发送邮件给【%s】失败", exam.getName(), user.getName())).append("<br/>");
//			} 
//		}
//		
//		if (errMsg.length() > 0) {
//			throw new MyException(errMsg.toString());
//		}
	}

	@Override
	public void timeUpdate(Integer id, Integer timeType, Integer minute) {
		// 数据校验
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
		if (!(CurLoginUserUtil.isSelf(exam.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {
			throw new MyException("无操作权限");
		}
		
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
					: (newEndTime.getTime() < exam.getStartTime().getTime() ? exam.getStartTime() : newEndTime));// 调整时间小于8点，改为8点（考试开始时间）。否则变更后的时间
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
		log.info("考试核心更新：【{}-{}】更新监听，{}开始自动阅卷", 
				exam.getId(), 
				exam.getName(), // 未阅卷 取 考试结束时间；阅卷中 取 阅卷结束时间
				exam.getMarkState() == 1 ? DateUtil.formatDateTime(exam.getEndTime()) : DateUtil.formatDateTime(exam.getMarkEndTime()));
	}
	
	@Override
	public List<Map<String, Object>> getExamUserList(Integer id) {
		return examDao.getExamUserList(id);
	}

	@Override
	public List<Map<String, Object>> getExamUserList(Integer id, Integer markUserId) {
		return examDao.getExamUserList(id, markUserId);
	}
	
	private Exam publishExam(ExamInfo examInfo) {
		Exam exam = new Exam();
		if (ValidateUtil.isValid(examInfo.getId())) {
			exam = getEntity(examInfo.getId());
		}
		exam.setName(examInfo.getName());
		exam.setPaperName(examInfo.getPaperName());
		exam.setStartTime(examInfo.getStartTime());
		exam.setEndTime(examInfo.getEndTime());
		exam.setMarkStartTime(examInfo.getMarkStartTime());
		exam.setMarkEndTime(examInfo.getMarkEndTime());
		exam.setMarkType(examInfo.getMarkType());
		exam.setScoreState(examInfo.getScoreState());
		exam.setRankState(examInfo.getRankState());
		exam.setAnonState(examInfo.getAnonState());
		exam.setPassScore(examInfo.getPassScore());
		exam.setTotalScore(examInfo.getTotalScore());
		exam.setMarkType(examInfo.getMarkType());
		exam.setShowType(examInfo.getShowType());
		exam.setGenType(examInfo.getGenType());
		exam.setSxes(examInfo.getSxes());
		exam.setState(examInfo.getState());
		exam.setMarkState(1);// 标记为未阅卷
		exam.setUpdateUserId(getCurUser().getId());
		exam.setUpdateTime(new Date());
		
		if (!ValidateUtil.isValid(examInfo.getId())) {
			exam.setCreateUserId(getCurUser().getId());
			add(exam);
			examInfo.setId(exam.getId());// 同步exam.id到examInfo.id
		} else {
			update(exam);
		}
		return exam;
	}
	
	private void publishPaper(ExamInfo examInfo) {
		// 删除旧试卷信息（二次修改会有；试题和规则都删除一次，页面可能会切换固定试卷和随机试卷）
		examQuestionService.clear(examInfo.getId());
//		List<ExamQuestion> examQuestionList = examQuestionService.getList(examInfo.getId());
//		for (ExamQuestion examQuestion : examQuestionList) {
//			examQuestionService.del(examQuestion.getId());
//		}
		examRuleService.clear(examInfo.getId());
//		List<ExamRule> examRuleList = examRuleService.getList(examInfo.getId());
//		for (ExamRule examRule : examRuleList) {
//			examRuleService.del(examRule.getId());
//		}
		
		// 保存新试卷信息
		if (examInfo.getGenType() == 1) {
			QuestionType questionType = null;// 创建一个题库，题库名称保持和考试名称一致，用于存放部分从文本导入的新题
			for (int i = 0; i < examInfo.getExamQuestions().length; i++) {
				ExamQuestionEx examQuestionEx = examInfo.getExamQuestions()[i];
				examQuestionEx.setNo(i + 1);// 重新保存顺序，不要依赖前端
				examQuestionEx.setExamId(examInfo.getId());
				if (ExamUtil.hasQuestion(examQuestionEx)) {
					if (!ValidateUtil.isValid(examQuestionEx.getQuestionId())) {// 如果是从文本导入的新题，先保存
						if (questionType == null) {
							questionType = new QuestionType();
							questionType.setName(examInfo.getName());
							questionType.setCreateUserId(getCurUser().getId());
							questionType.setUpdateTime(new Date());
							questionType.setUpdateUserId(getCurUser().getId());
							questionTypeService.add(questionType);
						}
						
						Question question = new Question();
						question.setType(examQuestionEx.getQuestionType());
						question.setTitle(examQuestionEx.getTitle());
						question.setMarkType(examQuestionEx.getMarkType());
						question.setMarkOptions(examQuestionEx.getMarkOptions());
						question.setAnalysis(examQuestionEx.getAnalysis());
						question.setScore(examQuestionEx.getScore());
						question.setQuestionTypeId(questionType.getId());
						question.setUpdateUserId(getCurUser().getId());
						question.setUpdateTime(new Date());
						questionService.addEx(question, examQuestionEx.getOptions(), examQuestionEx.getAnswers(), examQuestionEx.getScores());
						examQuestionEx.setQuestionId(question.getId());
					}
				}
				
				ExamQuestion examQuestion = new ExamQuestion();
				examQuestion.setChapterName(examQuestionEx.getChapterName());
				examQuestion.setChapterTxt(examQuestionEx.getChapterTxt());
				examQuestion.setType(examQuestionEx.getType());
				examQuestion.setScore(examQuestionEx.getScore());
				examQuestion.setScores(examQuestionEx.getScores());
				examQuestion.setMarkOptions(examQuestionEx.getMarkOptions());
				examQuestion.setExamId(examQuestionEx.getExamId());
				examQuestion.setQuestionId(examQuestionEx.getQuestionId());
				examQuestion.setNo(examQuestionEx.getNo());// 上面处理了
				examQuestion.setUpdateUserId(getCurUser().getId());
				examQuestion.setUpdateTime(new Date());
				examQuestionService.add(examQuestion);
			}
		}
		// 如果是随机试卷，保存抽题规则
		else if (examInfo.getGenType() == 2) {
			for (int i = 0; i < examInfo.getExamRules().length; i++) {
				ExamRule examRule = examInfo.getExamRules()[i];
				examRule.setExamId(examInfo.getId());
				examRule.setNo(i + 1);
				examRuleService.add(examRule);
			}
		}
	}
	
	private void publishUser(ExamInfo examInfo, Map<Integer, List<Question>> questionListCache, String processBarId) {
		// 删除用户试卷、协助阅卷用户
//		List<MyExam> myExamList = myExamService.getList(examInfo.getId());// 100道题100个用户需要删除1万多次，改成sql删除
//		for (MyExam myExam : myExamList) {
//			List<MyQuestion> myQuestionList = myQuestionService.getList(myExam.getExamId(), myExam.getUserId());
//			for (MyQuestion myQuestion : myQuestionList) {
//				myQuestionService.del(myQuestion.getId());
//			}
//			
//			myExamService.del(myExam.getId());
//		}
		myExamService.clear(examInfo.getId());
		myQuestionService.clear(examInfo.getId());
		
//		List<MyMark> myMarkList = myMarkService.getList(examInfo.getId());
//		for (MyMark myMark : myMarkList) {
//			myMarkService.del(myMark.getId());
//		}
		
		// 重新生成用户试卷、协助阅卷用户
		Map<Integer, List<QuestionOption>> questionOptionCache = new HashMap<>();
		Map<Integer, List<QuestionAnswer>> questionAnswerCache = new HashMap<>();
		int curProgressNum = 1;// 当前保存进度
		Double processLen = (examInfo.getExamUserIds().length + 5) * 1.0;// 说明参考控制层
		for (Integer examUserId : examInfo.getExamUserIds()) {
			MyExam myExam = new MyExam();// 生成我的考试信息
			myExam.setExamId(examInfo.getId());
			myExam.setUserId(examUserId);
			myExam.setMarkUserId(1);// 由子管理员自己分配
			myExam.setState(1);// 未考试
			myExam.setMarkState(1);// 未阅卷
			myExam.setUpdateTime(new Date());
			myExam.setUpdateUserId(getCurUser().getId());
			myExamService.add(myExam);
			
			if (examInfo.getGenType() == 1) {// 如果是人工组卷，直接生成我的试卷
				List<MyQuestion> shuffleCacheList = new ArrayList<>();// 乱序缓存列表，用于乱序
				for (int i = 0; i < examInfo.getExamQuestions().length; i++) {
					ExamQuestionEx examQuestion = examInfo.getExamQuestions()[i];
					MyQuestion myQuestion = new MyQuestion();
					myQuestion.setChapterName(examQuestion.getChapterName());
					myQuestion.setChapterTxt(examQuestion.getChapterTxt());
					myQuestion.setType(examQuestion.getType());
					myQuestion.setScore(examQuestion.getScore());
					myQuestion.setScores(examQuestion.getScores());
					myQuestion.setMarkOptions(examQuestion.getMarkOptions());
					myQuestion.setExamId(examQuestion.getExamId());
					myQuestion.setQuestionId(examQuestion.getQuestionId());
					myQuestion.setUserId(examUserId);
					myQuestion.setNo(i + 1);
					myQuestion.setUpdateUserId(getCurUser().getId());
					myQuestion.setUpdateTime(new Date());
					myQuestionService.add(myQuestion);
					
					if (ExamUtil.hasQuestionRand(examInfo)) {// 如果是试题乱序（章节不能乱序；试题不能跨章节乱序）
						if (ExamUtil.hasQuestion(myQuestion)) {// 1章节；2试题；3试题；4试题；5章节；6试题；7试题；8：试题 
							shuffleCacheList.add(myQuestion); // 2试题；3试题；4试题；
						} 
						if (ExamUtil.hasChapter(myQuestion) || i >= examInfo.getExamQuestions().length - 1) {// 5章节；（如果是章节或最后一道题，乱序已经缓存的试题，前面不要加else，最后一道题的情况不处理）
							Collections.shuffle(shuffleCacheList);// 3试题；2试题；4试题；
							Integer maxNo = ExamUtil.hasChapter(myQuestion) ? myQuestion.getNo() - 1 : myQuestion.getNo();// 5章节
							for (MyQuestion shuffleCache : shuffleCacheList) {
								shuffleCache.setNo(maxNo--);
								myQuestionService.update(shuffleCache);// 1章节；4试题；2试题；3试题；
							}
							shuffleCacheList.clear();
						}
					}
					if (ExamUtil.hasOptionRand(examInfo)) {// 如果是选项乱序
						if (questionOptionCache.get(myQuestion.getQuestionId()) == null) {
							questionOptionCache.put(myQuestion.getQuestionId(), questionOptionService.getList(myQuestion.getQuestionId()));
						}
						List<QuestionOption> questionOptionList = questionOptionCache.get(myQuestion.getQuestionId());// A,B,C,D
						myQuestion.setOptionsNo(shuffleNums(1, questionOptionList.size()));// D,B,A,C
						myQuestionService.update(myQuestion);
					}
					
				}
			} else if (examInfo.getGenType() == 2) {// 如果是随机组卷，按抽题规则生成我的试卷（校验里判断过规则是否满足，不用在判断）
				Set<Question> questionOfUsed = new HashSet<>();
				int no = 1;
				for (int i = 0; i < examInfo.getExamRules().length; i++) {
					ExamRule examRule = examInfo.getExamRules()[i];
					if (examRule.getType() == 1) {// 如果是章节
						MyQuestion myQuestion = new MyQuestion();
						myQuestion.setType(examRule.getType());
						myQuestion.setChapterName(examRule.getChapterName());
						myQuestion.setChapterTxt(examRule.getChapterTxt());
						myQuestion.setUserId(examUserId);
						myQuestion.setExamId(examInfo.getId());
						myQuestion.setNo(no++);
						myQuestionService.add(myQuestion);
					} else {// 如果是规则
						List<Question> questionList = questionListCache.get(examRule.getQuestionTypeId());
						Collections.shuffle(questionList);// 从当前规则中随机抽题（乱序模拟随机）
						Integer ruleRemainNum = examRule.getNum();// 该规则试题数量，找到一个数量减一
						for(Question question : questionList) {
							if (ruleRemainNum <= 0) {// 满足规则，处理下一个规则
								break;
							}
							if (questionOfUsed.contains(question)) {// 已经使用过的试题就不能在用，继续找下一个
								continue;
							}
							if (examRule.getQuestionType() != question.getType() // 当前试题不符合当前抽题规则，继续找下一个
									|| examRule.getMarkType() != question.getMarkType()) {
								continue;
							}
							
							MyQuestion myQuestion = new MyQuestion();
							myQuestion.setType(examRule.getType());
							myQuestion.setScore(examRule.getScore());
							myQuestion.setMarkOptions(examRule.getMarkOptions());
							myQuestion.setQuestionId(question.getId());
							myQuestion.setUserId(examUserId);
							myQuestion.setExamId(examInfo.getId());
							myQuestion.setNo(no++); // 试题乱序无效，因为本身就是随机的
							
							if (QuestionUtil.hasMultipleChoice(question)) {// 如果是多选，使用抽题规则的漏选分数
								myQuestion.setScores(examRule.getScores());
							} else if ((QuestionUtil.hasFillBlank(question) || QuestionUtil.hasQA(question)) // 如果是客观填空问答，把分数平均分配到子分数
									&& QuestionUtil.hasObjective(question)) {// 如果抽题不设置分数，使用题库默认的分数，会导致总分不确定
								if (questionAnswerCache.get(myQuestion.getQuestionId()) == null) {// 如果抽题设置分数，主观题答案数量不一样，没法按答案分配分数
									questionAnswerCache.put(myQuestion.getQuestionId(), questionAnswerService.getList(myQuestion.getQuestionId()));
								}
								List<QuestionAnswer> questionAnswerList = questionAnswerCache.get(myQuestion.getQuestionId());// 所以规则为当题分数，平均分配到每个答案
								myQuestion.setScores(splitScore(examRule.getScore(), questionAnswerList.size()));
							}
							
							myQuestion.setUpdateTime(new Date());
							myQuestion.setUpdateUserId(getCurUser().getId());
							myQuestionService.add(myQuestion);
							
							questionOfUsed.add(question);
							ruleRemainNum--;
						}
					}
				}
			}
			
			ProgressBarCache.setProgressBar(processBarId, 4.0 + curProgressNum++, processLen, "生成用户-"+examUserId+"试卷完成", HttpStatus.OK.value());
		}
		
//		for (Integer markUserId : examInfo.getMarkUserIds()) {
//			MyMark myMark = new MyMark();
//			myMark.setExamId(examInfo.getId());
//			myMark.setMarkUserId(markUserId);
//			myMark.setQuestionIds(null);
//			myMark.setUpdateTime(new Date());
//			myMark.setUpdateTime(new Date());
//			myMarkService.add(myMark);
//		}
	}

	private void publishValid(ExamInfo examInfo, Map<Integer, List<Question>> questionListCache) {
		// 校验考试信息
		publishValidExam(examInfo);
		// 校验试卷信息
		publishValidPaper(examInfo, questionListCache);
		// 校验考试用户信息
		publishValidUser(examInfo);
	}

	private void publishValidUser(ExamInfo examInfo) {
		if (!ValidateUtil.isValid(examInfo.getExamUserIds())) {
			throw new MyException("最少添加一个考试用户");
		}
		Set<Integer> examUserIdSet = new HashSet<>(Arrays.asList(examInfo.getExamUserIds()));
		if (examUserIdSet.size() != examInfo.getExamUserIds().length) {
			throw new MyException("考试用户重复");
		}
		
		if (getCurUser().getType() != 0) {
			User curUser = userService.getEntity(getCurUser().getId());
			Set<Integer> haveUsers = CollectionUtil.toSet(curUser.getUserIds());
			Set<Integer> examUsers = CollectionUtil.toSet(examInfo.getExamUserIds());
			if (!haveUsers.containsAll(examUsers)) {
				throw new MyException("无用户操作权限");
			}
		}
		
		if (ValidateUtil.isValid(examInfo.getMarkUserIds())) {// 不一定有，如果有则校验是否重复
			Set<Integer> markUserIdSet = new HashSet<>(Arrays.asList(examInfo.getMarkUserIds()));
			if (markUserIdSet.size() != examInfo.getMarkUserIds().length) {
				throw new MyException("阅卷用户重复");
			}
			if (examInfo.getMarkType() == 1) {
				throw new MyException("自动阅卷，无需子管理员参与");// 需要人工阅卷也不一定需要子管理员，admin就可以
			}
		}
	}

	private void publishValidPaper(ExamInfo examInfo, Map<Integer, List<Question>> questionListCache) {
		if (examInfo.getGenType() == 1) {// 人工组卷
			if (!ValidateUtil.isValid(examInfo.getExamQuestions())) {
				throw new MyException("最少添加一道试题");
			}
			Set<Integer> questionIdCache = new HashSet<>();
			for (ExamQuestionEx examQuestion : examInfo.getExamQuestions()) {
				if (!ValidateUtil.isValid(examQuestion.getType())
						|| (examQuestion.getType() != 1 && examQuestion.getType() != 2)) {
					throw new MyException("参数错误：examQuestions.type");
				}
				if (examQuestion.getType() == 1) {// 如果是章节
					//if (!ValidateUtil.isValid(examQuestion.getChapterName())) { // 可以没有
					//	throw new MyException("参数错误：examQuestion.chapterName");
					//}
					if (ValidateUtil.isValid(examQuestion.getScore())) {
						throw new MyException("参数错误：examQuestions.score");
					}
					if (ValidateUtil.isValid(examQuestion.getScores())) {
						throw new MyException("参数错误：examQuestions.scores");
					}
					if (ValidateUtil.isValid(examQuestion.getMarkOptions())) {
						throw new MyException("参数错误：examQuestions.markOptions");
					}
					if (ValidateUtil.isValid(examQuestion.getQuestionId())) {
						throw new MyException("参数错误：examQuestions.questionId");
					}
				} else if (examQuestion.getType() == 2) {// 如果是试题
					if (ValidateUtil.isValid(examQuestion.getChapterName())) {
						throw new MyException("参数错误：examQuestions.chapterName");
					}
					if (ValidateUtil.isValid(examQuestion.getChapterTxt())) {
						throw new MyException("参数错误：examQuestions.chapterTxt");
					}
					
					if (!ValidateUtil.isValid(examQuestion.getScore())) {
						throw new MyException("参数错误：examQuestions.score");
					}
					
					{
						if (!ValidateUtil.isValid(examQuestion.getMarkType())
								|| (examQuestion.getMarkType() != 1 && examQuestion.getMarkType() != 2)) {
							throw new MyException("参数错误：examQuestions.markType");
						}
						if (examQuestion.getQuestionType() == 1 || examQuestion.getQuestionType() == 2 // 单选多选判断只能是客观题
								|| examQuestion.getQuestionType() == 4) {
							if (examQuestion.getMarkType() == 2) {
								throw new MyException("参数错误：examQuestions.markType");
							}
						}
					}
					{
						if (examQuestion.getQuestionType() == 1// 单选、判断、主观问答，没有子分数
								|| examQuestion.getQuestionType() == 4
								|| (examQuestion.getQuestionType() == 5 && examQuestion.getMarkType() == 2)) {
							if (ValidateUtil.isValid(examQuestion.getScores())) {
								throw new MyException("参数错误：examQuestions.scores");
							}
						}
						if (examQuestion.getQuestionType() == 2) {// 多选只有一个漏选分值
							if (!ValidateUtil.isValid(examQuestion.getScores()) || examQuestion.getScores().length != 1) {
								throw new MyException("参数错误：examQuestions.scores");
							}
						}
						if (examQuestion.getQuestionType() == 3 // 填空、客观问答，最少一个子分数
								|| (examQuestion.getQuestionType() == 5) && examQuestion.getMarkType() == 1) {
							if (!ValidateUtil.isValid(examQuestion.getScores()) || examQuestion.getScores().length <= 0) {
								throw new MyException("参数错误：examQuestions.scores");
							}
						}
					}
					
					if (ValidateUtil.isValid(examQuestion.getQuestionId())) {// 导入的试题没有ID
						if (questionIdCache.contains(examQuestion.getQuestionId())) {// 一张试卷不能有重复的试题
							throw new MyException(String.format("试题重复，编号：%s", examQuestion.getQuestionId()));
						}
						questionIdCache.add(examQuestion.getQuestionId());
					
						Question question = QuestionCache.getQuestion(examQuestion.getQuestionId());
						if (!(CurLoginUserUtil.isSelf(question.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {
							throw new MyException(String.format("试题无权限，编号：%s", examQuestion.getQuestionId()));
						}
					}
				}
			}
		} else if (examInfo.getGenType() == 2) {
			if (!ValidateUtil.isValid(examInfo.getExamRules())) {
				throw new MyException("最少添加一条规则");
			}
			
			Set<Question> questionOfUsed = new HashSet<>();// 已使用过的试题
			int ruleNo = 0; // 用于过滤章节后，题数不足时，提示那个规则无效
			for (int i = 0; i < examInfo.getExamRules().length; i++) {
				ExamRule examRule = examInfo.getExamRules()[i];
				if (!ValidateUtil.isValid(examRule.getType())
						|| (examRule.getType() != 1 && examRule.getType() != 2)) {
					throw new MyException("参数错误：examRule.type");
				}
				if (examRule.getType() == 1) {// 如果是章节
					//if (!ValidateUtil.isValid(examRule.getChapterName())) { // 没有也可以
					//	throw new MyException("参数错误：examRule.chapterName");
					//}
					if (ValidateUtil.isValid(examRule.getQuestionTypeId())) {
						throw new MyException("参数错误：examRules.questionTypeId");
					}
					if (ValidateUtil.isValid(examRule.getQuestionType())) {
						throw new MyException("参数错误：examRules.questionType");
					}
					if (ValidateUtil.isValid(examRule.getMarkType())) {
						throw new MyException("参数错误：examRules.markType");
					}
					if (ValidateUtil.isValid(examRule.getMarkOptions())) {
						throw new MyException("参数错误：examRules.markOptions");
					}
					if (ValidateUtil.isValid(examRule.getNum())) {
						throw new MyException("参数错误：examRules.num");
					}
					if (ValidateUtil.isValid(examRule.getScore())) {
						throw new MyException("参数错误：examRules.score");
					}
					if (ValidateUtil.isValid(examRule.getScores())) {
						throw new MyException("参数错误：examRules.scores");
					}
				} else if (examRule.getType() == 2) {// 如果是规则
					if (ValidateUtil.isValid(examRule.getChapterName())) {
						throw new MyException("参数错误：examQuestions.chapterName");
					}
					if (ValidateUtil.isValid(examRule.getChapterTxt())) {
						throw new MyException("参数错误：examQuestions.chapterTxt");
					}
					
					
					if (!ValidateUtil.isValid(examRule.getQuestionTypeId())) {
						throw new MyException("参数错误：examRules.questionTypeId");
					}
					if (!ValidateUtil.isValid(examRule.getQuestionType()) 
							|| examRule.getQuestionType() < 1 || examRule.getQuestionType() > 5) {
						throw new MyException("参数错误：examRules.questionType");
					}
					if (!ValidateUtil.isValid(examRule.getMarkType())
							|| (examRule.getMarkType() != 1 && examRule.getMarkType() != 2)) {
						throw new MyException("参数错误：examRules.markType");
					}
					if (!ValidateUtil.isValid(examRule.getNum())) {
						throw new MyException("参数错误：examRules.num");
					}
					if (examRule.getNum() <= 0 || examRule.getNum() > 100) {
						throw new MyException("参数错误：examRules.num");
					}
					
					if (!ValidateUtil.isValid(examRule.getScore())) {
						throw new MyException("参数错误：examRules.score");
					}
					if (examRule.getScore().doubleValue() <= 0 || examRule.getScore().doubleValue() > 20) {
						throw new MyException("参数错误：examRules.score");
					}
					
					if (examRule.getMarkType() == 1 // 如果是客观填空或客观问答，才有阅卷选项
						&& (examRule.getQuestionType() == 3 || examRule.getQuestionType() == 5)) {
						if (ValidateUtil.isValid(examRule.getMarkOptions())) {
							for (Integer markOption : examRule.getMarkOptions()) {
								if (markOption != 2 && markOption != 3) {
									throw new MyException("参数错误：examRules.markOptions");
								}
							}
						}
					} else { // 否则不能有阅卷选项
						if (ValidateUtil.isValid(examRule.getMarkOptions())) {
							throw new MyException("参数错误：examRules.markOptions");
						}
					}
					
					if ((examRule.getQuestionType() == 1 // 单选多选判断不能是主观题
							|| examRule.getQuestionType() == 2 || examRule.getQuestionType() == 4)) {
						if (examRule.getMarkType() == 2) {
							throw new MyException("参数错误：examRules.MarkType");
						}
					}
					if (examRule.getQuestionType() == 2) {// 如果是多选，必须有子分数（漏选分值）
						if (!ValidateUtil.isValid(examRule.getScores())) {
							throw new MyException("参数错误：examRules.scores");
						}
						if (examRule.getScores().length != 1) {
							throw new MyException("参数错误：examRules.scores");
						}
						if (examRule.getScore().doubleValue() <= examRule.getScores()[0].doubleValue()) {// 漏选分数不能大于分数
							throw new MyException("参数错误：examRules.scores");
						}
					}
					if (examRule.getQuestionType() != 2) {// 如果是单选判断不能有子分数，填空问答平均分配分数
						if (ValidateUtil.isValid(examRule.getScores())) {
							throw new MyException("参数错误：examRules.scores");
						}
					}
					
					ruleNo++;
					int validQuestionNum = 0;// 符合当前抽题规则的有效题数
					for (Question question : questionListCache.get(examRule.getQuestionTypeId())) {
						if (!(CurLoginUserUtil.isSelf(question.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {
							throw new MyException(String.format("试题无权限，编号：%s", question.getId()));
						}
						
						if (questionOfUsed.contains(question)) {// 已经使用过的试题就不能在用，继续找下一个
							continue;
						}
						if (validQuestionNum >= examRule.getNum()) {// 当前题库已满足当前抽题规则，不在继续验证
							break;
						}
						
						if (examRule.getQuestionType() == question.getType() // 当前试题满足抽题规则
								&& examRule.getMarkType() == question.getMarkType()) {
							questionOfUsed.add(question);// 加入已使用过的试题
							validQuestionNum++; // 有效题数加一
						}
					}
					
					if (validQuestionNum < examRule.getNum()) {
						throw new MyException(String.format("试卷配置：第【%s】个规则题数不足%s，请修改", ruleNo, examRule.getNum()));
					}
				}
			}
		}
	}
	
	private void publishValidExam(ExamInfo examInfo) {
		if (ValidateUtil.isValid(examInfo.getId())) {// 如果是二次修改，校验考试是否结束
			Exam exam = getEntity(examInfo.getId());
			if (exam.getMarkType() == 3) {
				throw new MyException("已阅卷");
			}
			if (exam.getEndTime().getTime() <= System.currentTimeMillis()) {
				throw new MyException("考试已结束");
			}
			if (exam.getStartTime().getTime() <= System.currentTimeMillis()) {
				throw new MyException("考试已开始");// 考试结束放在考试开始前校验，可能的问题为考试已结束，提示的是考试已开始。
			}
		}
		
		if (!ValidateUtil.isValid(examInfo.getName())) {
			throw new MyException("参数错误：name");
		}
		
		BigDecimal paperTotalScore = BigDecimal.ZERO;// 试卷总分
		BigDecimalUtil bigDecimalUtil = BigDecimalUtil.newInstance(0);
		boolean containSubjectiveQuesiton = false;// 包含主观试题
		{
			if (examInfo.getGenType() == 1) {
				if (!ValidateUtil.isValid(examInfo.getExamQuestions())) {
					throw new MyException("最少添加一道试题");
				}
				for (ExamQuestionEx examQuestion : examInfo.getExamQuestions()) {
					if (ExamUtil.hasQuestion(examQuestion)) {
						bigDecimalUtil.add(examQuestion.getScore());// 单题分数从组卷后定制的分数中取
						 if (examQuestion.getMarkType() == 2) {// 简单校验markType就可以，保存试题时有详细校验
							containSubjectiveQuesiton = true;
						}
					}
				}
			} else if (examInfo.getGenType() == 2) {
				if (!ValidateUtil.isValid(examInfo.getExamRules())) {
					throw new MyException("最少添加一条规则");
				}
				for (ExamRule examRule : examInfo.getExamRules()) {
					if (ExamUtil.hasQuestion(examRule)) {
						bigDecimalUtil.add(// 单题分数*数量累加到总分
								BigDecimalUtil.newInstance(examRule.getScore()).mul(examRule.getNum()).getResult());
						if (examRule.getMarkType() == 2) {
							containSubjectiveQuesiton = true;
						}
					}
				}
			}
			paperTotalScore = bigDecimalUtil.getResult();
		}
		{
			if (!ValidateUtil.isValid(examInfo.getStartTime())) {
				throw new MyException("参数错误：startTime");
			}
			if (!ValidateUtil.isValid(examInfo.getEndTime())) {
				throw new MyException("参数错误：endTime");
			}
			if (examInfo.getStartTime().getTime() >= examInfo.getEndTime().getTime()) {
				throw new MyException("考试开始时间必须小于考试结束时间");
			}
			if (examInfo.getEndTime().getTime() <= System.currentTimeMillis()) {
				throw new MyException("考试结束时间必须大于当前时间");
			}
			if (containSubjectiveQuesiton) {// 如果包含主观题，阅卷时间不能缺失
				if (!ValidateUtil.isValid(examInfo.getMarkStartTime())) {
					throw new MyException("参数错误：markStartTime");
				}
				if (!ValidateUtil.isValid(examInfo.getMarkEndTime())) {
					throw new MyException("参数错误：markEndTime");
				}
				if (examInfo.getMarkStartTime().getTime() >= examInfo.getMarkEndTime().getTime()) {
					throw new MyException("阅卷开始时间必须小于阅卷结束时间");
				}
				if (examInfo.getEndTime().getTime() >= examInfo.getMarkStartTime().getTime()) {
					throw new MyException("阅卷开始时间必须大于考试结束时间");
				}
			} else {
				if (ValidateUtil.isValid(examInfo.getMarkStartTime())) {
					throw new MyException("参数错误：markStartTime");
				}
				if (ValidateUtil.isValid(examInfo.getMarkEndTime())) {
					throw new MyException("参数错误：markEndTime");
				}
			}
		}
		
		if (!ValidateUtil.isValid(examInfo.getScoreState()) 
				|| (examInfo.getScoreState() != 1 && examInfo.getScoreState() != 2 && examInfo.getScoreState() != 3)) {
			throw new MyException("参数错误：scoreState");
		}
		if (!ValidateUtil.isValid(examInfo.getRankState()) 
				|| (examInfo.getRankState() != 1 && examInfo.getRankState() != 2)) {
			throw new MyException("参数错误：rankState");
		}
		if (!ValidateUtil.isValid(examInfo.getAnonState()) 
				|| (examInfo.getAnonState() != 1 && examInfo.getAnonState() != 2)) {
			throw new MyException("参数错误：examInfo.anonState");
		}
		if (!ValidateUtil.isValid(examInfo.getShowType()) 
				|| (examInfo.getShowType() != 1 && examInfo.getShowType() != 3)) {
			throw new MyException("参数错误：showType");
		}
		if (!ValidateUtil.isValid(examInfo.getGenType()) 
				|| (examInfo.getGenType() != 1 && examInfo.getGenType() != 2)) {
			throw new MyException("参数错误：genType");
		}
		if (!ValidateUtil.isValid(examInfo.getState()) 
				|| (examInfo.getState() != 1 && examInfo.getState() != 2)) {
			throw new MyException("参数错误：state");
		}
		{
			if (!ValidateUtil.isValid(examInfo.getMarkType()) 
					|| (examInfo.getMarkType() != 1 && examInfo.getMarkType() != 2)) {
				throw new MyException("参数错误：markType");
			}
			if (containSubjectiveQuesiton && examInfo.getMarkType() != 2) {
				throw new MyException("参数错误：markType");
			}
			if (!containSubjectiveQuesiton && examInfo.getMarkType() != 1) {
				throw new MyException("参数错误：markType");
			}
		}
		if (!ValidateUtil.isValid(examInfo.getTotalScore()) //总分无效
				|| examInfo.getTotalScore().doubleValue() <= 0 //总分小于0
				|| examInfo.getTotalScore().doubleValue() != paperTotalScore.doubleValue()) {//总分和卷面分数不相等
			throw new MyException("参数错误：totalScore");
		}
		
		if (!ValidateUtil.isValid(examInfo.getPassScore()) // 及格分数无效
				|| examInfo.getPassScore().doubleValue() < 0 // 小于0
				|| examInfo.getPassScore().doubleValue() > examInfo.getTotalScore().doubleValue()) {// 大于总分
			throw new MyException("参数错误：passScore");
		}
	}

	private Map<Integer, List<Question>> publishHandle(ExamInfo examInfo) {
		if (examInfo.getGenType() == 1) {// 如果是人工组卷
			for (ExamQuestionEx examQuestion : examInfo.getExamQuestions()) {
				if (ExamUtil.hasQuestion(examQuestion)) {// 如果是试题
					if (ValidateUtil.isValid(examQuestion.getQuestionId())) {// 如果是从题库抽的题
						Question question = QuestionCache.getQuestion(examQuestion.getQuestionId());
						examQuestion.setQuestionType(question.getType());// 试题类型等重新查一遍数据库，不要依赖前端
						examQuestion.setMarkType(question.getMarkType());
						// examQuestion.setTitle(question.getTitle());
						// examQuestion.setAnalysis(question.getAnalysis());
						//examQuestion.setOptions(null);// 用不着可以不查询
						//examQuestion.setAnswers(null);// 
					}
				}
			}
			return null;
		}
		
		Map<Integer, List<Question>> questionListCache = new HashMap<>();
		if (examInfo.getGenType() == 2) {// 如果是随机组卷
			for (int i = 0; i < examInfo.getExamRules().length; i++) {
				ExamRule examRule = examInfo.getExamRules()[i];
				if (!ValidateUtil.isValid(questionListCache.get(examRule.getQuestionTypeId()))) {
					List<Question> questionList = questionService.getList(examRule.getQuestionTypeId());
					questionListCache.put(examRule.getQuestionTypeId(), questionList);// 把题库缓存起来，用于模拟随机抽题
				}
			}
		}
		return questionListCache;
	}
	
	/**
	 * 拆分分数<br/>
	 * 1分 拆分成2份，结果：0.5、0.5<br/>
	 * 1分 拆分成3份，结果：0.33、0.33、0.34<br/>
	 * 
	 * v1.0 zhanghc 2022年5月30日下午4:46:25
	 * @param score
	 * @param num
	 * @return List<BigDecimal>
	 */
	private BigDecimal[] splitScore(BigDecimal score, int num) {
		BigDecimal[] scores = new BigDecimal[num];
		BigDecimal singleScore = BigDecimalUtil.newInstance(score).div(num, 2).getResult();
		for (int i = 0; i < num - 1; i++) {
			scores[i] = singleScore;
		}
		scores[num- 1] = BigDecimalUtil.newInstance(singleScore).mul(num - 1).sub(score).mul(-1).getResult();
		return scores;
	}
	
	private Integer[] shuffleNums(int start, int end) {
		Integer[] shuffleNums = new Integer[end - start + 1];
		for (int i = 0; i < shuffleNums.length; i++) {
			shuffleNums[i] = start + i;
		}
		
		ArrayUtils.shuffle(shuffleNums);
		return shuffleNums;
	}

	@Override
	public void userAdd(Integer id, String[] examUserIds, Integer[] markUserIds) {
		
	}

	@Override
	public List<Question> getPaper(Integer id) {
		return null;
	}
}
