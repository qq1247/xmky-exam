package com.wcpdoc.exam.api.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.api.entity.PersonToken;
import com.wcpdoc.exam.api.service.ApiLoginService;
import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.JwtResult;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.util.DateUtil;
import com.wcpdoc.exam.core.util.JwtUtil;

/**
 * 登录控制层
 * 
 * v1.0 chenyun 2020年11月25日上午9:11:02
 */
@Controller
@RequestMapping("/api/login")
public class ApiLoginController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiLoginController.class);

	@Resource
	private ApiLoginService apiLoginService;
	
	@Value("${sys.env}")
	private String sysEnv;//主题  dev
	@Value("${token.active.access}")
	private Integer tokenActiveAccess;//获取下一分钟时间
	@Value("${token.active.refresh}")
	private Integer tokenActiveRefresh;//获取下一分钟时间

	/**
	 * 登录
	 * 
	 * v1.0 chenyun 2020年11月25日上午9:11:02
	 * 
	 * @param phone
	 * @param code
	 * @param appId
	 * @return PageResult
	 */
	@RequestMapping("/in")
	@ResponseBody
	public PageResult in(String loginName, String pwd, String code) throws LoginException {
		try {//TODO 
			// 登录
			PersonToken personToken = apiLoginService.in(loginName, pwd, code);
			Map<String, Object> data = new HashMap<>();
			data.put("id", personToken.getId());
			data.put("saasId", personToken.getSaasId());
			data.put("accessToken", personToken.getAccessToken());
			data.put("refreshToken", personToken.getRefreshToken());
			return new PageResultEx(true, "登录成功", data);
		} catch (LoginException e) {
			log.error("登录失败：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("登录失败：", e);
			return new PageResult(false, "未知错误");
		}
	}

	/**
	 * 刷新令牌
	 * 
	 * v1.0 chenyun 2020年11月25日上午9:11:02
	 * 
	 * @param accessToken
	 * @param refreshToken
	 * @return PageResult
	 */
	@RequestMapping("/refreshToken")
	@ResponseBody
	public PageResult refreshToken(String accessToken, String refreshToken) {
		try {
			// 校验数据有效性
			JwtResult accessTokenResult = JwtUtil.parse(accessToken);
			Date curTime = new Date();
			JwtResult refreshTokenResult = JwtUtil.parse(refreshToken);
			if (!refreshTokenResult.isSucc()) {
				throw new LoginException(refreshTokenResult.getMsg());
			}
			if (refreshTokenResult.getClaims().get("refreshToken") == null) {
				throw new LoginException("非刷新令牌");
			}
			if (!sysEnv.equals(refreshTokenResult.getClaims().getSubject())) {
				throw new LoginException("主题无效");
			}

			// 重新生成令牌
			Integer id = accessTokenResult.getClaims().get("id", Integer.class);
			accessToken = JwtUtil.createToken(id + "", sysEnv, DateUtil.getNextMinute(curTime, tokenActiveAccess), accessTokenResult.getClaims());
			refreshToken = JwtUtil.createToken(id + "", sysEnv, DateUtil.getNextMinute(curTime, tokenActiveRefresh), refreshTokenResult.getClaims());

			Map<String, Object> data = new HashMap<>();
			data.put("accessToken", accessToken);
			data.put("refreshToken", refreshToken);
			return new PageResultEx(true, "刷新成功", data);
		} catch (LoginException e) {
			log.error("刷新令牌失败：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("刷新令牌失败：", e);
			return new PageResult(false, "未知异常");
		}
	}
}
