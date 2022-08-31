package com.wcpdoc.exam.core.entity.ex;

import java.math.BigDecimal;

/**
 * 考试答案扩展
 * 
 * v1.0 chenyun 2022年3月8日上午10:55:29
 */
public class ExamAnswerEx {
	private String answer;
	private BigDecimal score;
	
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public BigDecimal getScore() {
		return score;
	}
	public void setScore(BigDecimal score) {
		this.score = score;
	}
	
	public Object getAnswerArr(Integer questionType, Integer questionMarkType) {
		if (questionType == 3 || (questionType == 5 && questionMarkType == 1)) {
			return answer.split("\n");
		}
		if (questionType == 2) {
			return answer.split(",");
		}
		return new String[] { answer };
	}
}
