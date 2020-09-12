package com.wcpdoc.exam.tag;

import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.wcpdoc.exam.base.cache.ResCache;
import com.wcpdoc.exam.base.entity.Res;
import com.wcpdoc.exam.core.constant.ConstantManager;
import com.wcpdoc.exam.core.entity.LoginUser;

public class AuthTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	private String url;

	@Override
	public int doStartTag() throws JspException {
		// 如果是系统管理员，则显示。
		HttpSession session = pageContext.getSession();
		if (session != null) {
			LoginUser user = (LoginUser) session.getAttribute(ConstantManager.USER);
			if (user != null && ConstantManager.ADMIN_LOGIN_NAME.equals(user.getLoginName())) {
				return EVAL_BODY_INCLUDE;
			}
		}

		// 如果当前用户未登陆，则不显示。
		if (session == null || session.getAttribute(ConstantManager.USER) == null) {
			return SKIP_BODY;
		}

		// 如果当前访问的链接未知，则不显示。
		Map<String, Res> authMap = ResCache.getUrlResMap();
		Res res = authMap.get(url);
		if (res == null) {
			return SKIP_BODY;
		}

		// 如果当前用户的权限不包含当前的链接，则不显示。
		@SuppressWarnings("unchecked")
		Map<Integer, Long> userAuthMap = (Map<Integer, Long>) session.getAttribute(ConstantManager.USER_AUTH_MAP);
		if (userAuthMap.get(res.getAuthPos()) == null 
				|| (userAuthMap.get(res.getAuthPos()) & res.getAuthCode()) == 0) {
			return SKIP_BODY;
		}

		// 放行。
		return EVAL_BODY_INCLUDE;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
