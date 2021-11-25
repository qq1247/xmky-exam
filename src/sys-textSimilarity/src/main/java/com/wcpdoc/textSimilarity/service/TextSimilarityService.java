package com.wcpdoc.textSimilarity.service;
/**
 * 比较俩个文本的相似度服务层接口
 * 
 * v1.0 wjj 2021年11月23日上午9:58:10
 */
public interface TextSimilarityService {

	/**
	 * 获取俩个句子的相似度 （建议相似值为0.85）
	 * 
	 * v1.0 wjj 2021年11月23日上午9:58:10
	 * 
	 * @param sentence
	 * @param compareSentence
	 * @return double 
	 */
	double getSimilarity(String sentence, String compareSentence);

}