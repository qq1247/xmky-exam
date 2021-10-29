package com.wcpdoc.base.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.wcpdoc.base.dao.OrgDao;
import com.wcpdoc.base.entity.Org;
import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.util.SqlUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.core.util.SqlUtil.Order;

/**
 * 组织机构数据访问层实现
 * 
 * v1.0 zhanghc 2016-5-8上午11:00:00
 */
@Repository
public class OrgDaoImpl extends RBaseDaoImpl<Org> implements OrgDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT ORG.ID, ORG.NAME, ORG.PARENT_ID, PARENT_ORG.NAME AS PARENT_NAME, "
				+ "ORG.PARENT_IDS, ORG.NO " 
				+ "FROM SYS_ORG ORG "
				+ "LEFT JOIN SYS_ORG PARENT_ORG ON ORG.PARENT_ID = PARENT_ORG.ID";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("parentId")) && !("1".equals(pageIn.get("parentId")) || pageIn.get("parentId").equals(pageIn.get("Ten"))), "ORG.PARENT_ID = ?", pageIn.get("parentId"))
				.addWhere(ValidateUtil.isValid(pageIn.get("name")), "ORG.NAME LIKE ?", "%" + pageIn.get("name") + "%")
				.addWhere("ORG.STATE = 1")
				.addOrder("ORG.NO", Order.ASC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public List<Map<String, Object>> getTreeList() {
		String sql = "SELECT T.ID, T.NAME, T.PARENT_ID, T.PARENT_IDS FROM SYS_ORG T WHERE T.STATE = 1 ORDER BY T.NO ASC";
		return getMapList(sql);
	}

	@Override
	public List<Org> getList() {
		String sql = "SELECT * FROM SYS_ORG WHERE STATE = 1 ";
		return getList(sql);
	}
	
	@Override
	public List<Org> getList(Integer parentId) {
		String sql = "SELECT * FROM SYS_ORG WHERE PARENT_ID = ? AND STATE = 1 ";
		return getList(sql, new Object[] { parentId });
	}

	@Override
	public boolean existName(String name, Integer excludeId) {
		if (excludeId == null) {
			String sql = "SELECT COUNT(*) AS NUM FROM SYS_ORG WHERE NAME = ? AND STATE = 1";
			return getCount(sql, new Object[] { name }) > 0;
		}

		String sql = "SELECT COUNT(*) AS NUM FROM SYS_ORG WHERE NAME = ? AND STATE = 1 AND ID != ?";
		return getCount(sql, new Object[] { name, excludeId }) > 0;
	}

	@Override
	public boolean existCode(String code, Integer excludeId) {
		if (excludeId == null) {
			String sql = "SELECT COUNT(*) AS NUM FROM SYS_ORG WHERE CODE = ? AND CODE != '' AND CODE IS NOT NULL AND STATE = 1";
			return getCount(sql, new Object[] { code }) > 0;
		}

		String sql = "SELECT COUNT(*) AS NUM FROM SYS_ORG WHERE CODE = ? AND CODE != '' AND CODE IS NOT NULL AND STATE = 1 AND ID != ?";
		return getCount(sql, new Object[] { code, excludeId }) > 0;
	}

	@Override
	public Org getOrg(String name) {
		String sql = "SELECT * FROM SYS_ORG WHERE NAME = ? AND STATE = 1";
		return getEntity(sql, new Object[] { name });
	}

	@Override
	public Org getOrg(String name, String code) {
		String sql = "SELECT * FROM SYS_ORG WHERE NAME = ? AND CODE = ? AND STATE = 1";
		return getEntity(sql, new Object[] { name, code });
	}
}