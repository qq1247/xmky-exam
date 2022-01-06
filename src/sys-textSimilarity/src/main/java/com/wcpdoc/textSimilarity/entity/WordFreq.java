package com.wcpdoc.textSimilarity.entity;

/**
 * 词频。
 * 
 * v1.0 wjj 2021年11月19日下午5:06:09
 */
public class WordFreq {

	private String word;
	private int freq;

	public WordFreq(String word, int freq) {
		this.word = word;
		this.freq = freq;
	}

	public String getWord() {
		return word;
	}

	public int getFreq() {
		return freq;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public void setFreq(int freq) {
		this.freq = freq;
	}

	@Override
	public String toString() {
		return "[" + word + " : " + freq + "]";
	}

	@Override
	public boolean equals(Object obj) {
		WordFreq wf = (WordFreq) obj;
		return this.getWord().equals(wf.getWord());
	}
}
