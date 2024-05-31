package com.wcpdoc.base.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.OrgExService;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.SpringUtil;
import com.wcpdoc.core.util.ValidateUtil;

/**
 * 机构扩展服务层实现
 * 
 * v1.0 zhanghc 2016-5-8上午11:00:00
 */
@Service
public class OrgExServiceImpl extends BaseServiceImp<Object> implements OrgExService {

	@Override
	public RBaseDao<Object> getDao() {
		return null;
	}

	@Override
	public void delEx(Integer orgId) {
		List<User> userList = SpringUtil.getBean(UserService.class).getList(orgId);
		if (ValidateUtil.isValid(userList)) {
			throw new MyException("存在考试用户");
		}
	}
}
