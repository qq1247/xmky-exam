package com.wcpdoc.exam.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wcpdoc.exam.core.entity.Question;

/**
 * 试题工具类
 * 
 * v1.0 zhanghc 2022年5月20日上午9:20:36
 */
public class QuestionUtil {
	
	/**
	 * 是否主观题
	 * 
	 * v1.0 zhanghc 2020年10月13日下午7:40:37
	 * @param question
	 * @return boolean
	 */
	public static boolean hasSubjective(Question question) {
		return (question.getMarkType() == 2 && (question.getType() == 3 || question.getType() == 5));
	}
	
	/**
	 * 是否客观题
	 * 
	 * v1.0 zhanghc 2020年10月13日下午7:40:37
	 * @param question
	 * @return boolean
	 */
	public static boolean hasObjective(Question question) {
		return !hasSubjective(question);
	}
	
	/**
	 * 是否单选题
	 * 
	 * v1.0 zhanghc 2020年10月13日下午7:40:37
	 * @param question
	 * @return boolean
	 */
	public static boolean hasSingleChoice(Question question) {
		return question.getType() == 1;
	}
	
	/**
	 * 是否多选题
	 * 
	 * v1.0 zhanghc 2020年10月13日下午7:40:37
	 * @param question
	 * @return boolean
	 */
	public static boolean hasMultipleChoice(Question question) {
		return question.getType() == 2;
	}
	
	/**
	 * 是否填空题
	 * 
	 * v1.0 zhanghc 2020年10月13日下午7:40:37
	 * @param question
	 * @return boolean
	 */
	public static boolean hasFillBlank(Question question) {
		return question.getType() == 3;
	}

	/**
	 * 是否判断题
	 * 
	 * v1.0 zhanghc 2020年10月13日下午7:40:37
	 * @param question
	 * @return boolean
	 */
	public static boolean hasTrueFalse(Question question) {
		return question.getType() == 4;
	}

	/**
	 * 是否问答题
	 * 
	 * v1.0 zhanghc 2020年10月13日下午7:40:37
	 * @param question
	 * @return boolean
	 */
	public static boolean hasQA(Question question) {
		return question.getType() == 5;
	}
	
	/**
	 * 获取填空数量
	 * 
	 * v1.0 zhanghc 2022年9月21日上午9:58:25
	 * @param title
	 * @return int
	 */
	public static int getFillBlankNum(String title) {
		Matcher matcher = Pattern.compile("[_]{5,}").matcher(title);
		int fillBlankNum = 0;
		while(matcher.find()) {
			fillBlankNum++;
		}
		return fillBlankNum;
	}
}
