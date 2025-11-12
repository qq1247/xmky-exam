package com.wcpdoc.file.service.impl;

import java.io.File;

import org.springframework.stereotype.Service;

import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.file.service.FileExService;

import lombok.RequiredArgsConstructor;

/**
 * 附件扩展服务层实现
 * 
 * v1.0 zhanghc 2016-6-11下午8:57:40
 */
@Service
@RequiredArgsConstructor
public class FileExServiceImpl extends BaseServiceImp<File> implements FileExService {
	private final BaseCacheService baseCacheService;

	@Override
	public RBaseDao<File> getDao() {
		return null;
	}

	@Override
	public String getFileUploadDir() {
		return baseCacheService.getParm().getFileUploadDir();
	}
}
