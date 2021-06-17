package com.wcpdoc.exam.file.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.util.DateUtil;
import com.wcpdoc.exam.core.util.HibernateUtil;
import com.wcpdoc.exam.core.util.SqlUtil;
import com.wcpdoc.exam.core.util.SqlUtil.Order;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.file.dao.FileDao;
import com.wcpdoc.exam.file.entity.File;

/**
 * 附件数据访问层实现
 * 
 * v1.0 zhanghc 2016-11-16下午10:13:48
 */
@Repository
public class FileDaoImpl extends RBaseDaoImpl<File> implements FileDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT FILE.ID, FILE.NAME, FILE.EXT_NAME, FILE.IP, USER.NAME AS USERNAME, FILE.UPDATE_TIME "
				+ "FROM SYS_FILE FILE "
				+ "LEFT JOIN SYS_USER USER ON FILE.UPDATE_USER_ID = USER.ID";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("name")), "FILE.NAME LIKE ?", "%" + pageIn.get("name") + "%");
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("extName")), "FILE.EXT_NAME LIKE ?", "%" + pageIn.get("extName") + "%");
		sqlUtil.addWhere("FILE.STATE = 1");
		sqlUtil.addOrder("FILE.UPDATE_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDate(pageOut.getList(), "UPDATE_TIME", DateUtil.FORMAT_DATE_TIME);
		return pageOut;
	}

	@Override
	public List<File> getDelList() {
		String sql = "SELECT * FROM SYS_FILE WHERE STATE = 0";
		return getList(sql);
	}
}