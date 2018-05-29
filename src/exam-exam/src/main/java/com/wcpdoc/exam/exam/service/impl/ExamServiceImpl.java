package com.wcpdoc.exam.exam.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.util.BigDecimalUtil;
import com.wcpdoc.exam.core.util.StringUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.exam.dao.ExamDao;
import com.wcpdoc.exam.exam.entity.Exam;
import com.wcpdoc.exam.exam.entity.ExamType;
import com.wcpdoc.exam.exam.entity.ExamUser;
import com.wcpdoc.exam.exam.entity.ExamUserQuestion;
import com.wcpdoc.exam.exam.entity.MarkUser;
import com.wcpdoc.exam.exam.entity.Paper;
import com.wcpdoc.exam.exam.entity.PaperQuestion;
import com.wcpdoc.exam.exam.entity.Question;
import com.wcpdoc.exam.exam.service.ExamService;
import com.wcpdoc.exam.exam.service.ExamTypeService;
import com.wcpdoc.exam.exam.service.ExamUserQuestionService;
import com.wcpdoc.exam.exam.service.ExamUserService;
import com.wcpdoc.exam.exam.service.MarkUserService;
import com.wcpdoc.exam.exam.service.PaperQuestionService;
import com.wcpdoc.exam.exam.service.PaperService;
import com.wcpdoc.exam.exam.service.PaperTypeService;
import com.wcpdoc.exam.exam.service.QuestionService;
import com.wcpdoc.exam.sys.service.OrgService;

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
	private PaperQuestionService paperQuestionService;
	
	private static final Object DO_EXAM_USER_ADD_LOCK = new Object();
	private static final Object DO_MARK_USER_ADD_LOCK = new Object();

	@Override
	@Resource(name = "examDaoImpl")
	public void setDao(BaseDao<Exam> dao) {
		super.dao = dao;
	}
	

	@Override
	public void saveAndUpdate(Exam exam) {
		//校验数据有效性
		if(exam.getStartTime().getTime() <= new Date().getTime()){
			throw new RuntimeException("开始时间必须大于当前时间！");
		}
		if(exam.getStartTime().getTime() >= exam.getEndTime().getTime()){
			throw new RuntimeException("结束时间必须大于开始时间！");
		}
		
		//添加考试
		examDao.save(exam);
	}
	
	@Override
	public void updateAndUpdate(Exam exam) {
		//校验数据有效性
		if(exam.getStartTime().getTime() <= new Date().getTime()){
			throw new RuntimeException("考试已开始，不允许修改！");
		}
		if(exam.getStartTime().getTime() >= exam.getEndTime().getTime()){
			throw new RuntimeException("结束时间必须大于开始时间！");
		}
		
		//添加考试
		examDao.update(exam);
	}

	@Override
	public List<Map<String, Object>> getPaperAddListTypeTreeList() {
		return paperTypeService.getTreeList();
	}

	@Override
	public PageOut getPaperAddListpage(PageIn pageIn) {
		return paperService.getListpage(pageIn);
	}

	@Override
	public Paper getPaper(Integer paperId) {
		return paperService.getEntity(paperId);
	}

	@Override
	public PageOut getExamUserListpage(PageIn pageIn) {
		return examDao.getExamUserListpage(pageIn);
	}

	@Override
	public List<Map<String, Object>> getOrgTreeList() {
		return orgService.getTreeList();
	}

	@Override
	public PageOut getExamUserAddListpage(PageIn pageIn) {
		return examDao.getExamUserAddListpage(pageIn);
	}

	@Override
	public List<Map<String, Object>> getExamUserAddOrgTreeList() {
		return orgService.getTreeList();
	}

	@Override
	public void doExamUserAdd(LoginUser user, Integer id, Integer[] userIds) {
		//校验数据有效性
		if(id == null){
			throw new RuntimeException("无法获取参数：id");
		}
		if(!ValidateUtil.isValid(userIds)){
			throw new RuntimeException("无法获取参数：userIds");
		}
		Exam exam = getEntity(id);
		if(exam.getStartTime().getTime() <= new Date().getTime()){
			throw new RuntimeException("考试已开始，不允许修改！");
		}
		
		//添加考试用户
		synchronized (DO_EXAM_USER_ADD_LOCK) {
			List<Question> questionList = paperService.getQuestionList(exam.getPaperId());
			for(Integer userId : userIds){
				ExamUser examUser = new ExamUser();
				examUser.setExamId(id);
				examUser.setUserId(userId);
				examUser.setTotalScore(new BigDecimal(0));
				examUser.setState(1);
				examUser.setUpdateUserId(user.getId());
				examUser.setUpdateTime(new Date());
				examUserService.save(examUser);
				
				//添加考试用户试题
				for(Question question : questionList){
					ExamUserQuestion examUserQuestion = new ExamUserQuestion();
					examUserQuestion.setExamUserId(examUser.getId());
					examUserQuestion.setExamId(id);
					examUserQuestion.setUserId(userId);
					examUserQuestion.setQuestionId(question.getId());
					examUserQuestionService.save(examUserQuestion);
				}
			}
		}
	}

	@Override
	public void doExamUserDel(Integer[] examUserIds) {
		//校验数据有效性
		if(!ValidateUtil.isValid(examUserIds)){
			throw new RuntimeException("无法获取参数：examUserIds");
		}
		
		ExamUser examUser = examUserService.getEntity(examUserIds[0]);
		Exam exam = getEntity(examUser.getExamId());
		if(exam.getStartTime().getTime() <= new Date().getTime()){
			throw new RuntimeException("考试已开始，不允许修改！");
		}
		
		//删除考试用户试题
		synchronized (DO_EXAM_USER_ADD_LOCK) {
			for(Integer examUserId : examUserIds){
				//删除考试用户试题列表
				List<ExamUserQuestion> examUserQuestionList = examUserQuestionService.getList(examUserId);
				if(examUserQuestionList.size() > 0){
					List<Integer> examUserQuestionIdList = new ArrayList<Integer>();
					Integer[] examUserQuestionIdArr = new Integer[examUserQuestionIdList.size()];
					for(ExamUserQuestion examUserQuestion : examUserQuestionList){
						examUserQuestionIdList.add(examUserQuestion.getId());
					}
					examUserQuestionService.del(examUserQuestionIdList.toArray(examUserQuestionIdArr));
				}
				
				//删除考试用户
				examUserService.del(examUserId);
			}
		}
	}

	@Override
	public List<Map<String, Object>> getUnFinishList(Integer userId) {
		return examDao.getUnFinishList(userId);
	}

	@Override
	public List<Map<String, Object>> getExamTypeTreeList() {
		return examTypeService.getTreeList();// examDao.getExamTypeTreeList();
	}

	@Override
	public ExamType getExamType(Integer id) {
		return examDao.getExamType(id);
	}

	@Override
	public void updateAnswer(LoginUser user, Integer examUserQuestionId, String answer) {
		//校验数据有效性
		if(examUserQuestionId == null){
			throw new RuntimeException("无法获取参数：examUserQuestionId");
		}
		
//		if(!ValidateUtil.isValid(answer)){
//			throw new RuntimeException("无法获取参数：answer");
//		}//如取消勾选则为空
		
		ExamUserQuestion examUserQuestion = examUserQuestionService.getEntity(examUserQuestionId);
		if(examUserQuestion.getExamUserId() != user.getId()){
			Exam exam = getEntity(examUserQuestion.getExamId());
			throw new RuntimeException("未参与考试：" + exam.getName());
		}
		
		Exam exam = getEntity(examUserQuestion.getExamId());
		if(exam.getEndTime().getTime() <= (new Date().getTime() - 5000)){
			throw new RuntimeException("考试已结束！");
		}
		
		//更新考试用户试题信息
		examUserQuestion.setAnswer(StringUtil.delHTMLTag(HtmlUtils.htmlEscape(answer, "UTF-8")));
		examUserQuestion.setUpdateUserId(user.getId());
		examUserQuestion.setUpdateTime(new Date());
		examUserQuestionService.update(examUserQuestion);
	}

	@Override
	public void doPaper(LoginUser user, Integer examUserId) {
		//校验数据有效性
		ExamUser examUser = examUserService.getEntity(examUserId);
		if(examUser.getUserId() != user.getId()){
			Exam exam = getEntity(examUser.getExamId());
			throw new RuntimeException("未参与考试：" + exam.getName());
		}
		
		Exam exam = getEntity(examUser.getExamId());
		if(exam.getEndTime().getTime() <= (new Date().getTime() - 5000)){
			throw new RuntimeException("考试已结束！");
		}
		
		//标记为已交卷
		examUser.setUpdateTime(new Date());
		examUser.setUpdateUserId(user.getId());
		examUser.setState(3);
		examUserService.update(examUser);
	}
	
	@Override
	public void doForcePaper(LoginUser user) {
		//标记为强制交卷
		log.debug("开始强制交卷");
		examDao.doForcePaper(user);
		log.debug("成功强制交卷");
	}
	
	@Override
	public void doForcePaper() {
		//标记为强制交卷
		log.debug("开始强制交卷");
		examDao.doForcePaper(new LoginUser() {
			@Override
			public String getName() {
				return "系统管理员";
			}
			@Override
			public String getLoginName() {
				return "sysadmin";
			}
			@Override
			public Integer getId() {
				return 1;
			}
		});
		log.debug("成功强制交卷");
	}

	@Override
	public PageOut getMarkUserListpage(PageIn pageIn) {
		return examDao.getMarkUserListpage(pageIn);
	}

	@Override
	public void updateMarkScore(LoginUser user, Integer examUserQuestionId, BigDecimal score) {
		//校验数据有效性
		if(score == null){
			return;
		}
		
		ExamUserQuestion examUserQuestion = examUserQuestionService.getEntity(examUserQuestionId);
		List<MarkUser> markUserList = getMarkUserList(examUserQuestion.getExamId());
		Exam exam = getEntity(examUserQuestion.getExamId());
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
		
		PaperQuestion paperQuestion = paperQuestionService.getEntity(exam.getPaperId(), examUserQuestion.getQuestionId());
		if(BigDecimalUtil.sub(score.doubleValue(), paperQuestion.getScore().doubleValue()) > 0){
			throw new RuntimeException("最大分值：" + score);
		}
		
		//更新判卷分数
		examUserQuestion.setScore(score);
		examUserQuestion.setUpdateMarkUserId(user.getId());
		examUserQuestion.setUpdateMarkTime(new Date());
		examUserQuestionService.update(examUserQuestion);
	}

	@Override
	public void doMark(LoginUser user, Integer examUserId) {
		//校验数据有效性
		ExamUser examUser = examUserService.getEntity(examUserId);
		List<MarkUser> markUserList = getMarkUserList(examUser.getExamId());
		Exam exam = getEntity(examUser.getExamId());
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
		
		List<ExamUserQuestion> examUserQuestionList = examUserQuestionService.getList(examUserId);
		for(ExamUserQuestion examUserQuestion : examUserQuestionList){
			Question question = questionService.getEntity(examUserQuestion.getQuestionId());
			if(question.getType() == 1 || question.getType() == 4){
				PaperQuestion paperQuestion = paperQuestionService.getEntity(exam.getPaperId(), examUserQuestion.getQuestionId());
				if(question.getAnswer().equals(examUserQuestion.getAnswer())){
					examUserQuestion.setScore(paperQuestion.getScore());
				}else{
					examUserQuestion.setScore(BigDecimal.ZERO);
				}
			}
		}
		
		examDao.flush();
		
		int num = 0;
		BigDecimal totalScore = new BigDecimal(0);
		for(ExamUserQuestion examUserQuestion : examUserQuestionList){
			if(examUserQuestion.getScore() == null){
				num++;
			}else{
				totalScore = new BigDecimal(BigDecimalUtil.add(examUserQuestion.getScore().doubleValue(), totalScore.doubleValue()));
			}
		}
		
		if(num > 0){
			throw new RuntimeException("还有" + num + "道题未判！");
		}
		
		//考试用户标记为及格或不及格
		examUser.setUpdateMarkUserId(user.getId());
		examUser.setUpdateMarkTime(new Date());
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
	public void delAndUpdate(Integer[] ids) {
		//校验数据有效性
		if(!ValidateUtil.isValid(ids)){
			throw new RuntimeException("无法获取参数：ids");
		}
		
		//删除试题
		for(Integer id : ids){
			Exam exam = getEntity(id);
			exam.setState(0);
			update(exam);
		}
	}

	@Override
	public PageOut getMyExamListpage(PageIn pageIn) {
		PageOut pageOut = examDao.getMyExamListpage(pageIn);
		List<Map<String, Object>> list = pageOut.getRows();
		for(Map<String, Object> map : list){
			Date curDate = new Date();
			Date endTime = (Date) map.get("EXAM_END_TIME");
			if(endTime.getTime() > curDate.getTime()){//START：如果考试时间未结束，则显示开始考试按钮；否则显示预览试卷按钮
				map.put("START", true);
			}else{
				map.put("START", false);
			}
		}
		
		return pageOut;
	}

	@Override
	public PageOut getMarkUserAddListpage(PageIn pageIn) {
		return examDao.getMarkUserAddListpage(pageIn);
	}

	@Override
	public void doMarkUserAdd(Integer id, Integer[] userIds) {
		//校验数据有效性
		if(id == null){
			throw new RuntimeException("无法获取参数：id");
		}
		if(!ValidateUtil.isValid(userIds)){
			throw new RuntimeException("无法获取参数：userIds");
		}
		
		//添加判卷用户
		synchronized (DO_MARK_USER_ADD_LOCK) {
			for(Integer userId : userIds){
				MarkUser markUser = new MarkUser();
				markUser.setExamId(id);
				markUser.setUserId(userId);
				markUserService.save(markUser);
			}
		}
	}

	@Override
	public void doMarkUserDel(Integer[] markUserIds) {
		//校验数据有效性
		if(!ValidateUtil.isValid(markUserIds)){
			throw new RuntimeException("无法获取参数：markUserIds");
		}
		
		synchronized (DO_MARK_USER_ADD_LOCK) {
			markUserService.del(markUserIds);
		}
	}

	@Override
	public PageOut getMarkPaperListpage(PageIn pageIn) {
		PageOut pageOut = examDao.getMarkPaperListpage(pageIn);
		List<Map<String, Object>> list = pageOut.getRows();
		for(Map<String, Object> map : list){
			Date curDate = new Date();
			Date endTime = (Date) map.get("EXAM_END_TIME");
			if(endTime.getTime() <= curDate.getTime()){//START：如果考试时间未结束，则显示预览按钮；否则显示判卷按钮
				map.put("START", true);
			}else{
				map.put("START", false);
			}
		}
		return pageOut;
	}


	@Override
	public List<MarkUser> getMarkUserList(Integer id) {
		return examDao.getMarkUserList(id);
	}


	@Override
	public PageOut getGradeListpage(PageIn pageIn) {
		return examDao.getGradeListpage(pageIn);
	}
}
