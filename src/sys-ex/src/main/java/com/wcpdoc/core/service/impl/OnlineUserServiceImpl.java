package com.wcpdoc.core.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.auth.cache.OldTokenCache;
import com.wcpdoc.auth.cache.TokenCache;
import com.wcpdoc.base.service.UserExService;
import com.wcpdoc.core.cache.OnlineUserCache;
import com.wcpdoc.core.entity.LoginUser;
import com.wcpdoc.core.entity.OnlineUser;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.OnlineUserService;
import com.wcpdoc.core.util.DateUtil;

/**
 * 在线用户服务层实现
 * 
 * v1.0 zhanghc 2021年10月15日下午1:44:08
 */
@Service
public class OnlineUserServiceImpl implements OnlineUserService {// 不继承BaseServiceImp，这里使用缓存实现
	@Resource
	private UserExService userExService;

	@Override
	public void add(OnlineUser onlineUser) {
		OnlineUserCache.put(onlineUser);
	}

	@Override
	public void update(OnlineUser onlineUser) {
		OnlineUserCache.put(onlineUser);
	}

	@Override
	public void del(Object id) {
		OnlineUserCache.remove((Integer)id);
	}

	@Override
	public void del(Object[] ids) {
		for (Object id : ids) {
			del(id);
		}
	}

	@Override
	public OnlineUser getEntity(Object id) {
		return OnlineUserCache.get((Integer)id);
	}

	@Override
	public PageOut getListpage(PageIn pageIn) {
		throw new MyException("不支持的功能");
	}

	@Override
	public LoginUser getCurUser() {
		throw new MyException("不支持的功能");
	}

	@Override
	public void out(Integer userId) {
		// 清除令牌
		TokenCache.del(userId);
		OldTokenCache.del(userId);
		
		// 设置为离线
		OnlineUser onlineUser = getEntity(userId);
		if (onlineUser != null && onlineUser.getState()) {// 如果在近半分钟内在线，设置为半分钟前，防止页面刷新后还是在线状态
			onlineUser.setUpdateTime(DateUtil.getNextSecond(onlineUser.getUpdateTime(), -65));
			update(onlineUser);
		}
	}
}
