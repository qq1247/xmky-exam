package com.wcpdoc.exam.exam.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import com.wcpdoc.exam.core.util.DateUtil;

/**
 * 考试用户实体
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Entity
@Table(name = "EXM_EXAM_USER")
public class ExamUser {
	@Id
	@GeneratedValue(generator = "generator")
	@GenericGenerator(name = "generator", strategy = "identity")
	@Column(name = "ID")
	private Integer id;
	@Column(name = "EXAM_ID")
	private Integer examId;
	@Column(name = "USER_ID")
	private Integer userId;
	@Column(name = "UPDATE_USER_ID")
	private Integer updateUserId;
	@Column(name = "UPDATE_TIME")
	@DateTimeFormat(pattern = DateUtil.FORMAT_DATE_TIME)
	private Date updateTime;
	@Column(name = "UPDATE_MARK_USER_ID")
	private Integer updateMarkUserId;
	@Column(name = "UPDATE_MARK_TIME")
	@DateTimeFormat(pattern = DateUtil.FORMAT_DATE_TIME)
	private Date updateMarkTime;
	@Column(name = "TOTAL_SCORE")
	private BigDecimal totalScore;
	@Column(name = "STATE")
	private Integer state;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public Integer getUpdateMarkUserId() {
		return updateMarkUserId;
	}
	public void setUpdateMarkUserId(Integer updateMarkUserId) {
		this.updateMarkUserId = updateMarkUserId;
	}
	public Date getUpdateMarkTime() {
		return updateMarkTime;
	}
	public void setUpdateMarkTime(Date updateMarkTime) {
		this.updateMarkTime = updateMarkTime;
	}
	public BigDecimal getTotalScore() {
		return totalScore;
	}
	public void setTotalScore(BigDecimal totalScore) {
		this.totalScore = totalScore;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
}
