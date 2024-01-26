package com.wcpdoc.exam.core.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 试题答案实体
 * 
 * v1.0 chenyun 2021-07-20 18:14:32
 */
@Data
@TableName(value = "EXM_QUESTION_ANSWER", autoResultMap = true)
public class QuestionAnswer {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private String answer;
	private BigDecimal score;
	private Integer questionId;
	private Integer no;
}
