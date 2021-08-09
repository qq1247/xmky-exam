package com.wcpdoc.exam.core.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 试题实体扩展
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
public class QuestionEx {
	private Question question = new Question();
	private List<QuestionAnswer> questionAnswerList = new ArrayList<>();
	private List<QuestionOption> questionOptionList = new ArrayList<>();

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public List<QuestionAnswer> getQuestionAnswerList() {
		return questionAnswerList;
	}

	public void setQuestionAnswerList(List<QuestionAnswer> questionAnswerList) {
		this.questionAnswerList = questionAnswerList;
	}

	public List<QuestionOption> getQuestionOptionList() {
		return questionOptionList;
	}

	public void setQuestionOptionList(List<QuestionOption> questionOptionList) {
		this.questionOptionList = questionOptionList;
	}

}
