package com.wcpdoc.exam.base.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.base.cache.ResCache;
import com.wcpdoc.exam.base.dao.ResDao;
import com.wcpdoc.exam.base.entity.Res;
import com.wcpdoc.exam.base.service.ResExService;
import com.wcpdoc.exam.base.service.ResService;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 资源服务层实现
 * 
 * v1.0 zhanghc 2016-6-11下午8:57:40
 */
@Service
public class ResServiceImpl extends BaseServiceImp<Res> implements ResService {
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
	public void addAndUpdate(Res res) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(res.getName())) {
			throw new MyException("参数错误：name");
		}
		if (!ValidateUtil.isValid(res.getUrl())) {
			throw new MyException("参数错误：url");
		}
		if (res.getParentId() == null || res.getParentId() == 0) {
			throw new MyException("参数错误：parentId");
		}
		if (existUrl(res)) {
			throw new MyException("链接已存在");
		}

		// 添加资源
		res.setUpdateUserId(getCurUser().getId());
		res.setUpdateTime(new Date());
		add(res);

		// 更新父子关系
		Res parentRes = getEntity(res.getParentId());
		res.setParentSub(parentRes.getParentSub() + res.getId() + "_");
		res.setLevel(res.getParentSub().split("_").length - 1);

		// 设置权限位权限码
		Integer[] maxAuthPosCode = getNextMaxAuthPosCode();
		res.setAuthPos(maxAuthPosCode[0]);
		res.setAuthCode(maxAuthPosCode[1]);
		update(res);

		// 刷新缓存
		ResCache.flushCache();
	}
	
	@Override
	public void editAndUpdate(Res res) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(res.getName())) {
			throw new MyException("参数错误：name");
		}
		if (!ValidateUtil.isValid(res.getUrl())) {
			throw new MyException("参数错误：url");
		}
		if (existUrl(res)) {
			throw new MyException("链接已存在！");
		}

		// 修改资源
		Res entity = getEntity(res.getId());
		entity.setName(res.getName());
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		entity.setUrl(res.getUrl());
		entity.setNo(res.getNo());
		entity.setIcon(res.getIcon());
		update(entity);

		// 刷新缓存
		ResCache.flushCache();
	}

	@Override
	public void delAndUpdate(Integer id) {
		// 校验数据有效性
		if (id == 1) { //不包括根资源
			return;
		}
		
		List<Res> resList = resDao.getList(id);
		if (ValidateUtil.isValid(resList)) {
			throw new MyException("请先删除子资源！");
		}

		// 删除资源
		Res res = getEntity(id);
		resExService.delAndUpdate(res);
		del(id);

		// 刷新缓存
		ResCache.flushCache();
	}

	private Integer[] getNextMaxAuthPosCode() {
		Map<String, Object> maxPostCodeMap = resDao.getMaxAuthPosCode();
		Integer maxPos = (Integer) maxPostCodeMap.get("POS");
		Integer maxCode = (Integer) maxPostCodeMap.get("CODE");
		if (maxPos == null) {
			maxPos = 0;
			maxCode = 1;
		} else {
			if (maxCode == (1 << 30)) {
				maxPos = maxPos + 1;
				maxCode = 1;
			} else {
				maxCode = maxCode << 1;
			}
		}

		return new Integer[] { maxPos, maxCode };
	}

	@Override
	public List<Map<String, Object>> getTreeList(Integer type) {
		return resDao.getTreeList(type);
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
			throw new MyException("源资源和目标资源一致！");
		}

		Res source = getEntity(sourceId);
		Res target = getEntity(targetId);
		if (target.getParentSub().contains(source.getParentSub())) {
			throw new MyException("父资源不能移动到子资源下！");
		}

		// 移动资源
		source.setParentId(target.getId());
		source.setParentSub(String.format("%s%s_", target.getParentSub(), source.getId()));
		source.setLevel(source.getParentSub().split("_").length - 1);
		update(source);
		
		List<Res> subSourceList = resDao.getList(source.getId());
		doMove(source, subSourceList);

		// 刷新缓存
		ResCache.flushCache();
	}

	private void doMove(Res target, List<Res> subTargetList) {
		for (Res subTarget : subTargetList) {
			subTarget.setParentId(target.getId());
			subTarget.setParentSub(String.format("%s%s_", target.getParentSub(), subTarget.getId()));
			subTarget.setLevel(subTarget.getParentSub().split("_").length - 1);
			update(subTarget);
			
			List<Res> subSubTargetList = resDao.getList(subTarget.getId());
			if (ValidateUtil.isValid(subSubTargetList)) {
				doMove(subTarget, subSubTargetList);
			}
		}
	}

	private boolean existUrl(Res res) {
		return resDao.existName(res.getUrl(), res.getId());
	}

	@Override
	public List<Res> getList() {
		return resDao.getList();
	}
}
