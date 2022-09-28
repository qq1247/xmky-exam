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
	@Column(name = "TIME_TYPE")
	private Integer timeType;
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
	@Column(name = "MARK_STATE")
	private Integer markState;
	@Column(name = "SCORE_STATE")
	private Integer scoreState;
	@Column(name = "RANK_STATE")
	private Integer rankState;
	@Column(name = "ANON_STATE")
	private Integer anonState;
	@Column(name = "LOGIN_TYPE")
	private Integer loginType;
	@Column(name = "PASS_SCORE")
	private BigDecimal passScore;
	@Column(name = "TOTAL_SCORE")
	private BigDecimal totalScore;
	@Column(name = "MARK_TYPE")
	private Integer markType;
	@Column(name = "SHOW_TYPE")
	private Integer showType;
	@Column(name = "GEN_TYPE")
	private Integer genType;
	@Column(name = "SXES")
	private String sxes;
	@Column(name = "STATE")
	private Integer state;
	@Column(name = "UPDATE_USER_ID")
	private Integer updateUserId;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "UPDATE_TIME")
	private Date updateTime;
	
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

	/** 成绩状态（1：公开；2：不公开） */
	public Integer getScoreState() {
		return scoreState;
	}

	/** 成绩状态（1：公开；2：不公开） */
	public void setScoreState(Integer scoreState) {
		this.scoreState = scoreState;
	}

	/** 排名状态（1：公开；2：不公开） */
	public Integer getRankState() {
		return rankState;
	}

	/** 排名状态（1：公开；2：不公开） */
	public void setRankState(Integer rankState) {
		this.rankState = rankState;
	}

	public Integer getLoginType() {
		return loginType;
	}

	public void setLoginType(Integer loginType) {
		this.loginType = loginType;
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

	/** 0：删除；1：发布；2：暂停；3：归档 */
	public Integer getState() {
		return state;
	}

	/** 0：删除；1：发布；2：暂停；3：归档 */
	public void setState(Integer state) {
		this.state = state;
	}

	/** 1：未阅卷；2：阅卷中；3：已阅卷； */
	public Integer getMarkState() {
		return markState;
	}

	/** 1：未阅卷；2：阅卷中；3：已阅卷； */
	public void setMarkState(Integer markState) {
		this.markState = markState;
	}

	/** 1：公开；2：不公开 */
	public Integer getAnonState() {
		return anonState;
	}

	/** 1：公开；2：不公开 */
	public void setAnonState(Integer anonState) {
		this.anonState = anonState;
	}
	
	public Integer getShowType() {
		return showType;
	}

	public void setShowType(Integer showType) {
		this.showType = showType;
	}

	/** 1：人工组卷；2：随机组卷 */
	public Integer getGenType() {
		return genType;
	}

	/** 1：人工组卷；2：随机组卷 */
	public void setGenType(Integer genType) {
		this.genType = genType;
	}

	public BigDecimal getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(BigDecimal totalScore) {
		this.totalScore = totalScore;
	}

	public BigDecimal getPassScore() {
		return passScore;
	}

	public void setPassScore(BigDecimal passScore) {
		this.passScore = passScore;
	}

	/** 1：自动阅卷；2：人工阅卷； */
	public Integer getMarkType() {
		return markType;
	}

	/** 1：自动阅卷；2：人工阅卷； */
	public void setMarkType(Integer markType) {
		this.markType = markType;
	}

	/** 1：限时；2：不限时 */
	public Integer getTimeType() {
		return timeType;
	}
	
	/** 1：限时；2：不限时 */
	public void setTimeType(Integer timeType) {
		this.timeType = timeType;
	}

	/** 选项（1：试题乱序；2：选项乱序；3：禁用右键；4：禁止复制；5：最小化警告） */
	public String getSxes() {
		return sxes;
	}

	/** 选项（1：试题乱序；2：选项乱序；3：禁用右键；4：禁止复制；5：最小化警告） */
	public void setSxes(String sxes) {
		this.sxes = sxes;
	}
}
