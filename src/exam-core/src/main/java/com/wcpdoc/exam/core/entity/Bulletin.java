package com.wcpdoc.exam.core.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 公告实体
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
@Data
@TableName(value = "EXM_BULLETIN", autoResultMap = true)
public class Bulletin {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private Date startTime;
	private Date endTime;
	private String title;
	private String content;
	private Integer state;
	private Date updateTime;
	private Integer updateUserId;
}