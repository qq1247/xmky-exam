package com.wcpdoc.base.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

/**
 * 组织机构表格
 * 
 * v1.0 chenyun 2021年3月4日下午5:25:13
 */
public class OrgXlsx {
	@ExcelProperty({"组织机构表", "机构名称（必填）"})
	@ColumnWidth(26)
    private String name;
	@ExcelProperty({"组织机构表", "所属机构（必填）"})
	@ColumnWidth(26)
    private String parentName;
	@ExcelProperty({"组织机构表", "排序（必填）"})
	@ColumnWidth(20)
    private Integer no;

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
