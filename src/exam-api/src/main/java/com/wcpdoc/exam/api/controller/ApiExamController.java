package com.wcpdoc.exam.api.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.base.service.UserService;
import com.wcpdoc.exam.core.constant.ConstantManager;
import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.MyMark;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.ExamTypeService;
import com.wcpdoc.exam.core.service.MyMarkService;
import com.wcpdoc.exam.core.service.PaperTypeService;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 考试控制层
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
@Controller
@RequestMapping("/api/exam")
public class ApiExamController extends BaseController{
	private static final Logger log = LoggerFactory.getLogger(ApiExamController.class);
	
	@Resource
	private ExamService examService;
	@Resource
	private PaperTypeService paperTypeService;
	@Resource
	private ExamTypeService examTypeService;
	@Resource
	private MyMarkService myMarkService;
	@Resource
	private UserService userService;
	
	/**
	 * 考试列表 
	 * 
	 * v1.0 zhanghc 2018年10月25日下午9:23:06
	 * @return pageOut
	 */
	@RequestMapping("/listpage")
	@ResponseBody
	public PageResult listpage() {
		try {
			PageIn pageIn = new PageIn(request);
			if(!ConstantManager.ADMIN_LOGIN_NAME.equals(getCurUser().getLoginName())) {
				pageIn.addAttr("curUserId", getCurUser().getId());
			}
			return PageResultEx.ok().data(examService.getListpage(pageIn));
		} catch (Exception e) {
			log.error("考试列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 完成添加考试
	 * 
	 * v1.0 zhanghc 2018年10月25日下午9:23:06
	 * @return pageOut
	 */
	@RequestMapping("/add")
	@ResponseBody
	public PageResult add(Exam exam) {
		try {
			examService.addAndUpdate(exam);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成添加考试错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成添加考试错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 完成修改考试
	 * 
	 * v1.0 zhanghc 2018年10月25日下午9:23:06
	 * @return pageOut
	 */
	@RequestMapping("/edit")
	@ResponseBody
	public PageResult edit(Exam exam) {
		try {
			examService.updateAndUpdate(exam);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成修改考试错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成修改考试错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 完成删除考试
	 * 
	 * v1.0 zhanghc 2017-06-11 09:13:23
	 * @return pageOut
	 */
	@RequestMapping("/del")
	@ResponseBody
	public PageResult del(Integer id) {
		try {
			examService.delAndUpdate(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成删除考试错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成删除考试错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 考试用户列表 
	 * 
	 * v1.0 zhanghc 2018年10月31日上午10:27:22
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/examUserList")
	@ResponseBody
	public PageResult examUserList(Integer id) {
		try {
			return PageResultEx.ok().data(examService.getExamUserList(id));
		} catch (Exception e) {
			log.error("用户列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 阅卷用户列表
	 * 
	 * v1.0 zhanghc 2018年11月24日上午9:13:22
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/markUserList")
	@ResponseBody
	public PageResult markUserList(Integer id) {
		try {
			List<MyMark> myMarkList = myMarkService.getList(id);
			List<Map<String, Object>> result = new ArrayList<>();
			for (MyMark myMark : myMarkList) {
				Map<String, Object> map = new HashMap<>();
				map.put("markUserId", userService.getEntity(myMark.getMarkUserId()).getId());
				map.put("markUserName", userService.getEntity(myMark.getMarkUserId()).getName());
				if (ValidateUtil.isValid(myMark.getExamUserIds())) {
					map.put("examUserList", examService.getMarkExamUserList(myMark.getExamId(), myMark.getMarkUserId()));
				}
				if (ValidateUtil.isValid(myMark.getQuestionIds())) {
					map.put("questionList", examService.getMarkQuestionList(myMark.getExamId(), myMark.getMarkUserId()));
				}
				result.add(map);
			}
			return PageResultEx.ok().data(result);
		}catch (Exception e) {
			log.error("阅卷用户列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 考试更新考试用户阅卷用户
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param id
	 * @param userIds
	 * @param markIds
	 * @return PageResult
	 */
	@RequestMapping("/updateMarkSet")
	@ResponseBody
	public PageResult updateMarkSet(Integer id, String[] examUserIds, Integer[] markUserIds) {
		try {
			examService.updateMarkSet(id, examUserIds, markUserIds);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成更新考试用户错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成更新考试用户错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 完成考试更新判卷用户
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param id
	 * @param markUserIds
	 * @param examUserIds
	 * @param questionIds
	 * @return PageResult
	 */
	@RequestMapping("/updateMarkUser")
	@ResponseBody
	public PageResult updateMarkUser(Integer id, Integer[] markUserIds, String[] examUserIds, String[] questionIds) {
		try {
			examService.updateMarkUser(id, markUserIds, examUserIds, questionIds);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成考试配置错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成考试配置错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 完成发布
	 * 
	 * v1.0 zhanghc 2018年11月24日上午9:13:22
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/publish")
	@ResponseBody
	public PageResult publish(Integer id) {
		try {
			examService.publish(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成发布错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成发布错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 归档
	 * 
	 * v1.0 zhanghc 2018年11月24日上午9:13:22
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/archive")
	@ResponseBody
	public PageResult archive(Integer id) {
		try {
			Exam exam = examService.getEntity(id);
			exam.setUpdateTime(new Date());
			exam.setUpdateUserId(getCurUser().getId());
			exam.setState(3);
			examService.update(exam);
			return PageResult.ok();
		}catch (Exception e) {
			log.error("归档错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 在线情况
	 * 
	 * v1.0 chenyun 2021年9月7日下午1:27:31
	 * @param ids
	 * @return PageResult
	 */
	@RequestMapping("/onLine")
	@ResponseBody
	public PageResult onLine(Integer[] ids) {
		try {
			return PageResultEx.ok().data(examService.onLine(ids));
		}catch (Exception e) {
			log.error("在线情况：", e);
			return PageResult.err();
		}
	}
}