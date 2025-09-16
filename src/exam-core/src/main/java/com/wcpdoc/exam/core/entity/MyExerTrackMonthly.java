package com.wcpdoc.exam.core.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 我的练习跟踪月度实体
 * 
 * v1.0 zhanghc 2025年9月8日下午7:25:39
 */
@Data
@TableName(value = "EXM_MY_EXER_TRACK_MONTHLY", autoResultMap = true)
public class MyExerTrackMonthly {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private Integer exerId;
	private Integer userId;
	private Integer ym;
	private Integer minuteCount;
	private Integer updateUserId;
	private Date updateTime;
}