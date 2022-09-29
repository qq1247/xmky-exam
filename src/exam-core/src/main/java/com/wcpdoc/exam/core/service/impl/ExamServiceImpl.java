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
import com.wcpdoc.core.util.StringUtil;
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

	@Override
	public void addEx(ExamInfo examInfo) {
		/**
		 *  如果是人工组卷且从题库选择的试题，重新查一遍数据库，不要依赖前端，用于检测包含主观题等
		 *  如果是随机组卷，返回需要的题库，用于随机抽题等
		 */
		Map<Integer, List<Question>> questionListCache = addHandle(examInfo);
		
		/**
		 * 校验数据有效性，数据结构如下：
		 * 
		 * {
		 * "exam":{"name":"期末考试","markType":1,"genType":1,"totalScore":100},
		 * "examQuestions": [
		 *    {"type":1,"chapterName":"单选题","chapterTxt":"每题2分，一共10题20分"},
		 *    {"type":2,"socre":2,"scores":1,"question":{"id":2,"title":"这是一道多选题"}}
		 *  ],
		 * "examUsers": [
		 *    {"examUserIds":[1,2],"markUserId":5},
		 *    {"examUserIds":[3,4],"markUserId":5}
		 *  ],
		 *  "examRules": [
		 *    {"questionTypeId":2,"type":2,"markTypes":1,"markOptions":[2,3],"num":10,"score":2},
		 *  ]
		 * }
		 */
		addExValid(examInfo, questionListCache);

		// 添加考试信息
		addExExam(examInfo);
		
		// 保存试卷信息
		addExPaper(examInfo, questionListCache);
		
		// 分配试卷到用户
		addExUser(examInfo, questionListCache);
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
	public void publish(Integer id) {
		
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
	
	private void addExExam(ExamInfo examInfo) {
		Exam exam = examInfo.getExam();
		exam.setMarkState(1);// 标记为未阅卷（考试时间结束，定时任务自动阅卷，标记为已阅）
		exam.setUpdateUserId(getCurUser().getId());
		exam.setUpdateTime(new Date());
		add(exam);
	}
	
	private void addExPaper(ExamInfo examInfo, Map<Integer, List<Question>> questionListCache) {
		// 如果是人工试卷，保存试卷模板
		if (examInfo.getExam().getGenType() == 1) {
			for (int i = 0; i < examInfo.getExamQuestions().length; i++) {
				ExamQuestionEx examQuestion = examInfo.getExamQuestions()[i];
				if (ExamUtil.hasQuestion(examQuestion)) {
					QuestionEx questionEx = examQuestion.getQuestionEx();
					if (!ValidateUtil.isValid(questionEx.getId())) {// 如果是从页面导入的新题，先保存
						questionService.addEx(questionEx, questionEx.getMarkOptionArr(), questionEx.getOptions(), questionEx.getAnswers(), questionEx.getAnswerScores());
					}
					examQuestion.setQuestionId(questionEx.getId());
				}
				examQuestion.setExamId(examInfo.getExam().getId());
				examQuestion.setNo(i + 1);
				examQuestion.setUpdateUserId(getCurUser().getId());
				examQuestion.setUpdateTime(new Date());
				examQuestionService.add(examQuestion);
			}
		}
		// 如果是随机试卷，保存抽题规则
		else if (examInfo.getExam().getGenType() == 2) {
			for (ExamRule examRule : examInfo.getExamRules()) {
				examRuleService.add(examRule);
			}
		}
	}
	
	private void addExUser(ExamInfo examInfo, Map<Integer, List<Question>> questionListCache) {
		// 获取用户列表
		Map<Integer, List<QuestionOption>> questionOptionCache = new HashMap<>();
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
							List<QuestionOption> questionOptionList = questionOptionCache.get(myQuestion.getQuestionId());// A,B,C,D
							if (questionOptionList == null) {
								questionOptionList = questionOptionService.getList(myQuestion.getQuestionId());
								questionOptionCache.put(myQuestion.getQuestionId(), questionOptionList);
							}
							myQuestion.setOptionNo(StringUtil.join(shuffleNums(1, questionOptionList.size())));// D,B,A,C
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
							
							ExamQuestion examQuestion = new ExamQuestion();
							examQuestion.setType(2);
							examQuestion.setScore(examRule.getScore());
							addExUserScores(examQuestion, examRule, question);
							examQuestion.setMarkOptions(examRule.getMarkOptions());
							examQuestion.setExamId(examRule.getExamId());
							examQuestion.setQuestionId(question.getId());
							examQuestion.setNo(no++);
							examQuestion.setUpdateTime(new Date());
							examQuestion.setUpdateUserId(getCurUser().getId());
							examQuestionService.add(examQuestion);
						}
					}
				}
			}
		}
	}

	private void addExValid(ExamInfo examInfo, Map<Integer, List<Question>> questionListCache) {
		// 校验考试信息
		addValidExam(examInfo);
		// 校验试卷信息
		addValidPaper(examInfo, questionListCache);
		// 校验考试用户信息
		addValidExamUser(examInfo);
	}

	private void addValidExamUser(ExamInfo examInfo) {
		ExamUser[] examUsers = examInfo.getExamUsers();
		Set<Integer> examUserIdCache = new HashSet<>();
		Set<Integer> markUserIdCache = new HashSet<>();
		for (ExamUser examUser : examUsers) {
			if (examInfo.getExam().getMarkType() == 1 && ValidateUtil.isValid(examUser.getMarkUserId())) {// 如果自动阅卷，不需要阅卷用户
				throw new MyException("参数错误：examUserList.markUserId");
			}
			if (examInfo.getExam().getMarkType() == 2 && ValidateUtil.isValid(examUser.getMarkUserId())) {// 如果人工阅卷，需要阅卷用户
				throw new MyException("参数错误：examUserList.markUserId");
			}
			if (!ValidateUtil.isValid(examUser.getExamUserIds())) {
				throw new MyException("参数错误：examUserList.examUserIds");
			}
			
			for (Integer examUserId : examUser.getExamUserIds()) {
				if (examUserIdCache.contains(examUserId)) {
					throw new MyException("参数错误：examUserList.examUserIds重复");
				}
				examUserIdCache.add(examUserId);
			}
			
			if (ValidateUtil.isValid(examUser.getMarkUserId())) {
				if (markUserIdCache.contains(examUser.getMarkUserId())) {
					throw new MyException("参数错误：examUserList.markUserId重复");
				}
				markUserIdCache.add(examUser.getMarkUserId());
			}
		}
	}

	private void addValidPaper(ExamInfo examInfo, Map<Integer, List<Question>> questionListCache) {
		if (examInfo.getExam().getGenType() == 1) {
			for (ExamQuestionEx examQuestion : examInfo.getExamQuestions()) {
				if (examQuestion.getType() != 1 && examQuestion.getType() != 2) {
					throw new MyException("参数错误：examQuestion.type");
				}
				if (examQuestion.getType() == 1) {
					if (!ValidateUtil.isValid(examQuestion.getChapterName())) {
						throw new MyException("参数错误：examQuestion.chapterName");
					}
					if (ValidateUtil.isValid(examQuestion.getScore())) {
						throw new MyException("参数错误：examQuestion.score");
					}
					if (ValidateUtil.isValid(examQuestion.getScores())) {
						throw new MyException("参数错误：examQuestion.scores");
					}
					if (ValidateUtil.isValid(examQuestion.getMarkOptions())) {
						throw new MyException("参数错误：examQuestion.markOptions");
					}
					if (examQuestion.getQuestionEx() != null) {
						throw new MyException("参数错误：examQuestion.question");
					}
				}
				if (examQuestion.getType() == 2) {
					if (ValidateUtil.isValid(examQuestion.getChapterName())) {
						throw new MyException("参数错误：examQuestion.chapterName");
					}
					if (ValidateUtil.isValid(examQuestion.getChapterTxt())) {
						throw new MyException("参数错误：examQuestion.chapterTxt");
					}
					
					if (!ValidateUtil.isValid(examQuestion.getScore())) {
						throw new MyException("参数错误：examQuestion.score");
					}
					if (!ValidateUtil.isValid(examQuestion.getScores())) {
						throw new MyException("参数错误：examQuestion.scores");
					}
					if (examQuestion.getQuestionEx() == null) {
						throw new MyException("参数错误：examQuestion.question");
					}
				}
			}
		}
		
		if (examInfo.getExam().getGenType() == 2) {
			Set<Question> questionOfUsed = new HashSet<>();// 已使用过的试题
			for (int i = 0; i < examInfo.getExamRules().length; i++) {
				ExamRule examRule = examInfo.getExamRules()[i];
				if (!ValidateUtil.isValid(examRule.getQuestionTypeId())) {
					throw new MyException("参数错误：examRule.questionTypeId");
				}
				if (!ValidateUtil.isValid(examRule.getType())) {
					throw new MyException("参数错误：examRule.type");
				}
				if (!ValidateUtil.isValid(examRule.getMarkType())) {
					throw new MyException("参数错误：examRule.markType");
				}
				if (!ValidateUtil.isValid(examRule.getNum())) {
					throw new MyException("参数错误：examRule.num");
				}
				if (!ValidateUtil.isValid(examRule.getScore())) {
					throw new MyException("参数错误：examRule.score");
				}
				
				if ((examRule.getType() == 1 || examRule.getType() == 2 || examRule.getType() == 4)) {// 单选多选判断不能是主观题
					if (examRule.getMarkType() == 2) {
						throw new MyException("参数错误：examRule.MarkType");
					}
				}
				if (examRule.getType() == 2) {
					if (!ValidateUtil.isValid(examRule.getScores())) {// 如果是多选，必须有子分数（漏选分值）
						throw new MyException("参数错误：examRule.scores");
					}
					if (examRule.getScore().doubleValue() < examRule.getScores().doubleValue()) {// 子分数不能大于分数
						throw new MyException("参数错误：examRule.scores");
					}
				}
				if (examRule.getType() != 2 && ValidateUtil.isValid(examRule.getScores())) {// 如果是单选判断不能有子分数，填空问答平均分配分数
					throw new MyException("参数错误：examRule.scores");
				}
				
				
				int validQuestionNum = 0;// 符合当前抽题规则的有效题数
				for (Question question : questionListCache.get(examRule.getQuestionTypeId())) {
					if (questionOfUsed.contains(question)) {// 已经使用过的试题就不能在用，继续找下一个
						continue;
					}
					if (validQuestionNum >= examRule.getNum()) {// 当前题库已满足当前抽题规则，不在继续验证
						break;
					}
					
					if (examRule.getType() == question.getType() // 当前试题满足抽题规则
							&& examRule.getMarkType() == question.getMarkType()) {
						questionOfUsed.add(question);// 加入已使用过的试题
						validQuestionNum++; // 有效题数加一
					}
				}
				
				if (validQuestionNum < examRule.getNum()) {
					throw new MyException(String.format("第【%s】个规则题数不足，请修改", i + 1));
				}
			}
		}
	}

	private void addValidExam(ExamInfo examInfo) {
		Exam exam = examInfo.getExam();
		if (!ValidateUtil.isValid(exam.getName())) {
			throw new MyException("参数错误：exam.name");
		}
		if (exam.getScoreState() != 1 && exam.getScoreState() != 2) {
			throw new MyException("参数错误：exam.scoreState");
		}
		if (exam.getRankState() != 1 && exam.getRankState() != 2) {
			throw new MyException("参数错误：exam.rankState");
		}
		if (exam.getAnonState() != 1 && exam.getAnonState() != 2) {
			throw new MyException("参数错误：exam.anonState");
		}
		if (exam.getPassScore().doubleValue() < 0 || exam.getPassScore().doubleValue() > 100) {
			throw new MyException("参数错误：exam.passScore");
		}
		if (exam.getShowType() != 1 && exam.getShowType() != 3) {
			throw new MyException("参数错误：exam.showType");
		}
		if (exam.getState() != 1 && exam.getState() != 2) {
			throw new MyException("参数错误：exam.state");
		}
		
		if (exam.getGenType() != 1 && exam.getGenType() != 2) {
			throw new MyException("参数错误：exam.genType");
		}
		BigDecimal paperTotalScore = BigDecimal.ZERO;// 试卷总分
		boolean containObjectiveQuesiton = false;// 包含主观试题
		if (exam.getGenType() == 1) {
			if (!ValidateUtil.isValid(examInfo.getExamQuestions())) {
				throw new MyException("参数错误：examQuestions");
			}
			for (ExamQuestionEx examQuestion : examInfo.getExamQuestions()) {
				if (ExamUtil.hasQuestion(examQuestion)) {
					paperTotalScore = BigDecimalUtil.newInstance(paperTotalScore).add(examQuestion.getScore()).getResult();// 单题分数从组卷后定制的分数中取
					if (QuestionUtil.hasObjective(examQuestion.getQuestionEx())) {
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
				paperTotalScore = BigDecimalUtil.newInstance(examRule.getScore()).mul(examRule.getNum()).add(paperTotalScore).getResult();// 单题分数*数量累加到总分
				if (examRule.getMarkType() == 2) {
					containObjectiveQuesiton = true;
				}
			}
		}
		
		if (containObjectiveQuesiton && exam.getMarkType() != 2) {
			throw new MyException("参数错误：exam.markType");
		}
		if (!containObjectiveQuesiton && exam.getMarkType() != 1) {
			throw new MyException("参数错误：exam.markType");
		}
		if (!ValidateUtil.isValid(exam.getTotalScore()) //总分无效
				|| exam.getTotalScore().doubleValue() <= 0 //总分小于0
				|| exam.getTotalScore().doubleValue() != paperTotalScore.doubleValue()) {//总分和卷面分数不相等
			throw new MyException("参数错误：exam.totalScore");
		}
		if (exam.getTimeType() != 1 && exam.getTimeType() != 2) {
			throw new MyException("参数错误：exam.timeType");
		}
		if (exam.getTimeType() == 1) {// 如果是限时，考试时间不能缺失
			if (!ValidateUtil.isValid(exam.getStartTime())) {
				throw new MyException("参数错误：exam.startTime");
			}
			if (!ValidateUtil.isValid(exam.getEndTime())) {
				throw new MyException("参数错误：exam.endTime");
			}
			if (containObjectiveQuesiton) {// 如果包含主观题，阅卷时间不能缺失
				if (!ValidateUtil.isValid(exam.getMarkStartTime())) {
					throw new MyException("参数错误：exam.markStartTime");
				}
				if (!ValidateUtil.isValid(exam.getMarkEndTime())) {
					throw new MyException("参数错误：exam.markEndTime");
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
		if (ValidateUtil.isValid(exam.getEndTime())) {
			if (exam.getEndTime().getTime() <= System.currentTimeMillis()) {
				throw new MyException("参数错误：exam.endTime");
			}
		}
		if (ValidateUtil.isValid(exam.getStartTime()) && ValidateUtil.isValid(exam.getEndTime())) {
			if (exam.getStartTime().getTime() <= exam.getEndTime().getTime()) {
				throw new MyException("参数错误：exam.endTime");
			}
		}
		if (ValidateUtil.isValid(exam.getMarkStartTime()) && ValidateUtil.isValid(exam.getMarkEndTime())) {
			if (exam.getMarkStartTime().getTime() <= exam.getMarkEndTime().getTime()) {
				throw new MyException("参数错误：exam.markEndTime");
			}
		}
		if (ValidateUtil.isValid(exam.getEndTime()) && ValidateUtil.isValid(exam.getMarkStartTime())) {
			if (exam.getEndTime().getTime() <= exam.getMarkStartTime().getTime()) {
				throw new MyException("参数错误：exam.markStartTime");
			}
		}
	}

	private Map<Integer, List<Question>> addHandle(ExamInfo examInfo) {
		Map<Integer, List<Question>> questionListCache = new HashMap<>();// 题库缓存
		if (ValidateUtil.isValid(examInfo.getExamQuestions())) {
			for (ExamQuestionEx examQuestion : examInfo.getExamQuestions()) {
				if (ExamUtil.hasQuestion(examQuestion) 
						&& ValidateUtil.isValid(examQuestion.getQuestionEx().getId())) {
					Question questionOfDB = questionService.getEntity(examQuestion.getQuestionEx().getId());
					try {
						BeanUtils.copyProperties(examQuestion.getQuestionEx(), questionOfDB);
					} catch (Exception e) {
						throw new MyException("拷贝数据失败");
					}
				}
			}
		} else if (ValidateUtil.isValid(examInfo.getExamRules())) {
			for (int i = 0; i < examInfo.getExamRules().length; i++) {
				ExamRule examRule = examInfo.getExamRules()[i];
				if (!ValidateUtil.isValid(questionListCache.get(examRule.getQuestionTypeId()))) {
					List<Question> questionList = questionService.getList(examRule.getQuestionTypeId());
					questionListCache.put(examRule.getQuestionTypeId(), questionList);
				}
			}
		}
		return questionListCache;
	}

	/**
	 * 设置分数
	 * 
	 * 如果抽题不设置分数，使用题库默认的分数，会导致总分不确定
	 * 如果抽题设置分数，主观题答案数量不一样，没法按答案分配分数
	 * 所以规则为当题分数，平均分配到每个答案
	 * 
	 * v1.0 zhanghc 2022年9月28日下午1:39:17
	 * @param examQuestion
	 * @param examRule
	 * @param question void
	 */
	private void addExUserScores(ExamQuestion examQuestion, ExamRule examRule, Question question) {
		// 如果是多选，使用抽题规则的漏选分数
		if (QuestionUtil.hasMultipleChoice(question)) {
			examQuestion.setScores(examRule.getScores().toString());
		} 
		// 如果是客观填空问答，把分数平均分配到子分数
		else if ((QuestionUtil.hasFillBlank(question) || QuestionUtil.hasQA(question))
				&& QuestionUtil.hasSubjective(question)) {
			List<QuestionAnswer> questionAnswerList = questionAnswerService.getList(question.getId());
			List<BigDecimal> scoreList = splitScore(examRule.getScore(), questionAnswerList.size());
			examQuestion.setScores(scoreList.toString().substring(1, scoreList.size() - 1).replaceAll(" ", ""));
		}
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
	private List<BigDecimal> splitScore(BigDecimal score, int num) {
		List<BigDecimal> singleScoreList = new ArrayList<>();
		BigDecimal singleScore = BigDecimalUtil.newInstance(score).div(num, 2).getResult();
		for (int j = 0; j < num - 1; j++) {
			singleScoreList.add(singleScore);
		}
		singleScoreList.add(BigDecimalUtil.newInstance(singleScore).mul(num - 1).sub(score).mul(-1).getResult());
		return singleScoreList;
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
