package com.wcpdoc.exam.core.service;

import java.util.List;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.MySxe;

/**
 * 我的作弊服务层接口
 * 
 * v1.0 zhanghc 2025年3月16日上午11:21:42
 */
public interface MySxeService extends BaseService<MySxe> {

	/**
	 * 我的作弊列表
	 * 
	 * v1.0 zhanghc 2025年3月16日上午11:41:56
	 * @param examId
	 * @param userId void
	 */
	List<MySxe> getList(Integer examId, Integer userId);

}
