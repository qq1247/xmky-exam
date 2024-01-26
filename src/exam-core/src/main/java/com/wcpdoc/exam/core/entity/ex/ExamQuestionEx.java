package com.wcpdoc.exam.core.entity.ex;

import java.util.List;

import com.wcpdoc.exam.core.entity.ExamQuestion;

/**
 * 考试试题扩展
 * 
 * v1.0 zhanghc 2022年9月15日上午10:33:48
 */
public class ExamQuestionEx extends ExamQuestion {
	private Integer questionType;
	private Integer markType;
	private String title;
	private List<String> options;
	private List<String> answers;
	private String analysis;

	/** 类型（1：单选；2：多选；3：填空；4：判断；5：问答） */
	public Integer getQuestionType() {
		return questionType;
	}

	/** 类型（1：单选；2：多选；3：填空；4：判断；5：问答） */
	public void setQuestionType(Integer questionType) {
		this.questionType = questionType;
	}

	/** 阅卷类型（1：客观题；2：主观题） */
	public Integer getMarkType() {
		return markType;
	}

	/** 阅卷类型（1：客观题；2：主观题） */
	public void setMarkType(Integer markType) {
		this.markType = markType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAnalysis() {
		return analysis;
	}

	public void setAnalysis(String analysis) {
		this.analysis = analysis;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	public List<String> getAnswers() {
		return answers;
	}

	public void setAnswers(List<String> answers) {
		this.answers = answers;
	}
}