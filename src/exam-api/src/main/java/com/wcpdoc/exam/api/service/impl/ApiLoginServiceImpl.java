package com.wcpdoc.exam.api.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wcpdoc.exam.api.entity.PersonToken;
import com.wcpdoc.exam.api.service.ApiLoginService;
import com.wcpdoc.exam.base.entity.User;
import com.wcpdoc.exam.base.service.UserService;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.util.DateUtil;
import com.wcpdoc.exam.core.util.JwtUtil;


/**
 * 登录业务层实现
 * 
 * v1.0 chenyun 2020年11月25日上午9:11:02
 */
@Service
public class ApiLoginServiceImpl extends BaseServiceImp<Object> implements ApiLoginService {
	private static final Logger log = LoggerFactory.getLogger(ApiLoginServiceImpl.class);

	@Value("${sys.env}")
	private String sysEnv;
	@Value("${token.active.access}")
	private Integer tokenActiveAccess;
	@Value("${token.active.refresh}")
	private Integer tokenActiveRefresh;
	@Resource
	private UserService userService;
	
	@Override
	public void setDao(BaseDao<Object> dao) {}
	
	@Override
	public PersonToken in(String loginName, String pwd, String code) throws LoginException {
		// 生成授权令牌
		try {
		//Integer saasId = OrgCache.getSaasId(code);

		User user = userService.getUser(loginName);
		if(user == null || !user.getPwd().equals(userService.getEncryptPwd(loginName, pwd))) {
			throw new LoginException("用户名或密码错误！");
		}
		log.info("登录生成授权令牌：{}", user.getId());
		PersonToken personToken = generateToken(user.getId());
		return personToken;
		} catch (Exception e) {
			log.error("登录失败：{}", e.getMessage());
			throw new LoginException(e.getMessage());
		}
	}

	private PersonToken generateToken(Integer id) {
		Map<String, Object> params = new HashMap<>();
		params.put("id", id);
		String accessToken = JwtUtil.createToken(id+"", sysEnv, DateUtil.getNextMinute(new Date(), tokenActiveAccess), params);
		params.put("refreshToken", true);
		String refreshToken = JwtUtil.createToken(id+"", sysEnv, DateUtil.getNextMinute(new Date(), tokenActiveRefresh), params);

		PersonToken personToken = new PersonToken();
		personToken.setId(id);
		personToken.setAccessToken(accessToken);
		personToken.setRefreshToken(refreshToken);
		return personToken;
	}
}
