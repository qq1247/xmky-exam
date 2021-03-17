package com.wcpdoc.exam.core.controller;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.base.cache.DictCache;
import com.wcpdoc.exam.core.constant.ConstantManager;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.ExamType;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperType;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.ExamTypeService;
import com.wcpdoc.exam.core.service.PaperService;
import com.wcpdoc.exam.core.service.PaperTypeService;

/**
 * 考试控制层
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
@Controller
@RequestMapping("/exam")
public class ExamController extends BaseController{
	private static final Logger log = LoggerFactory.getLogger(ExamController.class);
	
	@Resource
	private ExamService examService;
	@Resource
	private PaperService paperService;
	@Resource
	private PaperTypeService paperTypeService;
	@Resource
	private ExamTypeService examTypeService;
	
	/**
	 * 到达考试列表页面
	 * 
	 * v1.0 zhanghc 2018年10月25日下午9:23:06
	 * @param model
	 * @return String
	 */
	@RequestMapping("/toList")
	public String toList(Model model) {
		try {
			model.addAttribute("STATE_DICT", DictCache.getIndexDictlistMap().get("STATE"));
			return "exam/exam/examList";
		} catch (Exception e) {
			log.error("到达考试列表页面错误：", e);
			return "exam/exam/examList";
		}
	}
	
	/**
	 * 考试分类树
	 * 
	 * v1.0 zhanghc 2018年10月25日下午9:23:06
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/examTypeTreeList")
	@ResponseBody
	public PageResult examTypeTreeList() {
		try {
			return PageResultEx.ok().data(examTypeService.getAuthTreeList());
		} catch (Exception e) {
			log.error("获取考试分类树错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 试卷列表
	 * 
	 * v1.0 zhanghc 2018年10月27日上午9:22:15
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/paperList")
	@ResponseBody
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
	 * 到达添加考试页面 
	 * 
	 * v1.0 zhanghc 2018年10月25日下午9:23:06
	 * @param model
	 * @param examTypeId
	 * @return String
	 */
	@RequestMapping("/toAdd")
	public String toAdd(Model model) {
		try {
			return "exam/exam/examEdit";
		} catch (Exception e) {
			log.error("到达添加考试页面错误：", e);
			return "exam/exam/examEdit";
		}
	}
	
	/**
	 * 完成添加考试
	 * 
	 * v1.0 zhanghc 2018年10月25日下午9:23:06
	 * @return pageOut
	 */
	@RequestMapping("/doAdd")
	@ResponseBody
	public PageResult doAdd(Exam exam) {
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
	 * 到达修改考试页面 
	 * 
	 * v1.0 zhanghc 2018年10月25日下午9:23:06
	 * @return String
	 */
	@RequestMapping("/toEdit")
	public String toEdit(Model model, Integer id) {
		try {
			Exam exam = examService.getEntity(id);
			model.addAttribute("exam", exam);
			
			Paper paper = paperService.getEntity(exam.getPaperId());
			model.addAttribute("paper", paper);
			
			PaperType paperType = paperTypeService.getEntity(paper.getPaperTypeId());
			model.addAttribute("paperType", paperType);
			
			ExamType examType = examTypeService.getEntity(exam.getExamTypeId());
			model.addAttribute("examType", examType);
			return "exam/exam/examEdit";
		} catch (Exception e) {
			log.error("到达修改考试页面错误：", e);
			return "exam/exam/examEdit";
		}
	}
	
	/**
	 * 完成修改考试
	 * 
	 * v1.0 zhanghc 2018年10月25日下午9:23:06
	 * @return pageOut
	 */
	@RequestMapping("/doEdit")
	@ResponseBody
	public PageResult doEdit(Exam exam) {
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
//			entity.setPassScore(exam.getPassScore());
//			entity.setState(exam.getState());
			entity.setStartTime(exam.getStartTime());
			entity.setEndTime(exam.getEndTime());
			entity.setMarkStartTime(exam.getMarkStartTime());
			entity.setMarkEndTime(exam.getMarkEndTime());
			entity.setDescription(exam.getDescription());
			/*entity.setScoreA(exam.getScoreA());
			entity.setScoreARemark(exam.getScoreARemark());
			entity.setScoreB(exam.getScoreB());
			entity.setScoreBRemark(exam.getScoreBRemark());
			entity.setScoreC(exam.getScoreC());
			entity.setScoreCRemark(exam.getScoreCRemark());
			entity.setScoreD(exam.getScoreD());
			entity.setScoreDRemark(exam.getScoreDRemark());
			entity.setScoreE(exam.getScoreE());*/
			entity.setScoreERemark(exam.getScoreERemark());
//			entity.setExamTypeId(exam.getExamTypeId());
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
	@RequestMapping("/doDel")
	@ResponseBody
	public PageResult doDel(Integer id) {
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
	 * 到达考试配置页面
	 * 
	 * v1.0 zhanghc 2018年10月31日上午10:27:22
	 * @param model
	 * @param id
	 * @return String
	 */
	@RequestMapping("/toCfg")
	public String toCfg(Model model, Integer id) {
		try {
			model.addAttribute("id", id);
			return "exam/exam/examCfg";
		} catch (Exception e) {
			log.error("到达考试配置页面错误：", e);
			return "exam/exam/examCfg";
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
	@RequestMapping("/doCfg")
	@ResponseBody
	public PageResult doCfg(Integer id, Integer[] userIds, Integer[] myMarkIds) {
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
	@RequestMapping("/doPublish")
	@ResponseBody
	public PageResult doPublish(Integer id) {
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
}