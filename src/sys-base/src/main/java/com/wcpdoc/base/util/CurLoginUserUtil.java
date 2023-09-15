package com.wcpdoc.base.util;

import com.wcpdoc.base.entity.User;
import com.wcpdoc.core.context.UserContext;

/**
 * 当前登录用户工具类
 * 
 * v1.0 zhanghc 2023年9月15日上午10:38:21
 */
public class CurLoginUserUtil {
	/**
	 * 是否管理员
	 * 
	 * v1.0 zhanghc 2023年9月15日上午10:41:30
	 * @return boolean
	 */
	public static boolean isAdmin() {
		return UserContext.get().getType() == 0;
	}
	
	/**
	 * 是否管理员
	 * 
	 * v1.0 zhanghc 2023年9月15日上午10:41:30
	 * @return boolean
	 */
	public static boolean isAdmin(User user) {
		return user.getType() == 0;
	}

	/**
	 * 是否考试用户
	 * 
	 * v1.0 zhanghc 2023年9月15日上午10:41:37
	 * @return boolean
	 */
	public static boolean isExamUser() {
		return UserContext.get().getType() == 1;
	}
	
	/**
	 * 是否考试用户
	 * 
	 * v1.0 zhanghc 2023年9月15日上午10:41:37
	 * @return boolean
	 */
	public static boolean isExamUser(User user) {
		return user.getType() == 1;
	}

	/**
	 * 是否子管理员
	 * 
	 * v1.0 zhanghc 2023年9月15日上午10:41:37
	 * @return boolean
	 */
	public static boolean isSubAdmin() {
		return UserContext.get().getType() == 2;
	}
	
	/**
	 * 是否子管理员
	 * 
	 * v1.0 zhanghc 2023年9月15日上午10:41:37
	 * @return boolean
	 */
	public static boolean isSubAdmin(User user) {
		return user.getType() == 2;
	}

	/**
	 * 是否阅卷用户
	 * 
	 * v1.0 zhanghc 2023年9月15日上午10:41:56
	 * @return boolean
	 */
	public static boolean isMarkUser() {
		return UserContext.get().getType() == 3;
	}
	
	/**
	 * 是否阅卷用户
	 * 
	 * v1.0 zhanghc 2023年9月15日上午10:41:56
	 * @return boolean
	 */
	public static boolean isMarkUser(User user) {
		return user.getType() == 3;
	}
	
	/**
	 * 是否自己
	 * 
	 * v1.0 zhanghc 2023年9月15日上午10:41:56
	 * @param checkUserId 待检测用户ID
	 * @return boolean
	 */
	public static boolean isSelf(Integer checkUserId) {
		return UserContext.get().getId().intValue() == checkUserId.intValue();
	}
}
