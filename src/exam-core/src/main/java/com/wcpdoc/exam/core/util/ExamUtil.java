package com.wcpdoc.exam.core.util;

import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.entity.Exam;

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
		return ValidateUtil.isValid(exam.getSxes()) && exam.getSxes().contains("1");
	}
	
	/**
	 * 是否试题选项乱序
	 * 
	 * v1.0 zhanghc 2022年6月23日上午10:31:39
	 * @param exam
	 * @return boolean
	 */
	public static boolean hasOptionRand(Exam exam) {
		return ValidateUtil.isValid(exam.getSxes()) && exam.getSxes().contains("2");
	}
}
