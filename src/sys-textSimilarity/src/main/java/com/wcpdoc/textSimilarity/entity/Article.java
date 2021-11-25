package com.wcpdoc.textSimilarity.entity;

import java.util.List;

import com.hankcs.hanlp.seg.common.Term;

/**
 * 文章实体
 * 
 * v1.0 wjj 2021年11月18日下午2:13:45
 */
public class Article {
	private String name;

	private String text;

	private List<Term> segmentList;
	private List<WordFreq> wordFreqList;

	public List<Term> getSegmentList() {
		return segmentList;
	}

	public void setSegmentList(List<Term> segmentList) {
		this.segmentList = segmentList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return text;
	}

	public List<WordFreq> getWordFreqList() {
		return wordFreqList;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setWordFreqList(List<WordFreq> wordFreqList) {
		this.wordFreqList = wordFreqList;
	}

}
