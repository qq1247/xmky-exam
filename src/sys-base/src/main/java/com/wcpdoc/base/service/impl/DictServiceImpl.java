package com.wcpdoc.base.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.wcpdoc.base.constant.BaseConstant;
import com.wcpdoc.base.dao.DictDao;
import com.wcpdoc.base.entity.Dict;
import com.wcpdoc.base.service.DictService;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;

/**
 * 数据字典服务层实现
 * 
 * v1.0 zhanghc 2016-11-3下午9:03:40
 */
@Service
public class DictServiceImpl extends BaseServiceImp<Dict> implements DictService {
	@Resource
	private DictDao dictDao;

	public RBaseDao<Dict> getDao() {
		return dictDao;
	}

	@Override
	@CacheEvict(value = BaseConstant.DICT_CACHE, allEntries = true)
	public void addEx(Dict dict) {
		save(dict);
	}

	@Override
	@CacheEvict(value = BaseConstant.DICT_CACHE, allEntries = true)
	public void editEx(Dict dict) {
		Dict entity = getById(dict.getId());
		entity.setDictIndex(dict.getDictIndex());
		entity.setDictKey(dict.getDictKey());
		entity.setDictValue(dict.getDictValue());
		entity.setNo(dict.getNo());
		updateById(entity);
	}

	@Override
	@CacheEvict(value = BaseConstant.DICT_CACHE, allEntries = true)
	public void delEx(Integer id) {
		removeById(id);
	}

	@Override
	public List<Dict> getList(String index) {
		return dictDao.getList(index);
	}
}
