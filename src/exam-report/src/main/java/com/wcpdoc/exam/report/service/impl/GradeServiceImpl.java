package com.wcpdoc.exam.report.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.exam.report.dao.GradeDao;
import com.wcpdoc.exam.report.service.GradeService;

/**
 * 日志服务层实现
 * 
 * v1.0 zhanghc 2017年8月29日下午2:03:36
 */
@Service
public class GradeServiceImpl implements GradeService {
	
	@Resource
	private GradeDao gradeDao;
	
	@Override
	public PageOut getListpage(PageIn pageIn) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Map<String, Object> count(Integer examId) {
		List<Map<String, Object>> count = gradeDao.count(examId);
		if (count.size() == 0) {
			return null;
		}
		Map<String, Object> map = gradeDao.count(examId).get(0);
		map.put("examEndTime", map.get("examEndTime") == null ? null : DateUtil.formatDateTime(DateUtil.getDateTime( map.get("examEndTime").toString())));
		map.put("examStartTime", map.get("examStartTime") == null ? null : DateUtil.formatDateTime(DateUtil.getDateTime(map.get("examStartTime").toString())));
		map.put("maxExam", map.get("maxExam") == null ? null : DateUtil.formatDateTime(DateUtil.getDateTime(map.get("maxExam").toString())));
		map.put("minExam", map.get("minExam") == null ? null : DateUtil.formatDateTime(DateUtil.getDateTime(map.get("minExam").toString())));
		return map;
	}
}
