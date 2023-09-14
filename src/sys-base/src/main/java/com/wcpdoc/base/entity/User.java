package com.wcpdoc.base.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wcpdoc.core.entity.LoginUser;
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.core.util.ValidateUtil;

/**
 * 用户实体
 * 
 * v1.0 zhanghc 2016-6-15下午17:24:19
 */
@Entity
@Table(name = "SYS_USER")
public class User implements LoginUser {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	@Column(name = "NAME")
	private String name;
	@Column(name = "LOGIN_NAME")
	private String loginName;
	@Column(name = "EMAIL")
	private String email;
	@Column(name = "PHONE")
	private String phone;
	@Column(name = "PWD")
	private String pwd;
	@Column(name = "REGIST_TIME")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date registTime;
	@Column(name = "LAST_LOGIN_TIME")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date lastLoginTime;
	@Column(name = "UPDATE_USER_ID")
	private Integer updateUserId;
	@Column(name = "UPDATE_TIME")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;
	@Column(name = "ORG_ID")
	private Integer orgId;
	@Column(name = "STATE")
	private Integer state;
	@Column(name = "HEAD_FILE_ID")
	private Integer headFileId;
	@Column(name = "TYPE")
	private Integer type;
	@Column(name = "PARENT_ID")
	private Integer parentId;
	@Column(name = "USER_IDS")
	private String userIds;
	@Column(name = "ORG_IDS")
	private String orgIds;

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

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public Date getRegistTime() {
		return registTime;
	}

	public void setRegistTime(Date registTime) {
		this.registTime = registTime;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
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

	public Integer getOrgId() {
		return orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	/** 状态（0：删除；1：正常；2：冻结；） */
	public Integer getState() {
		return state;
	}

	/** 状态（0：删除；1：正常；2：冻结；） */
	public void setState(Integer state) {
		this.state = state;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getHeadFileId() {
		return headFileId;
	}

	public void setHeadFileId(Integer headFileId) {
		this.headFileId = headFileId;
	}

	/** 类型（0：管理员；1：考试用户；2：子管理员；3：阅卷用户） */
	public Integer getType() {
		return type;
	}

	/** 类型（0：管理员；1：考试用户；2：子管理员；3：阅卷用户） */
	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
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
	
	public void setUserIds(Integer[] userIds) {
		if (!ValidateUtil.isValid(userIds)) {
			this.userIds = null;
			return;
		}

		this.userIds = String.format(",%s,", StringUtil.join(userIds));
	}
	
	public Integer[] getOrgIds() {
		if (!ValidateUtil.isValid(orgIds)) {
			return new Integer[0];
		}

		String[] orgIdStrArr = orgIds.substring(1, orgIds.length() - 1).split(",");
		Integer[] orgIdArr = new Integer[orgIdStrArr.length];
		for (int i = 0; i < orgIdStrArr.length; i++) {
			orgIdArr[i] = Integer.parseInt(orgIdStrArr[i]);
		}
		return orgIdArr;
	}
	
	public void setOrgIds(Integer[] orgIds) {
		if (!ValidateUtil.isValid(orgIds)) {
			this.orgIds = null;
			return;
		}

		this.orgIds = String.format(",%s,", StringUtil.join(orgIds));
	}
}
