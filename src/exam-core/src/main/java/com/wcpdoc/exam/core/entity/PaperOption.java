package com.wcpdoc.exam.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 试卷实体
 * 
 * v1.0 chenyun 2021-03-10 13:47:35
 */
@Entity
@Table(name = "EXM_PAPER_OPTION")
public class PaperOption {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	@Column(name = "QUESTION")
	private Integer question;
	@Column(name = "QUESTION_OPTION")
	private Integer questionOption;
	@Column(name = "RIGHT_CLICK")
	private Integer rightClick;
	@Column(name = "RIGHT_COPY")
	private Integer rightCopy;
	@Column(name = "MINIMIZE")
	private Integer minimize;
	@Column(name = "MINIMIZE_NUM")
	private Integer minimizeNum;
	@Column(name = "PAPER_ID")
	private Integer paperId;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getQuestion() {
		return question;
	}

	public void setQuestion(Integer question) {
		this.question = question;
	}
	
	public Integer getQuestionOption() {
		return questionOption;
	}

	public void setQuestionOption(Integer questionOption) {
		this.questionOption = questionOption;
	}
	
	public Integer getRightClick() {
		return rightClick;
	}

	public void setRightClick(Integer rightClick) {
		this.rightClick = rightClick;
	}
	
	public Integer getRightCopy() {
		return rightCopy;
	}

	public void setRightCopy(Integer rightCopy) {
		this.rightCopy = rightCopy;
	}
	
	public Integer getMinimize() {
		return minimize;
	}

	public void setMinimize(Integer minimize) {
		this.minimize = minimize;
	}
	
	public Integer getMinimizeNum() {
		return minimizeNum;
	}

	public void setMinimizeNum(Integer minimizeNum) {
		this.minimizeNum = minimizeNum;
	}
	
	public Integer getPaperId() {
		return paperId;
	}

	public void setPaperId(Integer paperId) {
		this.paperId = paperId;
	}
}