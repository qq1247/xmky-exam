package com.wcpdoc.exam.api.service;

import javax.security.auth.login.LoginException;

import com.wcpdoc.exam.api.entity.PersonToken;


/**
 * 登录业务层
 * 
 * v1.0 chenyun 2020年11月25日上午9:11:02
 */
public interface ApiLoginService{
	
	/**
	 * 登录
	 * 
	 * v1.0 chenyun 2020年11月25日上午9:11:02
	 * @param id
	 * @return PersonToken
	 */
	PersonToken in(String loginName, String pwd, String code) throws LoginException;
}
