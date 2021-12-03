package com.wcpdoc.base.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.base.service.ParmExService;
import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.file.service.FileService;
import com.wcpdoc.notify.exception.EmailException;
import com.wcpdoc.notify.service.EmailService;

/**
 * 参数扩展服务层实现
 * 
 * v1.0 zhanghc 2016-6-11下午8:57:40
 */
@Service
public class ParmExServiceImpl extends BaseServiceImp<Parm> implements ParmExService {
	@Resource
	private EmailService emailService;
	@Resource
	private FileService fileService;
	
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
	
	@Override
	public void doUpload(Parm parm) {
		fileService.doUpload(parm.getOrgLogo());
	}
}
