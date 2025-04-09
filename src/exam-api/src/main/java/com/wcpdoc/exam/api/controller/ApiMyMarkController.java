package com.wcpdoc.exam.api.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.base.util.CurLoginUserUtil;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.ExamQuestion;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.service.ExamCacheService;
import com.wcpdoc.exam.core.service.ExamQuestionService;
import com.wcpdoc.exam.core.service.MyMarkService;
import com.wcpdoc.exam.core.util.MyExamUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 我的阅卷控制层
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
@RestController
@RequestMapping("/api/myMark")
@Slf4j
public class ApiMyMarkController extends BaseController {

	@Resource
	private MyMarkService myMarkService;
	@Resource
	private ExamCacheService examCacheService;
	@Resource
	private ExamQuestionService examQuestionService;
	@Resource
	private BaseCacheService baseCacheService;

	/**
	 * 我的阅卷列表
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * 
	 * @param pageIn
	 * @return PageResult
	 */
	@RequestMapping("/listpage")
	public PageResult listpage(PageIn pageIn) {
		try {
			if (CurLoginUserUtil.isAdmin()) {// 管理员看所有

			} else if (CurLoginUserUtil.isSubAdmin()) {// 子管理员看自己
				pageIn.addParm("subAdminUserId", getCurUser().getId());
			} else if (CurLoginUserUtil.isMarkUser()) {// 阅卷用户看（管理或子管理）分配的
				pageIn.addParm("markUserId", getCurUser().getId());
			} else if (CurLoginUserUtil.isExamUser()) {// 考试用户没有权限
				throw new MyException("无权限");
			}
			PageOut pageOut = myMarkService.getListpage(pageIn);
			for (Map<String, Object> map : pageOut.getList()) {
				if (map.get("examSxes") == null) {
					map.put("examSxes", new Integer[0]);
				} else {
					map.put("examSxes", ((String) map.get("examSxes")).split(","));
				}
			}
			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("我的阅卷列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 我的考试
	 * 
	 * v1.0 zhanghc 2022年11月2日下午2:38:55
	 * 
	 * @param examId
	 * @param userId
	 * @return PageResult
	 */
	@RequestMapping("/get")
	public PageResult get(Integer examId, Integer userId) {
		try {
			MyExam myExam = examCacheService.getMyExam(examId, userId);
			if (myExam == null) {
				throw new MyException("无查阅权限");
			}
			Exam exam = examCacheService.getExam(examId);
			return PageResultEx.ok()// 考试用户没有exam/get权限，所以字段在这里回显
					.addAttr("answerStartTime", myExam.getAnswerStartTime())
					.addAttr("answerEndTime", myExam.getAnswerEndTime())//
					.addAttr("markStartTime", myExam.getMarkStartTime())//
					.addAttr("markEndTime", myExam.getMarkEndTime())//
					.addAttr("objectiveScore",
							MyExamUtil.totalScoreShow(exam, myExam) ? myExam.getObjectiveScore() : null)//
					.addAttr("totalScore", MyExamUtil.totalScoreShow(exam, myExam) ? myExam.getTotalScore() : null)//
					.addAttr("answerState", MyExamUtil.totalScoreShow(exam, myExam) ? myExam.getAnswerState() : null)//
					.addAttr("state", myExam.getState())//
					.addAttr("markState", myExam.getMarkState())//
					.addAttr("no", exam.getRankState() == 1 ? myExam.getNo() : null)//
					.addAttr("userNum", exam.getUserIds().size());
		} catch (MyException e) {
			log.error("我的考试错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("我的考试错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 阅卷列表
	 * 
	 * v1.0 zhanghc 2025年2月16日上午11:56:47
	 * 
	 * @param pageIn
	 * @return PageResult
	 */
	@RequestMapping("/markList")
	public PageResult markList(Integer examId) {
		try {
			Exam exam = examCacheService.getExam(examId);
			List<Map<String, Object>> result = examCacheService.getMyExamList(examId).stream()//
					.filter(myExam -> {
						if (CurLoginUserUtil.isAdmin()) {// 管理员
							if (exam.getMarkState() == 3// 整场考试结束看所有
									|| (myExam.getMarkUserId() != null
											&& myExam.getMarkUserId().intValue() == getCurUser().getId().intValue())) {// 只看自己领取的
								return true;
							}
						} else if (CurLoginUserUtil.isSubAdmin()) {// 子管理员
							if ((exam.getMarkState() == 3 && exam.getCreateUserId().intValue() == getCurUser().getId().intValue())// 整场考试结束并且自己创建的
									|| (myExam.getMarkUserId() != null
											&& myExam.getMarkUserId().intValue() == getCurUser().getId().intValue())) {// 只看自己领取的
								return true;
							}
						} else if (CurLoginUserUtil.isMarkUser()) {// 阅卷用户看自己领取的
							return myExam.getMarkUserId().intValue() == getCurUser().getId();
						}
						return false;
					}).map(myExam -> {
						Map<String, Object> data = new HashMap<>();
						User examUser = baseCacheService.getUser(myExam.getUserId());
						data.put("examUserId", examUser.getId());
						data.put("examUserName", examUser.getName());

						User markUser = null;
						data.put("markUserId", null);
						data.put("markUserName", null);
						if (myExam.getMarkUserId() != null) {
							markUser = baseCacheService.getUser(myExam.getMarkUserId());
							data.put("markUserId", markUser.getId());
							data.put("markUserName", markUser.getName());
						}

						data.put("myExamState", myExam.getState());
						data.put("myExamMarkState", myExam.getMarkState());
						return data;
					}).collect(Collectors.toList());
			return PageResultEx.ok().data(result);
		} catch (Exception e) {
			log.error("阅卷列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 领取信息
	 * 
	 * v1.0 zhanghc 2025年2月24日下午12:22:49
	 * 
	 * @param examId
	 * @return PageResult
	 */
	@RequestMapping("/claimInfo")
	public PageResult claimInfo(Integer examId) {
		try {
			List<MyExam> myExamList = examCacheService.getMyExamList(examId);
			int paperNum = myExamList.size();
			long markNum = myExamList.stream()//
					.filter(myExam -> myExam.getMarkUserId() != null
							|| (myExam.getState() == 1 && myExam.getMarkState() == 3))//
					.count();
			long myClaimNum = myExamList.stream()//
					.filter(myExam -> myExam.getMarkUserId() != null//
							&& myExam.getMarkUserId().intValue() == getCurUser().getId().intValue())//
					.count();
			long myMarkNum = myExamList.stream()//
					.filter(myExam -> myExam.getMarkUserId() != null//
							&& myExam.getMarkUserId().intValue() == getCurUser().getId().intValue()//
							&& myExam.getMarkState() == 3)//
					.count();

			return PageResultEx.ok()//
					.addAttr("paperNum", paperNum).addAttr("markNum", markNum).addAttr("myClaimNum", myClaimNum)
					.addAttr("myMarkNum", myMarkNum);
		} catch (Exception e) {
			log.error("阅卷列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 试卷领取
	 * 
	 * v1.0 zhanghc 2023年2月23日下午2:31:16
	 * 
	 * @param examId 考试ID
	 * @param num    分配份数
	 * @return PageResult
	 */
	@RequestMapping("/claim")
	public PageResult claim(Integer examId, Integer num) {
		try {
			myMarkService.claim(examId, num);
			return PageResultEx.ok();
		} catch (MyException e) {
			log.error("试卷分配错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("试卷分配错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 获取用户试卷
	 * 
	 * v1.0 zhanghc 2022年5月18日下午1:21:07
	 * 
	 * @param examId
	 * @param userId
	 * @return PageResult
	 */
	@RequestMapping("/paper")
	public PageResult paper(Integer examId, Integer userId) {
		try {
			return PageResultEx.ok().data(myMarkService.paper(examId, userId));
		} catch (MyException e) {
			log.error("获取用户试卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("获取用户试卷错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 打分
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * 
	 * @param examId     考试ID
	 * @param userId     考试用户ID
	 * @param questionId 试题ID
	 * @param userScore  用户得分
	 * @return PageResult
	 */
	@RequestMapping("/score")
	public PageResult score(Integer examId, Integer userId, Integer questionId, BigDecimal userScore) {
		try {
			myMarkService.score(examId, userId, questionId, userScore);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("打分错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("打分错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 阅卷
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * 
	 * @param examId
	 * @param userId
	 * @return PageResult
	 */
	@RequestMapping("/finish")
	public PageResult finish(Integer examId, Integer userId) {
		try {
			myMarkService.finish(examId, userId);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("阅卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("阅卷错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 试题统计
	 * 
	 * v1.0 zhanghc 2025年2月15日下午12:06:41
	 * 
	 * @param examId
	 * @return PageResult
	 */
	@RequestMapping("/questionStatis")
	public PageResult questionStatis(Integer examId) {
		try {
			List<ExamQuestion> examQuestionList = examQuestionService.getList(examId);
			Map<Integer, Long> markTypeResult = examQuestionList.stream()//
					.filter(examQuestion -> examQuestion.getType() == 2)// 排除章节
					.map(examQuestion -> examCacheService.getQuestion(examQuestion.getQuestionId()))//
					.collect(Collectors.groupingBy(Question::getMarkType, Collectors.counting()));
			Map<String, Object> markTypeStatis = new HashMap<>();
			baseCacheService.getDictList()//
					.stream()//
					.filter(dict -> dict.getDictIndex().equals("PAPER_MARK_TYPE"))//
					.forEach(dict -> {
						String key = dict.getDictKey().equals("1") ? "objective" : "subjective";
						Long value = markTypeResult.get(Integer.parseInt(dict.getDictKey()));
						markTypeStatis.put(key, value == null ? 0 : value);
					});

			Map<Integer, Long> typeCountMap = examQuestionList.stream()//
					.filter(examQuestion -> examQuestion.getType() == 2)// 排除章节
					.map(examQuestion -> examCacheService.getQuestion(examQuestion.getQuestionId()))//
					.collect(Collectors.groupingBy(Question::getType, Collectors.counting()));

			List<Map<String, Object>> typeStatis = baseCacheService.getDictList()//
					.stream()//
					.filter(dict -> dict.getDictIndex().equals("QUESTION_TYPE"))//
					.map(dict -> {//
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("type", dict.getDictKey());
						Long value = typeCountMap.get(Integer.parseInt(dict.getDictKey()));
						data.put("count", value == null ? 0 : value);
						return data;
					})//
					.collect(Collectors.toList());

			markTypeStatis.put("chapter", examQuestionList.stream()//
					.filter(examQuestion -> examQuestion.getType() == 1)// 获取章节数量
					.count());// 临时追加字段，后续拆解 2025-02-14 zhc
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
}