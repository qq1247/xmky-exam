package com.wcpdoc.exam.base.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.base.dao.OrgDao;
import com.wcpdoc.exam.base.entity.Org;
import com.wcpdoc.exam.base.service.OrgExService;
import com.wcpdoc.exam.base.service.OrgService;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 组织机构服务层实现
 * 
 * v1.0 zhanghc 2016-5-8上午11:00:00
 */
@Service
public class OrgServiceImpl extends BaseServiceImp<Org> implements OrgService {
	@Resource
	private OrgDao orgDao;
	@Resource
	private OrgExService orgExService;

	@Override
	@Resource(name = "orgDaoImpl")
	public void setDao(BaseDao<Org> dao) {
		super.dao = dao;
	}

	@Override
	public void addAndUpdate(Org org) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(org.getName())) {
			throw new MyException("参数错误：name");
		}
		if (org.getParentId() == null || org.getParentId() == 0) {
			throw new MyException("参数错误：parentId");
		}
		
		if (existName(org)) {
			throw new MyException("名称已存在！");
		}
		if (existCode(org)) {
			throw new MyException("编码已存在！");
		}
		
		// 添加组织机构
		org.setUpdateUserId(getCurUser().getId());
		org.setUpdateTime(new Date());
		org.setState(1);
		add(org);
		
		// 更新父子关系
		Org parentOrg = orgDao.getEntity(org.getParentId());
		org.setParentSub(parentOrg.getParentSub() + org.getId() + "_");
		org.setLevel(org.getParentSub().split("_").length - 1);
		update(org);
	}

	@Override
	public void delAndUpdate(Integer id) {
		// 校验数据有效性
		if (id == 1) { //不包括根组织机构
			return;
		}
		List<Org> orgList = orgDao.getList(id);
		if (ValidateUtil.isValid(orgList)) {
			throw new MyException("请先删除子组织机构！");
		}
		
		// 删除组织机构
		Org org = getEntity(id);
		orgExService.delAndUpdate(org);
		
		org.setState(0);
		org.setUpdateTime(new Date());
		org.setUpdateUserId(getCurUser().getId());
		update(org);
	}

	@Override
	public List<Map<String, Object>> getTreeList() {
		return orgDao.getTreeList();
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
			throw new MyException("源组织机构和目标组织机构一致！");
		}

		Org source = getEntity(sourceId);
		Org target = getEntity(targetId);
		
		if (target.getParentSub().contains(source.getParentSub())) {
			throw new MyException("父组织机构不能移动到子组织机构下！");
		}

		// 移动组织机构
		source.setParentId(target.getId());
		source.setParentSub(String.format("%s%s_", target.getParentSub(), source.getId()));
		source.setLevel(source.getParentSub().split("_").length - 1);
		update(source);
		
		List<Org> subSourceList = orgDao.getList(source.getId());
		doMove(source, subSourceList);
	}
	
	private void doMove(Org target, List<Org> subTargetList) {
		for (Org subTarget : subTargetList) {
			subTarget.setParentId(target.getId());
			subTarget.setParentSub(String.format("%s%s_", target.getParentSub(), subTarget.getId()));
			subTarget.setLevel(subTarget.getParentSub().split("_").length - 1);
			update(subTarget);
			
			List<Org> subSubTargetList = orgDao.getList(subTarget.getId());
			if (ValidateUtil.isValid(subSubTargetList)) {
				doMove(subTarget, subSubTargetList);
			}
		}
	}

	/**
	 * 名称是否已存在
	 * 
	 * v1.0 zhanghc 2016年7月9日下午11:48:30
	 * 
	 * @param org
	 * @return boolean
	 */
	@Override
	public boolean existName(Org org) {
		return orgDao.existName(org.getName(), org.getId());
	}
	
	/**
	 * 编码是否已存在
	 * 
	 * v1.0 zhanghc 2016年7月9日下午11:48:30
	 * 
	 * @param org
	 * @return boolean
	 */
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
	public Map<String, Object> getOrg(Integer id) {
		Map<String, Object> map = new HashMap<String, Object>();
		Org entity = orgDao.getEntity(id);
		Org parentEntity = orgDao.getEntity(entity.getParentId());
		map.put("name", entity.getName());
		map.put("parentId", entity.getParentId());
		map.put("parentName", parentEntity.getName());
		map.put("sort", entity.getNo());
		return map;
	}

	@Override
	public Integer syncOrg(String orgName, String orgCode) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(orgName)) {
			throw new MyException("参数错误：orgName");
		}
		if (!ValidateUtil.isValid(orgCode)) {
			throw new MyException("参数错误：orgCode");
		}
		
		Org org = orgDao.getOrg(orgName, orgCode);
		if(org != null){
			return org.getId();
		}
		
		org = new Org();
		org.setName(orgName);
		org.setCode(orgCode);
		org.setParentId(1);
		org.setUpdateUserId(getCurUser().getId());
		org.setUpdateTime(new Date());
		org.setState(1);
		org.setNo(1);
		orgDao.add(org);
		
		// 更新父子关系
		Org parentOrg = orgDao.getEntity(org.getParentId());
		org.setParentSub(parentOrg.getParentSub() + org.getId() + "_");
		org.setLevel(org.getParentSub().split("_").length - 1);
		update(org);
		
		return org.getId();
	}
}
