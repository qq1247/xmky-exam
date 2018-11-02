package com.wcpdoc.exam.exam.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.exam.dao.PaperQuestionDao;
import com.wcpdoc.exam.exam.entity.PaperQuestion;
import com.wcpdoc.exam.exam.service.PaperQuestionService;

/**
 * 试卷试题服务层实现
 * 
 * v1.0 zhanghc 2017-05-26 14:23:38
 */
@Service
@Deprecated
public class PaperQuestionServiceImpl extends BaseServiceImp<PaperQuestion> implements PaperQuestionService {
	@Resource
	private PaperQuestionDao paperQuestionDao;

	@Override
	@Resource(name = "paperQuestionDaoImpl")
	public void setDao(BaseDao<PaperQuestion> dao) {
		super.dao = dao;
	}
	
	@Override
	public void saveAndUpdate(PaperQuestion paperQuestion) {
//		//校验数据有效性
//		if(paperQuestion.getType() == null){
//			throw new RuntimeException("无法获取参数：paperQuestion.type");
//		}
//		if(paperQuestion.getType() != 1 && paperQuestion.getType() != 2){//1：章节；2：试题
//			throw new RuntimeException("无法获取参数：paperQuestion.type");
//		}
//		
//		if(paperQuestion.getType() == 1 && !ValidateUtil.isValid(paperQuestion.getName())){
//			throw new RuntimeException("无法获取参数：paperQuestion.name");
//		}
//		if(paperQuestion.getPaperId() == null){
//			throw new RuntimeException("无法获取参数：paperQuestion.paperId");
//		}
//		if(paperQuestion.getType() == 2 && paperQuestion.getQuestionId() == null){
//			throw new RuntimeException("无法获取参数：paperQuestion.questionId");
//		}
////		if(paperQuestion.getType() == 1 && existName(paperQuestion)){
////			throw new RuntimeException("名称已存在！");
////		}
//				
//		//保存试卷试题
//		if(paperQuestion.getParentId() == null){
//			paperQuestion.setParentId(0);
//		}
//		paperQuestionDao.save(paperQuestion);
//		
//		//更新父子关系
//		PaperQuestion parentPaperQuestion = paperQuestionDao.getEntity(paperQuestion.getParentId());
//		if(parentPaperQuestion == null){
//			paperQuestion.setParentSub("_" + paperQuestion.getId() + "_");
//		}else {
//			paperQuestion.setParentSub(parentPaperQuestion.getParentSub() + paperQuestion.getId() + "_");
//		}
//		paperQuestionDao.update(paperQuestion);
	}
	
	/**
	 * 名称是否已存在
	 * 
	 * v1.0 zhanghc 2017年5月27日上午9:47:05
	 * @param paperQuestion
	 * @return boolean
	 */
//	private boolean existName(PaperQuestion paperQuestion){
//		//校验数据有效性
//		if(paperQuestion == null){
//			throw new RuntimeException("无法获取参数：paperQuestion");
//		}
//		if(!ValidateUtil.isValid(paperQuestion.getName())){
//			throw new RuntimeException("无法获取参数：paperQuestion.name");
//		}
//		
//		//如果是添加
//		PaperQuestion paperQuestion2 = paperQuestionDao.getPaperQuestionByName(paperQuestion.getName());
//		if(paperQuestion2 != null){
//			paperQuestionDao.evict(paperQuestion2);
//		}
//		
//		if(paperQuestion.getId() == null){
//			if(paperQuestion2 != null){
//				return true;
//			}
//			return false;
//		}
//		
//		//如果是修改
//		if(paperQuestion2 != null && !paperQuestion.getId().equals(paperQuestion2.getId())){
//			return true;
//		}
//		return false;
//	}

	@Override
	public List<PaperQuestion> getList(Integer parentId) {
		return paperQuestionDao.getList(parentId);
	}

	@Override
	public PaperQuestion getTopPaperQuestion(Integer paperId) {
		return paperQuestionDao.getTopPaperQuestion(paperId);
	}

	@Override
	public List<PaperQuestion> getPaperQuestionList(Integer paperId) {
		return paperQuestionDao.getPaperQuestionList(paperId);
	}

	@Override
	public PaperQuestion getEntity(Integer paperId, Integer questionId) {
		return paperQuestionDao.getEntity(paperId, questionId);
	}
}
