package com.wcpdoc.exam.core.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 试卷评语实体
 * 
 * v1.0 chenyun 2021-03-10 13:48:34
 */
@Entity
@Table(name = "EXM_PAPER_REMARK")
public class PaperRemark {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	@Column(name = "SCORE_A")
	private BigDecimal scoreA;
	@Column(name = "SCORE_A_REMARK")
	private String scoreARemark;
	@Column(name = "SCORE_B")
	private BigDecimal scoreB;
	@Column(name = "SCORE_B_REMARK")
	private String scoreBRemark;
	@Column(name = "SCORE_C")
	private BigDecimal scoreC;
	@Column(name = "SCORE_C_REMARK")
	private String scoreCRemark;
	@Column(name = "SCORE_D")
	private BigDecimal scoreD;
	@Column(name = "SCORE_D_REMARK")
	private String scoreDRemark;
	@Column(name = "SCORE_E")
	private BigDecimal scoreE;
	@Column(name = "SCORE_E_REMARK")
	private String scoreERemark;
	@Column(name = "PAPER_ID")
	private Integer paperId;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public BigDecimal getScoreA() {
		return scoreA;
	}

	public void setScoreA(BigDecimal scoreA) {
		this.scoreA = scoreA;
	}
	
	public String getScoreARemark() {
		return scoreARemark;
	}
 
	public void setScoreARemark(String scoreARemark) {
		this.scoreARemark = scoreARemark;
	}
	
	public BigDecimal getScoreB() {
		return scoreB;
	}

	public void setScoreB(BigDecimal scoreB) {
		this.scoreB = scoreB;
	}
	
	public String getScoreBRemark() {
		return scoreBRemark;
	}
 
	public void setScoreBRemark(String scoreBRemark) {
		this.scoreBRemark = scoreBRemark;
	}
	
	public BigDecimal getScoreC() {
		return scoreC;
	}

	public void setScoreC(BigDecimal scoreC) {
		this.scoreC = scoreC;
	}
	
	public String getScoreCRemark() {
		return scoreCRemark;
	}
 
	public void setScoreCRemark(String scoreCRemark) {
		this.scoreCRemark = scoreCRemark;
	}
	
	public BigDecimal getScoreD() {
		return scoreD;
	}

	public void setScoreD(BigDecimal scoreD) {
		this.scoreD = scoreD;
	}
	
	public String getScoreDRemark() {
		return scoreDRemark;
	}
 
	public void setScoreDRemark(String scoreDRemark) {
		this.scoreDRemark = scoreDRemark;
	}
	
	public BigDecimal getScoreE() {
		return scoreE;
	}

	public void setScoreE(BigDecimal scoreE) {
		this.scoreE = scoreE;
	}
	
	public String getScoreERemark() {
		return scoreERemark;
	}
 
	public void setScoreERemark(String scoreERemark) {
		this.scoreERemark = scoreERemark;
	}
	
	public Integer getPaperId() {
		return paperId;
	}

	public void setPaperId(Integer paperId) {
		this.paperId = paperId;
	}
}