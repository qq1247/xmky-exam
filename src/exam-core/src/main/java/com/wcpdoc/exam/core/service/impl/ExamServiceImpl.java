package com.wcpdoc.exam.core.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.wcpdoc.exam.base.service.OrgService;
import com.wcpdoc.exam.base.service.UserService;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.dao.ExamDao;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyExamDetail;
import com.wcpdoc.exam.core.entity.MyMark;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.UpdateMarkUserJson;
import com.wcpdoc.exam.core.exception.MyException;
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
	public PageOut getUserListpage(PageIn pageIn) {
		return examDao.getUserListpage(pageIn);
	}

	@Override
	public void updateExamUser(Integer id, Integer[] userIds) {
		// 校验数据有效性
				if (id == null) {
					throw new MyException("参数错误：id");
				}
				Exam exam = getEntity(id);
				if (exam.getState() == 0) {
					throw new MyException("考试已删除！");
				}
				if (exam.getState() == 2) {
					throw new MyException("考试未发布！");// 必须已发布，否则在考试结束前添加或删除考试用户，试题有可能会变更，每个人的考试详细可能不一样（也能处理，比较费事）。
				}

				if (exam.getEndTime().getTime() <= new Date().getTime()) {
					throw new MyException("考试已结束，不允许添加！");
				}

				// 添加我的考试（不能整个重新添加，因为有可能是已开始考试途中添加人员，部分人员已作答）
				List<MyExam> myExamList = myExamService.getList(id);// 当前考试的人员
				List<Question> questionList = paperService.getQuestionList(exam.getPaperId());// 试卷的问题
				Set<Integer> curUserIdSet = new HashSet<>(Arrays.asList(userIds));//当前页面选中的考试的人员
				ListIterator<MyExam> myExamListIterator = myExamList.listIterator();
				while (myExamListIterator.hasNext()) {// 如果页面有选择该用户，数据库也有，则不处理
					MyExam next = myExamListIterator.next();
					if (curUserIdSet.contains(next.getUserId())) {
						myExamListIterator.remove();
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
				for (Integer userId : curUserIdSet) {//如果页面有选择该用户，数据库没有，则数据库添加该用户的考试记录和考试详细记录
					MyExam myExam = new MyExam();
					myExam.setExamId(id);
					myExam.setUserId(userId);
					
					myExam.setAnswerStartTime(exam.getStartTime());
					myExam.setAnswerEndTime(exam.getEndTime());
					myExam.setMarkStartTime(exam.getMarkStartTime());
					myExam.setMarkEndTime(exam.getMarkEndTime());
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
				}
	}

	@Override
	public void updateMarkUser(UpdateMarkUserJson updateMarkUserJson) {
		// 校验数据有效性
		if (updateMarkUserJson.getId() == null) {
			throw new MyException("参数错误：id");
		}
		
		for(MyMark myMark : updateMarkUserJson.getMarkUserIds()){
			myMarkService.del(updateMarkUserJson.getId(), myMark.getMarkUserId());
			
			myMark.setExamId(updateMarkUserJson.getId());
			//myMark.setMarkUserId(myMark.getMarkUserId());
			//myMark.setQuestionIds(questionIds);
			//myMark.setExamUserIds(examUserIds);
			myMark.setUpdateTime(new Date());
			myMark.setUpdateUserId(getCurUser().getId());
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
	public List<Map<String, Object>> questionList(Integer id) {
		List<Map<String, Object>> questionList = examDao.questionList(id);
		for(Map<String, Object> map : questionList){
			String parameter = map.get("title").toString();
			Document document = Jsoup.parse(parameter);
			parameter = document.body().html();
			map.put("title", Jsoup.parse(parameter).text());
		}
		return questionList;
	}
}
