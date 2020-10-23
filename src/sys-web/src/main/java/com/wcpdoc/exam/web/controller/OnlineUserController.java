package com.wcpdoc.exam.web.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.base.entity.User;
import com.wcpdoc.exam.core.constant.ConstantManager;
import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 在线用户控制层
 * v1.0 zhanghc 2016年8月27日上午11:36:55
 */
@Controller
@RequestMapping("/onlineUser")
public class OnlineUserController extends BaseController{
	private static final Logger log = LoggerFactory.getLogger(OnlineUserController.class);
	
	/**
	 * 到达在线用户列表页面 
	 * v1.0 zhanghc 2016年8月27日上午11:36:55
	 * 
	 * @return String
	 */
	@RequestMapping("/toList")
	public String toList() {
		try {
			return "web/onlineUser/onlineUserList";
		} catch (Exception e) {
			log.error("到达在线用户列表页面错误：", e);
			return "web/onlineUserList";
		}
	}
	
	/**
	 * 在线用户列表 
	 * v1.0 zhanghc 2016年8月27日上午11:36:55
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/list")
	@ResponseBody
	public PageResult list(PageIn pageIn) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, HttpSession> sessionMap = (Map<String, HttpSession>) request
					.getServletContext().getAttribute(ConstantManager.SESSION_USER_LIST);
			Collection<HttpSession> sessionList = sessionMap.values();
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			for(HttpSession session : sessionList) {
				User user = (User) session.getAttribute(ConstantManager.USER);
				
				if(ValidateUtil.isValid(pageIn.getTwo())) {
					if(!user.getLoginName().contains(pageIn.getTwo())) {
						continue;
					}
				}
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("ID", user.getLoginName());
				map.put("SESSIONID", session.getId());
				map.put("LOGINNAME", user.getLoginName());
				list.add(map);
			}
			
			return new PageResultEx(true, "查询成功", new PageOut(list, list.size()));
		} catch (Exception e) {
			log.error("在线用户列表错误：", e);
			return new PageResult(false, "查询失败");
		}
	}
	
	/**
	 * 完成强制退出在线用户
	 * v1.0 zhanghc 2016年8月27日上午11:36:55
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/doDel")
	@ResponseBody
	public PageResult doDel(String id) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, HttpSession> sessionMap = (Map<String, HttpSession>) request
					.getServletContext().getAttribute(ConstantManager.SESSION_USER_LIST);
			HttpSession session = sessionMap.get(id);
			session.invalidate();//可能已失效
			
			return new PageResult(true, "强制退出成功");
		} catch (Exception e) {
			log.error("完成强制退出在线用户错误：", e);
			return new PageResult(false, "未知异常！");
		}
	}
}
