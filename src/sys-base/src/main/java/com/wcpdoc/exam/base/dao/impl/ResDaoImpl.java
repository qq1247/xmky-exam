package com.wcpdoc.exam.base.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.base.dao.ResDao;
import com.wcpdoc.exam.base.entity.Res;
import com.wcpdoc.exam.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.util.SqlUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.core.util.SqlUtil.Order;

/**
 * 资源数据访问层实现
 * 
 * v1.0 zhanghc 2016-6-11下午8:57:40
 */
@Repository
public class ResDaoImpl extends RBaseDaoImpl<Res> implements ResDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT RES.ID, RES.NAME, RES.PARENT_ID, RES.URL, PARENT_RES.NAME AS PARENT_NAME, "
				+ "RES.NO, RES.PARENT_SUB, RES.LEVEL, RES.TYPE, RES.ICON " 
				+ "FROM SYS_RES RES "
				+ "LEFT JOIN SYS_RES PARENT_RES ON RES.PARENT_ID = PARENT_RES.ID";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getOne()) && !"1".equals(pageIn.getOne()), "RES.PARENT_ID = ?", pageIn.getOne())
				.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "RES.NAME LIKE ?", "%" + pageIn.getTwo() + "%")
				.addWhere(ValidateUtil.isValid(pageIn.getThree()), "RES.URL LIKE ?", "%" + pageIn.getThree() + "%")
				.addWhere(ValidateUtil.isValid(pageIn.getFour()), "(RES.TYPE = ? OR RES.ID = 1)", pageIn.getFour())
				.addOrder("RES.NO", Order.ASC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public List<Map<String, Object>> getTreeList(Integer type) {
		String sql = "SELECT ID, NAME, PARENT_ID, PARENT_SUB FROM SYS_RES T WHERE (TYPE = ? OR ID = 1) ORDER BY NO ASC";
		return getMapList(sql, new Object[] { type });
	}
	
	@Override
	public List<Res> getList() {
		String sql = "SELECT * FROM SYS_RES";
		return getList(sql);
	}
	
	@Override
	public List<Res> getList(int parentId) {
		String sql = "SELECT * FROM SYS_RES WHERE PARENT_ID = ?";
		return getList(sql, new Object[] { parentId });
	}

	@Override
	public boolean existName(String url, Integer excludeId) {
		if (excludeId == null) {
			String sql = "SELECT COUNT(*) AS NUM FROM SYS_RES WHERE URL = ?";
			return getCount(sql, new Object[] { url}) > 0;
		}
		
		String sql = "SELECT COUNT(*) AS NUM FROM SYS_RES WHERE URL = ? AND ID != ?";
		return getCount(sql, new Object[] { url, excludeId }) > 0;
	}
	
	@Override
	public Map<String, Object> getMaxAuthPosCode() {
		String sql = "SELECT MAX(RES.AUTH_POS) AS POS,MAX(RES.AUTH_CODE) AS CODE " 
				+ "FROM SYS_RES RES "
				+ "WHERE RES.AUTH_POS = (SELECT MAX(AUTH_POS) FROM SYS_RES)";
		return getMapList(sql).get(0);
	}
}