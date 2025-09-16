package com.wcpdoc.exam.core.service.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.base.util.CurLoginUserUtil;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.lock.ReadWriteLockManager;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.constant.ExamConstant;
import com.wcpdoc.exam.core.dao.MyExerDao;
import com.wcpdoc.exam.core.entity.Exer;
import com.wcpdoc.exam.core.entity.MyExer;
import com.wcpdoc.exam.core.entity.MyExerQuestion;
import com.wcpdoc.exam.core.entity.MyExerTrack;
import com.wcpdoc.exam.core.entity.MyExerTrackMonthly;
import com.wcpdoc.exam.core.entity.MyQuestion;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.service.ExamCacheService;
import com.wcpdoc.exam.core.service.ExerService;
import com.wcpdoc.exam.core.service.MyExerQuestionService;
import com.wcpdoc.exam.core.service.MyExerService;
import com.wcpdoc.exam.core.service.MyExerTrackMonthlyService;
import com.wcpdoc.exam.core.service.MyExerTrackService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.util.MyExamUtil;
import com.wcpdoc.exam.core.util.QuestionUtil;

/**
 * 我的练习服务层实现
 * 
 * v1.0 chenyun 2021-03-02 13:43:21
 */
@Service
public class MyExerServiceImpl extends BaseServiceImp<MyExer> implements MyExerService {
	@Resource
	private MyExerDao myExerDao;
	@Resource
	private ExerService exerService;
	@Resource
	private BaseCacheService baseCacheService;
	@Resource
	private QuestionService questionService;
	@Resource
	private MyExerQuestionService myExerQuestionService;
	@Resource
	private ExamCacheService examCacheService;
	@Resource
	private ReadWriteLockManager readWriteLockManager;
	@Resource
	private CacheManager cacheManager;
	@Resource
	private MyExerTrackService myExerTrackService;
	@Resource
	private MyExerTrackMonthlyService myExerTrackMonthlyService;

	@Override
	public RBaseDao<MyExer> getDao() {
		return myExerDao;
	}

	@Override
	public Map<String, Object> pull(Integer exerId, Integer userId) {
		// 数据校验
		if (!ValidateUtil.isValid(exerId)) {
			throw new MyException("参数错误：exerId");
		}
		if (!ValidateUtil.isValid(userId)) {
			throw new MyException("参数错误：userId");
		}
		Exer exer = exerService.getById(exerId);
		if (exer == null) {
			throw new MyException("参数错误：exerId");
		}
		if (exer.getState() == 0) {
			throw new MyException("练习已删除");
		}
		if (exer.getState() == 2) {
			throw new MyException("练习已暂停");
		}
		User user = baseCacheService.getUser(userId);
		if (!(exer.getUserIds().contains(user.getId()) || exer.getOrgIds().contains(user.getOrgId()))) {
			throw new MyException("无操作权限");
		}

		// 拉取最新题库数据
		List<MyExerQuestion> myExerQuestionList = myExerQuestionService.getList(exerId, userId);
		Set<Integer> oldQuestionIds = myExerQuestionList.stream().map(MyExerQuestion::getQuestionId)
				.collect(Collectors.toSet());
		List<Question> newQuestionList = questionService.getList(exer.getQuestionBankId()).stream()//
				.filter(question -> question.getState() == 1)//
				.filter(question -> !oldQuestionIds.contains(question.getId())).collect(Collectors.toList());

		// 同步到我的练习
		int curNo = myExerQuestionList.size();
		List<Integer> shuffleNos = IntStream.rangeClosed(curNo + 1, curNo + newQuestionList.size()).boxed()
				.collect(Collectors.toList());// 第一次拉取到100道题，第二次新拉取到5道题，新拉取试题排序为101-105进行随机
		Collections.shuffle(shuffleNos);
		for (int i = 0; i < newQuestionList.size(); i++) {
			Question question = newQuestionList.get(i);
			List<QuestionAnswer> questionAnswerList = examCacheService.getQuestionAnswerList(question.getId());
			MyExerQuestion myExerQuestion = new MyExerQuestion();
			myExerQuestion.setExerId(exerId);
			myExerQuestion.setUserId(userId);
			myExerQuestion.setType(question.getType());
			myExerQuestion.setQuestionId(question.getId());
			myExerQuestion.setNo(curNo + i + 1);
			myExerQuestion.setShuffleNo(shuffleNos.get(i));
			myExerQuestion.setScore(question.getScore());
			if (QuestionUtil.hasMultipleChoice(question) || QuestionUtil.hasFillBlank(question)
					|| (QuestionUtil.hasQA(question) && QuestionUtil.hasObjective(question))) {// 只有多选、填空、客观问答才有子分数
				myExerQuestion.setScores(
						questionAnswerList.stream().map(QuestionAnswer::getScore).collect(Collectors.toList()));
			}
			myExerQuestion.setMarkOptions(question.getMarkOptions());
			myExerQuestion.setFav(2);// 默认不收藏
			myExerQuestion.setWrongAnswerNum(0);// 默认答错次数为0
			myExerQuestion.setUpdateUserId(getCurUser().getId());
			myExerQuestion.setUpdateTime(new Date());
			myExerQuestionService.save(myExerQuestion);
		}

		// 统计题型数量
		Map<Integer, Long> typeNumMap = Stream
				.concat(myExerQuestionList.stream().map(MyExerQuestion::getType),
						newQuestionList.stream().map(Question::getType))
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		long wrongAnswerNum = myExerQuestionList.stream()
				.filter(myExerQuestion -> myExerQuestion.getUserScore() != null
						&& myExerQuestion.getScore().doubleValue() != myExerQuestion.getUserScore().doubleValue())
				.count();
		long favNum = myExerQuestionList.stream().filter(myExerQuestion -> myExerQuestion.getFav() == 1).count();

		Map<String, Object> result = new HashMap<>();
		result.put("questionTypeStatis", typeNumMap);// 页面显示那个题型多少道题
		result.put("questionBankUpdateNum", newQuestionList.size());// 距上次练习题库更新了多少道题
		result.put("wrongAnswerNum", wrongAnswerNum);// 历史错题数量
		result.put("favNum", favNum);// 我的收藏数量
		return result;
	}

	@Override
	public List<MyExerQuestion> generate(Integer exerId, Integer userId, Integer type) {
		// 数据校验
		if (!ValidateUtil.isValid(exerId)) {
			throw new MyException("参数错误：exerId");
		}
		if (!ValidateUtil.isValid(userId)) {
			throw new MyException("参数错误：userId");
		}
		if (!ValidateUtil.isValid(type)) {
			throw new MyException("参数错误：type");
		}
		Exer exer = exerService.getById(exerId);
		if (exer == null) {
			throw new MyException("参数错误：exerId");
		}
		if (exer.getState() == 0) {
			throw new MyException("已删除");
		}
		if (exer.getState() == 2) {
			throw new MyException("已暂停");
		}
		User user = baseCacheService.getUser(userId);
		if (!(exer.getUserIds().contains(user.getId()) || exer.getOrgIds().contains(user.getOrgId()))) {
			throw new MyException("无操作权限");
		}

		// 生成我的练习
		if (type >= 1 && type <= 5) {
			long count = myExerDao.getList(exerId, getCurUser().getId()).stream()
					.filter(myExer -> myExer.getType().intValue() == type.intValue()).count();
			if (count == 0) {// 为每个题型生成一个练习（更科学），用于第二次查看进度，并继续训练。
				MyExer myExer = new MyExer();
				myExer.setExerId(exerId);
				myExer.setUserId(getCurUser().getId());
				myExer.setType(type);
				myExer.setUpdateTime(new Date());
				myExer.setUpdateUserId(getCurUser().getId());
				save(myExer);
			}
			return myExerQuestionService.getList(exerId, getCurUser().getId()).stream()
					.filter(myExerQuestion -> myExerQuestion.getType().intValue() == type).collect(Collectors.toList());
		}

		if (type == 11) {// 进入历史错误
			return myExerQuestionService.getList(exerId, getCurUser().getId()).stream()
					.filter(myExerQuestion -> myExerQuestion.getUserScore() != null
							&& myExerQuestion.getUserScore().doubleValue() != myExerQuestion.getScore().doubleValue())
					.collect(Collectors.toList());
		}

		if (type == 12) {// 进入我的收藏
			return myExerQuestionService.getList(exerId, getCurUser().getId()).stream()
					.filter(myExerQuestion -> myExerQuestion.getFav().intValue() == 1).collect(Collectors.toList());
		}
		return null;
	}

	@Override
	public List<MyExer> getList(Integer exerId, Integer userId) {
		return myExerDao.getList(exerId, userId);
	}

	@Override
	public BigDecimal answer(Integer exerId, Integer userId, Integer questionId, String[] userAnswers,
			BigDecimal userScore) {
		// 数据校验
		if (!ValidateUtil.isValid(exerId)) {
			throw new MyException("参数错误：exerId");
		}
		if (!ValidateUtil.isValid(userId)) {
			throw new MyException("参数错误：userId");
		}
		if (!ValidateUtil.isValid(questionId)) {
			throw new MyException("参数错误：questionId");
		}
		MyExerQuestion myExerQuestion = myExerQuestionService.getMyExerQuestion(exerId, userId, questionId);
		if (myExerQuestion == null) {
			throw new MyException("无操作权限");
		}
		Question question = examCacheService.getQuestion(myExerQuestion.getQuestionId());
		if (QuestionUtil.hasSubjective(question)) {// 主观题需要自评分数
			if (!ValidateUtil.isValid(userScore)) {
				throw new MyException("参数错误：userScore");
			}
			if (userScore.doubleValue() < 0) {
				throw new MyException("自评最低0分");
			}
			if (userScore.doubleValue() > question.getScore().doubleValue()) {
				throw new MyException(String.format("自评最高%s分", question.getScore().doubleValue()));
			}
		}

//		if (ValidateUtil.isValid(myExerQuestion.getUserAnswer())) {// 不知道答案作答，答案为空，分数为0。使用userScore更准确
		if (ValidateUtil.isValid(myExerQuestion.getUserScore())) {
			throw new MyException("已作答");
		}

		// 阅题
		List<QuestionAnswer> questionAnswerList = examCacheService
				.getQuestionAnswerList(myExerQuestion.getQuestionId());

		MyQuestion myQuestion = new MyQuestion();
		myQuestion.setScore(myExerQuestion.getScore());
		myQuestion.setScores(myExerQuestion.getScores());
		myQuestion.setMarkOptions(myExerQuestion.getMarkOptions());

		if (!ValidateUtil.isValid(userAnswers)) {
			myQuestion.setUserAnswer(null);
		} else if (QuestionUtil.hasJudge(question)) {
			myQuestion.setUserAnswer(userAnswers[0]);
		} else if (QuestionUtil.hasSingleChoice(question)) {
			myQuestion.setUserAnswer(userAnswers[0]);
		} else if (QuestionUtil.hasMultipleChoice(question)) {
			myQuestion.setUserAnswer(StringUtil.join(userAnswers));
		} else if (QuestionUtil.hasFillBlank(question)) {
			myQuestion.setUserAnswer(StringUtil.join(userAnswers, '\n'));
		} else if (QuestionUtil.hasQA(question)) {
			myQuestion.setUserAnswer(StringUtil.join(userAnswers));// bug：文本包含英文逗号会分割
		}

		if (QuestionUtil.hasQA(question)) {
			MyExamUtil.qAHandle(question, questionAnswerList, myQuestion);// 问答处理
		} else if (QuestionUtil.hasSingleChoice(question) || QuestionUtil.hasJudge(question)) {
			MyExamUtil.singleChoiceHandle(question, questionAnswerList, myQuestion);// 单选判断处理
		} else if (QuestionUtil.hasMultipleChoice(question)) {
			MyExamUtil.multipleChoiceHandle(question, questionAnswerList, myQuestion);// 多选处理
		} else if (QuestionUtil.hasFillBlank(question)) {
			MyExamUtil.fillBlankHandle(question, questionAnswerList, myQuestion);// 填空处理
		}

		myExerQuestion.setAnswerTime(new Date());
		myExerQuestion.setUserAnswer(myQuestion.getUserAnswer());
		myExerQuestion.setUserScore(QuestionUtil.hasSubjective(question) ? userScore : myQuestion.getUserScore());// 主观题需要自评
		myExerQuestion.setUpdateUserId(getCurUser().getId());
		myExerQuestion.setUpdateTime(new Date());
		if (myExerQuestion.getScore().doubleValue() != myExerQuestion.getUserScore().doubleValue()) {
			myExerQuestion.setWrongAnswerNum(myExerQuestion.getWrongAnswerNum() + 1);
		}
		myExerQuestionService.updateById(myExerQuestion);
		return myExerQuestion.getUserScore();
	}

	@Override
	public void exerReset(Integer exerId, Integer userId, Integer type) {
		// 数据校验
		if (!ValidateUtil.isValid(exerId)) {
			throw new MyException("参数错误：exerId");
		}
		if (!ValidateUtil.isValid(userId)) {
			throw new MyException("参数错误：userId");
		}
		if (!ValidateUtil.isValid(type)) {
			throw new MyException("参数错误：type");
		}
		Exer exer = exerService.getById(exerId);
		if (exer == null) {
			throw new MyException("参数错误：exerId");
		}
		if (exer.getState() == 0) {
			throw new MyException("已删除");
		}
		if (exer.getState() == 2) {
			throw new MyException("已暂停");
		}
		User user = baseCacheService.getUser(userId);
		if (!(exer.getUserIds().contains(user.getId()) || exer.getOrgIds().contains(user.getOrgId()))) {
			throw new MyException("无操作权限");
		}

		// 重新练习
		myExerQuestionService.getList(exerId, userId).stream()//
				.filter(myExerQuestion -> myExerQuestion.getType().intValue() == type)//
				.forEach(myExerQuestion -> {
					myExerQuestion.setAnswerTime(null);
					myExerQuestion.setUserAnswer(null);
					myExerQuestion.setUserScore(null);
					// myExerQuestion.setWrongAnswerNum(null);//单独设置
					myExerQuestion.setUpdateUserId(getCurUser().getId());
					myExerQuestion.setUpdateTime(new Date());
					myExerQuestionService.updateById(myExerQuestion);
				});
	}

	@SuppressWarnings("unchecked")
	@Override
	public void track(Integer exerId, Integer userId, Integer type) {
		// 数据校验
		if (!ValidateUtil.isValid(exerId)) {
			throw new MyException("参数错误：exerId");
		}
		if (!ValidateUtil.isValid(userId)) {
			throw new MyException("参数错误：userId");
		}
		if (!ValidateUtil.isValid(type)) {
			throw new MyException("参数错误：type");
		}
		if (type != 1 && type != 2 && type != 3 && type != 4 && type != 5 && type != 11 && type != 12) {
			throw new MyException("参数错误：type");
		}

		Exer exer = exerService.getById(exerId);
		if (exer == null) {
			throw new MyException("参数错误：exerId");
		}
		if (exer.getState() == 0) {
			throw new MyException("已删除");
		}
		if (exer.getState() == 2) {
			throw new MyException("已暂停");
		}
		User user = baseCacheService.getUser(userId);
		if (!(exer.getUserIds().contains(user.getId()) || exer.getOrgIds().contains(user.getOrgId()))) {
			throw new MyException("无操作权限");
		}

		/**
		 * 跟踪 <br/>
		 * 1：为防止高并发，先加入缓存。<br/>
		 * 2：定时器每5分钟把缓存数据批量落库。如果服务器重启，最多会丢失5分钟数据，在可以容忍范围内。<br/>
		 */
		Cache cache = cacheManager.getCache(ExamConstant.EXER_TIME_CACHE);
		String cacheKey = String.format("%s:%s:%s:%s", exerId, userId, type,
				DateUtil.formatDateCustom(new Date(), "yyyyMMdd"));// 3:5:1:20250908
		Lock writeLock = readWriteLockManager.getLock(cacheKey).writeLock();
		writeLock.lock();
		try {
			if (cache.get(cacheKey) == null) {
				cache.put(cacheKey, new HashSet<Integer>());
			}
			int minuteOfDay = DateUtil.getMinuteOfDay(new Date());
			cache.get(cacheKey, Set.class).add(minuteOfDay); // 去重，多次重复上报也没事
		} catch (Exception e) {
			log.error("加锁异常：", e);
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public List<MyExerTrack> getTrackList(Integer exerId, Integer userId, String startDate, String endDate) {
		// 数据校验
		if (!ValidateUtil.isValid(exerId)) {
			throw new MyException("参数错误：exerId");
		}
		if (!ValidateUtil.isValid(userId)) {
			throw new MyException("参数错误：userId");
		}
		if (!ValidateUtil.isValid(startDate)) {
			throw new MyException("参数错误：startDate");
		}
		if (!ValidateUtil.isValid(endDate)) {
			throw new MyException("参数错误：startDate");
		}

		Date _startDate = DateUtil.getDate(startDate);
		Date _endDate = DateUtil.getDate(endDate);
		int diffDay = DateUtil.diffMonth(_startDate, _endDate); // 如果跨度最大一年，统计数据不准确。如：2024-09-11 -
		if (diffDay > 12) {
			throw new MyException("查询时间范围不得超过12个月");
		}

		// 我的练习跟踪列表
		return myExerTrackService.getList(exerId, userId,
				Integer.parseInt(DateUtil.formatDateCustom(_startDate, "yyyyMMdd")),
				Integer.parseInt(DateUtil.formatDateCustom(_endDate, "yyyyMMdd")));
	}

	@Override
	public List<MyExerTrackMonthly> getTrackMonthlyList(Integer exerId, Integer userId, String startYm, String endYm) {
		// 数据校验
		if (!ValidateUtil.isValid(exerId)) {
			throw new MyException("参数错误：exerId");
		}
		if (!ValidateUtil.isValid(userId)) {
			throw new MyException("参数错误：userId");
		}
		if (!ValidateUtil.isValid(startYm)) {
			throw new MyException("参数错误：startYm");
		}
		if (!ValidateUtil.isValid(endYm)) {
			throw new MyException("参数错误：endYm");
		}

		Exer exer = exerService.getById(exerId);
		if (exer == null) {
			throw new MyException("参数错误：exerId");
		}

		User user = baseCacheService.getUser(userId);
		if (CurLoginUserUtil.isAdmin()) {

		} else if (CurLoginUserUtil.isSubAdmin()) {
			if (userId.intValue() != getCurUser().getId().intValue()) {
				throw new MyException("无查阅权限");
			}
		} else if (CurLoginUserUtil.isExamUser()) {
			if (userId.intValue() != getCurUser().getId().intValue()) {
				throw new MyException("无查阅权限");
			}
			if (!(exer.getUserIds().contains(user.getId()) || exer.getOrgIds().contains(user.getOrgId()))) {
				throw new MyException("无查询权限");
			}
		}

		Date _startDate = DateUtil.getDate(startYm + "01");
		Date _endDate = DateUtil.getDate(endYm + "31");

		int diffDay = DateUtil.diffMonth(_startDate, _endDate);
		if (diffDay > 12) {
			throw new MyException("查询时间范围不得超过12个月");
		}

		// 我的练习跟踪列表
		return myExerTrackMonthlyService.getList(exerId, userId, Integer.parseInt(startYm), Integer.parseInt(endYm));
	}
}
