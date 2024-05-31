package com.wcpdoc.exam.core.service.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.base.util.CurLoginUserUtil;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.SpringUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.constant.ExamConstant;
import com.wcpdoc.exam.core.dao.MyMarkDao;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyMark;
import com.wcpdoc.exam.core.entity.MyQuestion;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.ex.PaperPart;
import com.wcpdoc.exam.core.service.ExamCacheService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyMarkService;
import com.wcpdoc.exam.core.service.MyPaperService;
import com.wcpdoc.exam.core.service.MyQuestionService;

import lombok.extern.slf4j.Slf4j;

/**
 * 我的阅卷服务层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Service
@Slf4j
public class MyMarkServiceImpl extends BaseServiceImp<MyMark> implements MyMarkService {
	@Resource
	private MyMarkDao myMarkDao;
	@Resource
	@Lazy
	private MyExamService myExamService;
	@Resource
	private MyQuestionService myQuestionService;
	@Resource
	private ExamCacheService examCacheService;
	@Resource
	private BaseCacheService baseCacheService;
	@Resource
	private MyPaperService myPaperService;

	@Override
	public RBaseDao<MyMark> getDao() {
		return myMarkDao;
	}

	@Override
	@CacheEvict(value = ExamConstant.MYEXAM_CACHE, allEntries = true)
	public void assign(Integer examId, Integer num) {
		// 数据校验
		assignValid(examId, num);

		// 试卷分配
		List<MyExam> myExamList = examCacheService.getMyExamList(examId);
		Collections.shuffle(myExamList);
		for (MyExam myExam : myExamList) {
			if (!ValidateUtil.isValid(myExam.getMarkUserId())) {
				myExam.setMarkUserId(getCurUser().getId());
				myExamService.updateById(myExam);

				if (--num < 1) {
					break;
				}
			}
		}
	}

	@Override
	public List<PaperPart> paper(Integer examId, Integer userId) {
		// 数据校验
		paperValid(examId, userId);

		// 试卷处理
		Exam exam = examCacheService.getExam(examId);
		MyExam myExam = examCacheService.getMyExam(examId, userId);
		if (!ValidateUtil.isValid(myExam.getMarkStartTime())
				&& exam.getMarkEndTime().getTime() > System.currentTimeMillis()) {
			SpringUtil.getBean(MyMarkService.class).paperHandle(examId, userId);
		}
		// 试卷生成
		return myPaperService.generatePaper(examId, userId, true, true);
	}

	@Override
	@Caching(evict = { //
			@CacheEvict(value = ExamConstant.MYEXAM_CACHE, key = ExamConstant.MYEXAM_KEY_PRE
					+ "#examId + ':' + #userId"), //
			@CacheEvict(value = ExamConstant.MYEXAM_CACHE, key = ExamConstant.MYEXAM_LIST_KEY_PRE + "#examId"), //
			@CacheEvict(value = ExamConstant.MYEXAM_CACHE, key = ExamConstant.MYEXAM_UNMARK_LIST_KEY), })
	public void paperHandle(Integer examId, Integer userId) {
		/**
		 * 阅卷用户如果是第一次打开试卷，并且整场考试阅卷没结束，生成他自己的阅卷时间，并标记为阅卷中<br/>
		 * 
		 * 示例：阅卷时间为2024-01-30 14:00 - 2024-01-30 18:00<br/>
		 * 阅卷用户第一次打开试卷时间为2024-01-30 14:15:16；他的阅卷结束时间为2024-01-30 18:00:00<br/>
		 * 阅卷用户第一次打开试卷时间为2024-01-30 16:15:16；他的阅卷结束时间为2024-01-30 18:00:00<br/>
		 * 
		 */
		MyExam myExam = examCacheService.getMyExam(examId, userId);
		Exam exam = examCacheService.getExam(examId);
		myExam.setMarkStartTime(new Date());
		myExam.setMarkEndTime(exam.getMarkEndTime());
		myExam.setMarkState(2);
		myExamService.updateById(myExam);

		User markUser = baseCacheService.getUser(getCurUser().getId());
		User examUser = baseCacheService.getUser(myExam.getUserId());
		log.info("【{}-{}】进入【{}-{}】批阅【{}-{}】", markUser.getLoginName(), markUser.getName(), exam.getId(), exam.getName(),
				examUser.getLoginName(), examUser.getName());
	}

	@Override
	@CacheEvict(value = ExamConstant.MYQUESTION_CACHE, key = ExamConstant.MYQUESTION_LIST_KEY_PRE
			+ "#examId + ':' + #userId")
	public void score(Integer examId, Integer userId, Integer questionId, BigDecimal userScore) {
		// 数据校验
		MyQuestion myQuestion = scoreValid(examId, userId, questionId, userScore);

		// 打分
		myQuestion.setUserScore(userScore);
		myQuestion.setMarkTime(new Date());
		myQuestion.setMarkUserId(getCurUser().getId());
		myQuestionService.updateById(myQuestion);

		Exam exam = examCacheService.getExam(examId);
		User markUser = baseCacheService.getUser(getCurUser().getId());
		User examUser = baseCacheService.getUser(userId);
		log.info("【{}-{}】进入【{}-{}】批阅【{}-{}】得【{}-{}分】", markUser.getLoginName(), markUser.getName(), exam.getId(),
				exam.getName(), examUser.getLoginName(), examUser.getName(), questionId, userScore.toString());
	}

	@Override
	@Caching(evict = { //
			@CacheEvict(value = ExamConstant.MYEXAM_CACHE, key = ExamConstant.MYEXAM_KEY_PRE
					+ "#examId + ':' + #userId"),
			@CacheEvict(value = ExamConstant.MYEXAM_CACHE, key = ExamConstant.MYEXAM_LIST_KEY_PRE + "#examId"),
			@CacheEvict(value = ExamConstant.MYEXAM_CACHE, key = ExamConstant.MYEXAM_UNMARK_LIST_KEY), })
	public void finish(Integer examId, Integer userId) {
		// 数据校验
		finishValid(examId, userId);

		// 阅卷
		BigDecimal totalScore = examCacheService.getMyQuestionList(examId, userId).stream()//
				.filter(myQuestion -> myQuestion.getType() == 2)//
				.map(MyQuestion::getUserScore).reduce(BigDecimal.ZERO, BigDecimal::add);
		MyExam myExam = examCacheService.getMyExam(examId, userId);
		Exam exam = examCacheService.getExam(examId);
		myExam.setTotalScore(totalScore);
		myExam.setMarkEndTime(new Date());
		myExam.setMarkState(3);
		myExam.setAnswerState(myExam.getTotalScore().doubleValue() >= exam.getPassScore().doubleValue() ? 1 : 2);
		myExamService.updateById(myExam);

		User markUser = baseCacheService.getUser(getCurUser().getId());
		User examUser = baseCacheService.getUser(userId);
		log.info("【{}-{}】进入【{}-{}】批阅【{}-{}】完成，得分{}", markUser.getLoginName(), markUser.getName(), exam.getId(),
				exam.getName(), examUser.getLoginName(), examUser.getName(), myExam.getTotalScore());
	}

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return myMarkDao.getListpage(pageIn);
	}

	@Override
	public PageOut getUserListpage(PageIn pageIn) {
		return myMarkDao.getUserListpage(pageIn);
	}

	@Override
	public List<MyMark> getList(Integer examId) {
		return myMarkDao.getList(examId);
	}

	private MyQuestion scoreValid(Integer examId, Integer userId, Integer questionId, BigDecimal userScore) {
		if (!ValidateUtil.isValid(examId)) {
			throw new MyException("参数错误：examId");
		}
		if (!ValidateUtil.isValid(userId)) {
			throw new MyException("参数错误：userId");
		}
		if (!ValidateUtil.isValid(questionId)) {
			throw new MyException("参数错误：questionId");
		}
		if (!ValidateUtil.isValid(userScore)) {
			throw new MyException("参数错误：userScore");
		}
		MyExam myExam = examCacheService.getMyExam(examId, userId);
		if (myExam == null) {
			throw new MyException("未参与考试");
		}
		if (myExam.getMarkUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("未参与考试");
		}

		MyQuestion myQuestion = myQuestionService.getMyQuestion(examId, userId, questionId);
		if (myQuestion == null) {
			throw new MyException("未参与考试");
		}
		Exam exam = examCacheService.getExam(examId);
		if (exam.getState() == 0) {
			throw new MyException("考试已删除");
		}
		if (exam.getState() == 2) {
			throw new MyException("考试已暂停");
		}
		if (exam.getMarkType() == 1) {
			throw new MyException("无需人工阅卷");
		}
		long curTime = System.currentTimeMillis();
		if (exam.getMarkStartTime().getTime() > curTime) {
			throw new MyException("阅卷未开始");
		}
//		if (curTime - exam.getMarkEndTime().getTime() > 1000) {// 预留1秒网络延时
//			throw new MyException("阅卷已结束"); // 预留长和短都不合适，只要是阅卷中，就可以
//		}
		if (exam.getMarkState() == 1) {
			throw new MyException("客观题正在阅卷中，请等待");
		}
		if (exam.getMarkState() == 3) {
			throw new MyException("阅卷已结束");
		}
		if (userScore.doubleValue() > myQuestion.getScore().doubleValue()) {
			throw new MyException("最大分值：" + myQuestion.getScore());
		}
		if (userScore.doubleValue() < 0) {
			throw new MyException("最小分值：0");
		}
		Question question = examCacheService.getQuestion(questionId);
		if (question.getMarkType() != 2) {
			throw new MyException("该题为客观题");
		}
		if (myExam.getState() == 1) {
			throw new MyException("未参与考试，阅卷无效");
		}
		return myQuestion;
	}

	private void finishValid(Integer examId, Integer userId) {
		if (!ValidateUtil.isValid(examId)) {
			throw new MyException("参数错误：examId");
		}
		if (!ValidateUtil.isValid(userId)) {
			throw new MyException("参数错误：userId");
		}
		MyExam myExam = examCacheService.getMyExam(examId, userId);
		if (myExam == null) {
			throw new MyException("未参与考试");
		}
		if (myExam.getMarkUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("未参与考试");
		}

		Exam exam = examCacheService.getExam(examId);
		if (exam.getState() == 0) {
			throw new MyException("考试已删除");
		}
		if (exam.getState() == 2) {
			throw new MyException("考试已暂停");
		}
		if (exam.getMarkType() == 1) {
			throw new MyException("无需人工阅卷");
		}
		long curTime = System.currentTimeMillis();
		if (exam.getMarkStartTime().getTime() > curTime) {
			throw new MyException("阅卷未开始");
		}
//		if (curTime - exam.getMarkEndTime().getTime() > 1000) {// 预留1秒网络延时
//			throw new MyException("阅卷已结束");// 预留长和短都不合适，只要是阅卷中，就可以
//		}
		if (exam.getMarkState() == 1) {
			throw new MyException("客观题正在阅卷中，请等待");
		}
		if (exam.getMarkState() == 3) {
			throw new MyException("阅卷已结束");
		}
		long unMarkNum = examCacheService.getMyQuestionList(examId, userId).stream()//
				.filter(myQuestion -> myQuestion.getType() == 2 && myQuestion.getUserScore() == null)//
				.count();
		if (unMarkNum > 0) {
			throw new MyException(String.format("剩余%s道未阅", unMarkNum));
		}
	}

	private void paperValid(Integer examId, Integer userId) {
		if (!ValidateUtil.isValid(examId)) {
			throw new MyException("参数错误：examId");
		}
		if (!ValidateUtil.isValid(userId)) {
			throw new MyException("参数错误：userId");
		}

		MyExam myExam = examCacheService.getMyExam(examId, userId);
		if (myExam == null) {
			throw new MyException("试卷不存在");
		}
		if (CurLoginUserUtil.isAdmin()) {// 管理员看全部

		}
		if (CurLoginUserUtil.isSubAdmin()) {// 子管理看自己的人
			if (!baseCacheService.getUser(getCurUser().getId()).getUserIds().contains(userId)) {
				throw new MyException("无查阅权限");
			}
		}
		if (CurLoginUserUtil.isMarkUser()) {// 协助阅卷只看自己阅过的
			if (myExam.getMarkUserId().intValue() != getCurUser().getId().intValue()) {
				throw new MyException("无查阅权限");
			}
		}
		if (CurLoginUserUtil.isExamUser()) {// 考试用户不能看
			throw new MyException("无查阅权限");
		}

		Exam exam = examCacheService.getExam(examId);
		if (exam.getState() == 0) {
			throw new MyException("考试已删除");
		}
		if (exam.getState() == 2) {
			throw new MyException("考试已暂停");
		}
		if (exam.getMarkType() == 2) {
			if (exam.getMarkStartTime().getTime() > System.currentTimeMillis()) {
				throw new MyException("阅卷未开始");
			}
		}
	}

	private void assignValid(Integer examId, Integer num) {
		if (!ValidateUtil.isValid(examId)) {
			throw new MyException("参数错误：examId");
		}
		if (!ValidateUtil.isValid(num) || num < 1 || num > 100) {
			throw new MyException("参数错误：num");
		}

		Exam exam = examCacheService.getExam(examId);
		if (exam.getState() == 0) {
			throw new MyException("考试已删除");
		}
		if (exam.getState() == 2) {
			throw new MyException("考试已暂停");
		}
		long curTime = System.currentTimeMillis();
		if (exam.getMarkStartTime().getTime() > curTime) {
			throw new MyException("阅卷未开始");
		}
		if (curTime - exam.getMarkEndTime().getTime() > 0) {
			throw new MyException("阅卷已结束");
		}
		if (exam.getMarkState() == 1) {
			throw new MyException("客观题正在阅卷中，请等待");
		}
		if (exam.getMarkState() == 3) {
			throw new MyException("阅卷已结束");
		}
	}
}
