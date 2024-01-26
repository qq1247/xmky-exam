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
 * 练习实体
 * 
 * v1.0 chenyun 2021-03-02 13:43:21
 */
@Data
@TableName(value = "EXM_EXER", autoResultMap = true)
public class Exer {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private String name;
	private Integer questionTypeId;
	private Date startTime;
	private Date endTime;
	@TableField(typeHandler = IntTypeHandler.class)
	private List<Integer> userIds;
	private Integer state;
	private Integer rmkState;
	private Integer createUserId;
	private Integer updateUserId;
	private Date updateTime;
}