package com.wcpdoc.exam.core.entity;

import java.util.List;

/**
 * 考试更新判卷用户
 * 
 * v1.0 cY 2021年6月22日下午4:00:12
 */
public class UpdateMarkUserJson {
    private Integer id;
    private List<MyMark> markUserIds;
    
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public List<MyMark> getMarkUserIds() {
		return markUserIds;
	}
	public void setMarkUserIds(List<MyMark> markUserIds) {
		this.markUserIds = markUserIds;
	}
}
