package com.wcpdoc.exam.api.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.core.constant.ConstantManager;
import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperOption;
import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.entity.PaperRemark;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.PaperOptionService;
import com.wcpdoc.exam.core.service.PaperQuestionService;
import com.wcpdoc.exam.core.service.PaperRemarkService;
import com.wcpdoc.exam.core.service.PaperService;
import com.wcpdoc.exam.core.service.QuestionOptionService;
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
	@Resource
	private PaperQuestionService paperQuestionService;
	@Resource
	private QuestionOptionService questionOptionService;
	
	/**
	 * 试卷列表
	 * 
	 * zhanghc 2018年10月21日上午8:16:06
	 * @return pageOut
	 */
	@RequestMapping("/listpage")
	@ResponseBody
	@RequiresRoles("subAdmin")
	public PageResult listpage(PageIn pageIn, String name, String userName) {
		try {
			if (ValidateUtil.isValid(userName)) {
				pageIn.setTwo(userName);
			}
			if (ValidateUtil.isValid(name)) {
				pageIn.setFive(name);
			}
			if(!ConstantManager.ADMIN_LOGIN_NAME.equals(getCurUser().getLoginName())) {
				pageIn.setTen(getCurUser().getId().toString());
			}
			
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
	@RequiresRoles("subAdmin")
	public PageResult add(Paper paper, PaperOption paperOption, PaperRemark paperRemark) {
		try {
			if (paper.getShowType() == null) {
				paper.setShowType(new BigDecimal(1));
			}
			
			paper.setUpdateUserId(getCurUser().getId());
			paper.setUpdateTime(new Date());
			paper.setTotalScore(BigDecimal.ZERO);
			paper.setState(2);
			paperService.add(paper);
			
			paperOption.setPaperId(paper.getId());
			paperOptionService.add(paperOption);
			
			paperRemark.setPaperId(paper.getId());
			paperRemarkService.add(paperRemark);
			
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
	 * 试卷浏览
	 * 
	 * v1.0 chenyun 2021年3月29日下午5:40:20
	 * @param paperId
	 * @return PageResult
	 */
	@RequestMapping("/get")
	@ResponseBody
	@RequiresRoles("subAdmin")
	public PageResult echo(Integer paperId) {
		try {
			Paper paper = paperService.getEntity(paperId);
			PaperOption paperOption = paperOptionService.getPaperOption(paper.getId());
			List<PaperRemark> paperRemarkList = paperRemarkService.getPaperRemarkList(paper.getId());
			return PageResultEx.ok()
					.addAttr("paper", paper)
					.addAttr("paperOption", paperOption)
					.addAttr("paperRemarkList", paperRemarkList);
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
	@RequiresRoles("subAdmin")
	public PageResult edit(Integer paperId, Paper paper, PaperOption paperOption, List<PaperRemark> paperRemark) {
		try {
			Paper entity = paperService.getEntity(paperId);
			entity.setName(paper.getName());
			entity.setPassScore(paper.getPassScore());
			entity.setReadRemark(paper.getReadRemark());
			entity.setReadNum(paper.getReadNum());
			entity.setShowType(paper.getShowType());
			entity.setUpdateUserId(getCurUser().getId());
			entity.setUpdateTime(new Date());
			paperService.update(entity);
			
			PaperOption paperOptionEntity = paperOptionService.getPaperOption(entity.getId());
			paperOptionEntity.setQuestion(paperOption.getQuestion());
			paperOptionEntity.setQuestionOption(paperOption.getQuestionOption());
			paperOptionEntity.setRightClick(paperOption.getRightClick());
			paperOptionEntity.setRightCopy(paperOption.getRightCopy());
			paperOptionEntity.setMinimize(paperOption.getMinimize());
			paperOptionEntity.setMinimizeNum(paperOption.getMinimizeNum());
			paperOptionService.update(paperOptionEntity);

			paperRemarkService.removePaperRemark(entity.getId());//重新添加评语
			for (int i = 0; i < paperRemark.size(); i++) {
				paperRemark.get(i).setNo(i+1);
				paperRemark.get(i).setPaperId(entity.getId());
				paperRemarkService.add(paperRemark.get(i));
			}
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
	@RequiresRoles("subAdmin")
	public PageResult del(Integer id) {
		try {
			Paper paper = paperService.getEntity(id);
			paper.setState(0);
			paper.setUpdateTime(new Date());
			paper.setUpdateUserId(getCurUser().getId());
			paperService.update(paper);
			// 删除防作弊、成绩评语
			PaperOption paperOption = paperOptionService.getPaperOption(paper.getId());
			if (paperOption != null) {
				paperOptionService.del(paperOption.getId());
			}
			paperRemarkService.removePaperRemark(paper.getId());
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
	@RequiresRoles("subAdmin")
	public PageResult copy(Integer id) {
		try {
			Paper paper = paperService.getEntity(id);
			Paper entity = new Paper();
			BeanUtils.copyProperties(entity, paper);
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(getCurUser().getId());
			paperService.add(entity);
			
			PaperOption paperOption = paperOptionService.getPaperOption(paper.getId());
			PaperOption paperOptionEntity = new PaperOption();
			if(paperOption != null){				
				BeanUtils.copyProperties(paperOptionEntity, paperOption);
			}
			paperOptionEntity.setPaperId(entity.getId());
			paperOptionService.add(paperOptionEntity);

			List<PaperRemark> paperRemarkList = paperRemarkService.getPaperRemarkList(paper.getId());
			if (paperRemarkList != null && paperRemarkList.size() != 0) {
				for(PaperRemark paperRemark : paperRemarkList){
					PaperRemark paperRemarkEntity = new PaperRemark();
					BeanUtils.copyProperties(paperRemarkEntity, paperRemark);
					paperRemarkEntity.setPaperId(entity.getId());
					paperRemarkService.add(paperRemarkEntity);
				}
			}
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
	 * 试卷归档
	 * 
	 * v1.0 zhanghc 2018年11月24日上午9:13:22
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/archive")
	@ResponseBody
	@RequiresRoles("subAdmin")
	public PageResult archive(Integer id) {
		try {
			Paper entity = paperService.getEntity(id);
			entity.setState(3);
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
	@RequiresRoles("subAdmin")
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
	@RequiresRoles("subAdmin")
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
	@RequiresRoles("subAdmin")
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
	@RequiresRoles("subAdmin")
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
	@RequiresRoles("subAdmin")
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
	@RequiresRoles("subAdmin")
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
	 * 章节列表
	 * 
	 * v1.0 chenyun 2021年3月31日下午4:21:20
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/paperQuestionList")
	@ResponseBody
	@RequiresRoles("subAdmin")
	public PageResult PaperQuestionList(Integer id) {
		try {
			List<PaperQuestion> chapterList = paperQuestionService.getChapterList(id);
			List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
			for(PaperQuestion paperQuestion : chapterList){
				//章节
				Map<String, Object> map = new HashMap<String, Object>();
				Map<String, Object> chapterMap = new HashMap<String, Object>();
				chapterMap.put("id", paperQuestion.getId());
				chapterMap.put("name", paperQuestion.getName());
				chapterMap.put("description", paperQuestion.getDescription());
				chapterMap.put("parentId", paperQuestion.getPaperId());
				map.put("chapter",  chapterMap);
				//试题
				List<PaperQuestion> questionList = paperQuestionService.getQuestionList(paperQuestion.getId());
				List<Map<String, Object>> questionsListMap = new ArrayList<Map<String, Object>>();
				for(PaperQuestion questionId : questionList){
					Map<String, Object> questionMap = new HashMap<String, Object>();
					Question entity = questionService.getEntity(questionId.getQuestionId());
					questionMap.put("id", entity.getId());
					questionMap.put("type", entity.getType());
					questionMap.put("difficulty", entity.getDifficulty());
					questionMap.put("title", entity.getTitle());
					questionMap.put("answer", entity.getAnswer());
					questionMap.put("analysis", entity.getAnalysis());
					questionMap.put("score", entity.getScore());
					questionMap.put("scoreOptions", questionId.getScoreOptions());
					if(entity.getType() == 1 || entity.getType() == 2 ){
						questionMap.put("options", questionOptionService.getQuestionOptionList(questionId.getQuestionId()));
					}

					questionsListMap.add(questionMap);
				}
				map.put("questionsList",  questionsListMap);
				mapList.add(map);
			}
			return PageResultEx.ok().data(mapList);
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
	@RequiresRoles("subAdmin")
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
	@RequiresRoles("subAdmin")
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
	@RequestMapping("/scoreOptionsUpdate")
	@ResponseBody
	@RequiresRoles("subAdmin")
	public PageResult scoreOptionsUpdate(Integer paperQuestionId, Integer[] scoreOptions) {
		try {
			paperService.doOptionsUpdate(paperQuestionId, scoreOptions);
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
	@RequiresRoles("subAdmin")
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
	@RequiresRoles("subAdmin")
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
	@RequiresRoles("subAdmin")
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
	@RequiresRoles("subAdmin")
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
	@RequiresRoles("subAdmin")
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
	@RequiresRoles("subAdmin")
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
}
