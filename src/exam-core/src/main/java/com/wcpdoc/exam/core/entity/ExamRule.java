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
 * 考试随机规则
 * 
 * v1.0 chenyun 2022年2月11日 09:48:21
 */
@Entity
@Table(name = "EXM_EXAM_RULE")
public class ExamRule {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	@Column(name = "UPDATE_USER_ID")
	private Integer updateUserId;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "UPDATE_TIME")
	private Date updateTime;
	@Column(name = "EXAM_ID")
	private Integer examId;
	@Column(name = "QUESTION_TYPE_ID")
	private Integer questionTypeId;
	@Column(name = "TYPE")
	private Integer type;
	@Column(name = "MARK_TYPE")
	private String markTypes;
	@Column(name = "MARK_OPTIONS")
	private String markOptions;
	@Column(name = "NUM")
	private Integer num;
	@Column(name = "SCORE")
	private BigDecimal score;
	@Column(name = "NO")
	private Integer no;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public Integer getExamId() {
		return examId;
	}
	public void setExamId(Integer examId) {
		this.examId = examId;
	}
	public Integer getQuestionTypeId() {
		return questionTypeId;
	}
	public void setQuestionTypeId(Integer questionTypeId) {
		this.questionTypeId = questionTypeId;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getMarkOptions() {
		return markOptions;
	}
	public void setMarkOptions(String markOptions) {
		this.markOptions = markOptions;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public BigDecimal getScore() {
		return score;
	}
	public void setScore(BigDecimal score) {
		this.score = score;
	}
	public Integer getNo() {
		return no;
	}
	public void setNo(Integer no) {
		this.no = no;
	}
	public String getMarkTypes() {
		return markTypes;
	}
	public void setMarkTypes(String markTypes) {
		this.markTypes = markTypes;
	}
	public Integer[] getMarkTypeArr() {
		String[] split = markTypes.split(",");
		Integer[] markTypeInteger = new Integer[split.length];
		for(int i = 0; i < split.length; i++){
			markTypeInteger[i] = Integer.valueOf(split[i]);
		}
		return markTypeInteger;
	}
}