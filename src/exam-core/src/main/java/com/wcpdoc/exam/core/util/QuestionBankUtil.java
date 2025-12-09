package com.wcpdoc.exam.core.util;

import com.wcpdoc.exam.core.entity.QuestionBank;

/**
 * 题库工具类
 * 
 * v1.0 zhanghc 2025年12月9日上午11:48:24
 */
public class QuestionBankUtil {
	/**
	 * 是否私有权限
	 * 
	 * v1.0 zhanghc 2025年12月9日上午11:55:28
	 * 
	 * @param questionBank
	 * @return boolean
	 */
	public static boolean hasPrivate(QuestionBank questionBank) {
		return questionBank.getShareAuth() == 1;
	}

	/**
	 * 是否有读权限
	 * 
	 * v1.0 zhanghc 2025年12月9日上午11:48:45
	 * 
	 * @param question
	 * @return boolean
	 */
	public static boolean hasRead(QuestionBank questionBank) {
		return questionBank.getShareAuth() == 2 || questionBank.getShareAuth() == 3;
	}

	/**
	 * 是否有写权限
	 * 
	 * v1.0 zhanghc 2025年12月9日上午11:49:56
	 * 
	 * @param questionBank
	 * @return boolean
	 */
	public static boolean hasWrite(QuestionBank questionBank) {
		return questionBank.getShareAuth() == 3;
	}
}
