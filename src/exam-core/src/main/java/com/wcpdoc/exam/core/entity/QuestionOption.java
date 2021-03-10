package com.wcpdoc.exam.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 试题选项实体
 * 
 * v1.0 chenyun 2021-03-10 16:11:06
 */
@Entity
@Table(name = "EXM_QUESTION_OPTION")
public class QuestionOption {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	@Column(name = "OPTION_A")
	private String optionA;
	@Column(name = "OPTION_B")
	private String optionB;
	@Column(name = "OPTION_C")
	private String optionC;
	@Column(name = "OPTION_D")
	private String optionD;
	@Column(name = "OPTION_E")
	private String optionE;
	@Column(name = "OPTION_F")
	private String optionF;
	@Column(name = "OPTION_G")
	private String optionG;
	@Column(name = "QUESTION_ID")
	private Integer questionId;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getOptionA() {
		return optionA;
	}
 
	public void setOptionA(String optionA) {
		this.optionA = optionA;
	}
	
	public String getOptionB() {
		return optionB;
	}
 
	public void setOptionB(String optionB) {
		this.optionB = optionB;
	}
	
	public String getOptionC() {
		return optionC;
	}
 
	public void setOptionC(String optionC) {
		this.optionC = optionC;
	}
	
	public String getOptionD() {
		return optionD;
	}
 
	public void setOptionD(String optionD) {
		this.optionD = optionD;
	}
	
	public String getOptionE() {
		return optionE;
	}
 
	public void setOptionE(String optionE) {
		this.optionE = optionE;
	}
	
	public String getOptionF() {
		return optionF;
	}
 
	public void setOptionF(String optionF) {
		this.optionF = optionF;
	}
	
	public String getOptionG() {
		return optionG;
	}
 
	public void setOptionG(String optionG) {
		this.optionG = optionG;
	}
	
	public Integer getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Integer questionId) {
		this.questionId = questionId;
	}
}