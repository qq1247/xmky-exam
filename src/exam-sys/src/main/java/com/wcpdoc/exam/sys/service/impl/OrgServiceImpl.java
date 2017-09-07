package com.wcpdoc.exam.sys.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.sys.dao.OrgDao;
import com.wcpdoc.exam.sys.entity.Org;
import com.wcpdoc.exam.sys.service.OrgExService;
import com.wcpdoc.exam.sys.service.OrgService;
/**
 * 组织机构服务层实现
 * 
 * v1.0 zhanghc 2016-5-8上午11:00:00
 */
@Service
public class OrgServiceImpl extends BaseServiceImp<Org> implements OrgService{
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
	public void saveAndUpdate(Org org) {
		//校验数据有效性
		if(!ValidateUtil.isValid(org.getName())){
			throw new RuntimeException("无法获取参数：org.name");
		}
		if(existName(org)){
			throw new RuntimeException("名称已存在！");
		}
				
		//保存组织机构
		if(org.getParentId() == null){
			org.setParentId(0);
		}
		orgDao.save(org);
		
		//更新父子关系
		Org parentOrg = orgDao.getEntity(org.getParentId());
		if(parentOrg == null){
			org.setParentSub("_" + org.getId() + "_");
		}else {
			org.setParentSub(parentOrg.getParentSub() + org.getId() + "_");
		}
		orgDao.update(org);
	}
	
	@Override
	public void editAndUpdate(Org org) {
		//校验数据有效性
		if(!ValidateUtil.isValid(org.getName())){
			throw new RuntimeException("无法获取参数：org.name");
		}
		if(existName(org)){
			throw new RuntimeException("名称已存在！");
		}
		
		//修改组织机构
		orgDao.update(org);
	}

	@Override
	public void delAndUpdate(Integer[] ids) {
		//校验数据有效性
		if(!ValidateUtil.isValid(ids)){
			throw new RuntimeException("无法获取参数：ids");
		}
		
		//删除组织机构，不包括根组织机构
		for(Integer id : ids){
			if(id == 1){
				continue;
			}
			
			List<Org> orgList = orgDao.getAllSubOrgList(id);
			for(Org org : orgList){
				if(org.getState().equals("0")){
					continue;
				}
				
				org.setState(0);
				orgDao.update(org);
				
				orgExService.delAndUpdate(org);
			}
		}
	}

	@Override
	public List<Map<String, Object>> getTreeList() {
		return orgDao.getTreeList();
	}

	@Override
	public void doMove(Integer sourceId, Integer targetId) {
		//校验数据有效性
		if(sourceId == null){
			throw new RuntimeException("无法获取参数：sourceId");
		}
		if(targetId == null){
			throw new RuntimeException("无法获取参数：targetId");
		}
		if(sourceId == targetId){
			throw new RuntimeException("源组织机构和目标组织机构一致！");
		}
		
		Org source = getEntity(sourceId);
		Org target = getEntity(targetId);
		if(target.getParentSub().contains(source.getParentSub())){
			throw new RuntimeException("父组织机构不能移动到子组织机构下！");
		}
		
		//移动组织机构
		orgDao.doMove(sourceId, targetId);
	}

	/**
	 * 名称是否已存在
	 * v1.0 zhanghc 2016年7月9日下午11:48:30
	 * @param org
	 * @return boolean
	 */
	private boolean existName(Org org){
		//校验数据有效性
		if(org == null){
			throw new RuntimeException("无法获取参数：org");
		}
		if(!ValidateUtil.isValid(org.getName())){
			throw new RuntimeException("无法获取参数：org.name");
		}
		
		//如果是添加
		Org org2 = orgDao.getOrgByName(org.getName());
		if(org2 != null){
			orgDao.evict(org2);
		}
		
		if(org.getId() == null){
			if(org2 != null){
				return true;
			}
			return false;
		}
		
		//如果是修改
		if(org2 != null && !org.getId().equals(org2.getId())){
			return true;
		}
		return false;
	}
}
