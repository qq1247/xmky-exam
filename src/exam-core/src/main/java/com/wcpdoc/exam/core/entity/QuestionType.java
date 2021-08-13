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

/**
 * 试题分类实体
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
@Entity
@Table(name = "EXM_QUESTION_TYPE")
public class QuestionType {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	@Column(name = "NAME")
	private String name;
	@Column(name = "IMG_FILE_ID")
	private Integer imgFileId;
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
	@Column(name = "STATE")
	private Integer state;
	@Column(name = "READ_USER_IDS")
	private String readUserIds;
	@Column(name = "WRITE_USER_IDS")
	private String writeUserIds;
	
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
	public Integer getImgFileId() {
		return imgFileId;
	}
	public void setImgFileId(Integer imgFileId) {
		this.imgFileId = imgFileId;
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
	public String getReadUserIds() {
		return readUserIds;
	}
	public void setReadUserIds(String readUserIds) {
		this.readUserIds = readUserIds;
	}
	public String getWriteUserIds() {
		return writeUserIds;
	}
	public void setWriteUserIds(String writeUserIds) {
		this.writeUserIds = writeUserIds;
	}
}
