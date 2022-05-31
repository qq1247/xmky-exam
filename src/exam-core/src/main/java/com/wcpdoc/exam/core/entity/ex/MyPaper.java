package com.wcpdoc.exam.core.entity.ex;

import java.util.ArrayList;
import java.util.List;

import com.wcpdoc.exam.core.entity.Paper;

/**
 * 试卷
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
public class MyPaper {
	private Paper paper = new Paper();
	private List<Chapter> chapterList = new ArrayList<>();

	public Paper getPaper() {
		return paper;
	}

	public void setPaper(Paper paper) {
		this.paper = paper;
	}

	public List<Chapter> getChapterList() {
		return chapterList;
	}

	public void setChapterList(List<Chapter> chapterList) {
		this.chapterList = chapterList;
	}
}
