package com.wcpdoc.exam.auth.realm;

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
import org.springframework.stereotype.Component;

import com.wcpdoc.exam.auth.Service.ShiroService;
import com.wcpdoc.exam.auth.cache.TokenCache;
import com.wcpdoc.exam.auth.entity.AuthUser;
import com.wcpdoc.exam.auth.entity.JWTToken;
import com.wcpdoc.exam.core.entity.JwtResult;
import com.wcpdoc.exam.core.util.JwtUtil;

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
		// 校验数据有效性
		String jwt = (String) principals.getPrimaryPrincipal();
		JwtResult jwtResult = JwtUtil.getInstance().parse(jwt);
		String loginName = jwtResult.getClaims().get("loginName", String.class);
		AuthUser user = shiroService.getUser(loginName);
		if (user == null) {
			throw new UnknownAccountException("账号密码错误");
		}

		// 授予角色权限
		String[] splitRoles = user.getRoles().split(",");
		SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
		for (String role : splitRoles) {
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
		if (jwtResult.getCode() != 200) {
			throw new AuthenticationException(jwtResult.getMsg());
		}
		
		return new SimpleAuthenticationInfo(jwtToken, jwtToken, getName());
	}
	
	/**
	 * 是否拥有角色（重写：管理员不验证）
	 * 
	 */
	@Override
	public boolean hasRole(PrincipalCollection principal, String roleIdentifier) {
		AuthorizationInfo info = this.getAuthorizationInfo(principal);
        return (info.getRoles() != null && info.getRoles().contains("admin")) || super.hasRole(principal, roleIdentifier);
	}
	
	/**
	 * 是否拥有权限（重写：管理员不验证）
	 */
	@Override
    public  boolean isPermitted(PrincipalCollection principal, String permission) {
        AuthorizationInfo info = this.getAuthorizationInfo(principal);
        return info.getRoles().contains("admin") || super.isPermitted(principal, permission);
    }
	
	/**
	 * 清除授权
	 * 
	 * v1.0 zhanghc 2021年6月11日下午3:42:34
	 * @param userId void
	 */
	public void clearAuth(Integer userId) {
		String principal = TokenCache.get(String.format("TOKEN_%s", userId));
		Subject subject = SecurityUtils.getSubject();
		SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, getName());
		subject.runAs(principals);
		clearCachedAuthorizationInfo(subject.getPrincipals());
		subject.releaseRunAs();
	}
}
