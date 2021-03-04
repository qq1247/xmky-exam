package com.wcpdoc.exam.base.entity;

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
 * 邮箱实体
 * 
 * v1.0 chenyun 2021-03-04 15:02:18
 */
@Entity
@Table(name = "SYS_EMAIL")
public class Email {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	@Column(name = "EMAIL_HOST")
	private String emailHost;
	@Column(name = "EMAIL_NAME")
	private String emailName;
	@Column(name = "EMAIL_PWD")
	private String emailPwd;
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
	
	public String getEmailHost() {
		return emailHost;
	}
 
	public void setEmailHost(String emailHost) {
		this.emailHost = emailHost;
	}
	
	public String getEmailName() {
		return emailName;
	}
 
	public void setEmailName(String emailName) {
		this.emailName = emailName;
	}
	
	public String getEmailPwd() {
		return emailPwd;
	}
 
	public void setEmailPwd(String emailPwd) {
		this.emailPwd = emailPwd;
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