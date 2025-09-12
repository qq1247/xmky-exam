package com.wcpdoc.exam.core.entity;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wcpdoc.core.mybatis.IntTypeHandler;

import lombok.Data;

/**
 * 我的练习跟踪实体
 * 
 * v1.0 zhanghc 2025年9月8日下午7:25:39
 */
@Data
@TableName(value = "EXM_MY_EXER_TRACK", autoResultMap = true)
public class MyExerTrack {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private Integer exerId;
	private Integer userId;
	private Integer type;
	private Integer ymd;
	@TableField(typeHandler = IntTypeHandler.class)
	private List<Integer> minuteTicks;
	private Integer minuteCount;
	private Integer updateUserId;
	private Date updateTime;
}