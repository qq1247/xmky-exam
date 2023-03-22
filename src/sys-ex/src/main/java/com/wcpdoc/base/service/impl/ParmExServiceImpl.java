package com.wcpdoc.base.service.impl;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.base.service.ParmExService;
import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.file.entity.FileEx;
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
	private static final Logger log = LoggerFactory.getLogger(ParmExServiceImpl.class);
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
		notifyService.emailPush(
				parm.getEmailUserName(), 
				parm.getEmailUserName(), 
				parm.getEntName() == null ? "在线考试邮箱配置" : String.format("%s 在线考试邮箱配置", parm.getEntName()) , 
						"邮箱配置成功！");
	}
	
	@Override
	public void entUpdate(Integer logoFileId) {
		if (ValidateUtil.isValid(logoFileId)) {
			FileEx fileEx = fileService.getFileEx(logoFileId);
			File srcFile = fileEx.getFile();
			File destFile = new File("./config/logo.png");
			try {
				FileUtils.copyFile(srcFile, destFile);
			} catch (IOException e) {
				log.error("拷贝文件失败：", e);
				throw new MyException("拷贝文件失败");
			}
		}
		
	}

	@Override
	public void pushEmail(String from, String to, String title, String content) {
		
	}
}
