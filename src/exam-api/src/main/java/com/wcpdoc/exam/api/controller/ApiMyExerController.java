package com.wcpdoc.exam.api.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
import com.wcpdoc.exam.core.entity.MyExerQuestion;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.entity.QuestionBank;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.entity.ex.QuestionPart;
import com.wcpdoc.exam.core.service.ExamCacheService;
import com.wcpdoc.exam.core.service.ExerService;
import com.wcpdoc.exam.core.service.MyExerQuestionService;
import com.wcpdoc.exam.core.service.MyExerService;
import com.wcpdoc.exam.core.service.QuestionBankService;
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
	 * 我的练习拉取
	 * 
	 * v1.0 zhanghc 2025年6月15日上午10:04:37
	 * 
	 * @param exerId
	 * @param userId
	 * @return PageResult
	 */
	@RequestMapping("/pull")
	public PageResult pull(Integer exerId) {
		try {
			return PageResultEx.ok().data(myExerService.pull(exerId, getCurUser().getId()));
		} catch (MyException e) {
			log.error("我的练习拉取错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("我的练习拉取错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 我的练习生成
	 * 
	 * v1.0 zhanghc 2025年6月15日下午4:39:09
	 * 
	 * @param exerId 练习ID
	 * @param type   类型（1：单选题；2：多选题；3：填空题；4：判断题；5：问答题；11：历史错题；12：我的收藏）
	 * @return PageResult
	 */
	@RequestMapping("/generate")
	public PageResult generate(Integer exerId, Integer type) {
		try {
			List<MyExerQuestion> myExerQuestionList = myExerService.generate(exerId, getCurUser().getId(), type);
			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
			for (MyExerQuestion myExerQuestion : myExerQuestionList) {
				Map<String, Object> map = new HashMap<>();
				map.put("questionId", myExerQuestion.getQuestionId());
				map.put("no", myExerQuestion.getNo());
				map.put("shuffleNo", myExerQuestion.getShuffleNo());
				map.put("score", myExerQuestion.getScore());// 如果不是第一次显示，答题卡需要颜色标记分数状态
				map.put("userScore", myExerQuestion.getUserScore());// 至于每道题的答案，由前端用滑动窗口的方式批量加载。
				map.put("fav", myExerQuestion.getFav());
				map.put("wrongAnswerNum", myExerQuestion.getWrongAnswerNum());
				resultList.add(map);
			}
			return PageResultEx.ok().data(resultList);
		} catch (MyException e) {
			log.error("我的练习生成错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("我的练习生成错误：", e);
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
	public PageResult question(Integer exerId, Integer questionId) {
		try {
			// 数据校验
			Exer exer = exerService.getById(exerId);
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
			MyExerQuestion myExerQuestion = myExerQuestionService.getMyExerQuestion(exerId, getCurUser().getId(),
					questionId);
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
	 * @param exerId
	 * @param questionId
	 * @param userAnswers
	 * @param userScore
	 * @return PageResult
	 */
	@RequestMapping("/answer")
	public PageResult answer(Integer exerId, Integer questionId, String[] userAnswers, BigDecimal userScore) {
		try {
			return PageResultEx.ok()
					.data(myExerService.answer(exerId, getCurUser().getId(), questionId, userAnswers, userScore));
		} catch (MyException e) {
			log.error("我的练习答题错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("我的练习答题错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 我的练习重新练习
	 * 
	 * v1.0 zhanghc 2025年6月25日下午12:27:39
	 * 
	 * @param exerId 练习ID
	 * @param type   类型（1：单选题；2多选题；3：填空题；4：判断题；5：问答题；11：历史错题；12：我的收藏）
	 * @return PageResult
	 */
	@RequestMapping("/exerReset")
	public PageResult exerReset(Integer exerId, Integer type) {
		try {
			myExerService.exerReset(exerId, getCurUser().getId(), type);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("我的练习重新练习错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("我的练习重新练习错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 我的练习收藏
	 * 
	 * v1.0 zhanghc 2025年6月25日下午9:53:21
	 * 
	 * @param exerId
	 * @param questionId
	 * @return PageResult
	 */
	@RequestMapping("/fav")
	public PageResult fav(Integer exerId, Integer questionId) {
		try {
			MyExerQuestion myExerQuestion = myExerQuestionService.getMyExerQuestion(exerId, getCurUser().getId(),
					questionId);
			if (myExerQuestion.getFav() == 1) {
				myExerQuestion.setFav(2);
			} else {
				myExerQuestion.setFav(1);
			}
			myExerQuestionService.updateById(myExerQuestion);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("我的练习收藏错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("我的练习收藏错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 我的练习错题重置
	 * 
	 * v1.0 zhanghc 2025年6月25日下午11:26:03
	 * 
	 * @param exerId
	 * @param type
	 * @return PageResult
	 */
	@RequestMapping("/wrongReset")
	public PageResult wrongReset(Integer exerId, Integer questionId) {
		try {
			MyExerQuestion myExerQuestion = myExerQuestionService.getMyExerQuestion(exerId, getCurUser().getId(),
					questionId);
			myExerQuestion.setAnswerTime(null);
			myExerQuestion.setUserAnswer(null);
			myExerQuestion.setUserScore(null);
			myExerQuestion.setWrongAnswerNum(0);
			myExerQuestion.setUpdateTime(new Date());
			myExerQuestion.setUpdateUserId(getCurUser().getId());
			myExerQuestionService.updateById(myExerQuestion);
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
	 * 练习获取
	 * 
	 * v1.0 zhanghc 2025年6月10日下午8:05:41
	 * 
	 * @param exerId
	 * @return PageResult
	 */
	@RequestMapping("/exerGet")
	public PageResult exerGet(Integer exerId) {
		try {
			Exer exer = exerService.getById(exerId);
			QuestionBank questionBank = questionBankService.getById(exer.getQuestionBankId());
			return PageResultEx.ok()//
					.addAttr("id", exer.getId())//
					.addAttr("name", exer.getName())//
					.addAttr("questionBankId", exer.getQuestionBankId())//
					.addAttr("questionBankName", questionBank.getName())//
					.addAttr("state", exer.getState())//
					.addAttr("rmkState", exer.getRmkState())//
			;
		} catch (MyException e) {
			log.error("练习获取错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("练习获取错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 我的练习试题统计
	 * 
	 * v1.0 zhanghc 2025年6月9日下午9:02:28
	 * 
	 * @param exerId
	 * @return PageResult
	 */
	@RequestMapping("/questionStatis")
	public PageResult questionStatis(Integer exerId) {
		try {
			Exer exer = exerService.getById(exerId);
			User curUser = baseCacheService.getUser(getCurUser().getId());
			if (!(exer.getUserIds().contains(curUser.getId()) || exer.getOrgIds().contains(curUser.getOrgId()))) {
				throw new MyException("无操作权限");
			}
			QuestionBank questionBank = questionBankService.getById(exer.getQuestionBankId());

			List<Map<String, Object>> typeStatis = new ArrayList<>();
			Map<String, Object> typeMap = new HashMap<String, Object>();
			typeMap.put("type", 1);
			typeMap.put("count", questionBank.getSingleNum());
			typeStatis.add(typeMap);

			typeMap = new HashMap<String, Object>();
			typeMap.put("type", 2);
			typeMap.put("count", questionBank.getMultipleNum());
			typeStatis.add(typeMap);

			typeMap = new HashMap<String, Object>();
			typeMap.put("type", 3);
			typeMap.put("count", questionBank.getFillBlankObjNum() + questionBank.getFillBlankSubNum());
			typeStatis.add(typeMap);

			typeMap = new HashMap<String, Object>();
			typeMap.put("type", 4);
			typeMap.put("count", questionBank.getJudgeNum());
			typeStatis.add(typeMap);

			typeMap = new HashMap<String, Object>();
			typeMap.put("type", 5);
			typeMap.put("count", questionBank.getQaObjNum() + questionBank.getQaSubNum());
			typeStatis.add(typeMap);

			Map<String, Object> markTypeStatis = new HashMap<String, Object>();
			markTypeStatis.put("objective", questionBank.getObjectiveNum());
			markTypeStatis.put("subjective", questionBank.getSubjectiveNum());
			markTypeStatis.put("chapter", 0);
			return PageResultEx.ok()//
					.addAttr("markTypeStatis", markTypeStatis)//
					.addAttr("typeStatis", typeStatis);
		} catch (MyException e) {
			log.error("获取试题统计错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("获取试题统计错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 我的练习跟踪
	 * 
	 * v1.0 zhanghc 2025年9月4日下午1:14:56
	 * 
	 * @param exerId 练习ID
	 * @param type   类型（1：单选题；2多选题；3：填空题；4：判断题；5：问答题；11：历史错题；12：我的收藏）
	 * @return PageResult
	 */
	@RequestMapping("/track")
	public PageResult track(Integer exerId, Integer type) {
		try {
			myExerService.track(exerId, getCurUser().getId(), type);
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
			List<Map<String, Object>> myExerTrackList = myExerService
					.getTrackList(exerId, getCurUser().getId(), startDate, endDate).stream().map(myExerTrack -> {
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
}
