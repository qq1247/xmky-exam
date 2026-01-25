package com.wcpdoc.exam.api.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.entity.Course;
import com.wcpdoc.exam.core.entity.MyCourseMaterial;
import com.wcpdoc.exam.core.entity.MyCourseQuestion;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.entity.ex.QuestionPart;
import com.wcpdoc.exam.core.service.CourseService;
import com.wcpdoc.exam.core.service.ExamCacheService;
import com.wcpdoc.exam.core.service.MyCourseMaterialService;
import com.wcpdoc.exam.core.service.MyCourseQuestionService;
import com.wcpdoc.exam.core.util.QuestionUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 我的课程控制层
 * 
 * v1.0 zhanghc 2026-01-15 12:05:06
 */
@RestController
@RequestMapping("/api/my-course")
@RequiredArgsConstructor
@Slf4j
public class ApiMyCourseController extends BaseController {
	private final BaseCacheService baseCacheService;
	private final CourseService courseService;
	private final MyCourseMaterialService myCourseMaterialService;
	private final MyCourseQuestionService myCourseQuestionService;
	private final ExamCacheService examCacheService;

	/**
	 * 课程列表
	 * 
	 * v1.0 zhanghc 2026-01-22 21:03:24
	 * 
	 * @param pageIn
	 * @return PageResult
	 */
	@RequestMapping("/course-listpage")
	public PageResult exerListpage(PageIn pageIn) {
		try {
			User user = baseCacheService.getUser(getCurUser().getId());
			pageIn.addParm("examUserId", user.getId());
			pageIn.addParm("examOrgId", user.getOrgId());
			pageIn.addParm("state", 1);

			PageOut pageOut = courseService.getListpage(pageIn);
			for (Map<String, Object> map : pageOut.getList()) {
				map.remove("orgIds");
				map.remove("userIds");
				map.remove("updateTime");
				map.remove("shareAuth");
				map.remove("createUserName");
			}
			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("课程列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 我的课程资料生成
	 * 
	 * v1.0 zhanghc 2026-01-23 09:49:44
	 * 
	 * @param courseId
	 * @return PageResult
	 */
	@RequestMapping("/generate")
	public PageResult generate(Integer courseId) {
		try {
			myCourseMaterialService.generate(courseId);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("我的课程资料生成错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("我的课程资料生成错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 我的课程列表
	 * 
	 * v1.0 zhanghc 2026-01-15 12:05:06
	 * 
	 * @param courseId
	 * @return pageOut
	 */
	@RequestMapping("/list")
	public PageResult list(Integer courseId) {
		try {
			List<Map<String, Object>> mcmList = new ArrayList<>();
			List<MyCourseMaterial> myCourseMaterialList = myCourseMaterialService.getList(courseId);
			myCourseMaterialList.forEach(myCourseMaterial -> {
				Map<String, Object> mcm = new HashMap<>();
				mcm.put("courseId", myCourseMaterial.getCourseId());
				mcm.put("courseMaterialId", myCourseMaterial.getCourseMaterialId());
				mcm.put("userId", myCourseMaterial.getUserId());
				mcm.put("name", myCourseMaterial.getName());
				mcm.put("content", myCourseMaterial.getContent());
				mcm.put("videoFileId", myCourseMaterial.getVideoFileId());
				mcm.put("videoTime", myCourseMaterial.getVideoTime());
				mcm.put("no", myCourseMaterial.getNo());
				mcm.put("watchTime", myCourseMaterial.getWatchTime());
				mcm.put("state", myCourseMaterial.getState());
				mcm.put("questions", new ArrayList<>());

				List<MyCourseQuestion> myCourseQuestionList = myCourseQuestionService.getList(getCurUser().getId(),
						myCourseMaterial.getCourseMaterialId());
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> mcqList = (List<Map<String, Object>>) mcm.get("questions");
				myCourseQuestionList.forEach(myCourseQuestion -> {
					Map<String, Object> mcq = new HashMap<>();
					mcq.put("questionId", myCourseQuestion.getQuestionId());
					mcq.put("courseTime", myCourseQuestion.getCourseTime());
					mcq.put("answerTime", myCourseQuestion.getAnswerTime());
					mcqList.add(mcq);
				});

				mcmList.add(mcm);
			});

			return PageResultEx.ok().data(mcmList);
		} catch (Exception e) {
			log.error("我的课程列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 我的课程试题
	 * 
	 * v1.0 zhanghc 2026-01-23 23:21:22
	 * 
	 * @param id
	 * @param questionId
	 * @return PageResult
	 */
	@RequestMapping("/question")
	public PageResult question(Integer courseMaterialId, Integer questionId) {
		try {
			// 数据校验
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

			// 查询练习试题
			Question question = examCacheService.getQuestion(myCourseQuestion.getQuestionId());
			QuestionPart questionPart = new QuestionPart();
			questionPart.setType(2);
			questionPart.setQuestionId(question.getId());
			questionPart.setQuestionType(question.getType());
			questionPart.setMarkType(question.getMarkType());
			questionPart.setTitle(question.getTitle());
			questionPart.setImgFileIds(question.getImgFileIds());
			questionPart.setVideoFileId(question.getVideoFileId());
			questionPart.setMarkOptions(question.getMarkOptions());
			questionPart.setScore(question.getScore());
			questionPart.setUserScore(null);
			if (QuestionUtil.hasSingleChoice(question) || QuestionUtil.hasMultipleChoice(question)) {// 组装试题选项
				List<QuestionOption> questionOptionList = examCacheService.getQuestionOptionList(question.getId());
				for (QuestionOption questionOption : questionOptionList) {
					questionPart.getOptions().add(questionOption.getOptions());
				}
			}
			return PageResultEx.ok().data(questionPart);
		} catch (MyException e) {
			log.error("我的课程试题错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("我的课程试题错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 我的课程答题
	 * 
	 * v1.0 zhanghc 2026-01-24 13:08:58
	 * 
	 * @param courseMaterialId
	 * @param questionId
	 * @param userAnswers
	 * @return PageResult
	 */
	@RequestMapping("/answer")
	public PageResult answer(Integer courseMaterialId, Integer questionId, String[] userAnswers) {
		try {
			return PageResultEx.ok().data(myCourseMaterialService.answer(courseMaterialId, questionId, userAnswers));
		} catch (MyException e) {
			log.error("我的课程答题错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("我的课程答题错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 我的课程完成
	 * 
	 * v1.0 zhanghc 2026-01-24 23:00:20
	 * 
	 * @param courseMaterialId
	 * @param questionId
	 * @param userAnswers
	 * @return PageResult
	 */
	@RequestMapping("/finish")
	public PageResult finish(Integer courseMaterialId) {
		try {
			myCourseMaterialService.finish(courseMaterialId);
			return PageResultEx.ok();
		} catch (MyException e) {
			log.error("我的课程完成错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("我的课程完成错误：", e);
			return PageResult.err();
		}
	}
}
