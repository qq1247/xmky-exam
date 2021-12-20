package com.wcpdoc.exam.report.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.wcpdoc.base.cache.DictCache;
import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.PaperService;
import com.wcpdoc.exam.core.service.QuestionTypeService;
import com.wcpdoc.exam.report.dao.ReportDao;
import com.wcpdoc.exam.report.service.ReportExService;
import com.wcpdoc.exam.report.service.ReportService;

/**
 * 统计服务层实现
 * 
 * v1.0 zhanghc 2017年8月29日下午2:03:36
 */
@Service
public class ReportServiceImpl extends BaseServiceImp<Object> implements ReportService {
	
	@Resource
	private ReportDao reportDao;
	@Resource
	private ReportExService reportExService;
	@Value("${spring.datasource.url}")
	private String url;
	@Value("${spring.datasource.username}")
	private String username;
	@Value("${spring.datasource.password}")
	private String password;
	@Resource
	private QuestionTypeService questionTypeService;
	@Resource
	private ExamService examService;
	@Resource
	private PaperService paperService;
	
	@Override
	public void setDao(BaseDao<Object> dao) {}
	
	@Override
	public Map<String, Object> homeUser() {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> homeUserList = reportDao.homeUser(getCurUser().getId());
		Map<String, Object> homeUserMap = homeUserList.get(0);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("id", homeUserMap.get("userId"));
		data.put("name", homeUserMap.get("userName"));
		data.put("type", homeUserMap.get("type"));
		result.put("user", data);
		data = new HashMap<String, Object>();
		data.put("id", homeUserMap.get("orgId"));
		data.put("name", homeUserMap.get("orgName"));
		result.put("org", data);
		data = new HashMap<String, Object>();
		data.put("num", homeUserMap.get("examNum"));
		data.put("missNum", homeUserMap.get("missNum"));
		data.put("succNum", homeUserMap.get("succNum"));
		data.put("top", homeUserMap.get("top"));
		result.put("exam", data);
		data = new HashMap<String, Object>();
		data.put("avg", homeUserMap.get("avg"));
		data.put("min", homeUserMap.get("min"));
		data.put("max", homeUserMap.get("max"));
		data.put("sd", homeUserMap.get("sd")); //标准差
		result.put("score", data);
		return result;
	}

	@Override
	public Map<String, Object> homeSubAdmin() {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> homeSubAdminExam = reportDao.homeSubAdminExam(getCurUser().getId());
		Map<String, Object> examMap = homeSubAdminExam.get(0);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("id", examMap.get("userId"));
		data.put("name", examMap.get("userName"));
		data.put("type", examMap.get("type"));
		result.put("user", data);
		data = new HashMap<String, Object>();
		data.put("id", examMap.get("orgId"));
		data.put("name", examMap.get("orgName"));
		result.put("org", data);
		data = new HashMap<String, Object>();
		data.put("num", examMap.get("examNum"));
		result.put("exam", data);
		data = new HashMap<String, Object>();
		List<Map<String, Object>> paperMap = reportDao.homeSubAdminPaper(getCurUser().getId());
		data.put("num", paperMap.get(0).get("paperNum"));
		result.put("paper", data);
		data = new HashMap<String, Object>();
		List<Map<String, Object>> questionMap = reportDao.homeSubAdminQuestion(getCurUser().getId());
		data.put("num", questionMap.get(0).get("questionNum"));
		result.put("question", data);
		data = new HashMap<String, Object>();
		List<Map<String, Object>> markMap = reportDao.homeSubAdminMark(getCurUser().getId());
		data.put("num", markMap.get(0).get("markNum"));
		result.put("myMark", data);
		return result;
	}
	
	@Override
	public Map<String, Object> homeAdmin() {
		if (getCurUser().getId().intValue() != 1) {
			throw new MyException("登录用户角色错误！");
		}

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		List<Map<String, Object>> userMap = reportDao.homeAdminUser();
		data.put("num", userMap.get(0).get("userNum"));
		result.put("user", data);
		data = new HashMap<String, Object>();
		data.put("num", userMap.get(0).get("subadminNum"));
		result.put("subAdmin", data);
		data = new HashMap<String, Object>();
		List<Map<String, Object>> bulletinMap = reportDao.homeAdminBulletin();
		data.put("num", bulletinMap.get(0).get("bulletinNum"));
		result.put("bulletin", data);
		data = new HashMap<String, Object>();
		data.put("num", reportExService.OnlineNum());
		result.put("onlineUser", data);
		return result;
	}
	
	@Override
	public List<String> serverLog() throws Exception {
		List<String> result = new ArrayList<String>();
		File log4j2File = new File(ResourceUtils.getURL("./config/log4j2.xml").getFile()); // 从log4j.xml解析日志文件目录
		Document parse = Jsoup.parse(log4j2File, "UTF-8");
		Elements elementsByTag = parse.getElementsByTag("RollingFile");
		File logFile = new File(elementsByTag.attr("filename"));
		Long fileLen = logFile.length();
		Long curReadLen = fileLen - 10000;
		curReadLen = curReadLen < 0 ? 0 : curReadLen;
		List<String> strList = StringUtil.getString(logFile, curReadLen, fileLen);

		for (int i = 0; i < strList.size(); i++) {
			if (result.size() >= 10) {
				return result;
			}
			
			if (strList.get(i).contains("请求耗时异常：链接：")) {
				result.add(strList.get(i));
			}
		}
		return result;
	}
	
	@Override
	public Map<String, Object> questionStatis(Integer questionTypeId) {
		//校验数据有效性
		if (!ValidateUtil.isValid(questionTypeId)) {
			throw new MyException("参数错误：questionTypeId");
		}
		QuestionType questionType = questionTypeService.getEntity(questionTypeId);
		if (questionType.getCreateUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("权限不足！");
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Map<String, Object>> questionStatis = reportDao.questionStatis(questionTypeId);
		Map<String, Object> questionStatisMap = questionStatis.get(0);
		Map<String, Object> map;
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();//类型列表
		for(int i = 1; i <= 5 ; i++ ){
			map = new HashMap<>();
			map.put("name", DictCache.getDictValue("QUESTION_TYPE", i+""));
			map.put("value", questionStatisMap.get("type"+i) == null ? 0 : questionStatisMap.get("type"+i));
			list.add(map);
		}
		result.put("typeList", list);
		
		list = new ArrayList<Map<String, Object>>(); //难度列表
		for(int i = 1; i <= 5 ; i++ ){
			map = new HashMap<>();
			map.put("name", DictCache.getDictValue("QUESTION_DIFFICULTY", i+""));
			map.put("value", questionStatisMap.get("difficulty"+i) == null ? 0 : questionStatisMap.get("difficulty"+i));
			list.add(map);
		}
		result.put("difficultyList", list);
		
		list = new ArrayList<Map<String, Object>>(); //智能列表
		map = new HashMap<>();
		map.put("name", "智能阅卷");
		map.put("value", questionStatisMap.get("ai1"));
		list.add(map);
		map = new HashMap<>();
		map.put("name", "非智能阅卷");
		map.put("value", questionStatisMap.get("ai2"));
		list.add(map);
		result.put("aiList", list);
		return result;
	}	
	
	@Override
	public Map<String, Object> examStatis(Integer examId) {
		//校验数据有效性
		if (!ValidateUtil.isValid(examId)) {
			throw new MyException("参数错误：examId");
		}
		Exam entity = examService.getEntity(examId);
		if (entity.getMarkEndTime().getTime() >= System.currentTimeMillis()) {
			throw new MyException("阅卷时间未结束！");
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Map<String, Object>> examStatis = reportDao.examStatis(examId);
		Map<String, Object> examMap = examStatis.get(0);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", examMap.get("examName"));
		map.put("startTime", examMap.get("examStartTime"));
		map.put("endTime", examMap.get("examEndTime"));
		map.put("markStartTime", examMap.get("examMarkStartTime"));
		map.put("markEndTime", examMap.get("examMarkEndTime"));
		map.put("userNum", examMap.get("examUserNum") == null ? 0 : examMap.get("examUserNum"));
		map.put("missUserNum", examMap.get("missUserNum") == null ? 0 : examMap.get("missUserNum"));
		map.put("succUserNum", examMap.get("succUserNum") == null ? 0 : examMap.get("succUserNum"));
		result.put("exam", map);
		map.put("total", examMap.get("total"));
		map.put("avg", examMap.get("avg"));
		map.put("min", examMap.get("min"));
		map.put("max", examMap.get("max"));
		map.put("sd", examMap.get("sd")); //标准差
		result.put("score", map);
		
		List<Map<String, Object>> examStatisType = reportDao.examStatisType(entity.getPaperId()); //类型列表
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for(int i = 1; i <= 5 ; i++ ){
			map = new HashMap<>();
			map.put("name", DictCache.getDictValue("QUESTION_TYPE", i+""));
			map.put("value", examStatisType.get(0).get("type"+i) == null ? 0 :  examStatisType.get(0).get("type"+i));
			list.add(map);
		}
		result.put("typeList", list);
		
		list = new ArrayList<Map<String, Object>>();
		result.put("scoreGradeList", list);
		return result;
	}
	

	@Override
	public List<Map<String, Object>> myExamListpage(Integer examId) {
		//校验数据有效性
		if (!ValidateUtil.isValid(examId)) {
			throw new MyException("参数错误：examId");
		}
		Exam exam = examService.getEntity(examId);
		if (exam.getMarkEndTime().getTime() >= System.currentTimeMillis()) {
			throw new MyException("阅卷时间未结束！");
		}
		
		Paper paper = paperService.getEntity(exam.getPaperId());
		List<Map<String, Object>> myExamListpage = reportDao.myExamListpage(examId);
		for(Map<String, Object> map : myExamListpage){
			map.put("myExamStartTime", map.get("myExamStartTime") == null ? "" : DateUtil.formatDateTime(DateUtil.getDateTime(map.get("myExamStartTime").toString())));
			map.put("myExamEndTime", map.get("myExamEndTime") == null ? "" : DateUtil.formatDateTime(DateUtil.getDateTime(map.get("myExamEndTime").toString())));
			map.put("myExamMarkStartTime", map.get("myExamMarkStartTime") == null ? "" : DateUtil.formatDateTime(DateUtil.getDateTime(map.get("myExamMarkStartTime").toString())));
			map.put("myExamMarkEndTime", map.get("myExamMarkEndTime") == null ? "" : DateUtil.formatDateTime(DateUtil.getDateTime(map.get("myExamMarkEndTime").toString())));
			
			map.put("paperTotalScore", paper.getTotalScore());
			map.put("paperPassScore", paper.getPassScore());
		}
		return myExamListpage;
	}
	
	@Override
	public List<Map<String, Object>> questionListpage(Integer examId) {
		//校验数据有效性
		if (!ValidateUtil.isValid(examId)) {
			throw new MyException("参数错误：examId");
		}
		Exam exam = examService.getEntity(examId);
		if (exam.getMarkEndTime().getTime() >= System.currentTimeMillis()) {
			throw new MyException("阅卷时间未结束！");
		}
		
		return reportDao.questionListpage(exam.getId());
	}
	
	@Override
	public Map<String, Object> count(Integer examId) {
		List<Map<String, Object>> count = reportDao.count(examId);
		if (count.size() == 0) {
			return null;
		}
		Map<String, Object> map = reportDao.count(examId).get(0);
		map.put("examEndTime", map.get("examEndTime") == null ? null : DateUtil.formatDateTime(DateUtil.getDateTime( map.get("examEndTime").toString())));
		map.put("examStartTime", map.get("examStartTime") == null ? null : DateUtil.formatDateTime(DateUtil.getDateTime(map.get("examStartTime").toString())));
		map.put("maxExam", map.get("maxExam") == null ? null : DateUtil.formatDateTime(DateUtil.getDateTime(map.get("maxExam").toString())));
		map.put("minExam", map.get("minExam") == null ? null : DateUtil.formatDateTime(DateUtil.getDateTime(map.get("minExam").toString())));
		return map;
	}
}