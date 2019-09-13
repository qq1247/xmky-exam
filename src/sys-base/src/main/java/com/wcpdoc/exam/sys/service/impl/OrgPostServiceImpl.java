package com.wcpdoc.exam.sys.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.sys.dao.OrgPostDao;
import com.wcpdoc.exam.sys.entity.OrgPost;
import com.wcpdoc.exam.sys.service.OrgPostService;

/**
 * 机构岗位服务层实现
 * 
 * v1.0 zhanghc 2016-5-19下午9:32:43
 */
@Service
public class OrgPostServiceImpl extends BaseServiceImp<OrgPost> implements OrgPostService {
	@Resource
	private OrgPostDao orgPostDao;

	@Override
	@Resource(name = "orgPostDaoImpl")
	public void setDao(BaseDao<OrgPost> dao) {
		super.dao = dao;
	}
}
