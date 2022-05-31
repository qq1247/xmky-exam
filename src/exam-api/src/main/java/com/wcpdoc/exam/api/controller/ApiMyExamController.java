package com.wcpdoc.exam.api.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.base.cache.ParmCache;
import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.ParmService;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.cache.AutoMarkCache;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.service.MyExamDetailService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.notify.exception.NotifyException;
import com.wcpdoc.notify.service.NotifyService;

/**
 * 我的考试控制层
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
@Controller
@RequestMapping("/api/myExam")
public class ApiMyExamController extends BaseController{
	private static final Logger log = LoggerFactory.getLogger(ApiMyExamController.class);
	
	@Resource
	private MyExamService myExamService;
	@Resource
	private MyExamDetailService myExamDetailService;
	@Resource
	private UserService userService;
	@Resource
	private NotifyService notifyService;
	@Resource
	private ParmService parmService;
	
	/**
	 * 我的考试列表
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
			PageOut pageOut = myExamService.getListpage(pageIn);
			for (Map<String, Object> map : pageOut.getList()) {
				if ((Integer)map.remove("examScoreState") == 2) {
					map.put("totalScore", null);// 不显示分数
					map.put("answerState", null);// 不显示及格状态
				}
				if ((Integer)map.remove("examRankState") == 2) {
					map.put("no", null);// 不显示排名
					map.put("userNum", null);// 不显示排名
				}
			}
			
			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("我的考试列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 考试答案列表
	 * 
	 * v1.0 zhanghc 2018年11月24日上午9:13:22
	 * @param examId
	 * @return PageResult
	 */
	@RequestMapping("/answerList")
	@ResponseBody
	public PageResult answerList(Integer examId) {
		try {
			List<Map<String, Object>> answerList = myExamDetailService.getAnswerList(examId, getCurUser().getId());
			for (Map<String, Object> map : answerList) {
				QuestionAnswer answer = new QuestionAnswer();
				answer.setQuestionType((Integer)map.get("questionType"));
				answer.setQuestionAi((Integer)map.get("questionAi"));
				answer.setAnswer((String)map.remove("answer"));
				map.put("answers", answer.getAnswerArr());
			}
			
			return PageResultEx.ok().data(answerList);
		} catch (MyException e) {
			log.error("考试答案列表错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("考试答案列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 答题
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * @param examId
	 * @param questionId
	 * @param answers
	 * @param fileId
	 * @return PageResult
	 */
	@RequestMapping("/answer")
	@ResponseBody
	public PageResult answer(Integer examId, Integer questionId, String[] answers, Integer answerFileId) {
		try {
			if (!AutoMarkCache.tryReadLock(examId, 2000)) {
				throw new MyException("尝试加读锁失败");
			}
			myExamService.answerUpdate(examId, getCurUser().getId(), questionId, answers, answerFileId);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("更新答案错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("更新答案错误：", e);
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
	public PageResult finish(Integer examId) {
		try {
			if (!AutoMarkCache.tryReadLock(examId, 2000)) {
				throw new MyException("尝试加读锁失败");
			}
			myExamService.finish(examId, getCurUser().getId());
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成交卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成交卷错误：", e);
			return PageResult.err();
		} finally {
			AutoMarkCache.releaseReadLock(examId);
		}
	}
	
	/**
	 * 邮件通知
	 * 
	 * v1.0 zhanghc 2018年11月24日上午9:13:22
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/email")
	@ResponseBody
	public PageResult email(Integer examId) {
		try {
			Parm parm = ParmCache.get();
			List<MyExam> list = myExamService.getList(examId);
			for(MyExam myExam : list){
				User user = userService.getEntity(myExam.getUserId());
				if (!ValidateUtil.isValid(user.getEmail())) {
					continue;
				}
				notifyService.pushEmail(parm.getEmailUserName(), user.getEmail(), "考试邮箱通知", user.getName()+"-昵称。");
			}
			return PageResult.ok();
		} catch (NotifyException e) {
			log.error("邮件通知错误：", e);
			return PageResult.err().msg(e.getMessage());
		} catch (MyException e) {
			log.error("邮件通知错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("邮件通知错误：", e);
			return PageResult.err();
		}
	}
}