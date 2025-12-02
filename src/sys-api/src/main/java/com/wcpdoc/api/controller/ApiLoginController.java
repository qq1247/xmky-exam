package com.wcpdoc.api.controller;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.security.auth.login.LoginException;

import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.auth.entity.JwtToken;
import com.wcpdoc.auth.entity.OnlineUser;
import com.wcpdoc.auth.service.JwtTokenService;
import com.wcpdoc.auth.service.LoginAttemptService;
import com.wcpdoc.auth.service.NonceService;
import com.wcpdoc.auth.service.OnlineUserService;
import com.wcpdoc.base.constant.BaseConstant;
import com.wcpdoc.base.entity.Org;
import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.base.entity.RegistUser;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.base.service.OrgService;
import com.wcpdoc.base.service.RegistUserService;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.base.util.InviteCodeUtil;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.RsaUtil;
import com.wcpdoc.core.util.ValidateUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 登录控制层
 * 
 * v1.0 zhanghc 2016年7月10日下午11:44:41
 */
@RestController
@RequestMapping("/api/login")
@Slf4j
@RequiredArgsConstructor
public class ApiLoginController extends BaseController {
	private final HttpServletRequest request;
	private final HttpServletResponse response;
	private final BaseCacheService baseCacheService;
	private final LoginAttemptService loginAttemptService;
	private final JwtTokenService jwtTokenService;
	private final OnlineUserService onlineUserService;
	private final UserService userService;
	private final NonceService nonceService;
	private final RegistUserService registUserService;
	private final OrgService orgService;

	/**
	 * 登录进入
	 * 
	 * v1.0 zhanghc 2016年7月18日下午3:23:00
	 * 
	 * @param loginName
	 * @param pwd
	 * @return PageResult
	 */
	@RequestMapping("/in")
	public PageResult in(String loginName, String pwd) {
		try {
			// 数据校验
			if (!ValidateUtil.isValid(loginName)) {
				throw new MyException("参数错误：loginName");
			}
			if (!ValidateUtil.isValid(pwd)) {
				throw new MyException("参数错误：pwd");
			}
			if (loginAttemptService.isLock(loginName)) {
				throw new MyException("账号已被锁定，请1分钟后重试");
			}
			String decryptText = RsaUtil.decrypt(pwd);
			String[] plainTexts = decryptText.split(":"); // loginName:nonce:pwd
			if (plainTexts.length != 3) {
				throw new MyException("数据格式错误");
			}
			if (!plainTexts[0].equals(loginName)) {
				throw new MyException("数据格式错误");
			}

			nonceService.consumeNonce(String.format("%s:%s", plainTexts[0], plainTexts[1]));

			User user = userService.getUser(loginName);
			if (user == null || !user.getPwd().equals(userService.getEncryptPwd(loginName, plainTexts[2]))) {
				loginAttemptService.fail(loginName);
				throw new MyException("登录账号或密码错误");
			}
			if (user.getState() == 0) {
				throw new MyException("登录账号已被删除");
			}
			if (user.getState() == 2) {
				throw new MyException("登录账号已被冻结");
			}

			// 登录
			loginAttemptService.succ(loginName);
			String accessToken = jwtTokenService.createAccessToken(user.getLoginName(), List.of(user.getRole()),
					user.getId(), user.getName());
			String refreshToken = jwtTokenService.createRefreshToken(user.getLoginName());
			OnlineUser onlineUser = OnlineUser.builder()//
					.id(user.getId())//
					.loginName(user.getLoginName())//
					.role(user.getRole())//
					.ip(request.getRemoteAddr())//
					.updateTime(new Date())//
					.build();
			onlineUserService.login(onlineUser);

			// 更新用户登录时间
			user.setLastLoginTime(new Date());
			userService.updateById(user);

			return PageResultEx.ok()//
					.addAttr("userId", user.getId())//
					.addAttr("userName", user.getName())//
					.addAttr("role", user.getRole())//
					.addAttr("accessToken", accessToken)//
					.addAttr("refreshToken", refreshToken)//
			;
		} catch (MyException e) {
			log.error("登录错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("登录错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 登录进入（用于临时考试）
	 * 
	 * v1.0 zhanghc 2024年10月9日下午3:14:18
	 * 
	 * @param name 自定义内容
	 * @return PageResult
	 */
	@RequestMapping("/temp-in")
	public PageResult tempIn(String name) {
		try {
			// 数据校验
			if (!ValidateUtil.isValid(name)) {
				throw new LoginException("参数错误：name");
			}
			if (name.length() < 2) {
				throw new LoginException("参数错误：name必须大于2位");
			}
			if (name.length() > 16) {
				throw new LoginException("参数错误：name必须小于16位");
			}

//			if (!Pattern.compile("^1[3-9]\\d{9}$").matcher(phone).matches()) {
//				throw new LoginException("手机号格式错误");
//			}

			// 生成匿名用户
			User user = new User();
			user.setName(name);
			user.setLoginName(UUID.randomUUID().toString().replaceAll("-", ""));// 无登录账号
			user.setRole(BaseConstant.TEMP_USER);
			user.setRegistTime(new Date());
			user.setOrgId(0);
			user.setState(1);
			user.setSource("临时登录");
			user.setUpdateUserId(1);
			user.setUpdateTime(new Date());
			userService.save(user);

			// 登录
			String accessToken = jwtTokenService.createAccessToken(user.getLoginName(), List.of(user.getRole()),
					user.getId(), user.getName());
			String refreshToken = jwtTokenService.createRefreshToken(user.getLoginName());
			OnlineUser onlineUser = OnlineUser.builder()//
					.id(user.getId())//
					.loginName(user.getLoginName())//
					.role(user.getRole())//
					.ip(request.getRemoteAddr())//
					.updateTime(new Date())//
					.build();
			onlineUserService.login(onlineUser);

			return PageResultEx.ok()//
					.addAttr("userId", user.getId())//
					.addAttr("userName", user.getName())//
					.addAttr("role", user.getRole())//
					.addAttr("accessToken", accessToken)//
					.addAttr("refreshToken", refreshToken)//
			;
		} catch (LoginException e) {
			log.error("匿名登录错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("匿名登录错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 登出
	 * 
	 * v1.0 zhanghc 2016年9月8日下午8:50:37
	 * 
	 * @return PageResult
	 */
	@RequestMapping("/out")
	public PageResult out() {
		try {
			String token = request.getHeader("Authorization");
			if (jwtTokenService.isValid(token)) {
				JwtToken jwtToken = jwtTokenService.parse(token);
				jwtTokenService.blacklist(token);
				onlineUserService.logout(jwtToken.getLoginName());
			}
			return PageResult.ok();
		} catch (Exception e) {
			log.error("登出错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 刷新令牌
	 * 
	 * v1.0 zhanghc 2025年11月4日下午5:21:05
	 * 
	 * @param loginName
	 * @param refreshToken
	 * @return PageResult
	 */
	@RequestMapping("/refresh")
	public PageResult refresh(String refreshToken) {
		try {
			if (!jwtTokenService.isValid(refreshToken)) {
				throw new LoginException("登录已过期，请重新登录.");
			}

			JwtToken jwtToken = jwtTokenService.parse(refreshToken);
			User user = userService.getUser(jwtToken.getLoginName());
			if (user == null) {
				throw new LoginException("登录账号不存在");
			}
			if (user.getState() == 0) {
				throw new LoginException("账号已被删除");
			}
			if (user.getState() == 2) {
				throw new LoginException("账号已被冻结");
			}

			String accessToken = jwtTokenService.createAccessToken(user.getLoginName(), List.of(user.getRole()),
					user.getId(), user.getName());
			return PageResultEx.ok()//
					.addAttr("accessToken", accessToken)//
			;
		} catch (LoginException e) {
			log.error("刷新令牌错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("刷新令牌错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 密码修改
	 * 
	 * v1.0 zhanghc 2017年7月14日下午3:05:21
	 * 
	 * @param oldPwd
	 * @param newPwd
	 * @return PageResult
	 */
	@RequestMapping("/pwd")
	public PageResult pwd(String oldPwd, String newPwd) {
		try {
			// 数据校验
			if (!ValidateUtil.isValid(oldPwd)) {
				throw new MyException("参数错误：oldPwd");
			}
			if (!ValidateUtil.isValid(newPwd)) {
				throw new MyException("参数错误：newPwd");
			}
			User user = baseCacheService.getUser(getCurUser().getId());
			String oldEncryptPwd = userService.getEncryptPwd(user.getLoginName(), oldPwd);
			if (!user.getPwd().equals(oldEncryptPwd)) {
				throw new MyException("原始密码错误");
			}

			// 修改密码
			user.setPwd(userService.getEncryptPwd(user.getLoginName(), newPwd));
			userService.updateById(user);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("密码修改错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("密码修改错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 服务器时间
	 * 
	 * v1.0 zhanghc 2017年6月25日下午4:19:05 <br/>
	 * 
	 * @return PageResult
	 */
	@RequestMapping("/sys-time")
	public PageResult sysTime() {
		try {
			return PageResultEx.ok().data(DateUtil.formatDateTime(new Date()));
		} catch (Exception e) {
			log.error("获取服务器时间错误：", e);
			return PageResult.err().msg(e.getMessage());
		}
	}

	/**
	 * 登录参数
	 * 
	 * v1.0 zhanghc 2025年9月1日下午3:17:28
	 * 
	 * @return PageResult
	 */
	@RequestMapping("/parm")
	public PageResult parm() {
		try {
			Parm parm = baseCacheService.getParm();
			return PageResultEx.ok()//
					.addAttr("sysName", parm.getSysName())//
					.addAttr("customTitle", parm.getCustomTitle())//
					.addAttr("customContent", parm.getCustomContent())//
					.addAttr("icp", parm.getIcp())//
			;
		} catch (Exception e) {
			log.error("登录参数错误：", e);
			return PageResult.err().msg(e.getMessage());
		}
	}

	/**
	 * 登录logo
	 * 
	 * v1.0 chenyun 2021-10-08 16:05:35
	 * 
	 * @return void
	 */
	@RequestMapping("/logo")
	public void logo() {
		try {
			File logo = new File("./config/logo.png");
			response.addHeader("Content-Disposition",
					"attachment;filename=" + new String("logo.png".getBytes("UTF-8"), "ISO-8859-1"));
			response.setContentType("application/force-download");
			FileUtils.copyFile(logo, response.getOutputStream());
		} catch (MyException e) {
			log.error("企业logo失败：{}", e.getMessage());
		} catch (Exception e) {
			log.error("企业logo失败：", e);
		}
	}

	/**
	 * 登录密钥
	 * 
	 * v1.0 zhanghc 2025年9月18日下午10:30:47
	 * 
	 * @return String
	 */
	@RequestMapping("/encrypt")
	public PageResult encrypt(String loginName) {
		try {
			if (!ValidateUtil.isValid(loginName)) {
				throw new MyException("参数错误：loginName");
			}
			return PageResultEx.ok()//
					.addAttr("nonce", nonceService.generateNonce(loginName))//
					.addAttr("publicKey", RsaUtil.getPublicKeyBase64())//
			;
		} catch (MyException e) {
			log.error("登录密钥错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("登录密钥错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 注册用户
	 * 
	 * v1.0 zhanghc 2025年12月1日下午7:06:04
	 * 
	 * @param registUser
	 * @return PageResult
	 */
	@RequestMapping("/regist-user")
	public PageResult registUser(String name, String loginName, String pwd, String orgCode) {
		try {
			// 数据校验
			if (!ValidateUtil.isValid(name)) {
				throw new MyException("参数错误：name");
			}
			if (!ValidateUtil.isValid(loginName)) {
				throw new MyException("参数错误：loginName");
			}
			if (!ValidateUtil.isValid(pwd)) {
				throw new MyException("参数错误：pwd");
			}
			if (!ValidateUtil.isValid(orgCode)) {
				throw new MyException("参数错误：orgCode");
			}
			if (registUserService.existLoginName(loginName)) {
				throw new MyException("登录账号已注册");
			}
			if (userService.existLoginName(loginName, null)) {
				throw new MyException("登录账号已存在");
			}
			int orgId;
			try {
				orgId = InviteCodeUtil.extractIdFromCode(orgCode);
			} catch (Exception e) {
				throw new MyException("无效的机构码");
			}
			Org org = orgService.getById(orgId); // 不要用baseCacheService，比如id为0，org为null，但是log.debug获取了属性报错
			if (org == null) {
				throw new MyException("机构不存在");
			}

			// 注册用户
			registUserService.save(RegistUser.builder()//
					.name(name)//
					.loginName(loginName)//
					.pwd(pwd)//
					.orgId(orgId)//
					.registTime(new Date())//
					.state(3)//
					.build());
			return PageResult.ok();
		} catch (MyException e) {
			log.error("注册用户错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("注册用户错误：", e);
			return PageResult.err();
		}
	}
}
