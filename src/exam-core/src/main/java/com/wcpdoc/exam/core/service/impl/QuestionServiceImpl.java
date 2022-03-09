package com.wcpdoc.exam.core.service.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.jsoup.safety.Whitelist;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.wcpdoc.base.cache.ParmCache;
import com.wcpdoc.base.cache.ProgressBarCache;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.BigDecimalUtil;
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.QuestionDao;
import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.entity.QuestionEx;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.service.PaperQuestionService;
import com.wcpdoc.exam.core.service.QuestionAnswerService;
import com.wcpdoc.exam.core.service.QuestionOptionService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionTypeService;
import com.wcpdoc.exam.core.service.WordServer;
import com.wcpdoc.file.entity.FileEx;
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
	private QuestionTypeService questionTypeService;
	@Resource
	private FileService fileService;
	@Resource
	private UserService userService;
	@Resource
	private QuestionOptionService questionOptionService;
	@Resource
	private QuestionAnswerService questionAnswerService;
	@Resource
	private PaperQuestionService paperQuestionService;
	
	
	@Override
	@Resource(name = "questionDaoImpl")
	public void setDao(BaseDao<Question> dao) {
		super.dao = dao;
	}

	@Override
	public void addAndUpdate(Question question, Integer[] scoreOptions, String[] options, String[] answers, BigDecimal[] answerScores) {
		// 校验数据有效性
		if (question.getType() == null) {
			throw new MyException("参数错误：type");
		}
		if (question.getDifficulty() == null) {
			throw new MyException("参数错误：difficulty");
		}
		if (!ValidateUtil.isValid(question.getTitle())) {
			throw new MyException("参数错误：title");
		}
		if (!ValidateUtil.isValid(answers)) {
			throw new MyException("参数错误：answers");
		}
		
		QuestionType questionType = questionTypeService.getEntity(question.getQuestionTypeId());
		if(!questionTypeService.hasWriteAuth(questionType, getCurUser().getId())) {
			throw new MyException("无操作权限");
		}
		
		if (question.getType() == 1 && options != null) {
			if (options.length < 1) {
				throw new MyException("参数错误：options长度小于2");
			}
			if (answers.length != 1) {
				throw new MyException("参数错误：answers");
			}
			if (!"ABCDEFG".contains(answers[0].toUpperCase())) {
				throw new MyException("参数错误：answer");
			}
			int answerIndex = answers[0].getBytes()[0] - 65;
			if (options.length < answerIndex + 1) {
				throw new MyException("选项和答案不匹配");
			}
		}
		if (question.getType() == 2 && options != null) {
			if (options.length < 2) {
				throw new MyException("参数错误：options长度小于2");
			}
			if (answers.length < 1) {
				throw new MyException("参数错误：answers");
			}
			if (scoreOptions == null || scoreOptions.length <= 0) {
				scoreOptions = new Integer[1];
				scoreOptions[0] = 1; //多选的漏选得分必填
			}
			if (answerScores.length == 0) {
				answerScores[0] = question.getScore().divide(new BigDecimal(2));//答案分数是分数的一半
			}
			for (int i = 0; i < answers.length; i++) {
				if (!"ABCDEFG".contains(answers[i].toUpperCase())) {
					throw new MyException("参数错误：answers");
				}
				int answerIndex = answers[i].getBytes()[0] - 65;
				if (options.length < answerIndex + 1) {
					throw new MyException("选项和答案不匹配");
				}
			}
		}
		if (question.getType() == 3) {
			Pattern p = Pattern.compile("[_]{5,}"); //正则表达式
			Matcher m = p.matcher(Jsoup.clean(question.getTitle(), Whitelist.none())); // 获取 matcher 对象
			int count = 0;
			while(m.find()) {
				count++;
			}
			if (count != answers.length) {
				throw new MyException("填空和答案数量不匹配");
			}
		}
		if (question.getType() == 4) {
			if (answers.length != 1) {
				throw new MyException("参数错误：answers");
			}

			if (!"对是√否错×".contains(answers[0])) {
				throw new MyException("参数错误：answers");
			}
		}
		
		// 添加试题
		if (question.getType() == 1 || question.getType() == 2 || question.getType() == 4) {
			question.setAi(1);
		}
		question.setScoreOptions(ValidateUtil.isValid(scoreOptions) ? StringUtil.join(scoreOptions) : null);
		question.setCreateTime(new Date());
		question.setCreateUserId(getCurUser().getId());
		question.setUpdateTime(new Date());
		question.setUpdateUserId(getCurUser().getId());
		//question.setState(2);// 默认禁用
		add(question);

		BigDecimal total = new BigDecimal(0.00);
		//添加试题答案
		if (question.getType() == 1 || question.getType() == 4 ) {
			QuestionAnswer questionAnswer = new QuestionAnswer();
			questionAnswer.setAnswer(answers[0].toUpperCase());
			if (question.getAi() == 1 && answerScores != null) {				
				questionAnswer.setScore(answerScores[0]);
			}else{
				questionAnswer.setScore(new BigDecimal(0));
			}
			questionAnswer.setQuestionId(question.getId());
			questionAnswer.setNo(1);
			questionAnswerService.add(questionAnswer);
			total = answerScores[0];
		} else if (question.getType() == 2) {
			total = question.getScore();
			QuestionAnswer questionAnswer = new QuestionAnswer();
			String answerString = "";
			for(int i = 0; i < answers.length; i++ ){
				if (answerString == "") {
					answerString = answers[i].toUpperCase();
				}else{
					answerString = answerString + "," + answers[i].toUpperCase();
				}
				
				if (question.getAi() == 1 && answerScores != null) {
					questionAnswer.setScore(answerScores[0]);
				}else{
					questionAnswer.setScore(new BigDecimal(0));
				}
			}
			
			questionAnswer.setAnswer(answerString);
			questionAnswer.setQuestionId(question.getId());
			questionAnswer.setNo(1);
			questionAnswerService.add(questionAnswer);
		} else if (question.getType() == 3 || question.getType() == 5) {
			for(int i = 0; i < answers.length; i++ ){
				QuestionAnswer questionAnswer = new QuestionAnswer();
				questionAnswer.setAnswer(answers[i]);
				if (question.getAi() == 1 && answerScores != null) {
					questionAnswer.setScore(answerScores[i]);
					total = total.add(answerScores[i]);
				}else{
					questionAnswer.setScore(new BigDecimal(0));
				}
				questionAnswer.setQuestionId(question.getId());
				questionAnswer.setNo(i+1);
				questionAnswerService.add(questionAnswer);
			}
		}
		if (question.getAi() == 1 &&  question.getScore().compareTo(total) != 0) {
			throw new MyException("答案总分有误 ");
		}
		
		// 添加选项
		if (question.getType() == 1 || question.getType() == 2) {
			for (int i = 0; i < options.length; i++) {
				QuestionOption questionOption = new QuestionOption();
				questionOption.setQuestionId(question.getId());
				questionOption.setOptions(options[i]);
				questionOption.setNo(i + 1);
				questionOptionService.add(questionOption);
			}
		}

		// 保存附件
		saveFile(question);
	}

	@Override
	public void updateAndUpdate(Question question, Integer[] scoreOptions, String[] options, String[] answers, BigDecimal[] answerScores) {
		// 校验数据有效性
		if (question.getType() == null) {
			throw new MyException("参数错误：type");
		}
		if (question.getDifficulty() == null) {
			throw new MyException("参数错误：difficulty");
		}
		if (!ValidateUtil.isValid(question.getTitle())) {
			throw new MyException("参数错误：title");
		}
		if (!ValidateUtil.isValid(answers)) {
			throw new MyException("参数错误：answers");
		}
		
		List<PaperQuestion> questionList = paperQuestionService.getQuestionList(question.getId(),null,null);//判断是否被试卷引用
		if (questionList.size() > 0) {
			throw new MyException("此试题已被试卷引用，无法修改");
		}
		
		if (question.getType() == 1) {
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
			if (options.length < answerIndex + 1) {
				throw new MyException("选项和答案不匹配");
			}
		}
		if (question.getType() == 2) {
			if (options.length < 2) {
				throw new MyException("参数错误：options长度小于2");
			}
			if (answers.length < 1) {
				throw new MyException("参数错误：answers");
			}
			if (scoreOptions.length <= 0) {
				scoreOptions[0] = 1; //多选的漏选得分必填
			}
			if (answerScores.length <= 0) {
				answerScores[0] = question.getScore().divide(new BigDecimal(2));//答案分数是分数的一半
			}
			for (int i = 0; i < answers.length; i++) {
				if (!"ABCDEFG".contains(answers[i])) {
					throw new MyException("参数错误：answers");
				}
				int answerIndex = answers[i].getBytes()[0] - 65;
				if (options.length < answerIndex + 1) {
					throw new MyException("选项和答案不匹配");
				}
			}
		}
		if (question.getType() == 3) {
			Pattern p = Pattern.compile("[_]{5,}"); //正则表达式
			Matcher m = p.matcher(Jsoup.clean(question.getTitle(), Whitelist.none()));// 获取 matcher 对象
			int count = 0;
			while(m.find()) {
				count++;
			}
			if (count != answers.length) {
				throw new MyException("填空和答案数量不匹配");
			}
		}
		if (question.getType() == 4) {
			if (answers.length != 1) {
				throw new MyException("参数错误：answers");
			}

			if (!"对是√否错×".contains(answers[0])) {
				throw new MyException("参数错误：answers");
			}
		}
		
		// 如果有新版本标识，删除旧版本，生成新版本		
		Question entity = getEntity(question.getId());
		if (entity.getState() == 1) {
			throw new MyException("试题已发布不能修改");
		}
		if (entity.getState() == 0) {
			throw new MyException("试题已删除不能修改");
		}
		
		QuestionType questionType = questionTypeService.getEntity(entity.getQuestionTypeId());
		if(!questionTypeService.hasWriteAuth(questionType, getCurUser().getId())) {
			throw new MyException("无操作权限");
		}
		/*if (newVer) {
			// 删除旧版本
			Question newQuestion = new Question();
			BeanUtils.copyProperties(entity, newQuestion);
			entity.setState(0);
			update(entity);

			// 生成新版本
			// newQuestion.setState(question.getState());
			newQuestion.setDifficulty(question.getDifficulty());
			newQuestion.setTitle(question.getTitle());
			newQuestion.setAnswer(question.getAnswer());
			newQuestion.setAnalysis(question.getAnalysis());
			newQuestion.setUpdateTime(new Date());
			newQuestion.setUpdateUserId(getCurUser().getId());
			newQuestion.setScore(question.getScore());
			newQuestion.setScoreOptions(question.getScoreOptions());
			newQuestion.setNo(question.getNo());
			newQuestion.setVer(entity.getVer() + 1);
			add(newQuestion);

			if (question.getType() == 1 || question.getType() == 2) {
				List<QuestionOption> questionOptionList = questionOptionService.getList(entity.getId());
				for (QuestionOption questionOption : questionOptionList) {
					questionOptionService.del(questionOption.getId());
				}

				for (int i = 0; i < options.length; i++) {
					QuestionOption questionOption = new QuestionOption();
					questionOption.setQuestionId(entity.getId());
					questionOption.setOptions(options[i]);
					questionOption.setNo(i + 1);
					questionOptionService.add(questionOption);
				}
			}

			// 保存附件
			saveFile(newQuestion);
			return;
		}*/

		// 修改试题
		// entity.setState(question.getState());
		if (question.getType() == 3 || question.getType() == 5 ) {
			entity.setAi(question.getAi());
		}
		
		entity.setState(question.getState());
		entity.setDifficulty(question.getDifficulty());
		entity.setTitle(question.getTitle());
		entity.setAnalysis(question.getAnalysis());
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		entity.setScore(question.getScore());
		entity.setScoreOptions(ValidateUtil.isValid(scoreOptions) ? StringUtil.join(scoreOptions) : null);
		update(entity);

		//修改试题答案
		List<QuestionAnswer> list = questionAnswerService.getList(entity.getId());
		for(QuestionAnswer questionAnswer : list){
			questionAnswerService.updateAndDel(questionAnswer.getId());
		}
		
		BigDecimal total = new BigDecimal(0.00);
		if (question.getType() == 1 || question.getType() == 4 ) {
			QuestionAnswer questionAnswer = new QuestionAnswer();
			questionAnswer.setAnswer(answers[0]);
			if (question.getAi() == 1 && answerScores != null) {
				questionAnswer.setScore(answerScores[0]);
			}else{
				questionAnswer.setScore(new BigDecimal(0));
			}
			questionAnswer.setQuestionId(entity.getId());
			questionAnswer.setNo(1);
			questionAnswerService.add(questionAnswer);
			total = answerScores[0];
		} else if (question.getType() == 2) {
			total = question.getScore();
			QuestionAnswer questionAnswer = new QuestionAnswer();
			String answerString = "";
			for(int i = 0; i < answers.length; i++ ){
				if (answerString == "") {
					answerString = answers[i].toUpperCase();
				}else{
					answerString = answerString + "," + answers[i].toUpperCase();
				}
				
				if (question.getAi() == 1 && answerScores != null) {
					questionAnswer.setScore(answerScores[0]);
				}else{
					questionAnswer.setScore(new BigDecimal(0));
				}
			}
			
			questionAnswer.setAnswer(answerString);
			questionAnswer.setQuestionId(question.getId());
			questionAnswer.setNo(1);
			questionAnswerService.add(questionAnswer);
			/*for(int i = 0; i < answers.length; i++ ){
				QuestionAnswer questionAnswer = new QuestionAnswer();
				questionAnswer.setAnswer(answers[i]);
				if (question.getAi() == 1 && answerScores != null) {
					questionAnswer.setScore(answerScores[0]);
				}else{
					questionAnswer.setScore(new BigDecimal(0));
				}
				questionAnswer.setQuestionId(entity.getId());
				questionAnswer.setNo(i+1);
				questionAnswerService.add(questionAnswer);
				total = question.getScore();
			}*/
		} else if (question.getType() == 3 || question.getType() == 5) {
			for(int i = 0; i < answers.length; i++ ){
				QuestionAnswer questionAnswer = new QuestionAnswer();
				questionAnswer.setAnswer(answers[i]);
				if (question.getAi() == 1 && answerScores != null) {
					questionAnswer.setScore(answerScores[i]);
					total = total.add(answerScores[i]);
				}else{
					questionAnswer.setScore(new BigDecimal(0));
				}
				questionAnswer.setQuestionId(entity.getId());
				questionAnswer.setNo(i+1);
				questionAnswerService.add(questionAnswer);
			}
		} 
		if (question.getAi() == 1 && question.getScore().compareTo(total) != 0) {
			throw new MyException("答案总分有误 ");
		}
		
		// 修改选项
		if (question.getType() == 1 || question.getType() == 2) {
			List<QuestionOption> questionOptionList = questionOptionService.getList(entity.getId());
			for (QuestionOption questionOption : questionOptionList) {
				questionOptionService.del(questionOption.getId());
			}

			for (int i = 0; i < options.length; i++) {
				QuestionOption questionOption = new QuestionOption();
				questionOption.setQuestionId(entity.getId());
				questionOption.setOptions(options[i]);
				questionOption.setNo(i + 1);
				questionOptionService.add(questionOption);
			}
		}

		// 保存附件
		saveFile(entity);
	}

	@Override
	public void delAndUpdate(Integer questionTypeId, Integer[] ids) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(questionTypeId) && !ValidateUtil.isValid(ids)) {// 不能同时无效
			throw new MyException("参数错误：questionTypeId");
		}
		if (ValidateUtil.isValid(questionTypeId) && ValidateUtil.isValid(ids)) {// 不能同时有效
			throw new MyException("参数错误：questionTypeId");
		}
		List<Question> questionList = new ArrayList<>();
		if (ValidateUtil.isValid(questionTypeId)) {// 如果是按类型删除
			QuestionType questionType = questionTypeService.getEntity(questionTypeId);
			if(!questionTypeService.hasWriteAuth(questionType, getCurUser().getId())) {
				throw new MyException("无操作权限");
			}
			questionList = questionDao.getList(questionTypeId);
		}
		
		if (ValidateUtil.isValid(ids)) {// 如果是按ID删除
			Map<Integer, QuestionType> questionTypeCache = new HashMap<>();
			for (Integer id : ids) {
				Question question = getEntity(id);
				if (question.getState() == 0) {
					throw new MyException(String.format("试题已删除：%s", id));
				}
				
				QuestionType questionType = questionTypeCache.get(question.getQuestionTypeId());
				if (questionType == null) {
					questionType = questionTypeService.getEntity(question.getQuestionTypeId());
					questionTypeCache.put(questionType.getId(), questionType);
				}
				
				if(!questionTypeService.hasWriteAuth(questionType, getCurUser().getId())) {
					throw new MyException("无操作权限");
				}
				questionList.add(question);
			}
		}
		
		// 删除试题
		for (Question question : questionList) {// 逻辑删除
			question.setState(0);
			question.setUpdateTime(new Date());
			question.setUpdateUserId(getCurUser().getId());
			update(question);
		}
	}
	
	private void saveFile(Question question) {
		List<Integer> fileIdList = html2FileIds(question.getTitle());// 标题

		if (question.getType() == 1 || question.getType() == 2) {// 单选或多选
			QuestionOption entity = new QuestionOption();
			entity.setQuestionId(question.getId());
		} else if (question.getType() == 5) {// 问答
			List<QuestionAnswer> list = questionAnswerService.getList(question.getId());
		    StringBuilder answerString = new StringBuilder();    
		    for (int i = 0; i < list.size(); i++) {                
		    	answerString.append(list.get(i).getAnswer()).append("/n");
		    }
			fileIdList.addAll(html2FileIds(answerString.toString().substring(0,answerString.toString().length()-1)));
		}

		fileIdList.addAll(html2FileIds(question.getAnalysis()));// 解析

		for (Integer fileId : fileIdList) {
			fileService.doUpload(fileId);
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
			;
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
	public void wordImp(Integer fileId, Integer questionTypeId, String processBarId, Integer state) {
		// 校验数据有效性
		if(!questionTypeService.hasWriteAuth(questionTypeService.getEntity(questionTypeId), getCurUser().getId())) {
			throw new MyException("无操作权限");
		}
		
		if (fileId == null) {
			throw new MyException("参数错误：fileId");
		}
		if (questionTypeId == null) {
			throw new MyException("参数错误：questionTypeId");
		}
		FileEx fileEx = fileService.getFileEx(fileId);
		if (!"docx".equals(fileEx.getEntity().getExtName())) {
			throw new MyException("允许的上传类型为：docx");
		}

		// 解析文件
		ProgressBarCache.setProgressBar(processBarId, 3.0, 10.0, "解析开始", HttpStatus.OK.value());// 主要时间消耗在docx4j解析，进度条只能假模拟
		WordServer wordServer = new WordServerImpl();
		List<QuestionEx> questionExList = null;
		try (InputStream inputStream = new FileInputStream(fileEx.getFile())) {
			questionExList = wordServer.handle(inputStream, ParmCache.get().getFileUploadDir());  //questionExList = wordServer.handle(inputStream, fileUploadDir);
		} catch (IOException e) {
			throw new MyException("读取word时异常");
		} catch (Exception e) {
			throw e;
		}

		// 添加试题
		ProgressBarCache.setProgressBar(processBarId, 8.0, 10.0, "保存开始", HttpStatus.OK.value());
		for (int  j = 0; j < questionExList.size(); j++) { //QuestionEx questionEx : questionExList
			Question question = questionExList.get(j).getQuestion();
			question.setCreateTime(new Date());
			question.setCreateUserId(getCurUser().getId());
			question.setUpdateTime(new Date());
			question.setUpdateUserId(getCurUser().getId());
			question.setState(state);// 页面添加决定是否发布
			question.setQuestionTypeId(questionTypeId);
			
			String[] answers = new String[questionExList.get(j).getQuestionAnswerList().size()];
			BigDecimal[] answerScores = new BigDecimal[questionExList.get(j).getQuestionAnswerList().size()];
			BigDecimalUtil totalScore = BigDecimalUtil.newInstance(0);
			for (int i = 0; i < questionExList.get(j).getQuestionAnswerList().size(); i++) {
				answers[i] = questionExList.get(j).getQuestionAnswerList().get(i).getAnswer();
				if (question.getType() == 3 || (question.getType() == 5 && question.getAi() == 1)) {
					answers[i] = StringUtil.join(answers[i].split(" "), '\n');
				}
				answerScores[i] = questionExList.get(j).getQuestionAnswerList().get(i).getScore();
				totalScore.add(answerScores[i]);
			}
			if (question.getType() == 2) {// 多选特殊处理下，答案拆分
				answers = questionExList.get(j).getQuestionAnswerList().get(0).getAnswer().split("");
				answerScores = new BigDecimal[answers.length];
				for (int i = 0; i < answerScores.length; i++) {
					answerScores[i] = questionExList.get(j).getQuestionAnswerList().get(0).getScore();
				}
			}
			
			String [] options = new String[questionExList.get(j).getQuestionOptionList().size()];
			for (int i = 0; i < questionExList.get(j).getQuestionOptionList().size(); i++) {
				options[i] = questionExList.get(j).getQuestionOptionList().get(i).getOptions();
			}
			
			if (question.getType() == 3 || (question.getType() == 5 && question.getAi() == 1)) {
				question.setScore(totalScore.getResult());
			}
			
			Integer[] scoreOptions = null;//new Integer[split.length];
			if (ValidateUtil.isValid(question.getScoreOptions())) {
				String[] split = question.getScoreOptions().split(",");
				scoreOptions = new Integer[split.length];
				for(int i = 0; i < split.length; i++ ){
					scoreOptions[i] = Integer.parseInt(split[i]);
				}
			} else {
				scoreOptions = new Integer[0];
			}
			
			addAndUpdate(question, scoreOptions, options, answers, answerScores);
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
		QuestionType source = questionTypeService.getEntity(sourceId);
		if (source.getState() == 0 ){
			throw new MyException("该分类已删除");
		}
		QuestionType target = questionTypeService.getEntity(targetId);
		if (target.getState() == 0) {
			throw new MyException("该分类已删除");
		}
		
		if (source.getCreateUserId().intValue() != getCurUser().getId().intValue()) {// 只能移动自己的分类
			throw new MyException("无操作权限");
		}
		if (!target.getWriteUserIds().contains(String.format(",%s,", getCurUser().getId()))) {// 只能移动到自己的分类或有组权限的分类
			throw new MyException("无操作权限");
		}
		
		// 移动
		List<Question> questionList = questionDao.getList(sourceId);
		for (Question question : questionList) {
			question.setQuestionTypeId(targetId);
			question.setUpdateTime(new Date());
			question.setUpdateUserId(getCurUser().getId());
			questionDao.update(question);
		}
	}

	@Override
	public void copy(Integer id) throws Exception{
		Question question = questionDao.getEntity(id);
		QuestionType questionType = questionTypeService.getEntity(question.getQuestionTypeId());
		if(!questionTypeService.hasWriteAuth(questionType, getCurUser().getId())) {
			throw new MyException("无操作权限");
		}
		
		Question entity = new Question();
		BeanUtils.copyProperties(entity, question);
		entity.setState(2);
		entity.setCreateTime(new Date());
		entity.setCreateUserId(getCurUser().getId());
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		questionDao.add(entity);
		
		List<QuestionAnswer> questionAnswerList = questionAnswerService.getList(question.getId());
		for(QuestionAnswer questionAnswer : questionAnswerList){
			QuestionAnswer questionAnswerNew = new QuestionAnswer();
			BeanUtils.copyProperties(questionAnswerNew, questionAnswer);
			questionAnswerNew.setQuestionId(entity.getId());
			questionAnswerService.add(questionAnswerNew);
		}
		
		List<QuestionOption> questionOptionList = questionOptionService.getList(question.getId());
		for (QuestionOption questionOption : questionOptionList) {
			QuestionOption questionOptionNew = new QuestionOption();
			BeanUtils.copyProperties(questionOptionNew, questionOption);
			questionOptionNew.setQuestionId(entity.getId());
			questionOptionService.add(questionOptionNew);
		}
	}
	

	@Override
	public void publish(Integer questionTypeId, Integer[] ids) throws Exception {
		// 校验数据有效性
		if (!ValidateUtil.isValid(questionTypeId) && !ValidateUtil.isValid(ids)) {// 不能同时无效
			throw new MyException("参数错误：questionTypeId");
		}
		if (ValidateUtil.isValid(questionTypeId) && ValidateUtil.isValid(ids)) {// 不能同时有效
			throw new MyException("参数错误：questionTypeId");
		}
		List<Question> questionList = new ArrayList<>();
		if (ValidateUtil.isValid(questionTypeId)) {// 如果是按类型删除
			QuestionType questionType = questionTypeService.getEntity(questionTypeId);
			if(!questionTypeService.hasWriteAuth(questionType, getCurUser().getId())) {
				throw new MyException("无操作权限");
			}
			questionList = questionDao.getList(questionTypeId);
		}
		
		if (ValidateUtil.isValid(ids)) {// 如果是按ID删除
			Map<Integer, QuestionType> questionTypeCache = new HashMap<>();
			for (Integer id : ids) {
				Question question = getEntity(id);
				if (question.getState() == 0) {
					throw new MyException(String.format("试题已删除：%s", id));
				}
				if (question.getState() == 1) {
					throw new MyException(String.format("试题已发布：%s", id));
				}
				
				QuestionType questionType = questionTypeCache.get(question.getQuestionTypeId());
				if (questionType == null) {
					questionType = questionTypeService.getEntity(question.getQuestionTypeId());
					questionTypeCache.put(questionType.getId(), questionType);
				}
				
				if(!questionTypeService.hasWriteAuth(questionType, getCurUser().getId())) {
					throw new MyException("无操作权限");
				}
				questionList.add(question);
			}
		}
		
		// 删除试题
		for (Question question : questionList) {// 逻辑删除
			question.setState(1);
			question.setUpdateTime(new Date());
			question.setUpdateUserId(getCurUser().getId());
			update(question);
		}
	}
}