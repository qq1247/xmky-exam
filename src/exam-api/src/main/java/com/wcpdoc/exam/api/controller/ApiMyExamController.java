package com.wcpdoc.exam.api.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.base.entity.Parm;
import com.wcpdoc.exam.base.entity.User;
import com.wcpdoc.exam.base.service.ParmService;
import com.wcpdoc.exam.base.service.UserService;
import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyExamDetail;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.MyExamDetailService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.PaperService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.util.StringUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.notify.exception.NotifyException;
import com.wcpdoc.exam.notify.service.NotifyService;

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
	private ExamService examService;
	@Resource
	private PaperService paperService;
	@Resource
	private QuestionService questionService;
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
	@RequiresRoles(value={"user"},logical = Logical.OR)
	public PageResult listpage() {
		try {
			PageIn pageIn = new PageIn(request);
			pageIn.addAttr("curUserId", getCurUser().getId());
			return PageResultEx.ok().data(myExamService.getListpage(pageIn));
		} catch (Exception e) {
			log.error("我的考试列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 考试答案列表
	 * 
	 * v1.0 zhanghc 2018年11月24日上午9:13:22
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/answerList")
	@ResponseBody
	@RequiresRoles(value={"user"},logical = Logical.OR)
	public PageResult answerList(Integer id) {
		try {
			List<Map<String, Object>> list = myExamDetailService.getAnswerList(id);
			for (Map<String, Object> map : list) {
				map.put("myExamDetailId", map.remove("id"));// 前缀为myExam，默认为id有歧义。
				map.put("answers", map.remove("answer"));// 如果没有值，页面也返回字段
				
				if (map.get("answers") != null) {
					if (map.get("questionType").equals(1) || map.get("questionType").equals(4) || map.get("questionType").equals(5)) {
						map.put("answers", new String[] { map.remove("answers").toString() });
					} else if (map.get("questionType").equals(2)) {
						map.put("answers", map.remove("answers").toString().split(","));
					} else if (map.get("questionType").equals(3)) {
						map.put("answers", map.remove("answers").toString().split("\n"));
					}
				}
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
	
//	/**
//	 * 到达考试页面
//	 * 
//	 * v1.0 zhanghc 2017-05-25 16:34:59
//	 * @param model
//	 * @param myExamId
//	 * @return String
//	 */
//	@RequestMapping("/toExam")
//	@RequiresRoles(value={"user"},logical = Logical.OR)
//	public String toExam(Model model, Integer myExamId) {
//		try {
//			//校验数据有效性
//			MyExam myExam = myExamService.getEntity(myExamId);
//			if(myExam == null) {
//				throw new MyException("参数错误：myExamId");
//			}
//			if (myExam.getUserId().intValue() != getCurUser().getId()) {
//				throw new MyException("未参与该考试！");
//			}
//			
//			Exam exam = examService.getEntity(myExam.getExamId());
//			if(exam.getState() == 0) {
//				throw new MyException("考试已删除！");
//			}
//			if(exam.getState() == 2) {
//				throw new MyException("考试未发布！");
//			}
//			if(exam.getStartTime().getTime() > (new Date().getTime())) {
//				throw new MyException("考试未开始！");
//			}
//			if(exam.getEndTime().getTime() < (new Date().getTime() - 30000)){//预留30秒网络延时
//				throw new MyException("考试已结束！");
//			}
//			
//			// 试卷信息
//			model.addAttribute("exam", exam);//考试信息
//			
//			Paper paper = paperService.getEntity(exam.getPaperId());//试卷信息
//			model.addAttribute("paper", paper);
//			
//			List<PaperQuestionEx> paperQuestionExList = paperService.getPaperList(exam.getPaperId());//试题信息
//			model.addAttribute("paperQuestionExList", paperQuestionExList);
//			
//			model.addAttribute("myExam", myExam);// 我的考试信息
//			
//			List<MyExamDetail> myExamDetailList = myExamDetailService.getList(myExamId);//用户已做答案信息
//			Map<Long, MyExamDetail> myExamDetailMap = new HashMap<Long, MyExamDetail>();
//			for(MyExamDetail myExamDetail : myExamDetailList) {
//				myExamDetailMap.put(myExamDetail.getQuestionId().longValue(), myExamDetail);
//			}
//			model.addAttribute("myExamDetailMap", myExamDetailMap);
//			
//			model.addAttribute("answer", true);// 控制页面展示那部分
//			
//			// 标记为考试中
//			if (myExam.getState() == 1) {
//				myExam.setState(2);
//			}
//			if (myExam.getAnswerStartTime() == null) {
//				myExam.setAnswerStartTime(new Date());
//			}
//			myExamService.update(myExam);
//			return "exam/myExam/myExamPaper";
//		} catch (Exception e) {
//			log.error("到达考试页面错误：", e);
//			model.addAttribute("message", e.getMessage());
//			return "exam/error";
//		}
//	}
	
	/**
	 * 更新答案
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * @param myExamDetailId
	 * @param answers
	 * @return PageResult
	 */
	@RequestMapping("/updateAnswer")
	@ResponseBody
	@RequiresRoles(value={"user"},logical = Logical.OR)
	public PageResult updateAnswer(Integer myExamDetailId, String[] answers) {
		try {
			// 校验数据有效性
			if (myExamDetailId == null) {
				throw new MyException("参数错误：myExamDetailId");
			}

			// if(!ValidateUtil.isValid(answer)) {
			// 	throw new MyException("参数错误：answer");
			// }//如取消勾选则为空

			MyExamDetail myExamDetail = myExamDetailService.getEntity(myExamDetailId);
			if (myExamDetail.getUserId() != getCurUser().getId()) {
				throw new MyException("未参与考试！");
			}

			Exam exam = examService.getEntity(myExamDetail.getExamId());
			if (exam.getState() == 0) {
				throw new MyException("考试已删除！");
			}
			if (exam.getState() == 2) {
				throw new MyException("考试未发布！");
			}
			if (exam.getStartTime().getTime() > (new Date().getTime())) {
				throw new MyException("考试未开始！");
			}
			if (exam.getEndTime().getTime() < (new Date().getTime() - 30000)) {// 预留30秒网络延时
				throw new MyException("考试已结束！");
			}

			// 更新我的考试详细信息
			Question question = questionService.getEntity(myExamDetail.getQuestionId());
			if (!ValidateUtil.isValid(answers)) {
				myExamDetail.setAnswer(null);
			} else if (question.getType() == 1 || question.getType() == 4 || question.getType() == 5) {
				myExamDetail.setAnswer(answers[0]);
			} else if (question.getType() == 2) {
				myExamDetail.setAnswer(StringUtil.join(answers));
			} else if (question.getType() == 3) {
				myExamDetail.setAnswer(StringUtil.join(answers, "\n"));
			}
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
	@RequestMapping("/doAnswer")
	@ResponseBody
	@RequiresRoles(value={"user"},logical = Logical.OR)
	public PageResult doAnswer(Integer myExamId) {
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
			myExam.setAnswerEndTime(new Date());
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
	
	/**
	 * 考试时间表
	 * 
	 * v1.0 chenyun 2021年3月23日上午11:00:08
	 * @return PageResult
	 */
	@RequestMapping("/kalendar")
	@ResponseBody
	@RequiresRoles(value={"user"},logical = Logical.OR)
	public PageResult kalendar(Integer year, Integer month) {
		try {
			// 校验数据有效性
			if(year == null){
				throw new MyException("参数错误：year");
			}
			if(month == null){
				throw new MyException("参数错误：month");
			}
			
			return PageResultEx.ok().data(myExamService.kalendar(year, month));
		} catch (MyException e) {
			log.error("考试时间表错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("考试时间表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 考试排行
	 * 
	 * v1.0 chenyun 2021年3月23日下午4:07:48
	 * @param year
	 * @param month
	 * @return PageResult
	 */
	@RequestMapping("/rankingPage")
	@ResponseBody
	@RequiresRoles(value={"user"},logical = Logical.OR)
	public PageResult rankingPage() {
		try {
			return PageResultEx.ok().data(myExamService.getRankingPage(new PageIn(request)));
		} catch (MyException e) {
			log.error("考试时间表错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("考试时间表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 分数统计
	 * 
	 * v1.0 zhanghc 2018年11月24日上午9:13:22
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/count")
	@ResponseBody
	@RequiresRoles(value={"user"},logical = Logical.OR)
	public PageResult count(Integer examId) {
		try {
			return PageResultEx.ok().data(myExamService.count(examId));
		} catch (MyException e) {
			log.error("分数统计错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("分数统计错误：", e);
			return PageResult.err();
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
	@RequiresRoles(value={"user"},logical = Logical.OR)
	public PageResult email(Integer examId) {
		try {
			Parm parm = parmService.getEntity(1);
			List<MyExam> list = myExamService.getList(examId);
			for(MyExam myExam : list){
				User user = userService.getEntity(myExam.getUserId());
				if (!ValidateUtil.isValid(user.getEmail())) {
					continue;
				}
				notifyService.pushEmail(parm.getEmailUserName(), user.getEmail(), "考试邮箱通知！", user.getName()+"-昵称。");
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