package com.wcpdoc.auth.filter;

import java.util.Date;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;

import com.wcpdoc.auth.cache.OldTokenCache;
import com.wcpdoc.auth.cache.TokenCache;
import com.wcpdoc.auth.entity.JWTToken;
import com.wcpdoc.auth.entity.JwtResult;
import com.wcpdoc.auth.util.JwtUtil;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.SpringUtil;
import com.wcpdoc.core.util.ValidateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * jwt过滤器
 * 
 * v1.0 zhanghc 2021年3月2日上午9:52:04
 */
@Slf4j
public class JWTFilter extends BasicHttpAuthenticationFilter {

	/**
	 * 是否允许访问
	 */
	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		if (!isLoginAttempt(request, response)) {
			return false;
		}

		try {
			executeLogin(request, response);
			return true;
		} catch (Exception e) {
			throw new AuthenticationException(e.getMessage());
		}
	}

	/**
	 * 是否尝试登陆 检测http请求header头Authorization字段
	 */
	@Override
	protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
		String jwt = WebUtils.toHttp(request).getHeader("Authorization");
		return ValidateUtil.isValid(jwt);
	}

	/**
	 * 执行登陆
	 */
	@Override
	protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
		String jwt = WebUtils.toHttp(request).getHeader("Authorization");
		JWTToken jwtToken = new JWTToken(jwt);
		getSubject(request, response).login(jwtToken);
		refreshToken(request, response);
		return true;
	}

	/**
	 * 刷新令牌
	 * 
	 * v1.0 zhanghc 2021年3月18日下午4:01:28
	 * 
	 * @param request
	 * @param response void
	 */
	private void refreshToken(ServletRequest request, ServletResponse response) {
		// 数据校验
		Integer tokenExpireMinute = SpringUtil.getBean(Environment.class).getProperty("token.expireMinute",
				Integer.class);
		Integer tokenRefreshMinute = SpringUtil.getBean(Environment.class).getProperty("token.refreshMinute",
				Integer.class);
		String oldJwtToken = WebUtils.toHttp(request).getHeader("Authorization");
		JwtResult oldJwtResult = JwtUtil.getInstance().parse(oldJwtToken);// 能到这一步，肯定是登陆成功的，不需要校验空或校验错误。
		long oldTokenId = Long.parseLong(oldJwtResult.getClaims().getId());
		int oldUserId = oldJwtResult.getClaims().get("userId", Integer.class);
		String oldLoginName = oldJwtResult.getClaims().get("loginName", String.class);

		String tokenKey = String.format("TOKEN_%s", oldUserId);
		String curToken = TokenCache.get(oldUserId);
		if (curToken == null) {// 缓存中没有令牌（过期清理或人工清理）
			throw new AuthenticationException(String.format("已强制下线，请重新登录"));
		}

		try {
			if (!TokenCache.tryWriteLock(tokenKey, 6000)) { // 同一用户并发请求时，只让一个请求通过，该请求更新令牌后，其他请求就变成了旧令牌，旧令牌宽限30秒有效
				throw new AuthenticationException(
						String.format("用户【%s】并发【{}】请求超时", oldLoginName, WebUtils.toHttp(request).getRequestURI()));
			}

			curToken = TokenCache.get(oldUserId); // 当前令牌需要重新获取，因为并发请求等待的时候，令牌已经被更换
			Long curTokenId = Long.parseLong(JwtUtil.getInstance().parse(curToken).getClaims().getId());
			if (curTokenId != oldTokenId) {
				if (log.isDebugEnabled()) {
					log.debug("shiro权限：用户【{}】令牌过期，旧令牌创建时间【{}】，当前令牌创建时间【{}】", oldLoginName,
							DateUtil.formatDateTime(new Date(oldTokenId)),
							DateUtil.formatDateTime(new Date(curTokenId)));
				}
				if (ValidateUtil.isValid(OldTokenCache.get(String.format("OLD_TOKEN_%s", oldUserId)))) {// 最新令牌的上一个令牌，并且距离最新令牌30秒内，旧令牌有效
					if (log.isDebugEnabled()) {
						log.debug("shiro权限：用户【{}】旧令牌宽限期有效，旧令牌创建时间【{}】，当前令牌创建时间【{}】，", oldLoginName,
								DateUtil.formatDateTime(new Date(oldTokenId)),
								DateUtil.formatDateTime(new Date(curTokenId)));
					}
					return;
				}

				throw new AuthenticationException(String.format("已在别处登录，请重新登录"));
			}

			Date curTime = new Date();
			long oldTokenTimestamp = oldTokenId;
			if (curTime.getTime() - oldTokenTimestamp <= tokenRefreshMinute * 60 * 1000) {// 如果距离上次刷新不超过指定分钟，不处理
				return;
			}

			// 生成令牌信息（登陆由shiro接收令牌控制）
			if (log.isDebugEnabled()) {
				log.debug("shiro权限：用户【{}】刷新令牌，旧令牌创建时间【{}】，当前令牌创建时间【{}】，默认过期分钟【{}】，默认刷新分钟【{}】", oldLoginName,
						DateUtil.formatDateTime(new Date(oldTokenId)), DateUtil.formatDateTime(new Date(curTokenId)),
						tokenExpireMinute, tokenRefreshMinute);
			}
			Date newExpTime = DateUtil.getNextMinute(curTime, tokenExpireMinute);
			Long newTokenId = curTime.getTime();
			String newToken = JwtUtil.getInstance()
					.createToken(newTokenId.toString(), oldJwtResult.getClaims().getSubject(), newExpTime)
					.addAttr("userId", oldJwtResult.getClaims().get("userId"))
					.addAttr("loginName", oldJwtResult.getClaims().get("loginName"))
					.addAttr("type", oldJwtResult.getClaims().get("type")).build();

			// 缓存刷新令牌
			TokenCache.put(oldUserId, newToken); // 用于续租登陆
			OldTokenCache.put(String.format("OLD_TOKEN_%s", oldUserId), "true"); // 用于并发请求时，旧令牌宽限30秒有效期

			// 放入http响应头，供前端替换使用
			WebUtils.toHttp(response).setHeader("Access-Control-Expose-Headers", "Authorization"); // 不加前端只显示，无法获取到自定义header字段
			WebUtils.toHttp(response).setHeader("Authorization", newToken);
			return;
		} catch (InterruptedException e) {
			throw new AuthenticationException(String.format("用户【%s】挂起异常", oldLoginName));
		} finally {
			TokenCache.releaseWriteLock(tokenKey);
		}
	}

	/**
	 * 拒绝访问处理
	 */
	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		HttpServletResponse httpResponse = WebUtils.toHttp(response);
		httpResponse.setCharacterEncoding("UTF-8");
		httpResponse.setContentType("application/json;charset=UTF-8");
		httpResponse.setStatus(HttpStatus.OK.value());
		httpResponse.getWriter()
				.write(String.format("{\"code\": %s, \"msg\": \"未授权\"}", HttpStatus.UNAUTHORIZED.value()));
		return false;
	}

	/**
	 * 异常处理
	 */
	@Override
	protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		try {
			return super.preHandle(request, response);
		} catch (ShiroException e) {
			httpResponse.setCharacterEncoding("UTF-8");
			httpResponse.setContentType("application/json;charset=UTF-8");
			httpResponse.setStatus(HttpStatus.OK.value());
			httpResponse.getWriter().write(
					String.format("{\"code\": %s, \"msg\": \"%s\"}", HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
			log.error("shiro权限异常：{}", e.getMessage());
			return false;
		} catch (Exception e) {
			httpResponse.setCharacterEncoding("UTF-8");
			httpResponse.setContentType("application/json;charset=UTF-8");
			httpResponse.setStatus(HttpStatus.OK.value());
			httpResponse.getWriter().write(
					String.format("{\"code\": %s, \"msg\": \"%s\"}", HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
			log.error("shiro权限异常：", e);
			return false;
		}
	}

}
