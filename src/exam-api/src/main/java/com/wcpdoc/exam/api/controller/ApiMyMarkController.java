package com.wcpdoc.exam.api.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.base.entity.Org;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.OrgService;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.cache.AutoMarkCache;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyExamDetail;
import com.wcpdoc.exam.core.entity.MyMark;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperQuestionAnswer;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.entity.ex.Chapter;
import com.wcpdoc.exam.core.entity.ex.MyPaper;
import com.wcpdoc.exam.core.entity.ex.MyQuestion;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.MyExamDetailService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyMarkService;
import com.wcpdoc.exam.core.service.PaperService;
import com.wcpdoc.exam.core.util.PaperUtil;

/**
 * 我的阅卷控制层
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
@Controller
@RequestMapping("/api/myMark")
public class ApiMyMarkController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiMyMarkController.class);
	
	@Resource
	private MyExamService myExamService;
	@Resource
	private ExamService examService;
	@Resource
	private MyMarkService myMarkService;
	@Resource
	private MyExamDetailService myExamDetailService;
	@Resource
	private UserService userService;
	@Resource
	private OrgService orgService;
	@Resource
	private PaperService paperService;
	
	/**
	 * 我的阅卷列表
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return pageOut
	 */
	@RequestMapping("/listpage")
	@ResponseBody
	public PageResult listpage() {
		try {
			PageIn pageIn = new PageIn(request);
			pageIn.addAttr("curUserId", getCurUser().getId());
			PageOut pageOut = myMarkService.getListpage(pageIn);
			
			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("我的阅卷列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 试卷信息
	 * 
	 * v1.0 zhanghc 2022年5月18日下午1:21:07
	 * @param examId
	 * @param userId
	 * @return PageResult
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/paper")
	@ResponseBody
	public PageResult paper(Integer examId, Integer userId) {
		try {
			// 校验数据有效性
			if (!ValidateUtil.isValid(examId)) {
				throw new MyException("参数错误：examId");
			}
			List<MyMark> myMarkList = myMarkService.getList(examId);
			boolean readAuth = false;
			for (MyMark myMark : myMarkList) {
				if (myMark.getMarkUserId().intValue() == getCurUser().getId().intValue()) {// 参加了当前阅卷
					for (Integer _userId : myMark.getExamUserIdArr()) {
						if (_userId.intValue() == userId.intValue()) {// 有当前考试用户的阅卷权限
							readAuth = true;
							break;
						}
					}
					break;
				}
			}
			if (!readAuth) {
				throw new MyException("无查阅权限");
			}
			Exam exam = examService.getEntity(examId);
			Paper paper = paperService.getEntity(exam.getPaperId());
			
			// 生成试卷数据
			MyPaper myPaper = null;
			if (paper.getGenType() == 1) {// 固定试卷
				myPaper = paperService.getPaper(exam.getPaperId());
				if (PaperUtil.hasQuestionRand(paper) || PaperUtil.hasOptionRand(paper)) {
					List<MyExamDetail> myExamDetailList = myExamDetailService.getList(examId, userId);
					paperRandHandle(paper, myPaper, myExamDetailList);
				}
			} else if (paper.getGenType() == 2) {// 随机试卷
				myPaper = paperService.getPaperOfRand(exam.getId(), userId);
			}
			
			List<Map<String, Object>> resultList = new ArrayList<>();
			for (Chapter chapter : myPaper.getChapterList()) {
				Map<String, Object> singleResult = new HashMap<>();
				Map<Object, Object> chapterMap = new HashMap<>();
				chapterMap.put("id", chapter.getChapter().getId());
				chapterMap.put("name", chapter.getChapter().getName());
				chapterMap.put("description", chapter.getChapter().getDescription());
				singleResult.put("chapter", chapterMap);
				
				List<Map<String, Object>> questionsListMap = new ArrayList<>();
				for (MyQuestion myQuestion : chapter.getMyQuestionList()) {
					Map<String, Object> questionMap = new HashMap<>();
					questionMap.put("id", myQuestion.getQuestion().getId());
					questionMap.put("type", myQuestion.getQuestion().getType());
					questionMap.put("difficulty", myQuestion.getQuestion().getDifficulty());
					questionMap.put("title", myQuestion.getQuestion().getTitle());
					questionMap.put("ai", myQuestion.getQuestion().getAi());
					questionMap.put("analysis", myQuestion.getQuestion().getAnalysis());
					questionMap.put("score", myQuestion.getAttr().getScore());// 分数从试卷中取
					questionMap.put("aiOptions", myQuestion.getAttr().getAiOptionArr());// 分数选项从试卷中取
					questionMap.put("options", new ArrayList<Map<String, Object>>());
					
					if (myQuestion.getQuestion().getType() == 1 || myQuestion.getQuestion().getType() == 2) {
						for (QuestionOption questionOption : myQuestion.getOptionList()) {
							Map<String, Object> option = new HashMap<>();
							option.put("option", questionOption.getOptions());
							option.put("no", (char)(questionOption.getNo() + 64));
							((List<Map<String, Object>>)questionMap.get("options")).add(option);
						}
					}
					
					questionMap.put("answers", new ArrayList<Map<String, Object>>());
					for (PaperQuestionAnswer answer : myQuestion.getAnswerList()) {
						Map<String, Object> answerMap = new HashMap<String, Object>();
						answerMap.put("score", answer.getScore());
						answerMap.put("answer", answer.getAnswerArr());
						((List<Map<String, Object>>)questionMap.get("answers")).add(answerMap);
					}
					
					questionsListMap.add(questionMap);
				}
				
				singleResult.put("questionList", questionsListMap);
				resultList.add(singleResult);
			}
			
			return PageResultEx.ok().data(resultList);
		} catch (MyException e) {
			log.error("试卷信息错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("试卷信息错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 固定试卷乱序处理
	 * 
	 * v1.0 zhanghc 2022年6月23日下午1:55:31
	 * @param paper 
	 * @param myPaper
	 * @param myExamDetailList 
	 * void
	 */
	private void paperRandHandle(Paper paper, MyPaper myPaper, List<MyExamDetail> myExamDetailList) {
		Map<Integer, MyExamDetail> myExamDetailCache = new HashMap<>();
		for (MyExamDetail myExamDetail : myExamDetailList) {
			myExamDetailCache.put(myExamDetail.getQuestionId(), myExamDetail);
		}
		
		for (Chapter chapter : myPaper.getChapterList()) {
			List<MyQuestion> myQuestionList = chapter.getMyQuestionList();
			if (PaperUtil.hasQuestionRand(paper)) {
				Collections.sort(myQuestionList, new Comparator<MyQuestion>() {
					@Override
					public int compare(MyQuestion o1, MyQuestion o2) {
						return myExamDetailCache.get(o1.getQuestion().getId()).getQuestionNo() - myExamDetailCache.get(o2.getQuestion().getId()).getQuestionNo();
					}
				});
			}
			if (PaperUtil.hasOptionRand(paper)) {
				for (MyQuestion myquestion : myQuestionList) {
					if (!(myquestion.getQuestion().getType() == 1 || myquestion.getQuestion().getType() == 2)) {
						continue;
					}
					
					Integer[] optionNoArr = myExamDetailCache.get(myquestion.getQuestion().getId()).getOptionNoArr();
					List<QuestionOption> optionList = myquestion.getOptionList();
					Collections.sort(optionList, new Comparator<QuestionOption>() {
						@Override
						public int compare(QuestionOption o1, QuestionOption o2) {
							return optionNoArr[o1.getNo() - 1] - optionNoArr[o2.getNo() - 1];
						}
					});
				}
			}
		}
	}

	/**
	 * 阅卷
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * @param examId
	 * @param userId
	 * @param questionId
	 * @param score
	 * @return PageResult
	 */
	@RequestMapping("/score")
	@ResponseBody
	public PageResult score(Integer examId, Integer userId, Integer questionId, BigDecimal score) {
		try {
			if (!AutoMarkCache.tryReadLock(examId, 2000)) {
				throw new MyException("尝试加读锁失败");
			}
			myMarkService.scoreUpdate(examId, userId, questionId, score);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("阅卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("阅卷错误：", e);
			return PageResult.err();
		} finally {
			AutoMarkCache.releaseReadLock(examId);
		}
	}
	
	/**
	 * 交卷
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * @param examId
	 * @return PageResult
	 */
	@RequestMapping("/finish")
	@ResponseBody
	public PageResult finish(Integer examId, Integer userId) {
		try {
			if (!AutoMarkCache.tryReadLock(examId, 2000)) {
				throw new MyException("尝试加读锁失败");
			}
			myMarkService.finish(examId, userId);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成阅卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成阅卷错误：", e);
			return PageResult.err();
		} finally {
			AutoMarkCache.releaseReadLock(examId);
		}
	}
	
	/**
	 * 考试用户列表
	 * 
	 * v1.0 chenyun 2021年8月2日下午3:14:45
	 * @param examId
	 * @return PageResult
	 */
	@RequestMapping("/userList")
	@ResponseBody
	public PageResult userList(Integer examId) {
		try {
			// 校验数据有效性
			List<MyMark> myMarkList = myMarkService.getList(examId);
			MyMark myMark = null;
			for (MyMark _myMark : myMarkList) {
				if (_myMark.getMarkUserId().intValue() == getCurUser().getId().intValue()) {
					myMark = _myMark;
					break;
				}
			}
			if (myMark == null) {
				throw new MyException("未参与该场考试");
			}
			
			// 返回考试用户IDS
			List<User> userList = userService.getList(myMark.getExamUserIdArr());
			List<Map<String, Object>> result = new ArrayList<>();
			Map<Integer, Org> orgCache = new HashMap<>();// 机构不会很多，缓存利用
			Exam exam = examService.getEntity(examId);
			Paper paper = paperService.getEntity(exam.getPaperId());
			for (User user : userList) {
				MyExam myExam = myExamService.getMyExam(examId, user.getId());
				Map<String, Object> singleResult = new HashMap<>();
				singleResult.put("userId", user.getId());
				singleResult.put("userName", exam.getAnonState() == 1 ? user.getName() : null);//  如果是匿名阅卷，不显示名称等
				singleResult.put("userHeadFileId", exam.getAnonState() == 1 ? user.getHeadFileId() : null);
				if (orgCache.get(user.getOrgId()) == null) {
					orgCache.put(user.getOrgId(), orgService.getEntity(user.getOrgId()));
				}
				singleResult.put("orgId", user.getOrgId());
				singleResult.put("orgName", exam.getAnonState() == 1 ? orgCache.get(user.getOrgId()).getName() : null);
				singleResult.put("answerStartTime", myExam.getAnswerStartTime() == null ? null : DateUtil.formatDateTime(myExam.getAnswerStartTime()));
				singleResult.put("answerEndTime", myExam.getAnswerEndTime() == null ? null : DateUtil.formatDateTime(myExam.getAnswerEndTime()));
				singleResult.put("markStartTime", myExam.getMarkStartTime() == null ? null : DateUtil.formatDateTime(myExam.getMarkStartTime()));
				singleResult.put("markEndTime", myExam.getMarkEndTime() == null ? null : DateUtil.formatDateTime(myExam.getMarkEndTime()));
				singleResult.put("state", myExam.getState());
				singleResult.put("markState", myExam.getMarkState());
				singleResult.put("answerState", exam.getScoreState() == 1 ? myExam.getAnswerState() : null);
				singleResult.put("totalScore", exam.getScoreState() == 1 ? myExam.getTotalScore() : null);// 成绩不公开则不显示
				singleResult.put("paperTotalScore", paper.getTotalScore());
				singleResult.put("paperPassScore", paper.getPassScore());
				result.add(singleResult);
			}
			
			return PageResultEx.ok().data(result);
		} catch (MyException e) {
			log.error("考试用户列表错误：{}", e.getMessage());
			return PageResult.err();
		} catch (Exception e) {
			log.error("考试用户列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 考试用户
	 * 
	 * v1.0 zhanghc 2022年3月25日下午1:54:56
	 * @param examId
	 * @param userId
	 * @return PageResult
	 */
	@RequestMapping("/user")
	@ResponseBody
	public PageResult user(Integer examId, Integer userId) {
		try {
			// 校验数据有效性
			List<MyMark> myMarkList = myMarkService.getList(examId);
			MyMark myMark = null;
			for (MyMark _myMark : myMarkList) {
				if (_myMark.getMarkUserId().intValue() == getCurUser().getId().intValue()) {
					myMark = _myMark;
					break;
				}
			}
			if (myMark == null) {
				throw new MyException("未参与该场考试");
			}
			if (!myMark.getExamUserIds().contains(String.format(",%s,", userId))) {
				throw new MyException("未参与该用户阅卷");
			}
			
			// 获取考试用户信息
			MyExam myExam = myExamService.getMyExam(examId, userId);
			User user = userService.getEntity(userId);
			Org org = orgService.getEntity(user.getOrgId());
			Exam exam = examService.getEntity(examId);
			Paper paper = paperService.getEntity(exam.getPaperId());
			return PageResultEx.ok()
					.addAttr("userId", user.getId())
					.addAttr("userName", exam.getAnonState() == 1 ? user.getName() : null)//  如果是匿名阅卷，不显示名称等
					.addAttr("userHeadFileId", exam.getAnonState() == 1 ? user.getHeadFileId() : null)
					.addAttr("org", user.getOrgId())
					.addAttr("orgName", exam.getAnonState() == 1 ? org.getName() : null)
					.addAttr("answerStartTime", myExam.getAnswerStartTime() == null ? null : DateUtil.formatDateTime(myExam.getAnswerStartTime()))
					.addAttr("answerEndTime", myExam.getAnswerEndTime() == null ? null : DateUtil.formatDateTime(myExam.getAnswerEndTime()))
					.addAttr("markStartTime", myExam.getMarkStartTime() == null ? null : DateUtil.formatDateTime(myExam.getMarkStartTime()))
					.addAttr("markEndTime", myExam.getMarkEndTime() == null ? null : DateUtil.formatDateTime(myExam.getMarkEndTime()))
					.addAttr("state", myExam.getState())
					.addAttr("markState", myExam.getMarkState())
					.addAttr("answerState", exam.getScoreState() == 1 ? myExam.getAnswerState() : null)
					.addAttr("totalScore", exam.getScoreState() == 1 ? myExam.getTotalScore() : null)// 成绩不公开则不显示
					.addAttr("paperTotalScore", paper.getTotalScore())
					.addAttr("paperPassScore", paper.getPassScore());
		} catch (MyException e) {
			log.error("考试用户错误：{}", e.getMessage());
			return PageResult.err();
		} catch (Exception e) {
			log.error("考试用户错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 阅卷考试答案列表
	 * 
	 * v1.0 chenyun 2021年7月29日下午6:04:37
	 * @param examId
	 * @param userId
	 * @return PageResult
	 */
	@RequestMapping("/answerList")
	@ResponseBody
	public PageResult answerList(Integer examId, Integer userId) {
		try {
			// 校验数据有效性
			if (!ValidateUtil.isValid(examId)) {
				throw new MyException("参数错误：examId");
			}
			Exam exam = examService.getEntity(examId);
			if (exam.getCreateUserId().intValue() != getCurUser().getId().intValue()) {// 如果不是自己创建的考试，权限校验
				List<MyMark> myMarkList = myMarkService.getList(examId);
				boolean readAuth = false;
				for (MyMark myMark : myMarkList) {
					if (myMark.getMarkUserId().intValue() == getCurUser().getId().intValue()) {// 参加了当前阅卷
						for (Integer _userId : myMark.getExamUserIdArr()) {
							if (_userId.intValue() == userId.intValue()) {// 有当前考试用户的阅卷权限
								readAuth = true;
								break;
							}
						}
						break;
					}
				}
				if (!readAuth) {
					throw new MyException("无查阅权限");
				}
			}
			
			// 返回考试答案
			List<Map<String, Object>> list = myExamDetailService.getAnswerList(examId, userId);
			for (Map<String, Object> map : list) {
				QuestionAnswer answer = new QuestionAnswer();
				answer.setQuestionType((Integer)map.remove("questionType"));
				answer.setQuestionAi((Integer)map.remove("questionAi"));
				answer.setAnswer((String)map.remove("answer"));
				map.put("answers", answer.getAnswerArr()); 
			}
			
			return PageResultEx.ok().data(list);
		} catch (MyException e) {
			log.error("考试答案列表错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("考试答案列表错误：", e);
			return PageResult.err();
		}
	}
}