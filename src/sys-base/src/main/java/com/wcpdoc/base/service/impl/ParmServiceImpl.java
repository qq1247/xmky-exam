package com.wcpdoc.base.service.impl;

import java.io.File;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.wcpdoc.base.cache.ParmCache;
import com.wcpdoc.base.dao.ParmDao;
import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.base.service.ParmExService;
import com.wcpdoc.base.service.ParmService;
import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.notify.exception.NotifyException;
import com.wcpdoc.notify.service.NotifyService;

/**
 * 参数服务层实现
 * 
 * v1.0 chenyun 2021-03-04 15:02:18
 */
@Service
public class ParmServiceImpl extends BaseServiceImp<Parm> implements ParmService {
	private static final Logger log = LoggerFactory.getLogger(ParmServiceImpl.class);
	@Resource
	private ParmDao parmDao;
	@Resource
	private ParmExService parmExService;
	@Resource
	private NotifyService notifyService;
	
	@Override
	@Resource(name = "parmDaoImpl")
	public void setDao(BaseDao<Parm> dao) {
		super.dao = dao;
	}

	@Override
	public void addAndUpdate(Parm parm) {
		// 添加参数
		add(parm);
		
		// 扩展处理
		parmExService.addAndUpdate(parm);
		ParmCache.flushCache(parm);
	}
	
	@Override
	public void updateAndUpdate(Parm parm) {
		// 添加参数
		update(parm);
		
		// 扩展处理
		ParmCache.flushCache(parm);
		parmExService.updateAndUpdate(parm);

		// 发送邮件
		try {
			notifyService.pushEmail(parm.getEmailUserName(), parm.getEmailUserName(),
					parm.getOrgName() == null ? "在线考试邮箱配置" : String.format("%s 在线考试邮箱配置", parm.getOrgName()) , "邮箱配置成功！");
		} catch (NotifyException e) {
			throw new MyException(e.getMessage());
		}
	}

	@Override
	public void editLogo(Parm parm) throws Exception {
		// 更新logo
		Parm entity = getEntity(1);
		Integer orgLogo = entity.getOrgLogo();
		entity.setOrgLogo(parm.getOrgLogo());
		entity.setOrgName(parm.getOrgName());
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		parmDao.update(entity);
		
		// 保存附件
		if (parm.getOrgLogo() != null && parm.getOrgLogo() != orgLogo) {
			parmExService.doUpload(parm);
		}
		
		// 配置刷入缓存
		ParmCache.flushCache(entity);
	}

	@Override
	public void pwd(Integer type, String value) {
		//校验数据有效性
		if (type != 1 && type != 2) {
			throw new MyException("参数错误：type");
		}
		
		Parm entity = getEntity(1);
		entity.setPwdType(type);
		if (type == 2) {
			entity.setPwdValue(value);
		}
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		parmDao.update(entity);
		ParmCache.flushCache(entity);
	}

	@Override
	public void file(String uploadDir) {
		//校验数据有效性
		if (!ValidateUtil.isValid(uploadDir)) {
			throw new MyException("参数错误：uploadDir");
		}
		
		Parm parm = getEntity(1);
		String oldFileUploadDir = parm.getFileUploadDir();
		if (oldFileUploadDir.equals(uploadDir)) {
			throw new MyException("目录名称相同");
		}
		parm.setUpdateTime(new Date());
		parm.setUpdateUserId(getCurUser().getId());
		parm.setFileUploadDir(uploadDir);
		parmDao.update(parm);
		
		File oldFileUploadFile = new File(String.format("%s%s%s%s%s", oldFileUploadDir, File.separator, "bak", File.separator, "file"));
		File uploadDirFile = new File(String.format("%s%s%s", uploadDir, File.separator, "bak"));
		try {
			FileUtils.copyDirectoryToDirectory(oldFileUploadFile, uploadDirFile);//FileUtils.moveToDirectory();// 目录存在报错为已存在
			FileUtils.deleteDirectory(oldFileUploadFile);
		} catch (Exception e) {
			if (e.getMessage().contains("does not exist")) {
				throw new MyException("原备份目录不存在，不能移动");
			}
			log.error("移动上传备份目录失败：", e);
			throw new MyException("未知异常");
		}
		ParmCache.flushCache(parm);
	}
	
	@Override
	public void db(String bakDir) {
		//校验数据有效性
		if (!ValidateUtil.isValid(bakDir)) {
			throw new MyException("参数错误：bakDir");
		}
		
		Parm parm = getEntity(1);
		String oldBakDir = parm.getDbBakDir();
		if (oldBakDir.equals(bakDir)) {
			throw new MyException("目录名称相同");
		}
		
		// 修改参数
		parm.setUpdateTime(new Date());
		parm.setUpdateUserId(getCurUser().getId());
		parm.setDbBakDir(bakDir);
		parmDao.update(parm);
		
		// 移动备份目录
			File oldBakFile = new File(String.format("%s%s%s%s%s", oldBakDir, File.separator, "bak", File.separator, "db"));
			File bakDirFile = new File(String.format("%s%s%s", bakDir, File.separator, "bak"));
			try {
				FileUtils.copyDirectoryToDirectory(oldBakFile, bakDirFile);//FileUtils.moveToDirectory();// 在已存在的目录上移动不支持
				FileUtils.deleteDirectory(oldBakFile);
			} catch (Exception e) {
				if (e.getMessage().contains("does not exist")) {
					throw new MyException("原备份目录不存在，不能移动");
				}
				log.error("移动数据库备份目录失败：", e);
				throw new MyException("未知异常");
			}
		
		// 更新缓存
		ParmCache.flushCache(parm);
	}
}
