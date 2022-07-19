package com.wcpdoc.base.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.base.service.ParmExService;
import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.file.service.FileService;
import com.wcpdoc.notify.service.EmailService;
import com.wcpdoc.notify.service.NotifyService;

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
	private NotifyService notifyService;
	@Resource
	private FileService fileService;
	
	@Override
	public void setDao(BaseDao<Parm> dao) {
		
	}

	@Override
	public void emailUpdate(Parm parm) throws Exception {// 改成编译时异常，必须处理
		notifyService.pushEmail(
				parm.getEmailUserName(), 
				parm.getEmailUserName(), 
				parm.getOrgName() == null ? "在线考试邮箱配置" : String.format("%s 在线考试邮箱配置", parm.getOrgName()) , 
						"邮箱配置成功！");
	}
	
	@Override
	public void logoUpdate(Parm parm) {
		fileService.upload(parm.getOrgLogo());
	}

	@Override
	public void pushEmail(String from, String to, String title, String content) {
		
	}
}
