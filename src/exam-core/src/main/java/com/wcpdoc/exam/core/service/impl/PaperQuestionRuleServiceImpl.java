package com.wcpdoc.exam.core.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.StringUtil;
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
	public List<Map<String, Object>> chapterAndRuleList(Integer paperId) {
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
			List<Map<String, Object>> ruleMap = new ArrayList<>();
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
				ruleMap.add(paperQuestionRuleMap);
			}
			
			singleResult.put("rule", ruleMap);
			resultList.add(singleResult);
		}
		return resultList;
	}

	@Override
	public void update(Integer paperId, Integer chapterId, Integer[] questionTypeIds, Integer[] types, String[] difficultys, String[] ais, String[] scoreOptions, Integer[] nums, BigDecimal[] scores) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(paperId)) {
			throw new MyException("参数错误：paperId");
		}
		if (!ValidateUtil.isValid(chapterId)) {
			throw new MyException("参数错误：chapterId");
		}
		if (!ValidateUtil.isValid(questionTypeIds)) {
			throw new MyException("参数错误：questionTypeIds");
		}
		if (!ValidateUtil.isValid(types)) {
			throw new MyException("参数错误：types");
		}
		if (!ValidateUtil.isValid(difficultys)) {
			throw new MyException("参数错误：difficultys");
		}
		if (!ValidateUtil.isValid(ais)) {
			throw new MyException("参数错误：ais");
		}
		if (!ValidateUtil.isValid(nums)) {
			throw new MyException("参数错误：nums");
		}
		if (!ValidateUtil.isValid(scores)) {
			throw new MyException("参数错误：scores");
		}
		
		if (questionTypeIds.length == 1) {
			difficultys[0] = StringUtil.join(difficultys);// 规则只有一个的情况下按逗号分隔的难度被拆分成了数组
			ais[0] = StringUtil.join(ais);
			scoreOptions[0] = StringUtil.join(scoreOptions);
		}
		
		for (int i = 0; i < questionTypeIds.length; i++) {// 试题分类校验
			QuestionType questionType = questionTypeService.getEntity(questionTypeIds[i]);
			if(!questionTypeService.hasWriteAuth(questionType, getCurUser().getId())) {
				throw new MyException("无操作权限");
			}
		}
		
		Paper paper = paperService.getEntity(paperId);//试卷校验
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
		
		if(paper.getMarkType() == 1){//判断智能试卷
			for (int i = 0; i < ais.length; i++) {
				if (ais[i].contains("2")) {
					throw new MyException("智能试卷不能包含非智能题");
				}
			}
		}
		
		for (int i = 0; i < types.length; i++) {
			if (types[i] != 3 && types[i] != 5) {//除填空和阅读其他都是智能阅卷
				ais[i] = "1";
			}
			
			if (types[i] == 2) {// 多选漏选的分必填
				scoreOptions[i] = "2";
			}
		}
		
		//删除
		List<PaperQuestionRule> chapterList = paperQuestionRuleDao.getChapterList(paperId, chapterId);
		for(PaperQuestionRule paperQuestionRule : chapterList){
			del(paperQuestionRule.getId());
		}
		
		//添加
		for (int i = 0; i < questionTypeIds.length; i++) {
			PaperQuestionRule paperQuestionRule = new PaperQuestionRule();
			paperQuestionRule.setPaperId(paperId);
			paperQuestionRule.setPaperQuestionId(chapterId);
			paperQuestionRule.setQuestionTypeId(questionTypeIds[i]);
			paperQuestionRule.setType(types[i]);
			paperQuestionRule.setDifficultys(difficultys[i]);
			paperQuestionRule.setAis(ais[i]);
			if (ValidateUtil.isValid(scoreOptions)) {
				paperQuestionRule.setScoreOptions(scoreOptions[i]);
			}
			paperQuestionRule.setScore(scores[i]);
			paperQuestionRule.setNum(nums[i]);
			paperQuestionRule.setNo(i + 1 );
			paperQuestionRule.setUpdateUserId(getCurUser().getId());
			paperQuestionRule.setUpdateTime(new Date());
			add(paperQuestionRule);
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
	
	@Override
	public void publishCheck(Paper paper){
		Map<Integer, List<Question>> questionListCache = new HashMap<>();// 试题分类下所有的试题条件
		List<PaperQuestionRule> paperRuleList = getChapterList(paper.getId(), null);// 随机章节规则
		Set<Integer> questionTypeIdSet = new HashSet<>();
		for (PaperQuestionRule rule : paperRuleList) {
			if (questionTypeIdSet.contains(rule.getQuestionTypeId())) {
				continue;
			}
			questionTypeIdSet.add(rule.getQuestionTypeId());
			List<Question> questionList =  getQuestionList(rule.getQuestionTypeId());
			questionListCache.put(rule.getQuestionTypeId(), questionList);
		}
	
		Map<PaperQuestionRuleEx, Integer> ruleExNumCache = new HashMap<>();
		for (Integer questionTypeId : questionListCache.keySet()) {
			for (Question question : questionListCache.get(questionTypeId)) {
				PaperQuestionRuleEx paperQuestionRuleEx = new PaperQuestionRuleEx(question.getType(), question.getDifficulty(), question.getAi());
				if (ruleExNumCache.get(paperQuestionRuleEx) == null) {
					ruleExNumCache.put(paperQuestionRuleEx, 1);
				} else {
					ruleExNumCache.put(paperQuestionRuleEx, ruleExNumCache.get(paperQuestionRuleEx) + 1); // 
				}
			}
		}
		
		for (PaperQuestionRule paperRule : paperRuleList) {
			for (Integer difficulty : paperRule.getDifficultyArr()) {
				for (Integer ai : paperRule.getAiArr()) {
					PaperQuestionRuleEx paperRuleEx = new PaperQuestionRuleEx(paperRule.getType(), difficulty, ai);
					if (ruleExNumCache.get(paperRuleEx) == null) {
						PaperQuestion paperQuestion = paperQuestionService.getEntity(paperRule.getPaperQuestionId());
						throw new MyException(String.format("%s章节下第%s个规则题数不足%s道", paperQuestion.getName(), paperRule.getNo(), paperRule.getNum()));
					}
					
					Integer num = ruleExNumCache.put(paperRuleEx, ruleExNumCache.get(paperRuleEx) - paperRule.getNum());
					if (num < 0) {
						PaperQuestion paperQuestion = paperQuestionService.getEntity(paperRule.getPaperQuestionId());
						throw new MyException(String.format("%s章节下第%s个规则题数不足%s道", paperQuestion.getName(), paperRule.getNo(), paperRule.getNum()));
					}
				}
			}
		}
	}
}