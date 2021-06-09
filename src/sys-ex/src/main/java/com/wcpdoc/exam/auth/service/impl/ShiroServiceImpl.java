package com.wcpdoc.exam.auth.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.auth.Service.ShiroService;
import com.wcpdoc.exam.auth.entity.AuthUser;
import com.wcpdoc.exam.base.entity.User;
import com.wcpdoc.exam.base.service.UserService;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 权限服务层实现
 * 
 * v1.0 zhanghc 2016-6-11下午8:57:40
 */
@Service
public class ShiroServiceImpl implements ShiroService {
	@Resource
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
			public String getRoles() {
				return user.getRoles();
			}
		};
	}

	@Override
	public List<String> getRoleList(String roles) {
		List<String> roleList = new ArrayList<String>();
		if(ValidateUtil.isValid(roles)){
			String[] split = roles.split(",");
			for (String role : split) {
				roleList.add(role);
			}
		}
		return roleList;
	}
}
