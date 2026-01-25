package com.wcpdoc.exam.core.entity;

import java.time.LocalTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 我的课程资料实体
 * 
 * v1.0 zhanghc 2026-01-12 10:03:53
 */
@Data
@TableName(value = "EXM_MY_COURSE_MATERIAL", autoResultMap = true)
public class MyCourseMaterial {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private Integer courseId;
	private Integer courseMaterialId;
	private Integer userId;
	private String name;
	private String content;
	private Integer videoFileId;
	private LocalTime videoTime;
	private Integer questionNum;
	private Integer no;
	private LocalTime watchTime;
	private LocalTime activeTime;
	private Integer state;
	private Integer updateUserId;
	private Date updateTime;
}