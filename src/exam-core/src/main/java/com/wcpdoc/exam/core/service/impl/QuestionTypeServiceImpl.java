package com.wcpdoc.exam.core.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.base.util.CurLoginUserUtil;
import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.dao.QuestionTypeDao;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.service.QuestionTypeExService;
import com.wcpdoc.exam.core.service.QuestionTypeService;
/**
 * 题库服务层实现
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
@Service
public class QuestionTypeServiceImpl extends BaseServiceImp<QuestionType> implements QuestionTypeService {
	@Resource
	private QuestionTypeDao questionTypeDao;
	@Resource
	private QuestionTypeExService questionTypeExService;

	@Override
	@Resource(name = "questionTypeDaoImpl")
	public void setDao(BaseDao<QuestionType> dao) {
		super.dao = dao;
	}
	
	@Override
	public void delEx(Integer id) {
		// 数据校验
		QuestionType entity = getEntity(id);
		if (!(CurLoginUserUtil.isSelf(entity.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {
				throw new MyException("无操作权限");
		}
		// 删除题库
		//entity.setUpdateTime(new Date());// 物理删除，不需要再记录
		//entity.setUpdateUserId(getCurUser().getId());
		del(entity.getId());
		
		// 删除题库扩展
		questionTypeExService.delEx(entity);
	}

	@Override
	public void move(Integer sourceId, Integer targetId) {
		questionTypeExService.move(sourceId, targetId);
	}

	@Override
	public void clear(Integer id) {
		questionTypeExService.clear(id);
	}
}
