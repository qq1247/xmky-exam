package com.wcpdoc.exam.report.service.impl;

import java.io.File;
import java.math.BigDecimal;
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
import com.wcpdoc.base.entity.Dict;
import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.BigDecimalUtil;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.ExamType;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.ExamTypeService;
import com.wcpdoc.exam.core.service.MyExamService;
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
	@Resource
	private MyExamService myExamService;
	@Resource
	private ExamTypeService examTypeService;
	
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
		data.put("missNum", reportDao.homeUserMissNum(getCurUser().getId())); //缺考题统计错误，时间没到的状态也是1，单独查询   homeUserMap.get("missNum")
		data.put("succNum", homeUserMap.get("succNum"));
		data.put("top", homeUserMap.get("top"));
		result.put("exam", data);
		data = new HashMap<String, Object>();
		data.put("avg", homeUserMap.get("avg") == null ? 0 : String.format("%.2f", homeUserMap.get("avg")).toString());
		data.put("min", homeUserMap.get("min") == null ? 0 : homeUserMap.get("min"));
		data.put("max", homeUserMap.get("max") == null ? 0 : homeUserMap.get("max"));
		data.put("sd", homeUserMap.get("sd") == null ? 0 : String.format("%.2f", homeUserMap.get("sd")).toString()); //标准差
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
			throw new MyException("登录用户角色错误");
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
		if (getCurUser().getId().intValue() != 1) {
			throw new MyException("登录用户角色错误");
		}
		
		List<String> result = new ArrayList<String>();
		File log4j2File = new File(ResourceUtils.getURL("./config/log4j2.xml").getFile()); // 从log4j.xml解析日志文件目录
		Document parse = Jsoup.parse(log4j2File, "UTF-8");
		Elements elementsByTag = parse.getElementsByTag("RollingFile");
		File logFile = new File(elementsByTag.attr("filename"));
		Long fileLen = logFile.length();
		Long curReadLen = fileLen - 10000;//只读取最后10000字节的日志
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
			throw new MyException("权限不足");
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
		map.put("value", questionStatisMap.get("ai1") == null ? 0 : questionStatisMap.get("ai1"));
		list.add(map);
		map = new HashMap<>();
		map.put("name", "非智能阅卷");
		map.put("value", questionStatisMap.get("ai2") == null ? 0 : questionStatisMap.get("ai2"));
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
		Exam exam = examService.getEntity(examId);
		ExamType examType = examTypeService.getEntity(exam.getExamTypeId());
		if (examType.getCreateUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("权限不足");
		}
		
		if (exam.getEndTime().getTime() >= System.currentTimeMillis()) {
			throw new MyException("考试时间未结束");
		}
		if (exam.getMarkEndTime() != null && exam.getMarkEndTime().getTime() >= System.currentTimeMillis()) {
			throw new MyException("阅卷时间未结束");
		}
		
		// 获取考试、试卷、人员成绩信息
		Paper paper = paperService.getEntity(exam.getPaperId());
		List<MyExam> myExamList = myExamService.getList(examId);
		List<Question> questionList = paperService.getQuestionList(exam.getPaperId());
		Map<Integer, Question> questionCache = new HashMap<>();
		for (Question question : questionList) {
			questionCache.put(question.getId(), question);
		}
		
		// 统计考试基础信息
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> examResult = new HashMap<String, Object>();
		examResult.put("name", exam.getName());// 考试名称
		examResult.put("startTime", DateUtil.formatDateTime(exam.getStartTime()));// 考试开始时间
		examResult.put("endTime", DateUtil.formatDateTime(exam.getEndTime()));// 考试结束时间
		examResult.put("markStartTime", exam.getMarkStartTime() == null ? null : DateUtil.formatDateTime(exam.getMarkStartTime()));// 阅卷开始时间
		examResult.put("markEndTime", exam.getMarkEndTime() == null ? null : DateUtil.formatDateTime(exam.getMarkEndTime()));// 阅卷结束时间
		examResult.put("userNum", myExamList.size());// 考试人数
		examResult.put("missUserNum", 0);// 未考试人数
		examResult.put("succUserNum", 0);// 及格人数
		for (MyExam myExam : myExamList) {
			if (myExam.getState() == 1) {
				examResult.put("missUserNum", (Integer)examResult.get("missUserNum") + 1);
			}
			if (myExam.getAnswerState() == 1) {
				examResult.put("succUserNum", (Integer)examResult.get("succUserNum") + 1);
			}
		}
		result.put("exam", examResult);// 考试基础信息
		
		// 统计考试合计信息
		Map<String, Object> scoreResult = new HashMap<String, Object>();
		scoreResult.put("total", paper.getTotalScore());// 考试总分
		scoreResult.put("avg", 0.0);// 平均分
		scoreResult.put("min", paper.getTotalScore().doubleValue());// 默认最低分是0.0  最低分就会一直是0.0
		scoreResult.put("max", 0.0);// 最高分
		
		for (MyExam myExam : myExamList) {
			if (myExam.getState() == 1) {// 排除掉未考试的
				continue;
			}
			
			scoreResult.put("min", Math.min(myExam.getTotalScore().doubleValue(), (double)scoreResult.get("min")));
			scoreResult.put("max", Math.max(myExam.getTotalScore().doubleValue(), (double)scoreResult.get("max")));
			scoreResult.put("avg", BigDecimalUtil.newInstance(scoreResult.get("avg")).add(  myExam.getTotalScore()).getResult().doubleValue());
		}
		
		if (paper.getTotalScore().compareTo(new BigDecimal(scoreResult.get("min").toString())) == 0) { //最小值和总分一样 默认赋值0.0
			scoreResult.put("min",0.0);
		}
		
		if (myExamList.size() != 0) {// 被除数不能为0
			scoreResult.put("avg", BigDecimalUtil.newInstance(scoreResult.get("avg")).div(myExamList.size(), 2).getResult().doubleValue());
		}
		result.put("score", scoreResult);
		
		// 统计试题类型占比
		List<Dict> dictList = DictCache.getDictList("QUESTION_TYPE");
		List<Map<String, Object>> typeResultList = new ArrayList<Map<String, Object>>();
		Map<String, Map<String, Object>> typeCache = new HashMap<>();
		for (Dict dict : dictList) {
			Map<String, Object> map = new HashMap<>();
			map.put("key", dict.getDictKey());// 试题类型关键词
			map.put("name", dict.getDictValue());// 试题类型名称
			map.put("value", 0);// 试题类型总数
			
			typeCache.put(dict.getDictKey(), map);
			typeResultList.add(map);
		}
		
		for (Question question : questionList) {
			Map<String, Object> map = typeCache.get(question.getType().toString());
			map.put("value", (int)map.get("value") + 1);// 按分类累加
		}
		
		for (Map<String, Object> typeResult : typeResultList) {
			typeResult.remove("key");// 接口没有这个字段，移除掉
		}
		result.put("typeList", typeResultList);
		
		// 统计分数段占比
		double scoreGrade = BigDecimalUtil.newInstance(paper.getTotalScore()).div(10, 3).getResult().doubleValue();// 分数保留两位小数，十等分后需要保留3位小数
		List<Map<String, Object>> scoreGradeResultList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < 10; i++) {
			Map<String, Object> map = new HashMap<>();
			map.put("startScore", BigDecimalUtil.newInstance(scoreGrade).mul(i).getResult().doubleValue());// 分数段最小值
			map.put("endScore", BigDecimalUtil.newInstance(scoreGrade).mul(i + 1).getResult().doubleValue());// 分数段最大值
			map.put("name", map.get("endScore"));// 初始化名称   去掉 开始值     map.put("name", String.format("%s-%s", map.get("startScore"), map.get("endScore")));
			map.put("value", 0);// 初始化值为0
			
			scoreGradeResultList.add(map);
		}
		for (MyExam myExam : myExamList) {
			for (Map<String, Object> map : scoreGradeResultList) {
				double startScore = (double) map.get("startScore");
				double endScore = (double) map.get("endScore");
				if (myExam.getTotalScore().doubleValue() >= startScore && myExam.getTotalScore().doubleValue() < endScore) {// 不包括最大边界值，如60不属于50分数段
					map.put("value", (int)map.get("value") + 1);
				}
			}
		}
		for (Map<String, Object> map : scoreGradeResultList) {
			map.remove("startScore");// 移除非接口字段
			map.remove("endScore");// 移除非接口字段
		}
		
		result.put("scoreGradeList", scoreGradeResultList);
		return result;
	}
	

	@Override
	public PageOut myExamListpage(PageIn pageIn) {
		//校验数据有效性
		Integer examId = Integer.parseInt(pageIn.get("examId"));
		if (!ValidateUtil.isValid(pageIn.get("examId"))) {
			throw new MyException("参数错误：examId");
		}
		Exam exam = examService.getEntity(examId);
		ExamType examType = examTypeService.getEntity(exam.getExamTypeId());
		if (examType.getCreateUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("权限不足");
		}
		
		if (exam.getMarkEndTime() != null && exam.getMarkEndTime().getTime() >= System.currentTimeMillis()) {
			throw new MyException("阅卷时间未结束");
		}
		
		Paper paper = paperService.getEntity(exam.getPaperId());
		PageOut pageOut = reportDao.myExamListpage(pageIn);
		for(Map<String, Object> map : pageOut.getList()){
			map.put("myExamStartTime", map.get("myExamStartTime") == null ? null : DateUtil.formatDateTime(DateUtil.getDateTime(map.get("myExamStartTime").toString())));
			map.put("myExamEndTime", map.get("myExamEndTime") == null ? null : DateUtil.formatDateTime(DateUtil.getDateTime(map.get("myExamEndTime").toString())));
			map.put("myExamMarkStartTime", map.get("myExamMarkStartTime") == null ? null : DateUtil.formatDateTime(DateUtil.getDateTime(map.get("myExamMarkStartTime").toString())));
			map.put("myExamMarkEndTime", map.get("myExamMarkEndTime") == null ? null : DateUtil.formatDateTime(DateUtil.getDateTime(map.get("myExamMarkEndTime").toString())));
			
			map.put("paperTotalScore", paper.getTotalScore());
			map.put("paperPassScore", paper.getPassScore());
		}
		return pageOut;
	}
	
	@Override
	public PageOut questionListpage(PageIn pageIn) {
		//校验数据有效性
		Integer examId = Integer.parseInt(pageIn.get("examId"));
		if (!ValidateUtil.isValid(examId)) {
			throw new MyException("参数错误：examId");
		}
		Exam exam = examService.getEntity(examId);
		ExamType examType = examTypeService.getEntity(exam.getExamTypeId());
		if (examType.getCreateUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("权限不足");
		}
		if (exam.getMarkEndTime() != null && exam.getMarkEndTime().getTime() >= System.currentTimeMillis()) {
			throw new MyException("阅卷时间未结束");
		}
		
		PageOut pageOut = reportDao.questionListpage(pageIn);
		return pageOut;
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