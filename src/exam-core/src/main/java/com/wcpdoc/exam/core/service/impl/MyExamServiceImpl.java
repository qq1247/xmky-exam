package com.wcpdoc.exam.core.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.BigDecimalUtil;
import com.wcpdoc.core.util.CollectionUtil;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.SpringUtil;
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.constant.ExamConstant;
import com.wcpdoc.exam.core.dao.MyExamDao;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.ExamQuestion;
import com.wcpdoc.exam.core.entity.ExamRule;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyExamHis;
import com.wcpdoc.exam.core.entity.MyExamQuestion;
import com.wcpdoc.exam.core.entity.MyExamQuestionHis;
import com.wcpdoc.exam.core.entity.MySxe;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.entity.ex.PaperPart;
import com.wcpdoc.exam.core.service.ExamCacheService;
import com.wcpdoc.exam.core.service.ExamQuestionService;
import com.wcpdoc.exam.core.service.ExamRuleService;
import com.wcpdoc.exam.core.service.MyExamHisService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyPaperService;
import com.wcpdoc.exam.core.service.MyQuestionHisService;
import com.wcpdoc.exam.core.service.MyQuestionService;
import com.wcpdoc.exam.core.service.MySxeService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.util.ExamUtil;
import com.wcpdoc.exam.core.util.MyExamUtil;
import com.wcpdoc.exam.core.util.QuestionUtil;
import com.wcpdoc.file.service.FileService;

import lombok.extern.slf4j.Slf4j;

/**
 * 我的考试服务层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Service
@Slf4j
public class MyExamServiceImpl extends BaseServiceImp<MyExam> implements MyExamService {

	@Resource
	private MyExamDao myExamDao;
	@Resource
	private MyQuestionService myQuestionService;
	@Resource
	private MyPaperService myPaperService;
	@Resource
	private ExamCacheService examCacheService;
	@Resource
	private BaseCacheService baseCacheService;
	@Resource
	private ExamQuestionService examQuestionService;
	@Resource
	private ExamRuleService examRuleService;
	@Resource
	private QuestionService questionService;
	@Resource
	private MySxeService mySxeService;
	@Resource
	private MyExamHisService myExamHisService;
	@Resource
	private MyQuestionHisService myQuestionHisService;
	@Resource
	private FileService fileService;

	@Override
	public RBaseDao<MyExam> getDao() {
		return myExamDao;
	}

	@Override
	public List<PaperPart> paper(Integer examId, Integer userId) {
		// 数据校验
		paperValid(examId, userId);

		// 试卷处理
		Exam exam = examCacheService.getExam(examId);
		MyExam myExam = examCacheService.getMyExam(examId, userId);
		if (!ValidateUtil.isValid(myExam.getAnswerStartTime())
				&& exam.getEndTime().getTime() > System.currentTimeMillis()) {
			SpringUtil.getBean(MyExamService.class).paperHandle(examId, userId);// 接口调用缓存注解才生效
		}
		// 试卷生成
		return myPaperService.generatePaper(examId, userId, MyExamUtil.scoreShow(exam, myExam),
				MyExamUtil.answerShow(exam, myExam));
	}

	@Override
	@Caching(evict = { //
			@CacheEvict(value = ExamConstant.MYEXAM_CACHE, key = ExamConstant.MYEXAM_KEY_PRE
					+ "#examId + ':' + #userId"),
			@CacheEvict(value = ExamConstant.MYEXAM_CACHE, key = ExamConstant.MYEXAM_LIST_KEY_PRE + "#examId"),
			@CacheEvict(value = ExamConstant.MYEXAM_CACHE, key = ExamConstant.MYEXAM_UNMARK_LIST_KEY), }) // 字段变更就清除缓存，保持和数据库一致
	public void paperHandle(Integer examId, Integer userId) {
		/**
		 * 考试用户如果是第一次打开试卷，并且考试没结束，生成他自己的考试时间，并标记为考试中<br/>
		 * 
		 * 如果是限制考试，从当前开始计时，延长到限制的分钟数则结束，但不超过本次考试的结束时间<br/>
		 * 示例：考试时间为2024-01-30 08:00 - 2024-01-30 12:00，单次考试时间为60分钟<br/>
		 * 考试用户第一次打开试卷时间为2024-01-30 08:15:16；他的考试结束时间为2024-01-30 09:15:16<br/>
		 * 考试用户第一次打开试卷时间为2024-01-30 11:15:16；他的考试结束时间为2024-01-30 12:00:00<br/>
		 * 
		 * 如果是不限制考试，从当前开始计时，他的结束时间为本次考试的结束时间<br/>
		 * 考试用户第一次打开试卷时间为2024-01-30 08:15:16；他的考试结束时间为2024-01-30 12:00:00<br/>
		 * 考试用户第一次打开试卷时间为2024-01-30 11:15:16；他的考试结束时间为2024-01-30 12:00:00<br/>
		 */
		MyExam myExam = examCacheService.getMyExam(examId, userId);
		Exam exam = examCacheService.getExam(examId);
		myExam.setAnswerStartTime(new Date());
		if (ExamUtil.hasTimeLimit(exam)) {
			myExam.setAnswerEndTime(DateUtil.getNextMinute(myExam.getAnswerStartTime(), exam.getLimitMinute()));
			if (myExam.getAnswerEndTime().getTime() > exam.getEndTime().getTime()) {
				myExam.setAnswerEndTime(exam.getEndTime());
			}
		} else {
			myExam.setAnswerEndTime(exam.getEndTime());
		}
		myExam.setState(2);
		updateById(myExam);

		User user = baseCacheService.getUser(myExam.getUserId());
		log.info("【{}-{}】进入【{}-{}】考试", user.getLoginName(), user.getName(), exam.getId(), exam.getName());
	}

	@Override
	@CacheEvict(value = ExamConstant.MYQUESTION_CACHE, key = ExamConstant.MYQUESTION_LIST_KEY_PRE
			+ "#examId + ':' + #userId")
	public void answer(Integer examId, Integer userId, Integer questionId, String[] answers, Integer[] imgFileIds,
			Integer[] videoFileIds) {
		// 数据校验
		MyExamQuestion myQuestion = answerValid(examId, userId, questionId, imgFileIds, videoFileIds);

		// 答案保存
		answerSave(questionId, answers, myQuestion, imgFileIds, videoFileIds);

		// 答案附件保存
		answerFileSave(myQuestion);

		User user = baseCacheService.getUser(userId);
		Exam exam = examCacheService.getExam(examId);
		log.info("【{}-{}】正在【{}-{}】答题，【{}-{}】", user.getLoginName(), user.getName(), exam.getId(), exam.getName(),
				questionId, StringUtil.join(answers));
	}

	@Override
	@Caching(evict = { //
			@CacheEvict(value = ExamConstant.MYEXAM_CACHE, key = ExamConstant.MYEXAM_KEY_PRE
					+ "#examId + ':' + #userId"),
			@CacheEvict(value = ExamConstant.MYEXAM_CACHE, key = ExamConstant.MYEXAM_LIST_KEY_PRE + "#examId"), //
			@CacheEvict(value = ExamConstant.MYEXAM_CACHE, key = ExamConstant.MYEXAM_UNMARK_LIST_KEY), //
	})
	public void finish(Integer examId, Integer userId) {
		// 数据校验
		finishValid(examId, userId);

		/**
		 * 交卷
		 * 
		 * 正常在页面自动交卷，会有网络延时，更新交卷时间时，如果它超出正常范围时修正一下，保证计算答题分钟等结果正常。 <br/>
		 * 如：限时2分钟考试，2024-09-03 至 13:00:11 2024-09-03 13:02:12，页面计算答题时长为2分1秒 <br/>
		 * 交卷时间需修正为2024-09-03 13:02:11
		 */
		MyExam myExam = examCacheService.getMyExam(examId, userId);
		Date curTime = new Date();
		if (myExam.getAnswerEndTime().getTime() > curTime.getTime()) {
			myExam.setAnswerEndTime(curTime);
			updateById(myExam);
		}

		User user = baseCacheService.getUser(myExam.getUserId());
		Exam exam = examCacheService.getExam(examId);
		log.info("【{}-{}】考试，【{}-{}】交卷", exam.getId(), exam.getName(), user.getLoginName(), user.getName());
	}

	@Override
	@CacheEvict(value = ExamConstant.MYEXAM_CACHE, allEntries = true)
	public void clear(Integer examId) {
		myExamDao.clear(examId);
	}

	@Override
	@Caching(evict = { //
			@CacheEvict(value = ExamConstant.MYEXAM_CACHE, key = ExamConstant.MYEXAM_KEY_PRE
					+ "#examId + ':' + #userId"), })
	public void generatePaper(Integer examId, Integer userId) {
		// 数据校验
		Exam exam = examCacheService.getExam(examId);// 如果是免登录考试，并且是第一次进入，则生成试卷
		if (exam.getLoginType() != 2) {
			throw new MyException("非免登录考试");
		}

		MyExam myExam = examCacheService.getMyExam(examId, getCurUser().getId());
		if (myExam != null) {
			return;// 只生成一次
		}

		long curTime = System.currentTimeMillis();
		if (exam.getStartTime().getTime() > curTime) {
			throw new MyException("考试未开始");
		}
		if (exam.getEndTime().getTime() < curTime) {
			throw new MyException("考试已结束");
		}

		// 生成考试用户试卷
		Map<Integer, List<QuestionOption>> questionOptionCache = new HashMap<>();
		Map<Integer, List<QuestionAnswer>> questionAnswerCache = new HashMap<>();
		myExam = new MyExam();// 生成我的考试信息
		myExam.setExamId(examId);
		myExam.setUserId(userId);
		// myExam.setMarkUserId(1); //由管理员、子管理员或阅卷用户自己领取自己分配
		myExam.setState(1);// 未考试
		myExam.setMarkState(1);// 未阅卷
		myExam.setUpdateTime(new Date());
		myExam.setUpdateUserId(getCurUser().getId());
		save(myExam);

		exam = examCacheService.getExam(examId);
		if (exam.getGenType() == 1) {// 如果是人工组卷，直接生成我的试卷
			List<ExamQuestion> examQuestionList = examQuestionService.getList(examId);
			List<MyExamQuestion> shuffleCacheList = new ArrayList<>();// 乱序缓存列表，用于乱序
			for (int i = 0; i < examQuestionList.size(); i++) {
				ExamQuestion examQuestion = examQuestionList.get(i);
				MyExamQuestion myQuestion = new MyExamQuestion();
				myQuestion.setChapterName(examQuestion.getChapterName());
				myQuestion.setChapterTxt(examQuestion.getChapterTxt());
				myQuestion.setType(examQuestion.getType());
				myQuestion.setScore(examQuestion.getScore());
				myQuestion.setScores(examQuestion.getScores());
				myQuestion.setMarkOptions(examQuestion.getMarkOptions());
				myQuestion.setExamId(examQuestion.getExamId());
				myQuestion.setQuestionId(examQuestion.getQuestionId());
				myQuestion.setUserId(userId);
				myQuestion.setNo(i + 1);
				myQuestion.setVer(1);
				myQuestion.setUpdateUserId(getCurUser().getId());
				myQuestion.setUpdateTime(new Date());
				myQuestionService.save(myQuestion);

				if (ExamUtil.hasQuestionRand(exam)) {// 如果是试题乱序（章节不能乱序；试题不能跨章节乱序）
					if (ExamUtil.hasQuestion(myQuestion)) {// 1章节；2试题；3试题；4试题；5章节；6试题；7试题；8：试题
						shuffleCacheList.add(myQuestion); // 2试题；3试题；4试题；
					}
					if (ExamUtil.hasChapter(myQuestion) || i >= examQuestionList.size() - 1) {// 5章节；（如果是章节或最后一道题，乱序已经缓存的试题，前面不要加else，最后一道题的情况不处理）
						Collections.shuffle(shuffleCacheList);// 3试题；2试题；4试题；
						Integer maxNo = ExamUtil.hasChapter(myQuestion) ? myQuestion.getNo() - 1 : myQuestion.getNo();// 5章节
						for (MyExamQuestion shuffleCache : shuffleCacheList) {
							shuffleCache.setNo(maxNo--);
							myQuestionService.updateById(myQuestion);// 1章节；4试题；2试题；3试题；
						}
						shuffleCacheList.clear();
					}
				}
				if (ExamUtil.hasOptionRand(exam)) {// 如果是选项乱序
					if (questionOptionCache.get(myQuestion.getQuestionId()) == null) {
						questionOptionCache.put(myQuestion.getQuestionId(),
								examCacheService.getQuestionOptionList(myQuestion.getQuestionId()));
					}
					List<QuestionOption> questionOptionList = questionOptionCache.get(myQuestion.getQuestionId());// A,B,C,D
					myQuestion.setOptionsNo(shuffleNums(1, questionOptionList.size()));// D,B,A,C
					myQuestionService.updateById(myQuestion);
				}
			}
		} else if (exam.getGenType() == 2) {// 如果是随机组卷，按抽题规则生成我的试卷（校验里判断过规则是否满足，不用在判断）
			List<ExamRule> examRuleList = examRuleService.getList(examId);
			Set<Question> questionOfUsed = new HashSet<>();
			int no = 1;
			for (int i = 0; i < examRuleList.size(); i++) {
				ExamRule examRule = examRuleList.get(i);
				if (examRule.getType() == 1) {// 如果是章节
					MyExamQuestion myQuestion = new MyExamQuestion();
					myQuestion.setType(examRule.getType());
					myQuestion.setChapterName(examRule.getChapterName());
					myQuestion.setChapterTxt(examRule.getChapterTxt());
					myQuestion.setUserId(userId);
					myQuestion.setExamId(examId);
					myQuestion.setNo(no++);
					myQuestion.setVer(1);
					myQuestion.setUpdateTime(new Date());
					myQuestion.setUpdateUserId(getCurUser().getId());
					myQuestionService.save(myQuestion);
				} else {// 如果是规则
					List<Question> questionList = questionService.getList(examRule.getQuestionBankId());
					Collections.shuffle(questionList);// 从当前规则中随机抽题（乱序模拟随机）
					Integer ruleRemainNum = examRule.getNum();// 该规则试题数量，找到一个数量减一
					for (Question question : questionList) {
						if (ruleRemainNum <= 0) {// 满足规则，处理下一个规则
							break;
						}
						if (questionOfUsed.contains(question)) {// 已经使用过的试题就不能在用，继续找下一个
							continue;
						}
						if (examRule.getQuestionType() != question.getType() // 当前试题不符合当前抽题规则，继续找下一个
								|| examRule.getMarkType() != question.getMarkType()) {
							continue;
						}

						MyExamQuestion myQuestion = new MyExamQuestion();
						myQuestion.setType(examRule.getType());
						myQuestion.setScore(examRule.getScore());
						myQuestion.setMarkOptions(examRule.getMarkOptions());
						myQuestion.setQuestionId(question.getId());
						myQuestion.setUserId(userId);
						myQuestion.setExamId(examId);
						myQuestion.setNo(no++); // 试题乱序无效，因为本身就是随机的

						if (QuestionUtil.hasMultipleChoice(question)) {// 如果是多选，使用抽题规则的漏选分数
							myQuestion.setScores(Stream.of(examRule.getScores()).collect(Collectors.toList()));
						} else if ((QuestionUtil.hasFillBlank(question) || QuestionUtil.hasQA(question)) // 如果是客观填空问答，把分数平均分配到子分数
								&& QuestionUtil.hasObjective(question)) {// 如果抽题不设置分数，使用题库默认的分数，会导致总分不确定
							if (questionAnswerCache.get(myQuestion.getQuestionId()) == null) {// 如果抽题设置分数，主观题答案数量不一样，没法按答案分配分数
								questionAnswerCache.put(myQuestion.getQuestionId(),
										examCacheService.getQuestionAnswerList(myQuestion.getQuestionId()));
							}
							List<QuestionAnswer> questionAnswerList = questionAnswerCache
									.get(myQuestion.getQuestionId());// 所以规则为当题分数，平均分配到每个答案
							myQuestion.setScores(splitScore(examRule.getScore(), questionAnswerList.size()));
						}

						myQuestion.setVer(1);
						myQuestion.setUpdateTime(new Date());
						myQuestion.setUpdateUserId(getCurUser().getId());
						myQuestionService.save(myQuestion);

						questionOfUsed.add(question);
						ruleRemainNum--;
					}
				}
			}
		}
	}

	@Override
	@Cacheable(value = ExamConstant.SXE_CACHE, key = ExamConstant.SXE_KEY_PRE
			+ "#examId + ':' + #userId + ':' + #type + ':' + #content", sync = true) // 防抖，同一事件一分钟记录一次
	public boolean sxe(Integer examId, Integer userId, Integer type, String content) {
		MySxe mySxe = new MySxe();
		mySxe.setExamId(examId);
		mySxe.setUserId(getCurUser().getId());
		mySxe.setType(type);
		mySxe.setContent(content);
		mySxe.setUpdateTime(new Date());
		mySxeService.save(mySxe);

		long screenSwitchNum = mySxeService.getList(examId, userId).stream()//
				.filter(_mySxe -> _mySxe.getType() == 3)//
				.count();
		if (screenSwitchNum >= 3) {
			Exam exam = examCacheService.getExam(examId);
			User user = baseCacheService.getUser(userId);
			log.info("【{}-{}】考试中【{}-{}】切屏{}次，系统自动交卷", exam.getId(), exam.getName(), user.getLoginName(), user.getName(),
					screenSwitchNum);
			SpringUtil.getBean(MyExamService.class).finish(examId, userId);// 接口调用缓存注解才生效
			return true;
		}

		return false;
	}

	@Override
	@Caching(evict = { //
			@CacheEvict(value = ExamConstant.MYEXAM_CACHE, key = ExamConstant.MYEXAM_KEY_PRE
					+ "#examId + ':' + #userId"),
			@CacheEvict(value = ExamConstant.MYEXAM_CACHE, key = ExamConstant.MYEXAM_LIST_KEY_PRE + "#examId"),
			@CacheEvict(value = ExamConstant.MYEXAM_CACHE, key = ExamConstant.MYEXAM_UNMARK_LIST_KEY),
			@CacheEvict(value = ExamConstant.MYQUESTION_CACHE, key = ExamConstant.MYQUESTION_LIST_KEY_PRE
					+ "#examId + ':' + #userId") })
	public void retake(Integer examId, Integer userId) {
		// 数据校验
		Exam exam = examCacheService.getExam(examId);
		if (exam.getMarkType() == 2) {
			throw new MyException("主观题试卷");
		}
		if (exam.getMarkState() != 1) {
			throw new MyException("考试已结束");
		}
		if (exam.getRetakeNum() == 0) {
			throw new MyException("不允许重考");
		}

		MyExam myExam = examCacheService.getMyExam(examId, getCurUser().getId());
		if (myExam.getAnswerState() == null) {// 相当于判断了state和markState
			throw new MyException("请继续参考");
		}
		if (myExam.getAnswerState() == 1) {
			throw new MyException("已及格，无须重考");
		}
		if (myExam.getVer().intValue() > exam.getRetakeNum().intValue()) {
			throw new MyException(String.format("只能重考%s次", exam.getRetakeNum()));
		}

		// 旧试卷存档
		MyExamHis myExamHis = new MyExamHis();
		try {
			BeanUtils.copyProperties(myExamHis, myExam);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MyException("拷贝属性错误");
		}
		myExamHis.setId(null);
		myExamHisService.save(myExamHis);

		List<MyExamQuestion> myQuestionList = examCacheService.getMyQuestionList(examId, getCurUser().getId());
		for (MyExamQuestion myQuestion : myQuestionList) {
			MyExamQuestionHis myQuestionHis = new MyExamQuestionHis();
			try {
				BeanUtils.copyProperties(myQuestionHis, myQuestion);
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new MyException("拷贝属性错误");
			}

			myQuestionHis.setId(null);
			myQuestionHisService.save(myQuestionHis);
		}

		// 生成新试卷
		myExam.setMarkUserId(null);
		myExam.setAnswerStartTime(null);
		myExam.setAnswerEndTime(null);
		myExam.setMarkStartTime(null);
		myExam.setMarkEndTime(null);
		myExam.setObjectiveScore(null);
		myExam.setTotalScore(null);
		myExam.setState(1);
		myExam.setMarkState(1);
		myExam.setAnswerState(null);
		myExam.setNo(null);
		myExam.setVer(myExam.getVer() + 1);
		myExam.setUpdateUserId(getCurUser().getId());
		myExam.setUpdateTime(new Date());
		updateById(myExam);

		for (MyExamQuestion myQuestion : myQuestionList) {
			myQuestion.setAnswerTime(null);
			myQuestion.setUserAnswer(null);
			myQuestion.setUserScore(null);
			myQuestion.setMarkUserId(null);
			myQuestion.setMarkTime(null);
			myQuestion.setVer(myQuestion.getVer() + 1);
			myQuestion.setImgFileIds(null);
			myQuestion.setVideoFileIds(null);
			myQuestion.setUpdateUserId(getCurUser().getId());
			myQuestion.setUpdateTime(new Date());
			myQuestionService.updateById(myQuestion);
		}
	}

	private void paperValid(Integer examId, Integer userId) {
		if (!ValidateUtil.isValid(examId)) {
			throw new MyException("参数错误：examId");
		}
		if (!ValidateUtil.isValid(userId)) {
			throw new MyException("参数错误：userId");
		}

		MyExam myExam = examCacheService.getMyExam(examId, userId);
		if (myExam == null) {
			throw new MyException("试卷不存在");
		}
		Exam exam = examCacheService.getExam(examId);
		if (exam.getState() == 0) {
			throw new MyException("考试已删除");
		}
		if (exam.getState() == 2) {
			throw new MyException("考试已暂停");
		}
		if (exam.getStartTime().getTime() > System.currentTimeMillis()) {
			throw new MyException("考试未开始");
		}
	}

	private void answerSave(Integer questionId, String[] answers, MyExamQuestion myQuestion, Integer[] imgFileIds,
			Integer[] videoFileIds) {
		Question question = examCacheService.getQuestion(questionId);
		if (!ValidateUtil.isValid(answers)) {
			myQuestion.setUserAnswer(null);
		} else if (QuestionUtil.hasJudge(question)) {
			myQuestion.setUserAnswer(answers[0]);
		} else if (QuestionUtil.hasSingleChoice(question)) {
			if (ValidateUtil.isValid(myQuestion.getOptionsNo())) {
				myQuestion.setUserAnswer(myQuestion.getOptionsNoCacheOfAnswer().get(answers[0]));
			} else {
				myQuestion.setUserAnswer(answers[0]);
			}
		} else if (QuestionUtil.hasMultipleChoice(question)) {
			if (ValidateUtil.isValid(myQuestion.getOptionsNo())) {
				for (int i = 0; i < answers.length; i++) {
					answers[i] = myQuestion.getOptionsNoCacheOfAnswer().get(answers[i]);
				}
			}

			Arrays.sort(answers);// 页面先选d在选c，值为db，这里重新排序一下
			myQuestion.setUserAnswer(StringUtil.join(answers));
		} else if (QuestionUtil.hasFillBlank(question)) {
			myQuestion.setUserAnswer(StringUtil.join(answers, '\n'));
		} else if (QuestionUtil.hasQA(question)) {
			myQuestion.setUserAnswer(StringUtil.join(answers));// bug：主观问答题，文本包含英文逗号会分割，需要再合并一下
		}
		myQuestion.setAnswerTime(new Date());
		myQuestion.setImgFileIds(CollectionUtil.toList(imgFileIds));
		myQuestion.setVideoFileIds(CollectionUtil.toList(videoFileIds));
		myQuestion.setUpdateTime(new Date());
		myQuestion.setUpdateUserId(getCurUser().getId());
		myQuestionService.updateById(myQuestion);
	}

	private void answerFileSave(MyExamQuestion myQuestion) {
		myQuestion.getImgFileIds().forEach(imgFileId -> {
			fileService.upload(imgFileId);
		});
		myQuestion.getVideoFileIds().forEach(videoFileId -> {
			fileService.upload(videoFileId);
		});
	}

	private MyExamQuestion answerValid(Integer examId, Integer userId, Integer questionId, Integer[] imgFileIds,
			Integer[] videoFileIds) {
		if (!ValidateUtil.isValid(examId)) {
			throw new MyException("参数错误：examId");
		}
		if (!ValidateUtil.isValid(userId)) {
			throw new MyException("参数错误：userId");
		}
		if (!ValidateUtil.isValid(questionId)) {
			throw new MyException("参数错误：questionId");
		}
//		if (!ValidateUtil.isValid(answers)) {// 允许清空答案，比如多选
//			throw new MyException("参数错误：answers");
//		} 

		MyExamQuestion myQuestion = myQuestionService.getMyQuestion(examId, userId, questionId);
		if (myQuestion == null) {
			throw new MyException("未参与考试");
		}

		Exam exam = examCacheService.getExam(examId);
		if (exam.getState() == 0) {
			throw new MyException("考试已删除");
		}
		if (exam.getState() == 2) {
			throw new MyException("考试已暂停");
		}
		MyExam myExam = examCacheService.getMyExam(examId, userId);
		long curTime = System.currentTimeMillis();
		if (myExam.getAnswerStartTime().getTime() > curTime) {
			throw new MyException("考试未开始");
		}
		if (myExam.getAnswerEndTime().getTime() < curTime) {
			throw new MyException("考试已结束");
		}
		if (myExam.getState() == 3) {
			throw new MyException("已交卷");
		}
		Question question = examCacheService.getQuestion(questionId);
		if (ValidateUtil.isValid(imgFileIds)) {
			if (!(QuestionUtil.hasSubjective(question) && QuestionUtil.hasQA(question))) {
				throw new MyException("参数错误：imgFileIds");
			}
		}
		if (ValidateUtil.isValid(videoFileIds)) {
			if (!(QuestionUtil.hasSubjective(question) && QuestionUtil.hasQA(question))) {
				throw new MyException("参数错误：videoFileIds");
			}
		}
		return myQuestion;
	}

	private void finishValid(Integer examId, Integer userId) {
		if (!ValidateUtil.isValid(examId)) {
			throw new MyException("参数错误：examId");
		}
		if (!ValidateUtil.isValid(userId)) {
			throw new MyException("参数错误：userId");
		}
		MyExam myExam = examCacheService.getMyExam(examId, userId);
		if (myExam == null) {
			throw new MyException("未参与考试");
		}
		if (myExam.getState() == 1) {// 考试时间内，只要进入考试，状态就是2
			throw new MyException("未考试");
		}
		if (myExam.getState() == 3) {
			throw new MyException("已交卷");
		}
		Exam exam = examCacheService.getExam(examId);
		if (exam.getState() == 0) {
			throw new MyException("考试已删除");
		}
		if (exam.getState() == 2) {
			throw new MyException("考试已暂停");
		}
		long curTime = System.currentTimeMillis();
		if (myExam.getAnswerStartTime().getTime() > curTime) {
			throw new MyException("考试未开始");
		}
//		if (myExam.getExamEndTime().getTime() < curTime + 1) {// 预留1秒网络延时
//			throw new MyException("考试已结束");// 服务部署在低配的电脑上，如果有高并发，可以会造成延时1秒也不够
//		}// 所以只要状态是考试中，就可以更新交卷时间
	}

	/**
	 * 拆分分数<br/>
	 * 1分 拆分成2份，结果：0.5、0.5<br/>
	 * 1分 拆分成3份，结果：0.33、0.33、0.34<br/>
	 * 
	 * v1.0 zhanghc 2022年5月30日下午4:46:25
	 * 
	 * @param score
	 * @param num
	 * @return List<BigDecimal>
	 */
	private List<BigDecimal> splitScore(BigDecimal score, int num) {
		BigDecimal[] scores = new BigDecimal[num];
		BigDecimal singleScore = BigDecimalUtil.newInstance(score).div(num, 2).getResult();
		for (int i = 0; i < num - 1; i++) {
			scores[i] = singleScore;
		}
		scores[num - 1] = BigDecimalUtil.newInstance(singleScore).mul(num - 1).sub(score).mul(-1).getResult();
		return Arrays.asList(scores);
	}

	private List<Integer> shuffleNums(int start, int end) {
		Integer[] shuffleNums = new Integer[end - start + 1];
		for (int i = 0; i < shuffleNums.length; i++) {
			shuffleNums[i] = start + i;
		}

		ArrayUtils.shuffle(shuffleNums);
		return Arrays.asList(shuffleNums);
	}
}
