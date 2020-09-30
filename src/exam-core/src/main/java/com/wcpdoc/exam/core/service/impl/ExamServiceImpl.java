package com.wcpdoc.exam.core.service.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.util.HtmlUtils;

import com.wcpdoc.exam.base.cache.DictCache;
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
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperQuestionEx;
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
import com.wcpdoc.exam.core.util.StringUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;

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
		Set<Integer> userIdsSet = new HashSet<>(Arrays.asList(userIds));
		ListIterator<ExamUser> examUserListIterator = examUserList.listIterator();
		while (examUserListIterator.hasNext()) {//共同的剔除
			ExamUser next = examUserListIterator.next();
			if (userIdsSet.contains(next.getId())) {
				examUserListIterator.remove();
				userIdsSet.remove(next.getId());
			}
		}
		examUserListIterator = examUserList.listIterator();
		while (examUserListIterator.hasNext()) {//多余的删除
			ExamUser next = examUserListIterator.next();
			examUserService.del(next.getId());
		}
		for (Integer userId : userIdsSet) {//没有的添加
			ExamUser examUser = new ExamUser();
			examUser.setExamId(id);
			examUser.setUserId(userId);
			examUser.setAnswerTime(null);
			examUser.setMarkUserId(null);
			examUser.setMarkUserTime(null);
			examUser.setTotalScore(BigDecimal.ZERO);
			examUser.setState(1);
			examUser.setMarkState(1);
			examUser.setAnswerState(null);
			examUserService.add(examUser);
		}
		
		// 添加判卷用户
		List<MarkUser> markUserList = markUserService.getList(id);
		Set<Integer> markUserIdsSet = new HashSet<>(Arrays.asList(markUserIds));
		ListIterator<MarkUser> markUserListIterator = markUserList.listIterator();
		while (markUserListIterator.hasNext()) {//共同的剔除
			MarkUser next = markUserListIterator.next();
			if (markUserIdsSet.contains(next.getId())) {
				markUserListIterator.remove();
				markUserIdsSet.remove(next.getId());
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
	public void updateAnswer(LoginUser user, Integer examUserQuestionId, String answer) {
		//校验数据有效性
		if(examUserQuestionId == null){
			throw new MyException("参数错误：examUserQuestionId");
		}
		
//		if(!ValidateUtil.isValid(answer)){
//			throw new MyException("参数错误：answer");
//		}//如取消勾选则为空
		
		ExamUserQuestion examUserQuestion = examUserQuestionService.getEntity(examUserQuestionId);
		if(examUserQuestion.getUserId() != user.getId()){
			throw new MyException("未参与考试！");
		}
		
		Exam exam = getEntity(examUserQuestion.getExamId());
		if(exam.getState() == 0){
			throw new MyException("考试已删除！");
		}
		if(exam.getState() == 2){
			throw new MyException("考试未发布！");
		}
		if(exam.getStartTime().getTime() > (new Date().getTime())){
			throw new MyException("考试未开始！");
		}
		if(exam.getEndTime().getTime() < (new Date().getTime() - 5000)){
			throw new MyException("考试已结束！");
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
			throw new MyException("未参与考试：" + exam.getName());
		}
		
		Exam exam = getEntity(examUser.getExamId());
		if(exam.getState() == 0){
			throw new MyException("考试已删除！");
		}
		if(exam.getState() == 2){
			throw new MyException("考试未发布！");
		}
		if(exam.getStartTime().getTime() > (new Date().getTime())){
			throw new MyException("考试未开始！");
		}
		if(exam.getEndTime().getTime() < (new Date().getTime() - 5000)){
			throw new MyException("考试已结束！");
		}
		
		//标记为已交卷
//		examUser.setUpdateTime(new Date());
//		examUser.setUpdateUserId(user.getId());
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
			throw new MyException("判卷未开始！");
		}
		if(exam.getMarkEndTime().getTime() < (new Date().getTime() - 5000)){
			throw new MyException("判卷已结束！");
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
		List<MarkUser> markUserList = null;//getMarkUserList(examUser.getExamId());
		Exam exam = getEntity(examUser.getExamId());
		if(exam.getState() == 0){
			throw new MyException("考试已删除！");
		}
		if(exam.getState() == 2){
			throw new MyException("考试未发布！");
		}
		if(exam.getMarkStartTime().getTime() > (new Date().getTime())){
			throw new MyException("判卷未开始！");
		}
		if(exam.getMarkEndTime().getTime() < (new Date().getTime() - 5000)){
			throw new MyException("判卷已结束！");
		}
		
		boolean ok = false;
		for(MarkUser markUser : markUserList){
			if(markUser.getUserId() == user.getId()){
				ok = true;
				break;
			}
		}
		if(!ok){
			throw new MyException("未参与判卷：" + exam.getName());
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

	@Override
	public void toPaper(Model model, LoginUser user, Integer examId) {
		//校验数据有效性
		ExamUser examUser = examUserService.getEntity(examId, user.getId());
		if(examUser == null){
			Exam exam = getEntity(examId);
			throw new MyException("未参与考试：" + exam.getName());
		}
		
		Exam exam = getEntity(examId);
		if(exam.getState() == 0){
			throw new MyException("考试已删除！");
		}
		if(exam.getState() == 2){
			throw new MyException("考试未发布！");
		}
		if(exam.getStartTime().getTime() > (new Date().getTime())){
			throw new MyException("考试未开始！");
		}
		if(exam.getEndTime().getTime() < (new Date().getTime() - 5000)){
			throw new MyException("考试已结束！");
		}
		
		//考试信息
		model.addAttribute("exam", exam);
//		model.addAttribute("startTime", DateUtil.getFormatDateTime(exam.getStartTime()));
//		model.addAttribute("endTime", DateUtil.getFormatDateTime(exam.getEndTime()));
//		model.addAttribute("sysTime", DateUtil.getFormatDateTime());
		
		//试卷信息
		Paper paper = paperService.getEntity(exam.getPaperId());
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
//		examUser.setUpdateUserId(user.getId());
//		examUser.setUpdateTime(new Date());
		examUserService.update(examUser);
		model.addAttribute("examUser", examUser);
	}
	
	@Override
	public void toMark(Model model, LoginUser user, Integer examUserId) {
		//校验数据有效性
		ExamUser examUser = examUserService.getEntity(examUserId);
		Exam exam = getEntity(examUser.getExamId());
		List<MarkUser> markUserList = null;//getMarkUserList(examUser.getExamId());
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
		
		Date curTime = new Date();
		if(exam.getState() == 0){
			throw new MyException("考试已删除！");
		}
		if(exam.getState() == 2){
			throw new MyException("考试未发布！");
		}
		if(exam.getMarkStartTime().getTime() > curTime.getTime()){
			throw new MyException("判卷未开始！");
		}
		if(exam.getMarkEndTime().getTime() < curTime.getTime()){
			throw new MyException("判卷已结束！");
		}
		
		//用户信息
		model.addAttribute("examUserId", examUserId);
		model.addAttribute("user", userService.getEntity(examUser.getUserId()));
		
		//考试信息
		model.addAttribute("exam", exam);
//		model.addAttribute("startTime", DateUtil.getFormatDateTime(exam.getStartTime()));
//		model.addAttribute("endTime", DateUtil.getFormatDateTime(exam.getEndTime()));
//		model.addAttribute("markEndTime", DateUtil.getFormatDateTime(exam.getMarkEndTime()));
//		model.addAttribute("sysTime", DateUtil.getFormatDateTime());
		
		//试卷信息
		Paper paper = paperService.getEntity(exam.getPaperId());
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
//					String options = sub.getOptions();
//					if(question.getType() == 2){
//						if(!ValidateUtil.isValid(examUserQuestion.getAnswer())){
//							continue;
//						}
//						Set<String> questionAnswerSet = new HashSet<String>(Arrays.asList(question.getAnswer().split(",")));
//						Set<String> userAnswerSet = new HashSet<String>(Arrays.asList(examUserQuestion.getAnswer().split(",")));
//						if(questionAnswerSet.size() == userAnswerSet.size() && questionAnswerSet.containsAll(userAnswerSet)){
//							examUserQuestion.setScore(sub.getScore());
//							continue;
//						}
//						
//						if(bdbf(options)){
//							if(questionAnswerSet.containsAll(userAnswerSet)){
//								examUserQuestion.setScore(new BigDecimal(BigDecimalUtil.div(sub.getScore().toString(), "2", 2)));
//							}
//						}
//						continue;
//					}
					
					//如果是填空，1：半对半分（默认全对得分）；2：答案无顺序（默认答案有前后顺序）；
					//3：大小写不敏感（默认大小写敏感）；4：用户答案包含试题答案（默认等于答案得分）
					if(question.getType() == 3){
						if(!ValidateUtil.isValid(examUserQuestion.getAnswer())){
							continue;
						}

						String questionAnswerStr = question.getAnswer();
						String userAnswerStr = examUserQuestion.getAnswer();
//						if(dxxbmg(options)){//处理大小写敏感
//							questionAnswerStr = questionAnswerStr.toUpperCase();
//							userAnswerStr = userAnswerStr.toUpperCase();
//						}
//						
//						String[] questionAnswerArr = questionAnswerStr.split("\n");//试题答案：一般|||通常|||普遍\njava|||.net
//						String[] userAnswerStrArr = userAnswerStr.split("\n");//用户答案：一般情况下\nJava
//						boolean[] userFillBlanksArr = new boolean[questionAnswerArr.length];//添加用户每个填空是否正确
//						int trueNum = 0;
//						
//						for(int i = 0; i < questionAnswerArr.length; i++){//答案对比
//							String[] qaArr = questionAnswerArr[i].split("\\|\\|\\|");
//							for(int j = 0; j < userAnswerStrArr.length; j++){
//								if(!dawsx(options)){
//									if(i != j){//如果答案有前后顺序，则对应位置对比
//										continue;
//									}
//								}
//								
//								for(String str : qaArr){//用户答案和试题答案对比
//									if(userAnswerStrArr[j].equals(str)){//默认等于答案得分，userAnswerStrArr[j].equals(str)位置不要反
//										userFillBlanksArr[i] = true;
//										break;
//									}
//									
//									if(bhdadf(options)){
//										if(userAnswerStrArr[j].contains(str)){//用户答案包含试题答案
//											userFillBlanksArr[i] = true;
//											break;
//										}
//									}
//								}
//							}
//						}
						
//						for(boolean b : userFillBlanksArr){
//							if(b){
//								trueNum++;
//							}
//						}
//						
//						if(userFillBlanksArr.length == trueNum){//默认全对得分
//							examUserQuestion.setScore(sub.getScore());
//							continue;
//						}
//						
//						if(bdbf(options)){
//							if(trueNum > 0){//半对半分
//								examUserQuestion.setScore(new BigDecimal(BigDecimalUtil.div(sub.getScore().toString(), "2", 2)));
//							}
//						}
//						continue;
					}
				}
			}
		}
		
		//标记为判卷中
		examUser.setState(5);
//		examUser.setUpdateMarkUserId(user.getId());
//		examUser.setUpdateMarkTime(new Date());
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
}
