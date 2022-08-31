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
 * 考试试题实体
 * 
 * v1.0 zhanghc 2017-05-26 14:23:38
 */
@Entity
@Table(name = "EXM_EXAM_QUESTION")
public class ExamQuestion {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	@Column(name = "CHAPTER_NAME")
	private String chapterName;
	@Column(name = "CHAPTER_TXT")
	private String chapterTxt;
	@Column(name = "TYPE")
	private Integer type;
	@Column(name = "SCORE")
	private BigDecimal score;
	@Column(name = "SCORES")
	private String scores;
	@Column(name = "MARK_OPTIONS")
	private String markOptions;
	@Column(name = "QUESTION_ID")
	private Integer questionId;
	@Column(name = "EXAM_ID")
	private Integer examId;
	@Column(name = "USER_ID")
	private Integer userId;
	@Column(name = "NO")
	private Integer no;
	@Column(name = "UPDATE_USER_ID")
	private Integer updateUserId;
	@Column(name = "UPDATE_TIME")
	@DateTimeFormat(pattern = DateUtil.FORMAT_DATE_TIME)
	private Date updateTime;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getChapterName() {
		return chapterName;
	}

	public void setChapterName(String chapterName) {
		this.chapterName = chapterName;
	}

	public String getChapterTxt() {
		return chapterTxt;
	}

	public void setChapterTxt(String chapterTxt) {
		this.chapterTxt = chapterTxt;
	}

	public String getScores() {
		return scores;
	}

	public void setScores(String scores) {
		this.scores = scores;
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

	public String getMarkOptions() {
		return markOptions;
	}

	public void setMarkOptions(String markOptions) {
		this.markOptions = markOptions;
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

	public Integer[] getMarkOptionArr() {
		if (!ValidateUtil.isValid(markOptions)) {
			return new Integer[0];
		}

		String[] markOptionStrArr = markOptions.split(",");
		Integer[] markOptionArr = new Integer[markOptionStrArr.length];
		for (int i = 0; i < markOptionStrArr.length; i++) {
			markOptionArr[i] = Integer.parseInt(markOptionStrArr[i]);
		}
		return markOptionArr;
	}
}
