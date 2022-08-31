package com.wcpdoc.exam.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.wcpdoc.core.util.ValidateUtil;

/**
 * 考试试题排序
 * 
 * v1.0 zhanghc 2017-05-26 14:23:38
 */
@Entity
@Table(name = "EXM_EXAM_QUESTION_NO")
public class ExamQuestionNo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	@Column(name = "EXAM_ID")
	private Integer examId;
	@Column(name = "USER_ID")
	private Integer userId;
	@Column(name = "NO")
	private String no;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public String getNo() {
		return no;
	}
	public void setNo(String no) {
		this.no = no;
	}
	
	public Integer[] setOptionNoArr(String optionNo) {
		if (!ValidateUtil.isValid(optionNo)) {
			return new Integer[0];
		}
		
		String[] optionNoStrArr = optionNo.split(",");// 接口层面需要返回数字类型
		Integer[] optionNoArr = new Integer[optionNoStrArr.length];
		for (int i = 0; i < optionNoStrArr.length; i++) {
			optionNoArr[i] = Integer.parseInt(optionNoStrArr[i]);
		}
		return optionNoArr;
	}
}
