package com.wcpdoc.exam.report.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wcpdoc.base.entity.Dict;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.base.service.DictService;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.BigDecimalUtil;
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.ExamQuestion;
import com.wcpdoc.exam.core.entity.ExamRule;
import com.wcpdoc.exam.core.entity.Exer;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyMark;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.service.ExamCacheService;
import com.wcpdoc.exam.core.service.ExamQuestionService;
import com.wcpdoc.exam.core.service.ExamRuleService;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.ExerService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyMarkService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionTypeService;
import com.wcpdoc.exam.report.service.ReportService;

/**
 * 统计服务层实现
 * 
 * v1.0 zhanghc 2017年8月29日下午2:03:36
 */
@Service
public class ReportServiceImpl extends BaseServiceImp<Object> implements ReportService {

	@Resource
	private QuestionTypeService questionTypeService;
	@Resource
	private MyMarkService myMarkService;
	@Resource
	private DictService dictService;
	@Resource
	private BaseCacheService baseCacheService;
	@Resource
	private ExamQuestionService examQuestionService;
	@Resource
	private ExamRuleService examRuleService;
	@Resource
	private ExerService exerService;
	@Resource
	private ExamCacheService examCacheService;
	@Resource
	private ExamService examService;
	@Resource
	private MyExamService myExamService;
	@Resource
	private QuestionService questionService;
	@Resource
	private UserService userService;

	@Override
	public RBaseDao<Object> getDao() {
		return null;
	}

	@Override
	public Map<String, Object> userHome() {
		// 首页展示我的考试场数，只统计已发布的考试
		Integer curUserId = getCurUser().getId();
		Map<Integer, Exam> examCache = examService.list(new LambdaQueryWrapper<Exam>()//
				.eq(Exam::getState, 1))//
				.stream()//
				.collect(Collectors.toMap(Exam::getId, Function.identity()));
		List<MyExam> myExamList = myExamService.list(new LambdaQueryWrapper<MyExam>()//
				.eq(MyExam::getUserId, curUserId))//
				.stream()//
				.filter(myExam -> examCache.get(myExam.getExamId()) != null)//
				.collect(Collectors.toList());

		// 首页展示我的及格次数，只统计考试结束，并且成绩公开的考试
		List<MyExam> myExamOfPassList = myExamList.stream()//
				.filter(myExam -> examCache.get(myExam.getExamId()).getMarkState() == 3
						&& examCache.get(myExam.getExamId()).getScoreState() != 2 && myExam.getAnswerState() == 1)//
				.collect(Collectors.toList());

		// 首页展示我的最高排名，只统计考试结束，并且排名公开的考试
		MyExam myExamOfTopRank = myExamList.stream()//
				.filter(myExam -> examCache.get(myExam.getExamId()).getMarkState() == 3
						&& examCache.get(myExam.getExamId()).getRankState() == 1)//
				.min(Comparator.comparingInt(MyExam::getNo)).orElse(null);

		// 首页展示我的练习数量
		List<Exer> exerList = exerService.list(new LambdaQueryWrapper<Exer>()//
				.eq(Exer::getState, 1)//
				.and(c -> c.like(Exer::getUserIds, String.format("%%,%s,%%", curUserId)).or()
						.isNull(Exer::getUserIds)));

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("examNum", myExamList.size());
		result.put("exerNum", exerList.size());
		result.put("passExamNum", myExamOfPassList.size());
		result.put("topRank", myExamOfTopRank == null ? 0 : myExamOfTopRank.getNo());
		return result;
	}

	@Override
	public Map<String, Object> adminHome() {
		// 首页展示考试场数，只统计已发布的考试，包括管理员和子管理员创建的考试
		long examNum = examService.count(new LambdaQueryWrapper<Exam>()//
				.in(Exam::getState, 1, 2));
		// 首页展示试题数量，包括管理员和子管理员创建的试题
		long questionNum = questionService.count(new LambdaQueryWrapper<Question>()//
				.eq(Question::getState, 1));
		// 首页展示练习数量，包括管理员和子管理员创建的练习
		long exerNum = exerService.count(new LambdaQueryWrapper<Exer>()//
				.eq(Exer::getState, 1));
		// 首页展示考试用户数量，包括管理员和子管理员创建的考试用户
		long userNum = userService.count(new LambdaQueryWrapper<User>()//
				.in(User::getState, 1, 2)//
				.eq(User::getType, 1));

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("examNum", examNum);
		result.put("questionNum", questionNum);
		result.put("exerNum", exerNum);
		result.put("userNum", userNum);
		return result;
	}

	@Override
	public Map<String, Object> subAdminHome() {
		// 首页展示考试场数，只统计已发布的考试
		Integer subAdminUserId = getCurUser().getId();
		long examNum = examService.count(new LambdaQueryWrapper<Exam>()//
				.in(Exam::getState, 1, 2)//
				.eq(Exam::getCreateUserId, subAdminUserId));
		// 首页展示试题数量
		long questionNum = questionService.count(new LambdaQueryWrapper<Question>()//
				.eq(Question::getState, 1)//
				.eq(Question::getCreateUserId, subAdminUserId));
		// 首页展示练习数量
		long exerNum = exerService.count(new LambdaQueryWrapper<Exer>()//
				.eq(Exer::getState, 1)//
				.eq(Exer::getCreateUserId, subAdminUserId));
		// 首页展示考试用户数量
		int examUserNum = baseCacheService.getUser(subAdminUserId).getUserIds().size();

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("examNum", examNum);
		result.put("questionNum", questionNum);
		result.put("exerNum", exerNum);
		result.put("userNum", examUserNum);
		return result;
	}

	@Override
	public Map<String, Object> markUserHome() {
		// 首页展示总考试场数
		Map<Integer, Exam> examCache = examService.list(new LambdaQueryWrapper<Exam>()//
				.eq(Exam::getState, 1))//
				.stream()//
				.collect(Collectors.toMap(Exam::getId, Function.identity()));
		Integer markUserId = getCurUser().getId();
		List<MyMark> myMakrList = myMarkService.list(new LambdaQueryWrapper<MyMark>()//
				.eq(MyMark::getMarkUserId, markUserId))//
				.stream()//
				.filter(myMark -> examCache.get(myMark.getExamId()) != null)//
				.collect(Collectors.toList());

		// 首页展示未阅卷场数
		List<MyMark> myMarkOfUnMarkList = myMakrList.stream()//
				.filter(myMark -> examCache.get(myMark.getExamId()).getMarkState() != 3)//
				.collect(Collectors.toList());
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("unMarkNum", myMarkOfUnMarkList.size());
		result.put("examNum", myMakrList.size());
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
		Long curReadLen = fileLen - 10000;// 只读取最后10000字节的日志
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
		// 数据校验
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
		List<Map<String, Object>> typeList = new ArrayList<Map<String, Object>>();// 类型列表
		for (Dict typeDict : typeDictList) {
			Map<String, Object> typeResult = new HashMap<>();
			typeResult.put("name", typeDict.getDictKey());
			typeResult.put("value", typeStatis.get(Integer.parseInt(typeDict.getDictKey())) == null ? 0
					: typeStatis.get(Integer.parseInt(typeDict.getDictKey())));
			typeList.add(typeResult);
		}
		result.put("typeList", typeList);

		List<Map<String, Object>> markTypeList = new ArrayList<Map<String, Object>>(); // 智能列表
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
		// 数据校验
		if (!ValidateUtil.isValid(examId)) {
			throw new MyException("参数错误：examId");
		}
		Exam exam = examCacheService.getExam(examId);
		if (ValidateUtil.isValid(exam.getEndTime()) && exam.getEndTime().getTime() >= System.currentTimeMillis()) {
			throw new MyException("考试未结束");
		}
		if (ValidateUtil.isValid(exam.getMarkEndTime())
				&& exam.getMarkEndTime().getTime() >= System.currentTimeMillis()) {
			throw new MyException("阅卷未结束");
		}
		if (exam.getMarkState() != 3) {
			throw new MyException("阅卷未结束");
		}

		// 获取试卷、人员成绩信息
		List<MyExam> myExamList = examCacheService.getMyExamList(examId);// 我的考试信息
		List<Question> questionList = new ArrayList<>();// 试题列表
		List<ExamQuestion> examQuestionList = examQuestionService.getList(examId);// 试卷信息
		List<ExamRule> examRuleList = null;// 考试规则列表
		if (exam.getGenType() == 1) {// 如果是人工组卷，查询试题
			for (ExamQuestion examQuestion : examQuestionList) {
				if (examQuestion.getType() == 2) {
					questionList.add(examCacheService.getQuestion(examQuestion.getQuestionId()));
				}
			}
		} else {// 如果是随机组卷，查询考试规则
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
				result.put("sdScore",
						BigDecimalUtil.newInstance(standardDeviation.evaluate(scores)).div(1, 0).getResult());
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
				map.put("value", (int) map.get("value") + 1);// 按分类累加
			}
		} else {
			for (ExamRule examRule : examRuleList) {
				if (examRule.getType() == 2) {
					Map<String, Object> map = typeCache.get(examRule.getQuestionType().toString());
					map.put("value", (int) map.get("value") + examRule.getNum());// 按分类累加
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
		Page<Map<String, Object>> page = myExamService.selectJoinMapsPage(pageIn.toPage(), //
				new MPJQueryWrapper<MyExam>().setAlias("MY_EXAM")// 我的考试
						.innerJoin("EXM_EXAM EXAM ON MY_EXAM.EXAM_ID = EXAM.ID")// 考试用
						.innerJoin("SYS_USER USER ON MY_EXAM.USER_ID = USER.ID")// 考试用户
						.leftJoin("SYS_USER MARK_USER ON MY_EXAM.MARK_USER_ID = MARK_USER.ID")// 阅卷用户
						.leftJoin("SYS_ORG ORG ON USER.ORG_ID = ORG.ID")// 机构名称
						.select("MY_EXAM.EXAM_ID, MY_EXAM.NO AS MY_EXAM_NO", // 排名
								"USER.ID AS USER_ID", "USER.NAME AS USER_NAME", "ORG.NAME AS ORG_NAME", // 用户机构信息
								"MY_EXAM.STATE AS MY_EXAM_STATE", "MY_EXAM.MARK_STATE AS MY_EXAM_MARK_STATE",
								"MY_EXAM.ANSWER_STATE AS MY_EXAM_ANSWER_STATE", // 考试状态信息
								"MY_EXAM.EXAM_START_TIME AS MY_EXAM_START_TIME",
								"MY_EXAM.EXAM_END_TIME AS MY_EXAM_END_TIME", // 答题时间
								"MY_EXAM.MARK_START_TIME AS MY_EXAM_MARK_START_TIME",
								"MY_EXAM.MARK_END_TIME AS MY_EXAM_MARK_END_TIME", // 阅卷时间
								"MY_EXAM.TOTAL_SCORE AS MY_EXAM_TOTAL_SCORE", // 用户分数
								"EXAM.MARK_TYPE AS EXAM_MARK_TYPE", "EXAM.MARK_STATE AS EXAM_MARK_STATE")// 考试信息，用于判断如果是主观题试卷，分数应该考试结束才显示
						.eq("MY_EXAM.EXAM_ID", pageIn.getParm("examId"))//
						.orderByAsc("MY_EXAM.NO").orderByDesc("MY_EXAM.EXAM_END_TIME"));
		return new PageOut(page.getRecords(), page.getTotal());
	}

}