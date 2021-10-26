package com.wcpdoc.file.dao;

import java.util.List;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.file.entity.File;

/**
 * 附件数据访问层接口
 * 
 * v1.0 zhanghc 2016-11-16下午10:13:48
 */
public interface FileDao extends RBaseDao<File> {

	/**
	 * 获取已删除附件
	 * 
	 * v1.0 zhanghc 2017年4月24日上午12:08:30
	 * 
	 * @return List<File>
	 */
	List<File> getDelList();
}
