package com.wcpdoc.exam.core.job;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.wcpdoc.core.util.SpringUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.service.QuestionAnswerService;
import com.wcpdoc.exam.core.service.QuestionOptionService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.file.service.FileService;

/**
 * 物理删除未引用试题
 * 
 * v1.0 chenyun 2021年10月13日下午2:08:45
 */
public class QuestionDelJob implements Job {
	private static final QuestionService questionService  = SpringUtil.getBean(QuestionService.class);
	private static final QuestionOptionService questionOptionService  = SpringUtil.getBean(QuestionOptionService.class);
	private static final QuestionAnswerService questionAnswerService = SpringUtil.getBean(QuestionAnswerService.class);
	private static final FileService fileService  = SpringUtil.getBean(FileService.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		for (Question question : questionService.delQuestionList()) {
			//删除试题
			List<Integer> delFileIds = html2FileIds(question.getTitle());
			delFileIds.addAll(html2FileIds(question.getAnalysis()));
			questionService.del(question.getId());
			//单选多选删除选择项
			if (question.getType() == 1 || question.getType() == 2) {
				List<QuestionOption> questionOptionList = questionOptionService.getList(question.getId());
				for (QuestionOption questionOption : questionOptionList) {
					delFileIds.addAll(html2FileIds(questionOption.getOptions()));
					questionOptionService.del(questionOption.getId());
				}
			}
			//删除答案
			List<QuestionAnswer> questionAnswerList = questionAnswerService.getList(question.getId());
			for(QuestionAnswer questionAnswer : questionAnswerList){
				delFileIds.addAll(html2FileIds(questionAnswer.getAnswer()));
				questionAnswerService.del(questionAnswer.getId());
			}
			//删除附件
			for(Integer id : delFileIds){
				fileService.del(id);
			}
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
}
