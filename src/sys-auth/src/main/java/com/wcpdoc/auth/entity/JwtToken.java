package com.wcpdoc.auth.entity;

import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * jwt令牌
 * 
 * v1.0 zhanghc 2025年11月9日上午9:38:22
 */
@Data
@Builder
public class JwtToken {
	private Integer userId;
	private String LoginName;
	private List<String> roles;
}
