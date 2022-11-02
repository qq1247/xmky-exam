package com.wcpdoc.exam.core.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ArrayUtils;
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
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.cache.AutoMarkCache;
import com.wcpdoc.exam.core.dao.ExamDao;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.ExamQuestion;
import com.wcpdoc.exam.core.entity.ExamRule;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyMark;
import com.wcpdoc.exam.core.entity.MyQuestion;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.entity.ex.ExamInfo;
import com.wcpdoc.exam.core.entity.ex.ExamQuestionEx;
import com.wcpdoc.exam.core.entity.ex.ExamUser;
import com.wcpdoc.exam.core.entity.ex.QuestionEx;
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
	 {exam": {
			"name": "考试-20221020",
			"paperName": "点击这里输入试卷名称",
			"timeType": 1,
			"passScore": 14,
			"sxes": [1, 2],
			"showType": 1,
			"anonState": 1,
			"scoreState": 2,
			"rankState": 2,
			"state": 1,
			"genType": 1,
			"markType": 2,
			"totalScore": 24,
			"startTime": "2022-10-20 08:00:00",
			"endTime": "2022-10-20 10:00:00",
			"markStartTime": "2022-10-20 14:00:00",
			"markEndTime": "2022-10-20 18:00:00"
		},
		"examQuestions": [{
			"type": 2,
			"question": {
				"type": 1,
				"title": "这是一道单选题的题干，简单写法",
				"options": ["单选题的A选项", "单选题的B选项", "单选题的C选项", "单选题的D选项"],
				"answers": ["B"],
				"score": 1,
				"answerScores": [],
				"analysis": "",
				"markType": 1,
				"markTypeOptions": [],
				"state": 1,
				"questionTypeId": null,
				"no": 1
			}
		}],
		"examUsers": [{
			"examUserIds": [2, 3],
			"markUserId": 2
		}]
	}
	 */
	@Override
	public void publish(ExamInfo examInfo) {
		/**
		 * json数据处理
		 * 自定义的分数等信息临时保存在试题上，先保存到试卷中
		 * 
		 * 人工组卷
		 * 1：文本导入试题，需要保存试题
		 * 2：题库抽题，需要重新查一遍数据库，不要依赖前端，用于检测包含主观题等
		 * 
		 * 随机组卷
		 * 1：需要的题库，用于随机抽题等
		 */
		Map<Integer, List<Question>> questionListCache = publishHandle(examInfo);
		
		/**
		 * 校验数据有效性，数据结构如下：
		 */
		publishValid(examInfo, questionListCache);

		// 添加考试信息
		publishExam(examInfo);
		
		// 保存试卷信息
		publishPaper(examInfo, questionListCache);
		
		// 分配试卷到用户
		publishUser(examInfo, questionListCache);
	}
	
	@Override
	public void delEx(Integer id) {
		// 校验数据有效性（只要有权限就删，不管是否考试中，如创建了一个超长的结束时间）
		Exam exam = getEntity(id);
		if (exam.getUpdateUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("无操作权限");
		}
		
		// 删除考试
		exam.setState(0);
		exam.setUpdateTime(new Date());
		exam.setUpdateUserId(getCurUser().getId());
		update(exam);
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
	
	private void publishExam(ExamInfo examInfo) {
		if (examInfo.getExam().getId() != null) {
			Exam exam = getEntity(examInfo.getExam().getId());
			exam.setName(examInfo.getExam().getName());
			exam.setPaperName(examInfo.getExam().getPaperName());
			exam.setTimeType(examInfo.getExam().getTimeType());
			exam.setStartTime(examInfo.getExam().getStartTime());
			exam.setEndTime(examInfo.getExam().getEndTime());
			exam.setMarkStartTime(examInfo.getExam().getMarkStartTime());
			exam.setMarkEndTime(examInfo.getExam().getMarkEndTime());
			exam.setMarkType(examInfo.getExam().getMarkType());
			exam.setScoreState(examInfo.getExam().getScoreState());
			exam.setRankState(examInfo.getExam().getRankState());
			exam.setAnonState(examInfo.getExam().getAnonState());
			exam.setPassScore(examInfo.getExam().getPassScore());
			exam.setTotalScore(examInfo.getExam().getTotalScore());
			exam.setMarkType(examInfo.getExam().getMarkType());
			exam.setShowType(examInfo.getExam().getShowType());
			exam.setGenType(examInfo.getExam().getGenType());
			exam.setSxes(examInfo.getExam().getSxes());
			exam.setState(examInfo.getExam().getState());
			exam.setUpdateUserId(getCurUser().getId());
			exam.setUpdateTime(new Date());
			update(exam);
			return;
		}
		
		Exam exam = examInfo.getExam();
		exam.setMarkState(1);// 标记为未阅卷（考试时间结束，定时任务自动阅卷，标记为已阅）
		exam.setUpdateUserId(getCurUser().getId());
		exam.setUpdateTime(new Date());
		add(exam);
	}
	
	private void publishPaper(ExamInfo examInfo, Map<Integer, List<Question>> questionListCache) {
		// 如果是修改，先删除之前的数据
		{
			List<ExamQuestion> examQuestionList = examQuestionService.getList(examInfo.getExam().getId());// 如果是修改，删除之前的配置
			for (ExamQuestion examQuestion : examQuestionList) {
				examQuestionService.del(examQuestion.getId());
			}
			List<ExamRule> examRuleList = examRuleService.getList(examInfo.getExam().getId());
			for (ExamRule examRule : examRuleList) {
				examRuleService.del(examRule.getId());
			}
		}
		
		// 如果是人工试卷，保存试卷模板
		if (examInfo.getExam().getGenType() == 1) {
			QuestionType questionType = null;// 如果有从页面导入的新题，保存到一个题库中，名称保持和考试名称一致
			for (int i = 0; i < examInfo.getExamQuestions().length; i++) {
				ExamQuestionEx examQuestionEx = examInfo.getExamQuestions()[i];
				if (ExamUtil.hasQuestion(examQuestionEx)) {
					QuestionEx questionEx = examQuestionEx.getQuestion();
					if (!ValidateUtil.isValid(questionEx.getId())) {// 如果是从页面导入的新题，先保存
						if (questionType == null) {
							questionType = new QuestionType();
							questionType.setName(examInfo.getExam().getName());
							questionType.setUpdateTime(new Date());
							questionType.setUpdateUserId(getCurUser().getId());
							questionTypeService.add(questionType);
						}
						
						questionEx.setQuestionTypeId(questionType.getId());
						Question question = new Question();
						try {
							BeanUtils.copyProperties(question, questionEx);
						} catch (Exception e) {
							throw new MyException("复制属性失败：question");
						}
						questionService.addEx(question, questionEx.getOptions(), questionEx.getAnswers(), questionEx.getAnswerScores());
						questionEx.setId(question.getId());
					}
					examQuestionEx.setQuestionId(questionEx.getId());
				}
				examQuestionEx.setExamId(examInfo.getExam().getId());
				examQuestionEx.setNo(i + 1);
				examQuestionEx.setUpdateUserId(getCurUser().getId());
				examQuestionEx.setUpdateTime(new Date());
				
				ExamQuestion examQuestion = new ExamQuestion();
				try {
					BeanUtils.copyProperties(examQuestion, examQuestionEx);
				} catch (Exception e) {
					throw new MyException("复制属性失败：examQuestion");
				}
				examQuestionService.add(examQuestion);
				examQuestionEx.setId(examQuestion.getId());
			}
		}
		// 如果是随机试卷，保存抽题规则
		else if (examInfo.getExam().getGenType() == 2) {
			for (ExamRule examRule : examInfo.getExamRules()) {
				examRuleService.add(examRule);
			}
		}
	}
	
	private void publishUser(ExamInfo examInfo, Map<Integer, List<Question>> questionListCache) {
		List<MyExam> myExamList = myExamService.getList(examInfo.getExam().getId());
		for (MyExam myExam : myExamList) {
			List<MyQuestion> myQuestionList = myQuestionService.getList(myExam.getExamId(), myExam.getUserId());
			for (MyQuestion myQuestion : myQuestionList) {
				myQuestionService.del(myQuestion.getId());
			}
			
			myExamService.del(myExam.getId());
		}
		
		// 获取用户列表
		Map<Integer, List<QuestionOption>> questionOptionCache = new HashMap<>();
		Map<Integer, List<QuestionAnswer>> questionAnswerCache = new HashMap<>();
		for (ExamUser examUser : examInfo.getExamUsers()) {
			for (Integer examUserId : examUser.getExamUserIds()) {
				// 生成我的考试信息
				MyExam myExam = new MyExam();
				myExam.setExamId(examInfo.getExam().getId());
				myExam.setUserId(examUserId);
				myExam.setMarkUserId(examUser.getMarkUserId());
				myExam.setState(1);// 未考试
				myExam.setMarkState(1);// 未阅卷
				myExam.setUpdateTime(new Date());
				myExam.setUpdateUserId(getCurUser().getId());
				myExamService.add(myExam);
				
				// 如果是人工组卷，直接生成我的试卷
				if (examInfo.getExam().getGenType() == 1) {
					List<MyQuestion> myQuestionList = new ArrayList<>();
					List<MyQuestion> subMyQuestionList = new ArrayList<>();
					for (int i = 0; i < examInfo.getExamQuestions().length; i++) {
						ExamQuestionEx examQuestion = examInfo.getExamQuestions()[i];
						MyQuestion myQuestion = new MyQuestion();
						try {
							BeanUtils.copyProperties(myQuestion, examQuestion);// 复制试卷到用户试卷
						} catch (Exception e) {
							throw new MyException("复制属性失败");
						}
						myQuestion.setUserId(examUserId);
						
						if (ExamUtil.hasQuestionRand(examInfo.getExam())) {// 如果是试题乱序（章节不能乱序，试题不能跨章节乱序）
							if (ExamUtil.hasQuestion(myQuestion)) {// 1章节；2试题；3试题；4试题；5章节；6试题；7试题；8：试题 
								subMyQuestionList.add(myQuestion); // 2试题；3试题；4试题；
							} else if (ExamUtil.hasChapter(myQuestion) || i >= myQuestionList.size() - 1) {// 5章节；
								Collections.shuffle(subMyQuestionList);// 3试题；2试题；4试题；
								Integer maxNo = myQuestion.getNo();// 5章节
								for (MyQuestion subMyQuestion : subMyQuestionList) {
									subMyQuestion.setNo(maxNo--);
									myQuestionService.update(subMyQuestion);// 1章节；4试题；2试题；3试题；
								}
							}
						}
						if (ExamUtil.hasOptionRand(examInfo.getExam())) {// 如果是选项乱序
							if (questionOptionCache.get(myQuestion.getQuestionId()) == null) {
								questionOptionCache.put(myQuestion.getQuestionId(), questionOptionService.getList(myQuestion.getQuestionId()));
							}
							List<QuestionOption> questionOptionList = questionOptionCache.get(myQuestion.getQuestionId());// A,B,C,D
							myQuestion.setOptionsNo(shuffleNums(1, questionOptionList.size()));// D,B,A,C
						}
						myQuestionService.add(myQuestion);
					}
				} 
				// 如果是随机组卷，按抽题规则生成我的试卷
				else if (examInfo.getExam().getGenType() == 2) {
					Set<Question> questionOfUsed = new HashSet<>();
					int no = 1;
					for (int i = 0; i < examInfo.getExamRules().length; i++) {
						ExamRule examRule = examInfo.getExamRules()[i];
						List<Question> questionList = questionListCache.get(examRule.getQuestionTypeId());
						Collections.shuffle(questionList);// 从当前规则中随机抽题（乱序模拟随机）
						for(Question question : questionList) {
							if (questionOfUsed.contains(question)) {// 已经使用过的试题就不能在用，继续找下一个
								continue;
							}
							if (examRule.getType() != question.getType() // 当前试题不符合当前抽题规则，继续找下一个
									|| examRule.getMarkType() != question.getMarkType()) {
								continue;
							}
							
							MyQuestion myQuestion = new MyQuestion();
							myQuestion.setChapterName(examRule.getChapterName());
							myQuestion.setChapterTxt(examRule.getChapterTxt());
							myQuestion.setType(examRule.getType());
							myQuestion.setScore(examRule.getScore());
							myQuestion.setMarkOptions(examRule.getMarkOptions());
							myQuestion.setExamId(examRule.getExamId());
							myQuestion.setQuestionId(question.getId());
							myQuestion.setUserId(examUserId);
							myQuestion.setExamId(examInfo.getExam().getId());
							myQuestion.setNo(no++); // 试题乱序无效，因为本身就是随机的
							
//							if (ExamUtil.hasOptionRand(examInfo.getExam())) {// 如果是选项乱序
//								if (questionOptionCache.get(myQuestion.getQuestionId()) == null) {
//									questionOptionCache.put(myQuestion.getQuestionId(), questionOptionService.getList(myQuestion.getQuestionId()));
//								}
//								List<QuestionOption> questionOptionList = questionOptionCache.get(myQuestion.getQuestionId());// A,B,C,D
//								myQuestion.setOptionsNo(shuffleNums(1, questionOptionList.size()));// D,B,A,C
//							}
							
							if (QuestionUtil.hasMultipleChoice(question)) {// 如果是多选，使用抽题规则的漏选分数
								myQuestion.setScores(examRule.getScores());
							} else if ((QuestionUtil.hasFillBlank(question) || QuestionUtil.hasQA(question)) // 如果是客观填空问答，把分数平均分配到子分数
									&& QuestionUtil.hasSubjective(question)) {// 如果抽题不设置分数，使用题库默认的分数，会导致总分不确定
								if (questionAnswerCache.get(myQuestion.getQuestionId()) == null) {// 如果抽题设置分数，主观题答案数量不一样，没法按答案分配分数
									questionAnswerCache.put(myQuestion.getQuestionId(), questionAnswerService.getList(myQuestion.getQuestionId()));
								}
								List<QuestionAnswer> questionAnswerList = questionAnswerCache.get(myQuestion.getQuestionId());// 所以规则为当题分数，平均分配到每个答案
								myQuestion.setScores(splitScore(examRule.getScore(), questionAnswerList.size()));
							}
							
							myQuestion.setUpdateTime(new Date());
							myQuestion.setUpdateUserId(getCurUser().getId());
							myQuestionService.add(myQuestion);
						}
					}
				}
			}
		}
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
		Set<Integer> examUserIdCache = new HashSet<>();
		Set<Integer> markUserIdCache = new HashSet<>();
		if (!ValidateUtil.isValid(examInfo.getExamUsers())) {
			throw new MyException("参数错误：examUsers");
		}
		
		for (ExamUser examUser : examInfo.getExamUsers()) {
			if (!ValidateUtil.isValid(examUser.getExamUserIds())) {
				throw new MyException("参数错误：examUsers.examUserIds");
			}
			
			for (Integer examUserId : examUser.getExamUserIds()) {
				if (examUserIdCache.contains(examUserId)) {
					throw new MyException("参数错误：examUsers.examUserIds重复");
				}
				examUserIdCache.add(examUserId);
			}
			
			if (examInfo.getExam().getMarkType() == 1) { // 如果自动阅卷，不需要阅卷用户
				if (ValidateUtil.isValid(examUser.getMarkUserId())) {
					throw new MyException("参数错误：examUsers.markUserId");
				}
			}
			if (examInfo.getExam().getMarkType() == 2) { // 如果人工阅卷，需要阅卷用户
				if (!ValidateUtil.isValid(examUser.getMarkUserId())) {
					throw new MyException("参数错误：examUsers.markUserId");
				}
				
				if (markUserIdCache.contains(examUser.getMarkUserId())) {
					throw new MyException("参数错误：examUsers.markUserId重复");
				}
				markUserIdCache.add(examUser.getMarkUserId());
			}
		}
	}

	private void publishValidPaper(ExamInfo examInfo, Map<Integer, List<Question>> questionListCache) {
		if (examInfo.getExam().getGenType() == 1) {
			if (!ValidateUtil.isValid(examInfo.getExamQuestions())) {
				throw new MyException("参数错误：examQuestion");
			}
			Set<Integer> questionIdCache = new HashSet<>();
			Map<Integer, QuestionType> questionTypeCache = new HashMap<>();
			for (ExamQuestionEx examQuestion : examInfo.getExamQuestions()) {
				if (!ValidateUtil.isValid(examQuestion.getType())
						|| (examQuestion.getType() != 1 && examQuestion.getType() != 2)) {
					throw new MyException("参数错误：examQuestion.type");
				}
				if (examQuestion.getType() == 1) {// 如果是章节
					//if (!ValidateUtil.isValid(examQuestion.getChapterName())) { // 没有也行
					//	throw new MyException("参数错误：examQuestion.chapterName");
					//}
					if (ValidateUtil.isValid(examQuestion.getScore())) {
						throw new MyException("参数错误：examQuestion.score");
					}
					if (ValidateUtil.isValid(examQuestion.getScores())) {
						throw new MyException("参数错误：examQuestion.scores");
					}
					if (ValidateUtil.isValid(examQuestion.getMarkOptions())) {
						throw new MyException("参数错误：examQuestion.markOptions");
					}
					if (examQuestion.getQuestion() != null) {
						throw new MyException("参数错误：examQuestion.question");
					}
				}
				if (examQuestion.getType() == 2) {// 如果是试题
					if (ValidateUtil.isValid(examQuestion.getChapterName())) {
						throw new MyException("参数错误：examQuestion.chapterName");
					}
					if (ValidateUtil.isValid(examQuestion.getChapterTxt())) {
						throw new MyException("参数错误：examQuestion.chapterTxt");
					}
					
					if (!ValidateUtil.isValid(examQuestion.getScore())) {
						throw new MyException("参数错误：examQuestion.score");
					}
					{
						if (QuestionUtil.hasSingleChoice(examQuestion.getQuestion())// 单选、判断、主观问答，没有子分数
								|| QuestionUtil.hasTrueFalse(examQuestion.getQuestion())
								|| (QuestionUtil.hasQA(examQuestion.getQuestion()) && QuestionUtil.hasObjective(examQuestion.getQuestion()))) {
							if (ValidateUtil.isValid(examQuestion.getScores())) {
								throw new MyException("参数错误：examQuestion.question.scores");
							}
						}
						if (QuestionUtil.hasMultipleChoice(examQuestion.getQuestion())) {// 多选只有一个
							if (!ValidateUtil.isValid(examQuestion.getScores()) || examQuestion.getScores().length != 1) {
								throw new MyException("参数错误：examQuestion.question.scores");
							}
						}
						if (QuestionUtil.hasFillBlank(examQuestion.getQuestion())// 填空、客观问答，最少一个子分数
								|| (QuestionUtil.hasQA(examQuestion.getQuestion()) && QuestionUtil.hasSubjective(examQuestion.getQuestion()))) {
							if (!ValidateUtil.isValid(examQuestion.getScores()) || examQuestion.getScores().length <= 0) {
								throw new MyException("参数错误：examQuestion.question.scores");
							}
						}
					}
					if (examQuestion.getQuestion() == null) {
						throw new MyException("参数错误：examQuestion.question");
					}
					
					if (ValidateUtil.isValid(examQuestion.getQuestion().getId())) {
						if (questionIdCache.contains(examQuestion.getQuestion().getId())) {// 一张试卷不能有重复的试题
							throw new MyException(String.format("试题重复，编号：%s", examQuestion.getQuestion().getId()));
						}
						questionIdCache.add(examQuestion.getQuestion().getId());
						
						if (questionTypeCache.get(examQuestion.getQuestion().getId()) == null) {// 校验是否有使用权限
							questionTypeCache.put(examQuestion.getQuestion().getId(), questionTypeService.getEntity(examQuestion.getQuestion().getQuestionTypeId()));
						}
						QuestionType questionType = questionTypeCache.get(examQuestion.getQuestion().getId());
						if (questionType.getUpdateUserId().intValue() != getCurUser().getId().intValue()) {
							throw new MyException(String.format("试题无权限，编号：%s", examQuestion.getQuestion().getId()));
						}
					}
				}
			}
		}
		
		if (examInfo.getExam().getGenType() == 2) {
			if (!ValidateUtil.isValid(examInfo.getExamRules())) {
				throw new MyException("参数错误：examRules");
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
					//if (!ValidateUtil.isValid(examRule.getChapterName())) { // 没有也行
					//	throw new MyException("参数错误：examRule.chapterName");
					//}
					if (ValidateUtil.isValid(examRule.getQuestionTypeId())) {
						throw new MyException("参数错误：examRule.questionTypeId");
					}
					if (ValidateUtil.isValid(examRule.getQuestionType())) {
						throw new MyException("参数错误：examRule.questionType");
					}
					if (ValidateUtil.isValid(examRule.getMarkType())) {
						throw new MyException("参数错误：examRule.markType");
					}
					if (ValidateUtil.isValid(examRule.getMarkOptions())) {
						throw new MyException("参数错误：examRule.markOptions");
					}
					if (ValidateUtil.isValid(examRule.getNum())) {
						throw new MyException("参数错误：examRule.num");
					}
					if (ValidateUtil.isValid(examRule.getScore())) {
						throw new MyException("参数错误：examRule.score");
					}
					if (ValidateUtil.isValid(examRule.getScores())) {
						throw new MyException("参数错误：examRule.scores");
					}
				}
				
				if (examRule.getType() == 2) {// 如果是规则
					if (ValidateUtil.isValid(examRule.getChapterName())) {
						throw new MyException("参数错误：examQuestion.chapterName");
					}
					if (ValidateUtil.isValid(examRule.getChapterTxt())) {
						throw new MyException("参数错误：examQuestion.chapterTxt");
					}
					
					
					if (!ValidateUtil.isValid(examRule.getQuestionTypeId())) {
						throw new MyException("参数错误：examRule.questionTypeId");
					}
					if (!ValidateUtil.isValid(examRule.getQuestionType()) 
							|| examRule.getQuestionType() < 1 || examRule.getQuestionType() > 5) {
						throw new MyException("参数错误：examRule.questionType");
					}
					if (!ValidateUtil.isValid(examRule.getMarkType())
							|| (examRule.getMarkType() != 1 && examRule.getMarkType() != 2)) {
						throw new MyException("参数错误：examRule.markType");
					}
					if (!ValidateUtil.isValid(examRule.getNum())) {
						throw new MyException("参数错误：examRule.num");
					}
					if (examRule.getNum() <= 0 || examRule.getNum() > 100) {
						throw new MyException("参数错误：examRule.num");
					}
					
					if (!ValidateUtil.isValid(examRule.getScore())) {
						throw new MyException("参数错误：examRule.score");
					}
					if (examRule.getScore().doubleValue() <= 0 || examRule.getScore().doubleValue() > 20) {
						throw new MyException("参数错误：examRule.score");
					}
					
					if (examRule.getMarkType() == 1 // 如果是客观填空或客观问答，才有阅卷选项
						&& (examRule.getQuestionType() == 3 || examRule.getQuestionType() == 5)) {
						if (ValidateUtil.isValid(examRule.getMarkOptions())) {
							for (Integer markOption : examRule.getMarkOptions()) {
								if (markOption != 2 && markOption != 3) {
									throw new MyException("参数错误：examRule.markOptions");
								}
							}
						}
					} else { // 否则不能有阅卷选项
						if (ValidateUtil.isValid(examRule.getMarkOptions())) {
							throw new MyException("参数错误：examRule.markOptions");
						}
					}
					
					if ((examRule.getQuestionType() == 1 // 单选多选判断不能是主观题
							|| examRule.getQuestionType() == 2 || examRule.getQuestionType() == 4)) {
						if (examRule.getMarkType() == 2) {
							throw new MyException("参数错误：examRule.MarkType");
						}
					}
					if (examRule.getQuestionType() == 2) {// 如果是多选，必须有子分数（漏选分值）
						if (!ValidateUtil.isValid(examRule.getScores())) {
							throw new MyException("参数错误：examRule.scores");
						}
						if (examRule.getScores().length != 1) {
							throw new MyException("参数错误：examRule.scores");
						}
						if (examRule.getScore().doubleValue() <= examRule.getScores()[0].doubleValue()) {// 漏选分数不能大于分数
							throw new MyException("参数错误：examRule.scores");
						}
					}
					if (examRule.getQuestionType() != 2) {// 如果是单选判断不能有子分数，填空问答平均分配分数
						if (ValidateUtil.isValid(examRule.getScores())) {
							throw new MyException("参数错误：examRule.scores");
						}
					}
					
					if (examRule.getType() == 2) {
						ruleNo++;
					}
					int validQuestionNum = 0;// 符合当前抽题规则的有效题数
					for (Question question : questionListCache.get(examRule.getQuestionTypeId())) {
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
						throw new MyException(String.format("第【%s】个规则题数不足%s，请修改", ruleNo, examRule.getNum()));
					}
				}
			}
		}
	}
	
	private void publishValidExam(ExamInfo examInfo) {
		Exam exam = examInfo.getExam();
		if (ValidateUtil.isValid(exam.getId())) {
			Exam entity = getEntity(exam.getId());
			if (entity.getTimeType() == 1 && entity.getStartTime().getTime() <= System.currentTimeMillis()) {
				throw new MyException("考试已开始");
			}
//			if (entity.getTimeType() == 1 && entity.getEndTime().getTime() <= System.currentTimeMillis()) {
//				throw new MyException("考试已结束");
//			}
//			if (entity.getMarkType() == 3) {
//				throw new MyException("已阅卷");
//			}
		}
		
		if (!ValidateUtil.isValid(exam.getName())) {
			throw new MyException("参数错误：exam.name");
		}
		
		BigDecimal paperTotalScore = BigDecimal.ZERO;// 试卷总分
		boolean containObjectiveQuesiton = false;// 包含主观试题
		{
			if (exam.getGenType() == 1) {
				if (!ValidateUtil.isValid(examInfo.getExamQuestions())) {
					throw new MyException("参数错误：examQuestions");
				}
				for (ExamQuestionEx examQuestion : examInfo.getExamQuestions()) {
					if (ExamUtil.hasQuestion(examQuestion)) {
						paperTotalScore = BigDecimalUtil.newInstance(paperTotalScore).add(examQuestion.getScore()).getResult();// 单题分数从组卷后定制的分数中取
						if (QuestionUtil.hasObjective(examQuestion.getQuestion())) {
							containObjectiveQuesiton = true;
						}
					}
				}
			}
			if (exam.getGenType() == 2) {
				if (!ValidateUtil.isValid(examInfo.getExamRules())) {
					throw new MyException("参数错误：examRules");
				}
				for (ExamRule examRule : examInfo.getExamRules()) {
					if (examRule.getType() == 2) {
						paperTotalScore = BigDecimalUtil.newInstance(examRule.getScore()).mul(examRule.getNum()).add(paperTotalScore).getResult();// 单题分数*数量累加到总分
						if (examRule.getMarkType() == 2) {
							containObjectiveQuesiton = true;
						}
					}
				}
			}
		}
		{
			if (!ValidateUtil.isValid(exam.getTimeType()) 
					|| (exam.getTimeType() != 1 && exam.getTimeType() != 2)) {
				throw new MyException("参数错误：exam.timeType");
			}
			if (exam.getTimeType() == 1) {// 如果是限时，考试时间不能缺失
				if (!ValidateUtil.isValid(exam.getStartTime())) {
					throw new MyException("参数错误：exam.startTime");
				}
				if (!ValidateUtil.isValid(exam.getEndTime())) {
					throw new MyException("参数错误：exam.endTime");
				}
				if (exam.getStartTime().getTime() >= exam.getEndTime().getTime()) {
					throw new MyException("参数错误：exam.endTime");
				}
				if (exam.getEndTime().getTime() <= System.currentTimeMillis()) {// 考试结束时间必须大于当前时间
					throw new MyException("参数错误：exam.endTime");
				}
				if (containObjectiveQuesiton) {// 如果包含主观题，阅卷时间不能缺失
					if (!ValidateUtil.isValid(exam.getMarkStartTime())) {
						throw new MyException("参数错误：exam.markStartTime");
					}
					if (!ValidateUtil.isValid(exam.getMarkEndTime())) {
						throw new MyException("参数错误：exam.markEndTime");
					}
					if (exam.getMarkStartTime().getTime() >= exam.getMarkEndTime().getTime()) {
						throw new MyException("参数错误：exam.markEndTime");
					}
					if (exam.getEndTime().getTime() >= exam.getMarkStartTime().getTime()) {// 考试结束时间必须小于阅卷开始时间
						throw new MyException("参数错误：exam.markStartTime");
					}
				} else {
					if (ValidateUtil.isValid(exam.getMarkStartTime())) {
						throw new MyException("参数错误：exam.markStartTime");
					}
					if (ValidateUtil.isValid(exam.getMarkEndTime())) {
						throw new MyException("参数错误：exam.markEndTime");
					}
				}
			}
			if (exam.getTimeType() == 2) {// 如果是不限时，考试时间和阅卷时间无效
				if (ValidateUtil.isValid(exam.getStartTime())) {
					throw new MyException("参数错误：exam.startTime");
				}
				if (ValidateUtil.isValid(exam.getEndTime())) {
					throw new MyException("参数错误：exam.endTime");
				}
				if (ValidateUtil.isValid(exam.getMarkStartTime())) {
					throw new MyException("参数错误：exam.markStartTime");
				}
				if (ValidateUtil.isValid(exam.getMarkEndTime())) {
					throw new MyException("参数错误：exam.markEndTime");
				}
			}
		}
		
		if (!ValidateUtil.isValid(exam.getScoreState()) 
				|| (exam.getScoreState() != 1 && exam.getScoreState() != 2)) {
			throw new MyException("参数错误：exam.scoreState");
		}
		if (!ValidateUtil.isValid(exam.getRankState()) 
				|| (exam.getRankState() != 1 && exam.getRankState() != 2)) {
			throw new MyException("参数错误：exam.rankState");
		}
		if (!ValidateUtil.isValid(exam.getAnonState()) 
				|| (exam.getAnonState() != 1 && exam.getAnonState() != 2)) {
			throw new MyException("参数错误：exam.anonState");
		}
		if (!ValidateUtil.isValid(exam.getShowType()) 
				|| (exam.getShowType() != 1 && exam.getShowType() != 3)) {
			throw new MyException("参数错误：exam.showType");
		}
		if (!ValidateUtil.isValid(exam.getGenType()) 
				|| (exam.getGenType() != 1 && exam.getGenType() != 2)) {
			throw new MyException("参数错误：exam.genType");
		}
		if (!ValidateUtil.isValid(exam.getState()) 
				|| (exam.getState() != 1 && exam.getState() != 2)) {
			throw new MyException("参数错误：exam.state");
		}
		{
			if (!ValidateUtil.isValid(exam.getMarkType()) 
					|| (exam.getMarkType() != 1 && exam.getMarkType() != 2)) {
				throw new MyException("参数错误：exam.markType");
			}
			if (containObjectiveQuesiton && exam.getMarkType() != 2) {
				throw new MyException("参数错误：exam.markType");
			}
			if (!containObjectiveQuesiton && exam.getMarkType() != 1) {
				throw new MyException("参数错误：exam.markType");
			}
		}
		if (!ValidateUtil.isValid(exam.getTotalScore()) //总分无效
				|| exam.getTotalScore().doubleValue() <= 0 //总分小于0
				|| exam.getTotalScore().doubleValue() != paperTotalScore.doubleValue()) {//总分和卷面分数不相等
			throw new MyException("参数错误：exam.totalScore");
		}
		
		if (!ValidateUtil.isValid(exam.getTotalScore()) // 及格分数无效
				|| exam.getPassScore().doubleValue() < 0 // 小于0
				|| exam.getPassScore().doubleValue() > exam.getTotalScore().doubleValue()) {// 大于总分
			throw new MyException("参数错误：exam.passScore");
		}
	}

	private Map<Integer, List<Question>> publishHandle(ExamInfo examInfo) {
		if (examInfo.getExam().getGenType() == 1) {// 如果是人工组卷
			for (ExamQuestionEx examQuestion : examInfo.getExamQuestions()) {
				if (ExamUtil.hasQuestion(examQuestion)) {// 如果是试题
					examQuestion.setScore(examQuestion.getQuestion().getScore());// 把临时保存的分数等信息保存到试卷上
					examQuestion.setScores((examQuestion.getQuestion().getAnswerScores()));
					examQuestion.setMarkOptions(examQuestion.getQuestion().getMarkOptions());
				
					if (ValidateUtil.isValid(examQuestion.getQuestion().getId())) {// 如果是从题库抽的题
						Question questionOfDB = questionService.getEntity(examQuestion.getQuestion().getId());
						try {
							BeanUtils.copyProperties(examQuestion.getQuestion(), questionOfDB);// 重新查一遍数据库
						} catch (Exception e) {
							throw new MyException("拷贝数据失败");
						}
					}
				}
			}
			return null;
		} 
		
		Map<Integer, List<Question>> questionListCache = new HashMap<>();
		if (examInfo.getExam().getGenType() == 2) {// 如果是随机组卷
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
