package com.wcpdoc.exam.base.service.impl;

import javax.annotation.Resource;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import com.wcpdoc.exam.base.dao.EmailDao;
import com.wcpdoc.exam.base.entity.Email;
import com.wcpdoc.exam.base.service.EmailService;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;

/**
 * 邮箱服务层实现
 * 
 * v1.0 chenyun 2021-03-04 15:02:18
 */
@Service
public class EmailServiceImpl extends BaseServiceImp<Email> implements EmailService {
	@Resource
	private EmailDao emailDao;

	@Override
	@Resource(name = "emailDaoImpl")
	public void setDao(BaseDao<Email> dao) {
		super.dao = dao;
	}
	
	@Override
	public void delAndUpdate(Integer id) {
		// 校验数据有效性
		Email entity = getEntity(id);
		update(entity);
	}

	@Override
	public Email getEmail(Integer userId) {
		return emailDao.getEmail(userId);
	}
	
	@Override
	public void sendEmail(String receiverName, String receiverTitle, String receiverContent) {
		long zstartTime = System.currentTimeMillis(); 
		Email email = emailDao.getEmail(1);//getCurUser().getId()
		
		long startTime = System.currentTimeMillis(); 
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		javaMailSender.setHost(email.getEmailHost());
		javaMailSender.setDefaultEncoding("UTF-8");
		javaMailSender.setUsername(email.getEmailName());
		javaMailSender.setPassword(email.getEmailPwd());
		javaMailSender.setProtocol("smtp");
		long endTime = System.currentTimeMillis(); 
		System.out.println("JavaMailSenderImpl 耗时：" + (endTime - startTime) + "ms");
		
		for(int i = 0; i < 10 ; i++){
			long sendstartTime = System.currentTimeMillis(); 
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(email.getEmailName());
			message.setTo(receiverName);
			message.setSubject(receiverTitle);
			message.setText(receiverContent);
			javaMailSender.send(message);	
			long sendendTime = System.currentTimeMillis(); 
			System.out.println("发送邮件 耗时：" + (sendendTime - sendstartTime) + "ms");
		}
		long zendTime = System.currentTimeMillis();
		System.out.println("一共耗时：" + (zendTime - zstartTime) + "ms");
	}
}
