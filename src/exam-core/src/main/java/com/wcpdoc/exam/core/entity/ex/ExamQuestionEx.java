package com.wcpdoc.exam.core.entity.ex;

import java.util.List;

import com.wcpdoc.exam.core.entity.ExamQuestion;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 考试试题扩展
 * 
 * v1.0 zhanghc 2022年9月15日上午10:33:48
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExamQuestionEx extends ExamQuestion {
	private Integer questionType;
	private Integer markType;
	private String title;
	private List<Integer> imgFileIds;
	private List<String> options;
	private List<String> answers;
	private String analysis;
}