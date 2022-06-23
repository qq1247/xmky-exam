package com.wcpdoc.exam.core.util;

import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.entity.Paper;

/**
 * 试卷工具类
 * 
 * v1.0 zhanghc 2022年5月20日上午9:20:36
 */
public class PaperUtil {
	/**
	 * 是否试题乱序
	 * 
	 * v1.0 zhanghc 2022年6月23日上午10:31:16
	 * @param paper
	 * @return boolean
	 */
	public static boolean hasQuestionRand(Paper paper) {
		return ValidateUtil.isValid(paper.getOptions()) && paper.getOptions().contains("1");
	}
	
	/**
	 * 是否试题选项乱序
	 * 
	 * v1.0 zhanghc 2022年6月23日上午10:31:39
	 * @param paper
	 * @return boolean
	 */
	public static boolean hasOptionRand(Paper paper) {
		return ValidateUtil.isValid(paper.getOptions()) && paper.getOptions().contains("2");
	}
}
