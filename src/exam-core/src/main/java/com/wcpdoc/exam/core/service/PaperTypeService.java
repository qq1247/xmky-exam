package com.wcpdoc.exam.core.service;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.PaperType;
/**
 * 试卷分类服务层接口
 * 
 * v1.0 zhanghc 2017-05-25 16:34:59
 */
public interface PaperTypeService extends BaseService<PaperType> {
	
	/**
	 * 添加试卷分类
	 * 
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @param paperType
	 * void
	 */
	void addAndUpdate(PaperType paperType);

	/**
	 * 修改试卷分类
	 * 
	 * v1.0 cy 2021年6月11日下午2:43:48
	 * @param paperType void
	 */
	void editAndUpdate(PaperType paperType);
	
	/**
	 * 删除试卷分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @param id
	 * void
	 */
	void delAndUpdate(Integer id);

	/**
	 * 授权
	 * 
	 * v1.0 zhanghc 2020年9月8日上午10:06:53
	 * @param id
	 * @param readUserIds
	 * void
	 */
	void auth(Integer id, Integer[] readUserIds);

	/**
	 * 是否有读权限
	 * 
	 * v1.0 zhanghc 2021年11月5日下午5:50:58
	 * @param paperType
	 * @return boolean
	 */
	boolean hasReadAuth(PaperType paperType);

	/**
	 * 是否有写权限（只能操作自己创建的分类）
	 * 
	 * v1.0 zhanghc 2022年6月17日上午11:19:58
	 * @param paperType
	 * @return boolean
	 */
	boolean hasWriteAuth(PaperType paperType);
}
