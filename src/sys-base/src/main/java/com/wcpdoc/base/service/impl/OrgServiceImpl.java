package com.wcpdoc.base.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.base.dao.OrgDao;
import com.wcpdoc.base.entity.Org;
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
//	@Resource
//	private OrgExService orgExService;

	@Override
	public RBaseDao<Org> getDao() {
		return orgDao;
	}

	@Override
	public void addEx(Org org) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(org.getName())) {
			throw new MyException("参数错误：name");
		}
		if (org.getParentId() == null || org.getParentId() == 0) {
			throw new MyException("参数错误：parentId");
		}
		if (existName(org)) {
			throw new MyException("名称已存在");
		}
		if (existCode(org)) {
			throw new MyException("编码已存在");
		}

		// 添加机构
		org.setUpdateUserId(getCurUser().getId());
		org.setUpdateTime(new Date());
		org.setState(1);
		save(org);

		// 更新父子关系
		Org parentOrg = getById(org.getParentId());
		org.setParentIds(parentOrg.getParentIds() + org.getId() + "_");
		org.setLevel(org.getParentIds().split("_").length - 1);
		updateById(org);
	}

	@Override
	public void delEx(Integer id) {
		// 校验数据有效性
		if (id == 1) { // 不包括根机构
			return;
		}
		List<Org> orgList = orgDao.getList(id);
		if (ValidateUtil.isValid(orgList)) {
			throw new MyException("存在子机构，不允许删除");
		}

		// 删除机构
		Org org = getById(id);
		// orgExService.delEx(org);

		org.setState(0);
		org.setUpdateTime(new Date());
		org.setUpdateUserId(getCurUser().getId());
		updateById(org);
	}

	@Override
	public void doMove(Integer sourceId, Integer targetId) {
		// 校验数据有效性
		if (sourceId == null) {
			throw new MyException("参数错误：sourceId");
		}
		if (targetId == null) {
			throw new MyException("参数错误：targetId");
		}
		if (sourceId == targetId) {
			throw new MyException("源机构和目标机构一致");
		}

		Org source = getById(sourceId);
		Org target = getById(targetId);

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

	@Override
	public boolean existName(Org org) {
		return orgDao.existName(org.getName(), org.getId());
	}

	@Override
	public boolean existCode(Org org) {
		return orgDao.existCode(org.getCode(), org.getId());
	}

	@Override
	public Org getOrg(String name) {
		return orgDao.getOrg(name);
	}

	@Override
	public List<Org> getList(Integer parentId) {
		return orgDao.getList(parentId);
	}

	@Override
	public List<Org> getList() {
		return orgDao.getList();
	}
}
