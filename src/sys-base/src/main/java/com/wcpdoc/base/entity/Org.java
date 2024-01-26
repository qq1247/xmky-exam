package com.wcpdoc.base.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 机构实体
 * 
 * v1.0 zhanghc 2016-5-8上午11:00:00
 */
@Data
@TableName(value = "SYS_ORG", autoResultMap = true)
public class Org {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private String name;
	private String code;
	private Integer level;
	private Integer parentId;
	private String parentIds;
	private Integer updateUserId;
	private Date updateTime;
	private Integer state;
	private Integer no;
}
