package com.wcpdoc.exam.core.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 试卷试题答案实体
 * 
 * v1.0 chenyun 2021-07-20 18:14:32
 */
@Entity
@Table(name = "EXM_PAPER_QUESTION_ANSWER")
public class PaperQuestionAnswer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	@Column(name = "ANSWER")
	private String answer;
	@Column(name = "SCORE")
	private BigDecimal score;
	@Column(name = "PAPER_ID")
	private Integer paperId;
	@Column(name = "QUESTION_ID")
	private Integer questionId;
	@Column(name = "PAPER_QUESTION_ID")
	private Integer paperQuestionId;
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

	public Integer getPaperQuestionId() {
		return paperQuestionId;
	}

	public void setPaperQuestionId(Integer paperQuestionId) {
		this.paperQuestionId = paperQuestionId;
	}

	public Integer getPaperId() {
		return paperId;
	}

	public void setPaperId(Integer paperId) {
		this.paperId = paperId;
	}
}
