package com.wcpdoc.exam.core.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.wcpdoc.exam.base.service.UserService;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.dao.QuestionDao;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionEx;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.QuestionOptionService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionTypeService;
import com.wcpdoc.exam.core.service.WordServer;
import com.wcpdoc.exam.core.util.StringUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;
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

	@Override
	@Resource(name = "questionDaoImpl")
	public void setDao(BaseDao<Question> dao) {
		super.dao = dao;
	}

	@Override
	public void addAndUpdate(Question question, String[] answers, String[] options) {
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
			// if (question.getType() == 3) {
			// String pattern = "(___)+";// 正则表达式
			// Pattern r = Pattern.compile(pattern);
			// Matcher m = r.matcher(question.getTitle());
			// int titleNumber = 0;
			// while (m.find()) {
			// titleNumber++;
			// }
			// String[] split = question.getAnswer().split("/n");
			// Integer answerNumber = split.length + 1;
			//
			// if (titleNumber != answerNumber) {
			// // throw new MyException("填空和答案个数不匹配！");
			// }
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
		if (question.getType() == 1 || question.getType() == 4 || question.getType() == 5) {
			question.setAnswer(answers[0]);
		} else if (question.getType() == 2) {
			question.setAnswer(StringUtil.join(answers));
		} else if (question.getQuestionTypeId() == 3) {
			question.setAnswer(StringUtil.join(answers, "\n"));
		}
		question.setUpdateTime(new Date());
		question.setUpdateUserId(getCurUser().getId());
		question.setVer(1);// 默认版本为1
		question.setState(2);// 默认禁用
		add(question);
		
		question.setSrcId(question.getId());
		update(question);

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
	public void updateAndUpdate(Question question, String[] answers, String[] options, boolean newVer) {
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
			// if (question.getType() == 3) {
			// String pattern = "(___)+";// 正则表达式
			// Pattern r = Pattern.compile(pattern);
			// Matcher m = r.matcher(question.getTitle());
			// int titleNumber = 0;
			// while (m.find()) {
			// titleNumber++;
			// }
			// String[] split = question.getAnswer().split("/n");
			// Integer answerNumber = split.length + 1;
			//
			// if (titleNumber != answerNumber) {
			// // throw new MyException("填空和答案个数不匹配！");
			// }
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
		if (question.getType() == 1 || question.getType() == 4 || question.getType() == 5) {
			question.setAnswer(answers[0]);
		} else if (question.getType() == 2) {
			question.setAnswer(StringUtil.join(answers));
		} else if (question.getType() == 3) {
			question.setAnswer(StringUtil.join(answers, "\n"));
		}
		
		Question entity = getEntity(question.getId());
		if (newVer) {
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
		}

		// 修改试题
		// entity.setState(question.getState());
		entity.setDifficulty(question.getDifficulty());
		entity.setTitle(question.getTitle());

		entity.setAnswer(question.getAnswer());
		entity.setAnalysis(question.getAnalysis());
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		entity.setScore(question.getScore());
		entity.setScoreOptions(question.getScoreOptions());
		entity.setNo(question.getNo());
		update(entity);

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
	public List<Question> getList(Integer questionTypeId) {
		return questionDao.getList(questionTypeId);
	}

	private void saveFile(Question question) {
		List<Integer> fileIdList = html2FileIds(question.getTitle());// 标题

		if (question.getType() == 1 || question.getType() == 2) {// 单选或多选
			QuestionOption entity = new QuestionOption();
			entity.setQuestionId(question.getId());

			if (question instanceof QuestionEx) {
				QuestionEx questionEx = (QuestionEx) question;
				for (QuestionOption questionOption : questionEx.getQuestionOptionList()) {
					fileIdList.addAll(html2FileIds(questionOption.getOptions()));
				}
			}
		} else if (question.getType() == 5) {// 问答
			fileIdList.addAll(html2FileIds(question.getAnswer()));
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
	public void wordImp(MultipartFile file, Integer questionTypeId) {
		// 校验数据有效性
		String extName = FilenameUtils.getExtension(file.getOriginalFilename());
		if (!"doc".equals(extName)) {
			throw new MyException("允许的上传类型为：doc");
		}

		// 解析文件
		WordServer wordServer = new WordServerImpl();
		InputStream inputStream = null;
		try {
			inputStream = file.getInputStream();
		} catch (IOException e) {
			IOUtils.closeQuietly(inputStream);
			throw new MyException("读取文件流异常！");
		}

		List<QuestionEx> questionExList = wordServer.handle(inputStream);
		IOUtils.closeQuietly(inputStream);

		// 添加试题
		for (QuestionEx questionEx : questionExList) {
			Question question = new Question();
			BeanUtils.copyProperties(questionEx, question);
			question.setUpdateTime(new Date());
			question.setUpdateUserId(getCurUser().getId());
			question.setVer(1);
			question.setState(2);// 默认禁用
			question.setQuestionTypeId(questionTypeId);
			question.setNo(1);
			add(question);

			question.setSrcId(question.getId());
			update(question);
			
			// 添加试题选项
			if (questionEx.getType() == 1 || questionEx.getType() == 2) {
				for (QuestionOption questionOption : questionEx.getQuestionOptionList()) {
					questionOption.setQuestionId(question.getId());
					questionOptionService.add(questionOption);
				}
			}

			// 保存附件
			saveFile(question);
		}
	}

	@Override
	public void move(Integer id, Integer sourceId, Integer targetId) {
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
}