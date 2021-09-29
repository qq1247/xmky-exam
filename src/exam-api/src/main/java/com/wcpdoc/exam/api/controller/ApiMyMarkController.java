package com.wcpdoc.exam.api.controller;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.base.cache.ProgressBarCache;
import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.MyMark;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.MyExamDetailService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyMarkService;
import com.wcpdoc.exam.core.util.SpringUtil;

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
			pageIn.addAttr("curUserId", getCurUser().getId())
			  	  .addAttr("state", "1");
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
	 * 添加阅卷
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * @param examId
	 * @return PageResult
	 */
	@RequestMapping("/add")
	@ResponseBody
	public PageResult add(MyMark myMark) {
		try {
			myMarkService.add(myMark);
			return PageResultEx.ok();
		} catch (Exception e) {
			log.error("添加错误：", e);
			return PageResult.err();
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
	@RequestMapping("/updateScore")
	@ResponseBody
	public PageResult updateScore(Integer myExamDetailId, BigDecimal score) {
		try {
			myMarkService.updateScore(myExamDetailId, score);
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
	@RequestMapping("/doScore")
	@ResponseBody
	public PageResult doScore(Integer examId, Integer userId, Integer markId) {
		try {
			myMarkService.doScore(examId, userId, markId);
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
	@RequestMapping("/autoScore")
	@ResponseBody
	public PageResult autoScore(Integer id, Integer examId) {
		try {
			String processBarId = UUID.randomUUID().toString().replaceAll("-", "");
			LoginUser curUser = getCurUser();
			ProgressBarCache.setProgressBar(processBarId, 0.0, 10.0, null, 0);
			new Thread(new Runnable() {
				public void run() {
					SpringUtil.getBean(MyExamDetailService.class).autoMark(id, examId, curUser, processBarId);
				}
			}).start();
			
			return PageResultEx.ok().data(processBarId);
		} catch (Exception e) {
			log.error("完成试卷错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 我的阅卷列表
	 * 
	 * v1.0 chenyun 2021年8月2日下午3:14:45
	 * @return PageResult
	 */
	@RequestMapping("/markListpage")
	@ResponseBody
	public PageResult markListpage() {
		try {
			PageIn pageIn = new PageIn(request);
			pageIn.addAttr("markUserId", getCurUser().getId()); //阅卷人
			return PageResultEx.ok().data(myExamService.getListpage(pageIn));
		} catch (Exception e) {
			log.error("我的考试列表错误：", e);
			return PageResult.err();
		}
	}
}