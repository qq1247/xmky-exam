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
import com.wcpdoc.exam.sys.dao.PostDao;
import com.wcpdoc.exam.sys.entity.Org;
import com.wcpdoc.exam.sys.entity.Post;

/**
 * 岗位数据访问层实现
 * 
 * v1.0 zhanghc 2016-5-19下午9:32:43
 */
@Repository
public class PostDaoImpl extends BaseDaoImpl<Post> implements PostDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT POST.ID, POST.NAME, ORG.NAME AS ORGNAME "
				+ "FROM SYS_POST POST "
				+ "INNER JOIN SYS_ORG_POST ORGPOST ON POST.ID = ORGPOST.POST_ID "
				+ "INNER JOIN SYS_ORG ORG ON ORGPOST.ORG_ID = ORG.ID";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getOne()) && !"1".equals(pageIn.getOne()), "ORGPOST.ORG_ID = ?", pageIn.getOne())
				.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "POST.NAME LIKE ?", "%" + pageIn.getTwo() + "%")
				.addWhere("POST.STATE = ?", 1)
				.addOrder("POST.NAME", Order.DESC);
		
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public List<Post> getOrgPostList(Integer orgId) {
		String sql = "SELECT POST.* "
				+ "FROM SYS_POST POST "
				+ "INNER JOIN SYS_ORG_POST ORGPOST ON POST.ID = ORGPOST.POST_ID "
				+ "WHERE POST.STATE = 1 AND ORGPOST.ORG_ID = ? ";
		return getList(sql, new Object[]{orgId}, Post.class);
	}

	@Override
	public List<Map<String, Object>> getOrgPostTreeList() {
		String sql = "SELECT ORG.ID, ORG.NAME, ORG.PARENT_ID, 'icon-org' AS ICON, 'ORG' AS TYPE "
				+ "FROM SYS_ORG ORG "
				+ "WHERE ORG.STATE = 1 "
				+ "UNION ALL "
				+ "SELECT POST.ID, POST.NAME, ORGPOST.ORG_ID AS PARENT_ID, 'icon-man' as ICON, 'POST' AS TYPE "
				+ "FROM SYS_POST POST "
				+ "INNER JOIN SYS_ORG_POST ORGPOST ON POST.ID = ORGPOST.POST_ID "
				+ "WHERE POST.STATE = 1";
		return getList(sql);
	}

	@Override
	public List<Map<String, Object>> getResUpdateResTreeList(Integer id) {
		String sql = "SELECT RES.ID, RES.NAME, RES.PARENT_ID, "
				+ "		CASE WHEN POST_RES.ID IS NOT NULL THEN TRUE END AS CHECKED "
				+ "FROM SYS_RES RES "
				+ "LEFT JOIN (SELECT B1.* "
				+ "				FROM SYS_RES B "
				+ "				INNER JOIN SYS_POST_RES B1 ON B.ID = B1.RES_ID "
				+ "				WHERE B.PARENT_SUB LIKE '\\_%\\_%\\_%\\_%\\_%\\_' "
				+ "					AND B1.POST_ID = ? ) POST_RES ON RES.ID = POST_RES.RES_ID ";/* 某个岗位选中的第四级资源 */
		return getList(sql, new Object[]{id});
	}

	@Override
	public Org getOrg(Integer id) {
		String sql = "SELECT ORG.* "
				+ "FROM SYS_ORG ORG "
				+ "INNER JOIN SYS_ORG_POST ORGPOST ON ORG.ID = ORGPOST.ORG_ID "
				+ "WHERE ORG.STATE = 1 AND ORGPOST.POST_ID = ?";
		return getUnique(sql, new Object[]{id}, Org.class);
	}

	@Override
	public Post getPostByName(String name) {
		String sql = "SELECT * FROM SYS_POST WHERE NAME = ? AND STATE = 1";
		return getUnique(sql, new Object[]{name}, Post.class);
	}
}