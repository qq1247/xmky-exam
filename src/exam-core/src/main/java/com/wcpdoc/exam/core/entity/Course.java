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
 * 课程实体
 * 
 * v1.0 zhanghc 2026-01-12 10:03:53
 */
@Data
@TableName(value = "EXM_COURSE", autoResultMap = true)
public class Course {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private String name;
	private String content;
	@TableField(typeHandler = IntTypeHandler.class)
	private List<Integer> orgIds;
	@TableField(typeHandler = IntTypeHandler.class)
	private List<Integer> userIds;
	private Integer shareAuth;
	private Integer state;
	private Integer createUserId;
	private Integer updateUserId;
	private Date updateTime;
}