package com.wcpdoc.exam.core.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 我的收藏实体
 * 
 * v1.0 zhanghc 2025年6月8日下午9:22:47
 */
@Data
@TableName(value = "EXM_MY_FAV", autoResultMap = true)
public class MyFav {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private Integer exerId;
	private Integer userId;
	private Integer questionId;
	private Integer fav;
	private Integer num;
	private Integer updateUserId;
	private Date updateTime;
}