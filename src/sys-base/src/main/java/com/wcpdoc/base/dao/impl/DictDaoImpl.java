 package com.wcpdoc.base.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wcpdoc.base.dao.DictDao;
import com.wcpdoc.base.entity.Dict;
import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.util.SqlUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.core.util.SqlUtil.Order;

/**
 * 数据字典数据访问层实现
 * 
 * v1.0 zhanghc 2016-11-3下午9:03:40
 */
@Repository
public class DictDaoImpl extends RBaseDaoImpl<Dict> implements DictDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT DICT.ID, DICT.DICT_INDEX, DICT.DICT_KEY, DICT.DICT_VALUE, DICT.NO "
				+ "FROM SYS_DICT DICT ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("dictIndex")), "DICT.DICT_INDEX LIKE :DICT_INDEX", "%" + pageIn.get("dictIndex") + "%")
				.addWhere(ValidateUtil.isValid(pageIn.get("dictKey")), "DICT.DICT_KEY LIKE :DICT_KEY", "%" + pageIn.get("dictKey") + "%")
				.addWhere(ValidateUtil.isValid(pageIn.get("dictValue")), "DICT.DICT_VALUE LIKE :DICT_VALUE", "%" + pageIn.get("dictValue") + "%")
				.addOrder("DICT.DICT_INDEX", Order.ASC)
				.addOrder("DICT.NO", Order.ASC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public List<Dict> getListByIndex(String index) {
		String sql = "SELECT * FROM SYS_DICT WHERE DICT_INDEX = :DICT_INDEX ORDER BY NO ASC";
		return getList(sql, new Object[] { index });
	}
}