package com.wcpdoc.exam.core.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.BaseCacheService;
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
import com.wcpdoc.exam.core.entity.MyFavQuestion;
import com.wcpdoc.exam.core.entity.MyExamQuestion;
import com.wcpdoc.exam.core.entity.MyWrongQuestion;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.entity.QuestionBank;
import com.wcpdoc.exam.core.service.ExamCacheService;
import com.wcpdoc.exam.core.service.ExerService;
import com.wcpdoc.exam.core.service.MyExerQuestionService;
import com.wcpdoc.exam.core.service.MyExerService;
import com.wcpdoc.exam.core.service.MyExerTrackMonthlyService;
import com.wcpdoc.exam.core.service.MyExerTrackService;
import com.wcpdoc.exam.core.service.MyFavQuestionService;
import com.wcpdoc.exam.core.service.MyWrongQuestionService;
import com.wcpdoc.exam.core.service.QuestionBankService;
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
	@Resource
	private QuestionBankService questionBankService;
	@Resource
	private MyFavQuestionService myFavQuestionService;
	@Resource
	private MyWrongQuestionService myWrongQuestionService;

	@Override
	public RBaseDao<MyExer> getDao() {
		return myExerDao;
	}

	@Override
	public void add(MyExer myExer) {
		// 数据校验
		Exer exer = addValid0(myExer);

		List<MyExer> myExerList = myExerDao.getList(getCurUser().getId(), myExer.getExerId());
		List<MyExerQuestion> myExerQuestionList = myExerList.stream().map(MyExer::getId)
				.map(myExerQuestionService::getList).flatMap(List::stream).collect(Collectors.toList());// 获取自己练习关联的所有试题（试题会有重复）

		List<QuestionBank> questionBankList = exer.getQuestionBankIds().stream().map(questionBankService::getById)
				.collect(Collectors.toList());
		List<Question> questionList = questionBankList.stream().map(QuestionBank::getId).map(questionService::getList)
				.flatMap(List::stream).collect(Collectors.toList());// 获取练习关联的多个题库下的所有试题
		Map<Integer, List<Question>> questionGroup = questionList.stream()
				.collect(Collectors.groupingBy(Question::getType));
		baseCacheService.getDictList().stream().filter(dict -> dict.getDictIndex().equals("QUESTION_TYPE"))
				.forEach(dict -> {
					questionGroup.putIfAbsent(Integer.parseInt(dict.getDictKey()), new ArrayList<>());
				});

		addValid(myExer, myExerQuestionList, questionGroup);

		// 我的练习添加
		myExer.setUserId(getCurUser().getId());
		myExer.setUpdateUserId(getCurUser().getId());
		myExer.setUpdateTime(new Date());
		save(myExer);

		// 我的练习试题添加
		int no = 1;
		if (myExer.getSingleNum() > 0) {
			no = addMyExerQuestion(myExer, exer, myExerQuestionList, questionGroup, 1, myExer.getSingleNum(), no);
		}
		if (myExer.getMultipleNum() > 0) {
			no = addMyExerQuestion(myExer, exer, myExerQuestionList, questionGroup, 2, myExer.getMultipleNum(), no);
		}
		if (myExer.getFillBlankNum() > 0) {
			no = addMyExerQuestion(myExer, exer, myExerQuestionList, questionGroup, 3, myExer.getFillBlankNum(), no);
		}
		if (myExer.getJudgeNum() > 0) {
			no = addMyExerQuestion(myExer, exer, myExerQuestionList, questionGroup, 4, myExer.getJudgeNum(), no);
		}
		if (myExer.getQaNum() > 0) {
			no = addMyExerQuestion(myExer, exer, myExerQuestionList, questionGroup, 5, myExer.getQaNum(), no);
		}
	}

	@Override
	public List<MyExer> getList(Integer userId, Integer exerId) {
		return myExerDao.getList(userId, exerId);
	}

	@Override
	public BigDecimal answer(Integer id, Integer questionId, String[] userAnswers, BigDecimal userScore) {
		// 数据校验
		Exer exer = answerValid0(id, questionId, userAnswers, userScore);
		MyExerQuestion myExerQuestion = answerValid(id, questionId, userAnswers, userScore);

		// 打分（1：客观题，自动打分； 2：主观题，用户自评）
		Question question = examCacheService.getQuestion(myExerQuestion.getQuestionId());
		List<QuestionAnswer> questionAnswerList = examCacheService.getQuestionAnswerList(questionId);

		MyExamQuestion myQuestion = new MyExamQuestion();
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
		myExerQuestionService.updateById(myExerQuestion);

		// 如果答错了，该题错误次数+1
		if (myExerQuestion.getScore().doubleValue() != myExerQuestion.getUserScore().doubleValue()) {
			MyWrongQuestion myWrongQuestion = myWrongQuestionService.getMyWrongQuestion(getCurUser().getId(),
					questionId);
			if (myWrongQuestion == null) {
				myWrongQuestion = new MyWrongQuestion();
				myWrongQuestion.setUserId(getCurUser().getId());
				myWrongQuestion.setQuestionId(questionId);
				myWrongQuestion.setQuestionType(question.getType());
				myWrongQuestion.setWrongNum(1);
				myWrongQuestion.setFirstWrongTime(new Date());
				myWrongQuestion.setFirstWrongSource(exer.getName());
				myWrongQuestion.setLastWrongTime(new Date());
				myWrongQuestion.setLastWrongSource(exer.getName());
				myWrongQuestion.setState(2);
				myWrongQuestion.setUpdateUserId(getCurUser().getId());
				myWrongQuestion.setUpdateTime(new Date());
				myWrongQuestionService.save(myWrongQuestion);
			} else {
				myWrongQuestion.setWrongNum(myWrongQuestion.getWrongNum() + 1);
				myWrongQuestion.setLastWrongTime(new Date());
				myWrongQuestion.setLastWrongSource(exer.getName());
				myWrongQuestion.setState(2); // 不管是否标记已掌握，只要错了，就标记为未掌握
				myWrongQuestion.setUpdateUserId(getCurUser().getId());
				myWrongQuestion.setUpdateTime(new Date());
				myWrongQuestionService.updateById(myWrongQuestion);
			}
		}
		return myExerQuestion.getUserScore();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void track(Integer exerId) {
		// 数据校验
		if (!ValidateUtil.isValid(exerId)) {
			throw new MyException("参数错误：exerId");
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
		User user = baseCacheService.getUser(getCurUser().getId());
		if (!(exer.getUserIds().contains(user.getId()) || exer.getOrgIds().contains(user.getOrgId()))) {
			throw new MyException("无操作权限");
		}

		/**
		 * 跟踪 <br/>
		 * 1：为防止高并发，先加入缓存。<br/>
		 * 2：定时器每5分钟把缓存数据批量落库。如果服务器重启，最多会丢失5分钟数据，在可以容忍范围内。<br/>
		 */
		Cache cache = cacheManager.getCache(ExamConstant.EXER_TIME_CACHE);
		String cacheKey = String.format("%s:%s:%s", exerId, getCurUser().getId(),
				DateUtil.formatDateCustom(new Date(), "yyyyMMdd"));// 3:5:20250908
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
	public List<MyExerTrack> getTrackList(Integer exerId, String startDate, String endDate) {
		// 数据校验
		if (!ValidateUtil.isValid(exerId)) {
			throw new MyException("参数错误：exerId");
		}
		if (!ValidateUtil.isValid(startDate)) {
			throw new MyException("参数错误：startDate");
		}
		if (!ValidateUtil.isValid(endDate)) {
			throw new MyException("参数错误：startDate");
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
		User curUser = baseCacheService.getUser(getCurUser().getId());
		if (!(exer.getOrgIds().contains(curUser.getOrgId()) || exer.getUserIds().contains(curUser.getId()))) {
			throw new MyException("无操作权限");
		}

		Date _startDate = DateUtil.getDate(startDate);
		Date _endDate = DateUtil.getDate(endDate);
		int diffDay = DateUtil.diffMonth(_startDate, _endDate); // 如果跨度最大一年，统计数据不准确。如：2024-09-11至2025-09-11，应该2024-09-01
		if (diffDay > 12) {
			throw new MyException("查询时间范围不得超过12个月");
		}

		// 我的练习跟踪列表
		return myExerTrackService.getList(exerId, getCurUser().getId(),
				Integer.parseInt(DateUtil.formatDateCustom(_startDate, "yyyyMMdd")),
				Integer.parseInt(DateUtil.formatDateCustom(_endDate, "yyyyMMdd")));
	}

	@Override
	public List<MyExerTrackMonthly> getTrackMonthlyList(Integer exerId, String startYm, String endYm) {
		// 数据校验
		if (!ValidateUtil.isValid(exerId)) {
			throw new MyException("参数错误：exerId");
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
		if (exer.getState() == 0) {
			throw new MyException("已删除");
		}
		if (exer.getState() == 2) {
			throw new MyException("已暂停");
		}
		User curUser = baseCacheService.getUser(getCurUser().getId());
		if (!(exer.getOrgIds().contains(curUser.getOrgId()) || exer.getUserIds().contains(curUser.getId()))) {
			throw new MyException("无操作权限");
		}

		Date _startDate = DateUtil.getDate(startYm + "01");
		Date _endDate = DateUtil.getDate(endYm + "31");

		int diffDay = DateUtil.diffMonth(_startDate, _endDate);
		if (diffDay > 12) {
			throw new MyException("查询时间范围不得超过12个月");
		}

		// 我的练习跟踪列表
		return myExerTrackMonthlyService.getList(exerId, getCurUser().getId(), Integer.parseInt(startYm),
				Integer.parseInt(endYm));
	}

	/**
	 * 
	 * 抽题规则 <br/>
	 * 1：从题库未抽到的试题抽取 <br/>
	 * 2：题数不足，从未练习的试题抽取（练习A练习了试题A，练习B没练习试题A，实际算已练习） <br/>
	 * 3：题数不足，从已练习的试题抽取（不等于整个题库试题，排除了前两项） <br/>
	 * 
	 * v1.0 zhanghc 2025年9月25日下午10:05:06
	 * 
	 * @param myExer
	 * @param exer
	 * @param myExerQuestionList
	 * @param questionGroup
	 * @param questionType
	 * @param questionNum
	 * @param no
	 * @return int
	 */
	private int addMyExerQuestion(MyExer myExer, Exer exer, List<MyExerQuestion> myExerQuestionList,
			Map<Integer, List<Question>> questionGroup, Integer questionType, Integer questionNum, int no) {
		List<Question> exerQuestionList = new ArrayList<>();
		List<Question> questionList = questionGroup.get(questionType);// 练习关联的多个题库下所有的某个类型的试题

		if (myExer.getType() == 1 || myExer.getType() == 2) {
			Set<Integer> answerQuestionIds = myExerQuestionList.stream()
					.filter(meq -> ValidateUtil.isValid(meq.getUserScore())).map(MyExerQuestion::getQuestionId)
					.collect(Collectors.toSet()); // 用户已练习的试题（练习A练习了试题A，练习B没练习试题A，实际算已练习）
			Set<Integer> unAnswerQuestionIds = myExerQuestionList.stream()
					.filter(meq -> !answerQuestionIds.contains(meq.getQuestionId())).map(MyExerQuestion::getQuestionId)
					.collect(Collectors.toSet()); // 用户未练习的试题
			List<Question> unPickQuestionList = questionList.stream()
					.filter(question -> !(answerQuestionIds.contains(question.getId())
							|| unAnswerQuestionIds.contains(question.getId())))
					.collect(Collectors.toList());// 未从题库抽到的题

			Collections.shuffle(unPickQuestionList);
			exerQuestionList.addAll(unPickQuestionList.subList(0, Math.min(questionNum, unPickQuestionList.size())));// 抽10道，未从题库抽到的题只有4道

			if (exerQuestionList.size() < questionNum) {
				List<Question> unAnswerQuestionList = questionList.stream()
						.filter(question -> unAnswerQuestionIds.contains(question.getId()))
						.collect(Collectors.toList());
				Collections.shuffle(unAnswerQuestionList);
				exerQuestionList.addAll(unAnswerQuestionList.subList(0,
						Math.min((questionNum - exerQuestionList.size()), unAnswerQuestionList.size())));// 抽6道，用户未练习的试题只有3道
			}
			if (exerQuestionList.size() < questionNum) {
				List<Question> answerQuestionList = questionList.stream()
						.filter(question -> answerQuestionIds.contains(question.getId())).collect(Collectors.toList());
				Collections.shuffle(answerQuestionList);
				exerQuestionList.addAll(answerQuestionList.subList(0,
						Math.min(questionNum - exerQuestionList.size(), answerQuestionList.size())));// 抽3道，用户已练习的试题抽取4道（一定会满足，前面已校验）
			}

			Collections.shuffle(exerQuestionList); // 整体在打乱一次
		} else if (myExer.getType() == 3) {
			List<MyWrongQuestion> myWrongQuestionList = myWrongQuestionService.getList(getCurUser().getId());
			Set<Integer> wrongQuestionIds = myWrongQuestionList.stream()
					.filter(myWrongQuestion -> myWrongQuestion.getState() == 2).map(MyWrongQuestion::getQuestionId)
					.collect(Collectors.toSet());
			List<Question> wrongQuestionList = questionList.stream()
					.filter(question -> wrongQuestionIds.contains(question.getId())).collect(Collectors.toList());
			Collections.shuffle(wrongQuestionList);
			exerQuestionList.addAll(wrongQuestionList.subList(0, Math.min(questionNum, wrongQuestionList.size())));
		} else if (myExer.getType() == 4) {
			List<MyFavQuestion> myFavQuestionList = myFavQuestionService.getList(getCurUser().getId());
			Set<Integer> favQuestionIds = myFavQuestionList.stream().map(MyFavQuestion::getQuestionId)
					.collect(Collectors.toSet());
			List<Question> favQuestionList = questionList.stream()
					.filter(question -> favQuestionIds.contains(question.getId())).collect(Collectors.toList());
			Collections.shuffle(favQuestionList);
			exerQuestionList.addAll(favQuestionList.subList(0, Math.min(questionNum, favQuestionList.size())));
		}

		for (Question question : exerQuestionList) {
			MyExerQuestion myExerQuestion = new MyExerQuestion();
			myExerQuestion.setMyExerId(myExer.getId());
			myExerQuestion.setQuestionId(question.getId());
			myExerQuestion.setQuestionType(question.getType());
			myExerQuestion.setNo(no++);
			myExerQuestion.setScore(question.getScore());
			if (QuestionUtil.hasMultipleChoice(question) || QuestionUtil.hasFillBlank(question)
					|| (QuestionUtil.hasQA(question) && QuestionUtil.hasObjective(question))) {
				myExerQuestion.setScores(examCacheService.getQuestionAnswerList(myExerQuestion.getQuestionId()).stream()
						.map(QuestionAnswer::getScore).collect(Collectors.toList()));
			}
			myExerQuestion.setMarkOptions(question.getMarkOptions());
			myExerQuestion.setUpdateUserId(getCurUser().getId());
			myExerQuestion.setUpdateTime(new Date());
			myExerQuestionService.save(myExerQuestion);
		}

		return no;
	}

	private Exer addValid0(MyExer myExer) {
		if (!ValidateUtil.isValid(myExer.getName())) {
			throw new MyException("参数错误：name");
		}
		if (!ValidateUtil.isValid(myExer.getExerId())) {
			throw new MyException("参数错误：exerId");
		}
		if (!ValidateUtil.isValid(myExer.getType())) {
			throw new MyException("参数错误：type");
		}
		if (!(myExer.getType() >= 1 && myExer.getType() <= 4)) {
			throw new MyException("参数错误：type");
		}
		if (myExer.getSingleNum() == 0 && myExer.getMultipleNum() == 0 && myExer.getFillBlankNum() == 0
				&& myExer.getJudgeNum() == 0 && myExer.getQaNum() == 0) {
			throw new MyException("最少练习1道试题");
		}
		Exer exer = exerService.getById(myExer.getExerId());
		if (exer == null) {
			throw new MyException("参数错误：exerId");
		}
		if (exer.getState() == 0) {
			throw new MyException("练习已删除");
		}
		if (exer.getState() == 2) {
			throw new MyException("练习已暂停");
		}
		User user = baseCacheService.getUser(getCurUser().getId());
		if (!(exer.getUserIds().contains(user.getId()) || exer.getOrgIds().contains(user.getOrgId()))) {
			throw new MyException("无操作权限");
		}
		return exer;
	}

	private void addValid(MyExer myExer, List<MyExerQuestion> myExerQuestionList,
			Map<Integer, List<Question>> questionGroup) {
		if (myExer.getType() == 1) { // 类型（1：自组；2：未练；3：错题；4：收藏）
			if (!(myExer.getSingleNum() >= 0 && myExer.getSingleNum() <= questionGroup.get(1).size())) {
				throw new MyException("参数错误：singleNum");
			}
			if (!(myExer.getMultipleNum() >= 0 && myExer.getMultipleNum() <= questionGroup.get(2).size())) {
				throw new MyException("参数错误：multipleNum");
			}
			if (!(myExer.getFillBlankNum() >= 0 && myExer.getFillBlankNum() <= questionGroup.get(3).size())) {
				throw new MyException("参数错误：fillBlankNum");
			}
			if (!(myExer.getJudgeNum() >= 0 && myExer.getJudgeNum() <= questionGroup.get(4).size())) {
				throw new MyException("参数错误：judgeNum");
			}
			if (!(myExer.getQaNum() >= 0 && myExer.getQaNum() <= questionGroup.get(5).size())) {
				throw new MyException("参数错误：qaNum");
			}
		} else if (myExer.getType() == 2) {
			Set<Integer> answerQuestionIds = myExerQuestionList.stream()
					.filter(myExerQuestion -> ValidateUtil.isValid(myExerQuestion.getUserScore()))
					.map(MyExerQuestion::getQuestionId).collect(Collectors.toSet()); // 已练习过的试题

			if (!(myExer.getSingleNum() >= 0 && myExer.getSingleNum() <= questionGroup.get(1).stream()
					.filter(question -> !answerQuestionIds.contains(question.getId())).collect(Collectors.toList())
					.size())) {// 过滤掉已做过的（实际 = 练习未抽到题库的题+练习已抽到但未做的题）
				throw new MyException("参数错误：singleNum");
			}
			if (!(myExer.getMultipleNum() >= 0 && myExer.getMultipleNum() <= questionGroup.get(2).stream()
					.filter(question -> !answerQuestionIds.contains(question.getId())).collect(Collectors.toList())
					.size())) {
				throw new MyException("参数错误：multipleNum");
			}
			if (!(myExer.getFillBlankNum() >= 0 && myExer.getFillBlankNum() <= questionGroup.get(3).stream()
					.filter(question -> !answerQuestionIds.contains(question.getId())).collect(Collectors.toList())
					.size())) {
				throw new MyException("参数错误：fillBlankNum");
			}
			if (!(myExer.getJudgeNum() >= 0 && myExer.getJudgeNum() <= questionGroup.get(4).stream()
					.filter(question -> !answerQuestionIds.contains(question.getId())).collect(Collectors.toList())
					.size())) {
				throw new MyException("参数错误：judgeNum");
			}
			if (!(myExer.getQaNum() >= 0 && myExer.getQaNum() <= questionGroup.get(5).stream()
					.filter(question -> !answerQuestionIds.contains(question.getId())).collect(Collectors.toList())
					.size())) {
				throw new MyException("参数错误：qaNum");
			}
		}
	}

	private Exer answerValid0(Integer id, Integer questionId, String[] userAnswers, BigDecimal userScore) {
		if (!ValidateUtil.isValid(id)) {
			throw new MyException("参数错误：id");
		}
		MyExer myExer = getById(id);
		if (myExer == null) {
			throw new MyException("参数错误：id");
		}
		if (myExer.getUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("无操作权限");
		}
		Exer exer = exerService.getById(myExer.getExerId());
		if (exer == null) {
			throw new MyException("参数错误：id");
		}
		if (exer.getState() == 0) {
			throw new MyException("已删除");
		}
		if (exer.getState() == 2) {
			throw new MyException("已暂停");
		}
		User curUser = baseCacheService.getUser(getCurUser().getId());
		if (!(exer.getOrgIds().contains(curUser.getOrgId()) || exer.getUserIds().contains(curUser.getId()))) {
			throw new MyException("无操作权限");
		}
		return exer;
	}

	private MyExerQuestion answerValid(Integer id, Integer questionId, String[] userAnswers, BigDecimal userScore) {
		if (!ValidateUtil.isValid(questionId)) {
			throw new MyException("参数错误：questionId");
		}
		MyExerQuestion myExerQuestion = myExerQuestionService.getMyExerQuestion(id, questionId);
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

		return myExerQuestion;
	}
}
