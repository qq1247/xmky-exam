package com.wcpdoc.exam.core.service;

import com.wcpdoc.exam.core.entity.PaperType;

/**
 * 试卷分类扩展服务层接口
 * 
 * v1.0 zhanghc 2017-05-25 16:34:59
 */
public interface PaperTypeExService{

	/**
	 * 删除关联引用
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @param paperType 
	 * void
	 */
	void delAndUpdate(PaperType paperType);
	
}

