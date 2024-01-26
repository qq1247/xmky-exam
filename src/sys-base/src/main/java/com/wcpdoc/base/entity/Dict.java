package com.wcpdoc.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 数据字典实体
 * 
 * v1.0 zhanghc 2016-11-3下午9:03:40
 */
@Data
@TableName(value = "SYS_DICT", autoResultMap = true)
public class Dict {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private String dictIndex;
	private String dictKey;
	private String dictValue;
	private Integer no;
}
