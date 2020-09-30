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
 * 考试实体
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
@Entity
@Table(name = "EXM_EXAM")
public class Exam {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	@Column(name = "NAME")
	private String name;
	@Column(name = "PASS_SCORE")
	private BigDecimal passScore;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "START_TIME")
	private Date startTime;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "END_TIME")
	private Date endTime;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "MARK_START_TIME")
	private Date markStartTime;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "MARK_END_TIME")
	private Date markEndTime;
	@Column(name = "GRADE_TYPE")
	private Integer gradeType;
	@Column(name = "SCORE_A")
	private BigDecimal scoreA;
	@Column(name = "SCORE_A_REMARK")
	private String scoreARemark;
	@Column(name = "SCORE_B")
	private BigDecimal scoreB;
	@Column(name = "SCORE_B_REMARK")
	private String scoreBRemark;
	@Column(name = "SCORE_C")
	private BigDecimal scoreC;
	@Column(name = "SCORE_C_REMARK")
	private String scoreCRemark;
	@Column(name = "SCORE_D")
	private BigDecimal scoreD;
	@Column(name = "SCORE_D_REMARK")
	private String scoreDRemark;
	@Column(name = "SCORE_E")
	private BigDecimal scoreE;
	@Column(name = "SCORE_E_REMARK")
	private String scoreERemark;
	@Column(name = "DESCRIPTION")
	private String description;
	@Column(name = "UPDATE_USER_ID")
	private Integer updateUserId;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "UPDATE_TIME")
	private Date updateTime;
	@Column(name = "STATE")
	private Integer state;
	@Column(name = "PAPER_ID")
	private Integer paperId;
	@Column(name = "EXAM_TYPE_ID")
	private Integer examTypeId;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BigDecimal getPassScore() {
		return passScore;
	}
	public void setPassScore(BigDecimal passScore) {
		this.passScore = passScore;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Date getMarkStartTime() {
		return markStartTime;
	}
	public void setMarkStartTime(Date markStartTime) {
		this.markStartTime = markStartTime;
	}
	public Date getMarkEndTime() {
		return markEndTime;
	}
	public void setMarkEndTime(Date markEndTime) {
		this.markEndTime = markEndTime;
	}
	public Integer getGradeType() {
		return gradeType;
	}
	public void setGradeType(Integer gradeType) {
		this.gradeType = gradeType;
	}
	public BigDecimal getScoreA() {
		return scoreA;
	}
	public void setScoreA(BigDecimal scoreA) {
		this.scoreA = scoreA;
	}
	public String getScoreARemark() {
		return scoreARemark;
	}
	public void setScoreARemark(String scoreARemark) {
		this.scoreARemark = scoreARemark;
	}
	public BigDecimal getScoreB() {
		return scoreB;
	}
	public void setScoreB(BigDecimal scoreB) {
		this.scoreB = scoreB;
	}
	public String getScoreBRemark() {
		return scoreBRemark;
	}
	public void setScoreBRemark(String scoreBRemark) {
		this.scoreBRemark = scoreBRemark;
	}
	public BigDecimal getScoreC() {
		return scoreC;
	}
	public void setScoreC(BigDecimal scoreC) {
		this.scoreC = scoreC;
	}
	public String getScoreCRemark() {
		return scoreCRemark;
	}
	public void setScoreCRemark(String scoreCRemark) {
		this.scoreCRemark = scoreCRemark;
	}
	public BigDecimal getScoreD() {
		return scoreD;
	}
	public void setScoreD(BigDecimal scoreD) {
		this.scoreD = scoreD;
	}
	public String getScoreDRemark() {
		return scoreDRemark;
	}
	public void setScoreDRemark(String scoreDRemark) {
		this.scoreDRemark = scoreDRemark;
	}
	public BigDecimal getScoreE() {
		return scoreE;
	}
	public void setScoreE(BigDecimal scoreE) {
		this.scoreE = scoreE;
	}
	public String getScoreERemark() {
		return scoreERemark;
	}
	public void setScoreERemark(String scoreERemark) {
		this.scoreERemark = scoreERemark;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public Integer getPaperId() {
		return paperId;
	}
	public void setPaperId(Integer paperId) {
		this.paperId = paperId;
	}
	public Integer getExamTypeId() {
		return examTypeId;
	}
	public void setExamTypeId(Integer examTypeId) {
		this.examTypeId = examTypeId;
	}
}
