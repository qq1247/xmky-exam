package com.wcpdoc.exam.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.core.util.ValidateUtil;

/**
 * 练习实体
 * 
 * v1.0 chenyun 2021-03-02 13:43:21
 */
@Entity
@Table(name = "EXM_EXER")
public class Exer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	@Column(name = "NAME")
	private String name;
	@Column(name = "QUESTION_TYPE_ID")
	private Integer questionTypeId;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "START_TIME")
	private Date startTime;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "END_TIME")
	private Date endTime;
	@Column(name = "USER_IDS")
	private String userIds;
	@Column(name = "STATE")
	private Integer state;
	@Column(name = "RMK_STATE")
	private Integer rmkState;
	@Column(name = "CREATE_USER_ID")
	private Integer createUserId;
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

	public Integer getQuestionTypeId() {
		return questionTypeId;
	}

	public void setQuestionTypeId(Integer questionTypeId) {
		this.questionTypeId = questionTypeId;
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

	public Integer[] getUserIds() {
		if (!ValidateUtil.isValid(userIds)) {
			return new Integer[0];
		}

		String[] userIdStrArr = userIds.substring(1, userIds.length() - 1).split(",");
		Integer[] userIdArr = new Integer[userIdStrArr.length];
		for (int i = 0; i < userIdStrArr.length; i++) {
			userIdArr[i] = Integer.parseInt(userIdStrArr[i]);
		}
		return userIdArr;
	}

	public void setUserIds(String userIds) {
		this.userIds = userIds;
	}
	
	public void setUserIds(Integer[] userIds) {
		if (!ValidateUtil.isValid(userIds)) {
			this.userIds = null;
			return;
		}

		this.userIds = String.format(",%s,", StringUtil.join(userIds));
	}

	/** 状态（0：删除；1：正常） */
	public Integer getState() {
		return state;
	}

	/** 状态（0：删除；1：正常） */
	public void setState(Integer state) {
		this.state = state;
	}

	/** 评论状态（1：是；2：否） */
	public Integer getRmkState() {
		return rmkState;
	}

	/** 评论状态（1：是；2：否） */
	public void setRmkState(Integer rmkState) {
		this.rmkState = rmkState;
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

	public Integer getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(Integer createUserId) {
		this.createUserId = createUserId;
	}
}