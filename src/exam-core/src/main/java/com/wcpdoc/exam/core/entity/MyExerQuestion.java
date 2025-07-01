package com.wcpdoc.exam.core.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wcpdoc.core.mybatis.BigDecimalTypeHandler;
import com.wcpdoc.core.mybatis.IntTypeHandler;

import lombok.Data;

/**
 * 我的练习试题实体
 * 
 * v1.0 zhanghc 2025年6月8日下午9:22:47
 */
@Data
@TableName(value = "EXM_MY_EXER_QUESTION", autoResultMap = true)
public class MyExerQuestion {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private Integer exerId;
	private Integer userId;
	private Integer type;
	private Integer questionId;
	private Integer no;
	private Integer shuffleNo;
	private BigDecimal score;
	@TableField(typeHandler = BigDecimalTypeHandler.class)
	private List<BigDecimal> scores;
	@TableField(typeHandler = IntTypeHandler.class)
	private List<Integer> markOptions;
	private Date answerTime;
	private String userAnswer;
	private BigDecimal userScore;
	private Integer fav;
	private Integer wrongAnswerNum;
	private Integer updateUserId;
	private Date updateTime;
}