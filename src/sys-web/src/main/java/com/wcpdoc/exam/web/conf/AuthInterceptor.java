package com.wcpdoc.exam.web.conf;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.wcpdoc.exam.base.cache.ResCache;
import com.wcpdoc.exam.base.entity.Res;
import com.wcpdoc.exam.core.constant.ConstantManager;
import com.wcpdoc.exam.core.entity.LoginUser;


@Component
public class AuthInterceptor implements HandlerInterceptor {
	private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//如果是公共资源，则不拦截。
 		log.debug("权限拦截：访问链接：{}", request.getRequestURI());
		String contextPath = request.getContextPath();
		String uri = request.getRequestURI().replaceFirst(contextPath + "/", "");
		String pubRes = ",login/toIn,login/doIn,login/toHome,login/doOut,login/toPwdUpdate,login/doPwdUpdate,login/curTime,file/doTempUpload,file/doDownload,progressBar/get,";
		if(pubRes.contains("," + uri + ",")){
			log.debug("权限拦截：公共资源，不拦截");
			return true;
		}
		
		//如果是系统管理员登陆，则不拦截。
		HttpSession session = request.getSession(false);//直接访问jsp会创建session，看work目录反编译后的对应jsp（第一次访问request.getSession()会创建session）
		if(session != null){
			LoginUser user = (LoginUser) session.getAttribute(ConstantManager.USER);
			if (user != null && ConstantManager.ADMIN_LOGIN_NAME.equals(user.getLoginName())) {
				log.debug("权限拦截：系统管理员登陆，不拦截");
				return true;
			}
		}
		
		//如果当前用户未登陆，则拦截。
		String redirectUrl = String.format("%s/login/toIn", contextPath);
		if(session == null || session.getAttribute(ConstantManager.USER) == null){
			response.sendRedirect(redirectUrl);
			log.info("权限拦截：当前用户未登陆，拦截");
			return false;
		}
		
		//如果当前用户访问的资源未知，则拦截。
		Map<String, Res> authMap = ResCache.getUrlResMap();
		Res res = authMap.get(uri);
		if(res == null){
			response.sendRedirect(redirectUrl);
			log.info("权限拦截：当前用户访问的资源【{}】未知，拦截", uri);
			return false;
		}
		
		//如果当前用户的权限不包含当前的资源，则拦截。
		@SuppressWarnings("unchecked")
		Map<Integer, Long> userAuthMap = (Map<Integer, Long>) session.getAttribute(ConstantManager.USER_AUTH_MAP);
		if(userAuthMap.get(res.getAuthPos()) == null 
				|| (userAuthMap.get(res.getAuthPos()) & res.getAuthCode()) == 0){
			response.sendRedirect(redirectUrl);
			log.info("权限拦截：当前用户的权限不包含当前的资源【{}】，拦截", uri);
			return false;
		}
		
		//放行。
		log.debug("权限拦截：通过");
		return true;
	}
}
