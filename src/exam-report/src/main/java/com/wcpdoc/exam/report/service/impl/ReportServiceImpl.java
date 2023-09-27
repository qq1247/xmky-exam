package com.wcpdoc.exam.report.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.wcpdoc.base.entity.Dict;
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
import com.wcpdoc.exam.core.cache.QuestionCache;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.ExamQuestion;
import com.wcpdoc.exam.core.entity.ExamRule;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.service.ExamQuestionService;
import com.wcpdoc.exam.core.service.ExamRuleService;
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
	@Resource
	private ExamRuleService examRuleService;
	
	@Override
	public void setDao(BaseDao<Object> dao) {}
	
	@Override
	public Map<String, Object> userHome() {
		return reportDao.userHome(getCurUser().getId());
	}

	@Override
	public Map<String, Object> adminHome() {
		return reportDao.adminHome();
	}
	
	@Override
	public Map<String, Object> subAdminHome() {
		return reportDao.subAdminHome(getCurUser().getId());
	}
	
	@Override
	public Map<String, Object> markUserHome() {
		return reportDao.markUserHome(getCurUser().getId());
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
		// 校验数据有效性
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
		if (exam.getMarkState() != 3) {
			throw new MyException("阅卷未结束");
		}
		
		// 获取试卷、人员成绩信息
		List<MyExam> myExamList = myExamService.getList(examId);// 我的考试信息
		List<Question> questionList = new ArrayList<>();// 试题列表
		List<ExamQuestion> examQuestionList = examQuestionService.getList(examId);// 试卷信息
		List<ExamRule> examRuleList = null;// 考试规则列表
		if (exam.getGenType() == 1) {// 如果是人工组卷，查询试题
			for (ExamQuestion examQuestion : examQuestionList) {
				if (examQuestion.getType() == 2) {
					questionList.add(QuestionCache.getQuestion(examQuestion.getQuestionId()));// 已关联考试的试题不会改变，缓存起来加速查询。
				}
			}
		} else {// 如果是随机组卷，查询随机规则
			examRuleList = examRuleService.getList(examId);
		}
		
		// 统计考试信息
		Map<String, Object> result = new HashMap<String, Object>();
		{
			int userNum = myExamList.size();// 考试人数
			int missUserNum = 0; // 未考试人数
			int failUserNum = 0;// 不及格人数
			for (MyExam myExam : myExamList) {
				missUserNum += myExam.getState() == 1 ? 1 : 0;
				failUserNum += myExam.getAnswerState() == 2 ? 1 : 0;
			}
			result.put("userNum", userNum);
			result.put("missUserNum", missUserNum);
			result.put("failUserNum", failUserNum);
		}
		
		{
			int questionNum = 0;// 试题数量
			int objectiveQuestionNum = 0;// 客观题数量
			if (exam.getGenType() == 1) {
				questionNum = questionList.size();
				for (Question question : questionList) {
					objectiveQuestionNum += question.getMarkType() == 1 ? 1 : 0;
				}
			} else {
				for (ExamRule examRule : examRuleList) {
					if (examRule.getType() == 2) {
						questionNum += examRule.getNum();
						objectiveQuestionNum += examRule.getMarkType() == 1 ? examRule.getNum() : 0;
					}
				}
			}
			result.put("questionNum", questionNum);
			result.put("objectiveQuestionNum", objectiveQuestionNum);
		}
		
		{
			List<Double> scoreList = new ArrayList<>();
			for (MyExam myExam : myExamList) {
				if (myExam.getState() == 1) {// 排除掉未考试的
					continue;
				}
				
				scoreList.add(myExam.getTotalScore().doubleValue());
			}
			
			if (!ValidateUtil.isValid(scoreList)) {
				result.put("avgScore", 0);
				result.put("minScore", 0);
				result.put("maxScore", 0);
				result.put("sdScore", 0);
			} else {
				double[] scores = new double[scoreList.size()];
				for (int i = 0; i < scoreList.size(); i++) {
					scores[i] = scoreList.get(i).doubleValue();
				}
				
				StandardDeviation standardDeviation = new StandardDeviation(false);
				result.put("avgScore", BigDecimalUtil.newInstance(StatUtils.mean(scores)).div(1, 1).getResult());
				result.put("minScore", StatUtils.min(scores));
				result.put("maxScore", StatUtils.max(scores));
				result.put("sdScore", BigDecimalUtil.newInstance(standardDeviation.evaluate(scores)).div(1, 0).getResult());
			}
		}
		
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
		
		if (exam.getGenType() == 1) {
			for (Question question : questionList) {
				Map<String, Object> map = typeCache.get(question.getType().toString());
				map.put("value", (int)map.get("value") + 1);// 按分类累加
			}
		} else {
			for (ExamRule examRule : examRuleList) {
				if (examRule.getType() == 2) {
					Map<String, Object> map = typeCache.get(examRule.getQuestionType().toString());
					map.put("value", (int)map.get("value") + examRule.getNum());// 按分类累加
				}
			}
		}
		
		for (Map<String, Object> typeResult : typeResultList) {
			typeResult.remove("key");// 接口没有这个字段，移除掉
		}
		result.put("questionTypeList", typeResultList);
		
		// 统计分数段占比
		int scoreGrade = BigDecimalUtil.newInstance(exam.getTotalScore()).div(5, 0).getResult().intValue();// 分数分成5等分进行人数统计（如：20,40,60,80,100）
		List<Map<String, Object>> scoreGradeResultList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < 5; i++) {
			Map<String, Object> map = new HashMap<>();
			int minScore = scoreGrade * (i);
			int maxScore = scoreGrade * (i + 1);
			int userNum = 0;
			for (MyExam myExam : myExamList) {
				if (myExam.getState() == 1) {// 排除掉未考试的
					continue;
				}
				
				if (i < 4) {
					if (myExam.getTotalScore().doubleValue() >= minScore 
						&& myExam.getTotalScore().doubleValue() < maxScore) {// 不包括最大边界值，如60不属于50分数段，[50-60)
						userNum++;
					}
				} else if (myExam.getTotalScore().doubleValue() >= minScore // 最后的100分包含进去,[80-100]
							&& myExam.getTotalScore().doubleValue() <= maxScore) {
					userNum++;
				}
			}
			
			map.put("name", "<" + maxScore);// 名称
			if (i == 4) {
				map.put("name", "<=" + maxScore);
			}
			map.put("value", userNum);// 人数
			scoreGradeResultList.add(map);
		}
		
		result.put("scoreGradeList", scoreGradeResultList);
		return result;
	}
	
	@Override
	public PageOut examRankListpage(PageIn pageIn) {
		return reportDao.examRankListpage(pageIn);
	}
	
	@Override
	public List<Map<String, Object>> questionErrList(Integer examId) {
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
		if (exam.getMarkState() != 3) {
			throw new MyException("阅卷未结束");
		}
		
		// 错题统计
		return reportDao.questionErrList(examId);
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
	
	public static void main(String[] args) {
		double[] values = new double[] {  };
        System.out.println("个    数：" +values.length);
        System.out.println("平均数：" + StatUtils.mean(values));
        System.out.println("相加值：" + StatUtils.sum(values));
        System.out.println("最小值：" + StatUtils.min(values));
        System.out.println("最大值：" + StatUtils.max(values));
       
		StandardDeviation standardDeviation =new StandardDeviation(false);
		System.out.println("标准差：" + standardDeviation.evaluate(values));
	}

}