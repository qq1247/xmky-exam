package com.wcpdoc.file.service.impl;

import java.io.File;

import org.springframework.stereotype.Service;

import com.wcpdoc.base.cache.ParmCache;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.file.service.FileExService;

/**
 * 附件扩展服务层实现
 * 
 * v1.0 zhanghc 2016-6-11下午8:57:40
 */
@Service
public class FileExServiceImpl extends BaseServiceImp<File> implements FileExService {
	@Override
	public RBaseDao<File> getDao() {
		return null;
	}

	@Override
	public String getFileUploadDir() {
		return ParmCache.get().getFileUploadDir();
	}
}
