package com.wcpdoc.exam.core.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

import com.wcpdoc.base.service.UserService;
import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.BigDecimalUtil;
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.PaperDao;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.entity.PaperQuestionAnswer;
import com.wcpdoc.exam.core.entity.PaperQuestionRule;
import com.wcpdoc.exam.core.entity.PaperRemark;
import com.wcpdoc.exam.core.entity.PaperType;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.entity.ex.Chapter;
import com.wcpdoc.exam.core.entity.ex.MyPaper;
import com.wcpdoc.exam.core.entity.ex.MyQuestion;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.PaperQuestionAnswerService;
import com.wcpdoc.exam.core.service.PaperQuestionRuleService;
import com.wcpdoc.exam.core.service.PaperQuestionService;
import com.wcpdoc.exam.core.service.PaperRemarkService;
import com.wcpdoc.exam.core.service.PaperService;
import com.wcpdoc.exam.core.service.PaperTypeService;
import com.wcpdoc.exam.core.service.QuestionAnswerService;
import com.wcpdoc.exam.core.service.QuestionOptionService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionTypeService;

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
	@Resource
	private PaperQuestionRuleService paperQuestionRuleService;
	@Resource
	private ExamService examService;
	
	@Override
	@Resource(name = "paperDaoImpl")
	public void setDao(BaseDao<Paper> dao) {
		super.dao = dao;
	}
	
	@Override
	public Integer addAndUpdate(Paper paper, PaperRemark paperRemark) {// paperRemark暂时不用保留
		// 校验数据有效性
		PaperType paperType = paperTypeService.getEntity(paper.getPaperTypeId());
		if(paperType.getCreateUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("无操作权限");
		}
		
		// 添加试卷
		Date curTime = new Date();
		paper.setCreateUserId(getCurUser().getId());
		paper.setCreateTime(curTime);
		paper.setUpdateUserId(getCurUser().getId());
		paper.setUpdateTime(curTime);
		paper.setTotalScore(BigDecimal.ZERO);// 默认总分数为0
		paper.setState(2);// 默认为草稿
		add(paper);
		
		return paper.getId();//快速创建考试需要用id查找信息
	}
	
	@Override
	public void updateAndUpdate(Paper paper) {
		// 校验数据有效性
		Paper entity = getEntity(paper.getId());
		if (entity.getState() == 0) {
			throw new MyException("已删除");
		}
		if (entity.getGenType().intValue() != paper.getGenType().intValue()) {// 类型的切换导致修改的地方多，暂时不允许修改 22.03.31 zhanghc
			throw new MyException("参数错误：genType");
		}
		if (entity.getMarkType().intValue() != paper.getMarkType().intValue()) {
			throw new MyException("参数错误：markType");
		}
		if (entity.getState() == 3) {
			throw new MyException("已归档");
		}
		PaperType paperType = paperTypeService.getEntity(entity.getPaperTypeId());
		if(paperType.getCreateUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("无操作权限");
		}
		
		List<Exam> examList = examService.getList(); //是否被考试引用
		if (ValidateUtil.isValid(examList)) {
			for(Exam exam : examList){
				if (exam.getPaperId().intValue() ==  paper.getId().intValue()) {
					throw new MyException(String.format("该试卷已被【%s】考试引用", exam.getName()));
				}
			}
		}
		
		// 更新试卷
		entity.setGenType(paper.getGenType());
		entity.setShowType(paper.getShowType());
		entity.setName(paper.getName());
		entity.setMarkType(paper.getMarkType());
		entity.setPassScore(paper.getPassScore());
		entity.setUpdateUserId(getCurUser().getId());
		entity.setUpdateTime(new Date());
		update(entity);
	}
	
	@Override
	public void delAndUpdate(Integer id) {
		// 校验数据有效性
		Paper paper = getEntity(id);
		if(paper.getState() == 0) {
			throw new MyException("已删除");
		}
		PaperType paperType = paperTypeService.getEntity(paper.getPaperTypeId());
		if(paperType.getCreateUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("无操作权限");
		}
		
		// 删除试卷
		paper.setState(0);
		paper.setUpdateTime(new Date());
		paper.setUpdateUserId(getCurUser().getId());
		update(paper);
	}
	
	@Override
	public void copy(Integer id) {
		// 校验数据有效性
		Paper paper = paperDao.getEntity(id);
		PaperType paperType = paperTypeService.getEntity(paper.getPaperTypeId());
		if(paperType.getCreateUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("无操作权限");
		}
		
		// 复制试卷
		Paper copyPaper = new Paper();// 复制试卷
		try {
			BeanUtils.copyProperties(copyPaper, paper);
		} catch (Exception e) {
			throw new MyException(e.getMessage());
		}
		copyPaper.setName(String.format("【复件】%s", paper.getName()));
		copyPaper.setState(2);
		copyPaper.setCreateTime(new Date());
		copyPaper.setCreateUserId(getCurUser().getId());
		copyPaper.setUpdateTime(new Date());
		copyPaper.setUpdateUserId(getCurUser().getId());
		add(copyPaper);

		List<PaperQuestion> chapterList = paperQuestionService.getChapterList(paper.getId());// 复制章节
		for (PaperQuestion chapter : chapterList) {
			PaperQuestion copyChapter = new PaperQuestion();
			try {
				BeanUtils.copyProperties(copyChapter, chapter);
			} catch (Exception e) {
				throw new MyException(e.getMessage());
			}
			copyChapter.setPaperId(copyPaper.getId());
			paperQuestionService.add(copyChapter);
			copyChapter.setParentSub(String.format("_%s_", copyChapter.getId()));
			paperQuestionService.update(copyChapter);
			
			if (copyPaper.getGenType() == 1) {// 人工组卷
				List<PaperQuestion> questionList = paperQuestionService.getChapterDetailList(chapter.getId());//复制试题
				for (PaperQuestion paperQuestion : questionList) {
					PaperQuestion copyPaperQuestion = new PaperQuestion();
					try {
						BeanUtils.copyProperties(copyPaperQuestion, paperQuestion);
					} catch (Exception e) {
						throw new MyException(e.getMessage());
					}
					copyPaperQuestion.setParentId(copyChapter.getId());
					copyPaperQuestion.setPaperId(copyPaper.getId());
					paperQuestionService.add(copyPaperQuestion);
					copyPaperQuestion.setParentSub(String.format("%s%s_", copyChapter.getParentSub(), copyPaperQuestion.getId()));
					paperQuestionService.update(copyPaperQuestion);
				}

				List<PaperQuestionAnswer> answerList = paperQuestionAnswerService.getListByChapter(chapter.getId());// 复制答案
				for (PaperQuestionAnswer answer : answerList) {
					PaperQuestionAnswer copyAnswer = new PaperQuestionAnswer();
					try {
						BeanUtils.copyProperties(copyAnswer, answer);
					} catch (Exception e) {
						throw new MyException(e.getMessage());
					}
					copyAnswer.setPaperQuestionId(copyChapter.getId());
					copyAnswer.setPaperId(copyPaper.getId());
					paperQuestionAnswerService.add(copyAnswer);
				}
			} else {// 随机组卷
				List<PaperQuestionRule> ruleList = paperQuestionRuleService.getList(chapter.getId());// 复制规则
				for(PaperQuestionRule paperQuestionRule : ruleList){
					PaperQuestionRule copyPaperQuestionRule = new PaperQuestionRule();
					try {
						BeanUtils.copyProperties(copyPaperQuestionRule, paperQuestionRule);
					} catch (Exception e) {
						throw new MyException(e.getMessage());
					}
					
					copyPaperQuestionRule.setPaperId(copyPaper.getId());
					copyPaperQuestionRule.setPaperQuestionId(copyChapter.getId());
					copyPaperQuestionRule.setUpdateTime(new Date());
					copyPaperQuestionRule.setUpdateUserId(getCurUser().getId());
					paperQuestionRuleService.add(copyPaperQuestionRule);
				}
			}
		}
	}
	
	@Override
	public void archive(Integer id) {
		// 校验数据有效性
		Paper paper = getEntity(id);
		if(paper.getState() == 0) {
			throw new MyException("已删除");
		}
		if(paper.getState() == 2){
			throw new MyException("未发布");
		}
		if(paper.getState() == 3){
			throw new MyException("已归档");
		}
		PaperType paperType = paperTypeService.getEntity(paper.getPaperTypeId());
		if(paperType.getCreateUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("无操作权限");
		}
		
		// 归档
		paper.setState(3);
		paper.setUpdateTime(new Date());
		paper.setUpdateUserId(getCurUser().getId());
		update(paper);
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
		if(paper.getState() == 3){
			throw new MyException("已归档");
		}
		PaperType paperType = paperTypeService.getEntity(paper.getPaperTypeId());
		if(paperType.getCreateUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("无操作权限");
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
	public void chapterEdit(PaperQuestion chapter) {
		//校验数据有效性
		PaperQuestion paperQuestion = paperQuestionService.getEntity(chapter.getId());
		if(paperQuestion == null){
			throw new MyException("参数错误：id");
		}
		if (paperQuestion.getType() != 1) {
			throw new MyException("参数错误：id");
		}
		Paper paper = getEntity(paperQuestion.getPaperId());
		if (paper.getState() == 0) {
			throw new MyException("试卷已删除");
		}
		if (paper.getState() == 1) {
			throw new MyException("试卷已发布");
		}
		if(paper.getState() == 3){
			throw new MyException("已归档");
		}
		PaperType paperType = paperTypeService.getEntity(paper.getPaperTypeId());
		if(paperType.getCreateUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("无操作权限");
		}
		
		// 修改章节
		paperQuestion.setName(chapter.getName());
		paperQuestion.setDescription(chapter.getDescription());
		paperQuestion.setUpdateTime(new Date());
		paperQuestion.setUpdateUserId(getCurUser().getId());
		paperQuestionService.update(paperQuestion);
	}
	
	@Override
	public void chapterDel(Integer chapterId) {
		//校验数据有效性
		PaperQuestion chapter = paperQuestionService.getEntity(chapterId);
		if(chapter == null){
			throw new MyException("参数错误：chapterId");
		}
		if (chapter.getType() != 1) {
			throw new MyException("参数错误：chapterId");
		}
		Paper paper = getEntity(chapter.getPaperId());
		if (paper.getState() == 0) {
			throw new MyException("试卷已删除");
		}
		if (paper.getState() == 1) {
			throw new MyException("试卷已发布");
		}
		if(paper.getState() == 3){
			throw new MyException("已归档");
		}
		PaperType paperType = paperTypeService.getEntity(paper.getPaperTypeId());
		if(paperType.getCreateUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("无操作权限");
		}
		
		// 删除章节下试题
		List<PaperQuestion> questionList = paperQuestionService.getChapterDetailList(chapterId);
		for(PaperQuestion curQuestion : questionList) {
			paperQuestionService.del(curQuestion.getId());
		}
		
		//删除随机章节规则
		if (paper.getGenType() == 2) {
			List<PaperQuestionRule> chapterList = paperQuestionRuleService.getList(chapterId);
			for(PaperQuestionRule rule : chapterList) {
				paperQuestionRuleService.del(rule.getId());
			}
		}
		
		// 删除章节
		paperQuestionService.del(chapterId);
		
		
	}
	
	@Override
	public void chapterMove(Integer sourceId, Integer targetId) {
		//校验数据有效性
		if (!ValidateUtil.isValid(sourceId)) {
			throw new MyException("参数错误：sourceId");
		}
		if (!ValidateUtil.isValid(targetId)) {
			throw new MyException("参数错误：targetId");
		}
		PaperQuestion source = paperQuestionService.getEntity(sourceId);
		PaperQuestion target = paperQuestionService.getEntity(targetId);
		if (source.getType() != 1) {
			throw new MyException("参数错误：sourceId");
		}
		if (target.getType() != 1) {
			throw new MyException("参数错误：targetId");
		}
		if (source.getPaperId().intValue() != target.getPaperId().intValue()) {// 如果修改的不是同一张试卷
			throw new MyException("参数错误：targetId");
		}
		Paper paper = getEntity(source.getPaperId());
		if (paper.getState() == 0) {
			throw new MyException("试卷已删除");
		}
		if (paper.getState() == 1) {
			throw new MyException("试卷已发布");
		}
		if(paper.getState() == 3){
			throw new MyException("已归档");
		}
		PaperType paperType = paperTypeService.getEntity(paper.getPaperTypeId());
		if(paperType.getCreateUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("无操作权限");
		}
		
		// 移动
		Integer sourceNo = source.getNo();
		source.setUpdateUserId(getCurUser().getId());
		source.setUpdateTime(new Date());
		source.setNo(target.getNo());
		target.setUpdateUserId(getCurUser().getId());
		target.setUpdateTime(new Date());
		target.setNo(sourceNo);
		paperQuestionService.update(source);
		paperQuestionService.update(target);
	}
	
	@Override
	public void questionMove(Integer id, Integer sourceId, Integer targetId) {
		//校验数据有效性
		if (!ValidateUtil.isValid(id)) {
			throw new MyException("参数错误：id");
		}
		if (!ValidateUtil.isValid(sourceId)) {
			throw new MyException("参数错误：sourceId");
		}
		if (!ValidateUtil.isValid(targetId)) {
			throw new MyException("参数错误：targetId");
		}
		PaperQuestion source = paperQuestionService.getEntity(id, sourceId);
		PaperQuestion target = paperQuestionService.getEntity(id, targetId);
		if (source.getType() == 1) {
			throw new MyException("参数错误：sourceId");
		}
		if (target.getType() == 1) {
			throw new MyException("参数错误：targetId");
		}
		//if (source.getPaperId().intValue() != target.getPaperId().intValue()) {// 如果修改的不是同一张试卷
		//	throw new MyException("参数错误：targetId");// 肯定是一张，不用在校验
		//}
		Paper paper = getEntity(source.getPaperId());
		if (paper.getState() == 0) {
			throw new MyException("试卷已删除");
		}
		if (paper.getState() == 1) {
			throw new MyException("试卷已发布");
		}
		if(paper.getState() == 3){
			throw new MyException("已归档");
		}
		PaperType paperType = paperTypeService.getEntity(paper.getPaperTypeId());
		if(paperType.getCreateUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("无操作权限");
		}
		
		// 移动
		Integer sourceNo = source.getNo();
		source.setUpdateUserId(getCurUser().getId());
		source.setUpdateTime(new Date());
		source.setNo(target.getNo());
		target.setUpdateUserId(getCurUser().getId());
		target.setUpdateTime(new Date());
		target.setNo(sourceNo);
		
		if (source.getParentId().intValue() != target.getParentId().intValue()) {// 如果不是同一个章节，更新父子关系
			Integer parentId = source.getParentId();
			String parentSub = source.getParentSub();
			source.setParentId(target.getParentId());
			source.setParentSub(target.getParentSub());
			
			target.setParentId(parentId);
			target.setParentSub(parentSub);
		}
		
		paperQuestionService.update(source);
		paperQuestionService.update(target);
	}
	
	@Override
	public void questionAdd(Integer chapterId, Integer[] questionIds) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(chapterId)) {
			throw new MyException("参数错误：chapterId");
		}
		if (!ValidateUtil.isValid(questionIds)) {
			throw new MyException("参数错误：questionIds");
		}
		PaperQuestion _chapter = paperQuestionService.getEntity(chapterId);
		if (_chapter.getType() != 1) {
			throw new MyException("参数错误：chapterId");
		}
		Paper paper = getEntity(_chapter.getPaperId());
		if (paper.getState() == 0) {
			throw new MyException("试卷已删除");
		}
		if (paper.getState() == 1) {
			throw new MyException("试卷已发布");
			
		}
		if (paper.getState() == 3) {
			throw new MyException("已归档");
		}
		PaperType paperType = paperTypeService.getEntity(paper.getPaperTypeId());
		if(paperType.getCreateUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("无操作权限");
		}
		
		List<PaperQuestion> chapterList = paperQuestionService.getChapterList(paper.getId());
		List<PaperQuestion> curChapterDetailList = null;
		for (PaperQuestion chapter : chapterList) {
			List<PaperQuestion> chapterDetailList = paperQuestionService.getChapterDetailList(chapter.getId());
			if (chapter.getId().intValue() == chapterId.intValue()) {
				curChapterDetailList = chapterDetailList;
			}
			for (PaperQuestion chapterDetail : chapterDetailList) {
				for (Integer questionId : questionIds) {
					if (chapterDetail.getQuestionId().intValue() == questionId.intValue()) {
						throw new MyException(String.format("试题已添加，编号：%s", questionId));
					}
				}
			}
		}
		
		List<Question> unAddQuestionList = new ArrayList<>();
		for (Integer questionId : questionIds) {
			Question question = questionService.getEntity(questionId);
			if (question.getState() != 1) {
				throw new MyException(String.format("试题未发布，编号：%s", question.getId()));
			}
			if (paper.getMarkType() == 1 && question.getAi() != 1) {// 如果试卷是智能阅卷，添加试题为人工阅卷类型
				throw new MyException(String.format("不支持主观题，编号：%s", question.getId()));
			}
			unAddQuestionList.add(question);
		}
		
		// 添加试题
		int maxNo = curChapterDetailList.size();// 试题最大序号
		for (Question question : unAddQuestionList) {
			PaperQuestion pq = new PaperQuestion();
			pq.setUpdateTime(new Date());
			pq.setUpdateUserId(getCurUser().getId());
			pq.setPaperId(_chapter.getPaperId());
			pq.setParentId(chapterId);
			pq.setQuestionId(question.getId());
			pq.setType(2);
			pq.setNo(++maxNo);
			pq.setScore(question.getScore());
			pq.setAiOptions(question.getAiOptions());
			paperQuestionService.add(pq);
			
			pq.setParentSub(String.format("%s%s_", _chapter.getParentSub(), pq.getId()));
			paperQuestionService.update(pq);
			
			//添加试题答案
			List<QuestionAnswer> questionAnswerList = questionAnswerService.getList(question.getId());
			for(QuestionAnswer questionAnswer : questionAnswerList){
				PaperQuestionAnswer paperQuestionAnswer = new PaperQuestionAnswer();
				try {
					BeanUtils.copyProperties(paperQuestionAnswer, questionAnswer);
				} catch (Exception e) {
					throw new MyException(e.getMessage());
				}
				paperQuestionAnswer.setPaperQuestionId(chapterId);
				paperQuestionAnswer.setPaperId(_chapter.getPaperId());
				paperQuestionAnswerService.add(paperQuestionAnswer);
			}
		}
	}
	
	@Override
	public void scoreUpdate(Integer id, Integer questionId, BigDecimal score, BigDecimal[] subScores, Integer[] aiOptions) {
		// 校验数据有效性
		if (id == null) {
			throw new MyException("参数错误：id");
		}
		if (score == null) {
			throw new MyException("参数错误：score");
		}
		Paper paper = getEntity(id);
		if (paper.getState() == 0) {
			throw new MyException("试卷已删除");
		}
		if (paper.getState() == 1) {
			throw new MyException("试卷已发布");
		}
		if (paper.getState() == 3) {
			throw new MyException("试卷已归档");
		}

		//设置分数
		String aiOptionsTemp = ValidateUtil.isValid(aiOptions) ? StringUtil.join(aiOptions) : "";
		Question question = questionService.getEntity(questionId);
		PaperQuestion pq = paperQuestionService.getEntity(id, questionId);
		if (question.getType() == 2 && aiOptionsTemp.contains("1")) {//智能选项（1：漏选得分；2：答案无顺序；3：大小写不敏感；)
			// pq.setAiOptions("1");// 默认就是不需要设置
		} else if (question.getType() == 3 && question.getAi() == 1 ) {
			if (aiOptionsTemp.contains("2") && aiOptionsTemp.contains("3")) {
				pq.setAiOptions("2,3");
			} else if (aiOptionsTemp.contains("2")) {
				pq.setAiOptions("2");
			} else if (aiOptionsTemp.contains("2")) {
				pq.setAiOptions("3");
			}
		} else if (question.getType() == 5 && question.getAi() == 1) {
			if (aiOptionsTemp.contains("3")) {
				pq.setAiOptions("3");
			}
		}
		pq.setScore(score);
		paperQuestionService.update(pq);
			
		// 更新答案分数
		List<PaperQuestionAnswer> answerList = paperQuestionAnswerService.getListBySingleQuestion(pq.getPaperId(), pq.getQuestionId());
		if (question.getType() == 1 || question.getType() == 4) { //单选 判断
			for(PaperQuestionAnswer paperQuestionAnswer : answerList){
				paperQuestionAnswer.setScore(score);
				paperQuestionAnswerService.update(paperQuestionAnswer);
			}
		} else if (question.getType() == 2) {// 多选
			if (aiOptionsTemp.contains("1")) {
				for (PaperQuestionAnswer answer : answerList) {// 只有一个
					answer.setScore(subScores[0]);//页面设置的值
					paperQuestionAnswerService.update(answer);
				}
			} else {
				for (PaperQuestionAnswer answer : answerList) {
					answer.setScore(BigDecimalUtil.newInstance(score).div(2, 2).getResult());// 分数的一半
					paperQuestionAnswerService.update(answer);
				}
			}
		} else if ((question.getType() == 3 && question.getAi() == 1) 
				|| (question.getType() == 5 && question.getAi() == 1)) { // 智能填空 智能问答
			BigDecimal scoreSum = new BigDecimal(0);
			for (int i = 0; i < subScores.length; i++) {
				scoreSum = scoreSum.add(subScores[i]);
				PaperQuestionAnswer answer = answerList.get(i);
				answer.setScore(subScores[i]);
				paperQuestionAnswerService.update(answer);
			}
			if (scoreSum.compareTo(score) != 0) {
				throw new MyException("分值错误");
			}
		}
	}

	@Override
	public void questionDel(Integer id, Integer questionId) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(id)) {
			throw new MyException("参数错误：id");
		}
		if (!ValidateUtil.isValid(questionId)) {
			throw new MyException("参数错误：questionId");
		}
		
		Paper paper = getEntity(id);
		if (paper.getState() == 0) {
			throw new MyException("试卷已删除");
		}
		if (paper.getState() == 1) {
			throw new MyException("试卷已发布");
		}
		if(paper.getState() == 3){
			throw new MyException("试卷已归档");
		}
		PaperType paperType = paperTypeService.getEntity(paper.getPaperTypeId());
		if(paperType.getCreateUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("无操作权限");
		}
		PaperQuestion curPaperQuestion = null;
		List<PaperQuestion> curChapterDetailList = null;
		List<PaperQuestion> chapterList = paperQuestionService.getChapterList(id);
		for (PaperQuestion chapter : chapterList) {
			List<PaperQuestion> chapterDetailList = paperQuestionService.getChapterDetailList(chapter.getId());
			for (PaperQuestion chapterDetail : chapterDetailList) {
				if (chapterDetail.getQuestionId().intValue() == questionId.intValue()) {
					curPaperQuestion = chapterDetail;
					curChapterDetailList = chapterDetailList;
					break;
				}
			}
		}
		
		if (curPaperQuestion == null) {
			throw new MyException("当前试题不存在");
		}
		
		// 删除答案
		List<PaperQuestionAnswer> answerList = paperQuestionAnswerService.getListBySingleQuestion(id, questionId);
		for(PaperQuestionAnswer answer : answerList){
			paperQuestionAnswerService.del(answer.getId());
		}
		
		// 删除试题
		paperQuestionService.del(curPaperQuestion.getId());
		
		// 同章节下试题重新排序
		int maxNo = 1;
		for (PaperQuestion curChapterDetail : curChapterDetailList) {
			curChapterDetail.setNo(maxNo++);
			paperQuestionService.update(curChapterDetail);
		}
	}

	@Override
	public void questionClear(Integer chapterId) {
		// 校验数据有效性
		PaperQuestion chapter = paperQuestionService.getEntity(chapterId);
		if(chapter == null){
			throw new MyException("参数错误：chapterId");
		}
		if (chapter.getType() != 1) {
			throw new MyException("参数错误：chapterId");
		}
		Paper paper = getEntity(chapter.getPaperId());
		if (paper.getState() == 0) {
			throw new MyException("试卷已删除");
		}
		if (paper.getState() == 1) {
			throw new MyException("试卷已发布");
		}
		if(paper.getState() == 3){
			throw new MyException("已归档");
		}
		PaperType paperType = paperTypeService.getEntity(paper.getPaperTypeId());
		if(paperType.getCreateUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("无操作权限");
		}

		// 清空试题
		List<PaperQuestion> chapterDetailList = paperQuestionService.getChapterDetailList(chapterId);
		for (PaperQuestion chapterDetail : chapterDetailList) {
			List<PaperQuestionAnswer> answerList = paperQuestionAnswerService.getListBySingleQuestion(chapterDetail.getPaperId(), chapterDetail.getQuestionId());// 删除答案
			for(PaperQuestionAnswer answer : answerList){
				paperQuestionAnswerService.del(answer.getId());
			}
			paperQuestionService.del(chapterDetail.getId());// 删除试题
		}
	}

	@Override
	public List<Paper> getList(Integer paperTypeId) {
		return paperDao.getList(paperTypeId);
	}

	@Override
	public void batchScoreUpdate(Integer chapterId, BigDecimal score, BigDecimal subScores, Integer[] aiOptions) {
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
		if (paper.getState() == 3) {
			throw new MyException("试卷已归档");
		}

		// 更新试卷分数
		String aiOptionsTemp = ValidateUtil.isValid(aiOptions) ? StringUtil.join(aiOptions) : "";
		List<PaperQuestion> chapterDetailList = paperQuestionService.getChapterDetailList(chapterId);
		for(PaperQuestion chapterDetai : chapterDetailList){
			Question question = questionService.getEntity(chapterDetai.getQuestionId());
			if (question.getType() == 2 && aiOptionsTemp.contains("1")) {//智能选项（1：漏选得分；2：答案无顺序；3：大小写不敏感；)
				// pq.setAiOptions("1");// 默认就是不需要设置
			} else if (question.getType() == 3 && question.getAi() == 1 ) {
				if (aiOptionsTemp.contains("2") && aiOptionsTemp.contains("3")) {
					chapterDetai.setAiOptions("2,3");
				} else if (aiOptionsTemp.contains("2")) {
					chapterDetai.setAiOptions("2");
				} else if (aiOptionsTemp.contains("2")) {
					chapterDetai.setAiOptions("3");
				}
			} else if (question.getType() == 5 && question.getAi() == 1) {
				if (aiOptionsTemp.contains("3")) {
					chapterDetai.setAiOptions("3");
				}
			}
			chapterDetai.setScore(score);
			paperQuestionService.update(chapterDetai);
			
			// 更新答案分数
			List<PaperQuestionAnswer> answerList = paperQuestionAnswerService.getListBySingleQuestion(chapterDetai.getPaperId(), chapterDetai.getQuestionId());
			if (question.getType() == 1 || question.getType() == 4) { //单选 判断
				for(PaperQuestionAnswer paperQuestionAnswer : answerList){
					paperQuestionAnswer.setScore(score);
					paperQuestionAnswerService.update(paperQuestionAnswer);
				}
			} else if (question.getType() == 2) {// 多选
				if (aiOptionsTemp.contains("1")) {
					for (PaperQuestionAnswer answer : answerList) {// 只有一个
						answer.setScore(subScores);//页面设置的值
						paperQuestionAnswerService.update(answer);
					}
				} else {
					for (PaperQuestionAnswer answer : answerList) {
						answer.setScore(BigDecimalUtil.newInstance(score).div(2, 2).getResult());// 分数的一半
						paperQuestionAnswerService.update(answer);
					}
				}
			} else if ((question.getType() == 3 && question.getAi() == 1) 
					|| (question.getType() == 5 && question.getAi() == 1)) { // 智能填空 智能问答
				BigDecimal singleScore = BigDecimalUtil.newInstance(score).div(answerList.size(), 2).getResult();
				for (int i = 0; i < answerList.size() - 1; i++) {
					PaperQuestionAnswer answer = answerList.get(i);
					answer.setScore(singleScore);
					paperQuestionAnswerService.update(answer);
				}
				
				answerList.get(answerList.size() - 1).setScore(BigDecimalUtil.newInstance(singleScore).mul(answerList.size() - 1).sub(score).mul(-1).getResult());
				paperQuestionAnswerService.update(answerList.get(answerList.size() - 1));// 最后一位处理
			}
		}	
	}

	@Override
	public void move(Integer sourceId, Integer targetId) {
		// 校验数据有效性
		if (sourceId == null) {
			throw new MyException("参数错误：sourceId");
		}
		if(targetId == null){
			throw new MyException("参数错误：targetId");
		}
		PaperType source = paperTypeService.getEntity(sourceId);
		if (source.getState() == 0 ){
			throw new MyException("该分类已删除");
		}
		PaperType target = paperTypeService.getEntity(targetId);
		if (target.getState() == 0) {
			throw new MyException("该分类已删除");
		}
		
		if (source.getCreateUserId().intValue() != getCurUser().getId().intValue()) {// 只能移动自己的分类
			throw new MyException("无操作权限");
		}
		//if (!target.getWriteUserIds().contains(String.format(",%s,", getCurUser().getId()))) {// 只能移动到自己的分类或有组权限的分类
		//	throw new MyException("无操作权限");
		//}
		
		// 移动
		List<Paper> list = paperDao.getList(sourceId);
		for (Paper paper : list) {
			paper.setPaperTypeId(targetId);
			update(paper);
		}
	}

	@Override
	public void publish(Integer id) {
		// 校验数据有效性
		Paper paper = getEntity(id);
		List<Question> questionList = null; 
		List<PaperQuestion> chapterList = paperQuestionService.getChapterList(paper.getId());
		Map<Integer, List<PaperQuestionRule>> ruleCache = null;
		if (paper.getGenType() == 1) {
			questionList = paperDao.getQuestionList(paper.getId());
		} else if (paper.getGenType() == 2) {
			ruleCache = new HashMap<>();
			for (PaperQuestion chapter : chapterList) {
				List<PaperQuestionRule> ruleList = paperQuestionRuleService.getList(chapter.getId());
				ruleCache.put(chapter.getId(), ruleList);
			}
		}
		publishValid(paper, questionList, chapterList, ruleCache);
		
		// 发布试卷
		paper.setMarkType(publishCalculateMarkType(paper, questionList, chapterList, ruleCache));
		paper.setTotalScore(publishCalculateTotalScore(paper, chapterList, ruleCache));
		paper.setUpdateTime(new Date());
		paper.setUpdateUserId(getCurUser().getId());
		paper.setState(1);
		update(paper);
	}

	private Integer publishCalculateMarkType(Paper paper, List<Question> questionList, List<PaperQuestion> chapterList,
			Map<Integer, List<PaperQuestionRule>> ruleCache) {
		// 主观题试卷没有包含客观题，则变更试卷类型为客观题试卷
		if (paper.getMarkType() == 1) {// 校验的时候，验证了客观题试卷不包含主观题。
			return paper.getMarkType();
		}

		Integer ai = 1;// 默认为客观题
		if (paper.getGenType() == 1) {// 人工组卷
			for (Question question : questionList) {
				if (question.getAi() != ai) {// 当前试题有主观题
					ai = question.getAi();// 变更默认为主观题
					break;
				}
			}
			return ai;
		}
		
		if (paper.getGenType() == 2) {// 随机组卷 
			for (PaperQuestion chapter : chapterList) {
				for (PaperQuestionRule rule : ruleCache.get(chapter.getId())) {
					if (!rule.getAis().contains(ai.toString())) {// 当前规则有主观题
						ai = 2;// 变更默认为主观题
						break;
					}
				}
			}
			
			return ai;
		}
		
		return null;
	}

	private BigDecimal publishCalculateTotalScore(Paper paper, List<PaperQuestion> chapterList,
			Map<Integer, List<PaperQuestionRule>> ruleCache) {
		BigDecimalUtil totalScore = BigDecimalUtil.newInstance(0);
		if (paper.getGenType() == 1) {
			for (PaperQuestion chapter : chapterList) {
				List<PaperQuestion> chapterDetailList = paperQuestionService.getChapterDetailList(chapter.getId());
				for (PaperQuestion chapterDetail : chapterDetailList) {
					totalScore.add(chapterDetail.getScore());
				}
			}
		} else if (paper.getGenType() == 2) {
			for (PaperQuestion chapter : chapterList) {
				List<PaperQuestionRule> ruleList = ruleCache.get(chapter.getId());
				for (PaperQuestionRule rule : ruleList) {
					totalScore.add(new BigDecimal(rule.getNum()).multiply(rule.getScore()));
				}
			}
		}
		
		return totalScore.getResult();
	}

	/**
	 * 发布校验
	 * 
	 * v1.0 zhanghc 2022年5月23日下午5:11:22
	 * @param paper
	 * @param ruleCache 
	 * @param chapterList 
	 * @param questionList 
	 * void
	 */
	private void publishValid(Paper paper, List<Question> questionList, List<PaperQuestion> chapterList, Map<Integer, List<PaperQuestionRule>> ruleCache) {
		if (paper.getState() == 0) {
			throw new MyException("已删除");
		}
		if (paper.getState() == 1) {
			throw new MyException("已发布");
		}
		if (paper.getState() == 3) {
			throw new MyException("已归档");
		}
		PaperType paperType = paperTypeService.getEntity(paper.getPaperTypeId());
		if(paperType.getCreateUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("无操作权限");
		}
		if (paper.getGenType() == 1) {//1：人工组卷；2：随机组卷 
			publishValidForMan(paper, questionList);
		} else if (paper.getGenType() == 2) {
			publishValidForRandom(paper, chapterList, ruleCache);
		}
	}

	private void publishValidForRandom(Paper paper, List<PaperQuestion> chapterList, Map<Integer, List<PaperQuestionRule>> ruleCache) {
		// 校验是否添加规则
		boolean hasRule = false;
		for (PaperQuestion chapter : chapterList) {
			if (ValidateUtil.isValid(ruleCache.get(chapter.getId()))) {
				hasRule = true;
				break;
			}
		}
		if (!hasRule) {
			throw new MyException("试卷内容为空");
		}
		
		// 校验客观题试卷是否包含主观题
		if (paper.getMarkType() == 1) {
			for (PaperQuestion chapter : chapterList) {
				for (PaperQuestionRule rule : ruleCache.get(chapter.getId())) {
					if (rule.getAis().contains("2")) {
						throw new MyException(String.format("章节【%s】下第【%s】个规则包含人工试题，发布失败", chapter.getName(), rule.getNo()));
					}
				}
			}
		}
		
		// 校验所有规则的试题数量是否满足
		Map<Integer, List<Question>> questionListCache = new HashMap<>();// 试题分类下所有的试题条件
		for (PaperQuestion chapter : chapterList) {
			for (PaperQuestionRule rule : ruleCache.get(chapter.getId())) {
				if (questionListCache.get(rule.getQuestionTypeId()) != null) {
					continue;
				}
				
				List<Question> questionList = questionService.getList(rule.getQuestionTypeId());
				ListIterator<Question> questionIterator = questionList.listIterator();
				while (questionIterator.hasNext()) {
					Question question = questionIterator.next();
					if (question.getState() != 1) {// 非发布的删除
						questionIterator.remove();
					}
				}
				
				questionListCache.put(rule.getQuestionTypeId(), questionList);
			}
		}
		for (PaperQuestion chapter : chapterList) {
			for (PaperQuestionRule rule : ruleCache.get(chapter.getId())) {
				int remainQuestionNum = rule.getNum();
				for (List<Question> questionList : questionListCache.values()) {
					if (remainQuestionNum <= 0) {// 下面的break只能跳出while循环，这里在校验一次
						break;
					}
					
					ListIterator<Question> questionIterator = questionList.listIterator();
					while (questionIterator.hasNext()) {
						Question question = questionIterator.next();
						if (rule.getType() == question.getType() 
								&& rule.getDifficultys().contains(question.getDifficulty().toString()) 
								&& rule.getAis().contains(question.getAi().toString())) {
							questionIterator.remove();
							remainQuestionNum--;
						}
						if (remainQuestionNum <= 0) {// 当前条件都满足
							break;
						}
					}
				}
				
				if (remainQuestionNum > 0) {// 当前条件不满足
					throw new MyException(String.format("章节【%s】下第【%s】个规则题数不足【%s】道", chapter.getName(), rule.getNo(), rule.getNum()));
				}
			}
		}
	}

	private void publishValidForMan(Paper paper, List<Question> questionList) {
		if (!ValidateUtil.isValid(questionList)) {
			throw new MyException("试卷内容为空");
		}
		
		if (paper.getMarkType() == 1) {//1：智能阅卷；2：人工阅卷；
			for (Question question : questionList) {
				if (question.getAi() != 1) {// 包含主观题
					throw new MyException(String.format("编号为【%s】的试题为阅卷题，发布失败", question.getId()));
				}
			}
		}
	}
	
	@Override
	public void totalScoreUpdate(Integer id) {
		// 校验数据有效性
		Paper paper = getEntity(id);
		if(paper.getState() == 0) {
			throw new MyException("已删除");
		}
		if (paper.getState() == 1) {
			throw new MyException("已发布");
		}
		if(paper.getState() == 3){
			throw new MyException("已归档");
		}
		PaperType paperType = paperTypeService.getEntity(paper.getPaperTypeId());
		if(paperType.getCreateUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("无操作权限");
		}
		
		// 更新总分数
		List<PaperQuestion> chapterList = null;
		Map<Integer, List<PaperQuestionRule>> ruleCache = null;
		if (paper.getGenType() == 2) {
			chapterList = paperQuestionService.getChapterList(paper.getId());
			ruleCache = new HashMap<>();
			for (PaperQuestion chapter : chapterList) {
				List<PaperQuestionRule> ruleList = paperQuestionRuleService.getList(chapter.getId());
				ruleCache.put(chapter.getId(), ruleList);
			}
		}
		
		paper.setTotalScore(publishCalculateTotalScore(paper, chapterList, ruleCache));
		update(paper);
	}

	@Override
	public void sxe(Integer id, Integer[] options) {
		// 校验数据有效性
		Paper paper = getEntity(id);
		if(paper.getState() == 0) {
			throw new MyException("已删除");
		}
		if (paper.getState() == 1) {
			throw new MyException("已发布");
		}
		if(paper.getState() == 3){
			throw new MyException("已归档");
		}
		if (paper.getGenType() == 2) {
			throw new MyException("随机试卷无效");
		}
		PaperType paperType = paperTypeService.getEntity(paper.getPaperTypeId());
		if(paperType.getCreateUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("无操作权限");
		}
		
		// 更新反作弊
		paper.setOptions(ValidateUtil.isValid(options) ? StringUtil.join(options) : null);
	}

	@Override
	public MyPaper getPaper(Integer id) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(id)) {
			throw new MyException("参数错误：id");
		}
		Paper paper = getEntity(id);
		if (paper.getGenType() != 1) {
			throw new MyException("参数错误：id");
		}
		
		// 组合成需要的格式
		List<PaperQuestion> chapterList = paperQuestionService.getChapterList(id);
		Map<Integer, Question> questionCache = getQuestionCache(id);
		Map<Integer, List<QuestionOption>> questionOptionCache = getQuestionOptionCache(id);
		Map<Integer, List<PaperQuestionAnswer>> paperQuestionAnswerCache = getPaperQuestionAnswerCache(id);
		
		return generatePaper(paper, chapterList, questionCache, questionOptionCache, paperQuestionAnswerCache);
	}
	
	@Override
	public MyPaper getPaperOfRand(Integer examId, Integer userId) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(examId)) {
			throw new MyException("参数错误：id");
		}
		Exam exam = examService.getEntity(examId);
		Paper paper = getEntity(exam.getPaperId());
		if (paper.getGenType() != 2) {
			throw new MyException("参数错误：examId");
		}
		
		// 组合成需要的格式
		List<PaperQuestion> chapterList = paperQuestionService.getChapterList(paper.getId());
		Map<Integer, Question> questionCache = getQuestionCache(examId, userId);
		Map<Integer, List<QuestionOption>> questionOptionCache = getQuestionOptionCache(examId, userId);
		Map<Integer, List<PaperQuestionAnswer>> paperQuestionAnswerCache = getPaperQuestionAnswerCache(examId, userId);
		return generatePaperOfRand(exam, paper, userId, chapterList, questionCache, questionOptionCache, paperQuestionAnswerCache);
	}

	private MyPaper generatePaperOfRand(Exam exam, Paper paper, Integer userId, List<PaperQuestion> chapterList,
			Map<Integer, Question> questionCache, Map<Integer, List<QuestionOption>> questionOptionCache,
			Map<Integer, List<PaperQuestionAnswer>> paperQuestionAnswerCache) {
		MyPaper myPaper = new MyPaper();
		List<PaperQuestion> chapterDetailList = paperQuestionService.getList(exam.getId(), userId);// 这里找到是所有的，包含章节
		for (PaperQuestion chapter : chapterList) {
			Chapter _chapter = new Chapter(chapter);
			myPaper.getChapterList().add(_chapter);
			for (PaperQuestion questionAttr : chapterDetailList) {
				if (questionAttr.getParentId().intValue() != chapter.getId().intValue()) {// 过滤掉非当前章节的
					continue;
				}
				MyQuestion myQuestion = new MyQuestion(
						questionCache.get(questionAttr.getQuestionId()), 
						questionOptionCache.get(questionAttr.getQuestionId()),
						paperQuestionAnswerCache.get(questionAttr.getQuestionId()),
						questionAttr );
				_chapter.getMyQuestionList().add(myQuestion);
			}
		}
		return myPaper;
	}

	private MyPaper generatePaper(Paper paper, List<PaperQuestion> chapterList, Map<Integer, Question> questionCache,
			Map<Integer, List<QuestionOption>> questionOptionCache,
			Map<Integer, List<PaperQuestionAnswer>> paperQuestionAnswerCache) {
		MyPaper myPaper = new MyPaper();
		for (PaperQuestion chapter : chapterList) {
			Chapter _chapter = new Chapter(chapter);
			myPaper.getChapterList().add(_chapter);
			
			List<PaperQuestion> chapterDetailList = paperQuestionService.getChapterDetailList(chapter.getId());
			for (PaperQuestion questionAttr : chapterDetailList) {
				MyQuestion myQuestion = new MyQuestion(
						questionCache.get(questionAttr.getQuestionId()), 
						questionOptionCache.get(questionAttr.getQuestionId()),
						paperQuestionAnswerCache.get(questionAttr.getQuestionId()),
						questionAttr );
				_chapter.getMyQuestionList().add(myQuestion);
			}
		}
		return myPaper;
	}

	private Map<Integer, Question> getQuestionCache(Integer id) {
		List<Question> questionList = paperDao.getQuestionList(id);
		Map<Integer, Question> questionCache = new HashMap<>();
		for (Question question : questionList) {
			questionCache.put(question.getId(), question);
		}
		return questionCache;
	}
	
	private Map<Integer, Question> getQuestionCache(Integer examId, Integer userId) {
		List<Question> questionList = paperDao.getQuestionList(examId, userId);
		Map<Integer, Question> questionCache = new HashMap<>();
		for (Question question : questionList) {
			questionCache.put(question.getId(), question);
		}
		return questionCache;
	}

	private Map<Integer, List<QuestionOption>> getQuestionOptionCache(Integer id) {
		List<QuestionOption> questionOptionList = paperDao.getQuestionOptionList(id);
		Map<Integer, List<QuestionOption>> questionOptionCache = new HashMap<>();
		for (QuestionOption questionOption : questionOptionList) {
			if (questionOptionCache.get(questionOption.getQuestionId()) == null) {
				questionOptionCache.put(questionOption.getQuestionId(), new ArrayList<>());
			}
			
			questionOptionCache.get(questionOption.getQuestionId()).add(questionOption);
		}
		
		return questionOptionCache;
	}
	
	private Map<Integer, List<QuestionOption>> getQuestionOptionCache(Integer examId, Integer userId) {
		List<QuestionOption> questionOptionList = paperDao.getQuestionOptionList(examId, userId);
		Map<Integer, List<QuestionOption>> questionOptionCache = new HashMap<>();
		for (QuestionOption questionOption : questionOptionList) {
			if (questionOptionCache.get(questionOption.getQuestionId()) == null) {
				questionOptionCache.put(questionOption.getQuestionId(), new ArrayList<>());
			}
			
			questionOptionCache.get(questionOption.getQuestionId()).add(questionOption);
		}
		
		return questionOptionCache;
	}

	private Map<Integer, List<PaperQuestionAnswer>> getPaperQuestionAnswerCache(Integer id) {
		List<PaperQuestionAnswer> paperQuestionAnswerList = paperQuestionAnswerService.getList(id);
		Map<Integer, List<PaperQuestionAnswer>> paperQuestionAnswerCache = new HashMap<>();
		for (PaperQuestionAnswer paperQuestionAnswer : paperQuestionAnswerList) {
			if (paperQuestionAnswerCache.get(paperQuestionAnswer.getQuestionId()) == null) {
				paperQuestionAnswerCache.put(paperQuestionAnswer.getQuestionId(), new ArrayList<>());
			}

			paperQuestionAnswerCache.get(paperQuestionAnswer.getQuestionId()).add(paperQuestionAnswer);
		}

		return paperQuestionAnswerCache;
	}
	
	private Map<Integer, List<PaperQuestionAnswer>> getPaperQuestionAnswerCache(Integer examId, Integer userId) {
		List<PaperQuestionAnswer> paperQuestionAnswerList = paperQuestionAnswerService.getList(examId, userId);
		Map<Integer, List<PaperQuestionAnswer>> paperQuestionAnswerCache = new HashMap<>();
		for (PaperQuestionAnswer paperQuestionAnswer : paperQuestionAnswerList) {
			if (paperQuestionAnswerCache.get(paperQuestionAnswer.getQuestionId()) == null) {
				paperQuestionAnswerCache.put(paperQuestionAnswer.getQuestionId(), new ArrayList<>());
			}
			
			paperQuestionAnswerCache.get(paperQuestionAnswer.getQuestionId()).add(paperQuestionAnswer);
		}
		
		return paperQuestionAnswerCache;
	}

	@Override
	public List<Question> getQuestionList(Integer id) {
		return paperDao.getQuestionList(id);
	}

	@Override
	public List<Question> getQuestionList(Integer examId, Integer userId) {
		return paperDao.getQuestionList(examId, userId);
	}

}
