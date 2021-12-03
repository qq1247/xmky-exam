package com.wcpdoc.wordFilter.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 敏感词
 * 
 * v1.0 chenyun 2021年9月2日下午4:25:01
 */
@Entity
@Table(name = "SYS_SENSITIVE")
public class Sensitive {
	@Id
	@Column(name = "ID")
	private Integer id;
	@Column(name = "WHITE_LIST")
	private String whiteList;
	@Column(name = "BLACK_LIST")
	private String blackList;
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
	public String getWhiteList() {
		return whiteList;
	}
	public void setWhiteList(String whiteList) {
		this.whiteList = whiteList;
	}
	public String getBlackList() {
		return blackList;
	}
	public void setBlackList(String blackList) {
		this.blackList = blackList;
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
