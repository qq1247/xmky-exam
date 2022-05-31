package com.wcpdoc.exam.core.entity.ex;

import java.util.ArrayList;
import java.util.List;

import com.wcpdoc.exam.core.entity.PaperQuestion;

/**
 * 章节实体扩展
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
public class Chapter {
	private PaperQuestion chapter;
	private List<MyQuestion> myQuestionList = new ArrayList<>();

	public Chapter() {
	}

	public Chapter(PaperQuestion chapter) {
		this.chapter = chapter;
	}

	public PaperQuestion getChapter() {
		return chapter;
	}

	public void setChapter(PaperQuestion chapter) {
		this.chapter = chapter;
	}

	public List<MyQuestion> getMyQuestionList() {
		return myQuestionList;
	}

	public void setMyQuestionList(List<MyQuestion> myQuestionList) {
		this.myQuestionList = myQuestionList;
	}
}
