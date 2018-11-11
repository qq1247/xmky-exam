package com.wcpdoc.exam.home.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.util.DateUtil;
import com.wcpdoc.exam.exam.entity.Exam;
import com.wcpdoc.exam.exam.entity.ExamUser;
import com.wcpdoc.exam.exam.entity.ExamUserQuestion;
import com.wcpdoc.exam.exam.entity.MarkUser;
import com.wcpdoc.exam.exam.entity.Paper;
import com.wcpdoc.exam.exam.entity.PaperQuestionEx;
import com.wcpdoc.exam.exam.service.ExamService;
import com.wcpdoc.exam.exam.service.ExamUserQuestionService;
import com.wcpdoc.exam.exam.service.ExamUserService;
import com.wcpdoc.exam.exam.service.PaperService;
import com.wcpdoc.exam.home.service.HomeMarkService;
import com.wcpdoc.exam.sys.cache.DictCache;
import com.wcpdoc.exam.sys.service.UserService;

/**
 * 判卷服务层实现
 * 
 * v1.0 zhanghc 2017-06-23 15:39:11
 */
@Service
public class HomeMarkServiceImpl extends BaseServiceImp<Object> implements HomeMarkService {
	@Resource
	private ExamService examService;
	@Resource
	private ExamUserService examUserService;
	@Resource
	private PaperService paperService;
	@Resource
	private UserService userService;
	@Resource
	private ExamUserQuestionService examUserQuestionService;

	@Override
	public void setDao(BaseDao<Object> dao) {
		
	}

	@Override
	public PageOut getMarkPaperListpage(PageIn pageIn) {
		return examService.getMarkListpage(pageIn);
	}

	@Override
	public ExamUser getExamUser(Integer examUserId) {
		return examUserService.getEntity(examUserId);
	}

	@Override
	public Exam getExam(Integer examId) {
		return examService.getEntity(examId);
	}

	@Override
	public Paper getPaper(Integer paperId) {
		return paperService.getEntity(paperId);
	}

	@Override
	public void updateExamUser(ExamUser examUser) {
		examUserService.update(examUser);
	}

	@Override
	public List<PaperQuestionEx> getPaperList(Integer paperId) {
		return paperService.getPaperList(paperId);
	}

	@Override
	public List<ExamUserQuestion> getExamUserQuestionList(Integer examId, Integer userId) {
		return examUserQuestionService.getList(examId, userId);
	}

	@Override
	public void updateScore(LoginUser user, Integer examUserQuestionId, BigDecimal score) {
		examService.updateMarkScore(user, examUserQuestionId, score);
	}

	@Override
	public void doMark(LoginUser user, Integer examUserId) {
		examService.doMark(user, examUserId);
	}

	@Override
	public void toMark(Model model, LoginUser user, Integer examUserId) {
		//校验数据有效性
		ExamUser examUser = getExamUser(examUserId);
		Exam exam = examService.getEntity(examUser.getExamId());
		List<MarkUser> markUserList = examService.getMarkUserList(examUser.getExamId());
		boolean ok = false;
		for(MarkUser markUser : markUserList){
			if(markUser.getUserId() == user.getId()){
				ok = true;
				break;
			}
		}
		if(!ok){
			throw new RuntimeException("未参与考试：" + exam.getName());
		}
		
		if(exam.getEndTime().getTime() > new Date().getTime()){
			throw new RuntimeException("考试未结束！");
		}
		
		//用户信息
		model.addAttribute("user", userService.getEntity(examUser.getUserId()));
		
		//考试信息
		model.addAttribute("exam", exam);
		model.addAttribute("endTime", DateUtil.getFormatDateTime(exam.getEndTime()));
		model.addAttribute("sysTime", DateUtil.getFormatDateTime());
		
		//试卷信息
		Paper paper = getPaper(exam.getPaperId());
		model.addAttribute("paper", paper);
		
		//试题信息
		List<PaperQuestionEx> paperQuestionExList = getPaperList(exam.getPaperId());
		model.addAttribute("paperQuestionExList", paperQuestionExList);
		
		//用户已做答案信息
		List<ExamUserQuestion> examUserQuestionList = getExamUserQuestionList(exam.getId(), examUser.getUserId());
		model.addAttribute("examUserQuestionList", examUserQuestionList);
		Map<Long, ExamUserQuestion> examUserQuestionMap = new HashMap<Long, ExamUserQuestion>();
		for(ExamUserQuestion examUserQuestion : examUserQuestionList){
			examUserQuestionMap.put(examUserQuestion.getQuestionId().longValue(), examUserQuestion);
		}
		model.addAttribute("examUserQuestionMap", examUserQuestionMap);
		model.addAttribute("questionOptions", DictCache.getIndexDictlistMap().get("QUESTION_OPTIONS"));
		
		//标记为判卷中
		examUser.setState(5);
		examUser.setUpdateMarkUserId(user.getId());
		examUser.setUpdateMarkTime(new Date());
		updateExamUser(examUser);
		model.addAttribute("examUser", examUser);
	}

	@Override
	public void toMarkView(LoginUser user, Model model, Integer examUserId) {
		//校验数据有效性
		ExamUser examUser = getExamUser(examUserId);
		Exam exam = examService.getEntity(examUser.getExamId());
		List<MarkUser> markUserList = examService.getMarkUserList(examUser.getExamId());
		boolean ok = false;
		for(MarkUser markUser : markUserList){
			if(markUser.getUserId() == user.getId()){
				ok = true;
				break;
			}
		}
		if(!ok){
			throw new RuntimeException("未参与考试：" + exam.getName());
		}
		
//		if(exam.getEndTime().getTime() > new Date().getTime()){
//			throw new RuntimeException("考试未结束！");
//		}
		
		//用户信息
		model.addAttribute("user", userService.getEntity(examUser.getUserId()));
		
		//考试信息
		model.addAttribute("exam", exam);
		model.addAttribute("endTime", DateUtil.getFormatDateTime(exam.getEndTime()));
		model.addAttribute("sysTime", DateUtil.getFormatDateTime());
		
		//试卷信息
		Paper paper = getPaper(exam.getPaperId());
		model.addAttribute("paper", paper);
		
		//试题信息
		List<PaperQuestionEx> paperQuestionExList = getPaperList(exam.getPaperId());
		model.addAttribute("paperQuestionExList", paperQuestionExList);
		
		//用户已做答案信息
		List<ExamUserQuestion> examUserQuestionList = getExamUserQuestionList(exam.getId(), examUser.getUserId());
		model.addAttribute("examUserQuestionList", examUserQuestionList);
		Map<Long, ExamUserQuestion> examUserQuestionMap = new HashMap<Long, ExamUserQuestion>();
		for(ExamUserQuestion examUserQuestion : examUserQuestionList){
			examUserQuestionMap.put(examUserQuestion.getQuestionId().longValue(), examUserQuestion);
		}
		model.addAttribute("examUserQuestionMap", examUserQuestionMap);
		model.addAttribute("questionOptions", DictCache.getIndexDictlistMap().get("QUESTION_OPTIONS"));
		
		//考试用户信息
		model.addAttribute("examUser", examUser);
	}
}
