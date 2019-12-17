package com.wcpdoc.exam.sys.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.core.dao.impl.BaseDaoImpl;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.util.SqlUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.core.util.SqlUtil.Order;
import com.wcpdoc.exam.sys.dao.OrgDao;
import com.wcpdoc.exam.sys.entity.Org;

/**
 * 组织机构数据访问层实现
 * 
 * v1.0 zhanghc 2016-5-8上午11:00:00
 */
@Repository
public class OrgDaoImpl extends BaseDaoImpl<Org> implements OrgDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT ORG.ID, ORG.NAME, ORG.PARENT_ID, PARENT_ORG.NAME AS PARENT_NAME, "
				+ "ORG.PARENT_SUB, ORG.NO " //
				+ "FROM SYS_ORG ORG "
				+ "LEFT JOIN SYS_ORG PARENT_ORG ON ORG.PARENT_ID = PARENT_ORG.ID";
		SqlUtil sqlUtil = new SqlUtil(sql);
		// 如果查询的是根目录，则查询所有。否则查询选中机构的子机构
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getOne()) && !"1".equals(pageIn.getOne()), "ORG.PARENT_ID = ?", pageIn.getOne())
				.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "ORG.NAME LIKE ?", "%" + pageIn.getTwo() + "%")
				.addWhere("ORG.STATE = ?", 1).addOrder("ORG.NO", Order.ASC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public List<Map<String, Object>> getTreeList() {
		String sql = "SELECT T.ID, T.NAME, T.PARENT_ID, T.PARENT_SUB FROM SYS_ORG T WHERE T.STATE = 1";
		return getList(sql);
	}

	@Override
	/**
	 * 	1	组织机构	0	_1_				SYS	2016-05-14 13:58:19
	 *	33	山西总部	1	_1_33_			sys	2016-05-14 13:58:51
	 *	34	北京分部	1	_1_34_			sys	2016-05-14 13:58:56
	 *	35	研发部	33	_1_33_35_		sys	2016-05-14 13:59:06
	 *	36	测试		35	_1_33_35_36_	sys	2016-05-14 13:59:12
	 *	37	开发		35	_1_33_35_37_	sys	2016-05-14 13:59:19
	 *	38	工程部	33	_1_33_38_		sys	2016-05-14 13:59:24
	 *	39	工程部1	38	_1_33_38_39_	sys	2016-05-14 14:01:53
	 *	40	工程部2	38	_1_33_38_40_	sys	2016-05-14 14:01:57
	 */
	public void doMove(Integer sourceId, Integer targetId) {
		Org sourceOrg = getEntity(sourceId);
		Org targetOrg = getEntity(targetId);
		sourceOrg.setParentId(targetId);
		flush();

		String sql = "UPDATE SYS_ORG ORG" //
				+ "   SET ORG.PARENT_SUB = REPLACE(ORG.PARENT_SUB, ?, ?)" + " WHERE ORG.PARENT_SUB LIKE ?";
		Object[] params = new Object[] { sourceOrg.getParentSub(), targetOrg.getParentSub() + sourceOrg.getId() + "_",
				sourceOrg.getParentSub() + "%" };
		update(sql, params);
	}

	@Override
	public List<Org> getAllSubOrgList(Integer id) {
		String sql = "SELECT * FROM SYS_ORG WHERE PARENT_SUB LIKE ? AND STATE = 1";
		return getList(sql, new Object[] { "%\\_" + id + "\\_%" }, Org.class);
	}

	@Override
	public Org getOrgByName(String name) {
		String sql = "SELECT * FROM SYS_ORG WHERE NAME = ? AND STATE = 1";
		return getUnique(sql, new Object[] { name }, Org.class);
	}
}