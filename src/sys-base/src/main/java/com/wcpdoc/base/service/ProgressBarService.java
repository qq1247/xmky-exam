package com.wcpdoc.base.service;

import com.wcpdoc.base.entity.ProgressBar;

/**
 * 进度条服务层
 * 
 * v1.0 zhanghc 2017年7月5日下午4:22:50
 */
public interface ProgressBarService {
	/**
	 * 设置进度条
	 * 
	 * v1.0 zhanghc 2020年10月13日下午12:00:50
	 * 
	 * @param id       唯一ID
	 * @param curNum   当前已处理数量
	 * @param totalNum 总数量
	 * @param code     错误码
	 * @param msg      错误消息
	 * @param data     自定义数据
	 * @return void
	 */
	void setProgressBar(String id, Double curNum, Double totalNum, Integer code, String msg, Object data);

	/**
	 * 获取进度条
	 * 
	 * v1.0 zhanghc 2020年10月13日下午12:12:12
	 * 
	 * @param id
	 * @return ProgressBar
	 */
	ProgressBar getProgressBar(String id);
}
