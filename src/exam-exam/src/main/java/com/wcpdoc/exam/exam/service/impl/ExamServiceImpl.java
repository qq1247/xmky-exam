package com.wcpdoc.exam.exam.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.util.HtmlUtils;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.util.BigDecimalUtil;
import com.wcpdoc.exam.core.util.DateUtil;
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
import com.wcpdoc.exam.exam.entity.PaperQuestionEx;
import com.wcpdoc.exam.exam.entity.Question;
import com.wcpdoc.exam.exam.service.ExamService;
import com.wcpdoc.exam.exam.service.ExamTypeService;
import com.wcpdoc.exam.exam.service.ExamUserQuestionService;
import com.wcpdoc.exam.exam.service.ExamUserService;
import com.wcpdoc.exam.exam.service.MarkUserService;
import com.wcpdoc.exam.exam.service.PaperService;
import com.wcpdoc.exam.exam.service.PaperTypeService;
import com.wcpdoc.exam.exam.service.QuestionService;
import com.wcpdoc.exam.sys.cache.DictCache;
import com.wcpdoc.exam.sys.entity.Org;
import com.wcpdoc.exam.sys.entity.Post;
import com.wcpdoc.exam.sys.service.OrgService;
import com.wcpdoc.exam.sys.service.UserService;

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
	
	private static final Object DO_EXAM_USER_ADD_LOCK = new Object();
	private static final Object DO_MARK_USER_ADD_LOCK = new Object();

	@Override
	@Resource(name = "examDaoImpl")
	public void setDao(BaseDao<Exam> dao) {
		super.dao = dao;
	}
	

	@Override
	public void saveAndUpdate(Exam exam, LoginUser user) {
		//校验数据有效性
		if(exam.getStartTime().getTime() <= new Date().getTime()){
			throw new RuntimeException("考试开始时间必须大于当前时间！");
		}
		if(exam.getStartTime().getTime() >= exam.getEndTime().getTime()){
			throw new RuntimeException("考试结束时间必须大于考试开始时间！");
		}
		if(exam.getMarkStartTime().getTime() <= exam.getEndTime().getTime()){
			throw new RuntimeException("判卷开始时间必须大于考试结束时间！");
		}
		if(exam.getMarkStartTime().getTime() >= exam.getMarkEndTime().getTime()){
			throw new RuntimeException("判卷结束时间必须大于判卷开始时间！");
		}
		
		
		//添加考试
		exam.setUpdateTime(new Date());
		exam.setUpdateUserId(user.getId());
		exam.setState(2);
		save(exam);
	}
	
	@Override
	public void updateAndUpdate(Exam exam, LoginUser user) {
		//校验数据有效性
		if(exam.getStartTime().getTime() <= new Date().getTime()){
			throw new RuntimeException("考试开始时间必须大于当前时间！");
		}
		if(exam.getStartTime().getTime() >= exam.getEndTime().getTime()){
			throw new RuntimeException("考试结束时间必须大于考试开始时间！");
		}
		if(exam.getMarkStartTime().getTime() <= exam.getEndTime().getTime()){
			throw new RuntimeException("判卷开始时间必须大于考试结束时间！");
		}
		if(exam.getMarkStartTime().getTime() >= exam.getMarkEndTime().getTime()){
			throw new RuntimeException("判卷结束时间必须大于判卷开始时间！");
		}
		Exam entity = getEntity(exam.getId());
		if(entity.getState() == 1){
			throw new RuntimeException("考试已发布！");
		}
		
		//添加考试
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(user.getId());
		entity.setName(exam.getName());
		entity.setPassScore(exam.getPassScore());
//		entity.setState(exam.getState());
		entity.setStartTime(exam.getStartTime());
		entity.setEndTime(exam.getEndTime());
		entity.setMarkStartTime(exam.getMarkStartTime());
		entity.setMarkEndTime(exam.getMarkEndTime());
		entity.setDescription(exam.getDescription());
		entity.setPaperId(exam.getPaperId());
		entity.setExamTypeId(exam.getExamTypeId());
		update(entity);
	}

	@Override
	public List<Map<String, Object>> getPaperAddListTypeTreeList() {
		return paperTypeService.getTreeList();
	}

	@Override
	public PageOut getPaperListpage(PageIn pageIn) {
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
		if(exam.getState() == 0){
			throw new RuntimeException("考试已删除！");
		}
		if(exam.getState() == 2){
			throw new RuntimeException("考试未发布！");
		}
		
		if(exam.getEndTime().getTime() <= new Date().getTime()){
			throw new RuntimeException("考试已结束，不允许添加！");
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
		if(exam.getState() == 0){
			throw new RuntimeException("考试已删除！");
		}
		if(exam.getState() == 2){
			throw new RuntimeException("考试未发布！");
		}
		
		if(exam.getEndTime().getTime() <= new Date().getTime()){
			throw new RuntimeException("考试已结束，不允许删除！");
		}
		if(exam.getStartTime().getTime() <= new Date().getTime()){
			throw new RuntimeException("考试已开始，不允许删除！");
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
	public List<Map<String, Object>> getExamTypeTreeList(Integer userId) {
		Org org = userService.getOrg(userId);
		List<Post> postList = userService.getPostList(userId);
		List<ExamType> examTypeList = examTypeService.getList();
		List<Map<String, Object>> examTypeTreeList = new ArrayList<Map<String,Object>>();
		
		for(ExamType examType : examTypeList){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("ID", examType.getId());
			map.put("NAME", examType.getName());
			map.put("PARENT_ID", examType.getParentId());
			//map.put("DISABLED", true);
			//map.put("EXPANDED", true);
			
			if(userId == 1){
				examTypeTreeList.add(map);
				continue;
			}
			
			if(examType.getUserIds() != null 
					&& examType.getUserIds().contains(userId.toString())){//有用户权限
				examTypeTreeList.add(map);
				continue;
			}
			if(examType.getOrgIds() != null 
					&& examType.getOrgIds().contains(org.getId().toString())){//有机构权限
				examTypeTreeList.add(map);
				continue;
			}
			
			for(Post post : postList){
				if(examType.getPostIds() != null 
						&& examType.getPostIds().contains(post.getId().toString())){//有岗位权限
					examTypeTreeList.add(map);
					break;
				}
			}
		}
		
		return examTypeTreeList;
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
		if(examUserQuestion.getUserId() != user.getId()){
			throw new RuntimeException("未参与考试！");
		}
		
		Exam exam = getEntity(examUserQuestion.getExamId());
		if(exam.getState() == 0){
			throw new RuntimeException("考试已删除！");
		}
		if(exam.getState() == 2){
			throw new RuntimeException("考试未发布！");
		}
		if(exam.getStartTime().getTime() > (new Date().getTime())){
			throw new RuntimeException("考试未开始！");
		}
		if(exam.getEndTime().getTime() < (new Date().getTime() - 5000)){
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
		if(exam.getState() == 0){
			throw new RuntimeException("考试已删除！");
		}
		if(exam.getState() == 2){
			throw new RuntimeException("考试未发布！");
		}
		if(exam.getStartTime().getTime() > (new Date().getTime())){
			throw new RuntimeException("考试未开始！");
		}
		if(exam.getEndTime().getTime() < (new Date().getTime() - 5000)){
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
		ExamUserQuestion examUserQuestion = examUserQuestionService.getEntity(examUserQuestionId);
		List<MarkUser> markUserList = getMarkUserList(examUserQuestion.getExamId());
		Exam exam = getEntity(examUserQuestion.getExamId());
		if(exam.getState() == 0){
			throw new RuntimeException("考试已删除！");
		}
		if(exam.getState() == 2){
			throw new RuntimeException("考试未发布！");
		}
		if(exam.getMarkStartTime().getTime() > (new Date().getTime())){
			throw new RuntimeException("判卷未开始！");
		}
		if(exam.getMarkEndTime().getTime() < (new Date().getTime() - 5000)){
			throw new RuntimeException("判卷已结束！");
		}
		
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
		
		if(score != null){
			PaperQuestion paperQuestion = paperService.getPaperQuestion(exam.getPaperId(), examUserQuestion.getQuestionId());
			if(new BigDecimal(BigDecimalUtil.sub(score.toString(), paperQuestion.getScore().toString())).doubleValue() > 0){
				throw new RuntimeException("最大分值：" + paperQuestion.getScore());
			}
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
		if(exam.getState() == 0){
			throw new RuntimeException("考试已删除！");
		}
		if(exam.getState() == 2){
			throw new RuntimeException("考试未发布！");
		}
		if(exam.getMarkStartTime().getTime() > (new Date().getTime())){
			throw new RuntimeException("判卷未开始！");
		}
		if(exam.getMarkEndTime().getTime() < (new Date().getTime() - 5000)){
			throw new RuntimeException("判卷已结束！");
		}
		
		boolean ok = false;
		for(MarkUser markUser : markUserList){
			if(markUser.getUserId() == user.getId()){
				ok = true;
				break;
			}
		}
		if(!ok){
			throw new RuntimeException("未参与判卷：" + exam.getName());
		}
		
		List<ExamUserQuestion> examUserQuestionList = examUserQuestionService.getList(examUserId);
		int num = 0;
		BigDecimal totalScore = new BigDecimal(0);
		for(ExamUserQuestion examUserQuestion : examUserQuestionList){
			if(examUserQuestion.getScore() == null){
				num++;
			}else{
				totalScore = new BigDecimal(BigDecimalUtil.add(examUserQuestion.getScore().toString(), totalScore.toString()));
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
//		Date curTime = new Date();
		for(Integer id : ids){
			Exam exam = getEntity(id);
//			if(exam.getStartTime().getTime() >= curTime.getTime()
//					&& exam.getEndTime().getTime() <= curTime.getTime()){
//				throw new RuntimeException("【"+exam.getName()+"】考试未结束");
//			}
			
			exam.setState(0);
			update(exam);
		}
	}

	@Override
	@Deprecated
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
		Exam exam = getEntity(id);
		if(exam.getState() == 0){
			throw new RuntimeException("考试已删除！");
		}
		if(exam.getState() == 2){
			throw new RuntimeException("考试未发布！");
		}
		
		if(exam.getMarkEndTime().getTime() <= new Date().getTime()){
			throw new RuntimeException("判卷已结束，不允许添加！");
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
		MarkUser markUser = markUserService.getEntity(markUserIds[0]);
		Exam exam = getEntity(markUser.getExamId());
		if(exam.getState() == 0){
			throw new RuntimeException("考试已删除！");
		}
		if(exam.getState() == 2){
			throw new RuntimeException("考试未发布！");
		}
		
		if(exam.getMarkEndTime().getTime() <= new Date().getTime()){
			throw new RuntimeException("判卷已结束，不允许删除！");
		}
		if(exam.getMarkStartTime().getTime() <= new Date().getTime()){
			throw new RuntimeException("判卷已开始，不允许删除！");
		}
		
		synchronized (DO_MARK_USER_ADD_LOCK) {
			markUserService.del(markUserIds);
		}
	}

	@Override
	public PageOut getMarkListpage(PageIn pageIn) {
		return examDao.getMarkListpage(pageIn);
	}


	@Override
	public List<MarkUser> getMarkUserList(Integer id) {
		return examDao.getMarkUserList(id);
	}


	@Override
	public PageOut getGradeListpage(PageIn pageIn) {
		return examDao.getGradeListpage(pageIn);
	}


	@Override
	public ExamType getExamType2(Integer examTypeId) {
		return examTypeService.getEntity(examTypeId);
	}


	@Override
	public List<Map<String, Object>> getPaperTypeTreeList(Integer userId) {
		return paperService.getPaperTypeTreeList(userId);
	}

	@Override
	public void toPaper(Model model, LoginUser user, Integer examId) {
		//校验数据有效性
		ExamUser examUser = examUserService.getEntity(examId, user.getId());
		if(examUser == null){
			Exam exam = getEntity(examId);
			throw new RuntimeException("未参与考试：" + exam.getName());
		}
		
		Exam exam = getEntity(examId);
		if(exam.getState() == 0){
			throw new RuntimeException("考试已删除！");
		}
		if(exam.getState() == 2){
			throw new RuntimeException("考试未发布！");
		}
		if(exam.getStartTime().getTime() > (new Date().getTime())){
			throw new RuntimeException("考试未开始！");
		}
		if(exam.getEndTime().getTime() < (new Date().getTime() - 5000)){
			throw new RuntimeException("考试已结束！");
		}
		
		//考试信息
		model.addAttribute("exam", exam);
		model.addAttribute("startTime", DateUtil.getFormatDateTime(exam.getStartTime()));
		model.addAttribute("endTime", DateUtil.getFormatDateTime(exam.getEndTime()));
		model.addAttribute("sysTime", DateUtil.getFormatDateTime());
		
		//试卷信息
		Paper paper = getPaper(exam.getPaperId());
		model.addAttribute("paper", paper);
		
		//试题信息
		List<PaperQuestionEx> paperQuestionExList = paperService.getPaperList(exam.getPaperId());
		model.addAttribute("paperQuestionExList", paperQuestionExList);
		
		//用户已做答案信息
		List<ExamUserQuestion> examUserQuestionList = examUserQuestionService.getList(exam.getId(), examUser.getUserId());
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
		examUserService.update(examUser);
		model.addAttribute("examUser", examUser);
	}
	
	@Override
	public void toMark(Model model, LoginUser user, Integer examUserId) {
		//校验数据有效性
		ExamUser examUser = examUserService.getEntity(examUserId);
		Exam exam = getEntity(examUser.getExamId());
		List<MarkUser> markUserList = getMarkUserList(examUser.getExamId());
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
		
		Date curTime = new Date();
		if(exam.getState() == 0){
			throw new RuntimeException("考试已删除！");
		}
		if(exam.getState() == 2){
			throw new RuntimeException("考试未发布！");
		}
		if(exam.getMarkStartTime().getTime() > curTime.getTime()){
			throw new RuntimeException("判卷未开始！");
		}
		if(exam.getMarkEndTime().getTime() < curTime.getTime()){
			throw new RuntimeException("判卷已结束！");
		}
		
		//用户信息
		model.addAttribute("examUserId", examUserId);
		model.addAttribute("user", userService.getEntity(examUser.getUserId()));
		
		//考试信息
		model.addAttribute("exam", exam);
		model.addAttribute("startTime", DateUtil.getFormatDateTime(exam.getStartTime()));
		model.addAttribute("endTime", DateUtil.getFormatDateTime(exam.getEndTime()));
		model.addAttribute("markEndTime", DateUtil.getFormatDateTime(exam.getMarkEndTime()));
		model.addAttribute("sysTime", DateUtil.getFormatDateTime());
		
		//试卷信息
		Paper paper = getPaper(exam.getPaperId());
		model.addAttribute("paper", paper);
		
		//试题信息
		List<PaperQuestionEx> paperQuestionExList = paperService.getPaperList(exam.getPaperId());
		model.addAttribute("paperQuestionExList", paperQuestionExList);
		
		//用户已做答案信息
		List<ExamUserQuestion> examUserQuestionList = examUserQuestionService.getList(exam.getId(), examUser.getUserId());
		model.addAttribute("examUserQuestionList", examUserQuestionList);
		Map<Long, ExamUserQuestion> examUserQuestionMap = new HashMap<Long, ExamUserQuestion>();
		for(ExamUserQuestion examUserQuestion : examUserQuestionList){
			examUserQuestionMap.put(examUserQuestion.getQuestionId().longValue(), examUserQuestion);
		}
		model.addAttribute("examUserQuestionMap", examUserQuestionMap);
		model.addAttribute("questionOptions", DictCache.getIndexDictlistMap().get("QUESTION_OPTIONS"));
		
		//如果是第一次，智能判卷
		if(examUser.getState() < 5){//1：未考试；2：考试中；3：已交卷；4：强制交卷；5：判卷中；6：已判卷；7：及格；8：不及格
			for(PaperQuestionEx parent : paperQuestionExList){
				for(PaperQuestionEx sub : parent.getSubList()){	
					//过滤掉问答题
					if(sub.getQuestion().getType() >= 5){//1：单选；2：多选；3：填空；4：判断；5：问答
						continue;
					}
					//设置默认0分
					Question question = sub.getQuestion();
					ExamUserQuestion examUserQuestion = examUserQuestionMap.get(sub.getQuestion().getId() + 0L);
					examUserQuestion.setScore(BigDecimal.ZERO);
					
					//如果是单选或判断，全对得分
					if(question.getType() == 1 || question.getType() == 4){
						if(!ValidateUtil.isValid(examUserQuestion.getAnswer())){
							continue;
						}

						if(question.getAnswer().equals(examUserQuestion.getAnswer())){
							examUserQuestion.setScore(sub.getScore());
						}
						continue;
					}
					
					//如果是多选，1：半对半分（默认全对得分）
					String options = sub.getOptions();
					if(question.getType() == 2){
						if(!ValidateUtil.isValid(examUserQuestion.getAnswer())){
							continue;
						}
						Set<String> questionAnswerSet = new HashSet<String>(Arrays.asList(question.getAnswer().split(",")));
						Set<String> userAnswerSet = new HashSet<String>(Arrays.asList(examUserQuestion.getAnswer().split(",")));
						if(questionAnswerSet.size() == userAnswerSet.size() && questionAnswerSet.containsAll(userAnswerSet)){
							examUserQuestion.setScore(sub.getScore());
							continue;
						}
						
						if(bdbf(options)){
							if(questionAnswerSet.containsAll(userAnswerSet)){
								examUserQuestion.setScore(new BigDecimal(BigDecimalUtil.div(sub.getScore().toString(), "2", 2)));
							}
						}
						continue;
					}
					
					//如果是填空，1：半对半分（默认全对得分）；2：答案无顺序（默认答案有前后顺序）；
					//3：大小写不敏感（默认大小写敏感）；4：用户答案包含试题答案（默认等于答案得分）
					if(question.getType() == 3){
						if(!ValidateUtil.isValid(examUserQuestion.getAnswer())){
							continue;
						}

						String questionAnswerStr = question.getAnswer();
						String userAnswerStr = examUserQuestion.getAnswer();
						if(dxxbmg(options)){//处理大小写敏感
							questionAnswerStr = questionAnswerStr.toUpperCase();
							userAnswerStr = userAnswerStr.toUpperCase();
						}
						
						String[] questionAnswerArr = questionAnswerStr.split("\n");//试题答案：一般|||通常|||普遍\njava|||.net
						String[] userAnswerStrArr = userAnswerStr.split("\n");//用户答案：一般情况下\nJava
						boolean[] userFillBlanksArr = new boolean[questionAnswerArr.length];//保存用户每个填空是否正确
						int trueNum = 0;
						
						for(int i = 0; i < questionAnswerArr.length; i++){//答案对比
							String[] qaArr = questionAnswerArr[i].split("\\|\\|\\|");
							for(int j = 0; j < userAnswerStrArr.length; j++){
								if(!dawsx(options)){
									if(i != j){//如果答案有前后顺序，则对应位置对比
										continue;
									}
								}
								
								for(String str : qaArr){//用户答案和试题答案对比
									if(userAnswerStrArr[j].equals(str)){//默认等于答案得分，userAnswerStrArr[j].equals(str)位置不要反
										userFillBlanksArr[i] = true;
										break;
									}
									
									if(bhdadf(options)){
										if(userAnswerStrArr[j].contains(str)){//用户答案包含试题答案
											userFillBlanksArr[i] = true;
											break;
										}
									}
								}
							}
						}
						
						for(boolean b : userFillBlanksArr){
							if(b){
								trueNum++;
							}
						}
						
						if(userFillBlanksArr.length == trueNum){//默认全对得分
							examUserQuestion.setScore(sub.getScore());
							continue;
						}
						
						if(bdbf(options)){
							if(trueNum > 0){//半对半分
								examUserQuestion.setScore(new BigDecimal(BigDecimalUtil.div(sub.getScore().toString(), "2", 2)));
							}
						}
						continue;
					}
				}
			}
		}
		
		//标记为判卷中
		examUser.setState(5);
		examUser.setUpdateMarkUserId(user.getId());
		examUser.setUpdateMarkTime(new Date());
		examUserService.update(examUser);
		model.addAttribute("examUser", examUser);
	}
	
	/**
	 * 4：用户答案包含试题答案（默认等于答案得分）
	 * 
	 * v1.0 zhanghc 2018年11月14日下午11:05:51
	 * @param options
	 * @return boolean
	 */
	private boolean bhdadf(String options) {
		return ValidateUtil.isValid(options) && options.contains("4");
	}

	/**
	 * 3：大小写不敏感（默认大小写敏感）；
	 * 
	 * v1.0 zhanghc 2018年11月14日下午11:07:02
	 * @param options
	 * @return boolean
	 */
	private boolean dxxbmg(String options) {
		return ValidateUtil.isValid(options) && options.contains("3");
	}

	/**
	 * 2：答案无顺序（默认答案有前后顺序）
	 * 
	 * v1.0 zhanghc 2018年11月14日下午11:06:24
	 * @param options
	 * @return boolean
	 */
	private boolean dawsx(String options) {
		return ValidateUtil.isValid(options) && options.contains("2");
	}

	/**
	 * 1：半对半分（默认全对得分）
	 * 
	 * v1.0 zhanghc 2018年11月14日下午11:07:10
	 * @param options
	 * @return boolean
	 */
	private boolean bdbf(String options) {
		return ValidateUtil.isValid(options) && options.contains("1");
	}


	@Override
	public ExamUser getExamUser(Integer examId, Integer userId) {
		return examUserService.getEntity(examId, userId);
	}


	@Override
	public void doPublish(Integer id) {
		Exam exam = getEntity(id);
		if(exam.getState() == 0){
			throw new RuntimeException("考试【"+exam.getName()+"】已删除！");
		}
		if(exam.getState() == 1){
			throw new RuntimeException("考试【"+exam.getName()+"】已发布！");
		}
		
		exam.setState(1);
		update(exam);
	}
}
