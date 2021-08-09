package com.wcpdoc.exam.sys.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.base.entity.Org;
import com.wcpdoc.exam.base.entity.User;
import com.wcpdoc.exam.base.service.OrgExService;
import com.wcpdoc.exam.base.service.OrgService;
import com.wcpdoc.exam.base.service.UserService;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 组织机构扩展服务层实现
 * 
 * v1.0 zhanghc 2016-5-8上午11:00:00
 */
@Service
public class OrgExServiceImpl extends BaseServiceImp<Object> implements OrgExService {
	@Resource
	private UserService userService;
	@Resource
	private OrgService orgService;

	@Override
	public void delAndUpdate(Org org) {
		List<User> userList = userService.getList(org.getId());
		if (ValidateUtil.isValid(userList)) {
			throw new MyException("该机构下有用户，不允许删除！");
		}
	}

	@Override
	public void addAndUpdate(Org org, String phone, String pwd) {
		
	}

	@Override
	public void setDao(BaseDao<Object> dao) {
		
	}
}
