package com.wcpdoc.exam.core.entity.ex;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 试卷试题部分
 * 
 * v1.0 zhanghc 2024年2月28日下午7:20:35
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionPart extends PaperPart {
	private Integer questionId;
	private Integer questionType;
	private Integer markType;
	private String title;
	private List<Integer> markOptions;
	private BigDecimal score;
	private String analysis;
	private List<String> options = new ArrayList<>();
	private List<Object> answers = new ArrayList<>();

	private BigDecimal userScore;
	private List<String> userAnswers = new ArrayList<>();
}