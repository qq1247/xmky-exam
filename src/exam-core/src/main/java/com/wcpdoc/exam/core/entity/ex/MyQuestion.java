package com.wcpdoc.exam.core.entity.ex;

import java.util.ArrayList;
import java.util.List;

import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.entity.PaperQuestionAnswer;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionOption;

/**
 * 试题扩展
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
public class MyQuestion {
	private PaperQuestion attr = new PaperQuestion();
	private Question question = new Question();
	private List<PaperQuestionAnswer> answerList = new ArrayList<>();
	private List<QuestionOption> optionList = new ArrayList<>();

	public MyQuestion() {
	}

	public MyQuestion(Question question, List<QuestionOption> optionList, List<PaperQuestionAnswer> answerList, PaperQuestion attr) {
		this.question = question;
		this.attr = attr;
		this.answerList = answerList;
		this.optionList = optionList;
	}

	public PaperQuestion getAttr() {
		return attr;
	}

	public void setAttr(PaperQuestion attr) {
		this.attr = attr;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public List<PaperQuestionAnswer> getAnswerList() {
		return answerList;
	}

	public void setAnswerList(List<PaperQuestionAnswer> answerList) {
		this.answerList = answerList;
	}

	public List<QuestionOption> getOptionList() {
		return optionList;
	}

	public void setOptionList(List<QuestionOption> optionList) {
		this.optionList = optionList;
	}

}
