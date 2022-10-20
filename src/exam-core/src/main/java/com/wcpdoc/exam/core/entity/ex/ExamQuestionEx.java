package com.wcpdoc.exam.core.entity.ex;

import com.wcpdoc.exam.core.entity.ExamQuestion;

/**
 * 考试试题实体扩展
 * 
 * v1.0 zhanghc 2022年9月15日下午1:11:08
 */
public class ExamQuestionEx extends ExamQuestion {
	private QuestionEx question;

	public QuestionEx getQuestion() {
		return question;
	}

	public void setQuestion(QuestionEx question) {
		this.question = question;
	}

}
