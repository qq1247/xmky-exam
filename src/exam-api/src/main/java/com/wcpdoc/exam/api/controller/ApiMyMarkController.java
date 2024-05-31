package com.wcpdoc.exam.api.controller;

import java.math.BigDecimal;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.base.util.CurLoginUserUtil;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.service.ExamCacheService;
import com.wcpdoc.exam.core.service.MyMarkService;

import lombok.extern.slf4j.Slf4j;

/**
 * 我的阅卷控制层
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
@RestController
@RequestMapping("/api/myMark")
@Slf4j
public class ApiMyMarkController extends BaseController {

	@Resource
	private MyMarkService myMarkService;
	@Resource
	private ExamCacheService examCacheService;

	/**
	 * 我的阅卷列表
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/listpage")
	public PageResult listpage(PageIn pageIn) {
		try {
			if (CurLoginUserUtil.isAdmin()) {// 管理员看所有

			} else if (CurLoginUserUtil.isSubAdmin()) {// 子管理员看自己
				pageIn.addParm("subAdminUserId", getCurUser().getId());
			} else if (CurLoginUserUtil.isMarkUser()) {// 阅卷用户看（管理或子管理）分配的
				pageIn.addParm("markUserId", getCurUser().getId());
			} else if (CurLoginUserUtil.isExamUser()) {// 考试用户没有权限
				throw new MyException("无权限");
			}
			PageOut pageOut = myMarkService.getListpage(pageIn);
			for (Map<String, Object> map : pageOut.getList()) {
				if (map.get("examSxes") == null) {
					map.put("examSxes", new Integer[0]);
				} else {
					map.put("examSxes", ((String) map.get("examSxes")).split(","));
				}
			}
			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("我的阅卷列表错误：", e);
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
	public PageResult get(Integer examId, Integer userId) {
		try {
			MyExam myExam = examCacheService.getMyExam(examId, userId);
			Exam exam = examCacheService.getExam(examId);
			return PageResultEx.ok()//
					.addAttr("examMarkState", exam.getMarkState()) // 页面控制是否显示错题
					.addAttr("examScoreState", exam.getScoreState())// 页面控制是否显示错题
					.addAttr("examRankState", exam.getRankState())// 页面控制是否显示排名
					.addAttr("examStartTime", myExam.getExamStartTime())//
					.addAttr("answerEndTime", myExam.getExamEndTime())//
					.addAttr("markStartTime", myExam.getMarkStartTime())//
					.addAttr("markEndTime", myExam.getMarkEndTime())//
					.addAttr("objectiveScore", myExam.getObjectiveScore())//
					.addAttr("totalScore", myExam.getTotalScore())//
					.addAttr("state", myExam.getState())//
					.addAttr("markState", myExam.getMarkState())//
					.addAttr("answerState", myExam.getAnswerState())//
					.addAttr("no", exam.getRankState() == 1 ? myExam.getNo() : null)//
					.addAttr("userNum", exam.getRankState() == 1 ? exam.getUserNum() : null)//
			;
		} catch (MyException e) {
			log.error("我的考试错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("我的考试错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 考试用户列表
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/userListpage")
	public PageResult userListpage(PageIn pageIn) {
		try {
			pageIn.addParm("curUserId", getCurUser().getId());
			PageOut pageOut = myMarkService.getUserListpage(pageIn);
			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("我的阅卷用户列表：", e);
			return PageResult.err();
		}
	}

	/**
	 * 试卷分配
	 * 
	 * v1.0 zhanghc 2023年2月23日下午2:31:16
	 * 
	 * @param examId 考试ID
	 * @param num    分配份数
	 * @return PageResult
	 */
	@RequestMapping("/assign")
	public PageResult assign(Integer examId, Integer num) {
		try {
			myMarkService.assign(examId, num);
			return PageResultEx.ok();
		} catch (MyException e) {
			log.error("试卷分配错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("试卷分配错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 获取用户试卷
	 * 
	 * v1.0 zhanghc 2022年5月18日下午1:21:07
	 * 
	 * @param examId
	 * @param userId
	 * @return PageResult
	 */
	@RequestMapping("/paper")
	public PageResult paper(Integer examId, Integer userId) {
		try {
			return PageResultEx.ok().data(myMarkService.paper(examId, userId));
		} catch (MyException e) {
			log.error("获取用户试卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("获取用户试卷错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 打分
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * 
	 * @param examId     考试ID
	 * @param userId     考试用户ID
	 * @param questionId 试题ID
	 * @param userScore  用户得分
	 * @return PageResult
	 */
	@RequestMapping("/score")
	public PageResult score(Integer examId, Integer userId, Integer questionId, BigDecimal userScore) {
		try {
			myMarkService.score(examId, userId, questionId, userScore);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("打分错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("打分错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 阅卷
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * 
	 * @param examId
	 * @return PageResult
	 */
	@RequestMapping("/finish")
	public PageResult finish(Integer examId, Integer userId) {
		try {
			myMarkService.finish(examId, userId);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("阅卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("阅卷错误：", e);
			return PageResult.err();
		}
	}
}