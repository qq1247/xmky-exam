package com.wcpdoc.exam.core.service;

import com.wcpdoc.exam.core.service.BaseService;

import java.util.Map;

import com.wcpdoc.exam.core.entity.Bulletin;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;

/**
 * 公告服务层接口
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
public interface BulletinService extends BaseService<Bulletin> {

	/**
	 * 添加公告
	 * 
	 * v1.0 chenyun 2021年9月9日下午1:27:47
	 * @param bulletin void
	 */
	void addAndUpdate(Bulletin bulletin);
	
	/**
	 * 修改公告
	 * 
	 * v1.0 chenyun 2021年9月9日下午1:27:47
	 * @param bulletin void
	 */
	void updateAndUpdate(Bulletin bulletin);
	
	/**
	 * 删除公告
	 * 
	 * v1.0 chenyun 2021-03-24 13:39:37
	 * 
	 * @param id
	 * void
	 */
	void delAndUpdate(Integer id);
	
	/**
	 * 添加权限
	 * 
	 * v1.0 chenyun 2021年3月24日下午3:18:08
	 * @param id
	 * @param readUserIds
	 * @param readOrgIds void
	 */
	void auth(Integer id, String readUserIds, String readOrgIds);
	
	/**
	 * 获取人员列表
	 * 
	 * v1.0 zhanghc 2018年5月30日下午6:28:19
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getUserListpage(PageIn pageIn);
	
	/**
	 * 获取人员列表
	 * 
	 * v1.0 zhanghc 2018年5月30日下午6:28:19
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getOrgListpage(PageIn pageIn);
	
	/**
	 * 获取公告
	 * 
	 * v1.0 chenyun 2021年9月8日下午5:19:57
	 * @param id
	 * @return Map<String,Object>
	 */
	Map<String, Object> get(Integer id);
}
