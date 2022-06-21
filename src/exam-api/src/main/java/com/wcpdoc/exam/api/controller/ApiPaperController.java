package com.wcpdoc.exam.api.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.entity.PaperQuestionAnswer;
import com.wcpdoc.exam.core.entity.PaperRemark;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.entity.ex.Chapter;
import com.wcpdoc.exam.core.entity.ex.MyPaper;
import com.wcpdoc.exam.core.entity.ex.MyQuestion;
import com.wcpdoc.exam.core.service.PaperService;
/**
 * 试卷控制层
 * 
 * zhanghc 2018年10月21日上午8:16:06
 */
@Controller
@RequestMapping("/api/paper")
public class ApiPaperController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiPaperController.class);
	
	@Resource
	private PaperService paperService;
	
	/**
	 * 试卷列表
	 * 
	 * zhanghc 2018年10月21日上午8:16:06
	 * @return pageOut
	 */
	@RequestMapping("/listpage")
	@ResponseBody
	public PageResult listpage() {
		try {
			PageIn pageIn = new PageIn(request);
			pageIn.addAttr("curUserId", getCurUser().getId());
			PageOut listpage = paperService.getListpage(pageIn);
			return PageResultEx.ok().data(listpage);
		} catch (Exception e) {
			log.error("试卷列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 添加试卷
	 * 
	 * zhanghc 2018年10月21日上午8:16:06
	 * @param paper
	 * @param paperRemark
	 * @return pageOut
	 */
	@RequestMapping("/add")
	@ResponseBody
	public PageResult add(Paper paper, PaperRemark paperRemark) {
		try {
			paperService.addAndUpdate(paper, paperRemark);
			return PageResultEx.ok().data(paper.getId());
		} catch (MyException e) {
			log.error("添加试卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("添加试卷错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 修改试卷
	 * 
	 * zhanghc 2018年10月21日上午8:16:06
	 * @return pageOut
	 */
	@RequestMapping("/edit")
	@ResponseBody
	public PageResult edit(Paper paper) { // List<PaperRemark> paperRemark
		try {
			paperService.updateAndUpdate(paper);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("修改试卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("修改试卷错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 删除试卷
	 * 
	 * zhanghc 2018年10月21日上午8:16:06
	 * @return pageOut
	 */
	@RequestMapping("/del")
	@ResponseBody
	public PageResult del(Integer id) {
		try {
			paperService.delAndUpdate(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("删除试卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("删除试卷错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 获取试卷
	 * 
	 * v1.0 chenyun 2021年3月29日下午5:40:20
	 * @param paperId
	 * @return PageResult
	 */
	@RequestMapping("/get")
	@ResponseBody
	public PageResult get(Integer id) {
		try {
			Paper paper = paperService.getEntity(id);
			return PageResultEx.ok()
					.addAttr("id", paper.getId())
					.addAttr("name", paper.getName())
					.addAttr("passScore", paper.getPassScore())
					.addAttr("totalScore", paper.getTotalScore())
					.addAttr("genType", paper.getGenType())
					.addAttr("markType", paper.getMarkType())
					.addAttr("showType", paper.getShowType())
					.addAttr("state", paper.getState())
					.addAttr("paperTypeId", paper.getPaperTypeId())
					.addAttr("options", ValidateUtil.isValid(paper.getOptions()) ? paper.getOptions().split(",") : new String[0]);
		} catch (MyException e) {
			log.error("获取试卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("获取试卷错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 复制试卷
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @param id
	 * @return pageOut
	 */
	@RequestMapping("/copy")
	@ResponseBody
	public PageResult copy(Integer id) {
		try {
			paperService.copy(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("复制试题错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		}  catch (Exception e) {
			log.error("复制试题错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 试卷归档
	 * 
	 * v1.0 zhanghc 2018年11月24日上午9:13:22
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/archive")
	@ResponseBody
	public PageResult archive(Integer id) {
		try {
			paperService.archive(id);
			return PageResult.ok();
		}catch (Exception e) {
			log.error("归档错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 添加章节
	 * 
	 * v1.0 zhanghc 2018年10月21日上午8:16:22
	 * @param chapter
	 * @return PageResult
	 */
	@RequestMapping("/chapterAdd")
	@ResponseBody
	public PageResult chapterAdd(PaperQuestion chapter) {
		try {
			paperService.chapterAdd(chapter);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("添加章节错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("添加章节错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 修改章节
	 * 
	 * v1.0 zhanghc 2018年10月21日上午8:16:35
	 * @param chapter
	 * @return PageResult
	 */
	@RequestMapping("/chapterEdit")
	@ResponseBody
	public PageResult chapterEdit(PaperQuestion chapter, Integer chapterId) {
		try {
			chapter.setId(chapterId);
			paperService.chapterEdit(chapter);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("修改章节错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("修改章节错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 删除章节
	 * 
	 * v1.0 zhanghc 2018年10月21日上午8:16:46
	 * @param chapterId
	 * @return PageResult
	 */
	@RequestMapping("/chapterDel")
	@ResponseBody
	public PageResult chapterDel(Integer chapterId) {
		try {
			paperService.chapterDel(chapterId);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("删除章节错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("删除章节错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 移动章节
	 * 
	 * v1.0 zhanghc 2018年10月21日上午10:46:54
	 * @param sourceId
	 * @param targetId
	 * @return PageResult
	 */
	@RequestMapping("/chapterMove")
	@ResponseBody
	public PageResult chapterMove(Integer sourceId, Integer targetId) {
		try {
			paperService.chapterMove(sourceId, targetId);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("章节移动错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("章节移动错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 移动试题
	 * 
	 * v1.0 zhanghc 2018年10月21日上午10:46:54
	 * @param id
	 * @param sourceQuestionId 源试题ID
	 * @param targetQuestionId 目标试题ID
	 * @return PageResult
	 */
	@RequestMapping("/questionMove")
	@ResponseBody
	public PageResult questionMove(Integer id, Integer sourceQuestionId, Integer targetQuestionId) {
		try {
			paperService.questionMove(id, sourceQuestionId, targetQuestionId);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("移动试题错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("移动试题错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 试卷详细
	 * 
	 * v1.0 zhanghc 2022年5月18日下午1:21:07
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/myPaper")
	@ResponseBody
	public PageResult myPaper(Integer id) {
		try {
			MyPaper myPaper = paperService.getMyPaper(id);
			return PageResultEx.ok().data(detailHandle(myPaper));
		} catch (Exception e) {
			log.error("试题列表错误：", e);
			return PageResult.err();
		}
	}

	@RequestMapping("/myPaperOfRand")
	@ResponseBody
	public PageResult myPaperOfRand(Integer examId, Integer userId) {
		try {
			MyPaper myPaper = paperService.getMyPaperOfRand(examId, userId);
			return PageResultEx.ok().data(detailHandle(myPaper));
		} catch (Exception e) {
			log.error("试题列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 添加试题
	 * 
	 * v1.0 zhanghc 2018年10月21日上午8:17:26
	 * @param chapterId
	 * @param questionIds
	 * @param no
	 * @return PageResult
	 */
	@RequestMapping("/questionAdd")
	@ResponseBody
	public PageResult questionAdd(Integer chapterId, Integer[] questionIds, Integer no) {
		try {
			paperService.questionAdd(chapterId, questionIds, no);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("添加试题错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("添加试题错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 批量设置分数
	 * 
	 * v1.0 zhanghc 2018年10月21日上午10:46:54
	 * @param chapterId
	 * @param score
	 * @param subScores
	 * @param aiOptions
	 * @return PageResult
	 */
	@RequestMapping("/updateBatchScore")
	@ResponseBody
	public PageResult updateBatchScore(Integer chapterId, BigDecimal score, BigDecimal subScores, Integer[] aiOptions) {
		try {
			paperService.batchScoreUpdate(chapterId, score, subScores, aiOptions);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("设置分数错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("设置分数错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 试题删除
	 * 
	 * v1.0 zhanghc 2018年10月21日下午10:41:34
	 * @param paperQuestionId
	 * @return PageResult
	 */
	@RequestMapping("/questionDel")
	@ResponseBody
	public PageResult questionDel(Integer id, Integer questionId) {
		try {
			paperService.questionDel(id, questionId);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("试题删除错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("试题删除错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 清空试题
	 * 
	 * v1.0 zhanghc 2018年10月21日上午8:17:32
	 * @param chapterId
	 * @return PageResult
	 */
	@RequestMapping("/questionClear")
	@ResponseBody
	public PageResult questionClear(Integer chapterId) {
		try {
			paperService.questionClear(chapterId);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("清空试题错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("清空试题错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 设置分数
	 * 
	 * v1.0 zhanghc 2018年10月21日上午10:46:54
	 * @param id
	 * @param questionId
	 * @param score
	 * @param subScores 试题为智能阅卷，并且是填空或问答时有效
	 * @param aiOptions 
	 * @return PageResult
	 */
	@RequestMapping("/scoreUpdate")
	@ResponseBody
	public PageResult scoreUpdate(Integer id, Integer questionId, BigDecimal score, BigDecimal[] subScores, Integer[] aiOptions) {
		try {
			paperService.scoreUpdate(id, questionId, score, subScores, aiOptions);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("设置分数错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("设置分数错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 发布
	 * 
	 * v1.0 zhanghc 2018年11月24日上午9:13:22
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/publish")
	@ResponseBody
	public PageResult publish(Integer id) {
		try {
			paperService.publish(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("发布错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("发布错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 更新总分数
	 * 组卷时，总分数由前端计算并显示（加速响应提高用户体验）
	 * 在关闭浏览器，或在组卷页面点击返回按钮时，调用该接口来更新试卷总分数。
	 * 浏览器崩溃或前端异常等原因，不能保证一定能调用到该接口，最终由发布接口保证结果的一致性和正确性。
	 * 
	 * v1.0 chenyun 2021年8月23日上午10:58:27
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/totalScoreUpdate")
	@ResponseBody
	public PageResult totalScoreUpdate(Integer id) {
		try {
			paperService.totalScoreUpdate(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("更新总分数错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("更新总分数错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 反作弊
	 * 
	 * v1.0 zhanghc 2022年5月18日上午10:45:21
	 * @param id
	 * @param options 1：试题乱序；2：选项乱序；3：禁用右键；4：禁止复制；5：最小化警告
	 * @return PageResult
	 */
	@RequestMapping("/sxe")
	@ResponseBody
	public PageResult sxe(Integer id, Integer[] options) {
		try {
			paperService.sxe(id, options);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("发布错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("发布错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 操作权限
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param id
	 * @param readUserIds
	 * @return PageResult
	 */
	@RequestMapping("/auth")
	@ResponseBody
	public PageResult auth(Integer id, Integer[] readUserIds) {
		try {
			paperService.auth(id, readUserIds);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("添加组用户错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("添加组用户错误：", e);
			return PageResult.err();
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> detailHandle(MyPaper myPaper) {
		List<Map<String, Object>> resultList = new ArrayList<>();
		for (Chapter chapter : myPaper.getChapterList()) {
			Map<String, Object> singleResult = new HashMap<>();
			Map<Object, Object> chapterMap = new HashMap<>();
			chapterMap.put("id", chapter.getChapter().getId());
			chapterMap.put("name", chapter.getChapter().getName());
			chapterMap.put("description", chapter.getChapter().getDescription());
			singleResult.put("chapter", chapterMap);
			
			List<Map<String, Object>> questionsListMap = new ArrayList<>();
			if (ValidateUtil.isValid(myPaper.getPaper().getOptions()) && myPaper.getPaper().getOptions().contains("1")) {
				Collections.shuffle(chapter.getMyQuestionList());
			}
			
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
					if (ValidateUtil.isValid(myPaper.getPaper().getOptions()) && myPaper.getPaper().getOptions().contains("2")) {
						Collections.shuffle(myQuestion.getOptionList());
					}
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
		
		return resultList;
	}
	
	/**
	 * 移动
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @param id
	 * @param paperTypeId
	 * @return PageResult
	 */
	@RequestMapping("/move")
	@ResponseBody
	public PageResult move(Integer id, Integer paperTypeId) {
		try {
			paperService.move(id, paperTypeId);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("合并试题错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		}  catch (Exception e) {
			log.error("合并试题错误：", e);
			return PageResult.err();
		}
	}
}
