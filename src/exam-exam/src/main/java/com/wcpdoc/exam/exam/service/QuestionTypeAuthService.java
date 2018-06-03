package com.wcpdoc.exam.exam.service;

import com.wcpdoc.exam.core.service.BaseService;
import com.wcpdoc.exam.exam.entity.QuestionTypeAuth;

/**
 * 试题分类权限服务层接口
 * 
 * v1.0 zhanghc 2018年5月29日下午3:20:16
 */
@Deprecated //QuestionTypeService调用QuestionTypeAuthDao，外部调用QuestionTypeService
public interface QuestionTypeAuthService extends BaseService<QuestionTypeAuth>{

//	/**
//	 * 获取试题分类权限列表
//	 * 
//	 * v1.0 zhanghc 2018年6月3日下午2:00:44
//	 * @return List<QuestionTypeAuth>
//	 */
//	List<QuestionTypeAuth> getList();
	
}
