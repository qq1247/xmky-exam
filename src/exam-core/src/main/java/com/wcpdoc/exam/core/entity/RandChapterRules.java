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
 * 随机章节
 * 
 * v1.0 chenyun 2022年2月11日 09:48:21
 */
@Entity
@Table(name = "EXM_RAND_CHAPTER_RULES")
public class RandChapterRules {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	@Column(name = "UPDATE_USER_ID")
	private Integer updateUserId;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "UPDATE_TIME")
	private Date updateTime;
	@Column(name = "PAPER_ID")
	private Integer paperId;
	@Column(name = "QUESTION_TYPE_ID")
	private Integer questionTypeId;
	@Column(name = "TYPE")
	private Integer type;
	@Column(name = "DIFFICULTY")
	private Integer difficulty;
	@Column(name = "QUERY_SCORE")
	private BigDecimal queryScore;
	@Column(name = "AI")
	private Integer ai;
	@Column(name = "SCORE_OPTIONS")
	private String scoreOptions;
	@Column(name = "TOTAL_NUMBER")
	private Integer totalNumber;
	@Column(name = "SCORE")
	private BigDecimal score;
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
	public Integer getUpdateUserId() {
		return updateUserId;
	}
	public void setUpdateUserId(Integer updateUserId) {
		this.updateUserId = updateUserId;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public Integer getPaperId() {
		return paperId;
	}
	public void setPaperId(Integer paperId) {
		this.paperId = paperId;
	}
	public Integer getQuestionTypeId() {
		return questionTypeId;
	}
	public void setQuestionTypeId(Integer questionTypeId) {
		this.questionTypeId = questionTypeId;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getDifficulty() {
		return difficulty;
	}
	public void setDifficulty(Integer difficulty) {
		this.difficulty = difficulty;
	}
	public Integer getAi() {
		return ai;
	}
	public void setAi(Integer ai) {
		this.ai = ai;
	}
	public String getScoreOptions() {
		return scoreOptions;
	}
	public void setScoreOptions(String scoreOptions) {
		this.scoreOptions = scoreOptions;
	}
	public Integer getTotalNumber() {
		return totalNumber;
	}
	public void setTotalNumber(Integer totalNumber) {
		this.totalNumber = totalNumber;
	}
	public BigDecimal getScore() {
		return score;
	}
	public void setScore(BigDecimal score) {
		this.score = score;
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
	public BigDecimal getQueryScore() {
		return queryScore;
	}
	public void setQueryScore(BigDecimal queryScore) {
		this.queryScore = queryScore;
	}
}