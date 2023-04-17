package com.wcpdoc.exam.core.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.ExerRmkDao;
import com.wcpdoc.exam.core.entity.ExerRmk;
import com.wcpdoc.exam.core.service.ExerRmkService;

/**
 * 模拟练习评论服务层实现
 * 
 * v1.0 zhanghc 2016-5-8上午11:00:00
 */
@Service
public class ExerRmkServiceImpl extends BaseServiceImp<ExerRmk> implements ExerRmkService {
	@Resource
	private ExerRmkDao exerRmkDao;

	@Override
	@Resource(name = "exerRmkDaoImpl")
	public void setDao(BaseDao<ExerRmk> dao) {
		super.dao = dao;
	}

	@Override
	public void addEx(ExerRmk exerRmk, Integer anonymity) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(exerRmk.getContent())) {
			throw new MyException("参数错误：content");
		}
		if (exerRmk.getQuestionId() == null || exerRmk.getQuestionId() <= 0) {
			throw new MyException("参数错误：questionId");
		}
		if (anonymity == null) {
			throw new MyException("参数错误：anonymity");
		}
		
		// 添加模拟练习评论
		if (exerRmk.getParentId() == null) {
			exerRmk.setParentId(0);
		}
		
		if (anonymity == 1) {// 0: 匿名  1： 不匿名
			exerRmk.setCreateUserId(getCurUser().getId());
		}
		exerRmk.setCreateTime(new Date());
		exerRmk.setState(1);
		add(exerRmk);
		
		// 更新父子关系
		if(exerRmk.getParentId() == 0){
			exerRmk.setParentSub("_" + exerRmk.getId() + "_");
			exerRmk.setLevel(exerRmk.getParentSub().split("_").length - 1);
			update(exerRmk);
			return;
		}
		ExerRmk parentExerRmk = exerRmkDao.getEntity(exerRmk.getParentId());
		exerRmk.setParentSub(parentExerRmk.getParentSub() + exerRmk.getId() + "_");
		exerRmk.setLevel(exerRmk.getParentSub().split("_").length - 1);
		update(exerRmk);
	}

	@Override
	public void delEx(Integer id) {
		// 校验数据有效性
		List<Map<String, Object>> list = exerRmkDao.getList(id);
		if (list.size() > 0) {
			throw new MyException("请先删除子模拟练习评论");
		}
		
		// 删除模拟练习评论
		ExerRmk exerRmk = getEntity(id);
		exerRmk.setState(0);
		exerRmk.setUpdateTime(new Date());
		exerRmk.setUpdateUserId(getCurUser().getId());
		update(exerRmk);
	}

	@Override
	public List<Map<String, Object>> getList(Integer parentId) {
		List<Map<String, Object>> list = exerRmkDao.getList(parentId);
		for(Map<String, Object> map : list){
			if (map.get("createTime") != null) {
				map.put("createTime", DateUtil.formatDateTime( DateUtil.getDateTime(map.get("createTime").toString())));
			}
		}
		return list;
	}
}
