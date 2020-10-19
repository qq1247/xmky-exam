package com.wcpdoc.exam.core.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 考试用户试题实体
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Entity
@Table(name = "EXM_EXAM_USER_QUESTION")
public class ExamUserQuestion {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	@Column(name = "EXAM_USER_ID")
	private Integer examUserId;
	@Column(name = "EXAM_ID")
	private Integer examId;
	@Column(name = "USER_ID")
	private Integer userId;
	@Column(name = "QUESTION_ID")
	private Integer questionId;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "ANSWER_TIME")
	private Date answerTime;
	@Column(name = "MARK_USER_ID")
	private Integer markUserId;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "MARK_TIME")
	private Date markTime;
	@Column(name = "ANSWER")
	private String answer;
	@Column(name = "SCORE")
	private BigDecimal score;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getExamUserId() {
		return examUserId;
	}

	public void setExamUserId(Integer examUserId) {
		this.examUserId = examUserId;
	}

	public Integer getExamId() {
		return examId;
	}

	public void setExamId(Integer examId) {
		this.examId = examId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Integer questionId) {
		this.questionId = questionId;
	}

	public Date getAnswerTime() {
		return answerTime;
	}

	public void setAnswerTime(Date answerTime) {
		this.answerTime = answerTime;
	}

	public Integer getMarkUserId() {
		return markUserId;
	}

	public void setMarkUserId(Integer markUserId) {
		this.markUserId = markUserId;
	}

	public Date getMarkTime() {
		return markTime;
	}

	public void setMarkTime(Date markTime) {
		this.markTime = markTime;
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

}
