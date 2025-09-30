package com.wcpdoc.exam.core.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 我的收藏试题实体
 * 
 * v1.0 zhanghc 2025年9月26日下午9:51:28
 */
@Data
@TableName(value = "EXM_MY_FAV_QUESTION", autoResultMap = true)
public class MyFavQuestion {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private Integer userId;
	private Integer questionId;
	private Integer questionType;
	private Date favTime;
	private String favSource;
	private Integer updateUserId;
	private Date updateTime;
}