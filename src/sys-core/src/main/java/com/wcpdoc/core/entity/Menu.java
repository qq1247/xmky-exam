package com.wcpdoc.core.entity;

import java.util.List;

/**
 * 菜单
 * 
 * v1.0 zhanghc 2015-6-19下午08:30:16
 */
public class Menu {
	private Integer id;
	private String name;
	private String url;
	private String icon;
	private Integer parentId;
	private Integer no;
	private List<Menu> childrens;
	private Integer type;

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public List<Menu> getChildren() {
		return childrens;
	}

	public void setChildren(List<Menu> children) {
		this.childrens = children;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getNo() {
		return no;
	}

	public void setNo(Integer no) {
		this.no = no;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
}
