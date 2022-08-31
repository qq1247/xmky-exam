package com.wcpdoc.exam.core.entity.ex;

import java.util.ArrayList;
import java.util.List;

/**
 * 试卷
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
public class MyExamChapter {
	private List<Chapter> chapterList = new ArrayList<>();

	public List<Chapter> getChapterList() {
		return chapterList;
	}

	public void setChapterList(List<Chapter> chapterList) {
		this.chapterList = chapterList;
	}
}
