package com.wcpdoc.exam.core.entity.ex;

import java.util.List;

import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.ExamRule;

/**
 * 考试信息
 * 
 * v1.0 zhanghc 2022年9月15日上午10:33:48
 */
public class ExamInfo extends Exam {
	private List<ExamQuestionEx> examQuestions;// 固定组卷使用
	private List<ExamRule> examRules;// 随机组卷使用
	private List<Integer> examUserIds;// 考试用户
	private List<Integer> markUserIds;// 阅卷用户

	public List<ExamQuestionEx> getExamQuestions() {
		return examQuestions;
	}

	public void setExamQuestions(List<ExamQuestionEx> examQuestions) {
		this.examQuestions = examQuestions;
	}

	public List<ExamRule> getExamRules() {
		return examRules;
	}

	public void setExamRules(List<ExamRule> examRules) {
		this.examRules = examRules;
	}

	public List<Integer> getExamUserIds() {
		return examUserIds;
	}

	public void setExamUserIds(List<Integer> examUserIds) {
		this.examUserIds = examUserIds;
	}

	public List<Integer> getMarkUserIds() {
		return markUserIds;
	}

	public void setMarkUserIds(List<Integer> markUserIds) {
		this.markUserIds = markUserIds;
	}

}