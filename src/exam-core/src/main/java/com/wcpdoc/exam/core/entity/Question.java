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
 * 试题实体
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
@Data
@TableName(value = "EXM_QUESTION", autoResultMap = true)
public class Question {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private Integer type;
	private String title;
	private Integer markType;
	@TableField(typeHandler = IntTypeHandler.class)
	private List<Integer> markOptions;
	private String analysis;
	private BigDecimal score;
	private Integer state;
	private Integer questionTypeId;
	private Integer createUserId;
	private Integer updateUserId;
	private Date updateTime;
}
