package com.wcpdoc.exam.core.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import javax.annotation.Resource;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.wcpdoc.exam.base.service.PostService;
import com.wcpdoc.exam.base.service.UserService;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.dao.QuestionDao;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionTypeService;
import com.wcpdoc.exam.core.service.WordServer;
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
	private PostService postService;

	@Override
	@Resource(name = "questionDaoImpl")
	public void setDao(BaseDao<Question> dao) {
		super.dao = dao;
	}
	
	@Override
	public void addAndUpdate(Question question) {
		// 添加试题
		question.setUpdateTime(new Date());
		question.setUpdateUserId(getCurUser().getId());
		question.setVer(1);// 默认版本为1
		add(question);

		question.setSrcId(question.getId());
		update(question);

		// 保存附件
		saveFile(question);
	}
	
	@Override
	public void updateAndUpdate(Question question, boolean newVer) {
		// // 如果有新版本标识，删除旧版本，生成新版本
		Question entity = getEntity(question.getId());
		if (newVer) {
			Question newQuestion = new Question();
			BeanUtils.copyProperties(entity, newQuestion);
			entity.setState(0);
			update(entity);
			
			// entity.setType(question.getType());//不允许修改类型
			newQuestion.setState(question.getState());
			newQuestion.setDifficulty(question.getDifficulty());
			newQuestion.setTitle(question.getTitle());
			newQuestion.setOptionA(question.getOptionA());
			newQuestion.setOptionB(question.getOptionB());
			newQuestion.setOptionC(question.getOptionC());
			newQuestion.setOptionD(question.getOptionD());
			newQuestion.setOptionE(question.getOptionE());
			newQuestion.setOptionF(question.getOptionF());
			newQuestion.setOptionG(question.getOptionG());
			newQuestion.setAnswer(question.getAnswer());
			newQuestion.setAnalysis(question.getAnalysis());
			newQuestion.setUpdateTime(new Date());
			newQuestion.setUpdateUserId(getCurUser().getId());
			newQuestion.setScore(question.getScore());
			newQuestion.setScoreOptions(question.getScoreOptions());
			newQuestion.setNo(question.getNo());
			newQuestion.setVer(entity.getVer() + 1);
			add(newQuestion);
			
			saveFile(newQuestion);// 保存附件
			return;
		}

		// 修改试题
		entity.setState(question.getState());
		entity.setDifficulty(question.getDifficulty());
		entity.setTitle(question.getTitle());
		entity.setOptionA(question.getOptionA());
		entity.setOptionB(question.getOptionB());
		entity.setOptionC(question.getOptionC());
		entity.setOptionD(question.getOptionD());
		entity.setOptionE(question.getOptionE());
		entity.setOptionF(question.getOptionF());
		entity.setOptionG(question.getOptionG());
		entity.setAnswer(question.getAnswer());
		entity.setAnalysis(question.getAnalysis());
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		entity.setScore(question.getScore());
		entity.setScoreOptions(question.getScoreOptions());
		entity.setNo(question.getNo());
		update(entity);

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
			if (ValidateUtil.isValid(question.getOptionA())) {
				fileIdList.addAll(html2FileIds(question.getOptionA()));
			}
			if (ValidateUtil.isValid(question.getOptionB())) {
				fileIdList.addAll(html2FileIds(question.getOptionB()));
			}
			if (ValidateUtil.isValid(question.getOptionC())) {
				fileIdList.addAll(html2FileIds(question.getOptionC()));
			}
			if (ValidateUtil.isValid(question.getOptionD())) {
				fileIdList.addAll(html2FileIds(question.getOptionD()));
			}
			if (ValidateUtil.isValid(question.getOptionE())) {
				fileIdList.addAll(html2FileIds(question.getOptionE()));
			}
			if (ValidateUtil.isValid(question.getOptionF())) {
				fileIdList.addAll(html2FileIds(question.getOptionF()));
			}
			if (ValidateUtil.isValid(question.getOptionG())) {
				fileIdList.addAll(html2FileIds(question.getOptionG()));
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
		if(!ValidateUtil.isValid(html)){
			return fileIdList;
		}
		
		Document document = Jsoup.parse(html);
		ListIterator<Element> videoListIterator = document.getElementsByTag("video").listIterator();
		while(videoListIterator.hasNext()) {
			Element next = videoListIterator.next();
			String url = next.attr("src");
			
			String[] params = url.split("\\?")[1].split("&");;
			for(String param : params){
				String[] kv = param.split("=");
				if(kv[0].equals("id")){
					fileIdList.add(Integer.parseInt(kv[1]));
				}
			}
		}
		
		ListIterator<Element> imgListIterator = document.getElementsByTag("img").listIterator();
		while(imgListIterator.hasNext()){
			Element next = imgListIterator.next();
			String url = next.attr("src");
			if(url.startsWith("data")){
				continue;//字符串式的图片
			}
			
			String[] params = url.split("\\?")[1].split("&");;
			for(String param : params){
				String[] kv = param.split("=");
				if(kv[0].equals("id")){
					fileIdList.add(Integer.parseInt(kv[1]));
				}
			}
		}
		return fileIdList;
	}

	@Override
	public void wordImp(MultipartFile file, Integer questionTypeId) {
		//校验数据有效性
		String extName = FilenameUtils.getExtension(file.getOriginalFilename());
		if(!"doc".equals(extName)){
			throw new MyException("允许的上传类型为：doc");
		}
		
		//解析文件
		WordServer wordServer = new WordServerImpl();
		InputStream inputStream = null;
		try {
			inputStream = file.getInputStream();
		} catch (IOException e) {
			IOUtils.closeQuietly(inputStream);
			throw new MyException("读取文件流异常！");
		}
		
		List<Question> questionList = wordServer.handle(inputStream);
		IOUtils.closeQuietly(inputStream);
		
		//添加试题
		for(Question question : questionList){
			question.setUpdateTime(new Date());
			question.setUpdateUserId(getCurUser().getId());
			question.setVer(1);
			question.setState(1);
			question.setQuestionTypeId(questionTypeId);
			question.setScore(BigDecimal.ONE);
			question.setNo(1);
			add(question);
			
			question.setSrcId(question.getId());
			update(question);
			
			//保存附件
			saveFile(question);
		}
	}
}
