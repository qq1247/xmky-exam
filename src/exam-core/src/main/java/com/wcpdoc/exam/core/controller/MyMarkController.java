package com.wcpdoc.exam.core.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.MarkUser;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.entity.PaperQuestionEx;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.ExamUserQuestionService;
import com.wcpdoc.exam.core.service.ExamUserService;
import com.wcpdoc.exam.core.service.MarkUserService;
import com.wcpdoc.exam.core.service.PaperQuestionService;
import com.wcpdoc.exam.core.service.PaperService;
import com.wcpdoc.exam.core.util.BigDecimalUtil;
import com.wcpdoc.exam.core.util.SpringUtil;

/**
 * 我的阅卷控制层
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
@Controller
@RequestMapping("/myMark")
public class MyMarkController extends BaseController{
	private static final Logger log = LoggerFactory.getLogger(MyMarkController.class);
	
	@Resource
	private ExamUserService examUserService;
	@Resource
	private ExamService examService;
	@Resource
	private PaperService paperService;
	@Resource
	private ExamUserQuestionService examUserQuestionService;
	@Resource
	private MarkUserService markUserService;
	@Resource
	private PaperQuestionService paperQuestionService;
	
	/**
	 * 到达我的阅卷列表页面
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return String
	 */
	@RequestMapping("/toList")
	public String toList(Model model) {
		try {
			return "exam/myMark/myMarkList";
		} catch (Exception e) {
			log.error("到达我的阅卷列表页面错误：", e);
			return "exam/myMark/myMarkList";
		}
	}
	
	/**
	 * 我的阅卷列表
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return pageOut
	 */
	@RequestMapping("/list")
	@ResponseBody
	public PageResult list(PageIn pageIn) {
		try {
			pageIn.setTen(getCurUser().getId().toString());
			pageIn.setFour("1");
			pageIn.setEight(getCurUser().getId().toString());
			PageOut pageOut = examService.getListpage(pageIn);
			List<Map<String, Object>> list = pageOut.getRows();
			
			Date curTime = new Date();
			for(Map<String, Object> map : list){
				Date startTime = (Date) map.get("MARK_START_TIME");
				Date endTime = (Date) map.get("MARK_END_TIME");
				if (startTime.getTime() > curTime.getTime()) {
					map.put("EXAM_HAND", "AWAIT");
				} else if (startTime.getTime() <= curTime.getTime() && endTime.getTime() >= curTime.getTime()) {
					map.put("EXAM_HAND", "START");
				} else {
					map.put("EXAM_HAND", "END");
				}
			}

			return new PageResultEx(true, "查询成功", pageOut);
		} catch (Exception e) {
			log.error("我的阅卷列表错误：", e);
			return new PageResult(false, "查询失败");
		}
	}
	
	/**
	 * 到达我的阅卷详细列表页面
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return String
	 */
	@RequestMapping("/toDetailList")
	public String toDetailList(Model model, Integer examId) {
		try {
			model.addAttribute(examId);
			return "exam/myMark/myMarkDetailList";
		} catch (Exception e) {
			log.error("到达我的阅卷详细列表页面错误：", e);
			return "exam/myMark/myMarkDetailList";
		}
	}
	
	/**
	 * 我的阅卷详细列表
	 * 
	 * v1.0 zhanghc 2018年11月11日下午2:26:56
	 * @param pageIn
	 * @return String
	 */
	@RequestMapping("/detailList")
	@ResponseBody
	public PageResult detailList(PageIn pageIn) {
		try {
			pageIn.setEight(getCurUser().getId().toString());
			PageOut pageOut = examUserService.getListpage(pageIn);
			List<Map<String, Object>> list = pageOut.getRows();
			
			Date curTime = new Date();
			for(Map<String, Object> map : list){
				Date startTime = (Date) map.get("EXAM_MARK_START_TIME");
				Date endTime = (Date) map.get("EXAM_MARK_END_TIME");
				if (startTime.getTime() > curTime.getTime()) {
					map.put("EXAM_HAND", "AWAIT");
				} else if (startTime.getTime() <= curTime.getTime() && endTime.getTime() >= curTime.getTime()) {
					map.put("EXAM_HAND", "START");
				} else {
					map.put("EXAM_HAND", "END");
				}
			}
			
			return new PageResultEx(true, "查询成功", pageOut);
		} catch (Exception e) {
			log.error("我的阅卷详细列表错误：", e);
			return new PageResult(false, "查询失败");
		}
	}
	
	/**
	 * 到达阅卷页面
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @param model
	 * @param examUserId
	 * @return String
	 */
	@RequestMapping("/toMark")
	public String toMark(Model model, Integer examUserId) {
		try {
			// 校验数据有效性
			ExamUser examUser = examUserService.getEntity(examUserId);
			Exam exam = examService.getEntity(examUser.getExamId());
			List<MarkUser> markUserList = markUserService.getList(examUser.getExamId());
			boolean ok = false;
			for (MarkUser markUser : markUserList) {
				if (markUser.getUserId() == getCurUser().getId()) {
					ok = true;
					break;
				}
			}
			if (!ok) {
				throw new RuntimeException("未参与阅卷：" + exam.getName());
			}

			Date curTime = new Date();
			if (exam.getState() == 0) {
				throw new RuntimeException("考试已删除！");
			}
			if (exam.getState() == 2) {
				throw new RuntimeException("考试未发布！");
			}
			if (exam.getMarkStartTime().getTime() > curTime.getTime()) {
				throw new RuntimeException("阅卷未开始！");
			}
			if (exam.getMarkEndTime().getTime() < curTime.getTime()) {
				throw new RuntimeException("阅卷已结束！");
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
			
			model.addAttribute("answer", false);// 控制页面展示那部分
			model.addAttribute("mark", true);// 控制页面展示那部分
			
			// 标记为阅卷中
			if (examUser.getMarkState() == 1) {
				examUser.setMarkState(2);
			}
			if (examUser.getMarkTime() == null) {// 第二次进入就处理
				examUser.setMarkTime(new Date());
			}
			examUserService.update(examUser);
			return "exam/myMark/myMarkPaper";
		} catch (Exception e) {
			log.error("到达阅卷页面错误：", e);
			model.addAttribute("message", e.getMessage());
			return "exam/error";
		}
	}
	
	/**
	 * 更新阅卷分数
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * @param examUserQuestionId
	 * @param score
	 * @return PageResult
	 */
	@RequestMapping("/updateScore")
	@ResponseBody
	public PageResult updateScore(Integer examUserQuestionId, BigDecimal score) {
		try {
			// 校验数据有效性
			ExamUserQuestion examUserQuestion = examUserQuestionService.getEntity(examUserQuestionId);
			List<MarkUser> markUserList = markUserService.getList(examUserQuestion.getExamId());
			Exam exam = examService.getEntity(examUserQuestion.getExamId());
			if (exam.getState() == 0) {
				throw new RuntimeException("考试已删除！");
			}
			if (exam.getState() == 2) {
				throw new RuntimeException("考试未发布！");
			}
			if (exam.getMarkStartTime().getTime() > (new Date().getTime())) {
				throw new RuntimeException("阅卷未开始！");
			}
			if (exam.getMarkEndTime().getTime() < (new Date().getTime() - 5000)) {
				throw new RuntimeException("阅卷已结束！");
			}

			boolean ok = false;
			for (MarkUser markUser : markUserList) {
				if (markUser.getUserId() == getCurUser().getId()) {
					ok = true;
					break;
				}
			}

			if (!ok) {
				throw new RuntimeException("未参与考试：" + exam.getName());
			}

			if (score != null) {
				PaperQuestion paperQuestion = paperQuestionService.getEntity(exam.getPaperId(), examUserQuestion.getQuestionId());
				if (BigDecimalUtil.newInstance(score).sub(paperQuestion.getScore()).getResult().doubleValue() > 0) {
					throw new MyException("最大分值：" + paperQuestion.getScore());
				}
			}

			// 更新阅卷分数
			examUserQuestion.setScore(score);
			examUserQuestion.setMarkUserId(getCurUser().getId());
			examUserQuestion.setMarkTime(new Date());
			examUserQuestionService.update(examUserQuestion);
			return new PageResult(true, "更新成功！");
		} catch (MyException e) {
			log.error("更新分数错误：", e);
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("更新分数错误：", e);
			return new PageResult(false, "未知错误！");
		}
	}
	
	/**
	 * 完成阅卷
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * @param examId
	 * @return PageResult
	 */
	@RequestMapping("/doMark")
	@ResponseBody
	public PageResult doMark(Integer examUserId) {
		try {
			//校验数据有效性
			ExamUser examUser = examUserService.getEntity(examUserId);
			List<MarkUser> markUserList = markUserService.getList(examUser.getExamId());
			Exam exam = examService.getEntity(examUser.getExamId());
			if(exam.getState() == 0){
				throw new RuntimeException("考试已删除！");
			}
			if(exam.getState() == 2){
				throw new RuntimeException("考试未发布！");
			}
			if(exam.getMarkStartTime().getTime() > (new Date().getTime())){
				throw new RuntimeException("阅卷未开始！");
			}
			if(exam.getMarkEndTime().getTime() < (new Date().getTime() - 5000)){
				throw new RuntimeException("阅卷已结束！");
			}
			
			boolean ok = false;
			for(MarkUser markUser : markUserList){
				if(markUser.getUserId() == getCurUser().getId()){
					ok = true;
					break;
				}
			}
			if(!ok){
				throw new RuntimeException("未参与阅卷：" + exam.getName());
			}
			
			List<ExamUserQuestion> examUserQuestionList = examUserQuestionService.getList(examUserId);
			int num = 0;
			BigDecimal totalScore = new BigDecimal(0);
			for(ExamUserQuestion examUserQuestion : examUserQuestionList){
				if(examUserQuestion.getScore() == null){
					num++;
				}else{
					totalScore = BigDecimalUtil.newInstance(examUserQuestion.getScore()).add(totalScore).getResult();
				}
			}
			
			if(num > 0){
				throw new RuntimeException("还有" + num + "道题未阅！");
			}
			
			//考试用户标记为及格或不及格
			examUser.setMarkUserId(getCurUser().getId());
			examUser.setMarkTime(new Date());
			examUser.setTotalScore(totalScore);
			if(exam.getPassScore().doubleValue() >= totalScore.doubleValue()){
				examUser.setMarkState(7);
			}else{
				examUser.setState(8);
			}
			
			examUserService.update(examUser);
			return new PageResultEx(true, "阅卷成功", null);
		} catch (Exception e) {
			log.error("完成阅卷错误：", e);
			return new PageResult(false, "阅卷失败：" + e.getMessage());
		}
	}
	
	/**
	 * 完成自动阅卷
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * @param examId
	 * @return PageResult
	 */
	@RequestMapping("/doAutoMark")
	@ResponseBody
	public PageResult doAutoMark(Integer examId) {
		try {
			String processBarId = UUID.randomUUID().toString();
			LoginUser curUser = getCurUser();
			new Thread(new Runnable() {
				public void run() {
					SpringUtil.getBean(ExamUserQuestionService.class).doAutoMark(examId, curUser, processBarId);
				}
			}).start();
			
			return new PageResultEx(true, "完成成功", processBarId);
		} catch (Exception e) {
			log.error("完成试卷错误：", e);
			return new PageResult(false, "完成失败：" + e.getMessage());
		}
	}
}