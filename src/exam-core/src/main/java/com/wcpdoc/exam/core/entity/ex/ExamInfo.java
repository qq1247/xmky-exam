package com.wcpdoc.exam.core.entity.ex;

import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.ExamRule;

/**
 * 考试信息
 * 
 * v1.0 zhanghc 2022年9月15日上午10:33:48
 */
public class ExamInfo {
	private Exam exam;
	private ExamQuestionEx[] examQuestions;
	private ExamUser[] examUsers;
	private ExamRule[] examRules;

	public Exam getExam() {
		return exam;
	}

	public void setExam(Exam exam) {
		this.exam = exam;
	}

	public ExamQuestionEx[] getExamQuestions() {
		return examQuestions;
	}

	public void setExamQuestions(ExamQuestionEx[] examQuestions) {
		this.examQuestions = examQuestions;
	}

	public ExamUser[] getExamUsers() {
		return examUsers;
	}

	public void setExamUsers(ExamUser[] examUsers) {
		this.examUsers = examUsers;
	}

	public ExamRule[] getExamRules() {
		return examRules;
	}

	public void setExamRules(ExamRule[] examRules) {
		this.examRules = examRules;
	}

}