package com.wcpdoc.exam.api.controller;

import java.math.BigDecimal;

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
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.entity.PaperRemark;
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
	 * @return pageOut
	 */
	@RequestMapping("/add")
	@ResponseBody
	public PageResult add(Paper paper, PaperRemark paperRemark) {
		try {
			paperService.addAndUpdate(paper, paperRemark);
			return PageResult.ok();
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
					.addAttr("showType", paper.getShowType())
					.addAttr("readRemark", paper.getReadRemark())
					.addAttr("readNum", paper.getReadNum())
					.addAttr("state", paper.getState())
					.addAttr("paperTypeId", paper.getPaperTypeId())
					.addAttr("genType", paper.getGenType())
					.addAttr("options", paper.getOptions())
					.addAttr("minimizeNum", paper.getMinimizeNum())
					.addAttr("createUserId", paper.getCreateUserId())
					.addAttr("updateUserId", paper.getUpdateUserId());
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
	 * 移动章节或试题
	 * 章节之间可相互移动；同章节下试题之间可相互移动；不同章节下试题可跨章节移动；
	 * 
	 * v1.0 zhanghc 2018年10月21日上午10:46:54
	 * @param sourceId
	 * @param targetId
	 * @return PageResult
	 */
	@RequestMapping("/chapterQuestionMove")
	@ResponseBody
	public PageResult chapterQuestionMove(Integer sourceId, Integer targetId) {
		try {
			paperService.chapterQuestionMove(sourceId, targetId);
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
	 * 试卷试题列表
	 * 拥有读写权限才显示答案字段
	 * 
	 * v1.0 chenyun 2021年3月31日下午4:21:20
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/paperQuestionList")
	@ResponseBody
	public PageResult paperQuestionList(Integer id) {
		try {
			return PageResultEx.ok().data(paperService.paperQuestionList(id));
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
	 * @return PageResult
	 */
	@RequestMapping("/questionAdd")
	@ResponseBody
	public PageResult questionAdd(Integer chapterId, Integer[] questionIds) {
		try {
			paperService.questionAdd(chapterId, questionIds);
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
	 * @param options
	 * @return PageResult
	 */
//	@RequestMapping("/updateBatchScore")// 暂时不用
//	@ResponseBody
//	public PageResult updateBatchScore(Integer chapterId, BigDecimal score, String options) {
//		try {
//			paperService.batchScoreUpdate(chapterId, score, options);
//			return PageResult.ok();
//		} catch (MyException e) {
//			log.error("设置分数错误：{}", e.getMessage());
//			return PageResult.err().msg(e.getMessage());
//		} catch (Exception e) {
//			log.error("设置分数错误：", e);
//			return PageResult.err();
//		}
//	}
	
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
	 * @return PageResult
	 */
	@RequestMapping("/scoreUpdate")
	@ResponseBody
	public PageResult scoreUpdate(Integer id, Integer questionId, BigDecimal score, BigDecimal[] subScores) {
		try {
			paperService.scoreUpdate(id, questionId, score, subScores);
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
	 * 设置分数选项
	 * 
	 * v1.0 zhanghc 2018年10月21日上午10:46:54
	 * @param paperQuestionId
	 * @param options
	 * @return PageResult
	 */
	@RequestMapping("/scoreOptionUpdate")
	@ResponseBody
	public PageResult scoreOptionUpdate(Integer id, Integer questionId, Integer[] scoreOptions) {
		try {
			paperService.scoreOptionUpdate(id, questionId, scoreOptions);
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
}
