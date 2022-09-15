package com.wcpdoc.exam.core.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.BigDecimalUtil;
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.QuestionDao;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.service.QuestionAnswerService;
import com.wcpdoc.exam.core.service.QuestionExService;
import com.wcpdoc.exam.core.service.QuestionOptionService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionTypeService;
import com.wcpdoc.file.service.FileService;

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
	private QuestionExService questionExService;
	@Resource
	private QuestionTypeService questionTypeService;
	@Resource
	private FileService fileService;
	@Resource
	private QuestionOptionService questionOptionService;
	@Resource
	private QuestionAnswerService questionAnswerService;
	
	@Override
	@Resource(name = "questionDaoImpl")
	public void setDao(BaseDao<Question> dao) {
		super.dao = dao;
	}

	@Override
	public void addEx(Question question, Integer[] markOptions, String[] options, String[] answers, BigDecimal[] answerScores) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(question.getQuestionTypeId())) {
			throw new MyException("参数错误：questionTypeId");
		}
		QuestionType questionType = questionTypeService.getEntity(question.getQuestionTypeId());
		addAndUpdateValid(question, markOptions, options, answers, answerScores, questionType);
		
		// 添加试题
		question.setUpdateTime(new Date());
		question.setUpdateUserId(getCurUser().getId());
		question.setState(1);
		question.setMarkOptions(ValidateUtil.isValid(markOptions) ? StringUtil.join(markOptions) : null);
		add(question);

		//添加答案
		addQuestionAnswer(question, answers, answerScores);
		
		// 添加选项
		addQuestionOption(question, options);

		// 保存附件
		addQuestionFile(question, options);
	}

	@Override
	public void updateEx(Question question, Integer[] markOptions, String[] options, String[] answers, BigDecimal[] answerScores) {
		// 校验数据有效性
		Question entity = getEntity(question.getId());
		if (entity.getType() != question.getType()) {
			throw new MyException("参数错误：type");
		}
		if (entity.getState() == 0) {
			throw new MyException("参数错误：id");
		}
		QuestionType questionType = questionTypeService.getEntity(entity.getQuestionTypeId());
		addAndUpdateValid(question, markOptions, options, answers, answerScores, questionType);
		
		questionExService.updateAndUpdate(question);

		// 修改试题
		entity.setTitle(question.getTitle());
		entity.setMarkType(question.getMarkType());
		entity.setScore(question.getScore());
//		entity.setState(question.getState());
		entity.setAnalysis(question.getAnalysis());
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		entity.setMarkOptions(ValidateUtil.isValid(markOptions) ? StringUtil.join(markOptions) : null);
		update(entity);

		// 修改答案
		addQuestionAnswer(question, answers, answerScores);
		
		// 修改选项
		addQuestionOption(question, options);

		// 保存附件
		addQuestionFile(question, options);
	}

	@Override
	public void delEx(Integer[] ids) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(ids)) {
			throw new MyException("参数错误：ids");
		}
		List<Question> questionList = new ArrayList<>();
		Map<Integer, QuestionType> questionTypeCache = new HashMap<>();
		for (Integer id : ids) {
			Question question = getEntity(id);
			if (question.getState() == 0) {
				continue;
			}
			
			QuestionType questionType = questionTypeCache.get(question.getQuestionTypeId());
			if (questionType == null) {
				questionType = questionTypeService.getEntity(question.getQuestionTypeId());
				questionTypeCache.put(questionType.getId(), questionType);
			}
			if (questionType.getUpdateUserId().intValue() != getCurUser().getId().intValue()) {
				throw new MyException("无操作权限");
			}
			
			questionList.add(question);
		}
		
		// 删除试题
		for (Question question : questionList) {
			question.setState(0);
			question.setUpdateTime(new Date());
			question.setUpdateUserId(getCurUser().getId());
			update(question);
		}
	}
	
	private List<Integer> html2FileIds(String html) {
		List<Integer> fileIdList = new ArrayList<>();
		if (!ValidateUtil.isValid(html)) {
			return fileIdList;
		}

		Document document = Jsoup.parse(html);
		ListIterator<Element> videoListIterator = document.getElementsByTag("video").listIterator();
		while (videoListIterator.hasNext()) {
			Element next = videoListIterator.next();
			String url = next.attr("src");

			String[] params = url.split("\\?")[1].split("&");
			;
			for (String param : params) {
				String[] kv = param.split("=");
				if (kv[0].equals("id")) {
					fileIdList.add(Integer.parseInt(kv[1]));
				}
			}
		}

		ListIterator<Element> imgListIterator = document.getElementsByTag("img").listIterator();
		while (imgListIterator.hasNext()) {
			Element next = imgListIterator.next();
			String url = next.attr("src");
			if (url.startsWith("data")) {
				continue;// 字符串式的图片
			}

			String[] params = url.split("\\?")[1].split("&");
			for (String param : params) {
				String[] kv = param.split("=");
				if (kv[0].equals("id")) {
					fileIdList.add(Integer.parseInt(kv[1]));
				}
			}
		}
		return fileIdList;
	}

	@Override
	public void copy(Integer id) throws Exception{
		// 校验数据有效性
		Question question = getEntity(id);
		QuestionType questionType = questionTypeService.getEntity(question.getQuestionTypeId());
		if (questionType.getUpdateUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("无操作权限");
		}
		
		// 复制试题
		Question questionNew = new Question();
		BeanUtils.copyProperties(questionNew, question);
		//questionOfCopy.setState(2);
		questionNew.setUpdateTime(new Date());
		questionNew.setUpdateUserId(getCurUser().getId());
		List<Integer> fileIdList = html2FileIds(questionNew.getTitle());
		for (Integer fileId : fileIdList) {
			Integer copyFileId = fileService.copyFile(fileId);
			questionNew.setTitle(questionNew.getTitle().replace("/api/file/download?id="+fileId, "/api/file/download?id="+copyFileId));
		}
		fileIdList = html2FileIds(questionNew.getAnalysis());
		for (Integer fileId : fileIdList) {
			Integer copyFileId = fileService.copyFile(fileId);
			questionNew.setAnalysis(questionNew.getAnalysis().replace("/api/file/download?id="+fileId, "/api/file/download?id="+copyFileId));
		}
		add(questionNew);
		
		// 复制答案
		List<QuestionAnswer> questionAnswerList = questionAnswerService.getList(question.getId());
		for(QuestionAnswer questionAnswer : questionAnswerList){
			QuestionAnswer questionAnswerNew = new QuestionAnswer();
			BeanUtils.copyProperties(questionAnswerNew, questionAnswer);
			questionAnswerNew.setQuestionId(questionNew.getId());
			fileIdList = html2FileIds(questionAnswer.getAnswer());
			for (Integer fileId : fileIdList) {
				Integer copyFileId = fileService.copyFile(fileId);
				questionAnswer.setAnswer(questionAnswer.getAnswer().replace("/api/file/download?id="+fileId, "/api/file/download?id="+copyFileId));
			}
			questionAnswerService.add(questionAnswerNew);
		}
		
		// 复制选项
		List<QuestionOption> questionOptionList = questionOptionService.getList(question.getId());
		for (QuestionOption questionOption : questionOptionList) {
			QuestionOption questionOptionNew = new QuestionOption();
			BeanUtils.copyProperties(questionOptionNew, questionOption);
			questionOptionNew.setQuestionId(questionNew.getId());
			fileIdList = html2FileIds(questionOption.getOptions());
			for (Integer fileId : fileIdList) {
				Integer copyFileId = fileService.copyFile(fileId);
				questionOption.setOptions(questionOption.getOptions().replace("/api/file/download?id="+fileId, "/api/file/download?id="+copyFileId));
			}
			questionOptionService.add(questionOptionNew);
		}
	}
	
	private void addAndUpdateValid(Question question, Integer[] markOptions, String[] options, String[] answers, BigDecimal[] answerScores, QuestionType questionType) {
		if (questionType.getUpdateUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("无操作权限");
		}
		
		if (!ValidateUtil.isValid(question.getType())) {
			throw new MyException("参数错误：type");
		}
		if (!ValidateUtil.isValid(question.getTitle())) {
			throw new MyException("参数错误：title");
		}
		if (!ValidateUtil.isValid(answers)) {
			throw new MyException("参数错误：answers");
		}
		if (!ValidateUtil.isValid(question.getScore())) {
			throw new MyException("参数错误：score");
		}
		
		if (question.getType() == 1) {// 如果是单选
			if (question.getMarkType() != 1) {
				throw new MyException("参数错误：markType");
			}
			if (ValidateUtil.isValid(markOptions)) {
				throw new MyException("参数错误：markOptions");
			}
			if (!ValidateUtil.isValid(options)) {
				throw new MyException("参数错误：options");
			}
			if (options.length < 2) {
				throw new MyException("参数错误：options长度小于2");
			}
			if (answers.length != 1) {
				throw new MyException("参数错误：answers");
			}
			if (!"ABCDEFG".contains(answers[0])) {
				throw new MyException("参数错误：answer");
			}
			int answerIndex = answers[0].getBytes()[0] - 65;
			if (options.length < answerIndex + 1) {// 总共四个选项，答案是E就是有问题的
				throw new MyException("选项和答案不匹配");
			}
			if (ValidateUtil.isValid(answerScores)) {
				throw new MyException("参数错误：answerScores");
			}
		} else if (question.getType() == 2) {// 如果是多选
			if (question.getMarkType() != 1) {
				throw new MyException("参数错误：markType");
			}
			if (ValidateUtil.isValid(markOptions)) {
				throw new MyException("参数错误：markOptions");
			}
			if (!ValidateUtil.isValid(options)) {
				throw new MyException("参数错误：options");
			}
			if (options.length < 2) {
				throw new MyException("参数错误：options长度小于2");
			}
			if (answers.length < 2) { // 最少两个答案
				throw new MyException("参数错误：answers最少两个答案");
			}
			for (int i = 0; i < answers.length; i++) {
				if (!"ABCDEFG".contains(answers[i])) {
					throw new MyException("参数错误：answers");
				}
				int answerIndex = answers[i].getBytes()[0] - 65;
				if (options.length < answerIndex + 1) {// 总共四个选项，答案包含E就是有问题的
					throw new MyException("选项和答案不匹配");
				}
			}
			if (answerScores.length != 1) {
				throw new MyException("参数错误：answerScores");
			}
			if (BigDecimalUtil.newInstance(question.getScore()).sub(answerScores[0]).getResult().doubleValue() <= 0) {
				throw new MyException("漏选分数大于试题分数");
			}
		} else if (question.getType() == 3) {
			if (question.getMarkType() != 1 && question.getMarkType() != 2) {
				throw new MyException("参数错误：markType");
			}
			if (ValidateUtil.isValid(options)) {
				throw new MyException("参数错误：options");
			}
			Matcher matcher = Pattern.compile("[_]{5,}").matcher(question.getTitle());
			int fillBlankNum = 0;
			while(matcher.find()) {
				fillBlankNum++;
			}
			if (fillBlankNum == 0) {
				throw new MyException("最少1个填空");
			}
			if (fillBlankNum > 7) {
				throw new MyException("最多7个填空");
			}
			if (fillBlankNum != answers.length) {
				throw new MyException("填空和答案数量不匹配");
			}
				
			if (question.getMarkType() == 1) {
				if (ValidateUtil.isValid(markOptions)) {
					if (markOptions.length > 2) {
						throw new MyException("参数错误：scoreOption");
					}
					for (Integer markOption : markOptions) {
						if (markOption != 2 && markOption != 3) {//分值选项（2：答案无顺序；3：大小写不敏感；)
							throw new MyException("参数错误：scoreOption");
						}
					}
				}
				
				if (!ValidateUtil.isValid(answerScores)) {
					throw new MyException("参数错误：answerScores");
				}
				if (answerScores.length != answers.length) {
					throw new MyException("答案和分数不匹配");
				}
				
				BigDecimalUtil totalScore = BigDecimalUtil.newInstance(BigDecimal.ZERO);
				for (BigDecimal answerScore : answerScores) {
					totalScore.add(answerScore);
				}
				if (totalScore.sub(question.getScore()).getResult().doubleValue() != 0) {
					throw new MyException("单项分值合计和总分数不匹配");
				}
			} 
			if (question.getMarkType() == 2) {
				if (ValidateUtil.isValid(markOptions)) {
					throw new MyException("参数错误：scoreOption");
				}
				//if (ValidateUtil.isValid(answerScores)) {
				//	throw new MyException("参数错误：answerScores");
				//} // 主观填空允许有子分数
			}
		} else if (question.getType() == 4) {
			if (question.getMarkType() != 1) {
				throw new MyException("参数错误：markType");
			}
			if (ValidateUtil.isValid(markOptions)) {
				throw new MyException("参数错误：markOptions");
			}
			if (ValidateUtil.isValid(options)) {
				throw new MyException("参数错误：options");
			}
			if (answers.length != 1) {
				throw new MyException("参数错误：answers");
			}

			if (!"对是√否错×".contains(answers[0])) {
				throw new MyException("参数错误：answers");
			}
			
			if (ValidateUtil.isValid(answerScores)) {
				throw new MyException("参数错误：answerScores");
			}
		} else if (question.getType() == 5) {
			if (question.getMarkType() != 1 && question.getMarkType() != 2) {
				throw new MyException("参数错误：markType");
			}
			if (ValidateUtil.isValid(options)) {
				throw new MyException("参数错误：options");
			}
			if (question.getMarkType() == 1) {
				if (ValidateUtil.isValid(markOptions)) {
					if (markOptions.length != 1) {
						throw new MyException("参数错误：scoreOption");
					}
					for (Integer scoreOption : markOptions) {
						if (scoreOption != 3) {//分值选项（1：漏选得分；2：答案无顺序；3：大小写不敏感；)
							throw new MyException("参数错误：scoreOption");
						}
					}
				}
				
				if (!ValidateUtil.isValid(answerScores)) {
					throw new MyException("参数错误：answerScores");
				}
				if (answerScores.length != answers.length) {
					throw new MyException("答案和分数不匹配");
				}
				
				BigDecimalUtil totalScore = BigDecimalUtil.newInstance(BigDecimal.ZERO);
				for (BigDecimal answerScore : answerScores) {
					totalScore.add(answerScore);
				}
				if (totalScore.sub(question.getScore()).getResult().doubleValue() != 0) {
					throw new MyException("单项分值合计和总分数不匹配");
				}
			} 
			if (question.getMarkType() == 2) {
				if (ValidateUtil.isValid(markOptions)) {
					throw new MyException("参数错误：scoreOption");
				}
				if (ValidateUtil.isValid(answerScores)) {
					throw new MyException("参数错误：answerScores");
				}
			}
		}
	}
	
	private void addQuestionAnswer(Question question, String[] answers, BigDecimal[] answerScores) {
		List<QuestionAnswer> questionAnswerList = questionAnswerService.getList(question.getId());
		for(QuestionAnswer questionAnswer : questionAnswerList){
			questionAnswerService.del(questionAnswer.getId());
		}
		
		if (question.getType() == 1 || question.getType() == 4 || (question.getType() == 5 && question.getMarkType() == 2)) {
			QuestionAnswer questionAnswer = new QuestionAnswer();
			questionAnswer.setAnswer(answers[0]);
			questionAnswer.setScore(null);
			questionAnswer.setQuestionId(question.getId());
			questionAnswer.setNo(1);
			questionAnswerService.add(questionAnswer);
		} else if (question.getType() == 2) {
			QuestionAnswer questionAnswer = new QuestionAnswer();
			Arrays.sort(answers);// 页面前选b在选a，传值为ba，处理一下
			questionAnswer.setAnswer(StringUtil.join(answers));
			questionAnswer.setScore(answerScores[0]);
			questionAnswer.setQuestionId(question.getId());
			questionAnswer.setNo(1);
			questionAnswerService.add(questionAnswer);
		} else if (question.getType() == 3 || (question.getType() == 5 && question.getMarkType() == 1)) {
			for(int i = 0; i < answers.length; i++ ){
				QuestionAnswer questionAnswer = new QuestionAnswer();
				questionAnswer.setAnswer(answers[i]);
				questionAnswer.setScore(answerScores[i]);
				questionAnswer.setQuestionId(question.getId());
				questionAnswer.setNo(i + 1);
				questionAnswerService.add(questionAnswer);
			}
		}
	}
	
	private void addQuestionOption(Question question, String[] options) {
		List<QuestionOption> questionOptionList = questionOptionService.getList(question.getId());
		for (QuestionOption questionOption : questionOptionList) {
			questionOptionService.del(questionOption.getId());
		}
		if (question.getType() == 1 || question.getType() == 2) {
			for (int i = 0; i < options.length; i++) {
				QuestionOption questionOption = new QuestionOption();
				questionOption.setQuestionId(question.getId());
				questionOption.setOptions(options[i]);
				questionOption.setNo(i + 1);
				questionOptionService.add(questionOption);
			}
		}
	}

	private void addQuestionFile(Question question, String[] options) {
		List<Integer> fileIdList = html2FileIds(question.getTitle());
		fileIdList.addAll(html2FileIds(question.getAnalysis()));
		if (question.getType() == 1 || question.getType() == 2) {
			for (int i = 0; i < options.length; i++) {
				fileIdList.addAll(html2FileIds(options[i]));
			}
		}

		for (Integer fileId : fileIdList) {
			fileService.upload(fileId);
		}
	}
	
	@Override
	public List<Question> getListByDel() {
		return questionDao.getListByDel();
	}

	@Override
	public List<Question> getList(Integer questionTypeId) {
		return questionDao.getList(questionTypeId);
	}
}