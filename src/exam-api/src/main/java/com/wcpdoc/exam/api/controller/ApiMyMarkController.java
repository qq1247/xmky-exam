package com.wcpdoc.exam.api.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.base.service.UserService;
import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyExamDetail;
import com.wcpdoc.exam.core.entity.MyMark;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.entity.PaperQuestion;
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
@RequestMapping("/api/myMark")
public class ApiMyMarkController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiMyMarkController.class);
	
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
	@Resource
	private UserService userService;
	
	/**
	 * 我的阅卷列表
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return pageOut
	 */
	@RequestMapping("/listpage")
	@ResponseBody
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
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
	 * 我考试的列表
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return pageOut
	 */
	@RequestMapping("/examListpage")
	@ResponseBody
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
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
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
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
	@RequiresRoles(value={"user","subAdmin"},logical = Logical.OR)
	public PageResult updateScore(Integer myExamDetailId, BigDecimal score) {
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
				if (myMark.getMarkUserId() == getCurUser().getId()) {
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
			
			//我的考试更新分数
			BigDecimal totalScore = new BigDecimal(0);
			List<MyExamDetail> MyExamDetailList = myExamDetailService.getList(myExamDetail.getMyExamId());
			for(MyExamDetail entity : MyExamDetailList){
				totalScore = totalScore.add(entity.getScore());
			}
			MyExam myExam = myExamService.getEntity(myExamDetail.getMyExamId());
			myExam.setAnswerEndTime(new Date());
			myExam.setTotalScore(totalScore);
			myExamService.update(myExam);
			
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
	@RequiresRoles(value={"user","subAdmin"},logical = Logical.OR)
	public PageResult doScore(Integer examId, Integer userId, Integer markId) {
		try {
			// 校验数据有效性
			//MyExam myExam = myExamService.getEntity(markId);
			MyMark myMark = myMarkService.getEntity(markId);
			Exam exam = examService.getEntity(examId);
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

			if (myMark.getMarkUserId() != getCurUser().getId()) {
				throw new MyException("未参与阅卷：" + exam.getName());
			}
			MyExam myExam = myExamService.getEntity(examId, userId);
			List<MyExamDetail> myExamDetailList = myExamDetailService.getList(myExam.getId());
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
			myExam.setMarkEndTime(new Date());
			myExam.setTotalScore(totalScore);
//			if (totalScore.doubleValue() >= exam.getPassScore().doubleValue() ) {
//				myExam.setAnswerState(1);
//			} else {
//				myExam.setAnswerState(2);
//			}
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
	@RequestMapping("/autoScore")
	@ResponseBody
	@RequiresRoles(value={"user","subAdmin"},logical = Logical.OR)
	public PageResult autoScore(Integer id, Integer examId) {
		try {
			String processBarId = UUID.randomUUID().toString().replaceAll("-", "");
			LoginUser curUser = getCurUser();
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
	@RequiresRoles(value={"user","subAdmin"},logical = Logical.OR)
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