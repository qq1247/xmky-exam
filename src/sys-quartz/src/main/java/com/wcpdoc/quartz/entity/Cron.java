package com.wcpdoc.quartz.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 定时任务实体
 * 
 * v1.0 zhanghc 2019-07-29 10:38:17
 */
@Data
@TableName("SYS_CRON")
public class Cron {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private String name;
	private String jobClass;
	private String cron;
	private Integer state;
	private Integer updateUserId;
	private Date updateTime;

}
