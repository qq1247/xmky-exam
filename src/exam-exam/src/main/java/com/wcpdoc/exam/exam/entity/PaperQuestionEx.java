package com.wcpdoc.exam.exam.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 试卷试题扩展实体
 * 
 * v1.0 zhanghc 2017-05-26 14:23:38
 */
public class PaperQuestionEx extends PaperQuestion {
	private List<PaperQuestionEx> subList = new ArrayList<PaperQuestionEx>();
	private Question question;

	public List<PaperQuestionEx> getSubList() {
		return subList;
	}

	public void setSubList(List<PaperQuestionEx> subList) {
		this.subList = subList;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}
}
