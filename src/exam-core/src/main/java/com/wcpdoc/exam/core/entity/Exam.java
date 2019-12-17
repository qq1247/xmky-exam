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

import com.wcpdoc.exam.core.util.DateUtil;

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
	@Column(name = "DESCRIPTION")
	private String description;
	@Column(name = "START_TIME")
	@DateTimeFormat(pattern = DateUtil.FORMAT_DATE_TIME)
	private Date startTime;
	@Column(name = "END_TIME")
	@DateTimeFormat(pattern = DateUtil.FORMAT_DATE_TIME)
	private Date endTime;
	@Column(name = "MARK_START_TIME")
	@DateTimeFormat(pattern = DateUtil.FORMAT_DATE_TIME)
	private Date markStartTime;
	@Column(name = "MARK_END_TIME")
	@DateTimeFormat(pattern = DateUtil.FORMAT_DATE_TIME)
	private Date markEndTime;
	@Column(name = "UPDATE_USER_ID")
	private Integer updateUserId;
	@Column(name = "UPDATE_TIME")
	@DateTimeFormat(pattern = DateUtil.FORMAT_DATE_TIME)
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
}
