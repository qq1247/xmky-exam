package com.wcpdoc.exam.core.service.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.wcpdoc.exam.base.cache.ProgressBarCache;
import com.wcpdoc.exam.base.service.UserService;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.dao.QuestionDao;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.entity.QuestionEx;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.QuestionAnswerService;
import com.wcpdoc.exam.core.service.QuestionOptionService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionTypeService;
import com.wcpdoc.exam.core.service.WordServer;
import com.wcpdoc.exam.core.util.BigDecimalUtil;
import com.wcpdoc.exam.core.util.StringUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.file.entity.FileEx;
import com.wcpdoc.exam.file.service.FileService;

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
	@Value("${file.upload.dir}")
	private String fileUploadDir;

	@Override
	@Resource(name = "questionDaoImpl")
	public void setDao(BaseDao<Question> dao) {
		super.dao = dao;
	}

	@Override
	public void addAndUpdate(Question question, Integer[] scoreOptions, String[] answers, String[] options, BigDecimal[] scores) {
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
		if(!hasWriteAuth(question.getQuestionTypeId(), getCurUser().getId()) && !hasReadAuth(question.getQuestionTypeId(), getCurUser().getId())) {
			throw new MyException("权限不足！");
		}
		
		if (question.getType() == 1 && options != null) {
			if (options.length < 2) {
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
				throw new MyException("选项和答案不匹配！");
			}
		}
		if (question.getType() == 2 && options != null) {
			if (options.length < 2) {
				throw new MyException("参数错误：options长度小于2");
			}
			if (answers.length < 1) {
				throw new MyException("参数错误：answers");
			}
			for (int i = 0; i < answers.length; i++) {
				if (!"ABCDEFG".contains(answers[i].toUpperCase())) {
					throw new MyException("参数错误：answers");
				}
				int answerIndex = answers[i].getBytes()[0] - 65;
				if (options.length < answerIndex + 1) {
					throw new MyException("选项和答案不匹配！");
				}
			}
		}
		if (question.getType() == 3) {
			Pattern p = Pattern.compile("[_]{5,}"); //正则表达式
			Matcher m = p.matcher(question.getTitle()); // 获取 matcher 对象
			int count = 0;
			while(m.find()) {
				count++;
			}
			if (count != answers.length) {
				throw new MyException("填空答案不匹配！");
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
		question.setScoreOptions(ValidateUtil.isValid(scoreOptions) ? StringUtil.join(scoreOptions) : null);
		question.setCreateTime(new Date());
		question.setCreateUserId(getCurUser().getId());
		question.setUpdateTime(new Date());
		question.setUpdateUserId(getCurUser().getId());
		question.setVer(1);// 默认版本为1
		question.setState(2);// 默认禁用
		add(question);
		
		question.setSrcId(question.getId());
		update(question);

		BigDecimal total = new BigDecimal(0.00);
		//添加试题答案
		if (question.getType() == 1 || question.getType() == 4 ) {
			QuestionAnswer questionAnswer = new QuestionAnswer();
			questionAnswer.setAnswer(answers[0].toUpperCase());
			if (question.getAi() == 1 && scores != null) {				
				questionAnswer.setScore(scores[0]);
			}else{
				questionAnswer.setScore(new BigDecimal(0));
			}
			questionAnswer.setQuestionId(question.getId());
			questionAnswer.setNo(1);
			questionAnswerService.add(questionAnswer);
			total = scores[0];
		} else if (question.getType() == 2) {
			for(int i = 0; i < answers.length; i++ ){
				QuestionAnswer questionAnswer = new QuestionAnswer();
				questionAnswer.setAnswer(answers[i].toUpperCase());
				if (question.getAi() == 1 && scores != null) {
					questionAnswer.setScore(scores[0]);
				}else{
					questionAnswer.setScore(new BigDecimal(0));
				}
				questionAnswer.setQuestionId(question.getId());
				questionAnswer.setNo(i+1);
				questionAnswerService.add(questionAnswer);
				total = question.getScore();
			}
		} else if (question.getType() == 3 || question.getType() == 5) {
			for(int i = 0; i < answers.length; i++ ){
				QuestionAnswer questionAnswer = new QuestionAnswer();
				questionAnswer.setAnswer(answers[i]);
				if (question.getAi() == 1 && scores != null) {
					questionAnswer.setScore(scores[i]);
				}else{
					questionAnswer.setScore(new BigDecimal(0));
				}
				questionAnswer.setQuestionId(question.getId());
				questionAnswer.setNo(i+1);
				questionAnswerService.add(questionAnswer);
				total = total.add(scores[i]);
			}
		}
		if (question.getAi() == 1 &&  question.getScore().compareTo(total) != 0) {
			throw new MyException("答案总分有误！ ");
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
	public void updateAndUpdate(Question question, Integer[] scoreOptions, String[] answers, String[] options, BigDecimal[] scores) { //, boolean newVer
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
				throw new MyException("选项和答案不匹配！");
			}
		}
		if (question.getType() == 2) {
			if (options.length < 2) {
				throw new MyException("参数错误：options长度小于2");
			}
			if (answers.length < 1) {
				throw new MyException("参数错误：answers");
			}
			for (int i = 0; i < answers.length; i++) {
				if (!"ABCDEFG".contains(answers[i])) {
					throw new MyException("参数错误：answers");
				}
				int answerIndex = answers[i].getBytes()[0] - 65;
				if (options.length < answerIndex + 1) {
					throw new MyException("选项和答案不匹配！");
				}
			}
		}
		if (question.getType() == 3) {
			Pattern p = Pattern.compile("[_]{5,}"); //正则表达式
			Matcher m = p.matcher(question.getTitle()); // 获取 matcher 对象
			int count = 0;
			while(m.find()) {
				count++;
			}
			if (count != answers.length) {
				throw new MyException("填空答案不匹配！");
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
			throw new MyException("试题已发布不能修改！");
		}
		if (entity.getState() == 0) {
			throw new MyException("试题已删除不能修改！");
		}
		if(!hasWriteAuth(entity.getQuestionTypeId(), getCurUser().getId()) && !hasReadAuth(question.getQuestionTypeId(), getCurUser().getId())) {
			throw new MyException("权限不足！");
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
		entity.setAi(question.getAi());
		entity.setDifficulty(question.getDifficulty());
		entity.setTitle(question.getTitle());
		entity.setAnalysis(question.getAnalysis());
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		entity.setScore(question.getScore());
		entity.setScoreOptions(ValidateUtil.isValid(scoreOptions) ? StringUtil.join(scoreOptions) : null);
		entity.setNo(question.getNo());
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
			if (question.getAi() == 1 && scores != null) {
				questionAnswer.setScore(scores[0]);
			}else{
				questionAnswer.setScore(new BigDecimal(0));
			}
			questionAnswer.setQuestionId(entity.getId());
			questionAnswer.setNo(1);
			questionAnswerService.add(questionAnswer);
			total = scores[0];
		} else if (question.getType() == 2) {
			for(int i = 0; i < answers.length; i++ ){
				QuestionAnswer questionAnswer = new QuestionAnswer();
				questionAnswer.setAnswer(answers[i]);
				if (question.getAi() == 1 && scores != null) {
					questionAnswer.setScore(scores[0]);
				}else{
					questionAnswer.setScore(new BigDecimal(0));
				}
				questionAnswer.setQuestionId(entity.getId());
				questionAnswer.setNo(i+1);
				questionAnswerService.add(questionAnswer);
				total = question.getScore();
			}
		} else if (question.getType() == 3 || question.getType() == 5) {
			for(int i = 0; i < answers.length; i++ ){
				QuestionAnswer questionAnswer = new QuestionAnswer();
				questionAnswer.setAnswer(answers[i]);
				if (question.getAi() == 1 && scores != null) {
					questionAnswer.setScore(scores[i]);
				}else{
					questionAnswer.setScore(new BigDecimal(0));
				}
				questionAnswer.setQuestionId(entity.getId());
				questionAnswer.setNo(i+1);
				questionAnswerService.add(questionAnswer);
				total = total.add(scores[i]);
			}
		} 
		if (question.getAi() == 1 && question.getScore().compareTo(total) != 0) {
			throw new MyException("答案总分有误！ ");
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
	public void delAndUpdate(Integer id) {
		Question question = questionDao.getEntity(id);
		if(!hasWriteAuth(question.getQuestionTypeId(), getCurUser().getId()) && !hasReadAuth(question.getQuestionTypeId(), getCurUser().getId())) {
			throw new MyException("权限不足！");
		}
		question.setState(0);
		question.setUpdateTime(new Date());
		question.setUpdateUserId(getCurUser().getId());
		questionDao.update(question);
	}
	
	@Override
	public List<Question> getList(Integer questionTypeId) {
		return questionDao.getList(questionTypeId);
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
	public void wordImp(Integer fileId, Integer questionTypeId, String processBarId) {
		// 校验数据有效性
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
			questionExList = wordServer.handle(inputStream, fileUploadDir);
		} catch (IOException e) {
			throw new MyException("读取word时异常！");
		} catch (Exception e) {
			throw new MyException(e.getMessage());
		}

		// 添加试题
		ProgressBarCache.setProgressBar(processBarId, 8.0, 10.0, "保存开始", HttpStatus.OK.value());
		for (int  j = 0; j < questionExList.size(); j++) { //QuestionEx questionEx : questionExList
			Question question = questionExList.get(j).getQuestion();
			question.setCreateTime(new Date());
			question.setCreateUserId(getCurUser().getId());
			question.setUpdateTime(new Date());
			question.setUpdateUserId(getCurUser().getId());
			question.setVer(1);
			question.setState(2);// 默认禁用
			question.setQuestionTypeId(questionTypeId);
			
			String[] answers = new String[questionExList.get(j).getQuestionAnswerList().size()];
			BigDecimal[] scores = new BigDecimal[questionExList.get(j).getQuestionAnswerList().size()];
			BigDecimalUtil totalScore = BigDecimalUtil.newInstance(0);
			for (int i = 0; i < questionExList.get(j).getQuestionAnswerList().size(); i++) {
				answers[i] = questionExList.get(j).getQuestionAnswerList().get(i).getAnswer();
				if (question.getType() == 3 || (question.getType() == 5 && question.getAi() == 1)) {
					answers[i] = StringUtil.join(answers[i].split(" "), '\n');
				}
				scores[i] = questionExList.get(j).getQuestionAnswerList().get(i).getScore();
				totalScore.add(scores[i]);
			}
			if (question.getType() == 2) {// 多选特殊处理下，答案拆分
				answers = questionExList.get(j).getQuestionAnswerList().get(0).getAnswer().split("");
				scores = new BigDecimal[answers.length];
				for (int i = 0; i < scores.length; i++) {
					scores[i] = questionExList.get(j).getQuestionAnswerList().get(0).getScore();
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
			
			addAndUpdate(question, scoreOptions, answers, options, scores);
		}
		
		ProgressBarCache.setProgressBar(processBarId, 10.0, 10.0, "保存完成", HttpStatus.OK.value());
	}

	@Override
	public void move(Integer id, Integer sourceId, Integer targetId) {
		if (sourceId == null) {
			throw new MyException("参数错误：sourceId");
		}
		if(targetId == null){
			throw new MyException("参数错误：targetId");
		}
		QuestionType questionType = questionTypeService.getEntity(sourceId);
		if (questionType == null || questionType.getState() == 0 ){
			throw new MyException("此分类已被删除！");
		}
		QuestionType entity = questionTypeService.getEntity(targetId);
		if (entity == null || entity.getState() == 0) {
			throw new MyException("此分类已被删除！");
		}
		if (questionType.getCreateUserId() != getCurUser().getId()) {
			throw new MyException("权限不足！");
		}
		List<Question> list = questionDao.getList(sourceId);
		for (Question question : list) {
			question.setQuestionTypeId(targetId);
			update(question);
		}
	}

	@Override
	public Map<String, Object> statisticsTypeDifficulty(Integer questionTypeId) {
		List<Map<String, Object>> list = questionDao.statisticsTypeDifficulty(questionTypeId);
		DecimalFormat df = new DecimalFormat("0.0");
		for (Map<String, Object> map : list) {
			for (int i = 1; i <= 5; i++) {
				double T1 = Double.parseDouble(map.remove("TYPE"+i).toString()) / Double.parseDouble(map.get("TOTAL").toString());
				map.put("type"+i, df.format(T1 * 100));
				
				double D1 = Double.parseDouble(map.remove("DIFFICULTY"+i).toString()) / Double.parseDouble(map.get("TOTAL").toString());
				map.put("difficulty"+i, df.format(D1 * 100));
			}
			return map;
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> accuracy(Integer examId) {
		DecimalFormat df = new DecimalFormat("0.0");
		List<Map<String, Object>> accuracyList = questionDao.accuracy(examId);
		for (Map<String, Object> mapList : accuracyList) {
			mapList.put("questionId", mapList.remove("QUESTION_ID").toString());
			mapList.put("accuracy", df.format((Double.parseDouble(mapList.remove("CORRECT").toString())/ Double.parseDouble(mapList.get("TOTAL").toString()) * 100)));
		}
		return accuracyList;
	}

	@Override
	public PageOut randomListpage(PageIn pageIn) {
		return questionDao.randomListpage(pageIn);
	}

	@Override
	public void copy(Integer id) throws Exception{
		Question question = questionDao.getEntity(id);
		if(!hasWriteAuth(question.getQuestionTypeId(), getCurUser().getId()) && !hasReadAuth(question.getQuestionTypeId(), getCurUser().getId())) {
			throw new MyException("权限不足！");
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
	public void publish(Integer id) throws Exception {
		Question question = questionDao.getEntity(id);
		if (question.getState() == 0) {
			throw new MyException("试题已删除！");
		}
		if (question.getState() == 1) {
			throw new MyException("试题已发布！");
		}
		if(!hasWriteAuth(question.getQuestionTypeId(), getCurUser().getId()) && !hasReadAuth(question.getQuestionTypeId(), getCurUser().getId())) {
			throw new MyException("权限不足！");
		}
		if (question.getState() == 2) {
			question.setState(1);
		}

		question.setUpdateTime(new Date());
		question.setUpdateUserId(getCurUser().getId());
		questionDao.update(question);
	}
	
	private boolean hasWriteAuth(Integer questionTypeId, Integer userId) {
		QuestionType questionType = questionTypeService.getEntity(questionTypeId);
		return questionType.getWriteUserIds().contains(String.format(",%s,", userId));
	}
	private boolean hasReadAuth(Integer questionTypeId, Integer userId) {
		QuestionType questionType = questionTypeService.getEntity(questionTypeId);
		return questionType.getReadUserIds().contains(String.format(",%s,", userId));
	}
}