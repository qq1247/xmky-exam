package com.wcpdoc.sys.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.notify.entity.Email;
import com.wcpdoc.notify.exception.EmailException;
import com.wcpdoc.notify.service.EmailExService;

/**
 * 资源扩展服务层实现
 * 
 * v1.0 zhanghc 2016-6-11下午8:57:40
 */
@Service
public class EmailExServiceImpl implements EmailExService {
	@Resource
	private BaseCacheService baseCacheService;

	@Override
	public Email getEmail() throws EmailException {
		Parm parm = baseCacheService.getParm();
		if (!ValidateUtil.isValid(parm.getEmailHost()) || !ValidateUtil.isValid(parm.getEmailUserName())
				|| !ValidateUtil.isValid(parm.getEmailPwd()) || !ValidateUtil.isValid(parm.getEmailProtocol())
				|| !ValidateUtil.isValid(parm.getEmailEncode())) {
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
