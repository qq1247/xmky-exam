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
import com.wcpdoc.core.util.ValidateUtil;

/**
 * 我的考试详细实体
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Entity
@Table(name = "EXM_MY_EXAM_DETAIL")
public class MyExamDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	@Column(name = "MY_EXAM_ID")
	private Integer myExamId;
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
	@Column(name = "QUESTION_SCORE")
	private BigDecimal questionScore;
	@Column(name = "ANSWER_FILE_ID")
	private Integer answerFileId;
	@Column(name = "QUESTION_NO")
	private Integer questionNo;
	@Column(name = "OPTION_NO")
	private String optionNo;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getMyExamId() {
		return myExamId;
	}

	public void setMyExamId(Integer myExamId) {
		this.myExamId = myExamId;
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

	public BigDecimal getQuestionScore() {
		return questionScore;
	}

	public void setQuestionScore(BigDecimal questionScore) {
		this.questionScore = questionScore;
	}

	public Integer getAnswerFileId() {
		return answerFileId;
	}

	public void setAnswerFileId(Integer answerFileId) {
		this.answerFileId = answerFileId;
	}

	public Integer getQuestionNo() {
		return questionNo;
	}

	public void setQuestionNo(Integer questionNo) {
		this.questionNo = questionNo;
	}

	public String getOptionNo() {
		return optionNo;
	}
	
	public Integer[] getOptionNoArr() {
		if (!ValidateUtil.isValid(optionNo)) {
			return new Integer[0];
		}
		
		String[] optionNoStrArr = optionNo.split(",");// 接口层面需要返回数字类型
		Integer[] optionNoArr = new Integer[optionNoStrArr.length];
		for (int i = 0; i < optionNoStrArr.length; i++) {
			optionNoArr[i] = Integer.parseInt(optionNoStrArr[i]);
		}
		return optionNoArr;
	}

	public void setOptionNo(String optionNo) {
		this.optionNo = optionNo;
	}
}
