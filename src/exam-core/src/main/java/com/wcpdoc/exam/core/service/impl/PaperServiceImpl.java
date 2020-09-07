package com.wcpdoc.exam.core.service.impl;

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

import com.wcpdoc.exam.base.entity.Org;
import com.wcpdoc.exam.base.entity.Post;
import com.wcpdoc.exam.base.service.UserService;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.dao.PaperDao;
import com.wcpdoc.exam.core.dao.PaperQuestionDao;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.entity.PaperQuestionEx;
import com.wcpdoc.exam.core.entity.PaperType;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.service.PaperService;
import com.wcpdoc.exam.core.service.PaperTypeService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionTypeService;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.util.BigDecimalUtil;
import com.wcpdoc.exam.core.util.StringUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;

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
	private PaperQuestionDao paperQuestionDao;
	@Resource
	private PaperTypeService paperTypeService;
	@Resource
	private QuestionService questionService;
	@Resource
	private QuestionTypeService questionTypeService;
	@Resource
	private UserService userService;

	@Override
	@Resource(name = "paperDaoImpl")
	public void setDao(BaseDao<Paper> dao) {
		super.dao = dao;
	}

	@Override
	public List<Map<String, Object>> getPaperTypeTreeList(Integer userId) {
		Org org = userService.getOrg(userId);
		List<Post> postList = userService.getPostList(userId);
		List<PaperType> paperTypeList = paperTypeService.getList();
		List<Map<String, Object>> paperTypeTreeList = new ArrayList<Map<String,Object>>();
		
		for(PaperType paperType : paperTypeList){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("ID", paperType.getId());
			map.put("NAME", paperType.getName());
			map.put("PARENT_ID", paperType.getParentId());
			//map.put("DISABLED", true);
			//map.put("EXPANDED", true);
			
			if(userId == 1){
				paperTypeTreeList.add(map);
				continue;
			}
			
			if(paperType.getUserIds() != null 
					&& paperType.getUserIds().contains(userId.toString())){//有用户权限
				paperTypeTreeList.add(map);
				continue;
			}
			if(paperType.getOrgIds() != null 
					&& paperType.getOrgIds().contains(org.getId().toString())){//有机构权限
				paperTypeTreeList.add(map);
				continue;
			}
			
			for(Post post : postList){
				if(paperType.getPostIds() != null 
						&& paperType.getPostIds().contains(post.getId().toString())){//有岗位权限
					paperTypeTreeList.add(map);
					break;
				}
			}
		}
		
		return paperTypeTreeList;
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
		return paperQuestionDao.getEntity(paperQuestionId);
	}

	@Override
	public void initRootPaperQuestion(Integer id, LoginUser user) {
//		PaperQuestion paperQuestion = paperDao.getRootPaperQuestion(id);
//		if(paperQuestion == null){
//			paperQuestion = new PaperQuestion();
//			paperQuestion.setName("试卷");
//			paperQuestion.setParentId(0);
//			paperQuestion.setParentSub("_1_");
//			paperQuestion.setUpdateTime(new Date());
//			paperQuestion.setUpdateUserId(user.getId());;
//			paperQuestion.setType(1);
//			paperQuestion.setNo(1);
//			paperQuestion.setPaperId(id);
//			paperQuestionService.add(paperQuestion);
//		}
	}

	@Override
	public void doChapterAdd(PaperQuestion chapter, LoginUser user) {
		//校验数据有效性
		if(chapter.getPaperId() == null){
			throw new RuntimeException("无法获取参数：paperId");
		}
				
		//添加章节
		chapter.setUpdateTime(new Date());
		chapter.setUpdateUserId(user.getId());
		chapter.setType(1);
		chapter.setParentId(0);
		
		List<PaperQuestion> paperQuestionList = paperQuestionDao.getList(chapter.getParentId());
		chapter.setNo(paperQuestionList.size() + 1);
		
		paperQuestionDao.add(chapter);
		
		//更新父子关系
		chapter.setParentSub("_" + chapter.getId() + "_");
		paperQuestionDao.update(chapter);
	}

	@Override
	public void doChapterEdit(PaperQuestion chapter, LoginUser user) {
		PaperQuestion c = paperQuestionDao.getEntity(chapter.getId());
		c.setName(chapter.getName());
		c.setDescription(chapter.getDescription());
		c.setUpdateTime(new Date());
		c.setUpdateUserId(user.getId());
		paperQuestionDao.update(c);
	}

	@Override
	public PageOut getQuestionListpage(PageIn pageIn) {
		return paperDao.getQuestionListpage(pageIn);
	}

	@Override
	public void doQuestionAdd(Integer chapterId, Integer[] questionIds, LoginUser user){
		//校验数据有效性
		if(chapterId == null){
			throw new RuntimeException("无法获取参数：chapterId");
		}
		if(!ValidateUtil.isValid(questionIds)){
			throw new RuntimeException("无法获取参数：questionIds");
		}
		
		//添加试题
		PaperQuestion chapter = paperQuestionDao.getEntity(chapterId);
		List<PaperQuestion> questionList = paperQuestionDao.getList(chapterId);
		int maxNo = questionList.size();
		for(Integer questionId : questionIds){
			PaperQuestion pq = new PaperQuestion();
			pq.setUpdateTime(new Date());
			pq.setUpdateUserId(user.getId());
			pq.setPaperId(chapter.getPaperId());
			pq.setParentId(chapterId);
			pq.setQuestionId(questionId);
			pq.setType(2);
			pq.setNo(++maxNo);
			pq.setScore(BigDecimal.ZERO);
			paperQuestionDao.add(pq);
		}
	}

	@Override
	public void doChapterDel(Integer chapterId) {
		//删除章节
		List<PaperQuestion> questionList = paperQuestionDao.getList(chapterId);
		for(PaperQuestion pq : questionList){
			paperQuestionDao.del(pq.getId());
		}
		PaperQuestion chapter = paperQuestionDao.getEntity(chapterId);//不要放到下一行，因为第二行执行删除了。
		paperQuestionDao.del(chapterId);
		
		//更新总分数
		updateTotalScore(chapter.getPaperId());
	}

	@Override
	public List<PaperQuestion> getPaperQuestionList(Integer paperQuestionId) {
		return paperQuestionDao.getList(paperQuestionId);
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
	public void doScoresUpdate(Integer chapterId, BigDecimal score, String options) {
		//校验数据有效性
		if(chapterId == null){
			throw new RuntimeException("无法获取参数：chapterId");
		}
		if(score == null){
			throw new RuntimeException("无法获取参数：score");
		}
		
		//更新试卷分数
		List<PaperQuestion> pqList = paperQuestionDao.getList(chapterId);
		for(PaperQuestion pq : pqList){
			Question question = questionService.getEntity(pq.getQuestionId());
			if(question.getType() == 2){
				if(ValidateUtil.isValid(options) && options.contains("1")){
					pq.setOptions("1");
				}else{
					pq.setOptions(null);
				}
			}else if(question.getType() == 3){
				pq.setOptions(options);
			}else{
				pq.setOptions(null);
			}
			
			pq.setScore(score);
			paperQuestionDao.update(pq);
		}
		
		//更新试卷总分
		if(pqList.size() > 0){
			updateTotalScore(pqList.get(0).getPaperId());
		}
	}
	
	@Override
	public void doScoreUpdate(Integer paperQuestionId, BigDecimal score, String options) {
		//校验数据有效性
		if(paperQuestionId == null){
			throw new RuntimeException("无法获取参数：paperQuestionId");
		}
		if(score == null){
			throw new RuntimeException("无法获取参数：score");
		}
		
		PaperQuestion pq = paperQuestionDao.getEntity(paperQuestionId);
		Question question = questionService.getEntity(pq.getQuestionId());
		if(question.getType() == 2){
			if(ValidateUtil.isValid(options) && options.contains("1")){
				pq.setOptions("1");
			}else{
				pq.setOptions(null);
			}
		}else if(question.getType() == 3){
			pq.setOptions(options);
		}else{
			pq.setOptions(null);
		}
		
		pq.setScore(score);
		paperQuestionDao.update(pq);
		
		//更新试卷总分
		updateTotalScore(pq.getPaperId());
	}

	private void updateTotalScore(Integer paperId) {
		BigDecimal totalScore = new BigDecimal(0);
		List<PaperQuestion> paperQuestionList = paperQuestionDao.getPaperQuestionList(paperId);
		for(PaperQuestion paperQuestion : paperQuestionList){
			if(paperQuestion.getType() != 2){
				continue;
			}
			
			totalScore = new BigDecimal(BigDecimalUtil.add(totalScore.toString(), paperQuestion.getScore().toString()));
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

	@Override
	public PaperType getPaperType2(Integer paperTypeId) {
		return paperTypeService.getEntity(paperTypeId);
	}

	@Override
	public List<Map<String, Object>> getQuestionTypeTreeList(Integer userId) {
		return questionService.getQuestionTypeTreeList(userId);
	}

	@Override
	public void doQuestionClear(Integer chapterId, LoginUser currentUser) {
		//校验数据有效性
		if(chapterId == null){
			throw new RuntimeException("无法获取参数：chapterId");
		}
		
		List<PaperQuestion> pqList = paperQuestionDao.getList(chapterId);
		for(PaperQuestion pq : pqList){
			paperQuestionDao.del(pq.getId());
		}
	}
	
	@Override
	public void doChapterUp(Integer chapterId){
		//校验数据有效性
		if(chapterId == null){
			throw new RuntimeException("无法获取参数：chapterId");
		}
		
		//当前章节上移
		PaperQuestion chapter = paperQuestionDao.getEntity(chapterId);
		List<PaperQuestion> chapterList = paperQuestionDao.getChapterList(chapter.getPaperId());
		Collections.sort(chapterList, new Comparator<PaperQuestion>() {
			@Override
			public int compare(PaperQuestion o1, PaperQuestion o2) {
				return o2.getNo() - o1.getNo();
			}
		});
		
		for(PaperQuestion cur : chapterList){
			if(chapter.getNo() > cur.getNo()){
				Integer no = cur.getNo();
				cur.setNo(chapter.getNo());
				chapter.setNo(no);
				paperQuestionDao.update(cur);
				paperQuestionDao.update(chapter);
				break;
			}
		}
	}

	@Override
	public void doChapterDown(Integer chapterId) {
		//校验数据有效性
		if(chapterId == null){
			throw new RuntimeException("无法获取参数：chapterId");
		}
		
		//当前章节下移
		PaperQuestion chapter = paperQuestionDao.getEntity(chapterId);
		List<PaperQuestion> chapterList = paperQuestionDao.getChapterList(chapter.getPaperId());
		Collections.sort(chapterList, new Comparator<PaperQuestion>() {
			@Override
			public int compare(PaperQuestion o1, PaperQuestion o2) {
				return o1.getNo() - o2.getNo();
			}
		});
		
		for(PaperQuestion cur : chapterList){
			if(chapter.getNo() < cur.getNo()){
				Integer no = cur.getNo();
				cur.setNo(chapter.getNo());
				chapter.setNo(no);
				paperQuestionDao.update(cur);
				paperQuestionDao.update(chapter);
				break;
			}
		}
	}

	@Override
	public void doQuestionUp(Integer paperQuestionId) {
		//校验数据有效性
		if(paperQuestionId == null){
			throw new RuntimeException("无法获取参数：paperQuestionId");
		}
		
		//当前试题上移
		PaperQuestion pq = paperQuestionDao.getEntity(paperQuestionId);
		List<PaperQuestion> pqList = paperQuestionDao.getList(pq.getParentId());
		
		Collections.sort(pqList, new Comparator<PaperQuestion>() {
			@Override
			public int compare(PaperQuestion o1, PaperQuestion o2) {
				return o2.getNo() - o1.getNo();
			}
		});
		
		for(PaperQuestion cur : pqList){
			if(pq.getNo() > cur.getNo()){
				Integer no = cur.getNo();
				cur.setNo(pq.getNo());
				pq.setNo(no);
				paperQuestionDao.update(cur);
				paperQuestionDao.update(pq);
				break;
			}
		}
	}

	@Override
	public void doQuestionDown(Integer paperQuestionId) {
		//校验数据有效性
		if(paperQuestionId == null){
			throw new RuntimeException("无法获取参数：paperQuestionId");
		}
		
		//当前试题下移
		PaperQuestion pq = paperQuestionDao.getEntity(paperQuestionId);
		List<PaperQuestion> pqList = paperQuestionDao.getList(pq.getParentId());
		
		Collections.sort(pqList, new Comparator<PaperQuestion>() {
			@Override
			public int compare(PaperQuestion o1, PaperQuestion o2) {
				return o1.getNo() - o2.getNo();
			}
		});
		
		for(PaperQuestion cur : pqList){
			if(pq.getNo() < cur.getNo()){
				Integer no = cur.getNo();
				cur.setNo(pq.getNo());
				pq.setNo(no);
				paperQuestionDao.update(cur);
				paperQuestionDao.update(pq);
				break;
			}
		}
	}

	@Override
	public void doQuestionDel(Integer paperQuestionId) {
		paperQuestionDao.del(paperQuestionId);
	}

	@Override
	public PaperQuestion getPaperQuestion(Integer paperId, Integer questionId) {
		return paperQuestionDao.getEntity(paperId, questionId);
	}
}
