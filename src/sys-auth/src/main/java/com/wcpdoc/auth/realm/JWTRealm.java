package com.wcpdoc.auth.realm;

import javax.annotation.Resource;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.wcpdoc.auth.cache.TokenCache;
import com.wcpdoc.auth.entity.AuthUser;
import com.wcpdoc.auth.entity.JWTToken;
import com.wcpdoc.auth.entity.JwtResult;
import com.wcpdoc.auth.service.ShiroService;
import com.wcpdoc.auth.util.JwtUtil;
import com.wcpdoc.core.util.ValidateUtil;

/**
 * jwt授权认证
 * 
 * v1.0 zhanghc 2021年3月2日上午9:51:13
 */
@Component
public class JWTRealm extends AuthorizingRealm {
	@Resource
	private ShiroService shiroService;

	@Override
	public boolean supports(AuthenticationToken token) {
		return token instanceof JWTToken;
	}

	/**
	 * 授予角色权限
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// 数据校验
		String jwt = (String) principals.getPrimaryPrincipal();
		JwtResult jwtResult = JwtUtil.getInstance().parse(jwt);
		String loginName = jwtResult.getClaims().get("loginName", String.class);
		AuthUser user = shiroService.getUser(loginName);
		if (user == null) {
			throw new UnknownAccountException("账号密码错误");
		}

		// 授予角色权限
		SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
		for (String role : user.getRoles()) {
			simpleAuthorizationInfo.addRole(role);
		}

		return simpleAuthorizationInfo;
	}

	/**
	 * 登陆认证
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		String jwtToken = (String) token.getPrincipal();
		JwtResult jwtResult = JwtUtil.getInstance().parse(jwtToken);
		if (jwtResult.getCode() != HttpStatus.OK.value()) {
			throw new AuthenticationException(jwtResult.getMsg());
		}

		return new SimpleAuthenticationInfo(jwtToken, jwtToken, getName());
	}

	/**
	 * 清除授权
	 * 
	 * v1.0 zhanghc 2021年6月11日下午3:42:34
	 * 
	 * @param userId void
	 */
	public void clearAuth(Integer userId) {
		String principal = TokenCache.get(userId);
		if (!ValidateUtil.isValid(principal)) {
			return;
		}
		Subject subject = SecurityUtils.getSubject();
		SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, getName());
		subject.runAs(principals);
		clearCachedAuthorizationInfo(subject.getPrincipals());
		subject.releaseRunAs();
	}
}
