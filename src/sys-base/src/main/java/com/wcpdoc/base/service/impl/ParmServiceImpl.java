package com.wcpdoc.base.service.impl;

import java.io.File;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.wcpdoc.base.constant.BaseConstant;
import com.wcpdoc.base.dao.ParmDao;
import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.base.service.ParmExService;
import com.wcpdoc.base.service.ParmService;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.ValidateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 参数服务层实现
 * 
 * v1.0 chenyun 2021-03-04 15:02:18
 */
@Service
@Slf4j
public class ParmServiceImpl extends BaseServiceImp<Parm> implements ParmService {
	@Resource
	private ParmDao parmDao;
	@Resource
	private ParmExService parmExService;
	@Resource
	private BaseCacheService baseCacheService;

	@Override
	public RBaseDao<Parm> getDao() {
		return parmDao;
	}

	@Override
	@CacheEvict(value = BaseConstant.PARM_CACHE, key = BaseConstant.PARM_KEY)
	public void emailUpdate(String host, String userName, String pwd, String protocol, String encode) {
		// 数据校验
		if (!ValidateUtil.isValid(host)) {
			throw new MyException("参数错误：host");
		}
		if (!ValidateUtil.isValid(userName)) {
			throw new MyException("参数错误：userName");
		}
		if (!ValidateUtil.isValid(pwd)) {
			throw new MyException("参数错误：pwd");
		}
		if (!ValidateUtil.isValid(protocol)) {
			throw new MyException("参数错误：protocol");
		}
		if (!ValidateUtil.isValid(encode)) {
			throw new MyException("参数错误：encode");
		}

		// 邮箱修改
		Parm entity = baseCacheService.getParm();
		entity.setEmailHost(host);
		entity.setEmailUserName(userName);
		entity.setEmailPwd(pwd);
		entity.setEmailProtocol(protocol);
		entity.setEmailEncode(encode);
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		updateById(entity);

		// 邮箱验证
		try {
			parmExService.emailUpdate(entity);
		} catch (Exception e) {
			throw new MyException(e.getMessage());
		}
	}

	@Override
	@CacheEvict(value = BaseConstant.PARM_CACHE, key = BaseConstant.PARM_KEY)
	public void entUpdate(Integer logoFileId, String name) throws Exception {
		// 数据校验
		// if (!ValidateUtil.isValid(logoFileId)) {// 不是每次都替换
		// throw new MyException("参数错误：logoFileId");
		// }
		if (!ValidateUtil.isValid(name)) {
			throw new MyException("参数错误：name");
		}

		// 企业信息更新
		Parm entity = baseCacheService.getParm();
		entity.setEntName(name);
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		updateById(entity);

		// 附件处理
		parmExService.entUpdate(logoFileId);
	}

	@Override
	@CacheEvict(value = BaseConstant.PARM_CACHE, key = BaseConstant.PARM_KEY)
	public void pwdUpdate(Integer type, String value) {
		// 数据校验
		if (type != 1 && type != 2) {
			throw new MyException("参数错误：type");
		}

		// 密码修改
		Parm entity = baseCacheService.getParm();
		entity.setPwdType(type);
		if (type == 2) {
			entity.setPwdValue(value);
		}
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		updateById(entity);
	}

	@Override
	@CacheEvict(value = BaseConstant.PARM_CACHE, key = BaseConstant.PARM_KEY)
	public void fileUpdate(String uploadDir) {
		// 数据校验
		if (!ValidateUtil.isValid(uploadDir)) {
			throw new MyException("参数错误：uploadDir");
		}

		// 上传地址修改
		Parm parm = baseCacheService.getParm();
		String oldFileUploadDir = parm.getFileUploadDir();
		if (oldFileUploadDir.equals(uploadDir)) {
			throw new MyException("目录名称相同");
		}
		parm.setUpdateTime(new Date());
		parm.setUpdateUserId(getCurUser().getId());
		parm.setFileUploadDir(uploadDir);
		updateById(parm);

		File oldFileUploadFile = new File(oldFileUploadDir);
		File uploadDirFile = new File(uploadDir);
		try {
			FileUtils.copyDirectoryToDirectory(oldFileUploadFile, uploadDirFile);// FileUtils.moveToDirectory();//
																					// 目录存在报错为已存在
			FileUtils.deleteDirectory(oldFileUploadFile);
		} catch (Exception e) {
			if (e.getMessage().contains("does not exist")) {
				throw new MyException("原备份目录不存在，不能移动");
			}
			log.error("移动上传备份目录失败：", e);
			throw new MyException("未知异常");
		}
	}

	@Override
	@CacheEvict(value = BaseConstant.PARM_CACHE, key = BaseConstant.PARM_KEY)
	public void dbUpdate(String bakDir) {
		// 数据校验
		if (!ValidateUtil.isValid(bakDir)) {
			throw new MyException("参数错误：bakDir");
		}

		Parm parm = baseCacheService.getParm();
		String oldBakDir = parm.getDbBakDir();
		if (oldBakDir.equals(bakDir)) {
			throw new MyException("目录名称相同");
		}

		// 备份地址修改
		parm.setUpdateTime(new Date());
		parm.setUpdateUserId(getCurUser().getId());
		parm.setDbBakDir(bakDir);
		updateById(parm);

		// 移动备份目录
		File oldBakFile = new File(String.format("%s%s%s%s%s", oldBakDir, File.separator, "bak", File.separator, "db"));
		File bakDirFile = new File(String.format("%s%s%s", bakDir, File.separator, "bak"));
		try {
			FileUtils.copyDirectoryToDirectory(oldBakFile, bakDirFile);// FileUtils.moveToDirectory();// 在已存在的目录上移动不支持
			FileUtils.deleteDirectory(oldBakFile);
		} catch (Exception e) {
			if (e.getMessage().contains("does not exist")) {
				throw new MyException("原备份目录不存在，不能移动");
			}
			log.error("移动数据库备份目录失败：", e);
			throw new MyException("未知异常");
		}
	}

	@Override
	@CacheEvict(value = BaseConstant.PARM_CACHE, key = BaseConstant.PARM_KEY)
	public void customUpdate(String title, String content) {
		// 数据校验
		if (!ValidateUtil.isValid(title)) {
			throw new MyException("参数错误：title");
		}
		if (!ValidateUtil.isValid(content)) {
			throw new MyException("参数错误：content");
		}

		// 自定义内容修改
		Parm parm = baseCacheService.getParm();
		parm.setCustomTitle(title);
		parm.setCustomContent(content);
		updateById(parm);
	}
}
