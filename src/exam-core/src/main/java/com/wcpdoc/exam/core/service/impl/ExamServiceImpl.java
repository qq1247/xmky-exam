package com.wcpdoc.exam.core.service.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.wcpdoc.exam.base.service.OrgService;
import com.wcpdoc.exam.base.service.UserService;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.dao.ExamDao;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.ExamUser;
import com.wcpdoc.exam.core.entity.ExamUserQuestion;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.MarkUser;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.ExamTypeService;
import com.wcpdoc.exam.core.service.ExamUserQuestionService;
import com.wcpdoc.exam.core.service.ExamUserService;
import com.wcpdoc.exam.core.service.MarkUserService;
import com.wcpdoc.exam.core.service.PaperService;
import com.wcpdoc.exam.core.service.PaperTypeService;
import com.wcpdoc.exam.core.service.QuestionService;

/**
 * 考试服务层实现
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
@Service
public class ExamServiceImpl extends BaseServiceImp<Exam> implements ExamService {
	private static final Logger log = LoggerFactory.getLogger(ExamServiceImpl.class);
	
	@Resource
	private ExamDao examDao;
	@Resource
	private PaperService paperService;
	@Resource
	private QuestionService questionService;
	@Resource
	private PaperTypeService paperTypeService;
	@Resource
	private OrgService orgService;
	@Resource
	private ExamTypeService examTypeService;
	@Resource
	private ExamUserService examUserService;
	@Resource
	private ExamUserQuestionService examUserQuestionService;
	@Resource
	private MarkUserService markUserService;
	@Resource
	private UserService userService;
	
	@Override
	@Resource(name = "examDaoImpl")
	public void setDao(BaseDao<Exam> dao) {
		super.dao = dao;
	}

	@Override
	public PageOut getUserListpage(PageIn pageIn) {
		return examDao.getUserListpage(pageIn);
	}

	@Override
	public void doCfg(Integer id, Integer[] userIds, Integer[] markUserIds) {
		// 校验数据有效性
		if (id == null) {
			throw new MyException("参数错误：id");
		}
		Exam exam = getEntity(id);
		if (exam.getState() == 0) {
			throw new MyException("考试已删除！");
		}
//		if (exam.getState() == 2) {
//			throw new MyException("考试未发布！");
//		}

		if (exam.getEndTime().getTime() <= new Date().getTime()) {
			throw new MyException("考试已结束，不允许添加！");
		}

		// 添加考试用户
		List<ExamUser> examUserList = examUserService.getList(id);
		List<Question> questionList = paperService.getQuestionList(exam.getPaperId());
		Set<Integer> userIdsSet = new HashSet<>(Arrays.asList(userIds));
		ListIterator<ExamUser> examUserListIterator = examUserList.listIterator();
		while (examUserListIterator.hasNext()) {//共同的剔除
			ExamUser next = examUserListIterator.next();
			if (userIdsSet.contains(next.getUserId())) {
				examUserListIterator.remove();
				userIdsSet.remove(next.getUserId());
				
				for (Question question : questionList) {// 添加考试用户试题
					ExamUserQuestion examUserQuestion = new ExamUserQuestion();
					examUserQuestion.setExamUserId(next.getId());
					examUserQuestion.setExamId(next.getExamId());
					examUserQuestion.setUserId(next.getUserId());
					examUserQuestion.setQuestionId(question.getId());
					examUserQuestionService.add(examUserQuestion);
				}
			}
		}
		examUserListIterator = examUserList.listIterator();
		while (examUserListIterator.hasNext()) {//多余的删除
			ExamUser next = examUserListIterator.next();
			examUserQuestionService.delByExamUserId(next.getId());
			
			examUserService.del(next.getId());
		}
		Date curTime = new Date();
		for (Integer userId : userIdsSet) {//没有的添加
			ExamUser examUser = new ExamUser();
			examUser.setExamId(id);
			examUser.setUserId(userId);
			examUser.setTotalScore(BigDecimal.ZERO);
			examUser.setState(1);
			examUser.setMarkState(1);
			examUser.setUpdateTime(curTime);
			examUser.setUpdateUserId(getCurUser().getId());
			examUserService.add(examUser);
			
			for (Question question : questionList) {// 添加考试用户试题
				ExamUserQuestion examUserQuestion = new ExamUserQuestion();
				examUserQuestion.setExamUserId(examUser.getId());
				examUserQuestion.setExamId(examUser.getExamId());
				examUserQuestion.setUserId(examUser.getUserId());
				examUserQuestion.setQuestionId(question.getId());
				examUserQuestionService.add(examUserQuestion);
			}
		}
		
		// 添加阅卷用户
		List<MarkUser> markUserList = markUserService.getList(id);
		Set<Integer> markUserIdsSet = new HashSet<>(Arrays.asList(markUserIds));
		ListIterator<MarkUser> markUserListIterator = markUserList.listIterator();
		while (markUserListIterator.hasNext()) {//共同的剔除
			MarkUser next = markUserListIterator.next();
			if (markUserIdsSet.contains(next.getUserId())) {
				markUserListIterator.remove();
				markUserIdsSet.remove(next.getUserId());
			}
		}
		markUserListIterator = markUserList.listIterator();
		while (markUserListIterator.hasNext()) {//多余的删除
			MarkUser next = markUserListIterator.next();
			markUserService.del(next.getId());
		}
		for (Integer userId : markUserIdsSet) {//没有的添加
			MarkUser markUser = new MarkUser();
			markUser.setExamId(id);
			markUser.setUserId(userId);
			markUserService.add(markUser);
		}
	}

	@Override
	public void doPaper(LoginUser user, Integer examUserId) {
		
	}
	
	@Override
	public void doForcePaper(LoginUser user) {
		//标记为强制交卷
		log.debug("开始强制交卷");
		examDao.doForcePaper(user);
		log.debug("成功强制交卷");
	}

	@Override
	public void updateMarkScore(LoginUser user, Integer examUserQuestionId, BigDecimal score) {
		//校验数据有效性
		ExamUserQuestion examUserQuestion = examUserQuestionService.getEntity(examUserQuestionId);
		List<MarkUser> markUserList = null;//getMarkUserList(examUserQuestion.getExamId());
		Exam exam = getEntity(examUserQuestion.getExamId());
		if(exam.getState() == 0){
			throw new MyException("考试已删除！");
		}
		if(exam.getState() == 2){
			throw new MyException("考试未发布！");
		}
		if(exam.getMarkStartTime().getTime() > (new Date().getTime())){
			throw new MyException("阅卷未开始！");
		}
		if(exam.getMarkEndTime().getTime() < (new Date().getTime() - 5000)){
			throw new MyException("阅卷已结束！");
		}
		
		boolean ok = false;
		for(MarkUser markUser : markUserList){
			if(markUser.getUserId() == user.getId()){
				ok = true;
				break;
			}
		}
		
		if(!ok){
			throw new MyException("未参与考试：" + exam.getName());
		}
		
		if(score != null){
//			PaperQuestion paperQuestion = paperService.getPaperQuestion(exam.getPaperId(), examUserQuestion.getQuestionId());
//			if(new BigDecimal(BigDecimalUtil.sub(score.toString(), paperQuestion.getScore().toString())).doubleValue() > 0){
//				throw new MyException("最大分值：" + paperQuestion.getScore());
//			}
		}
		
		//更新阅卷分数
		examUserQuestion.setScore(score);
//		examUserQuestion.setUpdateMarkUserId(user.getId());
//		examUserQuestion.setUpdateMarkTime(new Date());
		examUserQuestionService.update(examUserQuestion);
	}

	@Override
	public void doMark(LoginUser user, Integer examUserId) {
		//校验数据有效性
		ExamUser examUser = examUserService.getEntity(examUserId);
		List<MarkUser> markUserList = null;//getMarkUserList(examUser.getExamId());
		Exam exam = getEntity(examUser.getExamId());
		if(exam.getState() == 0){
			throw new MyException("考试已删除！");
		}
		if(exam.getState() == 2){
			throw new MyException("考试未发布！");
		}
		if(exam.getMarkStartTime().getTime() > (new Date().getTime())){
			throw new MyException("阅卷未开始！");
		}
		if(exam.getMarkEndTime().getTime() < (new Date().getTime() - 5000)){
			throw new MyException("阅卷已结束！");
		}
		
		boolean ok = false;
		for(MarkUser markUser : markUserList){
			if(markUser.getUserId() == user.getId()){
				ok = true;
				break;
			}
		}
		if(!ok){
			throw new MyException("未参与阅卷：" + exam.getName());
		}
		
		List<ExamUserQuestion> examUserQuestionList = examUserQuestionService.getList(examUserId);
		int num = 0;
		BigDecimal totalScore = new BigDecimal(0);
		for(ExamUserQuestion examUserQuestion : examUserQuestionList){
			if(examUserQuestion.getScore() == null){
				num++;
			}else{
				//totalScore = new BigDecimal(BigDecimalUtil.add(examUserQuestion.getScore().toString(), totalScore.toString()));
			}
		}
		
		if(num > 0){
			throw new MyException("还有" + num + "道题未判！");
		}
		
		//考试用户标记为及格或不及格
//		examUser.setUpdateMarkUserId(user.getId());
//		examUser.setUpdateMarkTime(new Date());
		examUser.setTotalScore(totalScore);
		if(exam.getPassScore().doubleValue() >= totalScore.doubleValue()){
			examUser.setState(7);
		}else{
			examUser.setState(8);
		}
		
		examUserService.update(examUser);
	}

	@Override
	public List<Exam> getList(Integer examTypeId) {
		return examDao.getList(examTypeId);
	}

	@Override
	public PageOut getGradeListpage(PageIn pageIn) {
		return examDao.getGradeListpage(pageIn);
	}
}
