package com.wcpdoc.exam.core.dao;

import com.wcpdoc.exam.core.dao.RBaseDao;
import com.wcpdoc.exam.core.entity.PaperOption;

/**
 * 试卷数据访问层接口
 * 
 * v1.0 chenyun 2021-03-10 13:47:35
 */
public interface PaperOptionDao extends RBaseDao<PaperOption> {
	
	/**
	 * 获取实体
	 * 
	 * v1.0 chenyun 2021年3月10日下午3:31:55
	 * @param paperId
	 * @return PaperOption
	 */
	PaperOption getPaperOption(Integer paperId);
}
