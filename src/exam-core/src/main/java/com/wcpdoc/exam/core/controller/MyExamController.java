package com.wcpdoc.exam.core.controller;

import java.math.BigDecimal;
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
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyExamDetail;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperQuestionEx;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.MyExamDetailService;
import com.wcpdoc.exam.core.service.MyExamService;
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
	private MyExamService myExamService;
	@Resource
	private ExamService examService;
	@Resource
	private PaperService paperService;
	@Resource
	private MyExamDetailService myExamDetailService;
	
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
			PageOut pageOut = myExamService.getListpage(pageIn);

			Date curTime = new Date();
			for (Map<String, Object> map : pageOut.getRows()) {
				Date examStartTime = (Date) map.get("EXAM_START_TIME");
				Date examEndTime = (Date) map.get("EXAM_END_TIME");
				if (examStartTime.getTime() > curTime.getTime()) {
					map.put("EXAM_HAND", "AWAIT");
				} else if (examEndTime.getTime() < curTime.getTime()) {
					map.put("EXAM_HAND", "END");
				} else {
					map.put("EXAM_HAND", "START");
				}
				
				BigDecimal userScore = (BigDecimal) map.get("MY_EXAM_TOTAL_SCORE");
				BigDecimal scoreA = (BigDecimal) map.get("EXAM_SCORE_A");
				String scoreARemark = (String) map.get("EXAM_SCORE_A_REMARK");
				if (scoreA != null && userScore != null && userScore.doubleValue() >= scoreA.doubleValue()) {
					map.put("USER_REMARK", scoreARemark);
					continue;
				}
				BigDecimal scoreB = (BigDecimal) map.get("EXAM_SCORE_B");
				String scoreBRemark = (String) map.get("EXAM_SCORE_B_REMARK");
				if (scoreB != null && userScore != null && userScore.doubleValue() >= scoreB.doubleValue()) {
					map.put("USER_REMARK", scoreBRemark);
					continue;
				}
				BigDecimal scoreC = (BigDecimal) map.get("EXAM_SCORE_C");
				String scoreCRemark = (String) map.get("EXAM_SCORE_C_REMARK");
				if (scoreC != null && userScore != null && userScore.doubleValue() >= scoreC.doubleValue()) {
					map.put("USER_REMARK", scoreCRemark);
					continue;
				}
				
				BigDecimal scoreD = (BigDecimal) map.get("EXAM_SCORE_D");
				String scoreDRemark = (String) map.get("EXAM_SCORE_D_REMARK");
				if (scoreD != null && userScore != null && userScore.doubleValue() >= scoreD.doubleValue()) {
					map.put("USER_REMARK", scoreDRemark);
					continue;
				}
				
				BigDecimal scoreE = (BigDecimal) map.get("EXAM_SCORE_E");
				String scoreERemark = (String) map.get("EXAM_SCORE_E_REMARK");
				if (scoreE != null && userScore != null && userScore.doubleValue() >= scoreE.doubleValue()) {
					map.put("USER_REMARK", scoreERemark);
					continue;
				}
			}

			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("我的考试列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 到达考试页面
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @param model
	 * @param myExamId
	 * @return String
	 */
	@RequestMapping("/toExam")
	public String toExam(Model model, Integer myExamId) {
		try {
			//校验数据有效性
			MyExam myExam = myExamService.getEntity(myExamId);
			if(myExam == null) {
				throw new MyException("参数错误：myExamId");
			}
			if (myExam.getUserId().intValue() != getCurUser().getId()) {
				throw new MyException("未参与该考试！");
			}
			
			Exam exam = examService.getEntity(myExam.getExamId());
			if(exam.getState() == 0) {
				throw new MyException("考试已删除！");
			}
			if(exam.getState() == 2) {
				throw new MyException("考试未发布！");
			}
			if(exam.getStartTime().getTime() > (new Date().getTime())) {
				throw new MyException("考试未开始！");
			}
			if(exam.getEndTime().getTime() < (new Date().getTime() - 30000)){//预留30秒网络延时
				throw new MyException("考试已结束！");
			}
			
			// 试卷信息
			model.addAttribute("exam", exam);//考试信息
			
			Paper paper = paperService.getEntity(exam.getPaperId());//试卷信息
			model.addAttribute("paper", paper);
			
			List<PaperQuestionEx> paperQuestionExList = paperService.getPaperList(exam.getPaperId());//试题信息
			model.addAttribute("paperQuestionExList", paperQuestionExList);
			
			model.addAttribute("myExam", myExam);// 我的考试信息
			
			List<MyExamDetail> myExamDetailList = myExamDetailService.getList(myExamId);//用户已做答案信息
			Map<Long, MyExamDetail> myExamDetailMap = new HashMap<Long, MyExamDetail>();
			for(MyExamDetail myExamDetail : myExamDetailList) {
				myExamDetailMap.put(myExamDetail.getQuestionId().longValue(), myExamDetail);
			}
			model.addAttribute("myExamDetailMap", myExamDetailMap);
			
			model.addAttribute("answer", true);// 控制页面展示那部分
			
			// 标记为考试中
			if (myExam.getState() == 1) {
				myExam.setState(2);
			}
			if (myExam.getAnswerTime() == null) {
				myExam.setAnswerTime(new Date());
			}
			myExamService.update(myExam);
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
	 * @param myExamDetailId
	 * @param answer
	 * @return PageResult
	 */
	@RequestMapping("/updateAnswer")
	@ResponseBody
	public PageResult updateAnswer(Integer myExamDetailId, String answer) {
		try {
			//校验数据有效性
			if(myExamDetailId == null) {
				throw new MyException("参数错误：myExamDetailId");
			}
			
//			if(!ValidateUtil.isValid(answer)) {
//				throw new MyException("参数错误：answer");
//			}//如取消勾选则为空
			
			MyExamDetail myExamDetail = myExamDetailService.getEntity(myExamDetailId);
			if(myExamDetail.getUserId() != getCurUser().getId()) {
				throw new MyException("未参与考试！");
			}
			
			Exam exam = examService.getEntity(myExamDetail.getExamId());
			if(exam.getState() == 0) {
				throw new MyException("考试已删除！");
			}
			if(exam.getState() == 2) {
				throw new MyException("考试未发布！");
			}
			if(exam.getStartTime().getTime() > (new Date().getTime())) {
				throw new MyException("考试未开始！");
			}
			if(exam.getEndTime().getTime() < (new Date().getTime() - 30000)){//预留30秒网络延时
				throw new MyException("考试已结束！");
			}
			
			//更新我的考试详细信息
			myExamDetail.setAnswer(answer);
			myExamDetail.setAnswerTime(new Date());
			myExamDetailService.update(myExamDetail);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("更新答案错误：", e);
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("更新答案错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 完成交卷
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * @param myExamId
	 * @return PageResult
	 */
	@RequestMapping("/doExam")
	@ResponseBody
	public PageResult doExam(Integer myExamId) {
		try {
			// 校验数据有效性
			MyExam myExam = myExamService.getEntity(myExamId);
			if (myExam == null) {
				throw new MyException("参数错误：myExamId");
			}
			if (myExam.getUserId().intValue() != getCurUser().getId()) {
				throw new MyException("未参与该考试！");
			}

			Exam exam = examService.getEntity(myExam.getExamId());
			if (exam.getState() == 0) {
				throw new MyException("考试已删除！");
			}
			if (exam.getState() == 2) {
				throw new MyException("考试未发布！");
			}
			if (exam.getStartTime().getTime() > (new Date().getTime())) {
				throw new MyException("考试未开始！");
			}
			if (exam.getEndTime().getTime() < (new Date().getTime() - 30000)){//预留30秒网络延时
				throw new MyException("考试已结束！");
			}

			// 标记为已交卷
			myExam.setState(3);
			myExam.setAnswerFinishTime(new Date());
			myExamService.update(myExam);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成交卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成交卷错误：", e);
			return PageResult.err();
		}
	}
}