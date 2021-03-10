package com.wcpdoc.exam.base.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.base.dao.PostDao;
import com.wcpdoc.exam.base.entity.Post;
import com.wcpdoc.exam.base.entity.Res;
import com.wcpdoc.exam.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.util.SqlUtil;
import com.wcpdoc.exam.core.util.SqlUtil.Order;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 岗位数据访问层实现
 * 
 * v1.0 zhanghc 2016-5-19下午9:32:43
 */
@Repository
public class PostDaoImpl extends RBaseDaoImpl<Post> implements PostDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT POST.ID, POST.NAME, ORG.NAME AS ORGNAME, USER.NAME AS UPDATE_USER_NAME, "
				+ "POST.UPDATE_TIME, POST.CODE "
				+ "FROM SYS_POST POST "
				+ "INNER JOIN SYS_ORG ORG ON POST.ORG_ID = ORG.ID "
				+ "LEFT JOIN SYS_USER USER ON POST.UPDATE_USER_ID = USER.ID ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getOne()) && !("1".equals(pageIn.getOne()) || pageIn.getOne().equals(pageIn.getTen())), "POST.ORG_ID = ?", pageIn.getOne())
				.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "POST.NAME LIKE ?", "%" + pageIn.getTwo() + "%")
				.addWhere(ValidateUtil.isValid(pageIn.getThree()), "ORG.NAME LIKE ?", "%" + pageIn.getThree() + "%")
				.addWhere("POST.STATE = 1")
				.addOrder("POST.UPDATE_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public List<Post> getOrgPostList(Integer orgId) {
		String sql = "SELECT * FROM SYS_POST WHERE STATE = 1 AND ORG_ID = ? ";
		return getList(sql, new Object[] { orgId });
	}

	@Override
	public boolean existName(String name, Integer excludeId) {
		if (excludeId == null) {
			String sql = "SELECT COUNT(*) AS NUM FROM SYS_POST WHERE NAME = ? AND STATE = 1";
			return getCount(sql, new Object[] { name }) > 0;
		}

		String sql = "SELECT COUNT(*) AS NUM FROM SYS_POST WHERE NAME = ? AND STATE = 1 AND ID != ?";
		return getCount(sql, new Object[] { name, excludeId }) > 0;
	}

	@Override
	public boolean existCode(String code, Integer excludeId) {
		if (excludeId == null) {
			String sql = "SELECT COUNT(*) AS NUM FROM SYS_POST WHERE CODE = ? AND CODE != '' AND CODE IS NOT NULL AND STATE = 1";
			return getCount(sql, new Object[] { code }) > 0;
		}

		String sql = "SELECT COUNT(*) AS NUM FROM SYS_POST WHERE CODE = ? AND CODE != '' AND CODE IS NOT NULL AND STATE = 1 AND ID != ?";
		return getCount(sql, new Object[] { code, excludeId }) > 0;
	}

	@Override
	public List<Res> getResList(Integer id) {
		String sql = "SELECT RES.* "
				+ "FROM SYS_RES RES "
				+ "WHERE EXISTS (SELECT 1 FROM SYS_POST Z WHERE Z.ID = ? AND Z.RES_IDS LIKE CONCAT('%,' , RES.ID, ',%'))";
		return getList(sql, new Object[] {id}, Res.class);
	}

	@Override
	public List<Post> getResPostList(Integer resId) {
		String sql = "SELECT * FROM SYS_POST WHERE STATE = 1 AND RES_IDS LIKE ?";
		return getList(sql, new Object[] {String.format("%%,%s,%%", resId)});
	}

	@Override
	public List<Post> getList(Integer orgId) {
		String sql = "SELECT * FROM SYS_POST WHERE STATE = 1 AND ORG_ID = ?";
		return getList(sql, new Object[] { orgId });
	}

	@Override
	public Post getPost(String name) {
		String sql = "SELECT * FROM SYS_POST WHERE STATE = 1 AND NAME = ?";
		return getEntity(sql, new Object[] { name });
	}
}