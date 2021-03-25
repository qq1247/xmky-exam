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
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.util.DateUtil;

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
			map.put("time", DateUtil.formatDateTime(myExam.getAnswerTime()));
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
}
