package com.wcpdoc.exam.base.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.base.entity.Parm;
import com.wcpdoc.exam.base.service.ParmExService;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.notify.exception.EmailException;
import com.wcpdoc.exam.notify.service.EmailService;

/**
 * 参数扩展服务层实现
 * 
 * v1.0 zhanghc 2016-6-11下午8:57:40
 */
@Service
public class ParmExServiceImpl extends BaseServiceImp<Parm> implements ParmExService {
	@Resource
	private EmailService emailService;

	@Override
	public void addAndUpdate(Parm parm) {
		try {
			emailService.init();
		} catch (EmailException e) {
			throw new MyException(e.getMessage());
		}
	}

	@Override
	public void updateAndUpdate(Parm parm) {
		try {
			emailService.init();
		} catch (EmailException e) {
			throw new MyException(e.getMessage());
		}
	}

	@Override
	public void setDao(BaseDao<Parm> dao) {
		
	}
}
