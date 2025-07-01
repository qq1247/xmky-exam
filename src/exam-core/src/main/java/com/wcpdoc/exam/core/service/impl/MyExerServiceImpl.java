package com.wcpdoc.exam.core.service.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.MyExerDao;
import com.wcpdoc.exam.core.entity.Exer;
import com.wcpdoc.exam.core.entity.MyExer;
import com.wcpdoc.exam.core.entity.MyExerQuestion;
import com.wcpdoc.exam.core.entity.MyQuestion;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.service.ExamCacheService;
import com.wcpdoc.exam.core.service.ExerService;
import com.wcpdoc.exam.core.service.MyExerQuestionService;
import com.wcpdoc.exam.core.service.MyExerService;
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
			if (count == 0) {
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

		if (type == 11) {
			return myExerQuestionService.getList(exerId, getCurUser().getId()).stream()
					.filter(myExerQuestion -> myExerQuestion.getUserScore() != null
							&& myExerQuestion.getUserScore().doubleValue() != myExerQuestion.getScore().doubleValue())
					.collect(Collectors.toList());
		}

		if (type == 12) {
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
		if (QuestionUtil.hasSubjective(question) && !ValidateUtil.isValid(userScore)) {// 主观题需要自评分数
			throw new MyException("参数错误：userScore");
		}

		if (ValidateUtil.isValid(myExerQuestion.getUserAnswer())) {
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
}
