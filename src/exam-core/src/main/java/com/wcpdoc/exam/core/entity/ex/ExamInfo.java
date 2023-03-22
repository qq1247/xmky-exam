package com.wcpdoc.exam.core.entity.ex;

import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.ExamRule;

/**
 * 考试信息
 * 
 * v1.0 zhanghc 2022年9月15日上午10:33:48
 */
public class ExamInfo extends Exam {
	private ExamQuestionEx[] examQuestions;// 固定组卷使用
	private ExamRule[] examRules;// 随机组卷使用
	private Integer[] examUserIds;// 考试用户
	private Integer[] markUserIds;// 阅卷用户

	public ExamQuestionEx[] getExamQuestions() {
		return examQuestions;
	}

	public void setExamQuestions(ExamQuestionEx[] examQuestions) {
		this.examQuestions = examQuestions;
	}

	public ExamRule[] getExamRules() {
		return examRules;
	}

	public void setExamRules(ExamRule[] examRules) {
		this.examRules = examRules;
	}

	public Integer[] getExamUserIds() {
		return examUserIds;
	}

	public void setExamUserIds(Integer[] examUserIds) {
		this.examUserIds = examUserIds;
	}

	public Integer[] getMarkUserIds() {
		return markUserIds;
	}

	public void setMarkUserIds(Integer[] markUserIds) {
		this.markUserIds = markUserIds;
	}
}