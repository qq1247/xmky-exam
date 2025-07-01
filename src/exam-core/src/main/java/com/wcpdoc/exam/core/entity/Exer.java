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
	private Integer questionBankId;
	@TableField(typeHandler = IntTypeHandler.class)
	private List<Integer> userIds;
	@TableField(typeHandler = IntTypeHandler.class)
	private List<Integer> orgIds;
	private Integer state;
	private Integer rmkState;
	private Integer updateUserId;
	private Date updateTime;
}