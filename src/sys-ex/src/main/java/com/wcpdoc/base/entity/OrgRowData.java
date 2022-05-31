package com.wcpdoc.base.entity;

import java.util.List;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.wcpdoc.core.util.TreeUtil.Tree;

/**
 * 机构行数据
 * 
 * v1.0 chenyun 2021年3月4日下午5:25:13
 */
public class OrgRowData implements Tree {
	@ExcelProperty({ "机构表", "机构名称*" })
	@ColumnWidth(26)
	private String name;
	@ExcelProperty({ "机构表", "所属机构" })
	@ColumnWidth(26)
	private String parentName;

	private List<Tree> children;

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

	@Override
	public String getId() {
		return name;
	}

	@Override
	public String getParentId() {
		return parentName;
	}

	@Override
	public void setChildren(List<Tree> children) {
		this.children = children;
	}

	@Override
	public List<Tree> getChildren() {
		return children;
	}
}
