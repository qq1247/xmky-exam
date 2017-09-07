package com.wcpdoc.exam.home.service.impl;

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
import com.wcpdoc.exam.exam.entity.Paper;
import com.wcpdoc.exam.exam.entity.PaperQuestionEx;
import com.wcpdoc.exam.exam.service.ExamService;
import com.wcpdoc.exam.exam.service.ExamUserQuestionService;
import com.wcpdoc.exam.exam.service.ExamUserService;
import com.wcpdoc.exam.exam.service.PaperService;
import com.wcpdoc.exam.home.service.HomeMyExamService;
import com.wcpdoc.exam.sys.cache.DictCache;

/**
 * 首页服务层实现
 * 
 * v1.0 zhanghc 2017-06-23 15:39:11
 */
@Service
public class HomeMyExamServiceImpl extends BaseServiceImp<Object> implements HomeMyExamService {
	@Resource
	private ExamService examService;
	@Resource
	private PaperService paperService;
	@Resource
	private ExamUserQuestionService examUserQuestionService;
	@Resource
	private ExamUserService examUserService;

	@Override
	public void setDao(BaseDao<Object> dao) {
		
	}

	@Override
	public final PageOut getMyExamListpage(PageIn pageIn) {
		return examService.getMyExamListpage(pageIn);
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
	public List<ExamUserQuestion> getExamUserQuestionList(Integer examId, Integer userId) {
		return examUserQuestionService.getList(examId, userId);
	}
	
	@Override
	public void toPaper(Model model, LoginUser user, Integer examUserId) {
		//校验数据有效性
		ExamUser examUser = getExamUser(examUserId);
		Exam exam = getExam(examUser.getExamId());
		if(examUser.getUserId() != user.getId()){
			throw new RuntimeException("未参与考试：" + exam.getName());
		}
		if(exam.getEndTime().getTime() <= (new Date().getTime() - 5000)){
			throw new RuntimeException("考试已结束！");
		}
		
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
		
		//标记为考试中
		examUser.setState(2);
		examUser.setUpdateUserId(user.getId());
		examUser.setUpdateTime(new Date());
		updateExamUser(examUser);
		model.addAttribute("examUser", examUser);
	}

	@Override
	public void updateAnswer(LoginUser user, Integer examUserQuestionId, String answer) {
		examService.updateAnswer(user, examUserQuestionId, answer);
	}

	@Override
	public void doPaper(LoginUser user, Integer examUserId) {
		examService.doPaper(user, examUserId);
	}

	@Override
	public List<PaperQuestionEx> getPaperList(Integer paperId) {
		return paperService.getPaperList(paperId);
	}

	@Override
	public void updateExamUser(ExamUser examUser) {
		examUserService.update(examUser);
	}

	@Override
	public ExamUser getExamUser(Integer examId, Integer userId) {
		return examUserService.getEntity(examId, userId);
	}

	@Override
	public ExamUser getExamUser(Integer examUserId) {
		return examUserService.getEntity(examUserId);
	}
}
