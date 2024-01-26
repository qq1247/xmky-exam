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
 * 我的阅卷实体
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Data
@TableName(value = "EXM_MY_MARK", autoResultMap = true)
public class MyMark {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private Integer examId;
	private Integer markUserId;
	@TableField(typeHandler = IntTypeHandler.class)
	private List<Integer> questionIds;
	private Integer updateUserId;
	private Date updateTime;
}
