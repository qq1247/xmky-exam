package com.wcpdoc.exam.api.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.core.constant.ConstantManager;
import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.PaperOptionService;
import com.wcpdoc.exam.core.service.PaperRemarkService;
import com.wcpdoc.exam.core.service.PaperService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.util.ValidateUtil;
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
	@Resource
	private QuestionService questionService;
	@Resource
	private PaperOptionService paperOptionService;
	@Resource
	private PaperRemarkService paperRemarkService;
	
	/**
	 * 试卷列表
	 * 
	 * zhanghc 2018年10月21日上午8:16:06
	 * @return pageOut
	 */
	@RequestMapping("/list")
	@ResponseBody
	public PageResult list(PageIn pageIn, Integer paperTypeId, String name) {
		// one paperTypeId(试卷分类ID)
		// two name(试卷名称)
		// three state(试卷状态)
		// four id(试卷ID)
		try {
			if(paperTypeId != null){
				pageIn.setOne(paperTypeId.toString());
			}
			if (ValidateUtil.isValid(name)) {
				pageIn.setTwo(name);
			}
			
			if(!ConstantManager.ADMIN_LOGIN_NAME.equals(getCurUser().getLoginName())) {
				pageIn.setTen(getCurUser().getId().toString());
			}
			return PageResultEx.ok().data(paperService.getListpage(pageIn));
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
	public PageResult add(Paper paper) {
		try {
			paper.setUpdateUserId(getCurUser().getId());
			paper.setUpdateTime(new Date());
			paper.setTotalScore(BigDecimal.ZERO);
			paper.setState(2);
			paperService.add(paper);
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
	public PageResult edit(Paper paper) {
		try {
			Paper entity = paperService.getEntity(paper.getId());
//			entity.setPaperTypeId(paper.getPaperTypeId());//不需要修改
			entity.setName(paper.getName());
			entity.setPassScore(paper.getPassScore());
			entity.setDescription(paper.getDescription());
			entity.setShowType(paper.getShowType());
			//entity.setState(paper.getState());//单独控制
			entity.setUpdateUserId(getCurUser().getId());
			entity.setUpdateTime(new Date());
			paperService.update(entity);
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
			Paper paper = paperService.getEntity(id);
			paper.setState(0);
			paper.setUpdateTime(new Date());
			paper.setUpdateUserId(getCurUser().getId());
			paperService.update(paper);
			// 删除防作弊、成绩评语
			paperOptionService.del(paperOptionService.getPaperOption(paper.getId()).getId());
			paperRemarkService.del(paperRemarkService.getPaperRemark(paper.getId()).getId());
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
			Paper paper = paperService.getEntity(id);
			Paper entity = new Paper();
			BeanUtils.copyProperties(entity, paper);
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(getCurUser().getId());
			paperService.add(entity);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("拷贝试题错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		}  catch (Exception e) {
			log.error("拷贝试题错误：", e);
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
			paperService.doChapterAdd(chapter);
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
			paperService.doChapterEdit(chapter);
			return PageResult.ok();
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
			paperService.doChapterDel(chapterId);
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
	 * 章节上移
	 * 
	 * v1.0 zhanghc 2018年10月21日上午10:46:54
	 * @param chapterId
	 * @return PageResult
	 */
	@RequestMapping("/chapterUp")
	@ResponseBody
	public PageResult chapterUp(Integer chapterId) {
		try {
			paperService.doChapterUp(chapterId);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("章节上移错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("章节上移错误：", e);
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
	@RequestMapping("/chapterDown")
	@ResponseBody
	public PageResult chapterDown(Integer chapterId) {
		try {
			paperService.doChapterDown(chapterId);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("章节下移错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("章节下移错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 试题列表
	 * 
	 * v1.0 zhanghc 2018年10月21日上午8:17:09
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/questionList")
	@ResponseBody
	public PageResult questionList(PageIn pageIn) {
		try {
			if (getCurUser().getId() != 1) {
				pageIn.setFour("1");
				pageIn.setTen(getCurUser().getId().toString());
			}

			return PageResultEx.ok().data(questionService.getListpage(pageIn));
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
			paperService.doQuestionAdd(chapterId, questionIds);
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
	@RequestMapping("/scoreUpdate")
	@ResponseBody
	public PageResult scoreUpdate(Integer paperQuestionId, BigDecimal score) {
		try {
			paperService.doScoreUpdate(paperQuestionId, score);
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
	@RequestMapping("/optionsUpdate")
	@ResponseBody
	public PageResult optionsUpdate(Integer paperQuestionId, Integer[] options) {
		try {
			paperService.doOptionsUpdate(paperQuestionId, options);
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
	@RequestMapping("/batchScoreUpdate")
	@ResponseBody
	public PageResult batchScoreUpdate(Integer chapterId, BigDecimal score, String options) {
		try {
			paperService.doBatchScoreUpdate(chapterId, score, options);
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
	@RequestMapping("/questionUp")
	@ResponseBody
	public PageResult questionUp(Integer paperQuestionId) {
		try {
			paperService.doQuestionUp(paperQuestionId);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("试题上移错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("试题上移错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 试题下移
	 * 
	 * v1.0 zhanghc 2018年10月21日上午10:46:54
	 * @param paperQuestionId
	 * @return PageResult
	 */
	@RequestMapping("/questionDown")
	@ResponseBody
	public PageResult questionDown(Integer paperQuestionId) {
		try {
			paperService.doQuestionDown(paperQuestionId);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("试题下移错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("试题下移错误：", e);
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
	public PageResult questionDel(Integer paperQuestionId) {
		try {
			paperService.doQuestionDel(paperQuestionId);
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
			paperService.doQuestionClear(chapterId);
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
			Paper paper = paperService.getEntity(id);
			if(paper.getState() == 0) {
				throw new MyException("试卷【"+paper.getName()+"】已删除！");
			}
			if(paper.getState() == 1) {
				throw new MyException("试卷【"+paper.getName()+"】已发布！");
			}
			
			paper.setState(1);
			paperService.update(paper);
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
	 * 预览试卷
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @param id
	 * @return pageOut
	 */
	@RequestMapping("/get")
	@ResponseBody
	public PageResult get(Integer id) {
		try {
			Map<String, Object> map = new HashMap<>();
			Paper entity = paperService.getEntity(id);
			map.put("id", entity.getId());
			map.put("name", entity.getName());
			map.put("passScore", entity.getPassScore());
			map.put("totalScore", entity.getTotalScore());
			map.put("description", entity.getDescription());
			map.put("paperTypeId", entity.getPaperTypeId());
			map.put("state", entity.getState());
			return PageResultEx.ok().data(map);
		} catch (MyException e) {
			log.error("预览试卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		}  catch (Exception e) {
			log.error("预览试卷错误：", e);
			return PageResult.err();
		}
	}
}
