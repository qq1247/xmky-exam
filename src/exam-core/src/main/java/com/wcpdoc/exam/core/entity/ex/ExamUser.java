package com.wcpdoc.exam.core.entity.ex;

public class ExamUser {
	private Integer[] examUserIds;
	private Integer markUserId;

	public Integer[] getExamUserIds() {
		return examUserIds;
	}

	public void setExamUserIds(Integer[] examUserIds) {
		this.examUserIds = examUserIds;
	}

	public Integer getMarkUserId() {
		return markUserId;
	}

	public void setMarkUserId(Integer markUserId) {
		this.markUserId = markUserId;
	}
}