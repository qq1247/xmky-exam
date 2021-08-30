package com.wcpdoc.exam.core.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

import com.wcpdoc.exam.base.cache.DictCache;
import com.wcpdoc.exam.base.service.UserService;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.dao.PaperDao;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.entity.PaperQuestionAnswer;
import com.wcpdoc.exam.core.entity.PaperQuestionEx;
import com.wcpdoc.exam.core.entity.PaperRemark;
import com.wcpdoc.exam.core.entity.PaperType;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.PaperQuestionAnswerService;
import com.wcpdoc.exam.core.service.PaperQuestionService;
import com.wcpdoc.exam.core.service.PaperRemarkService;
import com.wcpdoc.exam.core.service.PaperService;
import com.wcpdoc.exam.core.service.PaperTypeService;
import com.wcpdoc.exam.core.service.QuestionAnswerService;
import com.wcpdoc.exam.core.service.QuestionOptionService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionTypeService;
import com.wcpdoc.exam.core.util.BigDecimalUtil;
import com.wcpdoc.exam.core.util.StringUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 试卷服务层实现
 * 
 * zhanghc 2018年10月21日上午8:16:06
 */
@Service
public class PaperServiceImpl extends BaseServiceImp<Paper> implements PaperService {
	@Resource
	private PaperDao paperDao;
	@Resource
	private PaperQuestionService paperQuestionService;
	@Resource
	private QuestionService questionService;
	@Resource
	private QuestionAnswerService questionAnswerService;
	@Resource
	private PaperQuestionAnswerService paperQuestionAnswerService;
	@Resource
	private PaperTypeService paperTypeService;
	@Resource
	private UserService userService;
	@Resource
	private QuestionTypeService questionTypeService;
	@Resource
	private PaperRemarkService paperRemarkService;
	@Resource
	private QuestionOptionService questionOptionService;

	@Override
	@Resource(name = "paperDaoImpl")
	public void setDao(BaseDao<Paper> dao) {
		super.dao = dao;
	}

	@Override
	public void chapterAdd(PaperQuestion chapter) {
		//校验数据有效性
		if(chapter.getPaperId() == null) {
			throw new MyException("参数错误：paperId");
		}
		if(!ValidateUtil.isValid(chapter.getName())) {
			throw new MyException("参数错误：name");
		}
		Paper paper = getEntity(chapter.getPaperId());
		if (paper.getState() == 0) {
			throw new MyException("试卷已删除");
		}
		if (paper.getState() == 1) {
			throw new MyException("试卷已发布");
		}
		if(!hasWriteAuth(paper.getPaperTypeId(), getCurUser().getId())) {
			throw new MyException("权限不足！");
		}
		
		//添加章节
		chapter.setUpdateTime(new Date());
		chapter.setUpdateUserId(getCurUser().getId());
		chapter.setType(1);
		chapter.setParentId(0);
		
		List<PaperQuestion> paperQuestionList = paperQuestionService.getChapterList(chapter.getPaperId());
		chapter.setNo(paperQuestionList.size() + 1);
		
		paperQuestionService.add(chapter);
		
		//更新父子关系
		chapter.setParentSub("_" + chapter.getId() + "_");
		paperQuestionService.update(chapter);
	}

	@Override
	public void updateAndUpdate(Paper paper) {
		Paper entity = paperDao.getEntity(paper.getId());
		if(entity.getState() == 1){
			throw new MyException("已发布的不能修改！");
		}
		if(!hasWriteAuth(entity.getPaperTypeId(), getCurUser().getId())) {
			throw new MyException("权限不足！");
		}
		
		entity.setName(paper.getName());
		entity.setPassScore(paper.getPassScore());
		entity.setReadRemark(paper.getReadRemark());
		entity.setReadNum(paper.getReadNum());
		entity.setShowType(paper.getShowType());
		entity.setMinimizeNum(paper.getMinimizeNum());
		entity.setUpdateUserId(getCurUser().getId());
		entity.setUpdateTime(new Date());
		paperDao.update(entity);

		/*paperRemarkService.remove(entity.getId());//重新添加评语
		for (int i = 0; i < paperRemark.size(); i++) {
			paperRemark.get(i).setNo(i+1);
			paperRemark.get(i).setPaperId(entity.getId());
			paperRemarkService.add(paperRemark.get(i));
		}*/
	}

	@Override
	public void delAndUpdate(Integer id) {
		Paper paper = paperDao.getEntity(id);
		if(!hasWriteAuth(paper.getPaperTypeId(), getCurUser().getId())) {
			throw new MyException("权限不足！");
		}
		paper.setState(0);
		paper.setUpdateTime(new Date());
		paper.setUpdateUserId(getCurUser().getId());
		paperDao.update(paper);
	}
	
	@Override
	public void chapterEdit(PaperQuestion chapter) {
		//校验数据有效性
		PaperQuestion entity = paperQuestionService.getEntity(chapter.getId());
		if(entity == null){
			throw new MyException("章节无效！");
		}
		Paper paper = getEntity(entity.getPaperId());
		if (paper.getState() == 0) {
			throw new MyException("试卷已删除");
		}
		if (paper.getState() == 1) {
			throw new MyException("试卷已发布");
		}
		if(!hasWriteAuth(paper.getPaperTypeId(), getCurUser().getId())) {
			throw new MyException("权限不足！");
		}
		
		// 修改章节
		entity.setName(chapter.getName());
		entity.setDescription(chapter.getDescription());
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		paperQuestionService.update(entity);
	}
	
	@Override
	public void chapterDel(Integer id) {
		//校验数据有效性
		PaperQuestion entity = paperQuestionService.getEntity(id);
		Paper paper = getEntity(entity.getPaperId());
		if (paper.getState() == 0) {
			throw new MyException("试卷已删除");
		}
		if (paper.getState() == 1) {
			throw new MyException("试卷已发布");
		}
				
		//删除章节
		List<PaperQuestion> questionList = paperQuestionService.getQuestionList(id);
		for(PaperQuestion pq : questionList) {
			paperQuestionService.del(pq.getId());
		}
		//PaperQuestion chapter = paperQuestionService.getEntity(id);//不要放到下一行，因为第二行执行删除了。
		paperQuestionService.del(id);
	}
	
	@Override
	public void movePosition(Integer sourceId, Integer targetId) {
		//校验数据有效性
		if(sourceId == null || targetId == null ){
			throw new MyException("参数错误！");
		}
		
		PaperQuestion sourceEntity = paperQuestionService.getEntity(sourceId);
		PaperQuestion targetEntity = paperQuestionService.getEntity(targetId);
		Paper paper = getEntity(sourceEntity.getPaperId());
		if (paper.getState() == 0) {
			throw new MyException("试卷已删除");
		}
		if (paper.getState() == 1) {
			throw new MyException("试卷已发布");
		}
		
		if (sourceEntity.getType() != targetEntity.getType()) {
			sourceEntity.setParentId(targetEntity.getId());
			sourceEntity.setParentSub(targetEntity.getParentSub()+sourceEntity.getId()+"_");
			sourceEntity.setNo(1);
			paperQuestionService.update(sourceEntity);
			return;
		}
		if(sourceEntity.getParentId().intValue() != targetEntity.getParentId().intValue()){
			PaperQuestion sourceParent = paperQuestionService.getEntity(sourceEntity.getParentId());
			PaperQuestion targetParent = paperQuestionService.getEntity(targetEntity.getParentId());
			Integer parentId = sourceEntity.getParentId();
			sourceEntity.setParentId(targetEntity.getParentId());
			sourceEntity.setParentSub(targetParent.getParentSub()+sourceEntity.getId()+"_");
			targetEntity.setParentId(parentId);
			targetEntity.setParentSub(sourceParent.getParentSub()+targetEntity.getId()+"_");
		}
		
		Integer no = sourceEntity.getNo();
		sourceEntity.setNo(targetEntity.getNo());
		targetEntity.setNo(no);
		paperQuestionService.update(sourceEntity);
		paperQuestionService.update(targetEntity);
		//当前章节上移
		/*PaperQuestion chapter = paperQuestionService.getEntity(chapterId);
		List<PaperQuestion> chapterList = paperQuestionService.getChapterList(chapter.getPaperId());
		Collections.sort(chapterList, new Comparator<PaperQuestion>() {
			@Override
			public int compare(PaperQuestion o1, PaperQuestion o2) {
				return o2.getNo() - o1.getNo();
			}
		});
		
		for(PaperQuestion cur : chapterList) {
			if(chapter.getNo() > cur.getNo()) {
				Integer no = cur.getNo();
				cur.setNo(chapter.getNo());
				chapter.setNo(no);
				paperQuestionService.update(cur);
				paperQuestionService.update(chapter);
				break;
			}
		}*/
	}
	
	@Override
	public List<PaperQuestionEx> getPaperList(Integer id) {
		List<PaperQuestion> paperQuestionList = paperDao.getPaperQuestionList(id);
		List<PaperQuestionEx> paperQuestionExList = new ArrayList<PaperQuestionEx>();
		List<Question> questionList = paperDao.getQuestionList(id);
		
		Map<Integer, PaperQuestionEx> paperQuestionExMap = new HashMap<Integer, PaperQuestionEx>();
		Map<Integer, Question> questionMap = new HashMap<Integer, Question>();
		for(Question question : questionList) {
			questionMap.put(question.getId(), question);
		}
		
		for(PaperQuestion paperQuestion : paperQuestionList) {
			PaperQuestionEx paperQuestionEx = new PaperQuestionEx();
			try {
				BeanUtils.copyProperties(paperQuestionEx, paperQuestion);
			} catch (Exception e) {
				new MyException(e);
			}
			paperQuestionEx.setQuestion(questionMap.get(paperQuestionEx.getQuestionId()));
			paperQuestionExList.add(paperQuestionEx);
			paperQuestionExMap.put(paperQuestionEx.getId(), paperQuestionEx);
		}
		
		List<PaperQuestionEx> treeList = new ArrayList<PaperQuestionEx>();
		for(PaperQuestionEx paperQuestionEx : paperQuestionExList) {
			if(paperQuestionEx.getParentId() == 0) {
				treeList.add(paperQuestionEx);
			}else{
				PaperQuestionEx parentPaperQuestionEx = paperQuestionExMap.get(paperQuestionEx.getParentId());
				parentPaperQuestionEx.getSubList().add(paperQuestionEx);
			}
		}
		
		return treeList;
	}

	@Override
	public void questionAdd(Integer chapterId, Integer[] questionIds) {
		// 校验数据有效性
		if (chapterId == null) {
			throw new MyException("请拖到到合适的位置！");
		}
		if (!ValidateUtil.isValid(questionIds)) {
			throw new MyException("无法获取参数：questionIds");
		}
		PaperQuestion entity = paperQuestionService.getEntity(chapterId);
		Paper paper = getEntity(entity.getPaperId());
		if (paper.getState() == 0) {
			throw new MyException("试卷已删除");
		}
		if (paper.getState() == 1) {
			throw new MyException("试卷已发布");
		}

		
		// 添加试题
		PaperQuestion chapter = paperQuestionService.getEntity(chapterId);
		List<PaperQuestion> questionList = paperQuestionService.getQuestionList(chapterId);
		int maxNo = questionList.size();
		for (Integer questionId : questionIds) {
			PaperQuestion paperQuestion = paperQuestionService.getEntityByChapter(chapterId, questionId);
			if (paperQuestion != null) {
				throw new MyException("试卷中已包含此试题！");
			}
			Question question = questionService.getEntity(questionId);

			PaperQuestion pq = new PaperQuestion();
			pq.setUpdateTime(new Date());
			pq.setUpdateUserId(getCurUser().getId());
			pq.setPaperId(chapter.getPaperId());
			pq.setParentId(chapterId);
			pq.setQuestionId(questionId);
			pq.setType(2);
			pq.setNo(++maxNo);
			pq.setScore(question.getScore());
			pq.setScoreOptions(question.getScoreOptions());
			paperQuestionService.add(pq);
			
			pq.setParentSub(chapter.getParentSub()+pq.getId()+"_");
			paperQuestionService.update(pq);
			
			//添加试题答案
			List<QuestionAnswer> questionAnswerList = questionAnswerService.getList(questionId);
			for(QuestionAnswer questionAnswer : questionAnswerList){
				PaperQuestionAnswer paperQuestionAnswer = new PaperQuestionAnswer();
				try {
					BeanUtils.copyProperties(paperQuestionAnswer, questionAnswer);
				} catch (IllegalAccessException | InvocationTargetException e) {
					System.err.println("添加答案错误！");
				}
				paperQuestionAnswer.setPaperQuestionId(chapterId);
				paperQuestionAnswerService.add(paperQuestionAnswer);
			}
		}
	}
	
	@Override
	public void scoreUpdate(Integer paperQuestionId, BigDecimal score, Integer[] paperQuestionAnswerId, BigDecimal[] paperQuestionAnswerScore) {
		System.err.println("修改分数："+paperQuestionId+"，————————————————————————"+score);
		//校验数据有效性
		if(paperQuestionId == null) {
			throw new MyException("无法获取参数：paperQuestionId");
		}
		if(score == null) {
			throw new MyException("无法获取参数：score");
		}
		PaperQuestion entity = paperQuestionService.getEntity(paperQuestionId);
		Paper paper = getEntity(entity.getPaperId());
		if (paper.getState() == 0) {
			throw new MyException("试卷已删除");
		}
		if (paper.getState() == 1) {
			throw new MyException("试卷已发布");
		}
		// 设置答案分数
		
		if (paperQuestionAnswerId != null && paperQuestionAnswerScore != null && paperQuestionAnswerId.length != paperQuestionAnswerScore.length) {
			throw new MyException("答案或分值有误！");
		}
		BigDecimal scoreSum = new BigDecimal(0);
		PaperQuestionAnswer paperQuestionAnswer = null;
		if (paperQuestionAnswerId != null && paperQuestionAnswerScore != null) {
			for (int i = 0; i < paperQuestionAnswerId.length; i++) {
				paperQuestionAnswer = paperQuestionAnswerService.getEntity(paperQuestionAnswerId[i]);
				paperQuestionAnswer.setScore(paperQuestionAnswerScore[i]);
				paperQuestionAnswerService.update(paperQuestionAnswer);
				scoreSum = scoreSum.add(paperQuestionAnswerScore[i]);
			}
		
		Question question = questionService.getEntity(paperQuestionAnswer.getQuestionId());
		if (question.getAi() == 1 && scoreSum.compareTo(score) != 0 && question.getType() != 2 && question.getType() != 1 && question.getType() != 4) {
			throw new MyException("答案分值与总分值不符！");
		}
		}
		
		// 设置分数
		PaperQuestion pq = paperQuestionService.getEntity(paperQuestionId);
		pq.setScore(score);
		paperQuestionService.update(pq);
	}
	
	@Override
	public void optionsUpdate(Integer paperQuestionId, Integer[] scoreOptions) {
		System.err.println("修改选择项："+paperQuestionId);
		// 校验数据有效性
		if (paperQuestionId == null) {
			throw new MyException("无法获取参数：paperQuestionId");
		}
		PaperQuestion entity = paperQuestionService.getEntity(paperQuestionId);
		Paper paper = getEntity(entity.getPaperId());
		if (paper.getState() == 0) {
			throw new MyException("试卷已删除");
		}
		if (paper.getState() == 1) {
			throw new MyException("试卷已发布");
		}

		// 更新试卷选项
		PaperQuestion pq = paperQuestionService.getEntity(paperQuestionId);
		Question question = questionService.getEntity(pq.getQuestionId());
		if (scoreOptions == null || scoreOptions.length == 0) {
			pq.setScoreOptions(null);
		} else if (question.getType() == 2) {
			if (ValidateUtil.isValid(scoreOptions) && StringUtil.join(scoreOptions).contains("1")) {
				pq.setScoreOptions("1");
			} else {
				pq.setScoreOptions(null);
			}
		} else if (question.getType() == 3) {
			pq.setScoreOptions(StringUtil.join(scoreOptions));
		} else if (question.getType() == 5) {
			pq.setScoreOptions(StringUtil.join(scoreOptions));
		} else {
			pq.setScoreOptions(null);
		}

		paperQuestionService.update(pq);
	}

	@Override
	public void questionDel(Integer paperQuestionId) {
		// 校验数据有效性
		PaperQuestion entity = paperQuestionService.getEntity(paperQuestionId);
		Paper paper = getEntity(entity.getPaperId());
		if (paper.getState() == 0) {
			throw new MyException("试卷已删除");
		}
		if (paper.getState() == 1) {
			throw new MyException("试卷已发布");
		}
		
		// 删除试题
		PaperQuestion pq = paperQuestionService.getEntity(paperQuestionId);
		List<PaperQuestionAnswer> paperQuestionAnswerList = paperQuestionAnswerService.getPaperQuestionAnswerList(pq.getParentId(), pq.getQuestionId());//章节id 和 试题id
		for(PaperQuestionAnswer paperQuestionAnswer : paperQuestionAnswerList){
			paperQuestionAnswerService.del(paperQuestionAnswer.getId());
		}
		paperQuestionService.del(paperQuestionId);
		List<PaperQuestion> pqList = paperQuestionService.getQuestionList(pq.getParentId());

		Collections.sort(pqList, new Comparator<PaperQuestion>() {
			@Override
			public int compare(PaperQuestion o1, PaperQuestion o2) {
				return o1.getNo() - o2.getNo();
			}
		});

		// 更新排序
		int maxNo = 1;
		for (PaperQuestion cur : pqList) {
			cur.setNo(maxNo++);
			paperQuestionService.update(cur);
		}
	}

	@Override
	public void questionClear(Integer chapterId) {
		// 校验数据有效性
		if (chapterId == null) {
			throw new MyException("无法获取参数：chapterId");
		}
		PaperQuestion entity = paperQuestionService.getEntity(chapterId);
		Paper paper = getEntity(entity.getPaperId());
		if (paper.getState() == 0) {
			throw new MyException("试卷已删除");
		}
		if (paper.getState() == 1) {
			throw new MyException("试卷已发布");
		}

		// 清空试题
		//PaperQuestion chapter = paperQuestionService.getEntity(chapterId);
		List<PaperQuestion> pqList = paperQuestionService.getQuestionList(chapterId);
		for (PaperQuestion pq : pqList) {
			paperQuestionService.del(pq.getId());
		}
	}

	@Override
	public List<Question> getQuestionList(Integer paperId) {
		return paperDao.getQuestionList(paperId);
	}

	@Override
	public List<Paper> getList(Integer paperTypeId) {
		return paperDao.getList(paperTypeId);
	}

	@Override
	public void batchScoreUpdate(Integer chapterId, BigDecimal score, String options) {
		// 校验数据有效性
		if (chapterId == null) {
			throw new MyException("参数错误：chapterId");
		}
		if (score == null) {
			throw new MyException("参数错误：score");
		}
		PaperQuestion entity = paperQuestionService.getEntity(chapterId);
		Paper paper = getEntity(entity.getPaperId());
		if (paper.getState() == 0) {
			throw new MyException("试卷已删除");
		}
		if (paper.getState() == 1) {
			throw new MyException("试卷已发布");
		}

		// 更新试卷分数
		List<PaperQuestion> pqList = paperQuestionService.getQuestionList(chapterId);
		for (PaperQuestion pq : pqList) {
			pq.setScoreOptions(null);
			Question question = questionService.getEntity(pq.getQuestionId());
			if (question.getType() == 2) {
				if (ValidateUtil.isValid(options) && options.contains("1")) {
					pq.setScoreOptions("1");
				}
			} else if (question.getType() == 3) {
				pq.setScoreOptions(options);
			}

			pq.setScore(score);
			paperQuestionService.update(pq);
		}
	}

	@Override
	public void move(Integer id, Integer sourceId, Integer targetId) {
		if (sourceId == null) {
			throw new MyException("参数错误：sourceId");
		}
		if(targetId == null){
			throw new MyException("参数错误：targetId");
		}
		PaperType paperType = paperTypeService.getEntity(sourceId);
		if (paperType == null || paperType.getState() == 0 ){
			throw new MyException("此分类已被删除！");
		}
		PaperType entity = paperTypeService.getEntity(targetId);
		if (entity == null || entity.getState() == 0 ){
			throw new MyException("此分类已被删除！");
		}
		if (paperType.getCreateUserId() != getCurUser().getId()) {
			throw new MyException("权限不足！");
		}
		
		List<Paper> list = paperDao.getList(sourceId);
		for (Paper paper : list) {
			paper.setPaperTypeId(targetId);
			update(paper);
		}
	}

	@Override
	public void addAndUpdate(Paper paper, PaperRemark paperRemark) {
		if(!ValidateUtil.isValid(paper.getName())){
			throw new MyException("参数错误不能为空！");
		}
		if (paper.getShowType() == null) {
			paper.setShowType(new BigDecimal(1));
		}
		
		paper.setCreateUserId(getCurUser().getId());
		paper.setCreateTime(new Date());
		paper.setUpdateTime(new Date());
		paper.setUpdateUserId(getCurUser().getId());
		paper.setTotalScore(BigDecimal.ZERO);
		paper.setState(2);
		paperDao.add(paper);
		
		paperRemark.setPaperId(paper.getId());
		paperRemarkService.add(paperRemark);
	}

	@Override
	public void copy(Integer id) throws Exception {
		Paper paper = paperDao.getEntity(id);
		if(!hasWriteAuth(paper.getPaperTypeId(), getCurUser().getId())) {
			throw new MyException("权限不足！");
		}
		Paper entity = new Paper();
		BeanUtils.copyProperties(entity, paper);
		entity.setName(paper.getName()+"【复件】");
		entity.setState(2);
		entity.setCreateTime(new Date());
		entity.setCreateUserId(getCurUser().getId());
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		paperDao.add(entity);

		List<PaperQuestion> chapterList = paperQuestionService.getChapterList(paper.getId());
		if (chapterList != null && chapterList.size() != 0) {
			for(PaperQuestion paperQuestionChapter : chapterList){//章节
				PaperQuestion paperQuestionChapterEntity = new PaperQuestion();
				BeanUtils.copyProperties(paperQuestionChapterEntity, paperQuestionChapter);
				paperQuestionChapterEntity.setPaperId(entity.getId());
				paperQuestionService.add(paperQuestionChapterEntity);
				paperQuestionChapterEntity.setParentSub("_"+paperQuestionChapterEntity.getId()+"_");
				paperQuestionService.update(paperQuestionChapterEntity);
				
				List<PaperQuestion> questionList = paperQuestionService.getQuestionList(paperQuestionChapter.getId());
				for(PaperQuestion paperQuestionQuestion : questionList){//试卷试题
					PaperQuestion paperQuestionQuestionEntity = new PaperQuestion();
					BeanUtils.copyProperties(paperQuestionQuestionEntity, paperQuestionQuestion);
					paperQuestionQuestionEntity.setParentId(paperQuestionChapterEntity.getId());
					paperQuestionQuestionEntity.setPaperId(entity.getId());
					paperQuestionService.add(paperQuestionQuestionEntity);
					paperQuestionQuestionEntity.setParentSub(paperQuestionChapterEntity.getParentSub() + paperQuestionQuestionEntity.getId()+"_");
					paperQuestionService.update(paperQuestionQuestionEntity);
					
					List<PaperQuestionAnswer> paperQuestionAnswerList = paperQuestionAnswerService.getPaperQuestionAnswerList(paperQuestionChapter.getId(), paperQuestionQuestion.getQuestionId());
					for(PaperQuestionAnswer paperQuestionAnswer : paperQuestionAnswerList){
						PaperQuestionAnswer paperQuestionAnswerEntity = new PaperQuestionAnswer();
						BeanUtils.copyProperties(paperQuestionAnswerEntity, paperQuestionAnswer);
						paperQuestionAnswerEntity.setPaperQuestionId(paperQuestionChapterEntity.getId());
						paperQuestionAnswerService.add(paperQuestionAnswerEntity);
					}
				}
			}
		}
		
		List<PaperRemark> paperRemarkList = paperRemarkService.getList(paper.getId());
		if (paperRemarkList != null && paperRemarkList.size() != 0) {
			for(PaperRemark paperRemark : paperRemarkList){
				PaperRemark paperRemarkEntity = new PaperRemark();
				BeanUtils.copyProperties(paperRemarkEntity, paperRemark);
				paperRemarkEntity.setPaperId(entity.getId());
				paperRemarkService.add(paperRemarkEntity);
			}
		}
	}

	@Override
	public List<Map<String, Object>> paperQuestionList(Integer id) {
		List<PaperQuestion> chapterList = paperQuestionService.getChapterList(id);
		List<Map<String, Object>> mapList = new ArrayList<>();
		for(PaperQuestion chapter : chapterList){
			//章节
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> chapterMap = new HashMap<String, Object>();
			chapterMap.put("id", chapter.getId());
			chapterMap.put("name", chapter.getName());
			chapterMap.put("description", chapter.getDescription());
			chapterMap.put("parentId", chapter.getPaperId());
			map.put("chapter",  chapterMap);
			//试题
			List<PaperQuestion> paperQuestionList = paperQuestionService.getQuestionList(chapter.getId());
			List<Map<String, Object>> questionsListMap = new ArrayList<>();
			for(PaperQuestion paperQuestion : paperQuestionList){
				Map<String, Object> questionMap = new HashMap<>();
				Question question = questionService.getEntity(paperQuestion.getQuestionId());
				questionMap.put("id", question.getId());
				questionMap.put("ai", question.getAi());
				questionMap.put("type", question.getType());
				questionMap.put("typeName", DictCache.getDictValue("QUESTION_TYPE", question.getType().toString()));
				questionMap.put("difficulty", question.getDifficulty());
				questionMap.put("difficultyName", DictCache.getDictValue("QUESTION_DIFFICULTY", question.getDifficulty().toString()));
				questionMap.put("updateUserName", userService.getEntity(question.getUpdateUserId()).getName());
				questionMap.put("title", question.getTitle());
				questionMap.put("analysis", question.getAnalysis());
				questionMap.put("score", paperQuestion.getScore());
				questionMap.put("scoreOptions", paperQuestion.getScoreOptions());
				questionMap.put("paperQuestionId", paperQuestion.getId());
				questionMap.put("options", new String[0]);// 默认为长度为0的数组
				
				//if(question.getType() == 1 || question.getType() == 2 ){
					List<QuestionOption> questionOptionList = questionOptionService.getList(paperQuestion.getQuestionId());
					if (questionOptionList != null) {							
						String[] options = new String[questionOptionList.size()];
						for (int i = 0; i < questionOptionList.size(); i++) {
							options[i] = questionOptionList.get(i).getOptions();// 按选项顺序添加试题
						}
						questionMap.put("options", options);
					}
				//}
				
				//QuestionType questionType = questionTypeService.getEntity(question.getQuestionTypeId()); //子管理员应该可以看到答案   2021-08-30 15:57:33
				//boolean writeAuth = questionTypeService.hasWriteAuth(questionType, getCurUser().getId());
				//boolean readAuth = questionTypeService.hasReadAuth(questionType, getCurUser().getId());
				List<PaperQuestionAnswer> paperQuestionAnswerList = paperQuestionAnswerService.getPaperQuestionAnswerList(chapter.getId(), question.getId());

				List<Map<String, Object>> questionAnswerSplitList = new ArrayList<Map<String, Object>>();
				if (question.getType() == 3) {
					for(PaperQuestionAnswer paperQuestionAnswer : paperQuestionAnswerList){
						Map<String, Object> questionAnswerMap = new HashMap<String, Object>();
						String[] split = paperQuestionAnswer.getAnswer().split("\n");
						questionAnswerMap.put("id", paperQuestionAnswer.getId());
						questionAnswerMap.put("answer", split);
						questionAnswerMap.put("score", paperQuestionAnswer.getScore());
						questionAnswerMap.put("questionId", paperQuestionAnswer.getQuestionId());
						questionAnswerSplitList.add(questionAnswerMap);
					}
				} else if (question.getType() == 5 && question.getAi() == 1) {
					for(PaperQuestionAnswer paperQuestionAnswer : paperQuestionAnswerList){					
						Map<String, Object> questionAnswerMap = new HashMap<String, Object>();
						String[] split = paperQuestionAnswer.getAnswer().split("\n");
						questionAnswerMap.put("id", paperQuestionAnswer.getId());
						questionAnswerMap.put("answer", split);
						questionAnswerMap.put("score", paperQuestionAnswer.getScore());
						questionAnswerMap.put("questionId", paperQuestionAnswer.getQuestionId());
						questionAnswerSplitList.add(questionAnswerMap);
					}
				} else {
					for(PaperQuestionAnswer paperQuestionAnswer : paperQuestionAnswerList){
						Map<String, Object> questionAnswerMap = new HashMap<String, Object>();
						questionAnswerMap.put("id", paperQuestionAnswer.getId());
						questionAnswerMap.put("answer", paperQuestionAnswer.getAnswer());
						questionAnswerMap.put("score", paperQuestionAnswer.getScore());
						questionAnswerMap.put("questionId", paperQuestionAnswer.getQuestionId());
						questionAnswerSplitList.add(questionAnswerMap);
					}
				}
				
				/*if (!writeAuth && !readAuth) { //子管理员应该可以看到答案   2021-08-30 15:57:33
					for(Map<String, Object> forMap : questionAnswerSplitList){
						forMap.put("answer", "");
					}
				}*/
				questionMap.put("answers", questionAnswerSplitList);

				questionsListMap.add(questionMap);
			}
			map.put("questionList",  questionsListMap);
			mapList.add(map);
		}
		return mapList;
	}

	@Override
	public void publish(Integer id) {
		Paper paper = paperDao.getEntity(id);
		if(paper.getState() == 0) {
			throw new MyException("试卷【"+paper.getName()+"】已删除！");
		}
		if(paper.getState() == 1) {
			throw new MyException("试卷【"+paper.getName()+"】已发布！");
		}
		
		
		BigDecimalUtil bigDecimalUtil = BigDecimalUtil.newInstance(0);
		List<PaperQuestion> paperQuestionList = paperQuestionService.getList(id);
		for (PaperQuestion paperQuestion : paperQuestionList) {
			if (paperQuestion.getType() != 2) {
				continue;
			}

			bigDecimalUtil.add(paperQuestion.getScore());
		}

		paper.setTotalScore(bigDecimalUtil.getResult());
		paper.setUpdateTime(new Date());
		paper.setUpdateUserId(getCurUser().getId());
		paper.setState(1);
		paperDao.update(paper);
	}
	
//	private void updateTotalScore(Integer paperId) {
//		BigDecimalUtil bigDecimalUtil = BigDecimalUtil.newInstance(0);
//		List<PaperQuestion> paperQuestionList = paperQuestionService.getList(paperId);
//		for (PaperQuestion paperQuestion : paperQuestionList) {
//			if (paperQuestion.getType() != 2) {
//				continue;
//			}
//
//			bigDecimalUtil.add(paperQuestion.getScore());
//		}
//
//		Paper paper = getEntity(paperId);
//		paper.setTotalScore(bigDecimalUtil.getResult());
//		update(paper);
//	}
	
	private boolean hasWriteAuth(Integer paperTypeId, Integer userId) {
		PaperType paperType = paperTypeService.getEntity(paperTypeId);
		return paperType.getWriteUserIds().contains(String.format(",%s,", userId));
	}
}
