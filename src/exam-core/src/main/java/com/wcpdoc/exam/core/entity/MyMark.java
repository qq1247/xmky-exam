package com.wcpdoc.exam.core.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wcpdoc.core.util.ValidateUtil;

/**
 * 我的阅卷实体
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Entity
@Table(name = "EXM_MY_MARK")
public class MyMark {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	@Column(name = "EXAM_ID")
	private Integer examId;
	@Column(name = "MARK_USER_ID")
	private Integer markUserId;
	@Column(name = "QUESTION_IDS")
	private String questionIds;
	@Column(name = "EXAM_USER_IDS")
	private String examUserIds;
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

	public Integer getExamId() {
		return examId;
	}

	public void setExamId(Integer examId) {
		this.examId = examId;
	}

	public String getQuestionIds() {
		return questionIds;
	}

	public void setQuestionIds(String questionIds) {
		this.questionIds = questionIds;
	}

	public Integer getMarkUserId() {
		return markUserId;
	}

	public void setMarkUserId(Integer markUserId) {
		this.markUserId = markUserId;
	}

	public String getExamUserIds() {
		return examUserIds;
	}
	
	public Integer[] getExamUserIdArr() {
		if (!ValidateUtil.isValid(examUserIds)) {
			return null;
		}
		
		List<Integer> examUserIdList = new ArrayList<>();
		for (String examUserId : examUserIds.substring(1, examUserIds.length() - 1).split(",")) {
			examUserIdList.add(Integer.parseInt(examUserId));
		}
		
		return examUserIdList.toArray(new Integer[0]);
	}
	
	public void setExamUserIds(String examUserIds) {
		this.examUserIds = examUserIds;
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
}
