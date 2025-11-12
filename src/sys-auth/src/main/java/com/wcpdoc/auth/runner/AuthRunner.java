package com.wcpdoc.auth.runner;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.wcpdoc.auth.service.JwtTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 系统权限启动
 * 
 * v1.0 chenyun 2021年11月16日下午1:44:19
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthRunner implements ApplicationRunner {
	private final JwtTokenService jwtTokenService;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		jwtTokenService.loadSecret();
		log.info("系统权限启动：加载秘钥");
	}
}