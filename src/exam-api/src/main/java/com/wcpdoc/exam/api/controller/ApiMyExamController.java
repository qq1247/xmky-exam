package com.wcpdoc.exam.api.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.service.ExamCacheService;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyQuestionService;
import com.wcpdoc.exam.core.util.ExamUtil;
import com.wcpdoc.exam.core.util.MyExamUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 我的考试控制层
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
@RestController
@RequestMapping("/api/myExam")
@Slf4j
public class ApiMyExamController extends BaseController {

	@Resource
	private MyExamService myExamService;
	@Resource
	private MyQuestionService myQuestionService;
	@Resource
	private ExamService examService;
	@Resource
	private ExamCacheService examCacheService;

	/**
	 * 我的考试列表
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/listpage")
	public PageResult listpage(PageIn pageIn) {
		try {
			pageIn.addParm("curUserId", getCurUser().getId());
			PageOut pageOut = myExamService.getListpage(pageIn);
			for (Map<String, Object> map : pageOut.getList()) {
				Exam exam = new Exam();
				exam.setScoreState((Integer) map.remove("examScoreState"));// 页面不需要字段用remove
				exam.setMarkState((Integer) map.get("examMarkState"));
				MyExam myExam = new MyExam();
				myExam.setMarkState((Integer) map.get("markState"));

				if (!MyExamUtil.scoreShow(exam, myExam)) {// 成绩查询状态（1：考试结束后；2：不公布；3：交卷后）
					map.put("totalScore", null);// 不显示分数
					map.put("answerState", null);// 不显示及格状态
				}
				if ((Integer) map.remove("examRankState") == 2) {// 排名状态（1：公布；2：不公布）
					map.put("no", null);// 不显示排名
				}
			}

			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("我的考试列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 我的考试
	 * 
	 * v1.0 zhanghc 2022年11月2日下午2:38:55
	 * 
	 * @param examId
	 * @return PageResult
	 */
	@RequestMapping("/get")
	public PageResult get(Integer examId) {
		try {
			MyExam myExam = examCacheService.getMyExam(examId, getCurUser().getId());
			if (myExam == null) {
				throw new MyException("无查阅权限");
			}
			Exam exam = examCacheService.getExam(examId);
			return PageResultEx.ok()// 考试用户没有exam/get权限，所以字段在这里回显
					.addAttr("examMarkState", exam.getMarkState()) // 页面控制是否显示错题
					.addAttr("examScoreState", exam.getScoreState())// 页面控制是否显示错题
					.addAttr("examRankState", exam.getRankState())// 页面控制是否显示排名
					.addAttr("examMarkStartTime", exam.getMarkStartTime())
					.addAttr("examMarkEndTime", exam.getMarkEndTime())// 如果是交卷后公布，但试卷是主观题试卷，页面提示几点之后查询
					.addAttr("examName", exam.getName())// 考试名称
					.addAttr("examStartTime",
							ExamUtil.hasTimeLimit(exam) ? myExam.getExamStartTime() : exam.getStartTime())// 我的考试结束时间（进入我的试卷使用）
					.addAttr("examEndTime", ExamUtil.hasTimeLimit(exam) ? myExam.getExamEndTime() : exam.getEndTime())//
					.addAttr("examStartTime", myExam.getExamStartTime())//
					.addAttr("answerEndTime", myExam.getExamEndTime())//
					.addAttr("markStartTime", myExam.getMarkStartTime())//
					.addAttr("markEndTime", myExam.getMarkEndTime())//
					.addAttr("objectiveScore", myExam.getObjectiveScore())//
					.addAttr("totalScore", MyExamUtil.scoreShow(exam, myExam) ? myExam.getTotalScore() : null)//
					.addAttr("answerState", MyExamUtil.scoreShow(exam, myExam) ? myExam.getAnswerState() : null)//
					.addAttr("state", myExam.getState())//
					.addAttr("markState", myExam.getMarkState())//
					.addAttr("no", exam.getRankState() == 1 ? myExam.getNo() : null)//
					.addAttr("userNum", exam.getRankState() == 1 ? exam.getUserNum() : null);
		} catch (MyException e) {
			log.error("获取我的考试错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("获取我的考试错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 我的试卷
	 * 
	 * v1.0 zhanghc 2022年5月18日下午1:21:07
	 * 
	 * @param examId
	 * @return PageResult
	 */
	@RequestMapping("/paper")
	public PageResult paper(Integer examId) {
		try {
			return PageResultEx.ok().data(myExamService.paper(examId, getCurUser().getId()));
		} catch (MyException e) {
			log.error("我的试卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("我的试卷错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 答题
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * 
	 * @param examId
	 * @param questionId
	 * @param answers
	 * @return PageResult
	 */
	@RequestMapping("/answer")
	public PageResult answer(Integer examId, Integer questionId, String[] answers) {
		try {
			myExamService.answer(examId, getCurUser().getId(), questionId, answers);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("答题错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("答题错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 交卷
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * 
	 * @param examId
	 * @return PageResult
	 */
	@RequestMapping("/finish")
	public PageResult finish(Integer examId) {
		try {
			myExamService.finish(examId, getCurUser().getId());
			return PageResult.ok();
		} catch (MyException e) {
			log.error("交卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("交卷错误：", e);
			return PageResult.err();
		}
	}
}