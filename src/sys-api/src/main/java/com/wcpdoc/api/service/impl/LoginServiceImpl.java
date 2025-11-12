package com.wcpdoc.api.service.impl;

import org.springframework.stereotype.Service;

import com.wcpdoc.api.service.LoginService;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;

import lombok.RequiredArgsConstructor;

/**
 * 登陆服务层实现
 * 
 * v1.0 zhanghc 2017-06-22 22:18:46
 */
@Service
@RequiredArgsConstructor
public class LoginServiceImpl extends BaseServiceImp<Object> implements LoginService {

	@Override
	public RBaseDao<Object> getDao() {
		return null;
	}
}
