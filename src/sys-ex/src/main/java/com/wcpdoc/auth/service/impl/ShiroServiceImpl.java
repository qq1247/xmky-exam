package com.wcpdoc.auth.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.wcpdoc.auth.entity.AuthUser;
import com.wcpdoc.auth.service.ShiroService;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.core.util.ValidateUtil;

/**
 * 权限服务层实现
 * 
 * shiro引用其他模块的业务层接口时，加@Lazy注解延迟初始化，保证被BeanPostProcessor拦截，创建具有事务功能的代理对象
 * 
 * v1.0 zhanghc 2016-6-11下午8:57:40
 */
@Service
public class ShiroServiceImpl implements ShiroService {
	@Resource
	@Lazy
	private UserService userService;

	@Override
	public AuthUser getUser(String loginName) {
		User user = userService.getUser(loginName);
		return new AuthUser() {

			@Override
			public String getName() {
				return user.getName();
			}

			@Override
			public String getLoginName() {
				return user.getLoginName();
			}

			@Override
			public Integer getId() {
				return user.getId();
			}

			@Override
			public String[] getRoles() {
				return new String[] { user.getType().toString() };
			}
		};
	}

	@Override
	public List<String> getRoleList(String roles) {
		List<String> roleList = new ArrayList<String>();
		if (ValidateUtil.isValid(roles)) {
			String[] split = roles.split(",");
			for (String role : split) {
				roleList.add(role);
			}
		}
		return roleList;
	}
}
