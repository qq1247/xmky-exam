package com.wcpdoc.notify.service;

import com.wcpdoc.notify.exception.NotifyException;

/**
 * 通知服务接口
 * 
 * v1.0 zhanghc 2019年10月15日下午15:51:27
 */
public interface NotifyService {
	
	/**
	 * 发送邮件
	 * 
	 * v1.0 zhanghc 2019年10月15日下午15:51:27
	 * @param from 发送邮件地址
	 * @param to 接收邮件地址（多邮件用英文逗号分隔）
	 * @param title 标题
	 * @param content 内容
	 * void
	 */
	void pushEmail(String from, String to, String title, String content) throws NotifyException;
	
	/**
	 * 发送短信
	 * 
	 * v1.0 zhanghc 2019年10月15日下午15:51:27
	 * @param phone 手机号
	 * @param code 验证码
	 * void
	 */
	void pushSMS(String phone, String code) throws NotifyException;
	
}
