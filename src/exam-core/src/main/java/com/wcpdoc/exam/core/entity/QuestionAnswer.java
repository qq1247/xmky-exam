package com.wcpdoc.exam.core.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 试题答案实体
 * 
 * v1.0 chenyun 2021-07-20 18:14:32
 */
@Entity
@Table(name = "EXM_QUESTION_ANSWER")
public class QuestionAnswer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	@Column(name = "ANSWER")
	private String answer;
	@Column(name = "SCORE")
	private BigDecimal score;
	@Column(name = "QUESTION_ID")
	private Integer questionId;
	@Column(name = "NO")
	private Integer no;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
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
	public Integer getQuestionId() {
		return questionId;
	}
	public void setQuestionId(Integer questionId) {
		this.questionId = questionId;
	}
	public Integer getNo() {
		return no;
	}
	public void setNo(Integer no) {
		this.no = no;
	}
	
	public String[] getAnswers(Integer type) {
		if (!ValidateUtil.isValid(answer)) {
			return new String[0];
		}
		
		if (type == 1 || type == 4 || type == 5) {
			return new String[] { answer };
		}
		if (type == 2) {
			return answer.split(",");
		}
		if (type == 3) {
			return answer.split("\n");
		}
		throw new MyException("getAnswers方法解析错误");
	}
	
	public String[] getAnswers(Integer type, String answer) {
		if (!ValidateUtil.isValid(answer)) {
			return new String[0];
		}
		
		if (type == 1 || type == 4 || type == 5) {
			return new String[] { answer };
		}
		if (type == 2) {
			return answer.split(",");
		}
		if (type == 3) {
			return answer.split("\n");
		}
		throw new MyException("getAnswers方法解析错误");
	}
}
