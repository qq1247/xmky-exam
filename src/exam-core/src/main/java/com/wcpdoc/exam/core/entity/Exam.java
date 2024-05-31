package com.wcpdoc.exam.core.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wcpdoc.core.mybatis.IntTypeHandler;

import lombok.Data;

/**
 * 考试实体
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
@Data
@TableName(value = "EXM_EXAM", autoResultMap = true)
public class Exam {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private String name;
	private String paperName;
	private Date startTime;
	private Date endTime;
	private Date markStartTime;
	private Date markEndTime;
	private Integer markState;
	private Integer scoreState;
	private Integer rankState;
	private Integer anonState;
	private BigDecimal passScore;
	private BigDecimal totalScore;
	private Integer markType;
	private Integer showType;
	private Integer genType;
	@TableField(typeHandler = IntTypeHandler.class)
	private List<Integer> sxes;
	private Integer userNum;
	private Integer state;
	private Integer limitMinute;
	private Integer createUserId;
	private Integer updateUserId;
	private Date updateTime;
}
