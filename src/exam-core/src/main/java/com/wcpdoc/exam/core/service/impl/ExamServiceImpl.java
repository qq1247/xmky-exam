package com.wcpdoc.exam.core.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.base.service.OrgService;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.BigDecimalUtil;
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.cache.AutoMarkCache;
import com.wcpdoc.exam.core.dao.ExamDao;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.ExamType;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyExamDetail;
import com.wcpdoc.exam.core.entity.MyMark;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.entity.PaperQuestionAnswer;
import com.wcpdoc.exam.core.entity.PaperQuestionRule;
import com.wcpdoc.exam.core.entity.PaperQuestionRuleEx;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.ExamTypeService;
import com.wcpdoc.exam.core.service.MyExamDetailService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyMarkService;
import com.wcpdoc.exam.core.service.PaperQuestionAnswerService;
import com.wcpdoc.exam.core.service.PaperQuestionRuleService;
import com.wcpdoc.exam.core.service.PaperQuestionService;
import com.wcpdoc.exam.core.service.PaperService;
import com.wcpdoc.exam.core.service.PaperTypeService;
import com.wcpdoc.exam.core.service.QuestionAnswerService;
import com.wcpdoc.exam.core.service.QuestionService;

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
	private OrgService orgService;
	@Resource
	private ExamTypeService examTypeService;
	@Resource
	private MyExamService myExamService;
	@Resource
	private MyExamDetailService myExamDetailService;
	@Resource
	private MyMarkService myMarkService;
	@Resource
	private UserService userService;
	@Resource
	private PaperQuestionService paperQuestionService;
	@Resource
	private PaperQuestionRuleService paperQuestionRuleService;
	@Resource
	private QuestionAnswerService questionAnswerService;
	@Resource
	private PaperQuestionAnswerService paperQuestionAnswerService;
	
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
//		if (exam.getStartTime().getTime() <= new Date().getTime()) {
//			throw new MyException("考试开始时间必须大于当前时间");
//		}
		if (exam.getStartTime().getTime() >= exam.getEndTime().getTime()) {
			throw new MyException("考试结束时间必须大于考试开始时间");
		}

		Paper paper = paperService.getEntity(exam.getPaperId());
		if (paper.getState() != 1) {
			throw new MyException("试卷未发布");
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
		if (exam.getStartTime().getTime() >= exam.getEndTime().getTime()) {
			throw new MyException("考试结束时间必须大于考试开始时间");
		}

		Paper paper = paperService.getEntity(exam.getPaperId());
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
		
		//添加考试
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
		Date curTime = new Date();
		Exam exam = examDao.getEntity(id);
		ExamType examType = examTypeService.getEntity(exam.getExamTypeId());
		if(examType.getCreateUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("无操作权限");
		}
		
		if(exam.getState() == 1 && exam.getStartTime().getTime() >= curTime.getTime() && exam.getEndTime().getTime() <= curTime.getTime()) {
			throw new MyException("考试未结束");
		}
		
		exam.setState(0);
		exam.setUpdateTime(new Date());
		exam.setUpdateUserId(getCurUser().getId());
		update(exam);
	}
	
	@Override
	public void publish(Integer id) {
		// 校验数据有效性
		Exam exam = examDao.getEntity(id);
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
		if (exam.getStartTime().getTime() >= exam.getEndTime().getTime()) {
			throw new MyException("考试结束时间必须大于考试开始时间");
		}
		Paper paper = paperService.getEntity(exam.getPaperId());
		if (paper.getMarkType() == 2) {// 如果是自动阅卷类型，没有阅卷开始时间和阅卷结束时间
			if (exam.getMarkStartTime().getTime() <= exam.getEndTime().getTime()) {
				throw new MyException("阅卷开始时间必须大于考试结束时间");
			}
			if (exam.getMarkStartTime().getTime() >= exam.getMarkEndTime().getTime()) {
				throw new MyException("阅卷结束时间必须大于阅卷开始时间");
			}
		}
		
		String[] examUserIds = new String[0];
		Integer[] markUserIds = new Integer[0];
		if (paper.getMarkType() == 2) {//人工阅卷
			List<MyMark> myMarkList = myMarkService.getList(exam.getId());
			if (myMarkList != null && myMarkList.size() > 0 ) {
				examUserIds = new String[myMarkList.size()];
				markUserIds = new Integer[myMarkList.size()];
				for (int i = 0; i < myMarkList.size(); i++) {
					markUserIds[i] = myMarkList.get(i).getMarkUserId();
					examUserIds[i] = myMarkList.get(i).getExamUserIds().substring(1, myMarkList.get(i).getExamUserIds().length()-1);
				}
			}
		}

		List<MyExam> myExamList = myExamService.getList(exam.getId()) ;//看看有没有考试人
		if (myExamList != null && myExamList.size() > 0 ) {//如果有删除重新添加试题
			if (paper.getMarkType() == 1) {
				examUserIds = new String[myExamList.size()];
			}

			for(int i = 0; i < myExamList.size(); i++){
				if (paper.getMarkType() == 1) {
					examUserIds[i] = myExamList.get(i).getUserId().toString();
				}
				
				myExamService.del(myExamService.getEntity(myExamList.get(i).getExamId(), myExamList.get(i).getUserId()).getId()); //不修改状态，直接删除 无用的数据
				myExamDetailService.del(myExamList.get(i).getId(), myExamList.get(i).getUserId());
			}
		}
		
		// 发布考试
		exam.setState(1);
		exam.setUpdateUserId(getCurUser().getId());
		exam.setUpdateTime(new Date());
		examDao.update(exam);
		if (exam.getId() != null  && examUserIds.length > 0) {
			assignPaper(id, examUserIds, markUserIds); //重新设置考试人 阅卷人
		}
		
		// 标记为需要监听的考试（考试结束自动阅卷）
		AutoMarkCache.put(exam.getId(), exam);
		//if (paper.getMarkType() == 2) {// 智能试卷，考试结束，定时任务就处理完成了
		//	AutoMarkCache.put(exam.getId(), exam.getMarkEndTime());// 为保证同步执行，考试定时任务执行完时，在进行添加
		//}
	}
	
	@Override
	public List<Map<String, Object>> getExamUserList(Integer id) {
		return examDao.getExamUserList(id);
	}

	@Override
	public void assignPaper(Integer id, String[] examUserIds, Integer[] markUserIds) {
		// 校验数据有效性
		Exam exam = getEntity(id);
		Paper paper = paperService.getEntity(exam.getPaperId());
		Map<Integer, List<Question>> questionListCache = null;// 试题分类下所有的试题条件
		List<PaperQuestionRule> paperRuleList = null;// 随机章节规则
		if (paper.getGenType() == 2) {// 获取随机组卷 列表
			questionListCache = new HashMap<>();
			paperRuleList = paperQuestionRuleService.getChapterList(paper.getId(), null);
			Set<Integer> questionTypeIdSet = new HashSet<>();
			for (PaperQuestionRule rule : paperRuleList) {
				if (questionTypeIdSet.contains(rule.getQuestionTypeId())) {
					continue;
				}
				questionTypeIdSet.add(rule.getQuestionTypeId());
				List<Question> questionList = paperQuestionRuleService.getQuestionList(rule.getQuestionTypeId());
				questionListCache.put(rule.getQuestionTypeId(), questionList);
			}
		}
		
		validForAssignPaper(id, examUserIds, markUserIds, exam, paper, paperRuleList, questionListCache);
		
		// 同步用户
		if (ValidateUtil.isValid(markUserIds) && markUserIds.length == 1) {
			examUserIds[0] = StringUtil.join(examUserIds);// 阅卷人只有一个需要把考试人格式修改一下
		}
		Set<Integer> examUserIdSet = new HashSet<>();
		for (String userIds : examUserIds) {
			for (String userId : userIds.split(",")) {
				examUserIdSet.add(Integer.parseInt(userId));
			}
		}
		Set<Integer> markUserIdSet = new HashSet<>();
		for (Integer userId : markUserIdSet) {
			markUserIdSet.add(userId);
		}
		
		synExamUserForAssignPaper(id, examUserIdSet);
		
		// 分配试卷
		List<PaperQuestion> myQuestionList = null;// 我的试题列表
		if (paper.getGenType() == 1) {
			myQuestionList = paperService.getPaperQuestionList(exam.getPaperId());
		}
		
		for(Integer userId : examUserIdSet){
			MyExam myExam = new MyExam();
			myExam.setExamId(id);
			myExam.setUserId(userId);
			myExam.setState(1);// 未考试
			myExam.setMarkState(1);// 未阅卷
			myExam.setUpdateTime(new Date());
			myExam.setUpdateUserId(getCurUser().getId());
			myExamService.add(myExam);// 添加我的考试
			
			if (paper.getMarkType() == 2) {// 如果试卷是人工试卷，添加阅卷用户
				synMarkUserForAssignPaper(id, examUserIds, markUserIds);
			}
			
			if (exam.getState() != 1) {// 没发布，不生成试卷（不确定使用的那个试卷）
				continue;
			}
			
			if (paper.getGenType() == 2) {// 如果是随机组卷，重新生成试卷
				myQuestionList = generatePaper(paperRuleList, questionListCache, userId, myExam.getExamId());
			}
			
			for (PaperQuestion paperQuestion : myQuestionList) {
				MyExamDetail myExamDetail = new MyExamDetail();
				myExamDetail.setMyExamId(myExam.getId());
				myExamDetail.setExamId(myExam.getExamId());
				myExamDetail.setUserId(myExam.getUserId());
				myExamDetail.setQuestionId(paperQuestion.getQuestionId());
				myExamDetail.setQuestionScore(paperQuestion.getScore());
				myExamDetailService.add(myExamDetail);// 添加我的考试详细
			}
		}
	}

	private List<PaperQuestion> generatePaper(List<PaperQuestionRule> paperRuleList, Map<Integer, List<Question>> questionListCache, Integer userId, Integer examId) {
		// 试题打乱顺序
		Set<Integer> usedQuestion = new HashSet<Integer>();
		for(List<Question> questionList : questionListCache.values()) {
	        Collections.shuffle(questionList);
	    }
		
		// 生成试卷
		List<PaperQuestion> randPaper = new ArrayList<>();
		for(PaperQuestionRule paperQuestionRule : paperRuleList) {// 循环所有规则
			Integer ruleRemainNum = paperQuestionRule.getNum();
			for (int i = 0; i < paperQuestionRule.getNum(); i++) {// 循环单个规则试题数量
				for(Question question : questionListCache.get(paperQuestionRule.getQuestionTypeId())) {// 循环当前规则下试题分类的所有试题（已乱序）
					if (ruleRemainNum <= 0) {// 该规则下试题都找到，处理下一个规则
						break;
					}
					if (paperQuestionRule.getType().intValue() != question.getType().intValue()
							|| !paperQuestionRule.getDifficultys().contains(question.getDifficulty().toString()) 
							|| !paperQuestionRule.getAis().contains(question.getAi().toString()) ) {// 当前试题不符合规则，不添加
							continue;
					}
					
					if (usedQuestion.contains(question.getId())) {// 添加过，不添加
						continue;
					}
					
					PaperQuestion paperQuestion = new PaperQuestion();
					paperQuestion.setUpdateTime(new Date());
					paperQuestion.setUpdateUserId(getCurUser().getId());
					paperQuestion.setQuestionId(question.getId());
					paperQuestion.setType(3);
					paperQuestion.setNo(i + 1);
					paperQuestion.setScore(paperQuestionRule.getScore());
					paperQuestion.setAiOptions(paperQuestionRule.getAiOptions());
					paperQuestion.setParentId(paperQuestionRule.getPaperQuestionId());
					paperQuestion.setPaperId(paperQuestionRule.getPaperId());
					paperQuestion.setExamId(examId);
					paperQuestion.setUserId(userId);
					paperQuestion.setPaperQuestionRuleId(paperQuestionRule.getId());
					paperQuestionService.add(paperQuestion);// 添加试题
					
					paperQuestion.setParentSub(String.format("_%s_%s_", paperQuestion.getParentId(), paperQuestion.getId()));
					paperQuestionService.update(paperQuestion);

					usedQuestion.add(question.getId());
					randPaper.add(paperQuestion);

					
					//添加试题答案
					BigDecimal total = new BigDecimal(0.00);
					List<QuestionAnswer> questionAnswerList = questionAnswerService.getList(question.getId());//获取试题答案
					
					if (!ValidateUtil.isValid(questionAnswerList)) {// 计算每个题多少分
						continue;
					}
					int listSize = questionAnswerList.size();
					List<BigDecimal> singleScoreList = new ArrayList<>();
					BigDecimal singleScore = BigDecimalUtil.newInstance(paperQuestionRule.getScore()).div(listSize, 3).getResult();
					for (int j = 1; j < listSize; j++) {
						singleScoreList.add(singleScore);
					}
					singleScoreList.add(BigDecimalUtil.newInstance(singleScore).mul(listSize - 1).sub(paperQuestionRule.getScore()).mul(-1).getResult());
					for(int j = 0; j < questionAnswerList.size(); j++ ){
						if (question.getType() == 1 || question.getType() == 4 ) {
							PaperQuestionAnswer paperQuestionAnswer = new PaperQuestionAnswer();
							paperQuestionAnswer.setAnswer(questionAnswerList.get(j).getAnswer());
							if (question.getAi() == 1 && paperQuestionRule.getScore() != null) {
								paperQuestionAnswer.setScore(singleScoreList.get(j));
							}else{
								paperQuestionAnswer.setScore(new BigDecimal(0));
							}
							paperQuestionAnswer.setPaperId(paperQuestionRule.getPaperId());
							paperQuestionAnswer.setPaperQuestionId(paperQuestion.getId());
							paperQuestionAnswer.setQuestionId(question.getId());
							paperQuestionAnswer.setNo(j+1);
							paperQuestionAnswerService.add(paperQuestionAnswer);
							total = paperQuestionRule.getScore();
						} else if (question.getType() == 2) {
							PaperQuestionAnswer paperQuestionAnswer = new PaperQuestionAnswer();
							if (question.getAi() == 1 && questionAnswerList.get(j).getAnswer() != null) {
								paperQuestionAnswer.setScore(singleScoreList.get(j).divide(new BigDecimal(2)));
							}else{
								paperQuestionAnswer.setScore(new BigDecimal(0));
							}
							paperQuestionAnswer.setAnswer(questionAnswerList.get(j).getAnswer());
							paperQuestionAnswer.setPaperId(paperQuestionRule.getPaperId());
							paperQuestionAnswer.setPaperQuestionId(paperQuestion.getId());
							paperQuestionAnswer.setQuestionId(question.getId());
							paperQuestionAnswer.setNo(j+1);
							paperQuestionAnswerService.add(paperQuestionAnswer);
							total = paperQuestionRule.getScore();
						} else if (question.getType() == 3 || question.getType() == 5) {
							PaperQuestionAnswer paperQuestionAnswer = new PaperQuestionAnswer();
							
							if (question.getAi() == 1 && questionAnswerList.get(j).getAnswer() != null) {
								paperQuestionAnswer.setScore(singleScoreList.get(j));
							}else{
								paperQuestionAnswer.setScore(new BigDecimal(0));
							}
							paperQuestionAnswer.setAnswer(questionAnswerList.get(j).getAnswer());
							paperQuestionAnswer.setPaperId(paperQuestionRule.getPaperId());
							paperQuestionAnswer.setPaperQuestionId(paperQuestion.getId());
							paperQuestionAnswer.setQuestionId(question.getId());
							paperQuestionAnswer.setNo(j+1);
							paperQuestionAnswerService.add(paperQuestionAnswer);
							total = total.add(singleScoreList.get(j));
						}
					}
					if (question.getAi() == 1 && paperQuestionRule.getScore().compareTo(total) != 0) {
						throw new MyException("答案总分有误 ");
					}
					ruleRemainNum--;
				}
			}
		}
		
		return randPaper;
	}

	private void synMarkUserForAssignPaper(Integer id, String[] examUserIds, Integer[] markUserIds) {
		List<MyMark> myMarkList = myMarkService.getList(id);// 获取我的考试列表
		for (MyMark myMark : myMarkList) {
			myMarkService.del(myMark.getId());
		}
		
		for (int i = 0; i < markUserIds.length; i++) {
			MyMark myMark = new MyMark();
			myMark.setMarkUserId(markUserIds[i]);
			myMark.setExamUserIds(String.format(",%s,", examUserIds[i]));
			myMark.setUpdateUserId(getCurUser().getId());
			myMark.setUpdateTime(new Date());
			myMark.setExamId(id);
			myMarkService.add(myMark);
		}
	}
	
	private void synExamUserForAssignPaper(Integer id, Set<Integer> examUserIdSet) {
		List<MyExam> myExamList = myExamService.getList(id);// 当前考试的人员
		ListIterator<MyExam> myExamListIterator = myExamList.listIterator();
		
		while (myExamListIterator.hasNext()) {// 同步考试人员信息
			/**
			 * 页面：1,2,3
			 * 数据库：2,3,4
			 * 1添加；4删除；2,3不动
			 */
			MyExam myExam = myExamListIterator.next();
			if (examUserIdSet.contains(myExam.getUserId())) {// 页面有数据库有，不处理
				myExamListIterator.remove();
				examUserIdSet.remove(myExam.getUserId());
			} else {// 页面没有数据库有，删除数据库数据
				myExamDetailService.del(myExam.getExamId(), myExam.getUserId());
				myExamService.del(myExam.getId());
				paperQuestionService.del(myExam.getExamId(), myExam.getUserId());//如果试卷时随机组卷 删除随机试题
			}
		}
	}

	private void validForAssignPaper(Integer id, String[] examUserIds, Integer[] markUserIds, Exam exam, Paper paper, List<PaperQuestionRule> paperRuleList, Map<Integer, List<Question>> questionListCache) {
		if (id == null) {
			throw new MyException("参数错误：id");
		}
		
		if (examUserIds == null) {
			throw new MyException("参数错误：examUserIds");
		}
		
		if (exam.getState() == 0) {
			throw new MyException("考试已删除");
		}
		if (exam.getState() == 3) {
			throw new MyException("考试已归档");
		}
		if (exam.getEndTime().getTime() < System.currentTimeMillis()) {
			throw new MyException("考试已结束");
		}
		
		if (paper.getMarkType() == 2) {// 如果试卷是智能阅卷类型，没有阅卷用户
			if (markUserIds == null) {
				throw new MyException("参数错误：markUserIds");
			}
			
			if (markUserIds.length != 1 && examUserIds.length != markUserIds.length) {
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
				throw new MyException("考试用户重复");
			}
		}
		
		if (paper.getGenType().intValue() == 2) {
			Map<PaperQuestionRuleEx, Integer> ruleExNumCache = new HashMap<>();
			for (Integer questionTypeId : questionListCache.keySet()) {
				for (Question question : questionListCache.get(questionTypeId)) {
					PaperQuestionRuleEx paperQuestionRuleEx = new PaperQuestionRuleEx(question.getType(), question.getDifficulty(), question.getAi());
					if (ruleExNumCache.get(paperQuestionRuleEx) == null) {
						ruleExNumCache.put(paperQuestionRuleEx, 1);
					} else {
						ruleExNumCache.put(paperQuestionRuleEx, ruleExNumCache.get(paperQuestionRuleEx) + 1); // 
					}
				}
			}
			
			for (PaperQuestionRule paperRule : paperRuleList) {
				Integer paperRuleNum = paperRule.getNum();
				Set<PaperQuestionRuleEx> keySet = ruleExNumCache.keySet();
				for(PaperQuestionRuleEx paperQuestionRuleEx : keySet){
					if (paperRule.getType() == paperQuestionRuleEx.getType() 
							&& paperRule.getDifficultys().contains(paperQuestionRuleEx.getDifficulty().toString()) 
							&& paperRule.getAis().contains(paperQuestionRuleEx.getAi().toString()) ) { //如果包含
						paperRuleNum = paperRuleNum - ruleExNumCache.get(paperQuestionRuleEx);
						if (paperRuleNum <= 0) {
							break;
						}
					}
				}
				
				if (paperRuleNum > 0) {
					PaperQuestion paperQuestion = paperQuestionService.getEntity(paperRule.getPaperQuestionId());
					throw new MyException(String.format("【%s】章节下第【%s】个规则题数不足【%s】道", paperQuestion.getName(), paperRule.getNo(), paperRule.getNum()));
				}
			}
		}
	}
	
	@Override
	public List<Exam> getList(Integer examTypeId) {
		return examDao.getList(examTypeId);
	}

	@Override
	public List<Map<String, Object>> getMarkExamUserList(Integer id, Integer markUserId) {
		return examDao.getMarkExamUserList(id, markUserId);
	}

	@Override
	public List<Map<String, Object>> getMarkQuestionList(Integer id, Integer markUserId) {
		return examDao.getMarkQuestionList(id, markUserId);
	}

	@Override
	public List<Exam> getUnMarkList() {
		return examDao.getUnMarkList();
	}

	@Override
	public List<Exam> getList() {
		return examDao.getList();
	}
	
	
	public static void main(String[] args) {
		BigDecimal divide = new BigDecimal(1).divide(new BigDecimal(3), 2, BigDecimal.ROUND_FLOOR);
		System.err.println(divide);
	}
	
	
}
