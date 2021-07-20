package com.wcpdoc.exam.core.entity;

import java.util.List;

/**
 * 试题实体扩展
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
public class QuestionEx extends Question {
	private List<QuestionAnswer> questionAnswersList;
	private List<QuestionOption> questionOptionList;

	public List<QuestionOption> getQuestionOptionList() {
		return questionOptionList;
	}

	public void setQuestionOptionList(List<QuestionOption> questionOptionList) {
		this.questionOptionList = questionOptionList;
	}

	public List<QuestionAnswer> getQuestionAnswerList() {
		return questionAnswersList;
	}

	public void setQuestionAnswerList(List<QuestionAnswer> questionAnswersList) {
		this.questionAnswersList = questionAnswersList;
	}
}
