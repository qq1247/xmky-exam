package com.wcpdoc.base.service.impl;

import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.wcpdoc.base.constant.BaseConstant;
import com.wcpdoc.base.dao.OrgDao;
import com.wcpdoc.base.dao.ParmDao;
import com.wcpdoc.base.dao.UserDao;
import com.wcpdoc.base.entity.Org;
import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.BaseCacheService;

import lombok.extern.slf4j.Slf4j;

/**
 * 基础缓存服务层实现
 * 
 * v1.0 zhanghc 2024年4月14日下午9:36:00
 */
@Service
@Slf4j
public class BaseCacheServiceImpl implements BaseCacheService {
	@Resource
	private UserDao userDao;
	@Resource
	private OrgDao orgDao;
	@Resource
	private ParmDao parmDao;

	@Override
	@Cacheable(value = BaseConstant.USER_CACHE, key = BaseConstant.USER_KEY_PRE + "#id", sync = true)
	public User getUser(Integer id) {
		User user = userDao.selectById(id);
		log.debug("缓存用户：{id: {}, loginName: {}, name: {}}", user.getId(), user.getLoginName(), user.getName());
		return user;
	}

	@Override
	@Cacheable(value = BaseConstant.ORG_CACHE, key = BaseConstant.ORG_KEY_PRE + "#id", sync = true)
	public Org getOrg(Integer id) {
		Org org = orgDao.selectById(id);
		log.debug("缓存机构：{id: {}, name: {}}", org.getId(), org.getName());
		return org;
	}

	@Override
	@Cacheable(value = BaseConstant.PARM_CACHE, key = BaseConstant.PARM_KEY, sync = true)
	public Parm getParm() {
		Parm parm = parmDao.selectById(1);
		log.debug("缓存参数：{id: {}}", parm.getId());
		return parm;
	}

}
