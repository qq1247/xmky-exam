package com.wcpdoc.exam.base.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.base.cache.DictCache;
import com.wcpdoc.exam.base.dao.DictDao;
import com.wcpdoc.exam.base.entity.Dict;
import com.wcpdoc.exam.base.service.DictService;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;

/**
 * 数据字典服务层实现
 * 
 * v1.0 zhanghc 2016-11-3下午9:03:40
 */
@Service
public class DictServiceImpl extends BaseServiceImp<Dict> implements DictService {
	@Resource
	private DictDao dictDao;

	@Override
	@Resource(name = "dictDaoImpl")
	public void setDao(BaseDao<Dict> dao) {
		super.dao = dao;
	}

	@Override
	public List<Dict> getList() {
		return dictDao.getList();
	}

	@Override
	public void addAndUpdate(Dict dict) {
		// 添加数据字典
		add(dict);

		// 刷新缓存
		DictCache.flushCache();
	}

	@Override
	public void updateAndUpdate(Dict dict) {
		// 修改数据字典
		Dict entity = getEntity(dict.getId());
		entity.setDictIndex(dict.getDictIndex());
		entity.setDictKey(dict.getDictKey());
		entity.setDictValue(dict.getDictValue());
		entity.setNo(dict.getNo());
		update(entity);

		// 刷新缓存
		DictCache.flushCache();
	}

	@Override
	public void delAndUpdate(Integer id) {
		// 删除数据字典
		del(id);

		// 刷新缓存
		DictCache.flushCache();
	}
}
