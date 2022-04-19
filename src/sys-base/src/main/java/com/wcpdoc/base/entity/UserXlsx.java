package com.wcpdoc.base.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

/**
 * 用户表格
 * 
 * v1.0 chenyun 2021年3月4日下午5:25:13
 */
public class UserXlsx{
	@ExcelProperty({"用户表", "姓名（必填）"})
	@ColumnWidth(20)
    private String name;
	@ExcelProperty({"用户表", "登录账号（必填）"})
	@ColumnWidth(26)
    private String loginName;
	@ExcelProperty({"用户表", "组织机构名称（必填）"})
	@ColumnWidth(30)
    private String orgName;
	@ExcelProperty({"用户表", "邮箱（非必填）"})
	@ColumnWidth(24)
    private String email;
	@ExcelProperty({"用户表", "手机号（非必填）"})
	@ColumnWidth(26)
    private String phone;
    
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
}
