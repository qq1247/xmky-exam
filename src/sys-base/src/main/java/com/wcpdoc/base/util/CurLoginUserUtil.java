package com.wcpdoc.base.util;

import com.wcpdoc.base.constant.BaseConstant;
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
	 * 
	 * @return boolean
	 */
	public static boolean isAdmin() {
		return UserContext.get().getRole().equals(BaseConstant.ADMIN);
	}

	/**
	 * 是否考试用户
	 * 
	 * v1.0 zhanghc 2023年9月15日上午10:41:37
	 * 
	 * @return boolean
	 */
	public static boolean isExamUser() {
		return UserContext.get().getRole().equals(BaseConstant.EXAM_USER);
	}

	/**
	 * 是否子管理员
	 * 
	 * v1.0 zhanghc 2023年9月15日上午10:41:37
	 * 
	 * @return boolean
	 */
	public static boolean isSubAdmin() {
		return UserContext.get().getRole().equals(BaseConstant.SUB_ADMIN);
	}

	/**
	 * 是否阅卷用户
	 * 
	 * v1.0 zhanghc 2023年9月15日上午10:41:56
	 * 
	 * @return boolean
	 */
	public static boolean isMarkUser() {
		return UserContext.get().getRole().equals(BaseConstant.MARK_USER);
	}

	/**
	 * 是否自己
	 * 
	 * v1.0 zhanghc 2023年9月15日上午10:41:56
	 * 
	 * @param checkUserId 待检测用户ID
	 * @return boolean
	 */
	public static boolean isSelf(Integer checkUserId) {
		return UserContext.get().getId().intValue() == checkUserId.intValue();
	}
}
