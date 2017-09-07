package com.wcpdoc.exam.core.constant;

/**
 * 常量管理
 * v1.0 zhanghc 2016年9月6日下午6:45:12
 */
public class ConstantManager {
	/** 当前登录用户，存放在session */
	public static final String USER = "USER";
	/** 权限合计，存放在session */
	public static final String USER_AUTH_MAP = "USER_AUTH_MAP";
	
	
	/** 登陆用户列表，存放在ServletContext */
	public static final String SESSION_USER_LIST = "SESSION_USER_LIST";
	/** 菜单，存放在ServletContext */
	public static final String MENU_LIST = "MENU_LIST";
	/** 前台菜单，存放在ServletContext */
	public static final String HOME_MENU_LIST = "HOME_MENU_LIST";
}
