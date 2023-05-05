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
 * 练习评论
 * 
 * v1.0 chenyun 2021年8月31日上午9:46:45
 */
@Entity
@Table(name = "EXM_EXER_RMK")
public class ExerRmk {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	@Column(name = "EXER_ID")
	private Integer exerId;
	@Column(name = "QUESTION_ID")
	private Integer questionId;
	@Column(name = "CONTENT")
	private String content;
	@Column(name = "LIKE_USER_IDS")
	private String likeUserIds;
	@Column(name = "LIKE_NUM")
	private Integer likeNum;
	@Column(name = "STATE")
	private Integer state;
	@Column(name = "UPDATE_USER_ID")
	private Integer updateUserId;
	@Column(name = "UPDATE_TIME")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Integer questionId) {
		this.questionId = questionId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer[] getLikeUserIds() {
		if (!ValidateUtil.isValid(likeUserIds)) {
			return new Integer[0];
		}

		String[] likeUserIdStrArr = likeUserIds.substring(1, likeUserIds.length() - 1).split(",");
		Integer[] likeUserIdArr = new Integer[likeUserIdStrArr.length];
		for (int i = 0; i < likeUserIdStrArr.length; i++) {
			likeUserIdArr[i] = Integer.parseInt(likeUserIdStrArr[i]);
		}
		return likeUserIdArr;
	}

	public void setLikeUserIds(Integer[] likeUserIds) {
		if (!ValidateUtil.isValid(likeUserIds)) {
			this.likeUserIds = null;
			return;
		}

		this.likeUserIds = String.format(",%s,", StringUtil.join(likeUserIds));
	}

	public Integer getLikeNum() {
		return likeNum;
	}

	public void setLikeNum(Integer likeNum) {
		this.likeNum = likeNum;
	}

	/** 状态（0：删除；1：正常） */
	public Integer getState() {
		return state;
	}

	/** 状态（0：删除；1：正常） */
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

	public Integer getExerId() {
		return exerId;
	}

	public void setExerId(Integer exerId) {
		this.exerId = exerId;
	}
}
