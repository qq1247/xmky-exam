package com.wcpdoc.exam.api.controller;

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

import com.wcpdoc.base.cache.ParmCache;
import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.ParmService;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
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
import com.wcpdoc.notify.exception.NotifyException;
import com.wcpdoc.notify.service.NotifyService;

/**
 * 我的考试控制层
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
@Controller
@RequestMapping("/api/myExam")
public class ApiMyExamController extends BaseController{
	private static final Logger log = LoggerFactory.getLogger(ApiMyExamController.class);
	
	@Resource
	private MyExamService myExamService;
	@Resource
	private MyMarkService myMarkService;
	@Resource
	private MyExamDetailService myExamDetailService;
	@Resource
	private UserService userService;
	@Resource
	private NotifyService notifyService;
	@Resource
	private ParmService parmService;
	@Resource
	private PaperService paperService;
	@Resource
	private ExamService examService;
	
	/**
	 * 我的考试列表
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
			PageOut pageOut = myExamService.getListpage(pageIn);
			for (Map<String, Object> map : pageOut.getList()) {
				if ((Integer)map.remove("examScoreState") == 2) {
					map.put("totalScore", null);// 不显示分数
					map.put("answerState", null);// 不显示及格状态
				}
				if ((Integer)map.remove("examRankState") == 2) {
					map.put("no", null);// 不显示排名
					map.put("userNum", null);// 不显示排名
				}
			}
			
			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("我的考试列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 试卷信息
	 * 
	 * v1.0 zhanghc 2022年5月18日下午1:21:07
	 * @param examId
	 * @return PageResult
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/paper")
	@ResponseBody
	public PageResult paper(Integer examId) {
		try {
			// 校验数据有效性
			if (!ValidateUtil.isValid(examId)) {
				throw new MyException("参数错误：examId");
			}
			List<MyMark> myMarkList = myMarkService.getList(examId);
			boolean readAuth = false;
			for (MyMark myMark : myMarkList) {
				for (Integer userId : myMark.getExamUserIdArr()) {
					if (userId.intValue() == getCurUser().getId().intValue()) {// 参加了当前考试
						readAuth = true;
						break;
					}
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
					List<MyExamDetail> myExamDetailList = myExamDetailService.getList(examId, getCurUser().getId());
					paperRandHandle(paper, myPaper, myExamDetailList);
				}
			} else if (paper.getGenType() == 2) {// 随机试卷
				myPaper = paperService.getPaperOfRand(exam.getId(), getCurUser().getId());
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
	 * 考试答案列表
	 * 
	 * v1.0 zhanghc 2018年11月24日上午9:13:22
	 * @param examId
	 * @return PageResult
	 */
	@RequestMapping("/answerList")
	@ResponseBody
	public PageResult answerList(Integer examId) {
		try {
			List<Map<String, Object>> answerList = myExamDetailService.getAnswerList(examId, getCurUser().getId());
			for (Map<String, Object> map : answerList) {
				QuestionAnswer answer = new QuestionAnswer();
				answer.setQuestionType((Integer)map.get("questionType"));
				answer.setQuestionAi((Integer)map.get("questionAi"));
				answer.setAnswer((String)map.remove("answer"));
				map.put("answers", answer.getAnswerArr());
			}
			
			return PageResultEx.ok().data(answerList);
		} catch (MyException e) {
			log.error("考试答案列表错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("考试答案列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 答题
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * @param examId
	 * @param questionId
	 * @param answers
	 * @param fileId
	 * @return PageResult
	 */
	@RequestMapping("/answer")
	@ResponseBody
	public PageResult answer(Integer examId, Integer questionId, String[] answers, Integer answerFileId) {
		try {
			if (!AutoMarkCache.tryReadLock(examId, 2000)) {
				throw new MyException("尝试加读锁失败");
			}
			myExamService.answerUpdate(examId, getCurUser().getId(), questionId, answers, answerFileId);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("更新答案错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("更新答案错误：", e);
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
	public PageResult finish(Integer examId) {
		try {
			if (!AutoMarkCache.tryReadLock(examId, 2000)) {
				throw new MyException("尝试加读锁失败");
			}
			myExamService.finish(examId, getCurUser().getId());
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成交卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成交卷错误：", e);
			return PageResult.err();
		} finally {
			AutoMarkCache.releaseReadLock(examId);
		}
	}
	
	/**
	 * 邮件通知
	 * 
	 * v1.0 zhanghc 2018年11月24日上午9:13:22
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/email")
	@ResponseBody
	public PageResult email(Integer examId) {
		try {
			Parm parm = ParmCache.get();
			List<MyExam> list = myExamService.getList(examId);
			for(MyExam myExam : list){
				User user = userService.getEntity(myExam.getUserId());
				if (!ValidateUtil.isValid(user.getEmail())) {
					continue;
				}
				notifyService.pushEmail(parm.getEmailUserName(), user.getEmail(), "考试邮箱通知", user.getName()+"-昵称。");
			}
			return PageResult.ok();
		} catch (NotifyException e) {
			log.error("邮件通知错误：", e);
			return PageResult.err().msg(e.getMessage());
		} catch (MyException e) {
			log.error("邮件通知错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("邮件通知错误：", e);
			return PageResult.err();
		}
	}
}