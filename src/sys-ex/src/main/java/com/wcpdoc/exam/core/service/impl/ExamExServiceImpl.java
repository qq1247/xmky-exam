package com.wcpdoc.exam.core.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.BigDecimalUtil;
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.ExamQuestion;
import com.wcpdoc.exam.core.entity.ExamQuestionNo;
import com.wcpdoc.exam.core.entity.ExamRule;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyExamDetail;
import com.wcpdoc.exam.core.entity.MyMark;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.entity.ex.Chapter;
import com.wcpdoc.exam.core.entity.ex.MyExamChapter;
import com.wcpdoc.exam.core.entity.ex.MyQuestion;
import com.wcpdoc.exam.core.service.ExamExService;
import com.wcpdoc.exam.core.service.ExamQuestionNoService;
import com.wcpdoc.exam.core.service.ExamQuestionService;
import com.wcpdoc.exam.core.service.ExamRuleService;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.MyExamDetailService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyMarkService;
import com.wcpdoc.exam.core.service.QuestionAnswerService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.util.ExamUtil;

/**
 * 考试服务层实现
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
@Service
public class ExamExServiceImpl extends BaseServiceImp<Exam> implements ExamExService {
	@Resource
	private ExamService examService;
	@Resource
	private QuestionService questionService;
	@Resource
	private MyMarkService myMarkService;
	@Resource
	private MyExamService myExamService;
	@Resource
	private MyExamDetailService myExamDetailService;
	@Resource
	private ExamQuestionService examQuestionService;
	@Resource
	private ExamRuleService examRuleService;
	@Resource
	private QuestionAnswerService questionAnswerService;
	@Resource
	private ExamQuestionNoService examQuestionNoService;
	
	@Override
	public void setDao(BaseDao<Exam> dao) {
	}

	@Override
	public void publish(Exam exam) {
		// 生成用户试卷
		List<MyMark> myMarkList = myMarkService.getList(exam.getId());
		if (!ValidateUtil.isValid(myMarkList)) {
			return;// 先发布后添加用户，是没有数据的
		}
		
//		myExamChapter myExamChapter = paperService.getPaper(exam.getPaperId());
//		for (MyMark myMark : myMarkList) {
//			Integer[] examUserIdArr = myMark.getExamUserIdArr();
//			for(Integer userId : examUserIdArr) {
//				generateUserPaper(exam, userId, paper, myExamChapter);
//			}
//		}
	}
	
	private void generateUserPaper(Exam exam, Integer userId, MyExamChapter myExamChapter) {
		MyExam myExam = new MyExam();
		myExam.setExamId(exam.getId());
		myExam.setUserId(userId);
		myExam.setState(1);// 未考试
		myExam.setMarkState(1);// 未阅卷
		myExam.setUpdateTime(new Date());
		myExam.setUpdateUserId(getCurUser().getId());
		myExamService.add(myExam);// 添加我的考试
		
		Map<Integer, String> noMap = new LinkedHashMap<Integer, String>(); //排序
		for (Chapter chapter : myExamChapter.getChapterList()) {
			if (!ValidateUtil.isValid(chapter.getMyQuestionList())) {
				continue;// 章节下无题
			}
			
			for (int i = 0; i < chapter.getMyQuestionList().size(); i++) {
				MyQuestion myQuestion = chapter.getMyQuestionList().get(i);
				MyExamDetail myExamDetail = new MyExamDetail();
				myExamDetail.setMyExamId(myExam.getId());
				myExamDetail.setExamId(myExam.getExamId());
				myExamDetail.setUserId(myExam.getUserId());
				myExamDetail.setQuestionId(myQuestion.getQuestion().getId());
				myExamDetail.setQuestionScore(myQuestion.getAttr().getScore());// 用组卷时设置的分数
				myExamDetailService.add(myExamDetail);// 添加我的考试详细
			}
			
			//乱序
			if (exam.getGenType() == 1 && (ExamUtil.hasQuestionRand(exam) || ExamUtil.hasOptionRand(exam))) {// 人工试卷
				if (ExamUtil.hasQuestionRand(exam)) {// 试题乱序
					Collections.shuffle(chapter.getMyQuestionList());
				}
				
				for (MyQuestion myQuestion : chapter.getMyQuestionList()) {
					noMap.put(myQuestion.getQuestion().getId(), "");
					if (ExamUtil.hasOptionRand(exam)) {// 选项乱序
						if (myQuestion.getQuestion().getType() == 1 || myQuestion.getQuestion().getType() == 2) {
							noMap.put(myQuestion.getQuestion().getId(), StringUtil.join(shuffleNums(1, myQuestion.getOptionList().size())));
						}
					}
				}
			}
		}
		
		if (exam.getGenType() == 1 && (ExamUtil.hasQuestionRand(exam) || ExamUtil.hasOptionRand(exam))) {
			ExamQuestionNo examQuestionNo = new ExamQuestionNo();
			examQuestionNo.setExamId(myExam.getExamId());
			examQuestionNo.setUserId(myExam.getUserId());
			examQuestionNo.setNo(JSON.toJSONString(noMap));
			examQuestionNoService.add(examQuestionNo);
		}
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
	public void publishOfRand(Exam exam) {
		// 校验数据有效性
		List<ExamQuestion> chapterList = examQuestionService.getChapterList(exam.getId());
		Map<Integer, List<ExamRule>> ruleCache = getRuleCache(chapterList);
		Map<Integer, List<Question>> questionListCache = getQuestionListCache(ruleCache);
		publishForRandValid(chapterList, ruleCache, questionListCache);
		
		// 获取考试用户列表
		List<MyMark> myMarkList = myMarkService.getList(exam.getId());
		for (MyMark myMark : myMarkList) {
			for (Integer userId : myMark.getExamUserIdArr()) {
				// 生成用户试卷
				MyExamChapter myExamChapter = generatePaperForRand(exam, ruleCache, questionListCache, userId);
				generateUserPaper(exam, userId, myExamChapter);
			}
		}
	}

	private MyExamChapter generatePaperForRand(Exam exam, Map<Integer, List<ExamRule>> ruleCache,
			Map<Integer, List<Question>> questionListCache, Integer userId) {
		// 查找符合规则的试题
		MyExamChapter myExamChapter = new MyExamChapter();
		Set<Integer> usedQuestion = new HashSet<Integer>();
		for(List<ExamRule> ruleList : ruleCache.values()) {// 循环章节
			Chapter chapter = new Chapter();
			myExamChapter.getChapterList().add(chapter);
			for (ExamRule rule : ruleList) {// 循环单章节下规则
				int remainQuestionNum = rule.getNum();
				Collections.shuffle(questionListCache.get(rule.getQuestionTypeId()));// 打乱顺序模拟随机
				for(Question question : questionListCache.get(rule.getQuestionTypeId())) {
					if (rule.getType().intValue() != question.getType().intValue()
							|| !rule.getMarkTypes().contains(question.getMarkType().toString()) ) {// 当前试题不符合规则，不添加
							continue;
					}
					
					if (usedQuestion.contains(question.getId())) {// 添加过，不添加
						continue;
					}
					
					// 添加考试试题
					ExamQuestion attr = new ExamQuestion();
					attr.setQuestionId(question.getId());
					attr.setType(3);
					attr.setScore(rule.getScore());
					
					// 添加考试试题子分数
					List<QuestionAnswer> questionAnswerList = questionAnswerService.getList(question.getId());//获取试题答案
					List<BigDecimal> singleScoreList = getSubScoreList(rule.getScore(), questionAnswerList.size());
					
					if (question.getType() == 1 || question.getType() == 4 ) {// 单选和判断
						attr.setScores(rule.getScore().toString());// 子分数和当前题分数一样（就一个可以不循环）
					} else if (question.getType() == 2) {// 多选题
						attr.setScores(BigDecimalUtil.newInstance(question.getScore()).div(2, 2).getResult().toString());// 子分数为当前题分数一半
					} else if (question.getType() == 3 || question.getType() == 5) {// 填空和问答题
						attr.setScores("0");// 如果是主观题，子分数无效
						if (question.getMarkType() == 1) {// 如果是客观题，子分数为当前题分数除总关键词
							String scores = "";
							for(int j = 0; j < questionAnswerList.size(); j++){
								if ("".equals(scores)) {
									scores = singleScoreList.get(j).toString();
									continue;
								}
								scores = String.format("%s,%s", scores, singleScoreList.get(j));
							}
							attr.setScores(scores);
						}
					}
					
					attr.setMarkOptions(rule.getMarkOptions());
					attr.setExamId(rule.getExamId());
					attr.setExamId(exam.getId());
					attr.setUserId(userId);
					attr.setNo(remainQuestionNum);
					attr.setUpdateTime(new Date());
					attr.setUpdateUserId(getCurUser().getId());
					examQuestionService.add(attr);// 添加随机试题
					
					chapter.getMyQuestionList().add(new MyQuestion(question, null, null, attr));// 模拟生成一个试卷，不用所有数据都生成
					usedQuestion.add(question.getId());
					remainQuestionNum--;
					if (remainQuestionNum <= 0) {// 某章节下某规则符合，退出循环。处理下一个规则
						break;
					}
				}
			}
		}
		
		return myExamChapter;
	}

	/**
	 * 获取子分数列表<br/>
	 * 1分 拆分成2份，结果：0.5、0.5<br/>
	 * 1分 拆分成3份，结果：0.33、0.33、0.34<br/>
	 * 
	 * v1.0 zhanghc 2022年5月30日下午4:46:25
	 * @param score
	 * @param num
	 * @return List<BigDecimal>
	 */
	private List<BigDecimal> getSubScoreList(BigDecimal score, int num) {
		List<BigDecimal> singleScoreList = new ArrayList<>();
		BigDecimal singleScore = BigDecimalUtil.newInstance(score).div(num, 2).getResult();
		for (int j = 0; j < num - 1; j++) {
			singleScoreList.add(singleScore);
		}
		singleScoreList.add(BigDecimalUtil.newInstance(singleScore).mul(num - 1).sub(score).mul(-1).getResult());
		return singleScoreList;
	}

	private Map<Integer, List<Question>> getQuestionListCache(Map<Integer, List<ExamRule>> ruleCache) {
		Map<Integer, List<Question>> questionListCache = new HashMap<>();// 按 题库 分组的试题
		for (List<ExamRule> ruleList : ruleCache.values()) {
			for (ExamRule rule : ruleList) {
				if (questionListCache.get(rule.getQuestionTypeId()) != null) {
					continue;
				}
				
				List<Question> questionList = questionService.getList(rule.getQuestionTypeId());
				ListIterator<Question> questionIterator = questionList.listIterator();
				while (questionIterator.hasNext()) {
					Question question = questionIterator.next();
					if (question.getState() != 1) {// 非发布的删除
						questionIterator.remove();
					}
				}
				
				questionListCache.put(rule.getQuestionTypeId(), questionList);
			}
		}
		return questionListCache;
	}

	private Map<Integer, List<ExamRule>> getRuleCache(List<ExamQuestion> chapterList) {
		Map<Integer, List<ExamRule>> ruleCache = new HashMap<>();
		for (ExamQuestion chapter : chapterList) {
			List<ExamRule> ruleList = examRuleService.getList(chapter.getId());
			ruleCache.put(chapter.getId(), ruleList);
		}
		return ruleCache;
	}

	private void publishForRandValid(List<ExamQuestion> chapterList, 
			Map<Integer, List<ExamRule>> ruleCache, Map<Integer, List<Question>> questionListCache) {
		Set<Integer> usedQuestion = new HashSet<Integer>();
		for (ExamQuestion chapter : chapterList) {// chapterList作用是抛异常时用
			for (ExamRule rule : ruleCache.get(chapter.getId())) {// 循环单章节下规则
				int remainQuestionNum = rule.getNum();
				for(Question question : questionListCache.get(rule.getQuestionTypeId())) {
					if (rule.getType().intValue() != question.getType().intValue()
							|| !rule.getMarkTypes().contains(question.getMarkType().toString()) ) {// 当前试题不符合规则，不添加
							continue;
					}
					
					if (usedQuestion.contains(question.getId())) {// 添加过，不添加
						continue;
					}
					
					remainQuestionNum--;
					if (remainQuestionNum <= 0) {// 某章节下某规则符合，退出循环
						break;
					}
				}
				
				if (remainQuestionNum > 0) {// // 某章节下某规则不符合，抛异常
					throw new MyException(String.format("章节【%s】下第【%s】个规则题数不足【%s】道", chapter.getChapterTxt(), rule.getNo(), rule.getNum()));
				}
			}
		}
	}

	@Override
	public void userAdd(Exam exam, String[] examUserIds, Integer[] markUserIds) {
		// 添加阅卷用户
		userAddMarkUser(exam, examUserIds, markUserIds);
				
		// 添加考试用户、生成用户试卷
		if (exam.getState() != 1) {// 未发布，不生成用户试卷，因为试卷可能变更，导致用户A和用户B试卷不一致
			return;
		}
		
		Set<Integer> userSet = userAddSyn(exam, examUserIds);
		MyExamChapter myExamChapter = examService.getExamChapter(exam.getId());
		for (Integer userId : userSet) {
			generateUserPaper(exam, userId, myExamChapter);
		}
	}

	private void userAddMarkUser(Exam exam, String[] examUserIds, Integer[] markUserIds) {
		List<MyMark> myMarkList = myMarkService.getList(exam.getId());// 获取我的考试列表
		for (MyMark myMark : myMarkList) {
			myMarkService.del(myMark.getId());
		}
		
		if (exam.getMarkType() == 1) {
			MyMark myMark = new MyMark();
			myMark.setMarkUserId(null);
			myMark.setExamUserIds(String.format(",%s,", StringUtil.join(examUserIds)));
			myMark.setUpdateUserId(getCurUser().getId());
			myMark.setUpdateTime(new Date());
			myMark.setExamId(exam.getId());
			myMarkService.add(myMark);
		} else if (exam.getMarkType() == 2) {
			for (int i = 0; i < markUserIds.length; i++) {
				MyMark myMark = new MyMark();
				myMark.setMarkUserId(markUserIds[i]);
				myMark.setExamUserIds(String.format(",%s,", examUserIds[i]));
				myMark.setUpdateUserId(getCurUser().getId());
				myMark.setUpdateTime(new Date());
				myMark.setExamId(exam.getId());
				myMarkService.add(myMark);
			}
		}
	}

	/**
	 * 同步考试用户
	 * 
	 * v1.0 zhanghc 2022年5月31日上午11:18:42
	 * @param exam
	 * @param examUserIds
	 * @return Set<Integer> 需要添加的用户试卷
	 */
	private Set<Integer> userAddSyn(Exam exam, String[] examUserIds) {
		/**
		 * 页面：1,2,3
		 * 数据库：2,3,4
		 * 1添加；4删除；2,3不动
		 */
		List<MyExam> myExamList = myExamService.getList(exam.getId());
		ListIterator<MyExam> myExamListIterator = myExamList.listIterator();// 当前考试的人员
		Set<Integer> examUserIdSet = new HashSet<>();
		for (String userIds : examUserIds) {
			for (String userId : userIds.split(",")) {
				examUserIdSet.add(Integer.parseInt(userId));
			}
		}
		while (myExamListIterator.hasNext()) {
			MyExam myExam = myExamListIterator.next();
			if (examUserIdSet.contains(myExam.getUserId())) {
				examUserIdSet.remove(myExam.getUserId());
				myExamListIterator.remove();
			} else {
				List<MyExamDetail> myExamDetailList = myExamDetailService.getList(exam.getId(), myExam.getUserId());
				for (MyExamDetail myExamDetail : myExamDetailList) {
					myExamDetailService.del(myExamDetail.getId());
				}
				
				MyExam myExamDel = myExamService.getMyExam(exam.getId(), myExam.getUserId());
				myExamService.del(myExamDel.getId());
			}
		}
		return examUserIdSet;
	}

	@Override
	public void userAddOfRand(Exam exam, String[] examUserIds) {
		// 校验数据有效性
		List<ExamQuestion> chapterList = examQuestionService.getChapterList(exam.getId());
		Map<Integer, List<ExamRule>> ruleCache = getRuleCache(chapterList);
		Map<Integer, List<Question>> questionListCache = getQuestionListCache(ruleCache);
		publishForRandValid(chapterList, ruleCache, questionListCache);
		
		// 添加阅卷用户
		userAddMarkUser(exam, examUserIds, null);
		
		// 添加考试用户，生成用户试卷
		if (exam.getState() != 1) {// 未发布，不生成用户试卷，因为随机规则可能会变更
			return;
		}
		
		Set<Integer> userSet = userAddSynForRand(exam, examUserIds);
		for (Integer userId : userSet) {
			MyExamChapter myExamChapter = generatePaperForRand(exam, ruleCache, questionListCache, userId);
			generateUserPaper(exam, userId, myExamChapter);
		}
	}

	private Set<Integer> userAddSynForRand(Exam exam, String[] examUserIds) {
		/**
		 * 页面：1,2,3
		 * 数据库：2,3,4
		 * 1添加；4删除；2,3不动
		 */
		List<MyExam> myExamList = myExamService.getList(exam.getId());
		ListIterator<MyExam> myExamListIterator = myExamList.listIterator();// 当前考试的人员
		Set<Integer> examUserIdSet = new HashSet<>();
		for (String userIds : examUserIds) {
			for (String userId : userIds.split(",")) {
				examUserIdSet.add(Integer.parseInt(userId));
			}
		}
		while (myExamListIterator.hasNext()) {
			MyExam myExam = myExamListIterator.next();
			if (examUserIdSet.contains(myExam.getUserId())) {
				examUserIdSet.remove(myExam.getUserId());
				myExamListIterator.remove();
			} else {
				{// 删除用户，用户答案
					List<MyExamDetail> myExamDetailList = myExamDetailService.getList(exam.getId(), myExam.getUserId());
					for (MyExamDetail myExamDetail : myExamDetailList) {
						myExamDetailService.del(myExamDetail.getId());
					}
					
					MyExam myExamDel = myExamService.getMyExam(exam.getId(), myExam.getUserId());
					myExamService.del(myExamDel.getId());
				}
				
				{// 删除用户试卷
					List<ExamQuestion> examQuestionList = examQuestionService.getList(myExam.getExamId(), myExam.getUserId());
					for (ExamQuestion examQuestion : examQuestionList) {
						examQuestionService.del(examQuestion.getId());
					}
				}
			}
		}
		return examUserIdSet;
	}
}
