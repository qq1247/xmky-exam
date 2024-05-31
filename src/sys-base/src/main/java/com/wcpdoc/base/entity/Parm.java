package com.wcpdoc.base.entity;

import java.io.File;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wcpdoc.core.util.ValidateUtil;

import lombok.Data;

/**
 * 参数实体
 * 
 * v1.0 chenyun 2021-03-04 15:02:18
 */
@Data
@TableName(value = "SYS_PARM", autoResultMap = true)
public class Parm {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private String emailHost;
	private String emailEncode;
	private String emailUserName;
	private String emailPwd;
	private String emailProtocol;
	private String entName;
	private Integer updateUserId;
	private Date updateTime;
	private String fileUploadDir;
	private String dbBakDir;
	private Integer pwdType;
	private String pwdValue;
	private String customTitle;
	private String customContent;

	public String getFileUploadDir() {
		if (ValidateUtil.isValid(fileUploadDir)) {
			return "";
		}
		return fileUploadDir.replace("\\", File.separator).replace("/", File.separator);
	}

	public String getDbBakDir() {
		if (ValidateUtil.isValid(dbBakDir)) {
			return "";
		}
		return dbBakDir.replace("\\", File.separator).replace("/", File.separator);
	}
}