package com.wcpdoc.exam.sys.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.base.entity.Parm;
import com.wcpdoc.exam.base.service.ParmService;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.notify.entity.Email;
import com.wcpdoc.exam.notify.exception.EmailException;
import com.wcpdoc.exam.notify.service.EmailExService;

/**
 * 资源扩展服务层实现
 * 
 * v1.0 zhanghc 2016-6-11下午8:57:40
 */
@Service
public class EmailExServiceImpl implements EmailExService {
	@Resource
	private ParmService parmService;

	@Override
	public Email getEmail() throws EmailException {
		Parm parm = parmService.getEntity(1);
		if (!ValidateUtil.isValid(parm.getEmailHost()) || !ValidateUtil.isValid(parm.getEmailUserName()) || 
				!ValidateUtil.isValid(parm.getEmailPwd()) || !ValidateUtil.isValid(parm.getEmailProtocol()) || 
				!ValidateUtil.isValid(parm.getEmailEncode())) {
			throw new EmailException("请配置系统参数：邮件相关");
		}
		
		Email email = new Email();
		email.setHost(parm.getEmailHost());
		email.setUserName(parm.getEmailUserName());
		email.setPwd(parm.getEmailPwd());
		email.setProtocol(parm.getEmailProtocol());
		email.setEncode(parm.getEmailEncode());
		return email;
	}
}
