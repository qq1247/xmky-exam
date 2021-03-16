package com.wcpdoc.exam.core.service;

import com.wcpdoc.exam.core.entity.PaperRemark;

/**
 * 试卷评语服务层接口
 * 
 * v1.0 chenyun 2021-03-10 13:48:34
 */
public interface PaperRemarkService extends BaseService<PaperRemark> {

	/**
	 * 删除试卷评语
	 * 
	 * v1.0 chenyun 2021-03-10 13:48:34
	 * 
	 * @param id
	 * void
	 */
	void delAndUpdate(Integer id);
	
	/**
	 * 获取试卷评语
	 * 
	 * v1.0 chenyun 2021年3月10日下午3:29:31
	 * @return PaperRemark
	 */
	PaperRemark getPaperRemark(Integer paperId);
}
