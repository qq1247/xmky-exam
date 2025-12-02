package com.wcpdoc.base.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Builder;
import lombok.Data;

/**
 * 注册用户实体
 * 
 * v1.0 zhanghc 2025年12月1日下午4:15:06
 */
@Data
@TableName(value = "SYS_REGIST_USER", autoResultMap = true)
@Builder
public class RegistUser {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private String name;
	private String loginName;
	private String pwd;
	private Date registTime;
	private Integer orgId;
	private Integer state;
	private String remark;
	private Integer updateUserId;
	private Date updateTime;
}
