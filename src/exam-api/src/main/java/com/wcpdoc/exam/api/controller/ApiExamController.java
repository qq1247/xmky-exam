package com.wcpdoc.exam.api.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.wcpdoc.base.entity.Org;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.base.service.ProgressBarService;
import com.wcpdoc.base.util.CurLoginUserUtil;
import com.wcpdoc.core.context.UserContext;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.LoginUser;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.SpringUtil;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyMark;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.ex.ExamInfo;
import com.wcpdoc.exam.core.service.ExamCacheService;
import com.wcpdoc.exam.core.service.ExamQuestionService;
import com.wcpdoc.exam.core.service.ExamRuleService;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyMarkService;
import com.wcpdoc.exam.core.util.QuestionUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 考试控制层
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
@RestController
@RequestMapping("/api/exam")
@Slf4j
public class ApiExamController extends BaseController {

	@Resource
	private ExamService examService;
	@Resource
	private ExamCacheService examCacheService;
	@Resource
	private ExamQuestionService examQuestionService;
	@Resource
	private ExamRuleService examRuleService;
	@Resource
	private MyExamService myExamService;
	@Resource
	private MyMarkService myMarkService;
	@Resource
	private BaseCacheService baseCacheService;
	@Resource
	private ProgressBarService progressBarService;

	/**
	 * 考试列表
	 * 
	 * v1.0 zhanghc 2018年10月25日下午9:23:06
	 * 
	 * @return pageOut
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

			PageOut pageOut = examService.getListpage(pageIn);
			for (Map<String, Object> map : pageOut.getList()) {
				if (map.get("sxes") == null) {
					map.put("sxes", new Integer[0]);
				} else {
					map.put("sxes", ((String) map.get("sxes")).split(","));
				}
			}
			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("考试列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 考试发布
	 * 
	 * v1.0 zhanghc 2018年11月24日上午9:13:22
	 * 
	 * @param examInfo 考试信息（json）
	 * @return PageResult
	 */
	@RequestMapping("/publish")
	public PageResult publish(@RequestBody ExamInfo examInfo) {
		try {
			ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
					.getRequestAttributes();
			RequestContextHolder.setRequestAttributes(requestAttributes, true);// 子线程共享请求属性

			String processBarId = UUID.randomUUID().toString().replaceAll("-", "");
			progressBarService.setProgressBar(processBarId, 0.0, 1.0, HttpStatus.OK.value(), "发布开始", null);// 放在前面，可能的问题为下面的线程执行慢，没有进度数据，前端显示空
			Double processLen = (examInfo.getExamUserIds().size() + 5) * 1.0;// 校验前数据处理+1，校验数据+1，保存考试+1，保存试卷+1，在业务层完成+1（事务内完成100%可能页面没刷新到），考试用户数量+userNum
			LoginUser loginUser = getCurUser();
			new Thread(new Runnable() {
				public void run() {
					UserContext.set(loginUser);// 子线程不走springboot拦截器，人工模拟拦截器，线程上绑定当前登录信息
					try {
						SpringUtil.getBean(ExamService.class).publish(examInfo, processBarId);
						progressBarService.setProgressBar(processBarId, processLen, processLen, HttpStatus.OK.value(),
								"发布成功", examInfo.getId());// 放在业务层最后一行，进度已经100%，数据还没有完全插入，这时查询数据库时为空
					} catch (Exception e) {
						progressBarService.setProgressBar(processBarId, 99.0, 100.0,
								HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
						if (e instanceof MyException) {
							log.error("发布考试错误：{}", e.getMessage());
						} else {
							log.error("发布考试错误：", e);
						}
						UserContext.remove();
					}
				}
			}).start();

			return PageResultEx.ok().data(processBarId);
		} catch (MyException e) {
			log.error("发布考试错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("发布考试错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 考试试卷
	 * 
	 * v1.0 zhanghc 2022年10月25日下午3:30:55
	 * 
	 * @param id 考试ID
	 * @return PageResult
	 */
	@RequestMapping("/paper")
	public PageResult paper(Integer id) {
		try {
			Exam exam = examCacheService.getExam(id);
			if (!(CurLoginUserUtil.isSelf(exam.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {
				throw new MyException("无操作权限");
			}

			List<Map<String, Object>> examQuestions = new ArrayList<>();
			List<Map<String, Object>> examRules = new ArrayList<>();
			if (exam.getGenType() == 1) {// 人工组卷
				examQuestionService.getList(id).stream()//
						.forEach(_examQuestion -> {
							Map<String, Object> examQuestion = new HashMap<>();
							examQuestion.put("type", _examQuestion.getType());
							if (_examQuestion.getType() == 1) {
								examQuestion.put("chapterName", _examQuestion.getChapterName());
								examQuestion.put("chapterTxt", _examQuestion.getChapterTxt());
							} else {
								Question _question = examCacheService.getQuestion(_examQuestion.getQuestionId());
								examQuestion.put("questionId", _question.getId());
								examQuestion.put("questionType", _question.getType());
								examQuestion.put("markType", _question.getMarkType());
								examQuestion.put("title", _question.getTitle());
								examQuestion.put("markOptions", _examQuestion.getMarkOptions());
								examQuestion.put("score", _examQuestion.getScore());
								examQuestion.put("scores", _examQuestion.getScores());
								examQuestion.put("analysis", _question.getAnalysis());

								List<String> _options = new ArrayList<>();
								if (QuestionUtil.hasSingleChoice(_question)
										|| QuestionUtil.hasMultipleChoice(_question)) {// 如果是单选或多选，添加选项字段
									examCacheService.getQuestionOptionList(_question.getId())//
											.forEach(questionOption -> _options.add(questionOption.getOptions()));
									examQuestion.put("options", _options);
								}

								List<Object> _answers = new ArrayList<>();
								examCacheService.getQuestionAnswerList(_question.getId())//
										.forEach(answer -> {
											if (QuestionUtil.hasSingleChoice(_question)
													|| QuestionUtil.hasTrueFalse(_question)
													|| (QuestionUtil.hasQA(_question)
															&& QuestionUtil.hasSubjective(_question))) {
												_answers.add(answer.getAnswer());
											} else if (QuestionUtil.hasMultipleChoice(_question)) {
												Collections.addAll(_answers, answer.getAnswer().split(","));
											} else if (QuestionUtil.hasFillBlank(_question)
													|| (QuestionUtil.hasQA(_question)
															&& QuestionUtil.hasObjective(_question))) {
												_answers.add(answer.getAnswer());
											}
										});

								examQuestion.put("answers", _answers);
							}
							examQuestions.add(examQuestion);
						});
			} else if (exam.getGenType() == 2) {// 随机组卷
				examRuleService.getList(id)//
						.forEach(_examRule -> {
							Map<String, Object> examRule = new HashMap<>();
							examRule.put("type", _examRule.getType());
							if (_examRule.getType() == 1) {
								examRule.put("chapterName", _examRule.getChapterName());
								examRule.put("chapterTxt", _examRule.getChapterTxt());
							} else {
								examRule.put("questionTypeId", _examRule.getQuestionTypeId());
								examRule.put("questionType", _examRule.getQuestionType());
								examRule.put("markType", _examRule.getMarkType());
								examRule.put("markOptions", _examRule.getMarkOptions());
								examRule.put("num", _examRule.getNum());
								examRule.put("score", _examRule.getScore());
								examRule.put("scores", _examRule.getQuestionType() == 2 ? _examRule.getScores() : null);// 客观填空等不需要页面处理
							}
							examRules.add(examRule);
						});
			}

			List<Integer> examUserIdList = examCacheService.getMyExamList(id).stream()//
					.map(myExam -> myExam.getUserId())//
					.collect(Collectors.toList());

			return PageResultEx.ok()//
					.addAttr("id", exam.getId())//
					.addAttr("name", exam.getName())//
					.addAttr("paperName", exam.getPaperName())//
					.addAttr("markType", exam.getMarkType())// 前端考试回显的时候，根据阅卷类型来判断是否回显阅卷时间
					.addAttr("startTime", exam.getStartTime())//
					.addAttr("endTime", exam.getEndTime())//
					.addAttr("markStartTime", exam.getMarkStartTime())//
					.addAttr("markEndTime", exam.getMarkEndTime())//
					.addAttr("genType", exam.getGenType())//
					.addAttr("showType", exam.getShowType())//
					.addAttr("passScore", exam.getPassScore())//
					.addAttr("anonState", exam.getAnonState())//
					.addAttr("scoreState", exam.getScoreState())//
					.addAttr("rankState", exam.getRankState())//
					.addAttr("sxes", exam.getSxes())//
					.addAttr("state", exam.getState())//
					.addAttr("examQuestions", examQuestions)//
					.addAttr("examRules", examRules)//
					.addAttr("examUserIds", examUserIdList)//
					.addAttr("limitMinute", exam.getLimitMinute());
		} catch (MyException e) {
			log.error("考试获取错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("考试获取错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 考试删除
	 * 
	 * v1.0 zhanghc 2017-06-11 09:13:23
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/del")
	public PageResult del(Integer id) {
		try {
			examService.delEx(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("删除考试错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("删除考试错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 考试获取
	 * 
	 * v1.0 zhanghc 2021年12月21日下午4:36:14
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/get")
	public PageResult get(Integer id) {
		try {
			Exam exam = examCacheService.getExam(id);
			return PageResultEx.ok().addAttr("id", exam.getId())//
					.addAttr("name", exam.getName())//
					.addAttr("paperName", exam.getPaperName())//
					.addAttr("startTime", exam.getStartTime())//
					.addAttr("endTime", exam.getEndTime())//
					.addAttr("markStartTime", exam.getMarkStartTime())//
					.addAttr("markEndTime", exam.getMarkEndTime())//
					.addAttr("markState", exam.getMarkState())//
					.addAttr("scoreState", exam.getScoreState())//
					.addAttr("rankState", exam.getRankState())//
					.addAttr("anonState", exam.getAnonState())//
					.addAttr("passScore", exam.getPassScore())//
					.addAttr("totalScore", exam.getTotalScore())//
					.addAttr("markType", exam.getMarkType())//
					.addAttr("genType", exam.getGenType())//
					.addAttr("sxes", exam.getSxes())//
					.addAttr("state", exam.getState());
		} catch (MyException e) {
			log.error("获取考试错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("获取考试错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 考试变更时间
	 * 
	 * v1.0 zhanghc 2022年4月17日下午6:52:08
	 * 
	 * @param id       考试ID
	 * @param timeType 时间类型：1：考试开始时间；2：考试结束时间；3：阅卷开始时间；4：阅卷结束时间
	 * @param minute   分钟数
	 * @return PageResult
	 */
	@RequestMapping("/time")
	public PageResult time(Integer id, Integer timeType, Integer minute) {
		try {
			examService.time(id, timeType, minute);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("考试变更时间错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("考试变更时间错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 考试暂停
	 * 
	 * v1.0 zhanghc 2024年1月31日下午1:36:20
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/pause")
	public PageResult pause(Integer id) {
		try {
			Exam exam = examService.pause(id);
			return PageResultEx.ok().data(exam.getState());
		} catch (MyException e) {
			log.error("考试暂停错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("考试暂停错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 阅卷协助
	 * 
	 * v1.0 zhanghc 2023年9月22日下午4:15:24
	 * 
	 * @param id
	 * @param markUserIds
	 * @return PageResult
	 */
	@RequestMapping("/assist")
	public PageResult assist(Integer id, Integer[] markUserIds) {
		try {
			examService.assist(id, markUserIds);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("阅卷协助错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("阅卷协助错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 阅卷用户列表
	 * 
	 * v1.0 zhanghc 2023年9月26日下午5:12:17
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/markUserList")
	public PageResult markUserList(Integer id) {
		try {
			// 数据校验
			Exam exam = examCacheService.getExam(id);
			if (!(CurLoginUserUtil.isSelf(exam.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {
				throw new MyException("无操作权限");
			}

			// 获取阅卷用户列表
			List<MyMark> myMarkList = myMarkService.getList(id);
			List<Map<String, Object>> resultList = new ArrayList<>();
			for (MyMark myMark : myMarkList) {
				Map<String, Object> result = new HashMap<>();
				User markUser = baseCacheService.getUser(myMark.getMarkUserId());
				result.put("id", markUser.getId());
				result.put("name", markUser.getName());
				resultList.add(result);
			}
			return PageResultEx.ok().data(resultList);
		} catch (MyException e) {
			log.error("阅卷用户列表错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("阅卷用户列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 考试用户列表
	 * 
	 * v1.0 zhanghc 2023年9月26日下午5:12:17
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/examUserList")
	public PageResult examUserList(Integer id) {
		try {
			// 数据校验
			Exam exam = examCacheService.getExam(id);
			if (!(CurLoginUserUtil.isSelf(exam.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {
				throw new MyException("无操作权限");
			}

			// 获取考试用户列表
			List<MyExam> myExamList = examCacheService.getMyExamList(id);
			Integer[] userIds = new Integer[myExamList.size()];
			for (int i = 0; i < myExamList.size(); i++) {
				userIds[i] = myExamList.get(i).getUserId();
			}

			List<Map<String, Object>> resultList = new ArrayList<>();
			for (MyExam myExam : myExamList) {
				User user = baseCacheService.getUser(myExam.getUserId());
				Org org = baseCacheService.getOrg(user.getOrgId());

				Map<String, Object> result = new HashMap<>();
				result.put("userId", user.getId());
				result.put("userName", user.getName());
				result.put("orgId", org.getId());
				result.put("orgName", org.getName());
				result.put("myExamState", myExam.getState());
				result.put("myExamMarkState", myExam.getMarkState());
				resultList.add(result);
			}
			return PageResultEx.ok().data(resultList);
		} catch (MyException e) {
			log.error("考试用户列表错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("考试用户列表错误：", e);
			return PageResult.err();
		}
	}
}