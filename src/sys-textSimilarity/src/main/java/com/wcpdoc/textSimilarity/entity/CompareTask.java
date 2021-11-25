package com.wcpdoc.textSimilarity.entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 用于表示比较两篇文章的任务。设置了任务状态；可查看比较的结果。
 * 
 * v1.0 wjj 2021年11月19日下午5:05:39
 */
public class CompareTask {

	private Article a1;
	private Article a2;

	private boolean isFinished;

	public CompareTask(Article a1, Article a2) {
		this.a1 = a1;
		this.a2 = a2;
		this.isFinished = false;
	}

	public double execute() {
		// 取高频词前若干位
		List<WordFreq> freq1 = a1.getWordFreqList();
		List<WordFreq> freq2 = a2.getWordFreqList();
		// 获取高频词并集
		ArrayList<WordFreq> union = new ArrayList<>(freq1);
		union.addAll(freq2);
		// 生成向量
		ArrayList<WordFreq> v1 = new ArrayList<>(freq1);
		ArrayList<WordFreq> v2 = new ArrayList<>(freq2);
		for (WordFreq wf : union) {
			// 向量包含了高频词并集里的每个词，如果不包含则词频为0
			if (!v1.contains(wf)) {
				v1.add(new WordFreq(wf.getWord(), 0));
			}
			if (!v2.contains(wf)) {
				v2.add(new WordFreq(wf.getWord(), 0));
			}
		}
		// 根据词语排序以对齐向量，方便计算
		Comparator<WordFreq> strComp = Comparator.comparing(WordFreq::getWord);
		v1.sort(strComp);
		v2.sort(strComp);
		double vProduct = 0;
		int sumSquare1 = 0;
		int sumSquare2 = 0;
		for (int i = 0; i < v1.size(); ++i) {
			int num1 = v1.get(i).getFreq();
			int num2 = v2.get(i).getFreq();
			// 向量点积
			vProduct += num1 * num2;
			// 求向量模的过程
			sumSquare1 += num1 * num1;
			sumSquare2 += num2 * num2;
		}
		// 两向量模的乘积
		double normProduct = Math.sqrt(sumSquare1 * sumSquare2);
		// 点积除以模乘积
		double similarity = vProduct / normProduct;
		// 标记完成状态
		isFinished = true;
		return similarity;
	}

	public boolean isFinished() {
		return isFinished;
	}

}
