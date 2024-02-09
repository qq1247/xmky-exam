package com.wcpdoc.exam.core.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wcpdoc.core.mybatis.IntTypeHandler;

import lombok.Data;

/**
 * 考试规则
 * 
 * v1.0 chenyun 2022年2月11日 09:48:21
 */
@Data
@TableName(value = "EXM_EXAM_RULE", autoResultMap = true)
public class ExamRule {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private String chapterName;
	private String chapterTxt;
	private Integer type;
	private Integer questionTypeId;
	private Integer questionType;
	private Integer markType;
	@TableField(typeHandler = IntTypeHandler.class)
	private List<Integer> markOptions;
	private Integer num;
	private BigDecimal score;
	private BigDecimal scores;
	private Integer examId;
	private Integer no;
	private Integer updateUserId;
	private Date updateTime;

}