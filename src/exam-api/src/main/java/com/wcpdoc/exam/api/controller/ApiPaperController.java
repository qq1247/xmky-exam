package com.wcpdoc.exam.api.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.base.cache.DictCache;
import com.wcpdoc.exam.base.service.UserService;
import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.entity.PaperQuestionAnswer;
import com.wcpdoc.exam.core.entity.PaperRemark;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.PaperQuestionAnswerService;
import com.wcpdoc.exam.core.service.PaperQuestionService;
import com.wcpdoc.exam.core.service.PaperRemarkService;
import com.wcpdoc.exam.core.service.PaperService;
import com.wcpdoc.exam.core.service.QuestionAnswerService;
import com.wcpdoc.exam.core.service.QuestionOptionService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionTypeService;
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
	private PaperRemarkService paperRemarkService;
	@Resource
	private PaperQuestionService paperQuestionService;
	@Resource
	private QuestionOptionService questionOptionService;
	@Resource
	private UserService userService;
	@Resource
	private QuestionTypeService questionTypeService;
	@Resource
	private QuestionAnswerService questionAnswerService;
	@Resource
	private PaperQuestionAnswerService paperQuestionAnswerService;
	
	/**
	 * 试卷列表
	 * 
	 * zhanghc 2018年10月21日上午8:16:06
	 * @return pageOut
	 */
	@RequestMapping("/listpage")
	@ResponseBody
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
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
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
	public PageResult add(Paper paper, PaperRemark paperRemark) {
		try {
			if(!ValidateUtil.isValid(paper.getName())){
				throw new MyException("参数错误不能为空！");
			}
			if (paper.getShowType() == null) {
				paper.setShowType(new BigDecimal(1));
			}
			
			paper.setCreateUserId(getCurUser().getId());
			paper.setCreateTime(new Date());
			paper.setUpdateTime(new Date());
			paper.setUpdateUserId(getCurUser().getId());
			paper.setTotalScore(BigDecimal.ZERO);
			paper.setState(2);
			paperService.add(paper);
			
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
	 * 修改试卷
	 * 
	 * zhanghc 2018年10月21日上午8:16:06
	 * @return pageOut
	 */
	@RequestMapping("/edit")
	@ResponseBody
	@RequiresRoles("subAdmin")
	public PageResult edit(Paper paper) { //, List<PaperRemark> paperRemark
		try {
			Paper entity = paperService.getEntity(paper.getId());
			if(entity.getState() == 1){
				throw new MyException("已发布的不能修改！");
			}
			entity.setName(paper.getName());
			entity.setPassScore(paper.getPassScore());
			entity.setReadRemark(paper.getReadRemark());
			entity.setReadNum(paper.getReadNum());
			entity.setShowType(paper.getShowType());
			entity.setMinimizeNum(paper.getMinimizeNum());
			entity.setUpdateUserId(getCurUser().getId());
			entity.setUpdateTime(new Date());
			paperService.update(entity);

			/*paperRemarkService.remove(entity.getId());//重新添加评语
			for (int i = 0; i < paperRemark.size(); i++) {
				paperRemark.get(i).setNo(i+1);
				paperRemark.get(i).setPaperId(entity.getId());
				paperRemarkService.add(paperRemark.get(i));
			}*/
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
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
	public PageResult del(Integer id) {
		try {
			Paper paper = paperService.getEntity(id);
			paper.setState(0);
			paper.setUpdateTime(new Date());
			paper.setUpdateUserId(getCurUser().getId());
			paperService.update(paper);
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
	@RequiresRoles(value={"subAdmin","user"},logical = Logical.OR)
	public PageResult get(Integer id) {
		try {
			return PageResultEx.ok().data(paperService.getEntity(id));
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
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
	public PageResult copy(Integer id) {
		try {
			Paper paper = paperService.getEntity(id);
			Paper entity = new Paper();
			BeanUtils.copyProperties(entity, paper);
			entity.setName(paper.getName()+"【复件】");
			entity.setState(2);
			entity.setCreateTime(new Date());
			entity.setCreateUserId(getCurUser().getId());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(getCurUser().getId());
			paperService.add(entity);

			List<PaperQuestion> chapterList = paperQuestionService.getChapterList(paper.getId());
			if (chapterList != null && chapterList.size() != 0) {
				for(PaperQuestion paperQuestionChapter : chapterList){//章节
					PaperQuestion paperQuestionChapterEntity = new PaperQuestion();
					BeanUtils.copyProperties(paperQuestionChapterEntity, paperQuestionChapter);
					paperQuestionChapterEntity.setPaperId(entity.getId());
					paperQuestionService.add(paperQuestionChapterEntity);
					paperQuestionChapterEntity.setParentSub("_"+paperQuestionChapterEntity.getId()+"_");
					paperQuestionService.update(paperQuestionChapterEntity);
					
					List<PaperQuestion> questionList = paperQuestionService.getQuestionList(paperQuestionChapter.getId());
					for(PaperQuestion paperQuestionQuestion : questionList){//试卷试题
						PaperQuestion paperQuestionQuestionEntity = new PaperQuestion();
						BeanUtils.copyProperties(paperQuestionQuestionEntity, paperQuestionQuestion);
						paperQuestionQuestionEntity.setParentId(paperQuestionChapterEntity.getId());
						paperQuestionQuestionEntity.setPaperId(entity.getId());
						paperQuestionService.add(paperQuestionQuestionEntity);
						paperQuestionQuestionEntity.setParentSub(paperQuestionChapterEntity.getParentSub() + paperQuestionQuestionEntity.getId()+"_");
						paperQuestionService.update(paperQuestionQuestionEntity);
						
						List<PaperQuestionAnswer> paperQuestionAnswerList = paperQuestionAnswerService.getPaperQuestionAnswerList(paperQuestionChapter.getId(), paperQuestionQuestion.getQuestionId());
						for(PaperQuestionAnswer paperQuestionAnswer : paperQuestionAnswerList){
							PaperQuestionAnswer paperQuestionAnswerEntity = new PaperQuestionAnswer();
							BeanUtils.copyProperties(paperQuestionAnswerEntity, paperQuestionAnswer);
							paperQuestionAnswerEntity.setPaperQuestionId(paperQuestionChapterEntity.getId());
							paperQuestionAnswerService.add(paperQuestionAnswerEntity);
						}
					}
				}
			}
			
			List<PaperRemark> paperRemarkList = paperRemarkService.getList(paper.getId());
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
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
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
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
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
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
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
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
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
	 * 章节上移
	 * 
	 * v1.0 zhanghc 2018年10月21日上午10:46:54
	 * @param chapterId
	 * @return PageResult
	 */
	@RequestMapping("/chapterUp")
	@ResponseBody
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
	public PageResult chapterUp(Integer oldId, Integer newId) {
		try {
			paperService.chapterUp(oldId, newId);
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
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
	public PageResult chapterDown(Integer chapterId) {
		try {
			paperService.chapterDown(chapterId);
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
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
	public PageResult questionList() {
		try {
			PageIn pageIn = new PageIn(request);
			if (getCurUser().getId() != 1) {
				pageIn.addAttr("PaperId", "1")
					  .addAttr("curUserId", getCurUser().getId());
			}
			return PageResultEx.ok().data(questionService.getListpage(pageIn));
		} catch (Exception e) {
			log.error("试题列表错误：", e);
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
	@RequiresRoles(value={"user","subAdmin"},logical = Logical.OR)
	public PageResult paperQuestionList(Integer id) {
		try {
			List<PaperQuestion> chapterList = paperQuestionService.getChapterList(id);
			List<Map<String, Object>> mapList = new ArrayList<>();
			for(PaperQuestion chapter : chapterList){
				//章节
				Map<String, Object> map = new HashMap<String, Object>();
				Map<String, Object> chapterMap = new HashMap<String, Object>();
				chapterMap.put("id", chapter.getId());
				chapterMap.put("name", chapter.getName());
				chapterMap.put("description", chapter.getDescription());
				chapterMap.put("parentId", chapter.getPaperId());
				map.put("chapter",  chapterMap);
				//试题
				List<PaperQuestion> paperQuestionList = paperQuestionService.getQuestionList(chapter.getId());
				List<Map<String, Object>> questionsListMap = new ArrayList<>();
				for(PaperQuestion paperQuestion : paperQuestionList){
					Map<String, Object> questionMap = new HashMap<>();
					Question question = questionService.getEntity(paperQuestion.getQuestionId());
					questionMap.put("id", question.getId());
					questionMap.put("ai", question.getAi());
					questionMap.put("type", question.getType());
					questionMap.put("typeName", DictCache.getDictValue("QUESTION_TYPE", question.getType().toString()));
					questionMap.put("difficulty", question.getDifficulty());
					questionMap.put("difficultyName", DictCache.getDictValue("QUESTION_DIFFICULTY", question.getDifficulty().toString()));
					questionMap.put("updateUserName", userService.getEntity(question.getUpdateUserId()).getName());
					questionMap.put("title", question.getTitle());
					questionMap.put("analysis", question.getAnalysis());
					questionMap.put("score", paperQuestion.getScore());
					questionMap.put("scoreOptions", paperQuestion.getScoreOptions());
					questionMap.put("paperQuestionId", paperQuestion.getId());
					questionMap.put("options", new String[0]);// 默认为长度为0的数组
					
					//if(question.getType() == 1 || question.getType() == 2 ){
						List<QuestionOption> questionOptionList = questionOptionService.getList(paperQuestion.getQuestionId());
						if (questionOptionList != null) {							
							String[] options = new String[questionOptionList.size()];
							for (int i = 0; i < questionOptionList.size(); i++) {
								options[i] = questionOptionList.get(i).getOptions();// 按选项顺序添加试题
							}
							questionMap.put("options", options);
						}
					//}
					
					QuestionType questionType = questionTypeService.getEntity(question.getQuestionTypeId());
					boolean writeAuth = questionTypeService.hasWriteAuth(questionType, getCurUser().getId());
					boolean readAuth = questionTypeService.hasReadAuth(questionType, getCurUser().getId());
					List<PaperQuestionAnswer> paperQuestionAnswerList = paperQuestionAnswerService.getPaperQuestionAnswerList(chapter.getId(), question.getId());

					List<Map<String, Object>> questionAnswerSplitList = new ArrayList<Map<String, Object>>();
					if (question.getType() == 3) {
						for(PaperQuestionAnswer paperQuestionAnswer : paperQuestionAnswerList){
							Map<String, Object> questionAnswerMap = new HashMap<String, Object>();
							String[] split = paperQuestionAnswer.getAnswer().split("\n");
							questionAnswerMap.put("id", paperQuestionAnswer.getId());
							questionAnswerMap.put("answer", split);
							questionAnswerMap.put("score", paperQuestionAnswer.getScore());
							questionAnswerMap.put("questionId", paperQuestionAnswer.getQuestionId());
							questionAnswerSplitList.add(questionAnswerMap);
						}
					} else if (question.getType() == 5 && question.getAi() == 1) {
						for(PaperQuestionAnswer paperQuestionAnswer : paperQuestionAnswerList){					
							Map<String, Object> questionAnswerMap = new HashMap<String, Object>();
							String[] split = paperQuestionAnswer.getAnswer().split("\n");
							questionAnswerMap.put("id", paperQuestionAnswer.getId());
							questionAnswerMap.put("answer", split);
							questionAnswerMap.put("score", paperQuestionAnswer.getScore());
							questionAnswerMap.put("questionId", paperQuestionAnswer.getQuestionId());
							questionAnswerSplitList.add(questionAnswerMap);
						}
					} else {
						for(PaperQuestionAnswer paperQuestionAnswer : paperQuestionAnswerList){
							Map<String, Object> questionAnswerMap = new HashMap<String, Object>();
							questionAnswerMap.put("id", paperQuestionAnswer.getId());
							questionAnswerMap.put("answer", paperQuestionAnswer.getAnswer());
							questionAnswerMap.put("score", paperQuestionAnswer.getScore());
							questionAnswerMap.put("questionId", paperQuestionAnswer.getQuestionId());
							questionAnswerSplitList.add(questionAnswerMap);
						}
					}
					
					if (!writeAuth & !readAuth) {
						for(Map<String, Object> forMap : questionAnswerSplitList){
							forMap.put("answer", null);
						}
					}
					questionMap.put("answers", questionAnswerSplitList);

					questionsListMap.add(questionMap);
				}
				map.put("questionList",  questionsListMap);
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
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
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
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
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
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
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
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
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
	@RequestMapping("/questionUp")
	@ResponseBody
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
	public PageResult questionUp(Integer paperQuestionId) {
		try {
			paperService.questionUp(paperQuestionId);
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
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
	public PageResult questionDown(Integer paperQuestionId) {
		try {
			paperService.questionDown(paperQuestionId);
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
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
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
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
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
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
	public PageResult publish(Integer id) {
		try {
			Paper paper = paperService.getEntity(id);
			if(paper.getState() == 0) {
				throw new MyException("试卷【"+paper.getName()+"】已删除！");
			}
			if(paper.getState() == 1) {
				throw new MyException("试卷【"+paper.getName()+"】已发布！");
			}
			
			paper.setUpdateTime(new Date());
			paper.setUpdateUserId(getCurUser().getId());
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
