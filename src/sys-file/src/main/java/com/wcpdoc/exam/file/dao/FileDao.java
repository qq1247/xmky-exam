package com.wcpdoc.exam.file.dao;

import java.util.List;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.file.entity.File;

/**
 * 附件数据访问层接口
 * 
 * v1.0 zhanghc 2016-11-16下午10:13:48
 */
public interface FileDao extends BaseDao<File> {

	/**
	 * 获取已删除附件
	 * 
	 * v1.0 zhanghc 2017年4月24日上午12:08:30
	 * 
	 * @return List<File>
	 */
	List<File> getDelList();
}
