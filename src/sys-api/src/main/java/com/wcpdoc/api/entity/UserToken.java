package com.wcpdoc.api.entity;

import lombok.Data;

/**
 * 用户令牌
 * 
 * v1.0 zhanghc 2017年7月17日下午4:20:21
 */
@Data
public class UserToken {
	private String accessToken;
	private Integer userId;
	private String userName;
	private Integer type;
}
