package com.wcpdoc.exam.api.controller;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.base.entity.Dict;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.entity.Exer;
import com.wcpdoc.exam.core.entity.MyExer;
import com.wcpdoc.exam.core.entity.MyExerQuestion;
import com.wcpdoc.exam.core.entity.MyFavQuestion;
import com.wcpdoc.exam.core.entity.MyWrongQuestion;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.entity.ex.QuestionPart;
import com.wcpdoc.exam.core.service.ExamCacheService;
import com.wcpdoc.exam.core.service.ExerService;
import com.wcpdoc.exam.core.service.MyExerQuestionService;
import com.wcpdoc.exam.core.service.MyExerService;
import com.wcpdoc.exam.core.service.MyFavQuestionService;
import com.wcpdoc.exam.core.service.MyWrongQuestionService;
import com.wcpdoc.exam.core.service.QuestionBankService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.util.QuestionUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 我的练习控制层
 * 
 * v1.0 zhanghc 2025年6月8日下午9:22:47
 */
@RestController
@RequestMapping("/api/myExer")
@Slf4j
public class ApiMyExerController extends BaseController {

	@Resource
	private MyExerService myExerService;
	@Resource
	private ExerService exerService;
	@Resource
	private QuestionBankService questionBankService;
	@Resource
	private BaseCacheService baseCacheService;
	@Resource
	private MyExerQuestionService myExerQuestionService;
	@Resource
	private ExamCacheService examCacheService;
	@Resource
	private QuestionService questionService;
	@Resource
	private MyWrongQuestionService myWrongQuestionService;
	@Resource
	private MyFavQuestionService myFavQuestionService;

	/**
	 * 我的练习列表
	 * 
	 * v1.0 zhanghc 2025年6月8日下午9:22:47
	 * 
	 * @param pageIn
	 * @return pageOut
	 */
	@RequestMapping("/listpage")
	public PageResult listpage(PageIn pageIn) {
		try {
			pageIn.addParm("curUserId", getCurUser().getId());
			PageOut pageOut = myExerService.getListpage(pageIn);
			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("我的练习列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 我的练习添加
	 * 
	 * v1.0 zhanghc 2025年9月25日上午10:51:30
	 * 
	 * @param user
	 * @return PageResult
	 */
	@RequestMapping("/add")
	public PageResult add(MyExer myExer) {
		try {
			myExerService.add(myExer);
			return PageResultEx.ok().data(myExer.getId());
		} catch (MyException e) {
			log.error("我的练习添加错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("我的练习添加错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 我的练习获取
	 * 
	 * v1.0 zhanghc 2025年9月26日上午10:54:21
	 * 
	 * @param exerId
	 * @return PageResult
	 */
	@RequestMapping("/get")
	public PageResult get(Integer exerId) {
		try {
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
			User curUser = baseCacheService.getUser(getCurUser().getId());
			if (!(exer.getOrgIds().contains(curUser.getOrgId()) || exer.getUserIds().contains(curUser.getId()))) {
				throw new MyException("无操作权限");
			}

			// 查询
			List<MyExer> myExerList = myExerService.getList(getCurUser().getId(), exerId);
			List<MyExerQuestion> myExerQuestionList = myExerList.stream().map(MyExer::getId)
					.map(myExerQuestionService::getList).flatMap(List::stream).collect(Collectors.toList());// 获取自己练习关联的所有试题（试题会有重复）

			List<Question> questionList = exer.getQuestionBankIds().stream().map(questionService::getList)
					.flatMap(List::stream).collect(Collectors.toList());// 获取练习关联的多个题库下的所有试题
			Map<Integer, List<Question>> questionGroup = questionList.stream()
					.collect(Collectors.groupingBy(Question::getType));

			Set<Integer> answerQuestionIds = myExerQuestionList.stream()
					.filter(meq -> ValidateUtil.isValid(meq.getUserScore())).map(MyExerQuestion::getQuestionId)
					.collect(Collectors.toSet()); // 用户创建练习后已答的试题（练习A练习了试题A，练习B没练习试题A，实际算已练习）
			List<Dict> dictList = baseCacheService.getDictList();

			List<Map<String, Object>> free = dictList.stream()//
					.filter(dict -> dict.getDictIndex().equals("QUESTION_TYPE"))//
					.map(dict -> {//
						List<Question> qList = questionGroup.get(Integer.parseInt(dict.getDictKey()));
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("type", dict.getDictKey());
						data.put("count", qList == null ? 0 : qList.size()); // 每个试题类型的数量
						return data;
					})//
					.collect(Collectors.toList());

			List<Map<String, Object>> unExer = dictList.stream()//
					.filter(dict -> dict.getDictIndex().equals("QUESTION_TYPE"))//
					.map(dict -> {//
						List<Question> qList = questionGroup.get(Integer.parseInt(dict.getDictKey()));
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("type", dict.getDictKey());
						data.put("count",
								qList == null ? 0
										: qList.stream().filter(q -> !answerQuestionIds.contains(q.getId()))
												.collect(Collectors.toSet()).size());// 未练数量 = 试题数量 - 已练数量
						return data;
					})//
					.collect(Collectors.toList());

			List<MyWrongQuestion> myWrongQuestionList = myWrongQuestionService.getList(getCurUser().getId());
			Set<Integer> errQuestionIds = myWrongQuestionList.stream().map(MyWrongQuestion::getQuestionId)
					.collect(Collectors.toSet());
			List<Map<String, Object>> wrong = dictList.stream()//
					.filter(dict -> dict.getDictIndex().equals("QUESTION_TYPE"))//
					.map(dict -> {//
						List<Question> qList = questionGroup.get(Integer.parseInt(dict.getDictKey()));
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("type", dict.getDictKey());
						data.put("count",
								qList == null ? 0
										: qList.stream().filter(q -> errQuestionIds.contains(q.getId()))
												.collect(Collectors.toSet()).size());// 错题数量
						return data;
					})//
					.collect(Collectors.toList());

			List<MyFavQuestion> myFavQuestionList = myFavQuestionService.getList(getCurUser().getId());
			Set<Integer> favQuestionIds = myFavQuestionList.stream().map(MyFavQuestion::getQuestionId)
					.collect(Collectors.toSet());
			List<Map<String, Object>> fav = dictList.stream()//
					.filter(dict -> dict.getDictIndex().equals("QUESTION_TYPE"))//
					.map(dict -> {//
						List<Question> qList = questionGroup.get(Integer.parseInt(dict.getDictKey()));
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("type", dict.getDictKey());
						data.put("count",
								qList == null ? 0
										: qList.stream().filter(q -> favQuestionIds.contains(q.getId()))
												.collect(Collectors.toSet()).size());// 收藏数量
						return data;
					})//
					.collect(Collectors.toList());

			return PageResultEx.ok()//
					.addAttr("free", free)//
					.addAttr("unExer", unExer)//
					.addAttr("wrong", wrong)//
					.addAttr("fav", fav)//
			;
		} catch (MyException e) {
			log.error("我的练习获取错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("我的练习获取错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 我的练习试题列表
	 * 
	 * v1.0 zhanghc 2025年9月26日下午4:15:06
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/questionList")
	public PageResult questionList(Integer id) {
		try {
			// 数据校验
			if (!ValidateUtil.isValid(id)) {
				throw new MyException("参数错误：id");
			}
			MyExer myExer = myExerService.getById(id);
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
			if (!(exer.getUserIds().contains(curUser.getId()) || exer.getOrgIds().contains(curUser.getOrgId()))) {
				throw new MyException("无操作权限");
			}

			// 试题列表
			List<MyExerQuestion> myExerQuestionList = myExerQuestionService.getList(id);
			List<Map<String, Object>> resultList = myExerQuestionList.stream().map(myExerQuestion -> {
				Map<String, Object> data = new HashMap<>();
				data.put("questionId", myExerQuestion.getQuestionId());
				data.put("no", myExerQuestion.getNo());
				data.put("score", myExerQuestion.getScore());// 如果不是第一次显示，答题卡需要颜色标记分数状态
				data.put("userScore", myExerQuestion.getUserScore());// 至于每道题的答案，由前端用滑动窗口的方式批量加载。
				return data;
			}).collect(Collectors.toList());
			return PageResultEx.ok().data(resultList);
		} catch (MyException e) {
			log.error("我的练习试题列表错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("我的练习试题列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 我的练习试题
	 * 
	 * v1.0 zhanghc 2025年6月8日下午9:22:47
	 * 
	 * @param exerId
	 * @param questionId
	 * @return PageResult
	 */
	@RequestMapping("/question")
	public PageResult question(Integer id, Integer questionId) {
		try {
			// 数据校验
			if (!ValidateUtil.isValid(id)) {
				throw new MyException("参数错误：id");
			}
			MyExer myExer = myExerService.getById(id);
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

			// 查询练习试题
			MyExerQuestion myExerQuestion = myExerQuestionService.getMyExerQuestion(id, questionId);
			Question question = examCacheService.getQuestion(myExerQuestion.getQuestionId());
			QuestionPart questionPart = new QuestionPart();
			questionPart.setType(2);
			questionPart.setQuestionId(question.getId());
			questionPart.setQuestionType(question.getType());
			questionPart.setMarkType(question.getMarkType());
			questionPart.setTitle(question.getTitle());
			questionPart.setImgFileIds(question.getImgFileIds());
			questionPart.setVideoFileId(question.getVideoFileId());
			questionPart.setMarkOptions(myExerQuestion.getMarkOptions());// 使用练习的阅卷选项
			questionPart.setScore(myExerQuestion.getScore());// 使用练习的分数
			questionPart.setUserScore(myExerQuestion.getUserScore());// 使用练习的用户分数
			if (QuestionUtil.hasSingleChoice(question) || QuestionUtil.hasMultipleChoice(question)) {// 组装试题选项
				List<QuestionOption> questionOptionList = examCacheService.getQuestionOptionList(question.getId());
				for (QuestionOption questionOption : questionOptionList) {
					questionPart.getOptions().add(questionOption.getOptions());
				}

			}

			{// 组装标准答案
				questionPart.setAnalysis(question.getAnalysis());
				List<QuestionAnswer> questionAnswerList = examCacheService.getQuestionAnswerList(question.getId());
				for (QuestionAnswer answer : questionAnswerList) {
					if (QuestionUtil.hasJudge(question)
							|| (QuestionUtil.hasQA(question) && QuestionUtil.hasSubjective(question))) {
						questionPart.getAnswers().add(answer.getAnswer());
					} else if (QuestionUtil.hasSingleChoice(question)) {
						questionPart.getAnswers().add(answer.getAnswer());
					} else if (QuestionUtil.hasMultipleChoice(question)) {
						String[] answers = answer.getAnswer().split(",");
						Collections.addAll(questionPart.getAnswers(), answers);
					} else if (QuestionUtil.hasFillBlank(question)
							|| (QuestionUtil.hasQA(question) && QuestionUtil.hasObjective(question))) {
						questionPart.getAnswers().add(answer.getAnswer());
					}
				}
			}

			{// 组装用户答案
				if (ValidateUtil.isValid(myExerQuestion.getUserAnswer())) {
					if (QuestionUtil.hasJudge(question) || QuestionUtil.hasQA(question)) {
						questionPart.getUserAnswers().add(myExerQuestion.getUserAnswer());
					} else if (QuestionUtil.hasSingleChoice(question)) {
						questionPart.getUserAnswers().add(myExerQuestion.getUserAnswer());
					} else if (QuestionUtil.hasMultipleChoice(question)) {
						String[] userAnswers = myExerQuestion.getUserAnswer().split(",");
						Collections.addAll(questionPart.getUserAnswers(), userAnswers);
					} else if (QuestionUtil.hasFillBlank(question)) {
						Collections.addAll(questionPart.getUserAnswers(),
								myExerQuestion.getUserAnswer().split("\n", -1));
					}
				}
			}
			return PageResultEx.ok().data(questionPart);
		} catch (MyException e) {
			log.error("我的练习试题错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("我的练习试题错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 我的练习答题
	 * 
	 * v1.0 zhanghc 2025年6月17日下午7:01:26
	 * 
	 * @param id
	 * @param questionId
	 * @param userAnswers
	 * @param userScore
	 * @return PageResult
	 */
	@RequestMapping("/answer")
	public PageResult answer(Integer id, Integer questionId, String[] userAnswers, BigDecimal userScore) {
		try {
			return PageResultEx.ok().data(myExerService.answer(id, questionId, userAnswers, userScore));
		} catch (MyException e) {
			log.error("我的练习答题错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("我的练习答题错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 我的练习试题收藏
	 * 
	 * v1.0 zhanghc 2025年6月25日下午9:53:21
	 * 
	 * @param id
	 * @param questionId
	 * @return PageResult
	 */
	@RequestMapping("/questionFav")
	public PageResult fav(Integer id, Integer questionId) {
		try {
			// 数据校验
			if (!ValidateUtil.isValid(id)) {
				throw new MyException("参数错误：id");
			}
			if (!ValidateUtil.isValid(questionId)) {
				throw new MyException("参数错误：questionId");
			}
			MyExerQuestion myExerQuestion = myExerQuestionService.getMyExerQuestion(id, questionId);
			if (myExerQuestion == null) {
				throw new MyException("无操作权限");
			}
			MyExer myExer = myExerService.getById(id);
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

			// 收藏
			MyFavQuestion myFavQuestion = myFavQuestionService.getMyFavQuestion(getCurUser().getId(), questionId);
			if (myFavQuestion == null) {
				myFavQuestion = new MyFavQuestion();
				myFavQuestion.setUserId(getCurUser().getId());
				myFavQuestion.setQuestionId(questionId);
				myFavQuestion.setQuestionType(myExerQuestion.getQuestionType());
				myFavQuestion.setFavTime(new Date());
				myFavQuestion.setFavSource(exer.getName());
				myFavQuestion.setUpdateUserId(getCurUser().getId());
				myFavQuestion.setUpdateTime(new Date());
				myFavQuestionService.save(myFavQuestion);
			} else {
				myFavQuestionService.removeById(myFavQuestion.getId());
			}
			return PageResult.ok();
		} catch (MyException e) {
			log.error("我的练习试题收藏错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("我的练习试题收藏错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 我的练习试题收藏列表
	 * 
	 * v1.0 zhanghc 2025年9月29日下午9:48:29
	 * 
	 * @return PageResult
	 */
	@RequestMapping("/favQuestionList")
	public PageResult questionFavList() {
		try {
			List<MyFavQuestion> myFavQuestionList = myFavQuestionService.getList(getCurUser().getId());
			return PageResultEx.ok()
					.data(myFavQuestionList.stream().map(MyFavQuestion::getQuestionId).collect(Collectors.toList()));
		} catch (MyException e) {
			log.error("我的练习收藏列表错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("我的练习收藏列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 我的练习错题列表
	 * 
	 * v1.0 zhanghc 2025年9月29日下午9:48:29
	 * 
	 * @return PageResult
	 */
	@RequestMapping("/wrongQuestionList")
	public PageResult wrongQuestionList() {
		try {
			List<MyWrongQuestion> myFavQuestionList = myWrongQuestionService.getList(getCurUser().getId());
			return PageResultEx.ok().data(myFavQuestionList.stream().map(myFavQuestion -> {
				Map<String, Object> data = new HashMap<>();
				data.put("questionId", myFavQuestion.getQuestionId());
				data.put("wrongNum", myFavQuestion.getWrongNum());
				return data;
			}).collect(Collectors.toList()));
		} catch (MyException e) {
			log.error("我的练习试题收藏列表错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("我的练习试题收藏列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 我的练习错题重置
	 * 
	 * v1.0 zhanghc 2025年6月25日下午11:26:03
	 * 
	 * @param id
	 * @param questionId
	 * @return PageResult
	 */
	@RequestMapping("/wrongQuestionReset")
	public PageResult wrongQuestionReset(Integer id, Integer questionId) {
		try {
			// 数据校验
			if (!ValidateUtil.isValid(id)) {
				throw new MyException("参数错误：id");
			}
			if (!ValidateUtil.isValid(questionId)) {
				throw new MyException("参数错误：questionId");
			}
			MyExerQuestion myExerQuestion = myExerQuestionService.getMyExerQuestion(id, questionId);
			if (myExerQuestion == null) {
				throw new MyException("无操作权限");
			}
			MyExer myExer = myExerService.getById(id);
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
			MyWrongQuestion myWrongQuestion = myWrongQuestionService.getMyWrongQuestion(getCurUser().getId(),
					questionId);
			if (myWrongQuestion == null) {
				throw new MyException("参数错误：questionId");
			}

			// 错题重置
			myWrongQuestion.setState(1);
			myWrongQuestion.setUpdateUserId(getCurUser().getId());
			myWrongQuestion.setUpdateTime(new Date());
			myWrongQuestionService.updateById(myWrongQuestion);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("我的练习错题重置错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("我的练习错题重置错误：", e);
			return PageResult.err();
		}
	}

//	/**
//	 * 评论列表
//	 * 
//	 * v1.0 chenyun 2021年8月31日上午9:54:28
//	 * 
//	 * @param pageIn
//	 * @return PageResult
//	 */
//	@RequestMapping("/rmkListpage")
//	public PageResult rmkListpage(PageIn pageIn) {
//		try {
//			PageOut pageOut = exerRmkService.getListpage(pageIn);
//			for (Map<String, Object> map : pageOut.getList()) {
//				if (map.get("likeUserIds") != null
//						&& map.get("likeUserIds").toString().contains(String.format(",%s,", getCurUser().getId()))) {
//					map.put("hasLike", true);
//				} else {
//					map.put("hasLike", false);
//				}
//			}
//			return PageResultEx.ok().data(pageOut);
//		} catch (Exception e) {
//			log.error("评论列表错误：", e);
//			return PageResult.err();
//		}
//	}
//
//	/**
//	 * 评论
//	 * 
//	 * v1.0 chenyun 2021年8月31日上午9:54:28
//	 * 
//	 * @param exerRmk 评论
//	 * @param anon    是否匿名（true：是；false：否）
//	 * @return PageResult
//	 */
//	@RequestMapping("/rmk")
//	public PageResult rmk(ExerRmk exerRmk, Boolean anon) {
//		try {
//			exerRmkService.rmk(exerRmk, anon);
//			return PageResultEx.ok().addAttr("id", exerRmk.getId());
//		} catch (MyException e) {
//			log.error("评论错误：{}", e.getMessage());
//			return PageResult.err().msg(e.getMessage());
//		} catch (Exception e) {
//			log.error("评论错误：", e);
//			return PageResult.err();
//		}
//	}
//
//	/**
//	 * 评论点赞
//	 * 
//	 * v1.0 zhanghc 2023年4月17日下午7:52:03
//	 * 
//	 * @param exerRmkId 评论ID
//	 * @return PageResult
//	 */
//	@RequestMapping("/rmkLike")
//	public PageResult rmkLike(Integer exerRmkId) {
//		try {
//			exerRmkService.like(exerRmkId);
//			return PageResult.ok();
//		} catch (MyException e) {
//			log.error("评论点赞错误：{}", e.getMessage());
//			return PageResult.err().msg(e.getMessage());
//		} catch (Exception e) {
//			log.error("评论点赞错误：", e);
//			return PageResult.err();
//		}
//	}

	/**
	 * 我的练习跟踪
	 * 
	 * v1.0 zhanghc 2025年9月4日下午1:14:56
	 * 
	 * @param exerId 练习ID
	 * @return PageResult
	 */
	@RequestMapping("/track")
	public PageResult track(Integer exerId) {
		try {
			myExerService.track(exerId);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("我的练习跟踪错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("我的练习跟踪错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 我的练习跟踪列表
	 * 
	 * v1.0 zhanghc 2025年9月9日下午3:33:51
	 * 
	 * @param exerId
	 * @param startDate yyyy-MM-dd
	 * @param endDate   yyyy-MM-dd
	 * @return PageResult
	 */
	@RequestMapping("/trackList")
	public PageResult trackList(Integer exerId, String startDate, String endDate) {
		try {
			List<Map<String, Object>> myExerTrackList = myExerService.getTrackList(exerId, startDate, endDate).stream()
					.map(myExerTrack -> {
						Map<String, Object> map = new HashMap<>();
						map.put("ymd", String.format("%08d", myExerTrack.getYmd())
								.replaceAll("(\\d{4})(\\d{2})(\\d{2})", "$1-$2-$3"));
						map.put("minuteTicks", myExerTrack.getMinuteTicks());
						map.put("minuteCount", myExerTrack.getMinuteCount());
						return map;
					}).collect(Collectors.toList());
			return PageResultEx.ok().data(myExerTrackList);
		} catch (MyException e) {
			log.error("我的练习跟踪列表错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("我的练习跟踪列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 我的练习跟踪月度列表
	 * 
	 * v1.0 zhanghc 2025年9月12日下午9:54:43
	 * 
	 * @param exerId
	 * @param startYm yyyy-MM
	 * @param endYm   yyyy-MM
	 * @return PageResult
	 */
	@RequestMapping("/trackMonthlyList")
	public PageResult trackMonthlyList(Integer exerId, String startYm, String endYm) {
		try {
			List<Map<String, Object>> myExerTrackMonthlyList = myExerService.getTrackMonthlyList(exerId, startYm, endYm)
					.stream().map(myExerTrackMonthly -> {
						Map<String, Object> map = new HashMap<>();
						map.put("ym", String.format("%06d", myExerTrackMonthly.getYm()).replaceAll("(\\d{4})(\\d{2})",
								"$1-$2"));
						map.put("minuteCount", myExerTrackMonthly.getMinuteCount());
						return map;
					}).collect(Collectors.toList());
			return PageResultEx.ok().data(myExerTrackMonthlyList);
		} catch (MyException e) {
			log.error("我的练习跟踪月度列表错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("我的练习跟踪月度列表错误：", e);
			return PageResult.err();
		}
	}
}
