package com.wcpdoc.exam.api.controller;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.base.util.CurLoginUserUtil;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.exam.core.entity.Course;
import com.wcpdoc.exam.core.entity.CourseMaterial;
import com.wcpdoc.exam.core.entity.CourseQuestion;
import com.wcpdoc.exam.core.service.CourseMaterialService;
import com.wcpdoc.exam.core.service.CourseQuestionService;
import com.wcpdoc.exam.core.service.CourseService;
import com.wcpdoc.exam.core.service.ExamCacheService;
import com.wcpdoc.exam.core.util.CourseUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 课程资料控制层
 * 
 * v1.0 zhanghc 2026-01-12 10:03:53
 */
@RestController
@RequestMapping("/api/course-material")
@RequiredArgsConstructor
@Slf4j
public class ApiCourseMaterialController extends BaseController {
	private final CourseMaterialService courseMaterialService;
	private final CourseService courseService;
	private final CourseQuestionService courseQuestionService;
	private final ExamCacheService examCacheService;

	/**
	 * 课程资料列表
	 * 
	 * v1.0 zhanghc 2026-01-12 10:03:53
	 * 
	 * @param pageIn
	 * @return PageResult
	 */
	@RequestMapping("/listpage")
	public PageResult listpage(PageIn pageIn) {
		try {
			if (!CurLoginUserUtil.isAdmin()) {// 考试用户、阅卷用户没有权限；子管理员看自己；管理员看所有；
				pageIn.addParm("curUserId", getCurUser().getId());
			}

			PageOut pageOut = courseMaterialService.getListpage(pageIn);
			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("课程资料列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 课程资料添加
	 * 
	 * v1.0 zhanghc 2026-01-12 10:03:53
	 * 
	 * @param courseMaterial
	 * @return PageResult
	 */
	@RequestMapping("/add")
	public PageResult add(CourseMaterial courseMaterial, LocalTime[] answerTimes, Integer[] questionIds) {
		try {
			courseMaterialService.add(courseMaterial, answerTimes, questionIds);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("课程资料添加错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("课程资料添加错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 课程资料修改
	 * 
	 * v1.0 zhanghc 2026-01-12 10:03:53
	 * 
	 * @param courseMaterial
	 * @return PageResult
	 */
	@RequestMapping("/edit")
	public PageResult edit(CourseMaterial courseMaterial, LocalTime[] answerTimes, Integer[] questionIds) {
		try {
			courseMaterialService.update(courseMaterial, answerTimes, questionIds);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("课程资料修改错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("课程资料修改错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 课程资料删除
	 * 
	 * v1.0 zhanghc 2026-01-12 10:03:53
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/del")
	public PageResult del(Integer id) {
		try {
			courseMaterialService.del(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("课程资料删除错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("课程资料删除错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 课程资料获取
	 * 
	 * v1.0 zhanghc 2026-01-12 10:03:53
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/get")
	public PageResult get(Integer id) {
		try {
			CourseMaterial entity = courseMaterialService.getById(id);
			Course course = courseService.getById(entity.getCourseId());
			if (!(CurLoginUserUtil.isSelf(course.getCreateUserId()) || CurLoginUserUtil.isAdmin()
					|| CourseUtil.hasWrite(course))) {
				throw new MyException("无操作权限");
			}

			List<CourseQuestion> courseQuestionList = courseQuestionService.getList(entity.getId());

			return PageResultEx.ok() //
					.addAttr("id", entity.getId()) //
					.addAttr("name", entity.getName()) //
					.addAttr("content", entity.getContent()) //
					.addAttr("videoFileId", entity.getVideoFileId()) //
					.addAttr("no", entity.getNo()) //
					.addAttr("courseQuestions", courseQuestionList.stream().map(courseQuestion -> {
						Map<String, Object> data = new HashMap<>();
						data.put("answerTime", courseQuestion.getAnswerTime());
						data.put("questionId", courseQuestion.getQuestionId());
						data.put("questionTitle",
								examCacheService.getQuestion(courseQuestion.getQuestionId()).getTitle());
						return data;
					}));
		} catch (MyException e) {
			log.error("课程资料获取错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("课程资料获取错误：", e);
			return PageResult.err();
		}
	}

}
