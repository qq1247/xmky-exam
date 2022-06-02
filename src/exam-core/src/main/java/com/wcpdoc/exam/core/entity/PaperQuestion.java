package com.wcpdoc.exam.core.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.ValidateUtil;

/**
 * 试卷试题实体
 * 
 * v1.0 zhanghc 2017-05-26 14:23:38
 */
@Entity
@Table(name = "EXM_PAPER_QUESTION")
public class PaperQuestion {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	@Column(name = "NAME")
	private String name;
	@Column(name = "DESCRIPTION")
	private String description;
	@Column(name = "PARENT_ID")
	private Integer parentId;
	@Column(name = "PARENT_SUB")
	private String parentSub;
	@Column(name = "UPDATE_USER_ID")
	private Integer updateUserId;
	@Column(name = "UPDATE_TIME")
	@DateTimeFormat(pattern = DateUtil.FORMAT_DATE_TIME)
	private Date updateTime;
	@Column(name = "PAPER_ID")
	private Integer paperId;
	@Column(name = "QUESTION_ID")
	private Integer questionId;
	@Column(name = "TYPE")
	private Integer type;
	@Column(name = "SCORE")
	private BigDecimal score;
	@Column(name = "AI_OPTIONS")
	private String aiOptions;
	@Column(name = "NO")
	private Integer no;
	@Column(name = "EXAM_ID")
	private Integer examId;
	@Column(name = "USER_ID")
	private Integer userId;
	
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getParentSub() {
		return parentSub;
	}

	public void setParentSub(String parentSub) {
		this.parentSub = parentSub;
	}

	public Integer getUpdateUserId() {
		return updateUserId;
	}

	public void setUpdateUserId(Integer updateUserId) {
		this.updateUserId = updateUserId;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getPaperId() {
		return paperId;
	}

	public void setPaperId(Integer paperId) {
		this.paperId = paperId;
	}

	public Integer getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Integer questionId) {
		this.questionId = questionId;
	}

	/** 1：章节；2：固定试题；3：随机试题 */
	public Integer getType() {
		return type;
	}

	/** 1：章节；2：固定试题；3：随机试题 */
	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getNo() {
		return no;
	}

	public void setNo(Integer no) {
		this.no = no;
	}

	public BigDecimal getScore() {
		return score;
	}

	public void setScore(BigDecimal score) {
		this.score = score;
	}

	public String getAiOptions() {
		return aiOptions;
	}

	public void setAiOptions(String aiOptions) {
		this.aiOptions = aiOptions;
	}

	public Integer getExamId() {
		return examId;
	}

	public void setExamId(Integer examId) {
		this.examId = examId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer[] getAiOptionArr() {
		if (!ValidateUtil.isValid(aiOptions)) {
			return new Integer[0];
		}

		String[] aiOptionStrArr = aiOptions.split(",");
		Integer[] aiOptionArr = new Integer[aiOptionStrArr.length];
		for (int i = 0; i < aiOptionStrArr.length; i++) {
			aiOptionArr[i] = Integer.parseInt(aiOptionStrArr[i]);
		}
		return aiOptionArr;
	}
}
