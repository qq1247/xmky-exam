package com.wcpdoc.exam.core.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.QuestionTypeOpenDao;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionTypeOpen;
import com.wcpdoc.exam.core.service.QuestionAnswerService;
import com.wcpdoc.exam.core.service.QuestionOptionService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionTypeOpenService;

/**
 * 题库开放服务层实现
 * 
 * v1.0 chenyun 2021-03-02 13:43:21
 */
@Service
public class QuestionTypeOpenServiceImpl extends BaseServiceImp<QuestionTypeOpen> implements QuestionTypeOpenService {
	@Resource
	private QuestionTypeOpenDao questionTypeOpenDao;
	@Resource
	private QuestionService questionService;
	@Resource
	private QuestionOptionService questionOptionService;
	@Resource
	private QuestionAnswerService questionAnswerService;

	@Override
	@Resource(name = "questionTypeOpenDaoImpl")
	public void setDao(BaseDao<QuestionTypeOpen> dao) {
		super.dao = dao;
	}
	
	@Override
	public void addEx(QuestionTypeOpen questionTypeOpen) {
		// 校验数据有效性
		List<QuestionTypeOpen> list = questionTypeOpenDao.getList(questionTypeOpen.getStartTime(), questionTypeOpen.getEndTime(), questionTypeOpen.getQuestionTypeId());
		if (list.size() != 0) {
			throw new MyException("该时间段已存在，请重新选择时间段");
		}
		
		List<Question> questionList = questionService.getList(questionTypeOpen.getQuestionTypeId());
		if (!ValidateUtil.isValid(questionList)) {
			throw new MyException("请先添加试题");
		}
		
		// 添加记录
		if (ValidateUtil.isValid(questionTypeOpen.getUserIds())) {
			questionTypeOpen.setUserIds(","+questionTypeOpen.getUserIds()+","+getCurUser().getId()+",");
		}else{
			questionTypeOpen.setUserIds(null);
		}
		if (ValidateUtil.isValid(questionTypeOpen.getOrgIds())) {
			questionTypeOpen.setOrgIds(","+questionTypeOpen.getOrgIds()+",");
		}else{
			questionTypeOpen.setOrgIds(null);
		}
		questionTypeOpen.setUpdateUserId(getCurUser().getId());
		questionTypeOpen.setUpdateTime(new Date());
		questionTypeOpen.setState(1);
		questionTypeOpenDao.add(questionTypeOpen);
	}
	
	@Override
	public void delEx(Integer id) {
		// 校验数据有效性
		QuestionTypeOpen entity = getEntity(id);
		entity.setState(2);
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		update(entity);
	}

	@Override
	public PageOut questionListpage(PageIn pageIn) {
		return questionService.getListpage(pageIn);
	}

	@Override
	public PageResultEx get(Integer questionId) {return null;
//		Question question = questionService.getEntity(questionId);
//		List<String> optionList = new ArrayList<>();
//		if (QuestionUtil.hasSingleChoice(question) || QuestionUtil.hasMultipleChoice(question)) {
//			List<QuestionOption> questionOptionList = questionOptionService.getList(question.getId());
//			for (QuestionOption questionOption : questionOptionList) {
//				optionList.add(questionOption.getOptions());
//			}
//		}
//		
//		List<QuestionAnswer> questionAnswerList = questionAnswerService.getList(question.getId());
//		List<Map<String, Object>> questionAnswerSplitList = new ArrayList<Map<String, Object>>();
//		if (QuestionUtil.hasMultipleChoice(question)) {
//			for(QuestionAnswer questionAnswer : questionAnswerList){
//				Map<String, Object> map = new HashMap<String, Object>();
//				String[] split = questionAnswer.getAnswer().split(",");
//				map.put("id", questionAnswer.getId());
//				map.put("answer", split);
//				map.put("score", questionAnswer.getScore());
//				map.put("questionId", questionAnswer.getQuestionId());
//				questionAnswerSplitList.add(map);
//			}
//		} else if  (QuestionUtil.hasFillBlank(question)) {
//			for(QuestionAnswer questionAnswer : questionAnswerList){
//				Map<String, Object> map = new HashMap<String, Object>();
//				String[] split = questionAnswer.getAnswer().split("\n");
//				map.put("id", questionAnswer.getId());
//				map.put("answer", split);
//				map.put("score", questionAnswer.getScore());
//				map.put("questionId", questionAnswer.getQuestionId());
//				questionAnswerSplitList.add(map);
//			}
//		} else if (QuestionUtil.hasQA(question) && QuestionUtil.hasSubjective(question)) {
//			for(QuestionAnswer questionAnswer : questionAnswerList){					
//				Map<String, Object> map = new HashMap<String, Object>();
//				String[] split = questionAnswer.getAnswer().split("\n");
//				map.put("id", questionAnswer.getId());
//				map.put("answer", split);
//				map.put("score", questionAnswer.getScore());
//				map.put("questionId", questionAnswer.getQuestionId());
//				questionAnswerSplitList.add(map);
//			}
//		} else {
//			for(QuestionAnswer questionAnswer : questionAnswerList){
//				Map<String, Object> map = new HashMap<String, Object>();
//				map.put("id", questionAnswer.getId());
//				map.put("answer", questionAnswer.getAnswer());
//				map.put("score", questionAnswer.getScore());
//				map.put("questionId", questionAnswer.getQuestionId());
//				questionAnswerSplitList.add(map);
//			}
//		}
//		
//		Integer[] markOptions = null;//new Integer[split.length];
//		if (ValidateUtil.isValid(question.getMarkOptions())) {
//			String[] split = question.getMarkOptions().split(",");
//			markOptions = new Integer[split.length];
//			for(int i = 0; i < split.length; i++ ){
//				markOptions[i] = Integer.parseInt(split[i]);
//			}
//		} else {
//			markOptions = new Integer[0];
//		}
//		
//		PageResultEx pageResult = PageResultEx.ok()
//				.addAttr("id", question.getId())
//				.addAttr("type", question.getType())
//				.addAttr("ai", question.getMarkType())
//				.addAttr("title", question.getTitle())
//				.addAttr("analysis", question.getAnalysis())
//				.addAttr("state", question.getState())
//				.addAttr("questionTypeId", question.getQuestionTypeId())
//				.addAttr("score", question.getScore())
//				.addAttr("markOptions", markOptions)
//				.addAttr("options", optionList.toArray(new String[optionList.size()]))
//				.addAttr("answers", questionAnswerSplitList);
//		return pageResult;
	}
}
