package com.wcpdoc.exam.core.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 我的作弊实体
 * 
 * v1.0 zhanghc 2025年3月16日上午11:21:42
 */
@Data
@TableName(value = "EXM_MY_SXE", autoResultMap = true)
public class MySxe {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private Integer examId;
	private Integer userId;
	private Integer type;
	private String content;
	private Date updateTime;
}
