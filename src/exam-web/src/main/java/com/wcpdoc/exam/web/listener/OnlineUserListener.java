package com.wcpdoc.exam.web.listener;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

import com.wcpdoc.exam.core.constant.ConstantManager;
import com.wcpdoc.exam.sys.entity.User;

/**
 * 在线用户监听 <br/>
 * v1.0 zhanghc 2016年7月20日下午4:50:17
 */
public class OnlineUserListener implements HttpSessionAttributeListener {
//	private static final Logger log = LoggerFactory.getLogger(OnlineUserListener.class);
	

	/**
	 * 属性添加一定发生在第一次登录，或第n个浏览器登录，或异地登录。
	 * 
	 * 浏览器	账号		方式		sessionid	HttpSessionAttributeListener	单一登录处理
	 * 火狐		zhc		登录			1		session1.attributeAdded
	 * 火狐		zhc		登录			1		session1.attributeReplaced
	 * 火狐		wd		登录			1		session1.attributeReplaced
	 * IE		zhc		登录			2		session2.attributeAdded
	 * IE		zhc		登录			2		session2.attributeReplaced
	 * IE		wd		登录			2		session2.attributeReplaced		session1.invalidate
	 * 火狐		wd		登录			1		session1.attributeReplaced		新session
	 * 谷歌		wd		登录			3		session3.attributeAdded			session2.invalidate
	 */
	@Override
	public void attributeAdded(HttpSessionBindingEvent se) {
		if(!ConstantManager.USER.equals(se.getName())){
			return;
		}
		
		HttpSession newSession = se.getSession();
		User newUser = (User) newSession.getAttribute(ConstantManager.USER);
		@SuppressWarnings("unchecked")
		Map<String, HttpSession> sessionMap = (Map<String, HttpSession>) newSession
				.getServletContext().getAttribute(ConstantManager.SESSION_USER_LIST);
		HttpSession oldSession = sessionMap.get(newUser.getLoginName());
		if(oldSession != null){
			try {
				oldSession.invalidate();//可能已失效。
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		sessionMap.put(newUser.getLoginName(), newSession);
	}

	@Override
	public void attributeRemoved(HttpSessionBindingEvent se) {
		if(!ConstantManager.USER.equals(se.getName())){
			return;
		}
		
		@SuppressWarnings("unchecked")
		Map<String, HttpSession> sessionMap = (Map<String, HttpSession>) se
				.getSession().getServletContext().getAttribute(ConstantManager.SESSION_USER_LIST);
		User oldUser = (User) se.getValue();//se.getSession().getAttribute(ChangliangManager.LOGIN_USER);已移除，取不到。
		sessionMap.remove(oldUser.getLoginName());
	}

	/**
	 * 属性替换一定发生在同一个浏览器第n次访问，n>1。
	 */
	@Override
	public void attributeReplaced(HttpSessionBindingEvent se) {
		if (!ConstantManager.USER.equals(se.getName())) {
			return;
		}
		
		@SuppressWarnings("unchecked")
		Map<String, HttpSession> sessionMap = (Map<String, HttpSession>) se
				.getSession().getServletContext().getAttribute(ConstantManager.SESSION_USER_LIST);
		HttpSession newSession = se.getSession();
		User newUser = (User) newSession.getAttribute(ConstantManager.USER);
		HttpSession oldSession = sessionMap.get(newUser.getLoginName());
		if(oldSession != null && newSession != oldSession){
			try {
				oldSession.invalidate();//可能已失效。
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Iterator<Entry<String, HttpSession>> iterator = sessionMap.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, HttpSession> entry = iterator.next();
			HttpSession session = entry.getValue();
			if(session == newSession){
				iterator.remove();
			}
		}
		
		sessionMap.put(newUser.getLoginName(), newSession);
	}
}