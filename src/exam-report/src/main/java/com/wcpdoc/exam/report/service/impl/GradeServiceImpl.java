package com.wcpdoc.exam.report.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.exam.report.dao.GradeDao;
import com.wcpdoc.exam.report.service.GradeService;
import com.wcpdoc.exam.report.service.ReportExService;

/**
 * 统计服务层实现
 * 
 * v1.0 zhanghc 2017年8月29日下午2:03:36
 */
@Service
public class GradeServiceImpl extends BaseServiceImp<Object> implements GradeService {
	
	@Resource
	private GradeDao gradeDao;
	@Resource
	private ReportExService reportExService;
	@Value("${spring.datasource.url}")
	private String url;
	@Value("${spring.datasource.username}")
	private String username;
	@Value("${spring.datasource.password}")
	private String password;
	
	@Override
	public void setDao(BaseDao<Object> dao) {}
	
	@Override
	public Map<String, Object> homeUser() {
		List<Map<String, Object>> homeUserList = gradeDao.homeUser(getCurUser().getId());
		return homeUserList.get(0);
	}

	@Override
	public Map<String, Object> homeSubAdmin() {
		Map<String, Object> homeSubAdmin = new HashMap<String, Object>();
		List<Map<String, Object>> homeSubAdminExam = gradeDao.homeSubAdminExam(getCurUser().getId());
		Map<String, Object> examMap = homeSubAdminExam.get(0);
		homeSubAdmin.put("userId", examMap.get("userId"));
		homeSubAdmin.put("userName", examMap.get("userName"));
		homeSubAdmin.put("orgId", examMap.get("orgId"));
		homeSubAdmin.put("orgName", examMap.get("orgName"));
		homeSubAdmin.put("type", examMap.get("type"));
		homeSubAdmin.put("examNum", examMap.get("examNum"));
		List<Map<String, Object>> paperMap = gradeDao.homeSubAdminPaper(getCurUser().getId());
		homeSubAdmin.put("paperNum", paperMap.get(0).get("paperNum"));
		List<Map<String, Object>> questionMap = gradeDao.homeSubAdminQuestion(getCurUser().getId());
		homeSubAdmin.put("questionNum", questionMap.get(0).get("questionNum"));
		List<Map<String, Object>> markMap = gradeDao.homeSubAdminMark(getCurUser().getId());
		homeSubAdmin.put("markNum", markMap.get(0).get("markNum"));
		return homeSubAdmin;
	}
	
	@Override
	public Map<String, Object> homeAdmin() {
		if (getCurUser().getId().intValue() != 1) {
			throw new MyException("登录用户角色错误！");
		}

		Map<String, Object> homeAdmin = new HashMap<String, Object>();
		List<Map<String, Object>> userMap = gradeDao.homeAdminUser();
		homeAdmin.put("userNum", userMap.get(0).get("userNum"));
		homeAdmin.put("subadminNum", userMap.get(0).get("subadminNum"));
		List<Map<String, Object>> bulletinMap = gradeDao.homeAdminBulletin();
		homeAdmin.put("bulletinNum", bulletinMap.get(0).get("bulletinNum"));
		homeAdmin.put("onUserNum", reportExService.OnlineNum());
		return homeAdmin;
	}
	
	@Override
	public Map<String, Object> slowLog() {
		Map<String, Object> homeAdmin = new HashMap<String, Object>();
		homeAdmin.put("content", "");
		return homeAdmin;
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