package com.wcpdoc.exam.core.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.PaperQuestionRuleDao;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.entity.PaperQuestionRule;
import com.wcpdoc.exam.core.entity.PaperQuestionRuleEx;
import com.wcpdoc.exam.core.entity.PaperType;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.service.PaperQuestionRuleService;
import com.wcpdoc.exam.core.service.PaperQuestionService;
import com.wcpdoc.exam.core.service.PaperService;
import com.wcpdoc.exam.core.service.PaperTypeService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionTypeService;

/**
 * 随机章节服务层实现
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
@Service
public class PaperQuestionRuleServiceImpl extends BaseServiceImp<PaperQuestionRule> implements PaperQuestionRuleService {
	@Resource
	private PaperQuestionRuleDao paperQuestionRuleDao;
	@Resource
	private PaperService paperService;
	@Resource
	private QuestionTypeService questionTypeService;
	@Resource
	private PaperTypeService paperTypeService;
	@Resource
	private QuestionService questionService;
	@Resource
	private PaperQuestionService paperQuestionService;
	
	@Override
	@Resource(name = "paperQuestionRuleDaoImpl")
	public void setDao(BaseDao<PaperQuestionRule> dao) {
		super.dao = dao;
	}

	@Override
	public List<Map<String, Object>> paperQuestionRuleList(Integer paperId) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(paperId)) {
			throw new MyException("参数错误：paperId");
		}
		List<Map<String, Object>> resultList = new ArrayList<>();
		List<PaperQuestion> chapterList = paperQuestionService.getChapterList(paperId);
		for (PaperQuestion chapter : chapterList) {
			Map<String, Object> singleResult = new HashMap<String, Object>();// 章节
			Map<String, Object> chapterMap = new HashMap<String, Object>();
			chapterMap.put("id", chapter.getId());
			chapterMap.put("name", chapter.getName());
			chapterMap.put("description", chapter.getDescription());
			singleResult.put("chapter", chapterMap);
			// 章节规则
			List<Map<String, Object>> paperQuestionRuleListMap = new ArrayList<>();
			List<PaperQuestionRule> paperQuestionRuleList = getChapterList(chapter.getPaperId(), chapter.getId());
			for (PaperQuestionRule paperQuestionRule : paperQuestionRuleList) {
				Map<String, Object> paperQuestionRuleMap = new HashMap<>();
				paperQuestionRuleMap.put("id", paperQuestionRule.getId());
				paperQuestionRuleMap.put("paperId", paperQuestionRule.getPaperId());
				paperQuestionRuleMap.put("questionTypeId", paperQuestionRule.getQuestionTypeId());
				paperQuestionRuleMap.put("questionTypeName", questionTypeService.getEntity(paperQuestionRule.getQuestionTypeId()).getName());
				paperQuestionRuleMap.put("type", paperQuestionRule.getType());
				paperQuestionRuleMap.put("difficulty", paperQuestionRule.getDifficultyArr());
				paperQuestionRuleMap.put("ai", paperQuestionRule.getAiArr());
				Integer[] scoreOptions = null;
				if (ValidateUtil.isValid(paperQuestionRule.getScoreOptions())) {
					String[] split = paperQuestionRule.getScoreOptions().split(",");
					scoreOptions = new Integer[split.length];
					for(int i = 0; i < split.length; i++ ){
						scoreOptions[i] = Integer.parseInt(split[i]);
					}
				} else {
					scoreOptions = new Integer[0];
				}
				paperQuestionRuleMap.put("scoreOptions", scoreOptions);
				paperQuestionRuleMap.put("totalNumber", paperQuestionRule.getNum());
				paperQuestionRuleMap.put("score", paperQuestionRule.getScore());
				paperQuestionRuleListMap.add(paperQuestionRuleMap);
			}
			
			singleResult.put("paperQuestionRuleList", paperQuestionRuleListMap);
			resultList.add(singleResult);
		}
		return resultList;
	}
	
	@Override
	public void addAndUpdate(PaperQuestionRule paperQuestionRule) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(paperQuestionRule.getPaperQuestionId())) {
			throw new MyException("参数错误：paperQuestionId");
		}
		if (!ValidateUtil.isValid(paperQuestionRule.getPaperId())) {
			throw new MyException("参数错误：paperId");
		}
		if (!ValidateUtil.isValid(paperQuestionRule.getQuestionTypeId())) {
			throw new MyException("参数错误：questionTypeId");
		}
		if (!ValidateUtil.isValid(paperQuestionRule.getType())) {
			throw new MyException("参数错误：type");
		}
		if (!ValidateUtil.isValid(paperQuestionRule.getNum())) {
			throw new MyException("参数错误：totalNumber");
		}
		if (!ValidateUtil.isValid(paperQuestionRule.getScore())) {
			throw new MyException("参数错误：score");
		}
		
		QuestionType questionType = questionTypeService.getEntity(paperQuestionRule.getQuestionTypeId());// 试题分类校验
		if(!questionTypeService.hasWriteAuth(questionType, getCurUser().getId())) {
			throw new MyException("无操作权限");
		}
		Paper paper = paperService.getEntity(paperQuestionRule.getPaperId());//试卷校验
		if (paper.getState() == 0) {
			throw new MyException("试卷已删除");
		}
		if (paper.getState() == 1) {
			throw new MyException("试卷已发布");
		}
		if(paper.getState() == 3){
			throw new MyException("已归档");
		}
		PaperType paperType = paperTypeService.getEntity(paper.getPaperTypeId());// 试卷分类权限校验
		if(paperType.getCreateUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("无操作权限");
		}
		if (paperQuestionRule.getType() != 3 && paperQuestionRule.getType() != 5) {//除填空和阅读其他都是智能阅卷
			paperQuestionRule.setAis("1");
		}
		
		if (paperQuestionRule.getType() == 2) {
			paperQuestionRule.setScoreOptions("2");
		}
		
		paperQuestionRule.setUpdateUserId(getCurUser().getId());
		paperQuestionRule.setUpdateTime(new Date());
		List<PaperQuestionRule> paperQuestionRuleList = getChapterList(paperQuestionRule.getPaperId(), paperQuestionRule.getPaperQuestionId());
		paperQuestionRule.setNo(paperQuestionRuleList.size() + 1 );
		add(paperQuestionRule);
	}

	@Override
	public void updateAndUpdate(PaperQuestionRule paperQuestionRule) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(paperQuestionRule.getId())) {
			throw new MyException("参数错误：id");
		}
		if (!ValidateUtil.isValid(paperQuestionRule.getPaperQuestionId())) {
			throw new MyException("参数错误：paperQuestionId");
		}
		if (!ValidateUtil.isValid(paperQuestionRule.getPaperId())) {
			throw new MyException("参数错误：paperId");
		}
		if (!ValidateUtil.isValid(paperQuestionRule.getQuestionTypeId())) {
			throw new MyException("参数错误：questionTypeId");
		}
		if (!ValidateUtil.isValid(paperQuestionRule.getType())) {
			throw new MyException("参数错误：type");
		}
		if (!ValidateUtil.isValid(paperQuestionRule.getNum())) {
			throw new MyException("参数错误：totalNumber");
		}
		if (!ValidateUtil.isValid(paperQuestionRule.getScore())) {
			throw new MyException("参数错误：score");
		}
		
		QuestionType questionType = questionTypeService.getEntity(paperQuestionRule.getQuestionTypeId());// 试题分类校验
		if(!questionTypeService.hasWriteAuth(questionType, getCurUser().getId())) {
			throw new MyException("无操作权限");
		}
		
		Paper paper = paperService.getEntity(paperQuestionRule.getPaperId());//试卷校验
		if (paper.getState() == 0) {
			throw new MyException("试卷已删除");
		}
		if (paper.getState() == 1) {
			throw new MyException("试卷已发布");
		}
		if(paper.getState() == 3){
			throw new MyException("已归档");
		}
		PaperType paperType = paperTypeService.getEntity(paper.getPaperTypeId());//试卷分类权限校验
		if(paperType.getCreateUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("无操作权限");
		}
		if (paperQuestionRule.getType() != 3 && paperQuestionRule.getType() != 5) {//除填空和阅读其他都是智能阅卷
			paperQuestionRule.setAis("1");
		}
		
		PaperQuestionRule entity = getEntity(paperQuestionRule.getId());
		entity.setQuestionTypeId(paperQuestionRule.getQuestionTypeId());
		entity.setType(paperQuestionRule.getType());
		entity.setDifficultys(paperQuestionRule.getDifficultys());
		entity.setAis(paperQuestionRule.getAis());
		entity.setScoreOptions(paperQuestionRule.getScoreOptions());
		entity.setNum(paperQuestionRule.getNum());
		entity.setScore(paperQuestionRule.getScore());
		entity.setUpdateUserId(getCurUser().getId());
		entity.setUpdateTime(new Date());
		update(entity);
	}
	
	@Override
	public void delAndUpdate(Integer[] ids) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(ids)) {
			throw new MyException("参数错误：ids");
		}
		
		PaperQuestionRule paperQuestionRule = null;
		for (Integer id : ids) {
			paperQuestionRule = getEntity(id);
			if (paperQuestionRule == null) {
				throw new MyException("参数错误：ids");
			}
			del(paperQuestionRule.getId());//删除随机章节
		}
		
		// 同级随机章节重新排序
		List<PaperQuestionRule> chapterList = getChapterList(paperQuestionRule.getPaperId(), paperQuestionRule.getPaperQuestionId());
		int maxNo = 1;
		for (PaperQuestionRule cur : chapterList) {
			cur.setNo(maxNo++);
			update(cur);
		}
	}

	@Override
	public List<PaperQuestionRule> getChapterList(Integer paperId, Integer paperQuestionId) {
		return paperQuestionRuleDao.getChapterList(paperId, paperQuestionId);
	}

	@Override
	public List<Question> getQuestionList(Integer questionTypeId) {
		return paperQuestionRuleDao.getQuestionList(questionTypeId);
	}

	@Override
	public List<PaperQuestionRuleEx> questionListCache(Integer questionTypeId) {
		List<Question> questionList = getQuestionList(questionTypeId);
		Map<PaperQuestionRuleEx, Integer> ruleExNumCache = new HashMap<>();
		for (Question question : questionList) {
			PaperQuestionRuleEx paperQuestionRuleEx = new PaperQuestionRuleEx(question.getType(), question.getDifficulty(), question.getAi());
			if (ruleExNumCache.get(paperQuestionRuleEx) == null) {
				ruleExNumCache.put(paperQuestionRuleEx, 1);
			} else {
				ruleExNumCache.put(paperQuestionRuleEx, ruleExNumCache.get(paperQuestionRuleEx) + 1); // 
			}
		}
		
		List<PaperQuestionRuleEx> paperQuestionRuleExList = new ArrayList<>();
		for (PaperQuestionRuleEx paperQuestionRuleEx : ruleExNumCache.keySet()) {
			PaperQuestionRuleEx newPaperQuestionRuleEx = new PaperQuestionRuleEx(paperQuestionRuleEx.getType(), paperQuestionRuleEx.getDifficulty(), paperQuestionRuleEx.getAi());
			try {
				BeanUtils.copyProperties(paperQuestionRuleEx, newPaperQuestionRuleEx);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new MyException(e.getMessage());
			}
			newPaperQuestionRuleEx.setNum(ruleExNumCache.get(paperQuestionRuleEx));
			paperQuestionRuleExList.add(newPaperQuestionRuleEx);
		}
		return paperQuestionRuleExList;
	}
}