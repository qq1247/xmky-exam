package com.wcpdoc.exam.core.util;

import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.entity.Question;

/**
 * 试题工具类
 * 
 * v1.0 zhanghc 2022年5月20日上午9:20:36
 */
public class QuestionUtil {
	/**
	 * 是否智能判卷
	 * 
	 * v1.0 zhanghc 2020年10月13日下午7:40:37
	 * @param question
	 * @return boolean
	 */
	public static boolean hasAi(Question question) {
		return question.getAi() == 1;
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
	 * 3：大小写不敏感（默认大小写敏感）；
	 * 
	 * v1.0 zhanghc 2018年11月14日下午11:07:02
	 * @param paperQuestionEx
	 * @return boolean
	 */
	public static boolean dxxbmg(PaperQuestion paperQuestion) {
		return ValidateUtil.isValid(paperQuestion.getAiOptions()) 
				&& paperQuestion.getAiOptions().contains("3");
	}
	
	/**
	 * 1：漏选得分（默认全对得分）
	 * 
	 * v1.0 zhanghc 2018年11月14日下午11:07:10
	 * @param paperQuestionEx
	 * @return boolean
	 */
	public static boolean lxdf(PaperQuestion paperQuestion) {
		return ValidateUtil.isValid(paperQuestion.getAiOptions()) 
				&& paperQuestion.getAiOptions().contains("1");
	}

	/**
	 * 2：答案无顺序（默认答案有前后顺序）
	 * 
	 * v1.0 zhanghc 2018年11月14日下午11:06:24
	 * @param paperQuestionEx
	 * @return boolean
	 */
	public static boolean dawsx(PaperQuestion paperQuestion) {
		return ValidateUtil.isValid(paperQuestion.getAiOptions()) 
				&& paperQuestion.getAiOptions().contains("2");
	}
}
