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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.core.util.ValidateUtil;

/**
 * 考试规则
 * 
 * v1.0 chenyun 2022年2月11日 09:48:21
 */
@Entity
@Table(name = "EXM_EXAM_RULE")
public class ExamRule {
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
	@Column(name = "QUESTION_TYPE_ID")
	private Integer questionTypeId;
	@Column(name = "QUESTION_TYPE")
	private Integer questionType;
	@Column(name = "MARK_TYPE")
	private Integer markType;
	@Column(name = "MARK_OPTIONS")
	private String markOptions;
	@Column(name = "NUM")
	private Integer num;
	@Column(name = "SCORE")
	private BigDecimal score;
	@Column(name = "SCORES")
	private String scores;
	@Column(name = "EXAM_ID")
	private Integer examId;
	@Column(name = "NO")
	private Integer no;
	@Column(name = "UPDATE_USER_ID")
	private Integer updateUserId;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "UPDATE_TIME")
	private Date updateTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getQuestionTypeId() {
		return questionTypeId;
	}

	public void setQuestionTypeId(Integer questionTypeId) {
		this.questionTypeId = questionTypeId;
	}

	/** 类型 （1：章节；2：试题） */
	public Integer getType() {
		return type;
	}

	/** 类型 （1：章节；2：试题） */
	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getMarkType() {
		return markType;
	}

	public void setMarkType(Integer markType) {
		this.markType = markType;
	}

	/** 阅卷选项（2：答案无顺序；3：大小写不敏感；) */
	public Integer[] getMarkOptions() {
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

	/** 阅卷选项（2：答案无顺序；3：大小写不敏感；) */
	public void setMarkOptions(Integer[] markOptions) {
		if (!ValidateUtil.isValid(markOptions)) {
			this.markOptions = null;
			return;
		}
		
		this.markOptions = StringUtil.join(markOptions);
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public BigDecimal getScore() {
		return score;
	}

	public void setScore(BigDecimal score) {
		this.score = score;
	}

	public BigDecimal[] getScores() {
		if (!ValidateUtil.isValid(scores)) {
			return new BigDecimal[0];
		}
	
		String[] scoresStrArr = scores.split(",");
		BigDecimal[] scoresArr = new BigDecimal[scoresStrArr.length];
		for (int i = 0; i < scoresStrArr.length; i++) {
			scoresArr[i] = new BigDecimal(scoresStrArr[i]);
		}
		return scoresArr;
	}

	public void setScores(BigDecimal[] scores) {
		if (!ValidateUtil.isValid(scores)) {
			this.scores = null;
			return;
		}
		
		this.scores = StringUtil.join(scores);
	}

	public Integer getExamId() {
		return examId;
	}

	public void setExamId(Integer examId) {
		this.examId = examId;
	}

	public Integer getNo() {
		return no;
	}

	public void setNo(Integer no) {
		this.no = no;
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

	public Integer getQuestionType() {
		return questionType;
	}

	public void setQuestionType(Integer questionType) {
		this.questionType = questionType;
	}

}