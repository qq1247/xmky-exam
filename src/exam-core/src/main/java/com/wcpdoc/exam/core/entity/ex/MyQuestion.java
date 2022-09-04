package com.wcpdoc.exam.core.entity.ex;

import java.util.ArrayList;
import java.util.List;

import com.wcpdoc.exam.core.entity.ExamQuestion;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionOption;

/**
 * 试题扩展
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
public class MyQuestion {
	private ExamQuestion attr = new ExamQuestion();
	private Question question = new Question();
	private List<ExamAnswerEx> answerList = new ArrayList<>();
	private List<QuestionOption> optionList = new ArrayList<>();

	public MyQuestion() {
	}

	public MyQuestion(Question question, List<QuestionOption> optionList, List<ExamAnswerEx> answerList, ExamQuestion attr) {
		this.question = question;
		this.attr = attr;
		this.answerList = answerList;
		this.optionList = optionList;
	}

	public ExamQuestion getAttr() {
		return attr;
	}

	public void setAttr(ExamQuestion attr) {
		this.attr = attr;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public List<ExamAnswerEx> getAnswerList() {
		return answerList;
	}

	public void setAnswerList(List<ExamAnswerEx> answerList) {
		this.answerList = answerList;
	}

	public List<QuestionOption> getOptionList() {
		return optionList;
	}

	public void setOptionList(List<QuestionOption> optionList) {
		this.optionList = optionList;
	}

}
