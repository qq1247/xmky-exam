package com.wcpdoc.exam.api.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.base.entity.Org;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.OrgService;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.exam.core.cache.AutoMarkCache;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyMark;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.MyExamDetailService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyMarkService;
import com.wcpdoc.exam.core.service.PaperService;

/**
 * 我的阅卷控制层
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
@Controller
@RequestMapping("/api/myMark")
public class ApiMyMarkController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiMyMarkController.class);
	
	@Resource
	private MyExamService myExamService;
	@Resource
	private ExamService examService;
	@Resource
	private MyMarkService myMarkService;
	@Resource
	private MyExamDetailService myExamDetailService;
	@Resource
	private UserService userService;
	@Resource
	private OrgService orgService;
	@Resource
	private PaperService paperService;
	
	/**
	 * 我的阅卷列表
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return pageOut
	 */
	@RequestMapping("/listpage")
	@ResponseBody
	public PageResult listpage() {
		try {
			PageIn pageIn = new PageIn(request);
			pageIn.addAttr("curUserId", getCurUser().getId());
			PageOut pageOut = myMarkService.getListpage(pageIn);
			
			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("我的阅卷列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 阅卷
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * @param examId
	 * @param userId
	 * @param questionId
	 * @param score
	 * @return PageResult
	 */
	@RequestMapping("/score")
	@ResponseBody
	public PageResult score(Integer examId, Integer userId, Integer questionId, BigDecimal score) {
		try {
			if (!AutoMarkCache.tryReadLock(examId, 2000)) {
				throw new MyException("尝试加读锁失败");
			}
			myMarkService.scoreUpdate(examId, userId, questionId, score);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("阅卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("阅卷错误：", e);
			return PageResult.err();
		} finally {
			AutoMarkCache.releaseReadLock(examId);
		}
	}
	
	/**
	 * 交卷
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * @param examId
	 * @return PageResult
	 */
	@RequestMapping("/finish")
	@ResponseBody
	public PageResult finish(Integer examId, Integer userId) {
		try {
			if (!AutoMarkCache.tryReadLock(examId, 2000)) {
				throw new MyException("尝试加读锁失败");
			}
			myMarkService.finish(examId, userId);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成阅卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成阅卷错误：", e);
			return PageResult.err();
		} finally {
			AutoMarkCache.releaseReadLock(examId);
		}
	}
	
	/**
	 * 考试用户列表
	 * 
	 * v1.0 chenyun 2021年8月2日下午3:14:45
	 * @param examId
	 * @return PageResult
	 */
	@RequestMapping("/userList")
	@ResponseBody
	public PageResult userList(Integer examId) {
		try {
			// 校验数据有效性
			List<MyMark> myMarkList = myMarkService.getList(examId);
			MyMark myMark = null;
			for (MyMark _myMark : myMarkList) {
				if (_myMark.getMarkUserId().intValue() == getCurUser().getId().intValue()) {
					myMark = _myMark;
					break;
				}
			}
			if (myMark == null) {
				throw new MyException("未参与该场考试");
			}
			
			// 返回考试用户IDS
			List<User> userList = userService.getList(myMark.getExamUserIdArr());
			List<Map<String, Object>> result = new ArrayList<>();
			Map<Integer, Org> orgCache = new HashMap<>();// 机构不会很多，缓存利用
			Exam exam = examService.getEntity(examId);
			Paper paper = paperService.getEntity(exam.getPaperId());
			for (User user : userList) {
				MyExam myExam = myExamService.getEntity(examId, user.getId());
				Map<String, Object> singleResult = new HashMap<>();
				singleResult.put("userId", user.getId());
				singleResult.put("userName", exam.getAnonState() == 1 ? user.getName() : null);//  如果是匿名阅卷，不显示名称等
				singleResult.put("userHeadFileId", exam.getAnonState() == 1 ? user.getHeadFileId() : null);
				if (orgCache.get(user.getOrgId()) == null) {
					orgCache.put(user.getOrgId(), orgService.getEntity(user.getOrgId()));
				}
				singleResult.put("orgId", user.getOrgId());
				singleResult.put("orgName", exam.getAnonState() == 1 ? orgCache.get(user.getOrgId()).getName() : null);
				singleResult.put("answerStartTime", myExam.getAnswerStartTime() == null ? null : DateUtil.formatDateTime(myExam.getAnswerStartTime()));
				singleResult.put("answerEndTime", myExam.getAnswerEndTime() == null ? null : DateUtil.formatDateTime(myExam.getAnswerEndTime()));
				singleResult.put("markStartTime", myExam.getMarkStartTime() == null ? null : DateUtil.formatDateTime(myExam.getMarkStartTime()));
				singleResult.put("markEndTime", myExam.getMarkEndTime() == null ? null : DateUtil.formatDateTime(myExam.getMarkEndTime()));
				singleResult.put("state", myExam.getState());
				singleResult.put("markState", myExam.getMarkState());
				singleResult.put("answerState", exam.getScoreState() == 1 ? myExam.getAnswerState() : null);
				singleResult.put("totalScore", exam.getScoreState() == 1 ? myExam.getTotalScore() : null);// 成绩不公开则不显示
				singleResult.put("paperTotalScore", paper.getTotalScore());
				singleResult.put("paperPassScore", paper.getPassScore());
				result.add(singleResult);
			}
			
			return PageResultEx.ok().data(result);
		} catch (MyException e) {
			log.error("考试用户列表错误：{}", e.getMessage());
			return PageResult.err();
		} catch (Exception e) {
			log.error("考试用户列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 考试用户
	 * 
	 * v1.0 zhanghc 2022年3月25日下午1:54:56
	 * @param examId
	 * @param userId
	 * @return PageResult
	 */
	@RequestMapping("/user")
	@ResponseBody
	public PageResult user(Integer examId, Integer userId) {
		try {
			// 校验数据有效性
			List<MyMark> myMarkList = myMarkService.getList(examId);
			MyMark myMark = null;
			for (MyMark _myMark : myMarkList) {
				if (_myMark.getMarkUserId().intValue() == getCurUser().getId().intValue()) {
					myMark = _myMark;
					break;
				}
			}
			if (myMark == null) {
				throw new MyException("未参与该场考试");
			}
			if (!myMark.getExamUserIds().contains(String.format(",%s,", userId))) {
				throw new MyException("未参与该用户阅卷");
			}
			
			// 获取考试用户信息
			MyExam myExam = myExamService.getEntity(examId, userId);
			User user = userService.getEntity(userId);
			Org org = orgService.getEntity(user.getOrgId());
			Exam exam = examService.getEntity(examId);
			Paper paper = paperService.getEntity(exam.getPaperId());
			return PageResultEx.ok()
					.addAttr("userId", user.getId())
					.addAttr("userName", exam.getAnonState() == 1 ? user.getName() : null)//  如果是匿名阅卷，不显示名称等
					.addAttr("userHeadFileId", exam.getAnonState() == 1 ? user.getHeadFileId() : null)
					.addAttr("org", user.getOrgId())
					.addAttr("orgName", exam.getAnonState() == 1 ? org.getName() : null)
					.addAttr("answerStartTime", myExam.getAnswerStartTime() == null ? null : DateUtil.formatDateTime(myExam.getAnswerStartTime()))
					.addAttr("answerEndTime", myExam.getAnswerEndTime() == null ? null : DateUtil.formatDateTime(myExam.getAnswerEndTime()))
					.addAttr("markStartTime", myExam.getMarkStartTime() == null ? null : DateUtil.formatDateTime(myExam.getMarkStartTime()))
					.addAttr("markEndTime", myExam.getMarkEndTime() == null ? null : DateUtil.formatDateTime(myExam.getMarkEndTime()))
					.addAttr("state", myExam.getState())
					.addAttr("markState", myExam.getMarkState())
					.addAttr("answerState", exam.getScoreState() == 1 ? myExam.getAnswerState() : null)
					.addAttr("totalScore", exam.getScoreState() == 1 ? myExam.getTotalScore() : null)// 成绩不公开则不显示
					.addAttr("paperTotalScore", paper.getTotalScore())
					.addAttr("paperPassScore", paper.getPassScore());
		} catch (MyException e) {
			log.error("考试用户错误：{}", e.getMessage());
			return PageResult.err();
		} catch (Exception e) {
			log.error("考试用户错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 阅卷考试答案列表
	 * 
	 * v1.0 chenyun 2021年7月29日下午6:04:37
	 * @param userId
	 * @param examId
	 * @return PageResult
	 */
	@RequestMapping("/answerList")
	@ResponseBody
	public PageResult answerList(Integer userId, Integer examId) {
		try {//TODO 添加权限校验
			List<Map<String, Object>> list = myExamDetailService.getAnswerList(examId, userId);
			for (Map<String, Object> map : list) {
				QuestionAnswer answer = new QuestionAnswer();
				answer.setQuestionType((Integer)map.remove("questionType"));
				answer.setQuestionAi((Integer)map.remove("questionAi"));
				answer.setAnswer((String)map.remove("answer"));
				map.put("answers", answer.getAnswerArr()); 
			}
			
			return PageResultEx.ok().data(list);
		} catch (MyException e) {
			log.error("考试答案列表错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("考试答案列表错误：", e);
			return PageResult.err();
		}
	}
}