package com.wcpdoc.exam.exam.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.util.BigDecimalUtil;
import com.wcpdoc.exam.core.util.StringUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.exam.dao.PaperDao;
import com.wcpdoc.exam.exam.entity.Paper;
import com.wcpdoc.exam.exam.entity.PaperQuestion;
import com.wcpdoc.exam.exam.entity.PaperQuestionEx;
import com.wcpdoc.exam.exam.entity.PaperType;
import com.wcpdoc.exam.exam.entity.Question;
import com.wcpdoc.exam.exam.service.PaperQuestionService;
import com.wcpdoc.exam.exam.service.PaperService;
import com.wcpdoc.exam.exam.service.PaperTypeService;
import com.wcpdoc.exam.exam.service.QuestionService;
import com.wcpdoc.exam.exam.service.QuestionTypeService;

/**
 * 试卷服务层实现
 * 
 * v1.0 zhanghc 2017-05-25 16:34:59
 */
@Service
public class PaperServiceImpl extends BaseServiceImp<Paper> implements PaperService {
	@Resource
	private PaperDao paperDao;
	@Resource
	private PaperTypeService paperTypeService;
	@Resource
	private PaperQuestionService paperQuestionService;
	@Resource
	private QuestionService questionService;
	@Resource
	private QuestionTypeService questionTypeService;

	@Override
	@Resource(name = "paperDaoImpl")
	public void setDao(BaseDao<Paper> dao) {
		super.dao = dao;
	}

	@Override
	public List<Map<String, Object>> getPaperTypeTreeList() {
		return paperTypeService.getTreeList();
	}

	@Override
	public PaperType getPaperType(Integer id) {
		return paperDao.getPaperType(id);
	}

	@Override
	public void doPaperTypeUpdate(Integer[] ids, Integer paperTypeId) {
		//校验数据有效性
		if(!ValidateUtil.isValid(ids)){
			throw new RuntimeException("无法获取参数：ids");
		}
		if(paperTypeId == null){
			throw new RuntimeException("无法获取参数：paperTypeId");
		}
		
		//完成设置试卷
		for(Integer id : ids){
			Paper paper = paperDao.getEntity(id);
			if(paper.getPaperTypeId() == paperTypeId){
				continue;
			}
			
			paper.setPaperTypeId(paperTypeId);
			paperDao.update(paper);
		}
	}

	@Override
	public List<Map<String, Object>> getPaperCfgPaperTreeList(Integer id) {
		List<Map<String, Object>> list = paperDao.getPaperCfgPaperTreeList(id);
		for(Map<String, Object> map : list){
			if(map.get("NAME") == null){
				continue;
			}
			String name = StringUtil.delHTMLTag(map.get("NAME").toString());
			if(name.length() > 40){
				name = name.substring(0, 40) + "...";
			}
			name = "（" + map.get("NO") + "）" + name;
			map.put("NAME", name);
		}
		return list;
	}

	@Override
	public PaperQuestion getPaperQuestion(Integer paperQuestionId) {
		return paperQuestionService.getEntity(paperQuestionId);
	}

	@Override
	public void initRootPaperQuestion(Integer id, LoginUser user) {
		PaperQuestion paperQuestion = paperDao.getRootPaperQuestion(id);
		if(paperQuestion == null){
			paperQuestion = new PaperQuestion();
			paperQuestion.setName("试卷");
			paperQuestion.setParentId(0);
			paperQuestion.setParentSub("_1_");
			paperQuestion.setUpdateTime(new Date());
			paperQuestion.setUpdateUserId(user.getId());;
			paperQuestion.setType(1);
			paperQuestion.setNo(1);
			paperQuestion.setPaperId(id);
			paperQuestionService.save(paperQuestion);
		}
	}

	@Override
	public void doPaperCfgAdd(PaperQuestion paperQuestion, LoginUser user) {
		paperQuestion.setUpdateTime(new Date());
		paperQuestion.setUpdateUserId(user.getId());
		paperQuestion.setType(1);
		
		List<PaperQuestion> paperQuestionList = paperQuestionService.getList(paperQuestion.getParentId());
		paperQuestion.setNo(paperQuestionList.size() + 1);
		paperQuestionService.saveAndUpdate(paperQuestion);
	}

	@Override
	public void doPaperCfgEdit(PaperQuestion paperQuestion, LoginUser user) {
		PaperQuestion entity = paperQuestionService.getEntity(paperQuestion.getId());
		entity.setName(paperQuestion.getName());
		entity.setDescription(paperQuestion.getDescription());
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(user.getId());
		paperQuestionService.update(entity);
	}

	@Override
	public PageOut getPaperCfgListpage(PageIn pageIn) {
		return paperDao.getPaperCfgListpage(pageIn);
	}

	@Override
	public void doPaperCfgListAdd(Integer paperId, Integer parentPaperQuestionId, Integer[] questionIds, LoginUser user) {
		//校验数据有效性
		if(paperId == null){
			throw new RuntimeException("无法获取参数：paperId");
		}
		if(parentPaperQuestionId == null){
			throw new RuntimeException("无法获取参数：parentPaperQuestionId");
		}
		if(!ValidateUtil.isValid(questionIds)){
			throw new RuntimeException("无法获取参数：questionIds");
		}
		
		//添加试题
		List<PaperQuestion> paperQuestionList = paperQuestionService.getList(parentPaperQuestionId);
		int maxNo = paperQuestionList.size();
		for(Integer questionId : questionIds){
			PaperQuestion paperQuestion = new PaperQuestion();
			paperQuestion.setUpdateTime(new Date());
			paperQuestion.setUpdateUserId(user.getId());
			paperQuestion.setPaperId(paperId);
			paperQuestion.setParentId(parentPaperQuestionId);
			paperQuestion.setQuestionId(questionId);
			paperQuestion.setType(2);
			paperQuestion.setNo(++maxNo);
			paperQuestionService.saveAndUpdate(paperQuestion);
		}
	}

	@Override
	public void doPaperCfgDel(Integer paperQuestionId) {
		PaperQuestion paperQuestion = paperQuestionService.getEntity(paperQuestionId);
		if(paperQuestion.getPaperId().equals(0)){
			return;
		}
		
		//删除试卷试题
		doPaperQuestionDel2(paperQuestionId);
		
		//更新总分数
		updateTotalScore(paperQuestion.getPaperId());
	}
	
	public void doPaperQuestionDel2(Integer paperQuestionId) {
		List<PaperQuestion> list = paperQuestionService.getList(paperQuestionId);
		for(PaperQuestion paperQuestion : list){
			doPaperQuestionDel2(paperQuestion.getId());
		}
		
		paperQuestionService.del(paperQuestionId);
	}

	@Override
	public void doPaperCfgSort(Integer paperQuestionId, Integer no){
		PaperQuestion currentPaperQuestion = paperQuestionService.getEntity(paperQuestionId);
		List<PaperQuestion> paperQuestionList = paperQuestionService.getList(currentPaperQuestion.getParentId());
		if(no <= 0 || no > paperQuestionList.size()){
			throw new RuntimeException("参数非法：no");
		}
		if(currentPaperQuestion.getNo() == no){
			return;
		}
		
		Collections.sort(paperQuestionList, new Comparator<PaperQuestion>() {
			@Override
			public int compare(PaperQuestion o1, PaperQuestion o2) {
				return o1.getNo() - o2.getNo();
			}
		});
		
		paperQuestionList.remove(currentPaperQuestion);
		if(currentPaperQuestion.getNo() < no){
			paperQuestionList.add(no - 1, currentPaperQuestion);
		}else{
			paperQuestionList.add(no, currentPaperQuestion);
		}
		
		for(int i = 0; i < paperQuestionList.size(); i++){
			PaperQuestion paperQuestion = paperQuestionList.get(i);
			paperQuestion.setNo(i + 1);
			paperQuestionService.update(paperQuestion);
		}
	}

	@Override
	public List<PaperQuestion> getPaperQuestionList(Integer paperQuestionId) {
		return paperQuestionService.getList(paperQuestionId);
	}

	@Override
	public List<Map<String, Object>> getQuestionTypeTreeList() {
		return questionTypeService.getTreeList();
	}

	@Override
	public List<PaperQuestionEx> getPaperList(Integer id) {
		List<PaperQuestion> paperQuestionList = paperDao.getPaperQuestionList(id);
		List<PaperQuestionEx> paperQuestionExList = new ArrayList<PaperQuestionEx>();
		List<Question> questionList = paperDao.getQuestionList(id);
		
		Map<Integer, PaperQuestionEx> paperQuestionExMap = new HashMap<Integer, PaperQuestionEx>();
		Map<Integer, Question> questionMap = new HashMap<Integer, Question>();
		for(Question question : questionList){
			questionMap.put(question.getId(), question);
		}
		
		for(PaperQuestion paperQuestion : paperQuestionList){
			PaperQuestionEx paperQuestionEx = new PaperQuestionEx();
			BeanUtils.copyProperties(paperQuestion, paperQuestionEx);
			paperQuestionEx.setQuestion(questionMap.get(paperQuestionEx.getQuestionId()));
			paperQuestionExList.add(paperQuestionEx);
			paperQuestionExMap.put(paperQuestionEx.getId(), paperQuestionEx);
		}
		
		List<PaperQuestionEx> treeList = new ArrayList<PaperQuestionEx>();
		for(PaperQuestionEx paperQuestionEx : paperQuestionExList){
			if(paperQuestionEx.getParentId() == 0){
				treeList.add(paperQuestionEx);
			}else{
				PaperQuestionEx parentPaperQuestionEx = paperQuestionExMap.get(paperQuestionEx.getParentId());
				parentPaperQuestionEx.getSubList().add(paperQuestionEx);
			}
		}
		
		return treeList;
	}

	@Override
	public void doPaperCfgScoreUpdate(Integer[] paperQuestionIds, BigDecimal[] scores) {
		//校验数据有效性
		if(!ValidateUtil.isValid(paperQuestionIds)){
			throw new RuntimeException("无法获取参数：paperQuestionIds");
		}
		if(!ValidateUtil.isValid(scores)){
			throw new RuntimeException("无法获取参数：scores");
		}
		
		//更新试卷分数
		Integer paperId = null;
		for(int i = 0; i < paperQuestionIds.length; i++){
			PaperQuestion paperQuestion = paperQuestionService.getEntity(paperQuestionIds[i]);
			paperQuestion.setScore(scores[i]);
			paperQuestionService.update(paperQuestion);
			
			if(i == 0){
				paperId = paperQuestion.getPaperId();
			}
		}
		paperDao.flush();
		

		//更新试卷总分
		updateTotalScore(paperId);
	}

	private void updateTotalScore(Integer paperId) {
		BigDecimal totalScore = new BigDecimal(0);
		List<PaperQuestion> paperQuestionList = paperQuestionService.getPaperQuestionList(paperId);
		for(PaperQuestion paperQuestion : paperQuestionList){
			if(paperQuestion.getType() != 2){
				continue;
			}
			
			totalScore = new BigDecimal(BigDecimalUtil.add(totalScore.doubleValue(), paperQuestion.getScore().doubleValue()));
		}
		
		Paper paper = getEntity(paperId);
		paper.setTotleScore(totalScore);
		update(paper);
	}
	
	@Override
	public List<Question> getQuestionList(Integer paperId) {
		return paperDao.getQuestionList(paperId);
	}

	@Override
	public List<Paper> getList(Integer paperId) {
		return paperDao.getList(paperId);
	}

	@Override
	public void delAndUpdate(Integer[] ids) {
		//校验数据有效性
		if(!ValidateUtil.isValid(ids)){
			throw new RuntimeException("无法获取参数：ids");
		}
		
		//删除试题
		for(Integer id : ids){
			Paper paper = getEntity(id);
			paper.setState(0);
			update(paper);
		}
	}
}
