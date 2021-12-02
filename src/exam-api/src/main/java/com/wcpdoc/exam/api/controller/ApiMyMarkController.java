package com.wcpdoc.exam.api.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.MyExamDetailService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyMarkService;

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
	
	/**
	 * 我的考试列表
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return pageOut
	 */
	@RequestMapping("/examListpage")
	@ResponseBody
	public PageResult examListpage() {
		try {
			PageIn pageIn = new PageIn(request);
			pageIn.addAttr("curUserId", getCurUser().getId());
			pageIn.addAttr("state", "1");
			return PageResultEx.ok().data(examService.getListpage(pageIn));
		} catch (Exception e) {
			log.error("我的阅卷列表错误：", e);
			return PageResult.err();
		}
	}
	
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
			PageOut listpage = myMarkService.getListpage(pageIn);
			
			for(Map<String, Object> map : listpage.getList()){
				map.put("examUserIds", map.get("examUserIds").toString().substring(1, map.get("examUserIds").toString().length() - 1).split(","));
			}
			
			return PageResultEx.ok().data(listpage);
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
			myMarkService.scoreUpdate(examId, userId, questionId, score);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("阅卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("阅卷错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 阅卷
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * @param examId
	 * @return PageResult
	 */
	@RequestMapping("/finish")
	@ResponseBody
	public PageResult finish(Integer examId, Integer userId) {
		try {
			myMarkService.finish(examId, userId);
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
	 * 阅卷考生
	 * 
	 * v1.0 chenyun 2021年8月2日下午3:14:45
	 * @return PageResult
	 */
	@RequestMapping("/userListpage")
	@ResponseBody
	public PageResult userListpage() {
		try {
			PageIn pageIn = new PageIn(request);
			pageIn.addAttr("markUserId", getCurUser().getId()); //阅卷人
			return PageResultEx.ok().data(myExamService.getListpage(pageIn));
		} catch (Exception e) {
			log.error("我的考试列表错误：", e);
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
		try {
			List<Map<String, Object>> list = myExamDetailService.getMarkAnswerList(userId, examId);
			for (Map<String, Object> map : list) {
				map.put("answers", new QuestionAnswer().getAnswers(
						(Integer)map.remove("questionType"), 
						(Integer)map.remove("questionAi"), 
						(String)map.remove("answer")
						)); 
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