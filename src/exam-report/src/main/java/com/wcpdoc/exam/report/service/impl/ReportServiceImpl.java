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
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.wcpdoc.base.entity.Dict;
import com.wcpdoc.base.entity.Org;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.DictService;
import com.wcpdoc.base.service.OrgService;
import com.wcpdoc.base.service.UserService;
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
import com.wcpdoc.exam.core.entity.ExamQuestion;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.service.ExamQuestionService;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyMarkService;
import com.wcpdoc.exam.core.service.QuestionService;
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
	@Resource
	private QuestionTypeService questionTypeService;
	@Resource
	private ExamService examService;
	@Resource
	private MyExamService myExamService;
	@Resource
	private MyMarkService myMarkService;
	@Resource
	private DictService dictService;
	@Resource
	private QuestionService questionService;
	@Resource
	private UserService userService;
	@Resource
	private OrgService orgService;
	@Resource
	private ExamQuestionService examQuestionService;
	
	@Override
	public void setDao(BaseDao<Object> dao) {}
	
	@Override
	public Map<String, Object> homeUser() {
		// 获取原始数据
		User user = userService.getEntity(getCurUser().getId());// 用户信息
		Org org = orgService.getEntity(user.getOrgId());// 机构信息
		List<MyExam> myExamList = myExamService.getListForUser(getCurUser().getId());// 我的考试信息
		List<Exam> examList = myExamService.getExamList(getCurUser().getId());// 考试信息
		Map<Integer, Exam> examCache = new HashMap<>();
		for (Exam exam : examList) {
			examCache.put(exam.getId(), exam);
		}
		
		// 统计结果
		int num = myExamList.size(), missNum = 0, succNum = 0, top = 9999, total = 0;
		double min = 9999, max = 0;
		BigDecimal sum = BigDecimal.ZERO;
		for (MyExam myExam : myExamList) {
			Exam curExam = examCache.get(myExam.getExamId());
			if (curExam == null) {// 考试已删除
				continue;
			}
			
			if (curExam.getMarkState() != 3) {// 整场考试未阅卷不统计（比如没有排名）
				continue;
			}
			
			missNum += (myExam.getState() == 1) ? 1 : 0;// 未考试加一
			succNum += (curExam.getScoreState() == 1 && myExam.getAnswerState() == 1) ? 1 : 0;//考试及格加一
			top = (curExam.getRankState() == 1) ? Math.min(myExam.getNo(), top) : top;// 有更靠前的名次则替换
			min = (curExam.getScoreState() == 1) ? Math.min(myExam.getTotalScore().doubleValue(), min) : min;// 最低分
			max = (curExam.getScoreState() == 1) ? Math.max(myExam.getTotalScore().doubleValue(), max) : max;// 最高分
			sum = (curExam.getScoreState() == 1) ? BigDecimalUtil.newInstance(sum).add(myExam.getTotalScore()).getResult() : sum;// 累加分
			total++;
		}
		top = top == 9999 ? 0 : top;// 没有参加过考试，排名为0  
		min = min == 9999 ? 0 : top;// 没有参加过考试，最低分为0
		
		// 拼接成接口的格式
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> userResult = new HashMap<String, Object>();
		userResult.put("id", user.getId());
		userResult.put("name", user.getName());
		userResult.put("headFileId", user.getHeadFileId());
		userResult.put("type", user.getType());
		result.put("user", userResult);
		
		Map<String, Object> orgResult = new HashMap<String, Object>();
		orgResult.put("id", org.getId());
		orgResult.put("name", org.getName());
		result.put("org", orgResult);
		
		Map<String, Object> examResult = new HashMap<String, Object>();
		examResult.put("num", num);
		examResult.put("missNum", missNum);
		examResult.put("succNum", succNum);
		examResult.put("top", top);
		result.put("exam", examResult);
		
		Map<String, Object> scoreResult = new HashMap<String, Object>();
		scoreResult.put("avg", total == 0 ? 0 : BigDecimalUtil.newInstance(sum).div(total, 2));
		scoreResult.put("min", min);
		scoreResult.put("max", max);
		result.put("score", scoreResult);
		return result;
	}

	@Override
	public Map<String, Object> homeSubAdmin() {
		Map<String, Object> result = new HashMap<String, Object>();
		User user = userService.getEntity(getCurUser().getId());// 用户信息
		Org org = orgService.getEntity(user.getOrgId());// 机构信息
		List<Exam> examList = examService.getList();// 考试信息
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("id", user.getId());
		data.put("name", user.getName());
		data.put("headFileId", user.getHeadFileId());
		data.put("type", user.getType());
		result.put("user", data);
		
		data = new HashMap<String, Object>();
		data.put("id", org.getId());
		data.put("name", org.getName());
		result.put("org", data);
		
		{
			int examNum = 0;
			for (Exam exam : examList) {
				if (exam.getUpdateUserId().intValue() == getCurUser().getId().intValue()) {
					examNum++;
				}
			}
			data = new HashMap<String, Object>();
			data.put("num", examNum);
			result.put("exam", data);
		}
		
//		{
//			List<Paper> paperList = paperService.getList();
//			int paperNum = 0;
//			for (Paper paper : paperList) {
//				if (paper.getCreateUserId().intValue() == getCurUser().getId().intValue()) {
//					paperNum++;
//				}
//			}
//			data = new HashMap<String, Object>();
//			data.put("num", paperNum);
//			result.put("paper", data);
//		}
		
		{
			data = new HashMap<String, Object>();
			data.put("num", reportDao.homeSubAdminQuestion(getCurUser().getId()));
			result.put("question", data);
		}
		
		{
//			List<MyMark> myMarkList = myMarkService.getListForMarkUser(getCurUser().getId());
//			Map<Integer, Exam> examCache = new HashMap<>();
//			for (Exam exam : examList) {
//				examCache.put(exam.getId(), exam);
//			}
//			int markNum = 0;
//			for (MyMark myMark : myMarkList) {
//				Exam curExam = examCache.get(myMark.getExamId());
//				if (curExam == null) {// 考试已删除
//					continue;
//				}
//				//if (curExam.getState() != 1) {// 考试未发布
//				//	continue;
//				//}
//				if (ValidateUtil.isValid(curExam.getMarkStartTime()) && curExam.getMarkState() != 3) {// 有阅卷时间就是主观试卷（和试卷中取状态效果一样）
//					markNum++;
//				}
//			}
//			
//			data = new HashMap<String, Object>();
//			data.put("num", markNum);
//			result.put("myMark", data);
		}
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
		data.put("num", reportDao.homeAdminBulletin());
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
		
		// 统计数据
		List<Question> questionList = questionService.getList(questionTypeId);
		Map<Integer, Integer> typeStatis = new HashMap<>();
		Map<Integer, Integer> markTypeStatis = new HashMap<>();
		for (Question question : questionList) {
			if (typeStatis.get(question.getType()) == null) {
				typeStatis.put(question.getType(), 0);
			}
			typeStatis.put(question.getType(), typeStatis.get(question.getType()) + 1);
			if (markTypeStatis.get(question.getMarkType()) == null) {
				markTypeStatis.put(question.getMarkType(), 0);
			}
			markTypeStatis.put(question.getMarkType(), markTypeStatis.get(question.getMarkType()) + 1);
		}
		
		// 组合成接口需要的格式
		Map<String, Object> result = new HashMap<>();
		List<Dict> typeDictList = dictService.getList("QUESTION_TYPE");
		List<Map<String, Object>> typeList = new ArrayList<Map<String, Object>>();//类型列表
		for (Dict typeDict : typeDictList) {
			Map<String, Object> typeResult = new HashMap<>();
			typeResult.put("name", typeDict.getDictKey());
			typeResult.put("value", typeStatis.get(Integer.parseInt(typeDict.getDictKey())) == null ? 0 : typeStatis.get(Integer.parseInt(typeDict.getDictKey())));
			typeList.add(typeResult);
		}
		result.put("typeList", typeList);
		
		List<Map<String, Object>> markTypeList = new ArrayList<Map<String, Object>>(); //智能列表
		for (int i = 1; i <= 2; i++) {
			Map<String, Object> markResult = new HashMap<>();
			markResult.put("name", i);
			markResult.put("value", markTypeStatis.get(i) == null ? 0 : markTypeStatis.get(i));
			markTypeList.add(markResult);
		}
		result.put("markTypeList", markTypeList);
		
		return result;
	}	
	
	@Override
	public Map<String, Object> examStatis(Integer examId) {
		//校验数据有效性
		if (!ValidateUtil.isValid(examId)) {
			throw new MyException("参数错误：examId");
		}
		Exam exam = examService.getEntity(examId);
		if (ValidateUtil.isValid(exam.getEndTime()) && exam.getEndTime().getTime() >= System.currentTimeMillis()) {
			throw new MyException("考试未结束");
		}
		if (ValidateUtil.isValid(exam.getMarkEndTime()) && exam.getMarkEndTime().getTime() >= System.currentTimeMillis()) {
			throw new MyException("阅卷未结束");
		}
		
		// 获取考试、试卷、人员成绩信息
		List<MyExam> myExamList = myExamService.getList(examId);
		List<Question> questionList = new ArrayList<>();
		List<ExamQuestion> examQuestionList = examQuestionService.getList(examId);
		if (exam.getGenType() == 1) {
			for (ExamQuestion examQuestion : examQuestionList) {
				questionList.add(questionService.getEntity(examQuestion.getQuestionId()));
			}
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
		scoreResult.put("total", exam.getTotalScore());// 考试总分 
		scoreResult.put("avg", 0.0);// 平均分
		scoreResult.put("min", exam.getTotalScore().doubleValue());// 默认最低分是0.0  最低分就会一直是0.0 
		scoreResult.put("max", 0.0);// 最高分
		
		for (MyExam myExam : myExamList) {
			if (myExam.getState() == 1) {// 排除掉未考试的
				continue;
			}
			
			scoreResult.put("min", Math.min(myExam.getTotalScore().doubleValue(), (double)scoreResult.get("min")));
			scoreResult.put("max", Math.max(myExam.getTotalScore().doubleValue(), (double)scoreResult.get("max")));
			scoreResult.put("avg", BigDecimalUtil.newInstance(scoreResult.get("avg")).add(  myExam.getTotalScore()).getResult().doubleValue());
		}
		
		if (exam.getTotalScore().compareTo(new BigDecimal(scoreResult.get("min").toString())) == 0) { //最小值和总分一样 默认赋值0.0
			scoreResult.put("min",0.0);
		}
		
		if (myExamList.size() != 0) {// 被除数不能为0
			scoreResult.put("avg", BigDecimalUtil.newInstance(scoreResult.get("avg")).div(myExamList.size(), 2).getResult().doubleValue());
		}
		result.put("score", scoreResult);
		
		// 统计试题类型占比
		List<Dict> dictList = dictService.getList("QUESTION_TYPE");
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
		double scoreGrade = BigDecimalUtil.newInstance(exam.getTotalScore()).div(10, 2).getResult().doubleValue();// 分数保留两位小数
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
		if (ValidateUtil.isValid(pageIn.get("questionId")) && ValidateUtil.isValid(pageIn.get("state"))) {//试题id不为空，默认查询错误人员
			pageIn.addAttr("state", 2);
		}
		Exam exam = examService.getEntity(examId);
		
//		ExamType
		
		if (exam.getMarkEndTime() != null && exam.getMarkEndTime().getTime() >= System.currentTimeMillis()) {
			throw new MyException("阅卷时间未结束");
		}
		
		PageOut pageOut = reportDao.myExamListpage(pageIn);
		for(Map<String, Object> map : pageOut.getList()){
			map.put("myExamStartTime", map.get("myExamStartTime") == null ? null : DateUtil.formatDateTime(DateUtil.getDateTime(map.get("myExamStartTime").toString())));
			map.put("myExamEndTime", map.get("myExamEndTime") == null ? null : DateUtil.formatDateTime(DateUtil.getDateTime(map.get("myExamEndTime").toString())));
			map.put("myExamMarkStartTime", map.get("myExamMarkStartTime") == null ? null : DateUtil.formatDateTime(DateUtil.getDateTime(map.get("myExamMarkStartTime").toString())));
			map.put("myExamMarkEndTime", map.get("myExamMarkEndTime") == null ? null : DateUtil.formatDateTime(DateUtil.getDateTime(map.get("myExamMarkEndTime").toString())));
			
			map.put("examTotalScore", exam.getTotalScore());
			map.put("examPassScore", exam.getPassScore());
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
		
//		ExamType
		
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