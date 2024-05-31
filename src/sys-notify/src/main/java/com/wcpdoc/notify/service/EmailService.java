package com.wcpdoc.notify.service;

import org.springframework.mail.javamail.JavaMailSender;

import com.wcpdoc.notify.exception.EmailException;

/**
 * 邮件服务接口
 * 
 * v1.0 zhanghc 2019年10月15日下午15:51:27
 */
public interface EmailService {
	/**
	 * 获取邮件服务
	 * 
	 * v1.0 zhanghc 2019年10月15日下午15:51:27 void
	 */
	public JavaMailSender getJavaMailSender() throws EmailException;
}
