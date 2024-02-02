package com.wcpdoc.file.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 附件实体
 * 
 * v1.0 zhanghc 2016-11-16下午10:13:48
 */
@Data
@TableName(value = "SYS_FILE", autoResultMap = true)
public class File {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private String name;
	private String extName;
	private String fileType;
	private String path;
	private String ip;
	private Integer state;
	private Integer updateUserId;
	private Date updateTime;
}
