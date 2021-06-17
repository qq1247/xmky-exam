package com.wcpdoc.exam.base.entity;

/**
 * 用户表格
 * 
 * v1.0 chenyun 2021年3月4日下午5:25:13
 */
public class UserXlsx {
    private String name;
    private String loginName;
    private String orgName;
    
	public UserXlsx(String name, String loginName, String orgName) {
		super();
		this.name = name;
		this.loginName = loginName;
		this.orgName = orgName;
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
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
    
}
