package com.wcpdoc.exam.core.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 我的错题实体
 * 
 * v1.0 zhanghc 2025年9月26日下午9:51:28
 */
@Data
@TableName(value = "EXM_MY_WRONG_QUESTION", autoResultMap = true)
public class MyWrongQuestion {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private Integer userId;
	private Integer questionId;
	private Integer questionType;
	private Integer wrongNum;
	private Date firstWrongTime;
	private String firstWrongSource;
	private Date lastWrongTime;
	private String lastWrongSource;
	private Integer state;
	private Integer updateUserId;
	private Date updateTime;
}