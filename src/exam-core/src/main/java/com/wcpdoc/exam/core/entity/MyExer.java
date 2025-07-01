package com.wcpdoc.exam.core.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 我的练习实体
 * 
 * v1.0 zhanghc 2025年6月8日下午9:22:47
 */
@Data
@TableName(value = "EXM_MY_EXER", autoResultMap = true)
public class MyExer {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private Integer exerId;
	private Integer userId;
	private Integer type;
	private Integer updateUserId;
	private Date updateTime;
}