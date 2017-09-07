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
import com.wcpdoc.exam.sys.dao.ResDao;
import com.wcpdoc.exam.sys.entity.Res;

/**
 * 资源数据访问层实现
 * 
 * v1.0 zhanghc 2016-6-11下午8:57:40
 */
@Repository
public class ResDaoImpl extends BaseDaoImpl<Res> implements ResDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT RES.ID, RES.NAME, RES.PARENT_ID, RES.URL, "
				+ "PARENT_RES.NAME AS PARENT_NAME, RES.NO, RES.PARENT_SUB "
				+ "FROM SYS_RES RES "
				+ "LEFT JOIN SYS_RES PARENT_RES ON RES.PARENT_ID = PARENT_RES.ID";
		SqlUtil sqlUtil = new SqlUtil(sql);
		//如果查询的是根目录，则查询所有。否则查询选中机构的子机构
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getOne()) && !"1".equals(pageIn.getOne()), "RES.PARENT_ID = ?", pageIn.getOne())
				.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "RES.NAME LIKE ?", "%" + pageIn.getTwo() + "%")
				.addWhere(ValidateUtil.isValid(pageIn.getThree()), "RES.URL LIKE ?", "%" + pageIn.getThree() + "%")
				.addWhere(ValidateUtil.isValid(pageIn.getTen()), "(RES.TYPE = ? OR RES.ID = 1)", pageIn.getTen())
				.addOrder("RES.NO", Order.ASC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public List<Map<String, Object>> getTreeList(Integer type) {
		String sql = "SELECT T.ID, T.NAME, T.PARENT_ID, T.PARENT_SUB FROM SYS_RES T WHERE (T.TYPE = ? OR T.ID = 1) ORDER BY T.NO ASC";
		return getList(sql, new Object[]{type});
	}

	@Override
	public void doMove(Integer sourceId, Integer targetId) {
		Res sourceRes = getEntity(sourceId);
		Res targetRes = getEntity(targetId);
		sourceRes.setParentId(targetId);
		flush();

		String sql = "UPDATE SYS_RES RES" //
				+ "   SET RES.PARENT_SUB = REPLACE(RES.PARENT_SUB, ?, ?)"
				+ " WHERE RES.PARENT_SUB LIKE ?";
		Object[] params = new Object[] { 
					sourceRes.getParentSub(), 
					targetRes.getParentSub() + sourceRes.getId() + "_",
					sourceRes.getParentSub()+"%" };
		update(sql, params);
	}

	@Override
	public Map<String, Object> getMaxAuthPosCode() {
		String sql = "SELECT MAX(RES.AUTH_POS) AS POS,MAX(RES.AUTH_CODE) AS CODE "
				+ "FROM SYS_RES RES "
				+ "WHERE RES.AUTH_POS = (SELECT MAX(AUTH_POS) FROM SYS_RES)";
		return getUnique(sql);
	}

	@Override
	public Res getResByName(String name) {
		String sql = "SELECT * FROM SYS_RES WHERE NAME = ?";
		return getUnique(sql, new Object[]{name}, Res.class);
	}

	@Override
	public Res getResByUrl(String url) {
		String sql = "SELECT * FROM SYS_RES WHERE URL = ?";
		return getUnique(sql, new Object[]{url}, Res.class);
	}

	@Override
	public List<Res> getResByParentId(Integer parentId) {
		String sql = "SELECT * FROM SYS_RES WHERE PARENT_ID = ? ORDER BY NO ASC";
		return getList(sql, new Object[]{parentId}, Res.class);
	}

	@Override
	public List<Res> getList() {
		String sql = "SELECT * FROM SYS_RES";
		return getList(sql, Res.class);
	}
}