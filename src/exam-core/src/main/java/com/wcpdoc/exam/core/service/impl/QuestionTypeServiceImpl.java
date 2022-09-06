package com.wcpdoc.exam.core.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.QuestionTypeDao;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.service.QuestionTypeExService;
import com.wcpdoc.exam.core.service.QuestionTypeService;
import com.wcpdoc.file.service.FileService;
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
	@Resource
	private FileService fileService;
	

	@Override
	@Resource(name = "questionTypeDaoImpl")
	public void setDao(BaseDao<QuestionType> dao) {
		super.dao = dao;
	}

	@Override
	public void addAndUpdate(QuestionType questionType) {
		//校验数据有效性
		if (!ValidateUtil.isValid(questionType.getName())) {
			throw new MyException("参数错误：name");
		}
		//if (existName(questionType)) {
		//	throw new MyException("名称已存在");
		//} // 不同的子管理员添加可以重复
		
		// 添加试题分类
		questionType.setUpdateTime(new Date());
		questionType.setUpdateUserId(getCurUser().getId());
		add(questionType);
		
		//保存图片
		//fileService.doUpload(imgId);
	}

	@Override
	public void editAndUpdate(QuestionType questionType) {
		//校验数据有效性
		if(!ValidateUtil.isValid(questionType.getName())) {
			throw new MyException("参数错误：name");
		}
		QuestionType entity = getEntity(questionType.getId());
		
		//保存图片
		//if (entity.getImgFileId().intValue() != questionType.getImgFileId().intValue()) {
		//	fileService.doUpload(imgId);
		//}
		
		// 保存试题分类
		entity.setName(questionType.getName());
		entity.setImgFileId(questionType.getImgFileId());
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		update(entity);
	}
	
	@Override
	public void delAndUpdate(Integer id) {
		// 删除试题分类
		QuestionType questionType = getEntity(id);
		del(questionType.getId());
		
		// 删除试题分类扩展
		questionTypeExService.delAndUpdate(questionType);
	}

	@Override
	public void auth(Integer id, Integer[] writeUserIds) {
		// 校验数据有效性
		QuestionType entity = getEntity(id);
		
		// 更新权限
//		if (!ValidateUtil.isValid(writeUserIds)) {
//			entity.setWriteUserIds(String.format(",%s,", entity.getCreateUserId()));// 如果没有，默认就是创建人（查询的时候方便）
//		} else {
//			entity.setWriteUserIds(String.format(",%s,", StringUtil.join(writeUserIds)));
//			if (!entity.getWriteUserIds().contains(String.format(",%s,", entity.getCreateUserId()))) {// 如果页面没有选择创建人，追加创建人
//				entity.setWriteUserIds(String.format("%s%s,", entity.getWriteUserIds(), entity.getCreateUserId()));
//			}
//		}
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		update(entity);
		
		// 更新权限扩展
		questionTypeExService.auth(entity);
	}
}
