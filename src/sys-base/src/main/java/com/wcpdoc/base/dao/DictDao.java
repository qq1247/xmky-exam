package com.wcpdoc.base.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wcpdoc.base.entity.Dict;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;

/**
 * 数据字典数据访问层接口
 * 
 * v1.0 zhanghc 2016-11-3下午9:03:40
 */
public interface DictDao extends RBaseDao<Dict> {

	@Override
	default PageOut getListpage(PageIn pageIn) {
		Page<Map<String, Object>> page = selectJoinMapsPage(pageIn.toPage(), //
				new MPJQueryWrapper<Dict>().setAlias("DICT")//
						.select("DICT.ID", "DICT.DICT_INDEX", "DICT.DICT_KEY", "DICT.DICT_VALUE", "DICT.NO")//
						.like(pageIn.hasParm("dictIndex"), "DICT.DICT_INDEX", pageIn.getParm("dictIndex"))//
						.like(pageIn.hasParm("dictKey"), "DICT.DICT_KEY", pageIn.getParm("dictKey"))//
						.like(pageIn.hasParm("dictValue"), "DICT.DICT_VALUE", pageIn.getParm("dictValue"))//
						.orderByAsc("DICT.DICT_INDEX", "DICT.NO"));
		return new PageOut(page.getRecords(), page.getTotal());
	}

	/**
	 * 获取列表
	 * 
	 * v1.0 zhanghc 2017年5月23日下午5:18:29
	 * 
	 * @return List<Dict>
	 */
	default List<Dict> getList(String dictIndex) {
		return selectList(new LambdaQueryWrapper<Dict>().eq(Dict::getDictIndex, dictIndex));
	}
}
