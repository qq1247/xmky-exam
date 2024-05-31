package com.wcpdoc.exam.core.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.constant.ExamConstant;
import com.wcpdoc.exam.core.dao.ExamDao;
import com.wcpdoc.exam.core.dao.MyExamDao;
import com.wcpdoc.exam.core.dao.MyQuestionDao;
import com.wcpdoc.exam.core.dao.QuestionAnswerDao;
import com.wcpdoc.exam.core.dao.QuestionDao;
import com.wcpdoc.exam.core.dao.QuestionOptionDao;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyQuestion;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.service.ExamCacheService;

import lombok.extern.slf4j.Slf4j;

/**
 * 考试缓存服务层实现
 * 
 * v1.0 zhanghc 2024年4月14日下午9:36:00
 */
@Service
@Slf4j
public class ExamCacheServiceImpl extends BaseServiceImp<Object> implements ExamCacheService {
	@Resource
	private QuestionDao questionDao;
	@Resource
	private QuestionOptionDao questionOptionDao;
	@Resource
	private QuestionAnswerDao questionAnswerDao;
	@Resource
	private ExamDao examDao;
	@Resource
	private MyExamDao myExamDao;
	@Resource
	private MyQuestionDao myQuestionDao;

	@Override
	public RBaseDao<Object> getDao() {
		return null;
	}

	@Override
	@Cacheable(value = ExamConstant.QUESTION_CACHE, key = ExamConstant.QUESTION_KEY_PRE + "#id", sync = true)
	public Question getQuestion(Integer id) {
		Question question = questionDao.selectById(id);
		if (log.isDebugEnabled()) {
			log.debug("缓存试题：{id: {}, type: {}, questionTypeId: {}}", question.getId(), question.getType(),
					question.getQuestionTypeId());
		}
		return question;
	}

	@Override
	@Cacheable(value = ExamConstant.QUESTION_CACHE, key = ExamConstant.QUESTION_OPTION_KEY_PRE
			+ "#questionId", sync = true)
	public List<QuestionOption> getQuestionOptionList(Integer questionId) {
		List<QuestionOption> optionList = questionOptionDao.getList(questionId);
		if (log.isDebugEnabled()) {
			log.debug("缓存试题选项：{questionId: {}}", questionId);
		}
		return optionList;
	}

	@Override
	@Cacheable(value = ExamConstant.QUESTION_CACHE, key = ExamConstant.QUESTION_ANSWER_KEY_PRE
			+ "#questionId", sync = true)
	public List<QuestionAnswer> getQuestionAnswerList(Integer questionId) {
		List<QuestionAnswer> answerList = questionAnswerDao.getList(questionId);
		if (log.isDebugEnabled()) {
			log.debug("缓存试题答案：{questionId: {}}", questionId);
		}
		return answerList;
	}

	@Override
	@Cacheable(value = ExamConstant.EXAM_CACHE, key = ExamConstant.EXAM_KEY_PRE + "#id", sync = true)
	public Exam getExam(Integer id) {
		Exam exam = examDao.selectById(id);
		if (log.isDebugEnabled()) {
			log.debug("缓存考试：{id: {}, name: {}}", exam.getId(), exam.getName());
		}
		return exam;
	}

	@Override
	@Cacheable(value = ExamConstant.EXAM_CACHE, key = ExamConstant.EXAMING_LIST_KEY, sync = true)
	public List<Exam> getExamingList() {
		List<Exam> examingList = examDao.getExamingList();
		if (log.isDebugEnabled()) {
			log.debug("缓存进行中的考试列表：{}", examingList.stream().map(exam -> String.format(
					"{id: %s, name: %s, endTime: %s, markEndTime: %s}", exam.getId(), exam.getName(),
					DateUtil.formatDateTime(exam.getEndTime()),
					ValidateUtil.isValid(exam.getMarkEndTime()) ? DateUtil.formatDateTime(exam.getMarkEndTime()) : ""))
					.collect(Collectors.toList()));
		}
		return examingList;
	}

	@Override
	@Cacheable(value = ExamConstant.MYEXAM_CACHE, key = ExamConstant.MYEXAM_KEY_PRE
			+ "#examId + ':' + #userId", sync = true)
	public MyExam getMyExam(Integer examId, Integer userId) {
		MyExam myExam = myExamDao.getMyExam(examId, userId);
		if (log.isDebugEnabled()) {
			log.debug("缓存我的考试：{examId: {}, userId: {}}", myExam.getExamId(), myExam.getUserId());
		}
		return myExam;
	}

	@Override
	@Cacheable(value = ExamConstant.MYEXAM_CACHE, key = ExamConstant.MYEXAM_UNMARK_LIST_KEY, sync = true)
	public List<MyExam> getUnMarkList() {
		List<MyExam> unMarkList = myExamDao.getUnMarkList();
		if (log.isDebugEnabled()) {
			log.debug("缓存待阅卷列表：{}", unMarkList.stream()//
					.map(myExam -> String.format("{examId: %s, userId: %s, examEndTime: %s}", myExam.getExamId(),
							myExam.getUserId(), DateUtil.formatDateTime(myExam.getExamEndTime())))
					.collect(Collectors.toList()));
		}
		return unMarkList;
	}

	@Override
	@Cacheable(value = ExamConstant.MYEXAM_CACHE, key = ExamConstant.MYEXAM_LIST_KEY_PRE + "#examId", sync = true)
	public List<MyExam> getMyExamList(Integer examId) {
		List<MyExam> myExamList = myExamDao.getList(examId);
		if (log.isDebugEnabled()) {
			log.debug("缓存我的考试列表：{examId: {}, userId: {}}", examId,
					myExamList.stream().map(MyExam::getUserId).collect(Collectors.toList()));
		}
		return myExamList;
	}

	@Override
	@Cacheable(value = ExamConstant.MYQUESTION_CACHE, key = ExamConstant.MYQUESTION_LIST_KEY_PRE
			+ "#examId + ':' + #userId")
	public List<MyQuestion> getMyQuestionList(Integer examId, Integer userId) {
		List<MyQuestion> myQuestionList = myQuestionDao.getList(examId, userId);
		if (log.isDebugEnabled()) {
			log.debug("缓存我的试题列表：{examId: {}, userId: {}, questionId: {}}", examId, userId, myQuestionList.stream()//
					.map(MyQuestion::getQuestionId)//
					.collect(Collectors.toList()));
		}
		return myQuestionList;
	}

}
