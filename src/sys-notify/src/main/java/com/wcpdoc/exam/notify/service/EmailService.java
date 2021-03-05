package com.wcpdoc.exam.notify.service;

import org.springframework.mail.javamail.JavaMailSender;

import com.wcpdoc.exam.notify.exception.EmailException;

/**
 * 邮件服务接口
 * 
 * v1.0 zhanghc 2019年10月15日下午15:51:27
 */
public interface EmailService {
	
	/**
	 * 初始化
	 * 
	 * v1.0 zhanghc 2019年10月15日下午15:51:27
	 * void
	 */
	public void init() throws EmailException;
	
	/**
	 * 获取邮件服务
	 * 
	 * v1.0 zhanghc 2019年10月15日下午15:51:27
	 * void
	 */
	public JavaMailSender getJavaMailSender() throws EmailException;
}
