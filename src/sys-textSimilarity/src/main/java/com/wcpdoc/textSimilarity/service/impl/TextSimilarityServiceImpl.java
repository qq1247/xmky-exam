package com.wcpdoc.textSimilarity.service.impl;

import java.io.IOException;
import java.math.BigDecimal;

import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;

import com.wcpdoc.textSimilarity.service.TextSimilarityService;
import com.wcpdoc.textSimilarity.util.HanLPUtil;

/**
 * 比较俩个文本的相似度服务层实现层
 * 
 * v1.0 wjj 2021年11月23日上午10:10:54
 */
@Service
public class TextSimilarityServiceImpl implements TextSimilarityService {

	@Override
	public double getSimilarity(String sentence, String compareSentence) {
		try {
			double similarity = HanLPUtil.sentenceSimilarity(sentence, compareSentence);
			BigDecimal bg = new BigDecimal(similarity);
			double result = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();//四舍五入
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		} catch (TikaException e) {
			e.printStackTrace();
			return -1;
		}
	}

}
