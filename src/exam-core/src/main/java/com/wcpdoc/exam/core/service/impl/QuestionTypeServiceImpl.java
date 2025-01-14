package com.wcpdoc.exam.core.service.impl;

import javax.annotation.Resource;

import com.wcpdoc.exam.core.dao.QuestionDao;
import com.wcpdoc.exam.core.entity.Question;
import org.springframework.stereotype.Service;

import com.wcpdoc.base.util.CurLoginUserUtil;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.dao.QuestionTypeDao;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.service.QuestionTypeExService;
import com.wcpdoc.exam.core.service.QuestionTypeService;

import java.util.List;

/**
 * 题库服务层实现
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
@Service
public class QuestionTypeServiceImpl extends BaseServiceImp<QuestionType> implements QuestionTypeService {
	@Resource
	private QuestionTypeDao questionTypeDao;
	@Resource
	private QuestionTypeExService questionTypeExService;
	@Resource
	private QuestionDao questionDao;

	@Override
	public RBaseDao<QuestionType> getDao() {
		return questionTypeDao;
	}

	@Override
	public void delEx(Integer id) {
		// 数据校验
		QuestionType entity = getById(id);
		if (!(CurLoginUserUtil.isSelf(entity.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {
			throw new MyException("无操作权限");
		}
		// 题库删除
		// entity.setUpdateTime(new Date());// 物理删除，不需要再记录
		// entity.setUpdateUserId(getCurUser().getId());
		removeById(entity.getId());

		// 题库扩展删除
		questionTypeExService.delEx(entity);
	}

	@Override
	public void move(Integer sourceId, Integer targetId) {
		questionTypeExService.move(sourceId, targetId);
	}

	@Override
	public void clear(Integer id) {
		questionTypeExService.clear(id);
	}

	@Override
	public void computeTypeNum(Integer id) {
		QuestionType questionType = getById(id);
		List<Question> list = questionDao.getList(id);
		if(list != null && list.size() > 0){
			Integer objectiveNum = 0;
			Integer subjectiveNum = 0;
			Integer singleNum = 0;
			Integer multipleNum = 0;
			Integer blankNum = 0;
			Integer judgeNum = 0;
			Integer shortAnswerNum = 0;
			Integer questionNum = list.size();

			for(Question question: list){
				switch (question.getType()){
					case 1: singleNum += 1;
					        break;
					case 2: multipleNum += 1;
						break;
					case 3: blankNum += 1;
						break;
					case 4: judgeNum += 1;
						break;
					case 5: shortAnswerNum += 1;
						break;
				}

				switch (question.getMarkType()){
					case 1: objectiveNum += 1;
						break;
					case 2: subjectiveNum += 1;
						break;
				}
			}
			questionType.setObjectiveNum(objectiveNum);
			questionType.setSubjectiveNum(subjectiveNum);
			questionType.setSingleNum(singleNum);
			questionType.setMultipleNum(multipleNum);
			questionType.setFillBlankNum(blankNum);
			questionType.setJudgeNum(judgeNum);
			questionType.setQaNum(shortAnswerNum);
			questionType.setQuestionNum(questionNum);

			updateById(questionType);
		}
	}
}
