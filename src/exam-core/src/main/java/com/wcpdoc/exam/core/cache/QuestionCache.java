package com.wcpdoc.exam.core.cache;

import java.util.List;

import org.springframework.cache.Cache;

import com.wcpdoc.cache.BaseEhCache;
import com.wcpdoc.core.util.SpringUtil;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.service.QuestionAnswerService;
import com.wcpdoc.exam.core.service.QuestionOptionService;
import com.wcpdoc.exam.core.service.QuestionService;

/**
 * 试题缓存（空闲2小时失效）
 * 
 * v1.0 zhanghc 2023年2月14日下午5:50:31
 */
public class QuestionCache extends BaseEhCache {
	private static final String CACHE_NAME = "QUESTION_CACHE";
	private static final String QUESTION_KEY_PRE = "QUESTION_";
	private static final String OPTION_KEY_PRE = "OPTION_";
	private static final String ANSWER_KEY_PRE = "ANSWER_";

	/**
	 * 获取试题
	 * 
	 * v1.0 zhanghc 2023年2月14日下午5:51:53
	 * @param id 试题ID
	 * @return Question 试题
	 */
	public static Question getQuestion(Integer id) {
		Cache cache = getCache(CACHE_NAME);
		String key = String.format("%s%s", QUESTION_KEY_PRE, id);

		Question question = cache.get(key, Question.class);
		if (question != null) {
			return question;
		}
		QuestionService questionService = SpringUtil.getBean(QuestionService.class);
		question = questionService.getEntity(id);
		if (question != null) {
			cache.put(key, question);
			return question;
		}
		return null;
	}
	
	/**
	 * 获取选项（单多选有效）
	 * 
	 * v1.0 zhanghc 2023年2月15日上午9:32:08
	 * @param id 试题ID
	 * @return QuestionOption 试题选项
	 */
	public static List<QuestionOption> getOption(Integer id) {
		Cache cache = getCache(CACHE_NAME);
		String key = String.format("%s%s", OPTION_KEY_PRE, id);

		@SuppressWarnings("unchecked")
		List<QuestionOption> questionOptionList = cache.get(key, List.class);
		if (questionOptionList != null) {
			return questionOptionList;
		}
		QuestionOptionService questionOptionService = SpringUtil.getBean(QuestionOptionService.class);
		questionOptionList = questionOptionService.getList(id);
		if (questionOptionList != null) {
			cache.put(key, questionOptionList);
			return questionOptionList;
		}
		return null;
	}
	
	/**
	 * 获取答案
	 * 
	 * v1.0 zhanghc 2023年2月15日上午9:38:12
	 * @param id 试题ID
	 * @return List<QuestionAnswer>
	 */
	public static List<QuestionAnswer> getAnswer(Integer id) {
		Cache cache = getCache(CACHE_NAME);
		String key = String.format("%s%s", ANSWER_KEY_PRE, id);

		@SuppressWarnings("unchecked")
		List<QuestionAnswer> questionAnswerList = cache.get(key, List.class);
		if (questionAnswerList != null) {
			return questionAnswerList;
		}
		QuestionAnswerService questionAnswerService = SpringUtil.getBean(QuestionAnswerService.class);
		questionAnswerList = questionAnswerService.getList(id);
		if (questionAnswerList != null) {
			cache.put(key, questionAnswerList);
			return questionAnswerList;
		}
		return null;
	}
	
	/**
	 * 清理缓存
	 * 
	 * v1.0 zhanghc 2023年3月24日上午10:32:12
	 * @param id void
	 */
	public static void clear(Integer id) {
		Cache cache = getCache(CACHE_NAME);
		String questionKey = String.format("%s%s", QUESTION_KEY_PRE, id);
		cache.evict(questionKey);
		
		String optionKey = String.format("%s%s", OPTION_KEY_PRE, id);
		cache.evict(optionKey);
		
		String answerKey = String.format("%s%s", ANSWER_KEY_PRE, id);
		cache.evict(answerKey);
	}
}
