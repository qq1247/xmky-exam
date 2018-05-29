package com.wcpdoc.exam.exam.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.exam.dao.QuestionDao;
import com.wcpdoc.exam.exam.entity.Question;
import com.wcpdoc.exam.exam.entity.QuestionType;
import com.wcpdoc.exam.exam.service.QuestionService;
import com.wcpdoc.exam.exam.service.QuestionTypeService;

/**
 * 试题服务层实现
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
@Service
public class QuestionServiceImpl extends BaseServiceImp<Question> implements QuestionService {
	@Resource
	private QuestionDao questionDao;
	@Resource
	private QuestionTypeService questionTypeService;

	@Override
	@Resource(name = "questionDaoImpl")
	public void setDao(BaseDao<Question> dao) {
		super.dao = dao;
	}

	@Override
	public List<Map<String, Object>> getQuestionTypeTreeList() {
		return questionTypeService.getTreeList();
	}

	@Override
	public QuestionType getQuestionType(Integer id) {
		return questionDao.getQuestionType(id);
	}

	@Override
	public void doQuestionTypeUpdate(Integer[] ids, Integer questionTypeId) {
		//校验数据有效性
		if(!ValidateUtil.isValid(ids)){
			throw new RuntimeException("无法获取参数：ids");
		}
		if(questionTypeId == null){
			throw new RuntimeException("无法获取参数：questionTypeId");
		}
		
		//完成设置试题
		for(Integer id : ids){
			Question question = questionDao.getEntity(id);
			if(question.getQuestionTypeId() == questionTypeId){
				continue;
			}
			
			question.setQuestionTypeId(questionTypeId);
			questionDao.update(question);
		}
	}

	@Override
	public List<Question> getList(Integer questionTypeId) {
		return questionDao.getList(questionTypeId);
	}

	@Override
	public void delAndUpdate(Integer[] ids) {
		//校验数据有效性
		if(!ValidateUtil.isValid(ids)){
			throw new RuntimeException("无法获取参数：ids");
		}
		
		//删除试题
		for(Integer id : ids){
			Question question = getEntity(id);
			question.setState(0);
			update(question);
		}
	}
}
