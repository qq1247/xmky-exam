package com.wcpdoc.exam.core.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.ExamUser;
import com.wcpdoc.exam.core.entity.ExamUserQuestion;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperQuestionEx;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.ExamUserQuestionService;
import com.wcpdoc.exam.core.service.ExamUserService;
import com.wcpdoc.exam.core.service.PaperService;

/**
 * 我的考试控制层
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
@Controller
@RequestMapping("/myExam")
public class MyExamController extends BaseController{
	private static final Logger log = LoggerFactory.getLogger(MyExamController.class);
	
	@Resource
	private ExamUserService examUserService;
	@Resource
	private ExamService examService;
	@Resource
	private PaperService paperService;
	@Resource
	private ExamUserQuestionService examUserQuestionService;
	
	/**
	 * 到达我的考试列表页面
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return String
	 */
	@RequestMapping("/toList")
	public String toList(Model model) {
		try {
			return "exam/myExam/myExamList";
		} catch (Exception e) {
			log.error("到达我的考试列表页面错误：", e);
			return "exam/myExam/myExamList";
		}
	}
	
	/**
	 * 我的考试列表
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return pageOut
	 */
	@RequestMapping("/list")
	@ResponseBody
	public PageResult list(PageIn pageIn) {
		try {
			pageIn.setTen(getCurUser().getId().toString());
			PageOut pageOut = examUserService.getListpage(pageIn);

			Date curTime = new Date();
			for (Map<String, Object> map : pageOut.getRows()) {
				Date examStartTime = (Date) map.get("EXAM_START_TIME");
				Date examEndTime = (Date) map.get("EXAM_END_TIME");
				if (examStartTime.getTime() > curTime.getTime()) {
					map.put("EXAM_HAND", "AWAIT");
				} else if (examEndTime.getTime() < curTime.getTime()){
					map.put("EXAM_HAND", "END");
				} else {
					map.put("EXAM_HAND", "START");
				}
			}

			return new PageResultEx(true, "查询成功", pageOut);
		} catch (Exception e) {
			log.error("我的考试列表错误：", e);
			return new PageResult(false, "查询失败");
		}
	}
	
	/**
	 * 到达考试页面
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @param model
	 * @param examUserId
	 * @return String
	 */
	@RequestMapping("/toExam")
	public String toExam(Model model, Integer examUserId) {
		try {
			//校验数据有效性
			ExamUser examUser = examUserService.getEntity(examUserId);
			if(examUser == null){
				throw new MyException("参数错误：examUserId");
			}
			if (examUser.getUserId().intValue() != getCurUser().getId()) {
				throw new MyException("未参与该考试！");
			}
			
			Exam exam = examService.getEntity(examUser.getExamId());
			if(exam.getState() == 0){
				throw new MyException("考试已删除！");
			}
			if(exam.getState() == 2){
				throw new MyException("考试未发布！");
			}
			if(exam.getStartTime().getTime() > (new Date().getTime())){
				throw new MyException("考试未开始！");
			}
			if(exam.getEndTime().getTime() < (new Date().getTime())){
				throw new MyException("考试已结束！");
			}
			
			// 试卷信息
			model.addAttribute("exam", exam);//考试信息
			
			Paper paper = paperService.getEntity(exam.getPaperId());//试卷信息
			model.addAttribute("paper", paper);
			
			List<PaperQuestionEx> paperQuestionExList = paperService.getPaperList(exam.getPaperId());//试题信息
			model.addAttribute("paperQuestionExList", paperQuestionExList);
			
			model.addAttribute("examUser", examUser);// 考试用户信息
			
			List<ExamUserQuestion> examUserQuestionList = examUserQuestionService.getList(examUserId);//用户已做答案信息
			Map<Long, ExamUserQuestion> examUserQuestionMap = new HashMap<Long, ExamUserQuestion>();
			for(ExamUserQuestion examUserQuestion : examUserQuestionList){
				examUserQuestionMap.put(examUserQuestion.getQuestionId().longValue(), examUserQuestion);
			}
			model.addAttribute("examUserQuestionMap", examUserQuestionMap);
			
			// 标记为考试中
			if (examUser.getState() == 1) {
				examUser.setState(2);
			}
			if (examUser.getAnswerTime() == null) {
				examUser.setAnswerTime(new Date());
			}
			examUserService.update(examUser);
			return "exam/myExam/myExamPaper";
		} catch (Exception e) {
			log.error("到达考试页面错误：", e);
			model.addAttribute("message", e.getMessage());
			return "exam/error";
		}
	}
	
	/**
	 * 更新答案
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * @param examUserQuestionId
	 * @param answer
	 * @return PageResult
	 */
	@RequestMapping("/updateAnswer")
	@ResponseBody
	public PageResult updateAnswer(Integer examUserQuestionId, String answer) {
		try {
			//校验数据有效性
			if(examUserQuestionId == null){
				throw new MyException("参数错误：examUserQuestionId");
			}
			
//			if(!ValidateUtil.isValid(answer)){
//				throw new MyException("参数错误：answer");
//			}//如取消勾选则为空
			
			ExamUserQuestion examUserQuestion = examUserQuestionService.getEntity(examUserQuestionId);
			if(examUserQuestion.getUserId() != getCurUser().getId()){
				throw new MyException("未参与考试！");
			}
			
			Exam exam = examService.getEntity(examUserQuestion.getExamId());
			if(exam.getState() == 0){
				throw new MyException("考试已删除！");
			}
			if(exam.getState() == 2){
				throw new MyException("考试未发布！");
			}
			if(exam.getStartTime().getTime() > (new Date().getTime())){
				throw new MyException("考试未开始！");
			}
			if(exam.getEndTime().getTime() < (new Date().getTime() - 5000)){//预留5秒网络延时
				throw new MyException("考试已结束！");
			}
			
			//更新考试用户试题信息
			examUserQuestion.setAnswer(answer);
			examUserQuestion.setAnswerTime(new Date());
			examUserQuestionService.update(examUserQuestion);
			return new PageResult(true, "更新成功");
		} catch (MyException e) {
			log.error("更新答案错误：", e);
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("更新答案错误：", e);
			return new PageResult(false, "未知错误！");
		}
	}
	
	/**
	 * 完成考试
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * @param examUserId
	 * @return PageResult
	 */
	@RequestMapping("/doExam")
	@ResponseBody
	public PageResult doExam(Integer examUserId) {
		try {
			// 校验数据有效性
			ExamUser examUser = examUserService.getEntity(examUserId);
			if (examUser == null) {
				throw new MyException("参数错误：examUserId");
			}
			if (examUser.getUserId().intValue() != getCurUser().getId()) {
				throw new MyException("未参与该考试！");
			}

			Exam exam = examService.getEntity(examUser.getExamId());
			if (exam.getState() == 0) {
				throw new MyException("考试已删除！");
			}
			if (exam.getState() == 2) {
				throw new MyException("考试未发布！");
			}
			if (exam.getStartTime().getTime() > (new Date().getTime())) {
				throw new MyException("考试未开始！");
			}
			if (exam.getEndTime().getTime() < (new Date().getTime())) {
				throw new MyException("考试已结束！");
			}

			// 标记为已交卷
			examUser.setState(3);
			examUser.setAnswerFinishTime(new Date());
			examUserService.update(examUser);
			return new PageResult(true, "完成成功");
		} catch (Exception e) {
			log.error("完成试卷错误：", e);
			return new PageResult(false, "完成失败：" + e.getMessage());
		}
	}
}