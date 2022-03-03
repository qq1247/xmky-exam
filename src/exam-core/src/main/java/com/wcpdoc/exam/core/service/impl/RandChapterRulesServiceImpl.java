package com.wcpdoc.exam.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.RandChapterRulesDao;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.entity.PaperType;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.entity.RandChapterRules;
import com.wcpdoc.exam.core.service.PaperQuestionService;
import com.wcpdoc.exam.core.service.PaperService;
import com.wcpdoc.exam.core.service.PaperTypeService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionTypeService;
import com.wcpdoc.exam.core.service.RandChapterRulesService;

/**
 * 随机章节服务层实现
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
@Service
public class RandChapterRulesServiceImpl extends BaseServiceImp<RandChapterRules> implements RandChapterRulesService {
	@Resource
	private RandChapterRulesDao randChapterRulesDao;
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
	@Resource(name = "randChapterRulesDaoImpl")
	public void setDao(BaseDao<RandChapterRules> dao) {
		super.dao = dao;
	}

	@Override
	public List<Map<String, Object>> randChapterRulesList(Integer paperId) {
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
			List<Map<String, Object>> randChapterRulesListMap = new ArrayList<>();
			List<RandChapterRules> randChapterRulesList = getChapterList(chapter.getPaperId(), chapter.getId());
			for (RandChapterRules randChapterRules : randChapterRulesList) {
				Map<String, Object> randChapterRulesMap = new HashMap<>();
				randChapterRulesMap.put("id", randChapterRules.getId());
				randChapterRulesMap.put("paperId", randChapterRules.getPaperId());
				randChapterRulesMap.put("questionTypeId", randChapterRules.getQuestionTypeId());
				randChapterRulesMap.put("questionTypeName", questionTypeService.getEntity(randChapterRules.getQuestionTypeId()).getName());
				randChapterRulesMap.put("type", randChapterRules.getType());
				randChapterRulesMap.put("difficulty", randChapterRules.getDifficulty().split(","));
				randChapterRulesMap.put("ai", randChapterRules.getAi().split(","));
				Integer[] scoreOptions = null;
				if (ValidateUtil.isValid(randChapterRules.getScoreOptions())) {
					String[] split = randChapterRules.getScoreOptions().split(",");
					scoreOptions = new Integer[split.length];
					for(int i = 0; i < split.length; i++ ){
						scoreOptions[i] = Integer.parseInt(split[i]);
					}
				} else {
					scoreOptions = new Integer[0];
				}
				randChapterRulesMap.put("scoreOptions", scoreOptions);
				randChapterRulesMap.put("totalNumber", randChapterRules.getTotalNumber());
				randChapterRulesMap.put("score", randChapterRules.getScore());
				randChapterRulesListMap.add(randChapterRulesMap);
			}
			
			singleResult.put("randChapterRulesList", randChapterRulesListMap);
			resultList.add(singleResult);
		}
		return resultList;
	}
	
	@Override
	public void addAndUpdate(RandChapterRules randChapterRules) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(randChapterRules.getPaperQuestionId())) {
			throw new MyException("参数错误：paperQuestionId");
		}
		if (!ValidateUtil.isValid(randChapterRules.getPaperId())) {
			throw new MyException("参数错误：paperId");
		}
		if (!ValidateUtil.isValid(randChapterRules.getQuestionTypeId())) {
			throw new MyException("参数错误：questionTypeId");
		}
		if (!ValidateUtil.isValid(randChapterRules.getType())) {
			throw new MyException("参数错误：type");
		}
		if (!ValidateUtil.isValid(randChapterRules.getTotalNumber())) {
			throw new MyException("参数错误：totalNumber");
		}
		if (!ValidateUtil.isValid(randChapterRules.getScore())) {
			throw new MyException("参数错误：score");
		}
		
		QuestionType questionType = questionTypeService.getEntity(randChapterRules.getQuestionTypeId());// 试题分类校验
		if(!questionTypeService.hasWriteAuth(questionType, getCurUser().getId())) {
			throw new MyException("无操作权限");
		}
		Paper paper = paperService.getEntity(randChapterRules.getPaperId());//试卷校验
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
		if (randChapterRules.getType() != 3 && randChapterRules.getType() != 5) {//除填空和阅读其他都是智能阅卷
			randChapterRules.setAi("1");
		}
		
		if (randChapterRules.getType() == 2) {
			randChapterRules.setScoreOptions("2");
		}
		
		randChapterRules.setUpdateUserId(getCurUser().getId());
		randChapterRules.setUpdateTime(new Date());
		List<RandChapterRules> randChapterRulesList = getChapterList(randChapterRules.getPaperId(), randChapterRules.getPaperQuestionId());
		randChapterRules.setNo(randChapterRulesList.size() + 1 );
		add(randChapterRules);
	}

	@Override
	public void updateAndUpdate(RandChapterRules randChapterRules) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(randChapterRules.getId())) {
			throw new MyException("参数错误：id");
		}
		if (!ValidateUtil.isValid(randChapterRules.getPaperQuestionId())) {
			throw new MyException("参数错误：paperQuestionId");
		}
		if (!ValidateUtil.isValid(randChapterRules.getPaperId())) {
			throw new MyException("参数错误：paperId");
		}
		if (!ValidateUtil.isValid(randChapterRules.getQuestionTypeId())) {
			throw new MyException("参数错误：questionTypeId");
		}
		if (!ValidateUtil.isValid(randChapterRules.getType())) {
			throw new MyException("参数错误：type");
		}
		if (!ValidateUtil.isValid(randChapterRules.getTotalNumber())) {
			throw new MyException("参数错误：totalNumber");
		}
		if (!ValidateUtil.isValid(randChapterRules.getScore())) {
			throw new MyException("参数错误：score");
		}
		
		QuestionType questionType = questionTypeService.getEntity(randChapterRules.getQuestionTypeId());// 试题分类校验
		if(!questionTypeService.hasWriteAuth(questionType, getCurUser().getId())) {
			throw new MyException("无操作权限");
		}
		
		Paper paper = paperService.getEntity(randChapterRules.getPaperId());//试卷校验
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
		if (randChapterRules.getType() != 3 && randChapterRules.getType() != 5) {//除填空和阅读其他都是智能阅卷
			randChapterRules.setAi("1");
		}
		
		RandChapterRules entity = getEntity(randChapterRules.getId());
		entity.setQuestionTypeId(randChapterRules.getQuestionTypeId());
		entity.setType(randChapterRules.getType());
		entity.setDifficulty(randChapterRules.getDifficulty());
		entity.setAi(randChapterRules.getAi());
		entity.setScoreOptions(randChapterRules.getScoreOptions());
		entity.setTotalNumber(randChapterRules.getTotalNumber());
		entity.setScore(randChapterRules.getScore());
		entity.setUpdateUserId(getCurUser().getId());
		entity.setUpdateTime(new Date());
		update(entity);
	}
	
	@Override
	public void delAndUpdate(Integer[] ids) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(ids)) {
			throw new MyException("参数错误：randChapterRulesIds");
		}
		
		RandChapterRules randChapterRules = null;
		for (Integer id : ids) {
			randChapterRules = getEntity(id);
			if (randChapterRules == null) {
				throw new MyException("参数错误：randChapterRulesId");
			}
			del(randChapterRules.getId());//删除随机章节
		}
		
		// 同级随机章节重新排序
		List<RandChapterRules> chapterList = getChapterList(randChapterRules.getPaperId(), randChapterRules.getPaperQuestionId());
		int maxNo = 1;
		for (RandChapterRules cur : chapterList) {
			cur.setNo(maxNo++);
			update(cur);
		}
	}

	@Override
	public List<RandChapterRules> getChapterList(Integer paperId, Integer paperQuestionId) {
		return randChapterRulesDao.getChapterList(paperId, paperQuestionId);
	}

	@Override
	public Map<Integer, List<Question>> checkRandChapterRules(Integer paperId) {
		Map<Integer, Integer> totalNumberMap = new HashMap<Integer, Integer>(); // 记录试题分类中试题总条数
		Map<String, Integer> randChapterMap = new HashMap<String, Integer>(); // 记录符合章节规则条数
		PageIn pageIn;
		List<RandChapterRules> chapterList = randChapterRulesDao.getChapterList(paperId, null);// 获取所有章节随机规则
		for (RandChapterRules randChapterRules : chapterList) {
			pageIn = new PageIn();
			pageIn.setCurPage(1);
			pageIn.setPageSize(1000);
			pageIn.addAttr("questionTypeId", randChapterRules.getQuestionTypeId());
			totalNumberMap.put(randChapterRules.getQuestionTypeId(), totalNumberMap.get(randChapterRules.getQuestionTypeId()) == null ? randChapterRules.getTotalNumber() : totalNumberMap.get(randChapterRules.getQuestionTypeId()) + randChapterRules.getTotalNumber());
			String randChapterMapKey = randChapterRules.getQuestionTypeId()+"_"+randChapterRules.getType()+"_"+randChapterRules.getDifficulty()+"_"+randChapterRules.getAi();
			randChapterMap.put(randChapterMapKey, randChapterMap.get(randChapterMapKey) == null ? randChapterRules.getTotalNumber() : randChapterMap.get(randChapterMapKey) + randChapterRules.getTotalNumber() );
		}
		
		Set<Integer> totalNumberKey = totalNumberMap.keySet();
		Map<Integer, List<Question>> questionTypeMap = new HashMap<Integer, List<Question>>(); // 记录试题分类下所有试题
		for(Integer questionTypeId : totalNumberKey){// 试题分类
			Integer total = totalNumberMap.get(questionTypeId);
			
			List<Question> questionList = questionService.getQuestionList(questionTypeId);// 获取试题分类下所有试题
			questionTypeMap.put(questionTypeId, questionList);
			
			if (questionList.size() < total) {
				throw new MyException(String.format("%s分类中试题数量总数不够", questionTypeService.getEntity(questionTypeId).getName()));
			}
			
			for (int i = 1; i <= total; i++) {// 试题总数需要大于随机题数条件
				List<Question> list = questionTypeMap.get(questionTypeId);
				Set<String> randChapterKeySet = randChapterMap.keySet();
				for (String randChapterKey : randChapterKeySet) {
					for(int j = 0; j< list.size(); j++){
						if (randChapterMap.get(randChapterKey) <= 0) {
							break;
						}
						
						String[] split = randChapterKey.split("_");// 章节规则 个数是固定的   0、ID, 1、TYPE, 2、DIFFICULTY, 3、AI
						Question question = list.get(j); // ID, TYPE, DIFFICULTY, AI split[0] 0 是ID 不做比较
						if ( split[1].equals(question.getType().toString()) && split[2].contains(question.getDifficulty().toString()) && split[3].contains(question.getAi().toString()) ) {
							randChapterMap.put(randChapterKey, randChapterMap.get(randChapterKey) - 1);
						}
					}
				}
			}
			
			for (String randChapterKey : randChapterMap.keySet()) {
				if ( randChapterMap.get(randChapterKey) > 0) {
					throw new MyException(String.format("%s分类中符合要求的数量不够", questionTypeService.getEntity(questionTypeId).getName()));
				}
			}
		}
		return questionTypeMap;
	}
}