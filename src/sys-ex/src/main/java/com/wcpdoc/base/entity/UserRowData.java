package com.wcpdoc.base.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

/**
 * 用户表头
 * 
 * v1.0 zhanghc 2022年5月9日下午3:16:51
 */
public class UserRowData {
	@ExcelProperty({ "用户表", "姓名*" })
	@ColumnWidth(20)
	private String name;
	@ExcelProperty({ "用户表", "登录账号*" })
	@ColumnWidth(26)
	private String loginName;
	@ExcelProperty({ "用户表", "机构名称*" })
	@ColumnWidth(30)
	private String orgName;
	@ExcelProperty({ "用户表", "密码" })
	@ColumnWidth(30)
	private String pwd;
 	@ExcelProperty({ "用户表", "邮箱" })
	@ColumnWidth(24)
	private String email;
	@ExcelProperty({ "用户表", "手机号" })
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

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
}
