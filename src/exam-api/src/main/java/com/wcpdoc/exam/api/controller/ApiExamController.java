package com.wcpdoc.exam.api.controller;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

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
import com.wcpdoc.exam.core.constant.ConstantManager;
import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.ExamTypeService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.PaperService;
import com.wcpdoc.exam.core.service.PaperTypeService;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.notify.exception.NotifyException;
import com.wcpdoc.exam.notify.service.NotifyService;

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
	private PaperService paperService;
	@Resource
	private PaperTypeService paperTypeService;
	@Resource
	private ExamTypeService examTypeService;
	@Resource
	private UserService userService;
	@Resource
	private NotifyService notifyService;
	@Resource
	private ParmService parmService;
	@Resource
	private MyExamService myExamService;
	
	/**
	 * 试卷列表
	 * 
	 * v1.0 zhanghc 2018年10月27日上午9:22:15
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/paperList")
	@ResponseBody
	@RequiresRoles("OP")
	public PageResult paperList(PageIn pageIn) {
		try {
			if(!ConstantManager.ADMIN_LOGIN_NAME.equals(getCurUser().getLoginName())) {
				pageIn.setTen(getCurUser().getId().toString());
			}
			pageIn.setThree("1");
			return PageResultEx.ok().data(paperService.getListpage(pageIn));
		} catch (Exception e) {
			log.error("试卷列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 用户列表 
	 * 
	 * v1.0 zhanghc 2018年10月31日上午10:27:22
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/userList")
	@ResponseBody
	@RequiresRoles("OP")
	public PageResult userList(PageIn pageIn) {
		try {
			return PageResultEx.ok().data(examService.getUserListpage(pageIn));
		} catch (Exception e) {
			log.error("用户列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 考试列表 
	 * 
	 * v1.0 zhanghc 2018年10月25日下午9:23:06
	 * @return pageOut
	 */
	@RequestMapping("/list")
	@ResponseBody
	@RequiresRoles("OP")
	public PageResult list(PageIn pageIn) {
		try {
			if(!ConstantManager.ADMIN_LOGIN_NAME.equals(getCurUser().getLoginName())) {
				pageIn.setTen(getCurUser().getId().toString());
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
	@RequiresRoles("OP")
	public PageResult add(Exam exam) {
		try {
			//校验数据有效性
			if(exam.getStartTime().getTime() <= new Date().getTime()) {
				throw new MyException("考试开始时间必须大于当前时间！");
			}
			if(exam.getStartTime().getTime() >= exam.getEndTime().getTime()) {
				throw new MyException("考试结束时间必须大于考试开始时间！");
			}
			if(exam.getMarkStartTime().getTime() <= exam.getEndTime().getTime()) {
				throw new MyException("阅卷开始时间必须大于考试结束时间！");
			}
			if(exam.getMarkStartTime().getTime() >= exam.getMarkEndTime().getTime()) {
				throw new MyException("阅卷结束时间必须大于阅卷开始时间！");
			}
			
			//添加考试
			exam.setUpdateTime(new Date());
			exam.setUpdateUserId(getCurUser().getId());
			exam.setState(2);
			examService.add(exam);
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
	@RequiresRoles("OP")
	public PageResult edit(Exam exam) {
		try {
			//校验数据有效性
			Exam entity = examService.getEntity(exam.getId());
			if(entity.getState() == 1) {
				throw new MyException("考试已发布！");
			}
			if(exam.getStartTime().getTime() <= new Date().getTime()) {
				throw new MyException("考试开始时间必须大于当前时间！");
			}
			if(exam.getStartTime().getTime() >= exam.getEndTime().getTime()) {
				throw new MyException("考试结束时间必须大于考试开始时间！");
			}
			if(exam.getMarkStartTime().getTime() <= exam.getEndTime().getTime()) {
				throw new MyException("阅卷开始时间必须大于考试结束时间！");
			}
			if(exam.getMarkStartTime().getTime() >= exam.getMarkEndTime().getTime()) {
				throw new MyException("阅卷结束时间必须大于阅卷开始时间！");
			}
			
			//添加考试
			entity.setName(exam.getName());
			entity.setPaperId(exam.getPaperId());
			entity.setStartTime(exam.getStartTime());
			entity.setEndTime(exam.getEndTime());
			entity.setMarkStartTime(exam.getMarkStartTime());
			entity.setMarkEndTime(exam.getMarkEndTime());
			entity.setScoreState(exam.getScoreState());
			entity.setRankState(exam.getRankState());
			entity.setLoginType(exam.getLoginType());
			entity.setDescription(exam.getDescription());
			entity.setScoreERemark(exam.getScoreERemark());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(getCurUser().getId());
			examService.update(entity);
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
	@RequiresRoles("OP")
	public PageResult del(Integer id) {
		try {
			Date curTime = new Date();
			Exam exam = examService.getEntity(id);
			if(exam.getStartTime().getTime() >= curTime.getTime()
					&& exam.getEndTime().getTime() <= curTime.getTime()) {
				throw new MyException("【"+exam.getName()+"】考试未结束");
			}
			
			exam.setState(0);
			examService.update(exam);
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
	 * 完成考试配置
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param id
	 * @param userIds
	 * @param markIds
	 * @return PageResult
	 */
	@RequestMapping("/cfg")
	@ResponseBody
	@RequiresRoles("OP")
	public PageResult cfg(Integer id, Integer[] userIds, Integer[] myMarkIds) {
		try {
			examService.doCfg(id, userIds, myMarkIds);
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
	@RequiresRoles("OP")
	public PageResult publish(Integer id) {
		try {
			Exam exam = examService.getEntity(id);
			if(exam.getState() == 0) {
				throw new MyException("考试【"+exam.getName()+"】已删除！");
			}
			if(exam.getState() == 1) {
				throw new MyException("考试【"+exam.getName()+"】已发布！");
			}
			
			exam.setState(1);
			examService.update(exam);
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
	 * 邮件通知
	 * 
	 * v1.0 zhanghc 2018年11月24日上午9:13:22
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/email")
	@ResponseBody
	@RequiresRoles("OP")
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
	
	/**
	 * 归档
	 * 
	 * v1.0 zhanghc 2018年11月24日上午9:13:22
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/archive")
	@ResponseBody
	@RequiresRoles("OP")
	public PageResult archive(Integer id) {
		try {
			Exam exam = examService.getEntity(id);
			exam.setState(3);
			examService.update(exam);
			return PageResult.ok();
		}catch (Exception e) {
			log.error("归档错误：", e);
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
	public PageResult rankingPage(PageIn pageIn, Integer id) {
		try {
			if (id == null) {
				throw new MyException("参数错误：id");
			}
			pageIn.setOne(id.toString());
			return PageResultEx.ok().data(myExamService.getRankingPage(pageIn));
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
	@RequiresRoles("OP")
	public PageResult count(Integer id) {
		try {
			return PageResultEx.ok().data(myExamService.count(id));
		} catch (MyException e) {
			log.error("分数统计错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("分数统计错误：", e);
			return PageResult.err();
		}
	}
}