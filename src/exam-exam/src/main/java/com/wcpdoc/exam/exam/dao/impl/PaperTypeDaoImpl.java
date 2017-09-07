package com.wcpdoc.exam.exam.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.core.dao.impl.BaseDaoImpl;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.util.SqlUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.core.util.SqlUtil.Order;
import com.wcpdoc.exam.exam.dao.PaperTypeDao;
import com.wcpdoc.exam.exam.entity.PaperType;

/**
 * 试卷分类数据访问层实现
 * 
 * v1.0 zhanghc 2017-05-25 16:34:59
 */
@Repository
public class PaperTypeDaoImpl extends BaseDaoImpl<PaperType> implements PaperTypeDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT PAPER_TYPE.ID, PAPER_TYPE.NAME, PAPER_TYPE.PARENT_ID, "
				+ "PARENT_PAPER_TYPE.NAME AS PARENT_NAME, PAPER_TYPE.PARENT_SUB, "
				+ "PAPER_TYPE.NO "
				+ "FROM EX_PAPER_TYPE PAPER_TYPE "
				+ "LEFT JOIN EX_PAPER_TYPE PARENT_PAPER_TYPE ON PAPER_TYPE.PARENT_ID = PARENT_PAPER_TYPE.ID";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getOne()) && !"1".equals(pageIn.getOne()), "PAPER_TYPE.PARENT_ID = ?", pageIn.getOne())//如果查询的是根目录，则查询所有。否则查询选中机构的子机构
				.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "PAPER_TYPE.NAME LIKE ?", "%" + pageIn.getTwo() + "%")
				.addWhere("PAPER_TYPE.STATE = ?", 1)
				.addOrder("PAPER_TYPE.NO", Order.ASC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public List<Map<String, Object>> getTreeList() {
		String sql = "SELECT PAPER_TYPE.ID, PAPER_TYPE.NAME, PAPER_TYPE.PARENT_ID, PAPER_TYPE.PARENT_SUB "
				+ "FROM EX_PAPER_TYPE PAPER_TYPE "
				+ "WHERE PAPER_TYPE.STATE = 1";
		return getList(sql);
	}

	@Override
	public void doMove(Integer sourceId, Integer targetId) {
		PaperType sourcePaperType = getEntity(sourceId);
		PaperType targetPaperType = getEntity(targetId);
		sourcePaperType.setParentId(targetId);
		flush();

		String sql = "UPDATE EX_PAPER_TYPE PAPER_TYPE" //
				+ "	SET PAPER_TYPE.PARENT_SUB = REPLACE(PAPER_TYPE.PARENT_SUB, ?, ?)"
				+ "	WHERE PAPER_TYPE.PARENT_SUB LIKE ?";
		Object[] params = new Object[] { 
					sourcePaperType.getParentSub(), 
					targetPaperType.getParentSub() + sourcePaperType.getId() + "_",
					sourcePaperType.getParentSub() + "%" };
		update(sql, params);
	}
	
	@Override
	public List<PaperType> getAllSubPaperTypeList(Integer id) {
		String sql = "SELECT * FROM EX_PAPER_TYPE WHERE PARENT_SUB LIKE ? AND STATE = 1";
		return getList(sql, new Object[]{"%\\_"+id+"\\_%"}, PaperType.class);
	}
	
	@Override
	public PaperType getPaperTypeByName(String name) {
		String sql = "SELECT * FROM EX_PAPER_TYPE WHERE NAME = ? AND STATE = 1";
		return getUnique(sql, new Object[]{name}, PaperType.class);
	}
}