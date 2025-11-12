package com.wcpdoc.notify.service.impl;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import com.wcpdoc.notify.entity.Email;
import com.wcpdoc.notify.exception.EmailException;
import com.wcpdoc.notify.service.EmailExService;
import com.wcpdoc.notify.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 资源扩展服务层实现
 * 
 * v1.0 zhanghc 2016-6-11下午8:57:40
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
	private JavaMailSender javaMailSender;
	private final EmailExService emailExService;

	@Override
	public JavaMailSender getJavaMailSender() throws EmailException {
		if (javaMailSender == null) {
			synchronized (EmailServiceImpl.class) {
				if (javaMailSender == null) {
					log.info("初始化邮件服务开始");
					Email email = emailExService.getEmail();
					JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
					javaMailSenderImpl.setHost(email.getHost());
					javaMailSenderImpl.setUsername(email.getUserName());
					javaMailSenderImpl.setPassword(email.getPwd());
					javaMailSenderImpl.setProtocol(email.getProtocol());
					javaMailSenderImpl.setDefaultEncoding(email.getEncode());
					javaMailSender = javaMailSenderImpl;
					log.info("初始化邮件服务成功：{},{},{},{}", email.getHost(), email.getUserName(), email.getProtocol(),
							email.getEncode());
				}
			}
		}
		return javaMailSender;
	}
}
