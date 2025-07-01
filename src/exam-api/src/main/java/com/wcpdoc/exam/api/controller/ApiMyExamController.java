package com.wcpdoc.exam.api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.service.ExamCacheService;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyQuestionService;
import com.wcpdoc.exam.core.util.MyExamUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 我的考试控制层
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
@RestController
@RequestMapping("/api/myExam")
@Slf4j
public class ApiMyExamController extends BaseController {

	@Resource
	private MyExamService myExamService;
	@Resource
	private MyQuestionService myQuestionService;
	@Resource
	private ExamService examService;
	@Resource
	private ExamCacheService examCacheService;
	@Resource
	private BaseCacheService baseCacheService;

	/**
	 * 我的考试列表
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * 
	 * @param pageIn
	 * @return PageResult
	 */
	@RequestMapping("/listpage")
	public PageResult listpage(PageIn pageIn) {
		try {
			pageIn.addParm("curUserId", getCurUser().getId());
			PageOut pageOut = myExamService.getListpage(pageIn);
			for (Map<String, Object> map : pageOut.getList()) {
				Exam exam = new Exam();
				exam.setScoreState((Integer) map.get("examScoreState"));
				exam.setMarkState((Integer) map.get("examMarkState"));
				MyExam myExam = new MyExam();
				myExam.setMarkState((Integer) map.get("markState"));

				if (!MyExamUtil.totalScoreShow(exam, myExam)) {// 成绩查询状态（1：考试结束后；2：不公布；3：交卷后）
					map.put("totalScore", null);// 不显示分数
					map.put("answerState", null);// 不显示及格状态
				}
				if ((Integer) map.remove("examRankState") == 2) {// 排名状态（1：公布；2：不公布）
					map.put("no", null);// 不显示排名
				}
			}

			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("我的考试列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 我的考试
	 * 
	 * v1.0 zhanghc 2022年11月2日下午2:38:55
	 * 
	 * @param examId
	 * @return PageResult
	 */
	@RequestMapping("/get")
	public PageResult get(Integer examId) {
		try {
			MyExam myExam = examCacheService.getMyExam(examId, getCurUser().getId());
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
			log.error("获取我的考试错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("获取我的考试错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 考试获取
	 * 
	 * v1.0 zhanghc 2022年11月2日下午2:38:55
	 * 
	 * @param examId
	 * @return PageResult
	 */
	@RequestMapping("/examGet")
	public PageResult examGet(Integer examId) {
		try {
			MyExam myExam = examCacheService.getMyExam(examId, getCurUser().getId());
			if (myExam == null) {
				throw new MyException("无查阅权限");
			}

			Exam exam = examCacheService.getExam(examId);
			return PageResultEx.ok()//
					.addAttr("id", exam.getId())//
					.addAttr("name", exam.getName())//
					.addAttr("paperName", exam.getPaperName())//
					.addAttr("startTime", exam.getStartTime())//
					.addAttr("endTime", exam.getEndTime())//
					.addAttr("markStartTime", exam.getMarkStartTime())//
					.addAttr("markEndTime", exam.getMarkEndTime())//
					.addAttr("markState", exam.getMarkState())// 页面控制是否显示错题
					.addAttr("scoreState", exam.getScoreState())// 页面控制是否显示错题
					.addAttr("rankState", exam.getRankState())// 页面控制是否显示排名
					.addAttr("anonState", exam.getAnonState())//
					.addAttr("passScore", exam.getPassScore())//
					.addAttr("totalScore", exam.getTotalScore())//
					.addAttr("markType", exam.getMarkType())//
					.addAttr("loginType", exam.getLoginType())//
					.addAttr("genType", exam.getGenType())//
					.addAttr("sxes", exam.getSxes())//
					.addAttr("state", exam.getState())//
					.addAttr("userNum", exam.getUserIds().size())//
					.addAttr("limitMinute", exam.getLimitMinute());//
		} catch (MyException e) {
			log.error("获取考试错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("获取考试错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 试题统计
	 * 
	 * v1.0 zhanghc 2024年8月30日下午4:18:55
	 * 
	 * @param examId
	 * @return PageResult
	 */
	@RequestMapping("/questionStatis")
	public PageResult questionStatis(Integer examId) {
		try {
			MyExam myExam = examCacheService.getMyExam(examId, getCurUser().getId());
			if (myExam == null) {
				throw new MyException("无查阅权限");
			}

			Map<Integer, Long> markTypeResult = examCacheService.getMyQuestionList(examId, getCurUser().getId())//
					.stream()//
					.filter(myQuestion -> myQuestion.getType() == 2)// 排除章节
					.map(myQuestion -> examCacheService.getQuestion(myQuestion.getQuestionId()))//
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

			Map<Integer, Long> typeCountMap = examCacheService.getMyQuestionList(examId, getCurUser().getId())//
					.stream()//
					.filter(myQuestion -> myQuestion.getType() == 2)// 排除章节
					.map(myQuestion -> examCacheService.getQuestion(myQuestion.getQuestionId()))//
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

			markTypeStatis.put("chapter", examCacheService.getMyQuestionList(examId, getCurUser().getId())//
					.stream()//
					.filter(myQuestion -> myQuestion.getType() == 1)// 获取章节数量
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

	/**
	 * 我的试卷
	 * 
	 * v1.0 zhanghc 2022年5月18日下午1:21:07
	 * 
	 * @param examId
	 * @return PageResult
	 */
	@RequestMapping("/paper")
	public PageResult paper(Integer examId) {
		try {
			return PageResultEx.ok().data(myExamService.paper(examId, getCurUser().getId()));
		} catch (MyException e) {
			log.error("我的试卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("我的试卷错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 生成试卷
	 * 
	 * v1.0 zhanghc 2024年10月11日上午9:11:57
	 * 
	 * @param examId
	 * @return PageResult
	 */
	@RequestMapping("/generatePaper")
	public PageResult generatePaper(Integer examId) {
		try {
			myExamService.generatePaper(examId, getCurUser().getId());
			return PageResult.ok();
		} catch (MyException e) {
			log.error("我的试卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("我的试卷错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 答题
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * 
	 * @param examId
	 * @param questionId
	 * @param answers
	 * @return PageResult
	 */
	@RequestMapping("/answer")
	public PageResult answer(Integer examId, Integer questionId, String[] answers) {
		try {
			myExamService.answer(examId, getCurUser().getId(), questionId, answers);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("答题错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("答题错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 交卷
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * 
	 * @param examId
	 * @return PageResult
	 */
	@RequestMapping("/finish")
	public PageResult finish(Integer examId) {
		try {
			myExamService.finish(examId, getCurUser().getId());
			return PageResult.ok();
		} catch (MyException e) {
			log.error("交卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("交卷错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 作弊
	 * 
	 * v1.0 zhanghc 2025年3月16日上午11:19:25
	 * 
	 * @param examId  考试ID
	 * @param type    作弊类型（3：禁止考试中切屏；）
	 * @param content 作弊内容
	 * @return PageResult
	 */
	@RequestMapping("/sxe")
	public PageResult sxe(Integer examId, Integer type, String content) {
		try {
			return PageResultEx.ok().data(myExamService.sxe(examId, getCurUser().getId(), type, content));
		} catch (MyException e) {
			log.error("作弊错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("作弊错误：", e);
			return PageResult.err();
		}
	}
}