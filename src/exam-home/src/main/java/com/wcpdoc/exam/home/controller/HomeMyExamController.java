package com.wcpdoc.exam.home.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.util.HibernateUtil;
import com.wcpdoc.exam.exam.entity.ExamUser;
import com.wcpdoc.exam.exam.service.ExamService;
import com.wcpdoc.exam.sys.cache.DictCache;

/**
 * 我的考试控制层
 * 
 * v1.0 zhanghc 2017-05-25 16:34:59
 */
@Controller
@RequestMapping("/home/myExam")
public class HomeMyExamController extends BaseController{
	private static final Logger log = LoggerFactory.getLogger(HomeMyExamController.class);
	
	@Resource
	private ExamService examService;
	
	/**
	 * 到达我的考试列表页面
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return String
	 */
	@RequestMapping("/toList")
	public String toList(Model model) {
		try {
			model.addAttribute("STATE_DICT", DictCache.getIndexDictlistMap().get("STATE"));
			return "/WEB-INF/jsp/home/myExam/myExamList.jsp";
		} catch (Exception e) {
			log.error("到达我的考试列表页面错误：", e);
			return "/WEB-INF/jsp/home/myExam/myExamList.jsp";
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
	public PageOut list(PageIn pageIn) {
		try {
			pageIn.setNine(getCurrentUser().getId() + "");
			PageOut listpage = examService.getListpage(pageIn);
			List<Map<String, Object>> list = listpage.getRows();
			
			Date curTime = new Date();
			for(Map<String, Object> map : list){
				ExamUser examUser = examService.getExamUser((int)map.get("ID"), getCurrentUser().getId());
				map.put("TOTAL_SCORE", examUser.getTotalScore());
				map.put("EXAM_USER_STATE", examUser.getState());
				
				Date startEndTime = (Date) map.get("START_TIME");
				Date examEndTime = (Date) map.get("END_TIME");
				if(startEndTime.getTime() <= curTime.getTime() && examEndTime.getTime() >= curTime.getTime()){
					map.put("START", "1");
				}else{
					map.put("START", "0");
				}
			}
			HibernateUtil.formatDict(list, DictCache.getIndexkeyValueMap(), "EXAM_USER_STATE", "EXAM_USER_STATE");
			return listpage;
		} catch (Exception e) {
			log.error("我的考试列表错误：", e);
			return new PageOut();
		}
	}
	
	/**
	 * 到达试卷页面
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @param model
	 * @param examId
	 * @return String
	 */
	@RequestMapping("/toPaper")
	public String toPaper(Model model, Integer examId) {
		try {
			examService.toPaper(model, getCurrentUser(), examId);
			return "/WEB-INF/jsp/home/myExam/myExamPaper.jsp";
		} catch (Exception e) {
			log.error("到达试卷页面错误：", e);
			model.addAttribute("message", e.getMessage());
			return "/WEB-INF/jsp/home/error.jsp";
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
			examService.updateAnswer(getCurrentUser(), examUserQuestionId, answer);
			return new PageResult(true, "更新成功");
		} catch (Exception e) {
			log.error("更新答案错误：", e);
			return new PageResult(false, "更新失败：" + e.getMessage());
		}
	}
	
	/**
	 * 完成试卷
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * @param examUserId
	 * @return PageResult
	 */
	@RequestMapping("/doPaper")
	@ResponseBody
	public PageResult doPaper(Integer examUserId) {
		try {
			examService.doPaper(getCurrentUser(), examUserId);
			return new PageResult(true, "完成成功");
		} catch (Exception e) {
			log.error("完成试卷错误：", e);
			return new PageResult(false, "完成失败：" + e.getMessage());
		}
	}
	
//	/**
//	 * 到达试卷预览页面
//	 * 
//	 * v1.0 zhanghc 2017-05-25 16:34:59
//	 * @return String
//	 */
//	@RequestMapping("/toPaperView")
//	public String toPaperView(Model model, Integer examUserId) {
//		try {
//			//校验数据有效性
//			ExamUser examUser = homeMyExamService.getExamUser(examUserId);
//			LoginUser user = getCurrentUser();
//			Exam exam = homeMyExamService.getExam(examUser.getExamId());
//			if(examUser.getUserId() != user.getId()){
//				throw new RuntimeException("未参与考试：" + exam.getName());
//			}
//			
//			//考试信息
//			model.addAttribute("exam", exam);
//			model.addAttribute("endTime", DateUtil.getFormatDateTime(exam.getEndTime()));
//			model.addAttribute("sysTime", DateUtil.getFormatDateTime());
//			
//			//试卷信息
//			Paper paper = homeMyExamService.getPaper(exam.getPaperId());
//			model.addAttribute("paper", paper);
//			
//			//试题信息
//			List<PaperQuestionEx> paperQuestionExList = homeMyExamService.getPaperList(exam.getPaperId());
//			model.addAttribute("paperQuestionExList", paperQuestionExList);
//			
//			//用户已做答案信息
//			List<ExamUserQuestion> examUserQuestionList = homeMyExamService.getExamUserQuestionList(exam.getId(), user.getId());
//			model.addAttribute("examUserQuestionList", examUserQuestionList);
//			Map<Long, ExamUserQuestion> examUserQuestionMap = new HashMap<Long, ExamUserQuestion>();
//			for(ExamUserQuestion examUserQuestion : examUserQuestionList){
//				examUserQuestionMap.put(examUserQuestion.getQuestionId().longValue(), examUserQuestion);
//			}
//			model.addAttribute("examUserQuestionMap", examUserQuestionMap);
//			model.addAttribute("questionOptions", DictCache.getIndexDictlistMap().get("QUESTION_OPTIONS"));
//			
//			//考试用户信息
//			model.addAttribute("examUser", examUser);
//			
//			return "/WEB-INF/jsp/home/myExam/myExamPaperView.jsp";
//		} catch (Exception e) {
//			log.error("到达试卷预览页面错误：", e);
//			model.addAttribute("message", e.getMessage());
//			return "/WEB-INF/jsp/home/error.jsp";
//		}
//	}
}
