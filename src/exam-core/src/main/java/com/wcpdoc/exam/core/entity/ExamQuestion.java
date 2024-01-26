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
 * 考试试题实体
 * 
 * v1.0 zhanghc 2017-05-26 14:23:38
 */
@Data
@TableName(value = "EXM_EXAM_QUESTION", autoResultMap = true)
public class ExamQuestion {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private String chapterName;
	private String chapterTxt;
	private Integer type;
	private BigDecimal score;
	@TableField(typeHandler = BigDecimalTypeHandler.class)
	private List<BigDecimal> scores;
	@TableField(typeHandler = IntTypeHandler.class)
	private List<Integer> markOptions;
	private Integer examId;
	private Integer questionId;
	private Integer no;
	private String optionsNo;
	private Integer updateUserId;
	private Date updateTime;
}
