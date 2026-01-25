package com.wcpdoc.exam.core.service.impl;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.MyCourseMaterialDao;
import com.wcpdoc.exam.core.entity.Course;
import com.wcpdoc.exam.core.entity.CourseMaterial;
import com.wcpdoc.exam.core.entity.CourseQuestion;
import com.wcpdoc.exam.core.entity.MyCourseMaterial;
import com.wcpdoc.exam.core.entity.MyCourseQuestion;
import com.wcpdoc.exam.core.entity.MyExamQuestion;
import com.wcpdoc.exam.core.entity.MyWrongQuestion;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.service.CourseMaterialService;
import com.wcpdoc.exam.core.service.CourseQuestionService;
import com.wcpdoc.exam.core.service.CourseService;
import com.wcpdoc.exam.core.service.ExamCacheService;
import com.wcpdoc.exam.core.service.MyCourseMaterialService;
import com.wcpdoc.exam.core.service.MyCourseQuestionService;
import com.wcpdoc.exam.core.service.MyWrongQuestionService;
import com.wcpdoc.exam.core.util.MyExamUtil;
import com.wcpdoc.exam.core.util.QuestionUtil;

import lombok.RequiredArgsConstructor;

/**
 * 我的课程资料服务层实现
 * 
 * v1.0 zhanghc 2026-01-23 09:50:50
 */
@Service
@RequiredArgsConstructor
public class MyCourseMaterialServiceImpl extends BaseServiceImp<MyCourseMaterial> implements MyCourseMaterialService {

	private final MyCourseMaterialDao myCourseMaterialDao;
	private final BaseCacheService baseCacheService;
	private final CourseService courseService;
	private final CourseMaterialService courseMaterialService;
	private final CourseQuestionService courseQuestionService;
	private final MyCourseQuestionService myCourseQuestionService;
	private final ExamCacheService examCacheService;
	private final MyWrongQuestionService myWrongQuestionService;

	@Override
	public RBaseDao<MyCourseMaterial> getDao() {
		return myCourseMaterialDao;
	}

	@Override
	public void generate(Integer courseId) {
		// 数据校验
		generateValid(courseId);

		// 我的课程资料对比更新（管理员添加或删除的课程同步到用户课程）
		List<CourseMaterial> courseMaterialList = courseMaterialService.getList(courseId);
		List<MyCourseMaterial> myCourseMaterialList = myCourseMaterialDao.getList(getCurUser().getId(), courseId);
		courseMaterialList.stream()
				.filter(courseMaterial -> myCourseMaterialList.stream().noneMatch(myCourseMaterial -> myCourseMaterial
						.getCourseMaterialId().intValue() == courseMaterial.getId().intValue()))
				.forEach(courseMaterial -> {
					MyCourseMaterial myCourseMaterial = new MyCourseMaterial();
					myCourseMaterial.setCourseId(courseMaterial.getCourseId());
					myCourseMaterial.setCourseMaterialId(courseMaterial.getId());
					myCourseMaterial.setUserId(getCurUser().getId());
					myCourseMaterial.setName(courseMaterial.getName());
					myCourseMaterial.setContent(courseMaterial.getContent());
					myCourseMaterial.setVideoFileId(courseMaterial.getVideoFileId());
					myCourseMaterial.setVideoTime(courseMaterial.getVideoTime());
					myCourseMaterial.setQuestionNum(courseMaterial.getQuestionNum());
					myCourseMaterial.setNo(courseMaterial.getNo());
					myCourseMaterial.setWatchTime(LocalTime.of(0, 0, 0));
					myCourseMaterial.setState(2); // 状态（0：删除；1：完成；2：未开始；3：进行中）
					myCourseMaterial.setUpdateUserId(getCurUser().getId());
					myCourseMaterial.setUpdateTime(new Date());
					save(myCourseMaterial);

					List<CourseQuestion> courseQuestionList = courseQuestionService.getList(courseMaterial.getId());
					courseQuestionList.forEach(courseQuestion -> {
						MyCourseQuestion myCourseQuestion = new MyCourseQuestion();
						myCourseQuestion.setCourseId(courseQuestion.getCourseId());
						myCourseQuestion.setCourseMaterialId(courseQuestion.getCourseMaterialId());
						myCourseQuestion.setQuestionId(courseQuestion.getQuestionId());
						myCourseQuestion.setCourseTime(courseQuestion.getCourseTime());
						myCourseQuestion.setUserId(getCurUser().getId());
						myCourseQuestion.setAnswerTime(null);
						myCourseQuestion.setState(1);
						myCourseQuestion.setUpdateUserId(getCurUser().getId());
						myCourseQuestion.setUpdateTime(new Date());
						myCourseQuestionService.save(myCourseQuestion);
					});

				});
		myCourseMaterialList.stream()
				.filter(myCourseMaterial -> courseMaterialList.stream().noneMatch(courseMaterial -> courseMaterial
						.getId().intValue() == myCourseMaterial.getCourseMaterialId().intValue()))
				.forEach(myCourseMaterial -> {
					myCourseMaterial.setState(0);
					myCourseMaterial.setUpdateUserId(getCurUser().getId());
					myCourseMaterial.setUpdateTime(new Date());
					updateById(myCourseMaterial);

					List<MyCourseQuestion> myCourseQuestionList = myCourseQuestionService.getList(getCurUser().getId(),
							courseId);
					myCourseQuestionList.forEach(mcq -> {
						mcq.setState(0);
						mcq.setUpdateTime(new Date());
						mcq.setUpdateUserId(getCurUser().getId());
						myCourseQuestionService.updateById(mcq);
					});
				});
	}

	@Override
	public List<MyCourseMaterial> getList(Integer courseId) {
		return myCourseMaterialDao.getList(getCurUser().getId(), courseId);
	}

	@Override
	public Boolean answer(Integer courseMaterialId, Integer questionId, String[] userAnswers) {
		// 数据校验
		MyCourseQuestion myCourseQuestion = answerValid0(courseMaterialId, questionId, userAnswers);
		Course course = answerValid(myCourseQuestion, questionId, userAnswers);

		// 打分
		Question question = examCacheService.getQuestion(questionId);
		List<QuestionAnswer> questionAnswerList = examCacheService.getQuestionAnswerList(questionId);

		MyExamQuestion myExamQuestion = new MyExamQuestion();
		myExamQuestion.setScore(question.getScore());
		myExamQuestion.setScores(questionAnswerList.stream().map(QuestionAnswer::getScore).toList());
		myExamQuestion.setMarkOptions(question.getMarkOptions());

		if (!ValidateUtil.isValid(userAnswers)) {
			myExamQuestion.setUserAnswer(null);
		} else if (QuestionUtil.hasJudge(question)) {
			myExamQuestion.setUserAnswer(userAnswers[0]);
		} else if (QuestionUtil.hasSingleChoice(question)) {
			myExamQuestion.setUserAnswer(userAnswers[0]);
		} else if (QuestionUtil.hasMultipleChoice(question)) {
			myExamQuestion.setUserAnswer(StringUtil.join(userAnswers));
		} else if (QuestionUtil.hasFillBlank(question)) {
			myExamQuestion.setUserAnswer(StringUtil.join(userAnswers, '\n'));
		} else if (QuestionUtil.hasQA(question)) {
			myExamQuestion.setUserAnswer(StringUtil.join(userAnswers));// bug：文本包含英文逗号会分割
		}

		if (QuestionUtil.hasQA(question)) {
			MyExamUtil.qAHandle(question, questionAnswerList, myExamQuestion);// 问答处理
		} else if (QuestionUtil.hasSingleChoice(question) || QuestionUtil.hasJudge(question)) {
			MyExamUtil.singleChoiceHandle(question, questionAnswerList, myExamQuestion);// 单选判断处理
		} else if (QuestionUtil.hasMultipleChoice(question)) {
			MyExamUtil.multipleChoiceHandle(question, questionAnswerList, myExamQuestion);// 多选处理
		} else if (QuestionUtil.hasFillBlank(question)) {
			MyExamUtil.fillBlankHandle(question, questionAnswerList, myExamQuestion);// 填空处理
		}

		// 如果答错了，该题错误次数+1
		if (myExamQuestion.getScore().doubleValue() != myExamQuestion.getUserScore().doubleValue()) {
			MyWrongQuestion myWrongQuestion = myWrongQuestionService.getMyWrongQuestion(getCurUser().getId(),
					questionId);
			if (myWrongQuestion == null) {
				myWrongQuestion = new MyWrongQuestion();
				myWrongQuestion.setUserId(getCurUser().getId());
				myWrongQuestion.setQuestionId(questionId);
				myWrongQuestion.setQuestionType(question.getType());
				myWrongQuestion.setWrongNum(1);
				myWrongQuestion.setFirstWrongTime(new Date());
				myWrongQuestion.setFirstWrongSource(course.getName());
				myWrongQuestion.setLastWrongTime(new Date());
				myWrongQuestion.setLastWrongSource(course.getName());
				myWrongQuestion.setState(2);
				myWrongQuestion.setUpdateUserId(getCurUser().getId());
				myWrongQuestion.setUpdateTime(new Date());
				myWrongQuestionService.save(myWrongQuestion);
			} else {
				myWrongQuestion.setWrongNum(myWrongQuestion.getWrongNum() + 1);
				myWrongQuestion.setLastWrongTime(new Date());
				myWrongQuestion.setLastWrongSource(course.getName());
				myWrongQuestion.setState(2); // 不管是否标记已掌握，只要错了，就标记为未掌握
				myWrongQuestion.setUpdateUserId(getCurUser().getId());
				myWrongQuestion.setUpdateTime(new Date());
				myWrongQuestionService.updateById(myWrongQuestion);
			}

			return false;
		}

		myCourseQuestion.setAnswerTime(new Date());
		myCourseQuestion.setUpdateTime(new Date());
		myCourseQuestion.setUpdateUserId(getCurUser().getId());
		myCourseQuestionService.updateById(myCourseQuestion);
		return true;
	}

	@Override
	public void finish(Integer courseMaterialId) {
		MyCourseMaterial myCourseMaterial = finishValid(courseMaterialId);
		if (myCourseMaterial.getState() == 2 || myCourseMaterial.getState() == 3) {
			myCourseMaterial.setState(1);
			myCourseMaterial.setUpdateTime(new Date());
			myCourseMaterial.setUpdateUserId(getCurUser().getId());
			updateById(myCourseMaterial);
		}
	}

	private MyCourseMaterial finishValid(Integer courseMaterialId) {
		if (!ValidateUtil.isValid(courseMaterialId)) {
			throw new MyException("参数错误：courseMaterialId");
		}

		MyCourseMaterial myCourseMaterial = myCourseMaterialDao.getMyCourseMaterial(getCurUser().getId(),
				courseMaterialId);
		if (myCourseMaterial == null) {
			throw new MyException("参数错误：courseMaterialId");
		}

		List<MyCourseQuestion> myCourseQuestionList = myCourseQuestionService.getList(getCurUser().getId(),
				courseMaterialId);
		for (MyCourseQuestion myCourseQuestion : myCourseQuestionList) {
			if (!ValidateUtil.isValid(myCourseQuestion.getAnswerTime())) {
				throw new MyException(String.format("课程试题未答：%s", myCourseQuestion.getCourseTime()));
			}
		}

		return myCourseMaterial;
	}

	private Course answerValid(MyCourseQuestion myCourseQuestion, Integer questionId, String[] userAnswers) {
		Course course = courseService.getById(myCourseQuestion.getCourseId());
		if (course.getState() == 0) {
			throw new MyException("课程已删除");
		}
		if (course.getState() == 2) {
			throw new MyException("课程已暂停");
		}

		User curUser = baseCacheService.getUser(getCurUser().getId());
		if (!(course.getOrgIds().contains(curUser.getOrgId()) || course.getUserIds().contains(curUser.getId()))) {
			throw new MyException("无操作权限");
		}
		return course;
	}

	private MyCourseQuestion answerValid0(Integer courseMaterialId, Integer questionId, String[] userAnswers) {
		if (!ValidateUtil.isValid(courseMaterialId)) {
			throw new MyException("参数错误：courseMaterialId");
		}
		if (!ValidateUtil.isValid(questionId)) {
			throw new MyException("参数错误：questionId");
		}

		MyCourseQuestion myCourseQuestion = myCourseQuestionService.getMyCourseQuestion(getCurUser().getId(),
				courseMaterialId, questionId);
		if (myCourseQuestion == null) {
			throw new MyException("无操作权限");
		}

		Question question = examCacheService.getQuestion(myCourseQuestion.getQuestionId());
		if (QuestionUtil.hasSubjective(question)) {
			throw new MyException("非客观试题");
		}
		return myCourseQuestion;
	}

	private void generateValid(Integer courseId) {
		if (!ValidateUtil.isValid(courseId)) {
			throw new MyException("参数错误：courseId");
		}
		Course course = courseService.getById(courseId);
		if (course == null) {
			throw new MyException("课程不存在");
		}
		if (course.getState() == 0) {
			throw new MyException("课程已删除");
		}
		if (course.getState() == 2) {
			throw new MyException("课程已暂停");
		}

		User user = baseCacheService.getUser(getCurUser().getId());
		if (!(course.getUserIds().contains(user.getId()) || course.getOrgIds().contains(user.getOrgId()))) {
			throw new MyException("无操作权限");
		}
	}
}
