package com.wcpdoc.exam.core.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.BigDecimalUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.constant.ExamConstant;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyQuestion;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.entity.ex.ChapterPart;
import com.wcpdoc.exam.core.entity.ex.PaperPart;
import com.wcpdoc.exam.core.entity.ex.QuestionPart;
import com.wcpdoc.exam.core.service.ExamCacheService;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyPaperService;
import com.wcpdoc.exam.core.service.MyQuestionService;
import com.wcpdoc.exam.core.util.ExamUtil;
import com.wcpdoc.exam.core.util.MyExamUtil;
import com.wcpdoc.exam.core.util.QuestionUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 我的试卷服务层实现
 * 
 * v1.0 zhanghc 2024年3月5日下午2:46:04
 */
@Service
@Slf4j
public class MyPaperServiceImpl extends BaseServiceImp<Object> implements MyPaperService {
	@Resource
	@Lazy
	private MyExamService myExamService;
	@Resource
	private MyQuestionService myQuestionService;
	@Resource
	@Lazy
	private ExamService examService;
	@Resource
	private ExamCacheService examCacheService;
	@Resource
	private BaseCacheService baseCacheService;

	@Override
	public RBaseDao<Object> getDao() {
		return null;
	}

	@Override
	public List<PaperPart> generatePaper(Integer examId, Integer userId, Boolean scoreShow, Boolean answerShow) {
		long curTime = System.currentTimeMillis();
		List<PaperPart> paper = new ArrayList<>();
		List<MyQuestion> myQuestionList = examCacheService.getMyQuestionList(examId, userId);
		Exam exam = examCacheService.getExam(examId);
		for (MyQuestion myQuestion : myQuestionList) {
			if (myQuestion.getType() == 1) {// 章节组装
				ChapterPart chapterPart = new ChapterPart();
				chapterPart.setType(myQuestion.getType());
				chapterPart.setChapterName(myQuestion.getChapterName());
				chapterPart.setChapterTxt(myQuestion.getChapterTxt());
				paper.add(chapterPart);
			} else {// 试题组装
				Question question = examCacheService.getQuestion(myQuestion.getQuestionId());
				QuestionPart questionPart = new QuestionPart();
				questionPart.setType(myQuestion.getType());
				questionPart.setQuestionId(myQuestion.getQuestionId());
				questionPart.setQuestionType(question.getType());
				questionPart.setMarkType(question.getMarkType());
				questionPart.setTitle(question.getTitle());
				questionPart.setMarkOptions(myQuestion.getMarkOptions());
				questionPart.setScore(myQuestion.getScore());
				questionPart.setAnalysis(question.getAnalysis());
				if (QuestionUtil.hasSingleChoice(question) || QuestionUtil.hasMultipleChoice(question)) {// 组装试题选项
					List<QuestionOption> questionOptionList = examCacheService
							.getQuestionOptionList(myQuestion.getQuestionId());
					if (ExamUtil.hasOptionRand(exam)) {
						for (int optionsNo : myQuestion.getOptionsNo()) {
							QuestionOption questionOption = questionOptionList.get(optionsNo - 1);
							questionPart.getOptions().add(questionOption.getOptions());
						}
					} else {
						for (QuestionOption questionOption : questionOptionList) {
							questionPart.getOptions().add(questionOption.getOptions());
						}
					}

				}
				if (scoreShow) {// 显示考试用户分数
					questionPart.setUserScore(myQuestion.getUserScore());
				}

				if (answerShow) {// 组装标准答案（考试结束前用不着，不用和试题一块查询）
					List<QuestionAnswer> questionAnswerList = examCacheService
							.getQuestionAnswerList(myQuestion.getQuestionId());
					for (QuestionAnswer answer : questionAnswerList) {
						if (QuestionUtil.hasTrueFalse(question)
								|| (QuestionUtil.hasQA(question) && QuestionUtil.hasSubjective(question))) {
							questionPart.getAnswers().add(answer.getAnswer());
						} else if (QuestionUtil.hasSingleChoice(question)) {
							if (ExamUtil.hasOptionRand(exam)) {
								questionPart.getAnswers().add(myQuestion.getOptionsNoCache().get(answer.getAnswer()));
							} else {
								questionPart.getAnswers().add(answer.getAnswer());
							}
						} else if (QuestionUtil.hasMultipleChoice(question)) {
							String[] answers = answer.getAnswer().split(",");
							if (ExamUtil.hasOptionRand(exam)) {
								for (String _answer : answers) {
									questionPart.getAnswers().add(myQuestion.getOptionsNoCache().get(_answer));
								}
							} else {
								Collections.addAll(questionPart.getAnswers(), answers);
							}
						} else if (QuestionUtil.hasFillBlank(question)
								|| (QuestionUtil.hasQA(question) && QuestionUtil.hasObjective(question))) {
							questionPart.getAnswers().add(answer.getAnswer());
						}
					}
				}

				if (ValidateUtil.isValid(myQuestion.getUserAnswer())) {// 组装用户答案
					if (QuestionUtil.hasTrueFalse(question) || QuestionUtil.hasQA(question)) {
						questionPart.getUserAnswers().add(myQuestion.getUserAnswer());
					} else if (QuestionUtil.hasSingleChoice(question)) {
						if (ValidateUtil.isValid(myQuestion.getOptionsNo())) {
							questionPart.getUserAnswers()
									.add(myQuestion.getOptionsNoCache().get(myQuestion.getUserAnswer()));
						} else {
							questionPart.getUserAnswers().add(myQuestion.getUserAnswer());
						}
					} else if (QuestionUtil.hasMultipleChoice(question)) {
						String[] userAnswers = myQuestion.getUserAnswer().split(",");
						if (ValidateUtil.isValid(myQuestion.getOptionsNo())) {
							for (String userAnswer : userAnswers) {
								questionPart.getUserAnswers().add(myQuestion.getOptionsNoCache().get(userAnswer));
							}
						} else {
							Collections.addAll(questionPart.getUserAnswers(), userAnswers);
						}
					} else if (QuestionUtil.hasFillBlank(question)) {
						Collections.addAll(questionPart.getUserAnswers(), myQuestion.getUserAnswer().split("\n", -1));
					}
				}
				paper.add(questionPart);
			}
		}
		long total = System.currentTimeMillis() - curTime;
		log.info("生成试卷【{}-{}】耗时：{}毫秒", examId, userId, total);
		return paper;
	}

	@Override
	@Caching(evict = { //
			@CacheEvict(value = ExamConstant.MYEXAM_CACHE, key = ExamConstant.MYEXAM_KEY_PRE
					+ "#examId + ':' + #userId"), //
			@CacheEvict(value = ExamConstant.MYEXAM_CACHE, key = ExamConstant.MYEXAM_LIST_KEY_PRE + "#examId"), //
			@CacheEvict(value = ExamConstant.MYEXAM_CACHE, key = ExamConstant.MYEXAM_UNMARK_LIST_KEY),
			@CacheEvict(value = ExamConstant.MYQUESTION_CACHE, key = ExamConstant.MYQUESTION_LIST_KEY_PRE
					+ "#examId + ':' + #userId"), })
	public MyExam doMark(Integer examId, Integer userId) {
		// 数据校验
		doMarkValid(examId, userId);

		// 批阅客观题
		MyExam myExam = examCacheService.getMyExam(examId, userId);
		Exam exam = examCacheService.getExam(examId);
		if (exam.getMarkType() == 1) {// 如果是客观题考试，记录自动阅卷时间
			myExam.setMarkStartTime(new Date());// 如果是主观题考试，记录阅卷用户打分时间
		}
		BigDecimal objectiveScore = doMarkObjectiveHandle(examId, userId);

		// 如果是客观题考试，直接出成绩
		if (exam.getMarkType() == 1) {
			myExam.setMarkUserId(1);// 阅卷人为admin
			myExam.setMarkEndTime(new Date());
			myExam.setObjectiveScore(objectiveScore);
			myExam.setTotalScore(myExam.getObjectiveScore());// 客观题分数就是总分数
			myExam.setAnswerState(BigDecimalUtil.newInstance(myExam.getTotalScore()).sub(exam.getPassScore())
					.getResult().doubleValue() >= 0 ? 1 : 2);// 标记用户是否及格
			myExam.setState(3);// 标记为已交卷
			myExam.setMarkState(3);// 标记为已阅卷
			myExamService.updateById(myExam);
		}
		// 如果是主观题考试，等待人工阅卷
		else if (exam.getMarkType() == 2) {
			myExam.setState(3);// 标记为已交卷
			myExam.setObjectiveScore(objectiveScore);
			myExamService.updateById(myExam);
		}

		return myExam;
	}

	@Override
	@Caching(evict = { //
			@CacheEvict(value = ExamConstant.EXAM_CACHE, allEntries = true),
			@CacheEvict(value = ExamConstant.MYEXAM_CACHE, allEntries = true),
			@CacheEvict(value = ExamConstant.MYQUESTION_CACHE, allEntries = true), })
	public void doExam(Integer examId) {
		// 数据校验
		List<MyExam> myExamList = doExamValid(examId);

		// 找出本场考试所有的试卷，进行收尾工作
		Exam exam = examCacheService.getExam(examId);
		if (exam.getMarkType() == 1) {// 客观题考试处理
			doExamPaperHandle(exam, myExamList);// 未考试的试卷处理
			doExamObjectiveHandle(exam, myExamList);
		} else {// 主观题考试处理
			doExamSubjectiveHandle(exam, myExamList);
		}
	}

	private List<MyExam> doExamValid(Integer examId) {
		Exam exam = examCacheService.getExam(examId);
		if (exam.getState() == 0) {
			throw new MyException("考试已删除");
		}
		if (exam.getState() == 2) {
			throw new MyException("考试已暂停");
		}
		if (!ValidateUtil.isValid(examId)) {
			throw new MyException("参数错误：examId");
		}
		if (exam.getMarkState() == 3) {
			throw new MyException("考试已阅卷");
		}

		long curTime = System.currentTimeMillis();
		if (exam.getEndTime().getTime() > curTime) {
			throw new MyException("考试未结束");
		}
		if (exam.getMarkState() == 2) {
			if (exam.getMarkEndTime().getTime() > curTime) {
				throw new MyException("人工阅卷未结束");
			}
		}

		List<MyExam> myExamList = examCacheService.getMyExamList(examId);
		List<MyExam> markingList = myExamList.stream().filter(myExam -> myExam.getState() == 2)
				.collect(Collectors.toList());
		if (ValidateUtil.isValid(markingList)) {
			throw new MyException(String.format("批量阅卷剩余%s张", markingList.size()));
		}

		return myExamList;
	}

	private BigDecimal doMarkObjectiveHandle(Integer examId, Integer userId) {
		// 查找当前试卷的所有客观题，进行批阅
		List<MyQuestion> objectiveQuestionList = examCacheService.getMyQuestionList(examId, userId).stream()//
				.filter(myQuestion -> myQuestion.getType() == 2
						&& QuestionUtil.hasObjective(examCacheService.getQuestion(myQuestion.getQuestionId())))
				.map(myQuestion -> {
					Question question = examCacheService.getQuestion(myQuestion.getQuestionId());
					List<QuestionAnswer> questionAnswerList = examCacheService.getQuestionAnswerList(question.getId());
					if (QuestionUtil.hasQA(question)) {
						MyExamUtil.qAHandle(question, questionAnswerList, myQuestion);// 问答处理
					} else if (QuestionUtil.hasSingleChoice(question) || QuestionUtil.hasTrueFalse(question)) {
						MyExamUtil.singleChoiceHandle(question, questionAnswerList, myQuestion);// 单选判断处理
					} else if (QuestionUtil.hasMultipleChoice(question)) {
						MyExamUtil.multipleChoiceHandle(question, questionAnswerList, myQuestion);// 多选处理
					} else if (QuestionUtil.hasFillBlank(question)) {
						MyExamUtil.fillBlankHandle(question, questionAnswerList, myQuestion);// 填空处理
					}

					myQuestion.setMarkTime(new Date());
					myQuestion.setMarkUserId(1);
					myQuestionService.updateById(myQuestion);
					return myQuestion;
				}).collect(Collectors.toList());

		// 返回客观题总分
		return objectiveQuestionList.stream()//
				.map(MyQuestion::getUserScore)//
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private void doMarkValid(Integer examId, Integer userId) {
		if (!ValidateUtil.isValid(examId)) {
			throw new MyException("参数错误：examId");
		}
		if (!ValidateUtil.isValid(userId)) {
			throw new MyException("参数错误：userId");
		}
		Exam exam = examCacheService.getExam(examId);
//		if (exam.getState() == 0) {// 只要考试时间到，一律阅卷结束考试。
//			throw new MyException("考试已删除");
//		}
//		if (exam.getState() == 2) {
//			throw new MyException("考试已暂停");
//		}
		if (exam.getMarkState() != 1) {// 等于2也表示自动阅卷已完成
			throw new MyException("客观题阅卷已结束");
		}

		MyExam myExam = examCacheService.getMyExam(examId, userId);
		if (myExam == null) {
			throw new MyException("未参与考试");
		}
		if (myExam.getState() == 1) {
			throw new MyException("未考试");
		}
		if (myExam.getState() == 3) {
			throw new MyException("已交卷");
		}

		long curTime = System.currentTimeMillis() + 500;// 业务上变更考试结束时间为当前时间时，自动阅卷概率性校验为考试未结束（数据库时间四舍五入后比当前时间大。
		if (myExam.getExamStartTime().getTime() > curTime) {// 例：传入2022-05-12 23:59:59,999999，保存为2022-05-13 00:00:00
			throw new MyException("考试未开始");
		}
		if (myExam.getExamEndTime().getTime() > curTime) {
			throw new MyException("考试未结束");
		}
	}

	private void doExamRank(Exam exam, List<MyExam> myExamList) {
		AtomicInteger no = new AtomicInteger(1);
		myExamList.stream()//
				.sorted(Comparator.comparing(MyExam::getTotalScore).reversed()// 按分数排名
						.thenComparing(Comparator.comparing(MyExam::getAnswerMs)))// 分数相同，答题时间短排在前面
				.forEach(myExam -> {// 更新排名
					myExam.setNo(no.getAndIncrement());
					myExamService.updateById(myExam);

					User examUser = baseCacheService.getUser(myExam.getUserId());
					log.info("自动结束【{}-{}】下【{}-{}】的考试，得{}分，排{}名", exam.getId(), exam.getName(), examUser.getId(),
							examUser.getName(), myExam.getTotalScore(), myExam.getNo());
				});
	}

	private void doExamPaperHandle(Exam exam, List<MyExam> myExamList) {
		// 未考试的用户直接批阅出成绩（0分）
		myExamList.stream()//
				.filter(myExam -> myExam.getState() == 1)//
				.forEach(myExam -> {
					myExam.setMarkStartTime(new Date());// 记录阅卷开始时间

					examCacheService.getMyQuestionList(myExam.getExamId(), myExam.getUserId()).stream()//
							.filter(myQuestion -> myQuestion.getType() == 2)// 章节忽略
							.forEach(myQuestion -> {
								myQuestion.setMarkTime(new Date());
								myQuestion.setMarkUserId(1);
								myQuestion.setUserScore(BigDecimal.ZERO);
								myQuestionService.updateById(myQuestion);
							});

					myExam.setMarkEndTime(new Date());// 记录阅卷结束时间
					myExam.setMarkUserId(1);// 阅卷人为admin
					myExam.setMarkState(3);// 标记为阅卷结束
					myExam.setObjectiveScore(BigDecimal.ZERO);// 客观题0分
					myExam.setTotalScore(BigDecimal.ZERO);// 总分0分
					myExam.setAnswerState(2);// 标记用户为不及格
					myExamService.updateById(myExam);

					User examUser = baseCacheService.getUser(myExam.getUserId());
					log.info("自动结束【{}-{}】下【{}-{}】的考试，未考试", exam.getId(), exam.getName(), examUser.getId(),
							examUser.getName());
				});
	}

	private void doExamObjectiveHandle(Exam exam, List<MyExam> myExamList) {
		// 考试排名
		doExamRank(exam, myExamList);

		// 考试结束
		exam.setMarkState(3);// 标记考试为已阅卷
		examService.updateById(exam);
	}

	private void doExamSubjectiveHandle(Exam exam, List<MyExam> myExamList) {
		// 如果是考试结束时间到，则等待人工阅卷结束
		if (exam.getMarkState() == 1) {
			doExamPaperHandle(exam, myExamList);// 未考试的试卷处理

			exam.setMarkState(2);
			examService.updateById(exam);
			return;
		}

		// 如果阅卷结束时间到，未阅卷和阅卷中的试卷进行收尾
		myExamList.stream()//
				.filter(myExam -> myExam.getMarkState() != 3)// 已阅卷的不处理
				.forEach(myExam -> {
					if (myExam.getMarkState() == 1) {// 试卷无人阅卷
						myExam.setMarkUserId(1);// 记录阅卷人为admin
						myExam.setMarkStartTime(new Date());// 阅卷时间为当前时间
					}

					List<MyQuestion> questionList = examCacheService.getMyQuestionList(exam.getId(), myExam.getUserId())//
							.stream()//
							.filter(myQuestion -> myQuestion.getType() == 2)// 忽略章节
							.collect(Collectors.toList());
					questionList.stream()//
							.filter(myQuestion -> myQuestion.getUserScore() == null)// 当阅卷人没有阅卷或部分未阅卷时，阅卷时间到。
							.forEach(myQuestion -> {
								myQuestion.setUserScore(BigDecimal.ZERO);// 标记该题为0分
								myQuestion.setMarkUserId(1);// 阅卷人admin
								myQuestion.setMarkTime(new Date());// 阅卷时间为当前时间
								myQuestionService.updateById(myQuestion);
							});

					myExam.setTotalScore(questionList.stream().map(MyQuestion::getUserScore).reduce(BigDecimal.ZERO,
							BigDecimal::add));// 记录成绩
					myExam.setAnswerState(
							myExam.getTotalScore().doubleValue() >= exam.getPassScore().doubleValue() ? 1 : 2);// 标记及格状态
					myExam.setMarkState(3);// 标记为阅卷结束
					myExamService.updateById(myExam);

					User examUser = baseCacheService.getUser(myExam.getUserId());
					log.info("自动结束【{}-{}】下【{}-{}】的考试，未阅卷完成", exam.getId(), exam.getName(), examUser.getId(),
							examUser.getName(), myExam.getTotalScore());
				});

		// 考试排名
		doExamRank(exam, myExamList);

		// 考试结束
		exam.setMarkState(3);// 标记考试为已阅卷
		examService.updateById(exam);
	}
}
