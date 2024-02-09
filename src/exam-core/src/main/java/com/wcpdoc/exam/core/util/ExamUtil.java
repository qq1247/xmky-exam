package com.wcpdoc.exam.core.util;

import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.ExamQuestion;
import com.wcpdoc.exam.core.entity.ExamRule;
import com.wcpdoc.exam.core.entity.MyQuestion;

/**
 * 考试工具类
 * 
 * v1.0 zhanghc 2022年5月20日上午9:20:36
 */
public class ExamUtil {
	/**
	 * 是否试题乱序
	 * 
	 * v1.0 zhanghc 2022年6月23日上午10:31:16
	 * @param exam
	 * @return boolean
	 */
	public static boolean hasQuestionRand(Exam exam) {
		return ValidateUtil.isValid(exam.getSxes()) && exam.getSxes().contains(1);
	}
	
	/**
	 * 是否试题选项乱序
	 * 
	 * v1.0 zhanghc 2022年6月23日上午10:31:39
	 * @param exam
	 * @return boolean
	 */
	public static boolean hasOptionRand(Exam exam) {
		return ValidateUtil.isValid(exam.getSxes()) && exam.getSxes().contains(2);
	}
	
	/**
	 * 是否章节
	 * 
	 * v1.0 zhanghc 2022年9月16日上午10:49:09
	 * @param examQuestion
	 * @return boolean
	 */
	public static boolean hasChapter(ExamQuestion examQuestion) {
		return examQuestion.getType() == 1;
	}
	
	/**
	 * 是否章节
	 * 
	 * v1.0 zhanghc 2022年9月16日上午10:49:09
	 * @param examQuestion
	 * @return boolean
	 */
	public static boolean hasChapter(MyQuestion myQuestion) {
		return myQuestion.getType() == 1;
	}
	
	/**
	 * 是否章节
	 * 
	 * v1.0 zhanghc 2022年9月16日上午10:49:09
	 * @param examQuestion
	 * @return boolean
	 */
	private static boolean hasChapter(ExamRule examRule) {
		return examRule.getType() == 1;
	}
	
	/**
	 * 是否试题
	 * 
	 * v1.0 zhanghc 2022年9月19日下午3:05:37
	 * @param examQuestion
	 * @return boolean
	 */
	public static boolean hasQuestion(ExamQuestion examQuestion) {
		return !hasChapter(examQuestion);
	}
	
	/**
	 * 是否试题
	 * 
	 * v1.0 zhanghc 2022年9月19日下午3:05:37
	 * @param examQuestion
	 * @return boolean
	 */
	public static boolean hasQuestion(ExamRule examRule) {
		return !hasChapter(examRule);
	}
	
	/**
	 * 是否试题
	 * 
	 * v1.0 zhanghc 2022年9月19日下午3:05:37
	 * @param examQuestion
	 * @return boolean
	 */
	public static boolean hasQuestion(MyQuestion myQuestion) {
		return !hasChapter(myQuestion);
	}
	
	/**
	 * 是否限时
	 * 
	 * v1.0 zhanghc 2024年2月5日下午5:15:35
	 * @param exam
	 * @return boolean
	 */
	public static boolean hasTimeLimit(Exam exam) {
		return exam.getLimitMinute() > 0;
	}
}
