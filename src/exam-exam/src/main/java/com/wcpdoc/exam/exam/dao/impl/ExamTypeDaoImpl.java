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
import com.wcpdoc.exam.exam.dao.ExamTypeDao;
import com.wcpdoc.exam.exam.entity.ExamType;

/**
 * 考试分类数据访问层实现
 * 
 * v1.0 zhanghc 2017-06-28 21:34:41
 */
@Repository
public class ExamTypeDaoImpl extends BaseDaoImpl<ExamType> implements ExamTypeDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT EXAM_TYPE.ID, EXAM_TYPE.NAME, EXAM_TYPE.PARENT_SUB, "
				+ "EXAM_TYPE.PARENT_ID, EXAM_TYPE.NO, PARENT_EXAM_TYPE.NAME AS PARENT_NAME "
				+ "FROM EXM_EXAM_TYPE EXAM_TYPE "
				+ "LEFT JOIN EXM_EXAM_TYPE PARENT_EXAM_TYPE ON EXAM_TYPE.PARENT_ID = PARENT_EXAM_TYPE.ID";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getOne()) && !"1".equals(pageIn.getOne()), "EXAM_TYPE.PARENT_ID = ?", pageIn.getOne())//如果查询的是根目录，则查询所有。否则查询选中机构的子机构
				.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "EXAM_TYPE.NAME LIKE ?", "%" + pageIn.getTwo() + "%")
				.addWhere("EXAM_TYPE.STATE = ?", 1)
				.addOrder("EXAM_TYPE.NO", Order.ASC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public List<Map<String, Object>> getTreeList() {
		String sql = "SELECT EXAM_TYPE.ID, EXAM_TYPE.NAME, EXAM_TYPE.PARENT_ID, EXAM_TYPE.PARENT_SUB "
				+ "FROM EXM_EXAM_TYPE EXAM_TYPE "
				+ "WHERE EXAM_TYPE.STATE = 1";
		return getList(sql);
	}

	@Override
	public void doMove(Integer sourceId, Integer targetId) {
		ExamType sourceExamType = getEntity(sourceId);
		ExamType targetExamType = getEntity(targetId);
		sourceExamType.setParentId(targetId);
		flush();

		String sql = "UPDATE EXM_EXAM_TYPE EXAM_TYPE" 
				+ "	SET EXAM_TYPE.PARENT_SUB = REPLACE(EXAM_TYPE.PARENT_SUB, ?, ?)"
				+ "	WHERE EXAM_TYPE.PARENT_SUB LIKE ?";
		Object[] params = new Object[] { 
					sourceExamType.getParentSub(), 
					targetExamType.getParentSub() + sourceExamType.getId() + "_",
					sourceExamType.getParentSub() + "%" };
		update(sql, params);
	}
	
	@Override
	public List<ExamType> getAllSubExamTypeList(Integer id) {
		String sql = "SELECT * FROM EXM_EXAM_TYPE WHERE PARENT_SUB LIKE ? AND STATE = 1";
		return getList(sql, new Object[]{"%\\_"+id+"\\_%"}, ExamType.class);
	}
	
	@Override
	public ExamType getExamTypeByName(String name) {
		String sql = "SELECT * FROM EXM_EXAM_TYPE WHERE NAME = ? AND STATE = 1";
		return getUnique(sql, new Object[]{name}, ExamType.class);
	}
}