package com.wcpdoc.exam.core.entity;

import java.time.LocalTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 我的课程试题实体
 * 
 * v1.0 zhanghc 2026-01-12 10:03:53
 */
@Data
@TableName(value = "EXM_MY_COURSE_QUESTION", autoResultMap = true)
public class MyCourseQuestion {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private Integer courseId;
	private Integer courseMaterialId;
	private Integer questionId;
	private LocalTime courseTime;
	private Integer userId;
	private Date answerTime;
	private Integer state;
	private Integer updateUserId;
	private Date updateTime;
}