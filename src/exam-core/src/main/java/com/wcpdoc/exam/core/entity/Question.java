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
 * 试题实体
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
@Entity
@Table(name = "EXM_QUESTION")
public class Question {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	@Column(name = "TYPE")
	private Integer type;
	@Column(name = "DIFFICULTY")
	private Integer difficulty;
	@Column(name = "TITLE")
	private String title;
	@Column(name = "ANALYSIS")
	private String analysis;
	@Column(name = "STATE")
	private Integer state;
	@Column(name = "CREATE_USER_ID")
	private Integer createUserId;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "CREATE_TIME")
	private Date createTime;
	@Column(name = "UPDATE_USER_ID")
	private Integer updateUserId;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "UPDATE_TIME")
	private Date updateTime;
	@Column(name = "QUESTION_TYPE_ID")
	private Integer questionTypeId;
	@Column(name = "SCORE")
	private BigDecimal score;
	@Column(name = "AI")
	private Integer ai;
	@Column(name = "SCORE_OPTIONS")
	private String scoreOptions;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	/** 1：单选；2：多选；3：填空；4：判断；5：问答 */
	public Integer getType() {
		return type;
	}

	/** 1：单选；2：多选；3：填空；4：判断；5：问答 */
	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(Integer difficulty) {
		this.difficulty = difficulty;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAnalysis() {
		return analysis;
	}

	public void setAnalysis(String analysis) {
		this.analysis = analysis;
	}

	/** （0：删除；1：发布；2：草稿） */
	public Integer getState() {
		return state;
	}

	/** （0：删除；1：发布；2：草稿） */
	public void setState(Integer state) {
		this.state = state;
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

	public Integer getQuestionTypeId() {
		return questionTypeId;
	}

	public void setQuestionTypeId(Integer questionTypeId) {
		this.questionTypeId = questionTypeId;
	}

	public BigDecimal getScore() {
		return score;
	}

	public void setScore(BigDecimal score) {
		this.score = score;
	}

	public String getScoreOptions() {
		return scoreOptions;
	}

	public void setScoreOptions(String scoreOptions) {
		this.scoreOptions = scoreOptions;
	}

	public Integer getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(Integer createUserId) {
		this.createUserId = createUserId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/** 智能阅卷：1：是；2：否； */
	public Integer getAi() {
		return ai;
	}

	/** 智能阅卷：1：是；2：否； */
	public void setAi(Integer ai) {
		this.ai = ai;
	}
}
