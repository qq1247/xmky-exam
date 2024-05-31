package com.wcpdoc.base.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.wcpdoc.base.constant.BaseConstant;
import com.wcpdoc.base.dao.OrgDao;
import com.wcpdoc.base.entity.Org;
import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.base.service.OrgExService;
import com.wcpdoc.base.service.OrgService;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.ValidateUtil;

/**
 * 机构服务层实现
 * 
 * v1.0 zhanghc 2016-5-8上午11:00:00
 */
@Service
public class OrgServiceImpl extends BaseServiceImp<Org> implements OrgService {
	@Resource
	private OrgDao orgDao;
	@Resource
	private OrgExService orgExService;
	@Resource
	private BaseCacheService baseCacheService;

	@Override
	public RBaseDao<Org> getDao() {
		return orgDao;
	}

	@Override
	public void addEx(Org org) {
		// 数据校验
		addValid(org);

		// 机构添加
		org.setUpdateUserId(getCurUser().getId());
		org.setUpdateTime(new Date());
		org.setState(1);
		save(org);

		// 更新父子关系
		Org parentOrg = baseCacheService.getOrg(org.getParentId());
		org.setParentIds(parentOrg.getParentIds() + org.getId() + "_");
		org.setLevel(org.getParentIds().split("_").length - 1);
		updateById(org);
	}

	@Override
	@CacheEvict(value = BaseConstant.ORG_CACHE, key = BaseConstant.ORG_KEY_PRE + "#org.id")
	public void editEx(Org org) {
		// 数据校验
		editValid(org);

		// 机构修改
		Org entity = baseCacheService.getOrg(org.getId());
		entity.setName(org.getName());
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		entity.setNo(org.getNo());
		entity.setCode(org.getCode());
		updateById(entity);
	}

	@Override
	@CacheEvict(value = BaseConstant.ORG_CACHE, key = BaseConstant.ORG_KEY_PRE + "#id")
	public void delEx(Integer id) {
		// 数据校验
		if (id == 1) { // 顶级机构保留，页面删除也不要报错
			return;
		}
		delValid(id);

		// 机构删除
		orgExService.delEx(id);
		
		Org org = baseCacheService.getOrg(id);
		org.setState(0);
		org.setUpdateTime(new Date());
		org.setUpdateUserId(getCurUser().getId());
		updateById(org);
		
	}

	@Override
	@CacheEvict(value = BaseConstant.ORG_CACHE, allEntries = true)
	public void doMove(Integer sourceId, Integer targetId) {
		// 数据校验
		if (sourceId == null) {
			throw new MyException("参数错误：sourceId");
		}
		if (targetId == null) {
			throw new MyException("参数错误：targetId");
		}
		if (sourceId == targetId) {
			throw new MyException("源机构和目标机构一致");
		}

		Org source = baseCacheService.getOrg(sourceId);
		Org target = baseCacheService.getOrg(targetId);

		if (target.getParentIds().contains(source.getParentIds())) {
			throw new MyException("父机构不能移动到子机构下");
		}

		// 移动机构
		source.setParentId(target.getId());
		source.setParentIds(String.format("%s%s_", target.getParentIds(), source.getId()));
		source.setLevel(source.getParentIds().split("_").length - 1);
		updateById(source);

		List<Org> subSourceList = orgDao.getList(source.getId());
		doMove(source, subSourceList);
	}
	

	@Override
	public List<Org> getList() {
		return orgDao.getList();
	}

	private void doMove(Org target, List<Org> subTargetList) {
		for (Org subTarget : subTargetList) {
			subTarget.setParentId(target.getId());
			subTarget.setParentIds(String.format("%s%s_", target.getParentIds(), subTarget.getId()));
			subTarget.setLevel(subTarget.getParentIds().split("_").length - 1);
			orgDao.updateById(subTarget);

			List<Org> subSubTargetList = orgDao.getList(subTarget.getId());
			if (ValidateUtil.isValid(subSubTargetList)) {
				doMove(subTarget, subSubTargetList);
			}
		}
	}

	private void addValid(Org org) {
		if (!ValidateUtil.isValid(org.getName())) {
			throw new MyException("参数错误：name");
		}
		if (org.getParentId() == null || org.getParentId() == 0) {
			throw new MyException("参数错误：parentId");
		}
		if (orgDao.existName(org.getName(), org.getId())) {
			throw new MyException("名称已存在");
		}
		if (orgDao.existCode(org.getCode(), org.getId())) {
			throw new MyException("编码已存在");
		}
	}

	private void editValid(Org org) {
		if (!ValidateUtil.isValid(org.getName())) {
			throw new MyException("参数错误：name");
		}
		if (orgDao.existName(org.getName(), org.getId())) {
			throw new MyException("名称已存在");
		}
		if (orgDao.existCode(org.getCode(), org.getId())) {
			throw new MyException("编码已存在");
		}
	}

	private void delValid(Integer id) {
		List<Org> orgList = orgDao.getList(id);
		if (ValidateUtil.isValid(orgList)) {
			throw new MyException("存在子机构");
		}
	}
}
