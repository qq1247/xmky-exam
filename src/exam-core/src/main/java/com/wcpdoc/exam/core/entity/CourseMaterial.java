package com.wcpdoc.exam.core.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 课程资料实体
 * 
 * v1.0 zhanghc 2026-01-12 10:03:53
 */
@Data
@TableName(value = "EXM_COURSE_MATERIAL", autoResultMap = true)
public class CourseMaterial {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private String name;
	private String content;
	private Integer videoFileId;
	private Integer videoSecond;
	private Integer questionNum;
	private Integer courseId;
	private Integer no;
	private Integer parentId;
	private Integer state;
	private Integer updateUserId;
	private Date updateTime;
}