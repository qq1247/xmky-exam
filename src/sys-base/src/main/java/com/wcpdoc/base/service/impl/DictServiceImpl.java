package com.wcpdoc.base.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.base.dao.DictDao;
import com.wcpdoc.base.entity.Dict;
import com.wcpdoc.base.service.DictService;
import com.wcpdoc.core.dao.BaseDao;
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
	}

	@Override
	public void delAndUpdate(Integer id) {
		// 删除数据字典
		del(id);
	}

	@Override
	public List<Dict> getList(String index) {
		List<Dict> dictList = dictDao.getList();
		List<Dict> targetDictList = new ArrayList<>();
		for (Dict dict : dictList) {
			if (!dict.getDictIndex().equals(index)) {
				continue;
			}
			targetDictList.add(dict);
		}
		
		Collections.sort(targetDictList, new Comparator<Dict>() {
			@Override
			public int compare(Dict o1, Dict o2) {
				return o1.getNo() - o2.getNo();
			}
			
		});
		return targetDictList;
	}
}
