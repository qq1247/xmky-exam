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
 * 试卷实体
 * 
 * zhanghc 2018年10月21日上午8:16:06
 */
@Entity
@Table(name = "EXM_PAPER")
public class Paper {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	@Column(name = "NAME")
	private String name;
	@Column(name = "PASS_SCORE")
	private BigDecimal passScore;
	@Column(name = "TOTAL_SCORE")
	private BigDecimal totalScore;
	@Column(name = "SHOW_TYPE")
	private Integer showType;
	@Column(name = "READ_REMARK")
	private String readRemark;
	@Column(name = "READ_NUM")
	private Integer readNum;
	@Column(name = "STATE")
	private Integer state;
	@Column(name = "PAPER_TYPE_ID")
	private Integer paperTypeId;
	@Column(name = "GEN_TYPE")
	private Integer genType;
	@Column(name = "OPTIONS")
	private String options;
	@Column(name = "MINIMIZE_NUM")
	private Integer minimizeNum;
	@Column(name = "MARK_TYPE")
	private Integer markType;
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
	@Column(name = "READ_USER_IDS")
	private String readUserIds;

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

	public String getReadRemark() {
		return readRemark;
	}

	public void setReadRemark(String readRemark) {
		this.readRemark = readRemark;
	}

	public Integer getPaperTypeId() {
		return paperTypeId;
	}

	public void setPaperTypeId(Integer paperTypeId) {
		this.paperTypeId = paperTypeId;
	}

	/** 0：删除；1：发布；2：草稿；3：归档 */
	public Integer getState() {
		return state;
	}

	/** 0：删除；1：发布；2：草稿；3：归档 */
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

	public Integer getReadNum() {
		return readNum;
	}

	public void setReadNum(Integer readNum) {
		this.readNum = readNum;
	}

	public Integer getMinimizeNum() {
		return minimizeNum;
	}

	public void setMinimizeNum(Integer minimizeNum) {
		this.minimizeNum = minimizeNum;
	}

	/** 选项（1：试题乱序；2：选项乱序；3：禁用右键；4：禁止复制；5：最小化警告） */
	public String getOptions() {
		return options;
	}

	/** 选项（1：试题乱序；2：选项乱序；3：禁用右键；4：禁止复制；5：最小化警告） */
	public void setOptions(String options) {
		this.options = options;
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

	/** 1：智能阅卷；2：人工阅卷； */
	public Integer getMarkType() {
		return markType;
	}

	/** 1：智能阅卷；2：人工阅卷； */
	public void setMarkType(Integer markType) {
		this.markType = markType;
	}

	public String getReadUserIds() {
		return readUserIds;
	}

	public void setReadUserIds(String readUserIds) {
		this.readUserIds = readUserIds;
	}
}