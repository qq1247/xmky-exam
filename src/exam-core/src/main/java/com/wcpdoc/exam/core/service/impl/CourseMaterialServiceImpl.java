package com.wcpdoc.exam.core.service.impl;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.wcpdoc.base.util.CurLoginUserUtil;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.CourseMaterialDao;
import com.wcpdoc.exam.core.entity.Course;
import com.wcpdoc.exam.core.entity.CourseMaterial;
import com.wcpdoc.exam.core.entity.CourseQuestion;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionBank;
import com.wcpdoc.exam.core.service.CourseMaterialService;
import com.wcpdoc.exam.core.service.CourseQuestionService;
import com.wcpdoc.exam.core.service.CourseService;
import com.wcpdoc.exam.core.service.ExamCacheService;
import com.wcpdoc.exam.core.service.QuestionBankService;
import com.wcpdoc.exam.core.util.CourseUtil;
import com.wcpdoc.exam.core.util.QuestionBankUtil;
import com.wcpdoc.file.service.FileService;
import com.wcpdoc.search.conf.VideoUtil;

import lombok.RequiredArgsConstructor;

/**
 * 课程资料服务层实现
 * 
 * v1.0 zhanghc 2026-01-12 10:03:53
 */
@Service
@RequiredArgsConstructor
public class CourseMaterialServiceImpl extends BaseServiceImp<CourseMaterial> implements CourseMaterialService {
	private final CourseMaterialDao courseMaterialDao;
	private final CourseService courseService;
	private final CourseQuestionService courseQuestionService;
	private final ExamCacheService examCacheService;
	private final QuestionBankService questionBankService;
	private final FileService fileService;

	@Override
	public RBaseDao<CourseMaterial> getDao() {
		return courseMaterialDao;
	}

	@Override
	public void add(CourseMaterial courseMaterial, LocalTime[] answerTimes, Integer[] questionIds) {
		// 数据校验
		Course course = addValid0(courseMaterial);
		double videoSecond = addValid(courseMaterial, course, answerTimes, questionIds);

		// 课程资料添加
		courseMaterial.setQuestionNum(ValidateUtil.isValid(answerTimes) ? answerTimes.length : 0);
		courseMaterial.setUpdateTime(new Date());
		courseMaterial.setUpdateUserId(getCurUser().getId());
		courseMaterial.setVideoSecond((int) videoSecond);// 向下取整
		courseMaterial.setState(1);
		save(courseMaterial);

		// 课程试题添加
		if (ValidateUtil.isValid(answerTimes) && ValidateUtil.isValid(questionIds)) {
			List<CourseQuestion> courseQuestionList = new ArrayList<>();
			for (int i = 0; i < answerTimes.length; i++) {
				CourseQuestion courseQuestion = new CourseQuestion();
				courseQuestion.setAnswerTime(answerTimes[i]);
				courseQuestion.setQuestionId(questionIds[i]);
				courseQuestion.setCourseId(course.getId());
				courseQuestion.setCourseMaterialId(courseMaterial.getId());
				courseQuestion.setUpdateTime(new Date());
				courseQuestion.setUpdateUserId(getCurUser().getId());
				courseQuestionList.add(courseQuestion);
			}
			courseQuestionList.sort(Comparator.comparing(CourseQuestion::getAnswerTime));// 按答题时间排序
			for (CourseQuestion courseQuestion : courseQuestionList) {
				courseQuestionService.save(courseQuestion);
			}
		}

		// 视频保存
		fileService.upload(courseMaterial.getVideoFileId());
	}

	@Override
	public void update(CourseMaterial courseMaterial, LocalTime[] answerTimes, Integer[] questionIds) {
		// 数据校验
		CourseMaterial entity = updateValid0(courseMaterial);
		double videoSecond = updateValid(courseMaterial, entity, answerTimes, questionIds);

		// 课程资料复制新版本，并删除旧版本
		entity.setState(0);
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		updateById(entity);

		CourseMaterial newCourseMaterial = new CourseMaterial();
		BeanUtils.copyProperties(entity, newCourseMaterial);
		newCourseMaterial.setId(null);
		newCourseMaterial.setState(1);
		newCourseMaterial.setParentId(entity.getId()); // 用于追溯历史

		newCourseMaterial.setName(courseMaterial.getName());
		newCourseMaterial.setContent(courseMaterial.getContent());
		newCourseMaterial.setVideoFileId(courseMaterial.getVideoFileId());
		newCourseMaterial.setVideoSecond((int) videoSecond);// 向下取整
		newCourseMaterial.setQuestionNum(ValidateUtil.isValid(answerTimes) ? answerTimes.length : 0);
		newCourseMaterial.setNo(courseMaterial.getNo());
		newCourseMaterial.setUpdateTime(new Date());
		newCourseMaterial.setUpdateUserId(getCurUser().getId());
		save(newCourseMaterial);

		// 课程试题添加
		if (ValidateUtil.isValid(answerTimes) && ValidateUtil.isValid(questionIds)) {
			List<CourseQuestion> courseQuestionList = new ArrayList<>();
			for (int i = 0; i < answerTimes.length; i++) {
				CourseQuestion courseQuestion = new CourseQuestion();
				courseQuestion.setAnswerTime(answerTimes[i]);
				courseQuestion.setQuestionId(questionIds[i]);
				courseQuestion.setCourseId(newCourseMaterial.getCourseId());
				courseQuestion.setCourseMaterialId(newCourseMaterial.getId());
				courseQuestion.setUpdateTime(new Date());
				courseQuestion.setUpdateUserId(getCurUser().getId());
				courseQuestionList.add(courseQuestion);
			}
			courseQuestionList.sort(Comparator.comparing(CourseQuestion::getAnswerTime));// 按答题时间排序
			for (CourseQuestion courseQuestion : courseQuestionList) {
				courseQuestionService.save(courseQuestion);
			}
		}

		// 视频保存
		fileService.upload(courseMaterial.getVideoFileId());
	}

	@Override
	public void del(Integer id) {
		// 数据校验
		CourseMaterial entity = delValid(id);

		// 课程资料删除
		entity.setState(0);
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		updateById(entity);
	}

	private CourseMaterial delValid(Integer id) {
		if (!ValidateUtil.isValid(id)) {
			throw new MyException("参数错误：id");
		}

		CourseMaterial entity = getById(id);
		Course course = courseService.getById(entity.getCourseId());
		if (!(CurLoginUserUtil.isSelf(course.getCreateUserId()) || CurLoginUserUtil.isAdmin()
				|| CourseUtil.hasWrite(course))) {// 子管理可以改自己创建的或有写权限的，管理员可以改所有
			throw new MyException("无操作权限");
		}
		return entity;
	}

	private double updateValid(CourseMaterial courseMaterial, CourseMaterial entity, LocalTime[] answerTimes,
			Integer[] questionIds) {
		Course course = courseService.getById(entity.getCourseId());
		if (!(CurLoginUserUtil.isSelf(course.getCreateUserId()) || CurLoginUserUtil.isAdmin()
				|| CourseUtil.hasWrite(course))) {// 子管理可以改自己创建的或有写权限的，管理员可以改所有
			throw new MyException("无操作权限");
		}
		if (!ValidateUtil.isValid(courseMaterial.getName())) {
			throw new MyException("参数错误：name");
		}
		if (!ValidateUtil.isValid(courseMaterial.getContent())) {
			throw new MyException("参数错误：content");
		}
		if (!ValidateUtil.isValid(courseMaterial.getVideoFileId())) {
			throw new MyException("参数错误：videoFileId");
		}
		if (!ValidateUtil.isValid(courseMaterial.getNo())) {
			throw new MyException("参数错误：no");
		}
		double videoSecond = VideoUtil.getSecond(fileService.getFileEx(courseMaterial.getVideoFileId()).getFile());
		if (ValidateUtil.isValid(answerTimes) && ValidateUtil.isValid(questionIds)) {
			if (answerTimes.length != questionIds.length) {
				throw new MyException("参数错误：answerTimes");
			}
			if (videoSecond <= 0) {
				throw new MyException("视频文件有误，无法获取时长");
			}
			Set<LocalTime> timeSet = new HashSet<>();
			for (LocalTime answerTime : answerTimes) {
				if (answerTime == null) {
					throw new MyException("参数错误：答题时间不能为空");
				}
				if (!timeSet.add(answerTime)) {
					throw new MyException("参数错误：答题时间不能重复");
				}
				if (answerTime.toSecondOfDay() > videoSecond) {
					throw new MyException("答题时间不能大于视频总时长");
				}
			}
			for (Integer questionId : questionIds) {
				Question question = examCacheService.getQuestion(questionId);
				QuestionBank questionBank = questionBankService.getById(question.getQuestionBankId());
				if (!(CurLoginUserUtil.isSelf(questionBank.getCreateUserId()) || CurLoginUserUtil.isAdmin()
						|| QuestionBankUtil.hasRead(questionBank))) {
					throw new MyException(String.format("试题无权限，编号：%s", questionId));
				}
			}
		}
		return videoSecond;
	}

	private CourseMaterial updateValid0(CourseMaterial courseMaterial) {
		if (!ValidateUtil.isValid(courseMaterial.getId())) {
			throw new MyException("参数错误：id");
		}
		return getById(courseMaterial.getId());
	}

	private double addValid(CourseMaterial courseMaterial, Course course, LocalTime[] answerTimes,
			Integer[] questionIds) {
		if (!(CurLoginUserUtil.isSelf(course.getCreateUserId()) || CurLoginUserUtil.isAdmin()
				|| CourseUtil.hasWrite(course))) {// 子管理可以改自己创建的或有写权限的，管理员可以改所有
			throw new MyException("无操作权限");
		}
		if (!ValidateUtil.isValid(courseMaterial.getName())) {
			throw new MyException("参数错误：name");
		}
		if (!ValidateUtil.isValid(courseMaterial.getContent())) {
			throw new MyException("参数错误：content");
		}
		if (!ValidateUtil.isValid(courseMaterial.getVideoFileId())) {
			throw new MyException("参数错误：videoFileId");
		}
		if (!ValidateUtil.isValid(courseMaterial.getNo())) {
			throw new MyException("参数错误：no");
		}
		double videoSecond = VideoUtil.getSecond(fileService.getFileEx(courseMaterial.getVideoFileId()).getFile());
		if (ValidateUtil.isValid(answerTimes) && ValidateUtil.isValid(questionIds)) {
			if (answerTimes.length != questionIds.length) {
				throw new MyException("参数错误：answerTimes");
			}
			if (videoSecond <= 0) {
				throw new MyException("视频文件有误，无法获取时长");
			}
			Set<LocalTime> timeSet = new HashSet<>();
			for (LocalTime answerTime : answerTimes) {
				if (answerTime == null) {
					throw new MyException("参数错误：答题时间不能为空");
				}
				if (!timeSet.add(answerTime)) {
					throw new MyException("答题时间不能重复");
				}
				if (answerTime.toSecondOfDay() > videoSecond) {
					throw new MyException("答题时间不能大于视频总时长");
				}
			}

			for (Integer questionId : questionIds) {
				Question question = examCacheService.getQuestion(questionId);
				QuestionBank questionBank = questionBankService.getById(question.getQuestionBankId());
				if (!(CurLoginUserUtil.isSelf(questionBank.getCreateUserId()) || CurLoginUserUtil.isAdmin()
						|| QuestionBankUtil.hasRead(questionBank))) {
					throw new MyException(String.format("试题无权限，编号：%s", questionId));
				}
			}
		}

		return videoSecond;
	}

	private Course addValid0(CourseMaterial courseMaterial) {
		if (!ValidateUtil.isValid(courseMaterial.getCourseId())) {
			throw new MyException("参数错误：courseId");
		}
		return courseService.getById(courseMaterial.getCourseId());
	}
}
