package com.wcpdoc.base.entity;

/**
 * 组织机构表格
 * 
 * v1.0 chenyun 2021年3月4日下午5:25:13
 */
public class OrgXlsx {
    private String name;
    private String parentName;
    private Integer no;
    
	public OrgXlsx(String name, String parentName, Integer no) {
		super();
		this.name = name;
		this.parentName = parentName;
		this.no = no;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public Integer getNo() {
		return no;
	}
	public void setNo(Integer no) {
		this.no = no;
	}
}
