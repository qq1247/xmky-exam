package com.wcpdoc.file.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.file.entity.File;

/**
 * 附件数据访问层接口
 * 
 * v1.0 zhanghc 2016-11-16下午10:13:48
 */
public interface FileDao extends RBaseDao<File> {

	@Override
	default PageOut getListpage(PageIn pageIn) {
		Page<Map<String, Object>> page = selectJoinMapsPage(pageIn.toPage(), //
				new MPJQueryWrapper<File>().setAlias("FILE")//
						.select("FILE.ID", "FILE.NAME", "FILE.EXT_NAME", "FILE.IP", "USER.NAME AS USERNAME",
								"FILE.UPDATE_TIME")//
						.leftJoin("SYS_USER USER ON FILE.UPDATE_USER_ID = USER.ID")//
						.like(pageIn.hasParm("name"), "FILE.NAME", pageIn.getParm("name"))//
						.like(pageIn.hasParm("extName"), "FILE.EXT_NAME", pageIn.getParm("extName"))//
						.eq("FILE.STATE", 1)//
						.orderByDesc("FILE.UPDATE_TIME"));//
		return new PageOut(page.getRecords(), page.getTotal());
	}

	/**
	 * 获取已删除附件
	 * 
	 * v1.0 zhanghc 2017年4月24日上午12:08:30
	 * 
	 * @return List<File>
	 */
	default List<File> getDelList() {
		return selectList(new LambdaQueryWrapper<File>().eq(File::getState, 0));
	}
}
