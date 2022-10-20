package com.wcpdoc.exam.core.entity.ex;

import java.math.BigDecimal;

import com.wcpdoc.exam.core.entity.Question;

/**
 * 试题实体扩展
 * 
 * v1.0 zhanghc 2022年9月15日下午1:11:08
 */
public class QuestionEx extends Question {
	private String[] options;
	private String[] answers;
	private BigDecimal[] answerScores;

	public String[] getOptions() {
		return options;
	}

	public void setOptions(String[] options) {
		this.options = options;
	}

	public String[] getAnswers() {
		return answers;
	}

	public void setAnswers(String[] answers) {
		this.answers = answers;
	}

	public BigDecimal[] getAnswerScores() {
		return answerScores;
	}

	public void setAnswerScores(BigDecimal[] answerScores) {
		this.answerScores = answerScores;
	}
}
