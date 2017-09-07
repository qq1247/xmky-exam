package com.wcpdoc.exam.sys.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.sys.cache.ResCache;
import com.wcpdoc.exam.sys.dao.ResDao;
import com.wcpdoc.exam.sys.entity.Res;
import com.wcpdoc.exam.sys.service.ResExService;
import com.wcpdoc.exam.sys.service.ResService;
/**
 * 资源服务层实现
 * 
 * v1.0 zhanghc 2016-6-11下午8:57:40
 */
@Service
public class ResServiceImpl extends BaseServiceImp<Res> implements ResService{
	@Resource
	private ResDao resDao;
	@Resource
	private ResExService resExService;

	@Override
	@Resource(name = "resDaoImpl")
	public void setDao(BaseDao<Res> dao) {
		super.dao = dao;
	}

	@Override
	public void saveAndUpdate(Res res) {
		//校验数据有效性
		if(!ValidateUtil.isValid(res.getName())){
			throw new RuntimeException("无法获取参数：res.name");
		}
		if(!ValidateUtil.isValid(res.getUrl())){
			throw new RuntimeException("无法获取参数：res.url");
		}
		
//		if(existName(res)){
//			throw new RuntimeException("名称已存在！");
//		}
		if(existUrl(res)){
			throw new RuntimeException("链接已存在！");
		}
		
		//保存资源
		if(res.getParentId() == null){
			res.setParentId(0);
		}
		resDao.save(res);
		
		//更新父子关系
		Res parentRes = resDao.getEntity(res.getParentId());
		if(parentRes == null){
			res.setParentSub("_" + res.getId() + "_");
		}else {
			res.setParentSub(parentRes.getParentSub() + res.getId() + "_");
		}
		
		//设置权限位权限码
		Integer[] maxAuthPosCode = getNextMaxAuthPosCode();
		res.setAuthPos(maxAuthPosCode[0]);
		res.setAuthCode(maxAuthPosCode[1]);
		resDao.update(res);
		
		//刷新缓存
		ResCache.flushCache();
	}
	
	@Override
	public void editAndUpdate(Res res) {
		//校验数据有效性
		if(!ValidateUtil.isValid(res.getName())){
			throw new RuntimeException("无法获取参数：res.name");
		}
		if(!ValidateUtil.isValid(res.getUrl())){
			throw new RuntimeException("无法获取参数：res.url");
		}
		
//		if(existName(res)){
//			throw new RuntimeException("名称已存在！");
//		}
		if(existUrl(res)){
			throw new RuntimeException("链接已存在！");
		}
		
		//修改组织机构
		resDao.update(res);
		
		//刷新缓存
		ResCache.flushCache();
	}
	
	@Override
	public void delAndUpdate(Integer[] ids) {
		//校验数据有效性
		if(!ValidateUtil.isValid(ids)){
			throw new RuntimeException("无法获取参数：ids");
		}
		
		//删除资源，不包括根资源
		for(Integer id : ids){
			if(id == 1){
				continue;
			}
			
			Res res = resDao.getEntity(id);
			resExService.delAndUpdate(res);
			
			resDao.del(id);
		}
		
		//刷新缓存
		ResCache.flushCache();
	}
	
	private Integer[] getNextMaxAuthPosCode(){
		Map<String, Object> maxPostCodeMap = resDao.getMaxAuthPosCode();
		Integer maxPos = (Integer) maxPostCodeMap.get("POS");
		Integer maxCode = (Integer) maxPostCodeMap.get("CODE");
		if(maxPos == null){
			maxPos = 0;
			maxCode = 1;
		}else{
			if(maxCode == (1 << 30)){
				maxPos = maxPos + 1;
				maxCode = 1;
			}else{
				maxCode = maxCode << 1;
			}
		}
		
		return new Integer[]{maxPos, maxCode};
	}

	@Override
	public List<Map<String, Object>> getTreeList(Integer type) {
		return resDao.getTreeList(type);
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
		
		Res source = getEntity(sourceId);
		Res target = getEntity(targetId);
		if(target.getParentSub().contains(source.getParentSub())){
			throw new RuntimeException("父组织机构不能移动到子组织机构下！");
		}
		
		//移动资源
		resDao.doMove(sourceId, targetId);
		
		//刷新缓存
		ResCache.flushCache();
	}
	
	/**
	 * 名称是否已存在
	 * v1.0 zhanghc 2016年7月9日下午11:48:30
	 * @param res
	 * @return boolean
	 */
//	private boolean existName(Res res){
//		校验数据有效性
//		if(res == null){
//			throw new RuntimeException("无法获取参数：res");
//		}
//		if(!ValidateUtil.isValid(res.getName())){
//			throw new RuntimeException("无法获取参数：res.name");
//		}
//		
//		//如果是添加
//		Res res2 = resDao.getResByName(res.getName());
//		if(res2 != null){
//			resDao.evict(res2);
//		}
//		
//		if(res.getId() == null){
//			if(res2 != null){
//				return true;
//			}
//			return false;
//		}
//		
//		//如果是修改
//		if(res2 != null && !res.getId().equals(res2.getId())){
//			return true;
//		}
//		return false;
//	}
	
	/**
	 * 链接是否已存在
	 * v1.0 zhanghc 2016年7月9日下午11:48:30
	 * @param res
	 * @return boolean
	 */
	private boolean existUrl(Res res){
		if(res == null){
			throw new RuntimeException("无法获取参数：res");
		}
		if(!ValidateUtil.isValid(res.getUrl())){
			throw new RuntimeException("无法获取参数：res.url");
		}
		
		//如果是添加
		Res res2 = resDao.getResByUrl(res.getUrl());
		if(res2 != null){
			resDao.evict(res2);
		}
		
		if(res.getId() == null){
			if(res2 != null){
				return true;
			}
			return false;
		}
		
		//如果是修改
		if(res2 != null && !res.getId().equals(res2.getId())){
			return true;
		}
		return false;
	}

	@Override
	public List<Res> getList() {
		return resDao.getList();
	}
}
