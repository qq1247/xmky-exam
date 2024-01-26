package com.wcpdoc.core.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 在线用户实体
 * 
 * v1.0 zhanghc 2021年10月15日下午3:28:26
 */
public class OnlineUser implements Serializable {
	private static final long serialVersionUID = -5959356841310592786L;
	private Integer id;
	private String loginName;
	private String ip;
	private Date updateTime;

	public OnlineUser() {

	}

	public OnlineUser(Integer id, String loginName, String ip) {
		this.id = id;
		this.loginName = loginName;
		this.ip = ip;
		this.updateTime = new Date();
	}

	public OnlineUser(Integer id, String loginName, String ip, Date updateTime) {
		this.id = id;
		this.loginName = loginName;
		this.ip = ip;
		this.updateTime = updateTime;
	}

	/**
	 * 获取在线状态<br/>
	 * 正常情况下，前端每隔30秒会请求一次服务器，如果半分钟内没有接受到请求，表示离线<br/>
	 * 加5秒用于请求延时用
	 * 
	 * v1.0 zhanghc 2021年10月18日下午4:31:33 void
	 */
	public boolean getState() {
		if (updateTime == null) {
			return false;
		}
		return System.currentTimeMillis() - updateTime.getTime() < 35000;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
