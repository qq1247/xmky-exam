package com.wcpdoc.exam.core.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.base.service.OrgService;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.cache.AutoExamCache;
import com.wcpdoc.exam.core.cache.AutoMarkCache;
import com.wcpdoc.exam.core.dao.ExamDao;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyExamDetail;
import com.wcpdoc.exam.core.entity.MyMark;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.ExamTypeService;
import com.wcpdoc.exam.core.service.MyExamDetailService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyMarkService;
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
	private MyExamService myExamService;
	@Resource
	private MyExamDetailService myExamDetailService;
	@Resource
	private MyMarkService myMarkService;
	@Resource
	private UserService userService;
	
	@Override
	@Resource(name = "examDaoImpl")
	public void setDao(BaseDao<Exam> dao) {
		super.dao = dao;
	}

	@Override
	public void addAndUpdate(Exam exam) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(exam.getName())) {
			throw new MyException("参数错误：name");
		}
		if (!ValidateUtil.isValid(exam.getPaperId())) {
			throw new MyException("参数错误：paperId");
		}
		if (!ValidateUtil.isValid(exam.getStartTime())) {
			throw new MyException("参数错误：startTime");
		}
		if (!ValidateUtil.isValid(exam.getEndTime())) {
			throw new MyException("参数错误：endTime");
		}
		if (exam.getStartTime().getTime() <= new Date().getTime()) {
			throw new MyException("考试开始时间必须大于当前时间");
		}
		if (exam.getStartTime().getTime() >= exam.getEndTime().getTime()) {
			throw new MyException("考试结束时间必须大于考试开始时间");
		}

		Paper paper = paperService.getEntity(exam.getPaperId());
		if (paper.getState() != 1) {
			throw new MyException("试卷未发布");
		}
		if (paper.getMarkType() == 2) {// 如果是自动阅卷类型，没有阅卷开始时间和阅卷结束时间
			if (!ValidateUtil.isValid(exam.getMarkStartTime())) {
				throw new MyException("参数错误：markStartTime");
			}
			if (!ValidateUtil.isValid(exam.getMarkEndTime())) {
				throw new MyException("参数错误：markEndTime");
			}
			if (exam.getMarkStartTime().getTime() <= exam.getEndTime().getTime()) {
				throw new MyException("阅卷开始时间必须大于考试结束时间");
			}
			if (exam.getMarkStartTime().getTime() >= exam.getMarkEndTime().getTime()) {
				throw new MyException("阅卷结束时间必须大于阅卷开始时间");
			}
		}

		// 添加考试
		exam.setCreateUserId(getCurUser().getId());
		exam.setCreateTime(new Date());
		exam.setUpdateUserId(getCurUser().getId());
		exam.setUpdateTime(new Date());
		exam.setState(2);// 草稿
		exam.setMarkState(1);// 标记为未阅卷（考试时间结束，定时任务自动阅卷，标记为已阅）
		exam.setMarkStartTime(paper.getMarkType() == 1 ? null : exam.getMarkStartTime());
		exam.setMarkEndTime(paper.getMarkType() == 1 ? null : exam.getMarkEndTime());
		add(exam);
	}
	
	@Override
	public void updateAndUpdate(Exam exam) {
		//校验数据有效性
		if (!ValidateUtil.isValid(exam.getName())) {
			throw new MyException("参数错误：name");
		}
		if (!ValidateUtil.isValid(exam.getPaperId())) {
			throw new MyException("参数错误：paperId");
		}
		if (!ValidateUtil.isValid(exam.getStartTime())) {
			throw new MyException("参数错误：startTime");
		}
		if (!ValidateUtil.isValid(exam.getEndTime())) {
			throw new MyException("参数错误：endTime");
		}
		if (exam.getStartTime().getTime() <= new Date().getTime()) {
			throw new MyException("考试开始时间必须大于当前时间");
		}
		if (exam.getStartTime().getTime() >= exam.getEndTime().getTime()) {
			throw new MyException("考试结束时间必须大于考试开始时间");
		}

		Paper paper = paperService.getEntity(exam.getPaperId());
		if (paper.getMarkType() == 2) {// 如果是自动阅卷类型，没有阅卷开始时间和阅卷结束时间
			if (!ValidateUtil.isValid(exam.getMarkStartTime())) {
				throw new MyException("参数错误：markStartTime");
			}
			if (!ValidateUtil.isValid(exam.getMarkEndTime())) {
				throw new MyException("参数错误：markEndTime");
			}
			if (exam.getMarkStartTime().getTime() <= exam.getEndTime().getTime()) {
				throw new MyException("阅卷开始时间必须大于考试结束时间");
			}
			if (exam.getMarkStartTime().getTime() >= exam.getMarkEndTime().getTime()) {
				throw new MyException("阅卷结束时间必须大于阅卷开始时间");
			}
		}
		
		Exam entity = getEntity(exam.getId());
		if(entity.getState() == 0) {
			throw new MyException("已删除");
		}
		if(entity.getState() == 1) {
			throw new MyException("已发布");
		}
		if(entity.getState() == 3) {
			throw new MyException("已归档");
		}
		
		//添加考试
		entity.setName(exam.getName());
		entity.setStartTime(exam.getStartTime());
		entity.setEndTime(exam.getEndTime());
		entity.setMarkStartTime(paper.getMarkType() == 1 ? null : exam.getMarkStartTime());
		entity.setMarkEndTime(paper.getMarkType() == 1 ? null : exam.getMarkEndTime());
		//exam.setMarkState(1);// 不处理
		//entity.setScoreState(exam.getScoreState());
		//entity.setRankState(exam.getRankState());
		//entity.setLoginType(exam.getLoginType());
		//entity.setDescription(exam.getDescription());
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		//entity.setState(null);
		entity.setPaperId(exam.getPaperId());
		//exam.setExamTypeId(null);// 分类不变
		update(entity);
	}
	
	@Override
	public void delAndUpdate(Integer id) {
		Date curTime = new Date();
		Exam exam = examDao.getEntity(id);
		if(exam.getStartTime().getTime() >= curTime.getTime()
				&& exam.getEndTime().getTime() <= curTime.getTime()) {
			throw new MyException("考试未结束");
		}
		
		exam.setState(0);
		exam.setUpdateTime(new Date());
		exam.setUpdateUserId(getCurUser().getId());
		update(exam);
	}
	
	@Override
	public void publish(Integer id) {
		// 校验数据有效性
		Exam exam = examDao.getEntity(id);
		if(exam.getState() == 0) {
			throw new MyException("考试已删除");
		}
		if(exam.getState() == 1) {
			throw new MyException("考试已发布");
		}
		if(exam.getState() == 3) {
			throw new MyException("考试已归档");
		}
		if (exam.getStartTime().getTime() <= new Date().getTime()) {
			throw new MyException("考试开始时间必须大于当前时间");
		}
		if (exam.getStartTime().getTime() >= exam.getEndTime().getTime()) {
			throw new MyException("考试结束时间必须大于考试开始时间");
		}
		Paper paper = paperService.getEntity(exam.getPaperId());
		if (paper.getMarkType() == 2) {// 如果是自动阅卷类型，没有阅卷开始时间和阅卷结束时间
			if (exam.getMarkStartTime().getTime() <= exam.getEndTime().getTime()) {
				throw new MyException("阅卷开始时间必须大于考试结束时间");
			}
			if (exam.getMarkStartTime().getTime() >= exam.getMarkEndTime().getTime()) {
				throw new MyException("阅卷结束时间必须大于阅卷开始时间");
			}
		}
		// 发布考试
		exam.setState(1);
		exam.setUpdateUserId(getCurUser().getId());
		exam.setUpdateTime(new Date());
		examDao.update(exam);
		
		// 标记为需要监听的考试（考试结束自动阅卷）
		AutoExamCache.put(exam.getId(), exam.getEndTime());
		if (paper.getMarkType() == 2) {// 智能试卷，考试结束，定时任务就处理完成了
			AutoMarkCache.put(exam.getId(), exam.getMarkEndTime());
		}
	}
	
	@Override
	public List<Map<String, Object>> getExamUserList(Integer id) {
		return examDao.getExamUserList(id);
	}

	@Override
	public void updateMarkSet(Integer id, String[] examUserIds, Integer[] markUserIds) {
		// 校验数据有效性
		if (id == null) {
			throw new MyException("参数错误：id");
		}
		
		if (examUserIds == null) {
			throw new MyException("参数错误：examUserIds");
		}
		
		Exam exam = getEntity(id);
		if (exam.getState() == 0) {
			throw new MyException("考试已删除");
		}
		if (exam.getState() == 2) {
			throw new MyException("考试未发布");
		}
		if (exam.getState() == 3) {
			throw new MyException("考试已归档");
		}
		if (exam.getEndTime().getTime() < System.currentTimeMillis()) {
			throw new MyException("考试已结束");
		}
		
		Paper paper = paperService.getEntity(exam.getPaperId());
		if (paper.getMarkType() == 2) {// 如果试卷是智能阅卷类型，没有阅卷用户
			if (markUserIds == null) {
				throw new MyException("参数错误：markUserIds");
			}
			
			if (markUserIds.length != 1 && examUserIds.length != markUserIds.length) {
				throw new MyException("参数错误：markUserIds和examUserIds数量不等");
			} 
		}
		Set<Integer> examUserIdSet = new HashSet<>();
		for (String userIds : examUserIds) {
			for (String userId : userIds.split(",")) {
				int _userId = Integer.parseInt(userId);
				if (examUserIdSet.contains(_userId)) {
					throw new MyException("考试用户重复");
				}
				examUserIdSet.add(_userId);
			}
		}
		Set<Integer> markUserIdSet = new HashSet<>();
		for (Integer userId : markUserIdSet) {
			if (markUserIdSet.contains(userId)) {
				throw new MyException("阅卷用户重复");
			}
			markUserIdSet.add(userId);
		}
		
		// 同步考试人员和阅卷人员到数据库
		if (ValidateUtil.isValid(markUserIds) && markUserIds.length == 1) {
			examUserIds[0] = StringUtil.join(examUserIds);// 按逗号分隔的参数，服务器接收会变成数组
		}
		
		List<MyExam> myExamList = myExamService.getList(id);// 当前考试的人员
		ListIterator<MyExam> myExamListIterator = myExamList.listIterator();
		Date curTime = new Date();
		List<PaperQuestion> paperQuestionList = paperService.getPaperQuestionList(exam.getPaperId());// 试题列表（不能用试题中的试题 分值修改有问题 只能用试卷试题中的试题）
		while (myExamListIterator.hasNext()) {// 同步考试人员信息
			/**
			 * 页面：1,2,3
			 * 数据库：2,3,4
			 * 1添加；4删除；2,3不动
			 */
			MyExam myExam = myExamListIterator.next();
			if (examUserIdSet.contains(myExam.getUserId())) {// 页面有数据库有，不处理
				myExamListIterator.remove();
				examUserIdSet.remove(myExam.getUserId());
			} else {// 页面没有数据库有，删除数据库数据
				myExamDetailService.del(myExam.getExamId(), myExam.getUserId());
				myExamService.del(myExam.getId());
			}
		}
		for (Integer userId : examUserIdSet) {// 页面有数据库没有，数据库添加
			MyExam myExam = new MyExam();
			myExam.setExamId(id);
			myExam.setUserId(userId);
			// myExam.setAnswerStartTime(exam.getStartTime());// 第一次答题记录时间，不是这里
			// myExam.setAnswerEndTime(exam.getEndTime());
			//myExam.setMarkUserId(null);
			// myExam.setMarkStartTime(exam.getMarkStartTime());
			// myExam.setMarkEndTime(exam.getMarkEndTime());
			// myExam.setTotalScore(BigDecimal.ZERO);//没有考试，不要设置分数
			myExam.setState(1);// 未考试
			myExam.setMarkState(1);// 未阅卷
			//myExam.setAnswerState(null);// 不设置及格状态
			myExam.setUpdateTime(curTime);
			myExam.setUpdateUserId(getCurUser().getId());
			myExamService.add(myExam);// 添加我的考试
			
			for (PaperQuestion paperQuestion : paperQuestionList) {
				MyExamDetail myExamDetail = new MyExamDetail();
				myExamDetail.setMyExamId(myExam.getId());
				myExamDetail.setExamId(myExam.getExamId());
				myExamDetail.setUserId(myExam.getUserId());
				myExamDetail.setQuestionId(paperQuestion.getQuestionId());
				//myExamDetail.setAnswerTime(null);
				//myExamDetail.setMarkUserId(null);
				//myExamDetail.setMarkTime(null);
				//myExamDetail.setAnswer(null);
				//myExamDetail.setScore(null);
				myExamDetail.setQuestionScore(paperQuestion.getScore());
				//myExamDetail.setAnswerFileId(null);
				myExamDetailService.add(myExamDetail);// 添加我的考试详细
			}
		}
		
		if (paper.getMarkType() == 2) {// 如果试卷是人工阅卷，添加阅卷用户
			List<MyMark> myMarkList = myMarkService.getList(id);// 先清理阅卷用户
			for (MyMark myMark : myMarkList) {
				myMarkService.del(myMark.getId());
			}
			
			for (int i = 0; i < markUserIds.length; i++) {
				MyMark myMark = new MyMark();
				myMark.setMarkUserId(markUserIds[i]);
				myMark.setExamUserIds(String.format(",%s,", examUserIds[i]));
				//myMark.setQuestionIds(null);这一版不实现按题阅卷
				myMark.setUpdateUserId(getCurUser().getId());
				myMark.setUpdateTime(curTime);
				myMark.setExamId(id);
				myMarkService.add(myMark);
			}
		}
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
	public List<Exam> getExamList(Integer paperId) {
		return examDao.getExamList(paperId);
	}

	@Override
	public List<Map<String, Object>> getMarkExamUserList(Integer id, Integer markUserId) {
		return examDao.getMarkExamUserList(id, markUserId);
	}

	@Override
	public List<Map<String, Object>> getMarkQuestionList(Integer id, Integer markUserId) {
		return examDao.getMarkQuestionList(id, markUserId);
	}

}
