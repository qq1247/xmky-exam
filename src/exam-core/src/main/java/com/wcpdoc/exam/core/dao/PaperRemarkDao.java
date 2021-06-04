package com.wcpdoc.exam.core.dao;

import java.util.List;

import com.wcpdoc.exam.core.dao.RBaseDao;
import com.wcpdoc.exam.core.entity.PaperRemark;


/**
 * 试卷评语数据访问层接口
 * 
 * v1.0 chenyun 2021-03-10 13:48:34
 */
public interface PaperRemarkDao extends RBaseDao<PaperRemark> {
	
	/**
	 * 获取试卷评语
	 * 
	 * v1.0 chenyun 2021年3月10日下午3:31:22
	 * @param paperId
	 * @return PaperRemark
	 */
	List<PaperRemark> getPaperRemarkList(Integer paperId);
}
