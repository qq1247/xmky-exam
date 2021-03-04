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
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyExamDetail;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.MyMark;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.entity.PaperQuestionEx;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.MyExamDetailService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyMarkService;
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
public class MyMarkController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(MyMarkController.class);
	
	@Resource
	private MyExamService myExamService;
	@Resource
	private ExamService examService;
	@Resource
	private PaperService paperService;
	@Resource
	private MyExamDetailService myExamDetailService;
	@Resource
	private MyMarkService myMarkService;
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
			for(Map<String, Object> map : list) {
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

			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("我的阅卷列表错误：", e);
			return PageResult.err();
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
			model.addAttribute("examId", examId);
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
			PageOut pageOut = myExamService.getListpage(pageIn);
			List<Map<String, Object>> list = pageOut.getRows();
			
			Date curTime = new Date();
			for(Map<String, Object> map : list) {
				Date startTime = (Date) map.get("EXAM_MARK_START_TIME");
				Date endTime = (Date) map.get("EXAM_MARK_END_TIME");
				if (startTime.getTime() > curTime.getTime()) {
					map.put("EXAM_HAND", "AWAIT");
				} else if (startTime.getTime() <= curTime.getTime() && endTime.getTime() >= curTime.getTime()) {
					map.put("EXAM_HAND", "START");
				} else {
					map.put("EXAM_HAND", "END");
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
			log.error("我的阅卷详细列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 到达阅卷页面
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @param model
	 * @param myExamId
	 * @return String
	 */
	@RequestMapping("/toMark")
	public String toMark(Model model, Integer myExamId) {
		try {
			// 校验数据有效性
			MyExam myExam = myExamService.getEntity(myExamId);
			if(myExam == null) {
				throw new MyException("参数错误：myExamId");
			}
			Exam exam = examService.getEntity(myExam.getExamId());
			List<MyMark> myMarkList = myMarkService.getList(myExam.getExamId());
			boolean ok = false;
			for (MyMark myMark : myMarkList) {
				if (myMark.getUserId() == getCurUser().getId()) {
					ok = true;
					break;
				}
			}
			if (!ok) {
				throw new MyException("未参与阅卷：" + exam.getName());
			}

			if (exam.getState() == 0) {
				throw new MyException("考试已删除！");
			}
			if (exam.getState() == 2) {
				throw new MyException("考试未发布！");
			}
			if (exam.getMarkStartTime().getTime() > (new Date().getTime() - 30000)){//预留30秒网络延时
				throw new MyException("阅卷未开始！");
			}
			if (exam.getMarkEndTime().getTime() < (new Date().getTime() - 30000)){//预留30秒网络延时
				throw new MyException("阅卷已结束！");
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
			
			model.addAttribute("mark", true);// 控制页面展示那部分
			
			// 标记为阅卷中
			if (myExam.getMarkState() == 1) {
				myExam.setMarkState(2);
			}
			if (myExam.getMarkTime() == null) {// 第一次进入标记为阅卷时间
				myExam.setMarkTime(new Date());
			}
			myExamService.update(myExam);
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
	 * @param myExamDetailId
	 * @param score
	 * @return PageResult
	 */
	@RequestMapping("/doScoreUpdate")
	@ResponseBody
	public PageResult doScoreUpdate(Integer myExamDetailId, BigDecimal score) {
		try {
			// 校验数据有效性
			MyExamDetail myExamDetail = myExamDetailService.getEntity(myExamDetailId);
			List<MyMark> myMarkList = myMarkService.getList(myExamDetail.getExamId());
			Exam exam = examService.getEntity(myExamDetail.getExamId());
			if (exam.getState() == 0) {
				throw new MyException("考试已删除！");
			}
			if (exam.getState() == 2) {
				throw new MyException("考试未发布！");
			}
			if (exam.getMarkStartTime().getTime() > (new Date().getTime())) {
				throw new MyException("阅卷未开始！");
			}
			if (exam.getMarkEndTime().getTime() < (new Date().getTime() - 30000)){//预留30秒网络延时
				throw new MyException("阅卷已结束！");
			}

			boolean ok = false;
			for (MyMark myMark : myMarkList) {
				if (myMark.getUserId() == getCurUser().getId()) {
					ok = true;
					break;
				}
			}

			if (!ok) {
				throw new MyException("未参与考试：" + exam.getName());
			}

			if (score != null) {
				PaperQuestion paperQuestion = paperQuestionService.getEntity(exam.getPaperId(), myExamDetail.getQuestionId());
				if (BigDecimalUtil.newInstance(score).sub(paperQuestion.getScore()).getResult().doubleValue() > 0) {
					throw new MyException("最大分值：" + paperQuestion.getScore());
				}
			}

			// 更新阅卷分数
			myExamDetail.setScore(score);
			myExamDetail.setMyMarkId(getCurUser().getId());
			myExamDetail.setMarkTime(new Date());
			myExamDetailService.update(myExamDetail);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("更新分数错误：", e);
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("更新分数错误：", e);
			return PageResult.err();
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
	public PageResult doMark(Integer myExamId) {
		try {
			// 校验数据有效性
			MyExam myExam = myExamService.getEntity(myExamId);
			List<MyMark> myMarkList = myMarkService.getList(myExam.getExamId());
			Exam exam = examService.getEntity(myExam.getExamId());
			if (exam.getState() == 0) {
				throw new MyException("考试已删除！");
			}
			if (exam.getState() == 2) {
				throw new MyException("考试未发布！");
			}
			if (exam.getMarkStartTime().getTime() > (new Date().getTime())) {
				throw new MyException("阅卷未开始！");
			}
			if (exam.getMarkEndTime().getTime() < (new Date().getTime() - 30000)){//预留30秒网络延时
				throw new MyException("阅卷已结束！");
			}

			boolean ok = false;
			for (MyMark myMark : myMarkList) {
				if (myMark.getUserId() == getCurUser().getId()) {
					ok = true;
					break;
				}
			}
			if (!ok) {
				throw new MyException("未参与阅卷：" + exam.getName());
			}

			List<MyExamDetail> myExamDetailList = myExamDetailService.getList(myExamId);
			int num = 0;
			BigDecimal totalScore = new BigDecimal(0);
			for (MyExamDetail myExamDetail : myExamDetailList) {
				if (myExamDetail.getScore() == null) {
					num++;
				} else {
					totalScore = BigDecimalUtil.newInstance(myExamDetail.getScore()).add(totalScore).getResult();
				}
			}

			if (num > 0) {
				throw new MyException("还有" + num + "道题未阅！");
			}

			// 标记为已阅
			myExam.setMarkState(3);
			myExam.setMyMarkId(getCurUser().getId());
			myExam.setMarkFinishTime(new Date());
			myExam.setTotalScore(totalScore);
			if (totalScore.doubleValue() >= exam.getPassScore().doubleValue() ) {
				myExam.setAnswerState(1);
			} else {
				myExam.setAnswerState(2);
			}
			myExam.setUpdateTime(new Date());
			myExam.setUpdateUserId(getCurUser().getId());
			myExamService.update(myExam);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成阅卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成阅卷错误：", e);
			return PageResult.err();
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
					SpringUtil.getBean(MyExamDetailService.class).doAutoMark(examId, curUser, processBarId);
				}
			}).start();
			
			return PageResultEx.ok().data(processBarId);
		} catch (Exception e) {
			log.error("完成试卷错误：", e);
			return PageResult.err();
		}
	}
}