package com.wcpdoc.exam.core.service.impl;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.dao.MyExamDao;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyExamDetail;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.MyExamDetailService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.util.DateUtil;
import com.wcpdoc.exam.core.util.StringUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 我的考试服务层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Service
public class MyExamServiceImpl extends BaseServiceImp<MyExam> implements MyExamService {
	private static final Logger log = LoggerFactory.getLogger(MyExamServiceImpl.class);
	
	@Resource
	private MyExamDao myExamDao;
	@Resource
	private QuestionService questionService;
	@Resource
	private ExamService examService;
	@Resource
	private MyExamDetailService myExamDetailService;

	@Override
	@Resource(name = "myExamDaoImpl")
	public void setDao(BaseDao<MyExam> dao) {
		super.dao = dao;
	}

	@Override
	public void del(Integer examId, Integer userId) {
		myExamDao.del(examId, userId);
	}

	@Override
	public List<MyExam> getList(Integer examId) {
		return myExamDao.getList(examId);
	}


	@Override
	public MyExam getEntity(Integer examId, Integer userId) {
		return myExamDao.getEntity(examId, userId);
	}
	
	@Override
	public List<Map<String, Object>> kalendar(Integer year, Integer month) {
		String time = year+"-"+month+"-01 00:00:00";
		Date dateTime = DateUtil.getDateTime(time);
		List<MyExam> myExamList = myExamDao.kalendar(getCurUser().getId(), DateUtil.getMonthStart(dateTime), DateUtil.getMonthEnd(dateTime));
		if(myExamList == null){
			return null;			
		}
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for(MyExam myExam : myExamList){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", myExam.getId());
			map.put("examId", myExam.getExamId());
			map.put("time", DateUtil.formatDateTime(myExam.getAnswerStartTime()));
			list.add(map);
		}
		return list;
	}

	@Override
	public PageOut getRankingPage(PageIn pageIn) {
		return myExamDao.getRankingPage(pageIn);
	}

	@Override
	public Map<String, Object> count(Integer examId) {
		List<Map<String, Object>> count = myExamDao.count(examId);
		Map<String, Object> map = new HashMap<String, Object>();
		DecimalFormat df = new DecimalFormat("0.0");
		for(Map<String, Object> mapList : count ){			
			map.put("max", df.format(Double.parseDouble(mapList.get("MAX").toString())));
			map.put("min", df.format(Double.parseDouble(mapList.get("MIN").toString())));
			map.put("avg", df.format(Double.parseDouble(mapList.get("AVG").toString())));
			map.put("pass", df.format(Double.parseDouble(mapList.get("ANSWER").toString())));
			int minutes = 0;
			try {
				SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				Date startTime = simpleFormat.parse(mapList.get("START_TIME").toString());
				Date endTime = simpleFormat.parse(mapList.get("END_TIME").toString());
				minutes = (int) ((endTime.getTime() - startTime.getTime()) / (1000 * 60));  
			} catch (ParseException e) {
				log.error(e.getMessage());
				throw new MyException("时间错误！");
			}
			map.put("mins", minutes+"分钟");
			map.put("totalScore", df.format(Double.parseDouble(mapList.get("TOTAL_SCORE").toString())));
		}
		return map;
	}

	@Override
	public void updateAnswer(Integer myExamDetailId, String[] answers) {
		// 校验数据有效性
		if (myExamDetailId == null) {
			throw new MyException("参数错误：myExamDetailId");
		}

		// if(!ValidateUtil.isValid(answer)) {
		// 	throw new MyException("参数错误：answer");
		// }//如取消勾选则为空

		MyExamDetail myExamDetail = myExamDetailService.getEntity(myExamDetailId);
		if (myExamDetail.getUserId() != getCurUser().getId()) {
			throw new MyException("未参与考试！");
		}

		Exam exam = examService.getEntity(myExamDetail.getExamId());
		if (exam.getState() == 0) {
			throw new MyException("考试已删除！");
		}
		if (exam.getState() == 2) {
			throw new MyException("考试未发布！");
		}
		if (exam.getStartTime().getTime() > (new Date().getTime())) {
			throw new MyException("考试未开始！");
		}
		if (exam.getEndTime().getTime() < (new Date().getTime() - 30000)) {// 预留30秒网络延时
			throw new MyException("考试已结束！");
		}

		// 更新我的考试详细信息
		Question question = questionService.getEntity(myExamDetail.getQuestionId());
		if (!ValidateUtil.isValid(answers)) {
			myExamDetail.setAnswer(null);
		} else if (question.getType() == 1 || question.getType() == 4 || question.getType() == 5) {
			myExamDetail.setAnswer(answers[0]);
		} else if (question.getType() == 2) {
			myExamDetail.setAnswer(StringUtil.join(answers));
		} else if (question.getType() == 3) {
			myExamDetail.setAnswer(StringUtil.join(answers, "\n"));
		} else if (question.getType() == 5 && question.getAi() == 1) {
			myExamDetail.setAnswer(StringUtil.join(answers, "\n"));
		}
		myExamDetail.setAnswerTime(new Date());
		myExamDetailService.update(myExamDetail);
	}

	@Override
	public void doAnswer(Integer myExamId) {
		// 校验数据有效性
		MyExam myExam = myExamDao.getEntity(myExamId);
		if (myExam == null) {
			throw new MyException("参数错误：myExamId");
		}
		if (myExam.getUserId().intValue() != getCurUser().getId()) {
			throw new MyException("未参与该考试！");
		}
	
		Exam exam = examService.getEntity(myExam.getExamId());
		if (exam.getState() == 0) {
			throw new MyException("考试已删除！");
		}
		if (exam.getState() == 2) {
			throw new MyException("考试未发布！");
		}
		if (exam.getStartTime().getTime() > (new Date().getTime())) {
			throw new MyException("考试未开始！");
		}
		if (exam.getEndTime().getTime() < (new Date().getTime() - 30000)){//预留30秒网络延时
			throw new MyException("考试已结束！");
		}
	
		// 标记为已交卷
		myExam.setState(3);
		myExam.setAnswerEndTime(new Date());
		myExamDao.update(myExam);
	}
}
