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
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.core.util.ValidateUtil;

/**
 * 我的试题实体
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Entity
@Table(name = "EXM_MY_QUESTION")
public class MyQuestion {
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
	@Column(name = "EXAM_ID")
	private Integer examId;
	@Column(name = "QUESTION_ID")
	private Integer questionId;
	@Column(name = "USER_ID")
	private Integer userId;
	@Column(name = "NO")
	private Integer no;
	@Column(name = "OPTIONS_NO")
	private String optionsNo;
	@DateTimeFormat(pattern = DateUtil.FORMAT_DATE_TIME)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "ANSWER_TIME")
	private Date answerTime;
	@Column(name = "ANSWER")
	private String answer;
	@Column(name = "USER_SCORE")
	private BigDecimal userScore;
	@Column(name = "MARK_USER_ID")
	private Integer markUserId;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "MARK_TIME")
	private Date markTime;
	@Column(name = "UPDATE_USER_ID")
	private Integer updateUserId;
	@DateTimeFormat(pattern = DateUtil.FORMAT_DATE_TIME)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "UPDATE_TIME")
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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
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

	public Integer getExamId() {
		return examId;
	}

	public void setExamId(Integer examId) {
		this.examId = examId;
	}

	public Integer getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Integer questionId) {
		this.questionId = questionId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getNo() {
		return no;
	}

	public void setNo(Integer no) {
		this.no = no;
	}

	public Integer[] getOptionsNo() {
		if (!ValidateUtil.isValid(optionsNo)) {
			return new Integer[0];
		}
	
		String[] markOptionStrArr = optionsNo.split(",");
		Integer[] markOptionArr = new Integer[markOptionStrArr.length];
		for (int i = 0; i < markOptionStrArr.length; i++) {
			markOptionArr[i] = Integer.parseInt(markOptionStrArr[i]);
		}
		return markOptionArr;
	}

	public void setOptionsNo(Integer[] optionsNo) {
		if (!ValidateUtil.isValid(optionsNo)) {
			this.optionsNo = null;
			return;
		}
		
		this.optionsNo = StringUtil.join(optionsNo);
	}
	
	public Date getAnswerTime() {
		return answerTime;
	}

	public void setAnswerTime(Date answerTime) {
		this.answerTime = answerTime;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public BigDecimal getUserScore() {
		return userScore;
	}

	public void setUserScore(BigDecimal userScore) {
		this.userScore = userScore;
	}

	public Integer getMarkUserId() {
		return markUserId;
	}

	public void setMarkUserId(Integer markUserId) {
		this.markUserId = markUserId;
	}

	public Date getMarkTime() {
		return markTime;
	}

	public void setMarkTime(Date markTime) {
		this.markTime = markTime;
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
}
