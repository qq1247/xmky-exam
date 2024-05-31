package com.wcpdoc.base.service.impl;

import javax.annotation.Resource;

import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.wcpdoc.base.constant.BaseConstant;
import com.wcpdoc.base.entity.ProgressBar;
import com.wcpdoc.base.service.ProgressBarService;

import lombok.extern.slf4j.Slf4j;

/**
 * 进度条服务层实现
 * 
 * v1.0 zhanghc 2024年4月14日下午9:36:00
 */
@Service
@Slf4j
public class ProgressBarServiceImpl implements ProgressBarService {
	@Resource
	private CacheManager cacheManager;

	@Override
	public void setProgressBar(String id, Double curNum, Double totalNum, Integer code, String msg, Object data) {
		log.debug("设置进度条：{}-{}-{}-{}-{}-{}", id, curNum, totalNum, code, msg, data);
		cacheManager.getCache(BaseConstant.PROGRESS_BAR_CACHE).put(BaseConstant.PROGRESS_BAR_KEY_PRE + id,
				new ProgressBar(id, curNum, totalNum, code, msg, data));
	}

	@Override
	public ProgressBar getProgressBar(String id) {
		ProgressBar progressBar = cacheManager.getCache(BaseConstant.PROGRESS_BAR_CACHE)
				.get(BaseConstant.PROGRESS_BAR_KEY_PRE + id, ProgressBar.class);
		log.debug("获取进度条：{}-{}-{}-{}-{}-{}", id, progressBar.getCurNum(), progressBar.getTotalNum(),
				progressBar.getCode(), progressBar.getMsg(), progressBar.getData());
		return progressBar;
	}
}
