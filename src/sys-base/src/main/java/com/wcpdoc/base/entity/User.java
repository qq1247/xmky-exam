package com.wcpdoc.base.entity;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wcpdoc.base.constant.BaseConstant;
import com.wcpdoc.core.entity.LoginUser;
import com.wcpdoc.core.mybatis.IntTypeHandler;
import com.wcpdoc.core.util.ValidateUtil;

import lombok.Data;

/**
 * 用户实体
 * 
 * v1.0 zhanghc 2016-6-15下午17:24:19
 */
@Data
@TableName(value = "SYS_USER", autoResultMap = true)
public class User implements LoginUser {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private String name;
	private String loginName;
	private String email;
	private String phone;
	private String pwd;
	private Date registTime;
	private Date lastLoginTime;
	private Integer updateUserId;
	private Date updateTime;
	private Integer orgId;
	private Integer state;
	private Integer headFileId;
	private String role;
	private Integer parentId;
	@TableField(typeHandler = IntTypeHandler.class)
	private List<Integer> userIds;
	@TableField(typeHandler = IntTypeHandler.class)
	private List<Integer> orgIds;

	public boolean hasAdmin() {
		return ValidateUtil.isValid(role) && role.equals(BaseConstant.ADMIN);
	}

	public boolean hasSubAdmin() {
		return ValidateUtil.isValid(role) && role.equals(BaseConstant.SUB_ADMIN);
	}

	public boolean hasExamUser() {
		return ValidateUtil.isValid(role) && role.equals(BaseConstant.EXAM_USER);
	}

	public boolean hasMarkUser() {
		return ValidateUtil.isValid(role) && role.equals(BaseConstant.MARK_USER);
	}
}
