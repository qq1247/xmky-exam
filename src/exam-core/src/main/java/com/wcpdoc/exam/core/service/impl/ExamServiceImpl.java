package com.wcpdoc.exam.core.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.wcpdoc.base.service.OrgService;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.entity.LoginUser;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.cache.AutoMarkCache;
import com.wcpdoc.exam.core.dao.ExamDao;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.ExamType;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyExamDetail;
import com.wcpdoc.exam.core.entity.MyMark;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.Question;
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
		if(!hasWriteAuth(exam.getExamTypeId(), getCurUser().getId())) {
			throw new MyException("无操作权限");
		}
		
		//添加考试
		exam.setCreateUserId(getCurUser().getId());
		exam.setCreateTime(new Date());
		exam.setUpdateUserId(getCurUser().getId());
		exam.setUpdateTime(new Date());
		exam.setUpdateUserId(getCurUser().getId());
		exam.setState(2);
		exam.setMarkState(2);// 标记为未阅卷（考试时间结束，定时任务自动阅卷，标记为已阅）
		
		Paper paper = paperService.getEntity(exam.getPaperId());
		if (paper.getMarkType() == 1) {// 如果是自动阅卷类型，没有阅卷开始时间和阅卷结束时间
			exam.setMarkStartTime(null);
			exam.setMarkEndTime(null);
		}
		add(exam);
	}
	
	@Override
	public void updateAndUpdate(Exam exam) {
		//校验数据有效性
		Exam entity = examDao.getEntity(exam.getId());
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
		if(!hasWriteAuth(entity.getExamTypeId(), getCurUser().getId())) {
		   throw new MyException("无操作权限");
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
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		//exam.setMarkState(2);// 不处理
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
		if(!hasWriteAuth(exam.getExamTypeId(), getCurUser().getId())) {
			throw new MyException("无操作权限");
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
		if(!hasWriteAuth(exam.getExamTypeId(), getCurUser().getId())) {
			throw new MyException("无操作权限");
		}
		
		// 发布考试
		exam.setState(1);
		exam.setUpdateUserId(getCurUser().getId());
		exam.setUpdateTime(new Date());
		examDao.update(exam);
		
		// 标记为需要监听的考试（考试结束自动阅卷）
		AutoMarkCache.put(exam.getId(), exam.getEndTime());
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
			throw new MyException("参数错误：userIds");
		}
		Exam exam = getEntity(id);
		if (exam.getState() == 0) {
			throw new MyException("考试已删除！");
		}
		if (exam.getState() == 2) {
			throw new MyException("考试未发布！");// 必须已发布，否则在考试结束前添加或删除考试用户，试题有可能会变更，每个人的考试详细可能不一样（也能处理，比较费事）。
		}
		if(markUserIds.length != 1 && examUserIds.length != markUserIds.length ){
			throw new MyException("阅卷人数有误！");
		}
		if (markUserIds.length == 1) {
			examUserIds[0] = StringUtil.join(examUserIds);
		}
		if (exam.getEndTime().getTime() <= new Date().getTime()) {
			throw new MyException("考试已结束，不允许添加！");
		}
		Set<Integer> examUserIdSet = new HashSet<>();
		
		// 删除旧阅卷用户
		List<MyMark> myMarkList = myMarkService.getList(id);
		for (MyMark myMark : myMarkList) {
			myMarkService.del(myMark.getId());
		}
		// 添加我的考试（不能整个重新添加，因为有可能是已开始考试途中添加人员，部分人员已作答）
		List<MyExam> myExamList = myExamService.getList(id);// 当前考试的人员
		List<Question> questionList = paperService.getQuestionList(exam.getPaperId());// 试卷的问题
		
		for(int i = 0; i < markUserIds.length; i++) {
			//考试人
			Set<String> curUserIdSet = new HashSet<>(Arrays.asList(examUserIds[i].split(",")));//当前页面选中的考试的人员
			ListIterator<MyExam> myExamListIterator = myExamList.listIterator();
			while (myExamListIterator.hasNext()) {// 如果页面有选择该用户，数据库也有，则不处理
				MyExam next = myExamListIterator.next();
				if (curUserIdSet.contains(next.getUserId())) {
					if (examUserIdSet.contains(next.getUserId())) {
						throw new MyException("考试人员重复！");
					}
					
					myExamListIterator.remove();
					examUserIdSet.add(next.getUserId());// set中添加考试人员
					curUserIdSet.remove(next.getUserId());
				}
			}
			myExamListIterator = myExamList.listIterator();
			while (myExamListIterator.hasNext()) {// 如果页面没有选择该用户，数据库有，则数据库记录删除该用户的考试记录和考试详细记录
				MyExam next = myExamListIterator.next();
				myExamDetailService.delByMyExamId(next.getId());
				myExamService.del(next.getId());
			}
			Date curTime = new Date();
			for (String userId : curUserIdSet) {//如果页面有选择该用户，数据库没有，则数据库添加该用户的考试记录和考试详细记录
				if (examUserIdSet.contains(userId)) {
					throw new MyException("考试人员重复！");
				}
				MyExam myExam = new MyExam();
				myExam.setExamId(id);
				myExam.setUserId(Integer.parseInt(userId));
				
				//myExam.setAnswerStartTime(exam.getStartTime());// 第一次答题记录时间，不是这里
				//myExam.setAnswerEndTime(exam.getEndTime());
				//myExam.setMarkStartTime(exam.getMarkStartTime());// 第一次阅卷记录时间，不是这里
				//myExam.setMarkEndTime(exam.getMarkEndTime());
				//myExam.setTotalScore(BigDecimal.ZERO);//没有考试，不要设置分数
				myExam.setState(1);
				myExam.setMarkState(1);
				myExam.setUpdateTime(curTime);
				myExam.setUpdateUserId(getCurUser().getId());
				myExamService.add(myExam);// 添加我的考试
				
				for (Question question : questionList) {
					MyExamDetail myExamDetail = new MyExamDetail();
					myExamDetail.setMyExamId(myExam.getId());
					myExamDetail.setExamId(myExam.getExamId());
					myExamDetail.setUserId(myExam.getUserId());
					myExamDetail.setQuestionId(question.getId());
					myExamDetail.setQuestionScore(question.getScore());
					myExamDetailService.add(myExamDetail);// 添加我的考试详细
				}
				examUserIdSet.add(Integer.parseInt(userId));// set中添加考试人员
			}
			
			//阅卷人
			MyMark myMark = new MyMark();
			myMark.setMarkUserId(markUserIds[i]);
			myMark.setExamUserIds(examUserIds != null ? String.format(",%s,", examUserIds[i]) : null);
			//myMark.setQuestionIds(questionIds != null ? String.format(",%s,", questionIds[i]) : null);  //目前只根据人判卷
			myMark.setUpdateUserId(getCurUser().getId());
			myMark.setUpdateTime(new Date());
			myMark.setExamId(id);
			myMarkService.add(myMark);
		}
	}

	@Override
	public void updateMarkUser(Integer id, Integer[] markUserIds, String[] examUserIds, String[] questionIds) {
		// 校验数据有效性
		if (id == null) {
			throw new MyException("参数错误：id");
		}
		if (markUserIds == null) {
			throw new MyException("参数错误：markUserIds");
		}
		if (!ValidateUtil.isValid(examUserIds) && !ValidateUtil.isValid(questionIds)) {// 两个不能都无效
			throw new MyException("参数错误：examUserIds,questionIds");
		}
		if (ValidateUtil.isValid(examUserIds) && ValidateUtil.isValid(questionIds)) {// 两个不能都有效
			throw new MyException("参数错误：examUserIds,questionIds");
		}
		
		// 更新阅卷用户
		List<MyMark> myMarkList = myMarkService.getList(id);
		for (MyMark myMark : myMarkList) {
			myMarkService.del(myMark.getId());
		}
		if (markUserIds.length == 1) {
			examUserIds[0] = StringUtil.join(examUserIds);
		}
		
		for(int i = 0; i < markUserIds.length; i++) {
			MyMark myMark = new MyMark();
			myMark.setMarkUserId(markUserIds[i]);
			myMark.setExamUserIds(examUserIds != null ? String.format(",%s,", examUserIds[i]) : null);
			myMark.setQuestionIds(questionIds != null ? String.format(",%s,", questionIds[i]) : null);
			myMark.setUpdateUserId(getCurUser().getId());
			myMark.setUpdateTime(new Date());
			myMark.setExamId(id);
			myMarkService.add(myMark);
		}
	}

	@Override
	public void forcePaper(LoginUser user) {
		//标记为强制交卷
		log.debug("开始强制交卷");
		examDao.doForcePaper(user);
		log.debug("成功强制交卷");
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
	
	private boolean hasWriteAuth(Integer examTypeId, Integer userId) {
		ExamType examType = examTypeService.getEntity(examTypeId);
		return examType.getWriteUserIds().contains(String.format(",%s,", userId));
	}
	
//	private boolean hasReadAuth(Integer examTypeId, Integer userId) {
//		ExamType examType = examTypeService.getEntity(examTypeId);
//		return examType.getReadUserIds().contains(String.format(",%s,", userId));
//	}

}
