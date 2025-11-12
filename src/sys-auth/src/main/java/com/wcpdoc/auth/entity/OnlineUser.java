package com.wcpdoc.auth.entity;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * 在线用户实体
 * 
 * v1.0 zhanghc 2021年10月15日下午3:28:26
 */
@Data
@Builder
public class OnlineUser {
	private Integer id;
	private String loginName;
	private String role;
	private String ip;
	private Date updateTime;
}
