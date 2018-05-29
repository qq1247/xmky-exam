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
 * 试卷实体
 * 
 * v1.0 zhanghc 2017-05-25 16:34:59
 */
@Entity
@Table(name = "EXM_PAPER")
public class Paper {
	@Id
	@GeneratedValue(generator = "generator")
	@GenericGenerator(name = "generator", strategy = "identity")
	@Column(name = "ID")
	private Integer id;
	@Column(name = "NAME")
	private String name;
	@Column(name = "DESCRIPTION")
	private String description;
	@Column(name = "TOTLE_SCORE")
	private BigDecimal totleScore;
	@Column(name = "STATE")
	private Integer state;
	@Column(name = "UPDATE_USER_ID")
	private Integer updateUserId;
	@Column(name = "UPDATE_TIME")
	@DateTimeFormat(pattern = DateUtil.FORMAT_DATE_TIME)
	private Date updateTime;
	@Column(name = "PAPER_TYPE_ID")
	private Integer paperTypeId;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getTotleScore() {
		return totleScore;
	}

	public void setTotleScore(BigDecimal totleScore) {
		this.totleScore = totleScore;
	}

	public Integer getState() {
		return state;
	}

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

	public Integer getPaperTypeId() {
		return paperTypeId;
	}

	public void setPaperTypeId(Integer paperTypeId) {
		this.paperTypeId = paperTypeId;
	}

}
