package com.wcpdoc.exam.core.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.ExamRuleDao;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.ExamRule;
import com.wcpdoc.exam.core.service.ExamQuestionService;
import com.wcpdoc.exam.core.service.ExamRuleService;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionTypeService;

/**
 * 考试随机规则服务层实现
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
@Service
public class ExamRuleServiceImpl extends BaseServiceImp<ExamRule> implements ExamRuleService {
	@Resource
	private ExamRuleDao examRuleDao;
	@Resource
	private QuestionTypeService questionTypeService;
	@Resource
	private QuestionService questionService;
	@Resource
	private ExamService examService;
	@Resource
	private ExamQuestionService examQuestionService;
	
	@Override
	@Resource(name = "examRuleDaoImpl")
	public void setDao(BaseDao<ExamRule> dao) {
		super.dao = dao;
	}

	@Override
	public void examRuleUpdate(Integer examId, Integer chapterId, Integer[] questionTypeIds, Integer[] types, String[] markTypes, String[] markTypOptions, Integer[] nums, BigDecimal[] scores) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(examId)) {
			throw new MyException("参数错误：examId");
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
		if (!ValidateUtil.isValid(markTypes)) {
			throw new MyException("参数错误：ais");
		}
		if (!ValidateUtil.isValid(nums)) {
			throw new MyException("参数错误：nums");
		}
		if (!ValidateUtil.isValid(scores)) {
			throw new MyException("参数错误：scores");
		}
		
		if (questionTypeIds.length == 1) {
			markTypes[0] = StringUtil.join(markTypes);// 规则只有一个的情况下按逗号分隔的难度被拆分成了数组
			if (ValidateUtil.isValid(markTypOptions)) {
				markTypOptions[0] = StringUtil.join(markTypOptions);
			}
		}
		
		Exam exam = examService.getEntity(examId);
		if (exam.getState() == 0) {
			throw new MyException("试卷已删除");
		}
		if (exam.getState() == 1) {
			throw new MyException("试卷已发布");
		}
		if(exam.getState() == 3){
			throw new MyException("已归档");
		}
		
//		PaperType
		
		if(exam.getMarkType() == 1){//判断智能试卷
			for (int i = 0; i < markTypes.length; i++) {
				if (markTypes[i].contains("2")) {
					throw new MyException("智能试卷不能包含非智能题");
				}
			}
		}
		
		for (int i = 0; i < types.length; i++) {
			if (types[i] != 3 && types[i] != 5) {//除填空和阅读其他都是智能阅卷
				markTypes[i] = "1";
			}
			
			if (types[i] == 2) {// 多选漏选的分必填
				if (!ValidateUtil.isValid(markTypOptions)) {
					markTypOptions = new String[1];
				}
				markTypOptions[i] = "1";
			}
		}
		
		//删除
		List<ExamRule> chapterList = examRuleDao.getList(chapterId);
		for(ExamRule examRule : chapterList){
			del(examRule.getId());
		}
		
		//添加
		for (int i = 0; i < questionTypeIds.length; i++) {
			ExamRule examRule = new ExamRule();
			examRule.setExamId(examId);
			examRule.setQuestionTypeId(questionTypeIds[i]);
			examRule.setType(types[i]);
			examRule.setMarkTypes(markTypes[i]);
			if (ValidateUtil.isValid(markTypOptions)) {
				examRule.setMarkOptions(markTypOptions[i]);
			}
			examRule.setScore(scores[i]);
			examRule.setNum(nums[i]);
			examRule.setNo(i + 1 );
			examRule.setUpdateUserId(getCurUser().getId());
			examRule.setUpdateTime(new Date());
			add(examRule);
		}
	}
	
	@Override
	public List<ExamRule> getList(Integer chapterId) {
		return examRuleDao.getList(chapterId);
	}
}