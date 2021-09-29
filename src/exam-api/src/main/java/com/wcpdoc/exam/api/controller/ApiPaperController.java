package com.wcpdoc.exam.api.controller;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.entity.PaperRemark;
import com.wcpdoc.exam.core.exception.MyException;
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
	 * 试卷浏览
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
			log.error("添加试卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("添加试卷错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 拷贝试卷
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
			Paper entity = paperService.getEntity(id);
			entity.setState(3);
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(getCurUser().getId());
			paperService.update(entity);
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
	public PageResult chapterEdit(PaperQuestion chapter) {
		try {
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
	public PageResult chapterDel(Integer id) {
		try {
			paperService.chapterDel(id);
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
	 * 章节试题移动
	 * 
	 * v1.0 zhanghc 2018年10月21日上午10:46:54
	 * @param chapterId
	 * @return PageResult
	 */
	@RequestMapping("/movePosition")
	@ResponseBody
	public PageResult movePosition(Integer sourceId, Integer targetId) {
		try {
			paperService.movePosition(sourceId, targetId);
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
	 * 章节下移
	 * 
	 * v1.0 zhanghc 2018年10月21日上午10:46:54
	 * @param chapterId
	 * @return PageResult
	 */
//	@RequestMapping("/chapterDown")
//	@ResponseBody
//	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
//	public PageResult chapterDown(Integer chapterId) {
//		try {
//			paperService.chapterDown(chapterId);
//			return PageResult.ok();
//		} catch (MyException e) {
//			log.error("章节下移错误：{}", e.getMessage());
//			return PageResult.err().msg(e.getMessage());
//		} catch (Exception e) {
//			log.error("章节下移错误：", e);
//			return PageResult.err();
//		}
//	}
	
	/**
	 * 试题列表
	 * 
	 * v1.0 zhanghc 2018年10月21日上午8:17:09
	 * @param pageIn
	 * @return PageOut
	 */
//	@RequestMapping("/questionList")
//	@ResponseBody
//	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
//	public PageResult questionList() {
//		try {
//			PageIn pageIn = new PageIn(request);
//			if (getCurUser().getId() != 1) {
//				pageIn.addAttr("paperId", "1")
//					  .addAttr("curUserId", getCurUser().getId());
//			}
//			return PageResultEx.ok().data(questionService.getListpage(pageIn));
//		} catch (Exception e) {
//			log.error("试题列表错误：", e);
//			return PageResult.err();
//		}
//	}
	
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
	 * 设置分数
	 * 
	 * v1.0 zhanghc 2018年10月21日上午10:46:54
	 * @param paperQuestionId
	 * @param score
	 * @param options
	 * @return PageResult
	 */
	@RequestMapping("/updateScore")
	@ResponseBody
	public PageResult updateScore(Integer paperQuestionId, BigDecimal score, Integer[] paperQuestionAnswerId, BigDecimal[] paperQuestionAnswerScore) {
		try {
			paperService.scoreUpdate(paperQuestionId, score, paperQuestionAnswerId, paperQuestionAnswerScore);
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
	 * 设置选项
	 * 
	 * v1.0 zhanghc 2018年10月21日上午10:46:54
	 * @param paperQuestionId
	 * @param options
	 * @return PageResult
	 */
	@RequestMapping("/updateScoreOptions")
	@ResponseBody
	public PageResult updateScoreOptions(Integer paperQuestionId, Integer[] scoreOptions) {
		try {
			paperService.optionsUpdate(paperQuestionId, scoreOptions);
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
	 * 批量设置分数
	 * 
	 * v1.0 zhanghc 2018年10月21日上午10:46:54
	 * @param chapterId
	 * @param score
	 * @param options
	 * @return PageResult
	 */
	@RequestMapping("/updateBatchScore")
	@ResponseBody
	public PageResult updateBatchScore(Integer chapterId, BigDecimal score, String options) {
		try {
			paperService.batchScoreUpdate(chapterId, score, options);
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
	 * 试题上移
	 * 
	 * v1.0 zhanghc 2018年10月21日上午10:46:54
	 * @param paperQuestionId
	 * @return PageResult
	 */
//	@RequestMapping("/questionUp")
//	@ResponseBody
//	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
//	public PageResult questionUp(Integer paperQuestionId) {
//		try {
//			paperService.questionUp(paperQuestionId);
//			return PageResult.ok();
//		} catch (MyException e) {
//			log.error("试题上移错误：{}", e.getMessage());
//			return PageResult.err().msg(e.getMessage());
//		} catch (Exception e) {
//			log.error("试题上移错误：", e);
//			return PageResult.err();
//		}
//	}
	
	/**
	 * 试题下移
	 * 
	 * v1.0 zhanghc 2018年10月21日上午10:46:54
	 * @param paperQuestionId
	 * @return PageResult
	 */
//	@RequestMapping("/questionDown")
//	@ResponseBody
//	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
//	public PageResult questionDown(Integer paperQuestionId) {
//		try {
//			paperService.questionDown(paperQuestionId);
//			return PageResult.ok();
//		} catch (MyException e) {
//			log.error("试题下移错误：{}", e.getMessage());
//			return PageResult.err().msg(e.getMessage());
//		} catch (Exception e) {
//			log.error("试题下移错误：", e);
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
	public PageResult questionDel(Integer paperQuestionId) {
		try {
			paperService.questionDel(paperQuestionId);
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
			log.error("添加试题错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("添加试题错误：", e);
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
	 * 修改试卷总分数
	 * 
	 * v1.0 chenyun 2021年8月23日上午10:58:27
	 * @param id
	 * @param totalScore
	 * @return PageResult
	 */
	@RequestMapping("/updateTotalScore")
	@ResponseBody
	public PageResult updateTotalScore(Integer id, BigDecimal totalScore) {
		try {
			Paper paper = paperService.getEntity(id);
			if(paper.getState() == 0) {
				throw new MyException("试卷【"+paper.getName()+"】已删除！");
			}
			if(paper.getState() == 1) {
				throw new MyException("试卷【"+paper.getName()+"】已发布！");
			}
			
			paper.setTotalScore(totalScore);
			paperService.update(paper);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("修改错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("修改错误：", e);
			return PageResult.err();
		}
	}
}
