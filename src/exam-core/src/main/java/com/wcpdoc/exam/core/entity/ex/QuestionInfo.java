package com.wcpdoc.exam.core.entity.ex;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 试题信息
 * 
 * v1.0 zhanghc 2024年2月28日下午7:20:35
 */
@Data
public class QuestionInfo {
	private Integer questionId;
	private Integer questionType;
	private Integer markType;
	private String title;
	private List<Integer> markOptions;
	private BigDecimal score;
	private String analysis;
	private List<String> options = new ArrayList<>();
	private List<Object> answers = new ArrayList<>();
}