package com.wcpdoc.exam.core.service.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.entity.ex.MyQuestion;
import com.wcpdoc.exam.core.service.PaperQuestionService;
import com.wcpdoc.exam.core.service.PaperService;
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
	@Resource
	private PaperService paperService;
	
	@Override
	@Resource(name = "questionDaoImpl")
	public void setDao(BaseDao<Question> dao) {
		super.dao = dao;
	}

	@Override
	public void addAndUpdate(Question question, Integer[] aiOptions, String[] options, String[] answers, BigDecimal[] answerScores) {
		// 校验数据有效性
		addAndUpdateValid(question, aiOptions, options, answers, answerScores);
		
		// 添加试题
		question.setCreateTime(new Date());
		question.setCreateUserId(getCurUser().getId());
		question.setUpdateTime(new Date());
		question.setUpdateUserId(getCurUser().getId());
		//question.setState(2);// 通过页面去控制添加的是草稿还是发布
		if (question.getType() == 1 || question.getType() == 4 ) {
			question.setAi(1);
			question.setAiOptions(null);
		} else if (question.getType() == 2) {
			question.setAi(1);
			question.setAiOptions("1");//多选的漏选得分必填。漏选分值页面控制
		} else if (question.getType() == 3 || question.getType() == 5 ) {
			question.setAiOptions(ValidateUtil.isValid(aiOptions) ? StringUtil.join(aiOptions) : null);
		}
		add(question);

		//添加答案
		if (question.getType() == 1 || question.getType() == 4 ) {
			QuestionAnswer questionAnswer = new QuestionAnswer();
			questionAnswer.setAnswer(answers[0]);
			questionAnswer.setScore(question.getScore());
			questionAnswer.setQuestionId(question.getId());
			questionAnswer.setQuestionType(question.getType());
			questionAnswer.setQuestionAi(question.getAi());
			questionAnswer.setNo(1);
			questionAnswerService.add(questionAnswer);
		} else if (question.getType() == 2) {
			QuestionAnswer questionAnswer = new QuestionAnswer();
			Arrays.sort(answers);// 页面前选b在选a，传值为ba，处理一下
			questionAnswer.setAnswer(StringUtil.join(answers));
			questionAnswer.setScore(answerScores[0]);
			questionAnswer.setQuestionId(question.getId());
			questionAnswer.setQuestionType(question.getType());
			questionAnswer.setQuestionAi(question.getAi());
			questionAnswer.setNo(1);
			questionAnswerService.add(questionAnswer);
		} else if (question.getType() == 3 || question.getType() == 5) {
			for(int i = 0; i < answers.length; i++ ){
				QuestionAnswer questionAnswer = new QuestionAnswer();
				questionAnswer.setAnswer(answers[i]);
				questionAnswer.setScore(question.getAi() == 1 ? answerScores[i] : BigDecimal.ZERO);
				questionAnswer.setQuestionId(question.getId());
				questionAnswer.setQuestionType(question.getType());
				questionAnswer.setQuestionAi(question.getAi());
				questionAnswer.setNo(i + 1);
				questionAnswerService.add(questionAnswer);
			}
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
	public void updateAndUpdate(Question question, Integer[] aiOptions, String[] options, String[] answers, BigDecimal[] answerScores) {
		// 校验数据有效性
		Question entity = getEntity(question.getId());
		question.setQuestionTypeId(entity.getQuestionTypeId());//校验用
		if (entity.getType() != question.getType()) {
			throw new MyException("参数错误：type");
		}
//		if (entity.getState() == 1) {// 未被引用可以修改
//			throw new MyException("试题已发布");
//		}
		if (entity.getState() == 0) {
			throw new MyException("已删除");
		}
		List<PaperQuestion> paperQuestionList = paperQuestionService.getList(question.getId());//判断是否被试卷引用
		if (ValidateUtil.isValid(paperQuestionList)) {
			Paper paper = paperService.getEntity(paperQuestionList.get(0).getPaperId());
			throw new MyException(String.format("该题已被【%s】试卷引用", paper.getName()));
		}
		
		addAndUpdateValid(question, aiOptions, options, answers, answerScores);

		// 修改试题
		entity.setTitle(question.getTitle());
		entity.setDifficulty(question.getDifficulty());
		entity.setAi(question.getAi());
		entity.setScore(question.getScore());
		entity.setState(question.getState());
		entity.setAnalysis(question.getAnalysis());
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		
		if (entity.getType() == 1 || entity.getType() == 4 ) {
			entity.setAi(1);
			entity.setAiOptions(null);
		} else if (question.getType() == 2) {
			entity.setAi(1);
			entity.setAiOptions("1");//多选的漏选得分必填。漏选分值页面控制
		} else if (question.getType() == 3 || question.getType() == 5 ) {
			entity.setAiOptions(ValidateUtil.isValid(aiOptions) ? StringUtil.join(aiOptions) : null);
		}
		update(entity);

		// 修改答案
		List<QuestionAnswer> list = questionAnswerService.getList(entity.getId());
		for(QuestionAnswer questionAnswer : list){
			questionAnswerService.del(questionAnswer.getId());
		}
		if (question.getType() == 1 || question.getType() == 4 ) {
			QuestionAnswer questionAnswer = new QuestionAnswer();
			questionAnswer.setAnswer(answers[0]);
			questionAnswer.setScore(question.getScore());
			questionAnswer.setQuestionId(question.getId());
			questionAnswer.setNo(1);
			questionAnswer.setQuestionType(question.getType());
			questionAnswer.setQuestionAi(question.getAi());
			questionAnswerService.add(questionAnswer);
		} else if (question.getType() == 2) {
			QuestionAnswer questionAnswer = new QuestionAnswer();
			Arrays.sort(answers);// 页面前选b在选a，传值为ba，处理一下
			questionAnswer.setAnswer(StringUtil.join(answers));
			questionAnswer.setScore(answerScores[0]);
			questionAnswer.setQuestionId(question.getId());
			questionAnswer.setNo(1);
			questionAnswer.setQuestionType(question.getType());
			questionAnswer.setQuestionAi(question.getAi());
			questionAnswerService.add(questionAnswer);
		} else if (question.getType() == 3 || question.getType() == 5) {
			for(int i = 0; i < answers.length; i++ ){
				QuestionAnswer questionAnswer = new QuestionAnswer();
				questionAnswer.setAnswer(answers[i]);
				questionAnswer.setScore(question.getAi() == 1 ? answerScores[i] : BigDecimal.ZERO);
				questionAnswer.setQuestionId(question.getId());
				questionAnswer.setNo(i + 1);
				questionAnswer.setQuestionType(question.getType());
				questionAnswer.setQuestionAi(question.getAi());
				questionAnswerService.add(questionAnswer);
			}
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
		List<MyQuestion> questionExList = null;
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
			
			String[] answers = new String[questionExList.get(j).getAnswerList().size()];
			BigDecimal[] answerScores = new BigDecimal[questionExList.get(j).getAnswerList().size()];
			BigDecimalUtil totalScore = BigDecimalUtil.newInstance(0);
			for (int i = 0; i < questionExList.get(j).getAnswerList().size(); i++) {
				answers[i] = questionExList.get(j).getAnswerList().get(i).getAnswer();
				if (question.getType() == 3 || (question.getType() == 5 && question.getAi() == 1)) {
					answers[i] = StringUtil.join(answers[i].split(" "), '\n');
				}
				answerScores[i] = questionExList.get(j).getAnswerList().get(i).getScore();
				totalScore.add(answerScores[i]);
			}
			if (question.getType() == 1 || question.getType() == 4 
					|| (question.getType() == 5 && question.getAi() == 2) || (question.getType() == 3 && question.getAi() == 2)) {
				answerScores = null;//添加试题严格校验后，不允许传入错误数据
			}
			if (question.getType() == 2) {// 多选特殊处理下，答案拆分
				answers = questionExList.get(j).getAnswerList().get(0).getAnswer().split("");
				answerScores = new BigDecimal[]{questionExList.get(j).getAnswerList().get(0).getScore()};
			}
			
			String [] options = new String[questionExList.get(j).getOptionList().size()];
			for (int i = 0; i < questionExList.get(j).getOptionList().size(); i++) {
				options[i] = questionExList.get(j).getOptionList().get(i).getOptions();
			}
			
			if (question.getType() == 3 || (question.getType() == 5 && question.getAi() == 1)) {
				question.setScore(totalScore.getResult());
			}
			
			Integer[] aiOptions = null;//new Integer[split.length];
			if (ValidateUtil.isValid(question.getAiOptions())) {
				String[] split = question.getAiOptions().split(",");
				aiOptions = new Integer[split.length];
				for(int i = 0; i < split.length; i++ ){
					aiOptions[i] = Integer.parseInt(split[i]);
				}
			} else {
				aiOptions = new Integer[0];
			}
			
			addAndUpdate(question, aiOptions, options, answers, answerScores);
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
		//修改标题图片
		List<Integer> fileIdList = html2FileIds(entity.getTitle());
		for (Integer fileId : fileIdList) {
			Integer copyFileId = fileService.copyFile(fileId);
			entity.setTitle(entity.getTitle().replace("/api/file/download?id="+fileId, "/api/file/download?id="+copyFileId));
		}
		//修改解释图片
		fileIdList = html2FileIds(entity.getAnalysis());
		for (Integer fileId : fileIdList) {
			Integer copyFileId = fileService.copyFile(fileId);
			entity.setAnalysis(entity.getAnalysis().replace("/api/file/download?id="+fileId, "/api/file/download?id="+copyFileId));
		}
		questionDao.add(entity);
		
		List<QuestionAnswer> questionAnswerList = questionAnswerService.getList(question.getId());
		for(QuestionAnswer questionAnswer : questionAnswerList){
			QuestionAnswer questionAnswerNew = new QuestionAnswer();
			BeanUtils.copyProperties(questionAnswerNew, questionAnswer);
			questionAnswerNew.setQuestionId(entity.getId());
			//修改答案图片
			fileIdList = html2FileIds(questionAnswer.getAnswer());
			for (Integer fileId : fileIdList) {
				Integer copyFileId = fileService.copyFile(fileId);
				questionAnswer.setAnswer(questionAnswer.getAnswer().replace("/api/file/download?id="+fileId, "/api/file/download?id="+copyFileId));
			}
			questionAnswerService.add(questionAnswerNew);
		}
		
		List<QuestionOption> questionOptionList = questionOptionService.getList(question.getId());
		for (QuestionOption questionOption : questionOptionList) {
			QuestionOption questionOptionNew = new QuestionOption();
			BeanUtils.copyProperties(questionOptionNew, questionOption);
			questionOptionNew.setQuestionId(entity.getId());
			//修改选项图片
			fileIdList = html2FileIds(questionOption.getOptions());
			for (Integer fileId : fileIdList) {
				Integer copyFileId = fileService.copyFile(fileId);
				questionOption.setOptions(questionOption.getOptions().replace("/api/file/download?id="+fileId, "/api/file/download?id="+copyFileId));
			}
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
	
	private void addAndUpdateValid(Question question, Integer[] aiOptions, String[] options, String[] answers, BigDecimal[] answerScores) {
		if (!ValidateUtil.isValid(question.getType())) {
			throw new MyException("参数错误：type");
		}
		if (!ValidateUtil.isValid(question.getDifficulty())) {
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
		
		if (question.getType() == 1) {// 如果是单选
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
			if (ValidateUtil.isValid(aiOptions)) {
				throw new MyException("参数错误：aiOptions");
			}
			if (ValidateUtil.isValid(answerScores)) {
				throw new MyException("参数错误：answerScores");
			}
		} else if (question.getType() == 2) {// 如果是多选
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
			
			if (ValidateUtil.isValid(aiOptions) && aiOptions[0] != 1) {
				throw new MyException("参数错误：aiOptions");
			}
		} else if (question.getType() == 3) {
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
				
			if (question.getAi() == 1) {
				if (ValidateUtil.isValid(aiOptions)) {
					if (aiOptions.length > 2) {
						throw new MyException("参数错误：scoreOption");
					}
					for (Integer aiOption : aiOptions) {
						if (aiOption != 2 && aiOption != 3) {//分值选项（2：答案无顺序；3：大小写不敏感；)
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
			if (question.getAi() == 2) {
				if (ValidateUtil.isValid(aiOptions)) {
					throw new MyException("参数错误：scoreOption");
				}
				if (ValidateUtil.isValid(answerScores)) {
					throw new MyException("参数错误：answerScores");
				}
			}
		} else if (question.getType() == 4) {
			if (answers.length != 1) {
				throw new MyException("参数错误：answers");
			}

			if (!"对是√否错×".contains(answers[0])) {
				throw new MyException("参数错误：answers");
			}
			
			if (ValidateUtil.isValid(aiOptions)) {
				throw new MyException("参数错误：aiOptions");
			}
			if (ValidateUtil.isValid(answerScores)) {
				throw new MyException("参数错误：answerScores");
			}
		} else if (question.getType() == 5) {
			if (question.getAi() == 1) {
				if (ValidateUtil.isValid(aiOptions)) {
					if (aiOptions.length != 1) {
						throw new MyException("参数错误：scoreOption");
					}
					for (Integer scoreOption : aiOptions) {
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
			if (question.getAi() == 2) {
				if (ValidateUtil.isValid(aiOptions)) {
					throw new MyException("参数错误：scoreOption");
				}
				if (ValidateUtil.isValid(answerScores)) {
					throw new MyException("参数错误：answerScores");
				}
			}
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