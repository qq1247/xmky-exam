package com.wcpdoc.exam.core.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.PaperTypeDao;
import com.wcpdoc.exam.core.entity.PaperType;
import com.wcpdoc.exam.core.service.PaperService;
import com.wcpdoc.exam.core.service.PaperTypeExService;
import com.wcpdoc.exam.core.service.PaperTypeService;
import com.wcpdoc.file.service.FileService;
/**
 * 试卷分类服务层实现
 * 
 * v1.0 zhanghc 2017-05-25 16:34:59
 */
@Service
public class PaperTypeServiceImpl extends BaseServiceImp<PaperType> implements PaperTypeService {
	@Resource
	private PaperTypeDao paperTypeDao;
	@Resource
	private PaperTypeExService paperTypeExService;
	@Resource
	private PaperService paperService;
	@Resource
	private FileService fileService;

	@Override
	@Resource(name = "paperTypeDaoImpl")
	public void setDao(BaseDao<PaperType> dao) {
		super.dao = dao;
	}

	@Override
	public void addAndUpdate(PaperType paperType) {
		//校验数据有效性
		if (!ValidateUtil.isValid(paperType.getName())) {
			throw new MyException("参数错误：name");
		}
		//if (existName(questionType)) {
		//	throw new MyException("名称已存在");
		//} // 不同的子管理员添加可以重复
		
		// 添加试题分类
		paperType.setReadUserIds(String.format(",%s,", getCurUser().getId()));
		paperType.setCreateUserId(getCurUser().getId());
		paperType.setCreateTime(new Date());
		paperType.setUpdateTime(new Date());
		paperType.setUpdateUserId(getCurUser().getId());
		paperType.setState(1);
		add(paperType);
		
		//保存图片
		//fileService.doUpload(imgId);
	}

	@Override
	public void editAndUpdate(PaperType paperType) {
		//校验数据有效性
		if(!ValidateUtil.isValid(paperType.getName())) {
			throw new MyException("参数错误：name");
		}
		PaperType entity = getEntity(paperType.getId());
		if (entity.getCreateUserId() != getCurUser().getId()) {
			throw new MyException("无操作权限");
		}
		
		//保存图片
		//if (entity.getImgFileId().intValue() != questionType.getImgFileId().intValue()) {
		//	fileService.doUpload(imgId);
		//}
		
		// 保存试题分类
		entity.setName(paperType.getName());
		entity.setImgFileId(paperType.getImgFileId());
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		update(entity);
	}
	
	@Override
	public void delAndUpdate(Integer id) {
		// 校验数据有效性
		PaperType paperTypeType = getEntity(id);
		if (paperTypeType.getCreateUserId() != getCurUser().getId()) {
			throw new MyException("无操作权限");
		}
		
		// 删除试题分类
		paperTypeType.setState(0);
		paperTypeType.setUpdateTime(new Date());
		paperTypeType.setUpdateUserId(getCurUser().getId());
		update(paperTypeType);
		
		// 删除试题分类扩展
		paperTypeExService.delAndUpdate(paperTypeType);
	}

	@Override
	public void auth(Integer id, Integer[] readUserIds) {
		// 校验数据有效性
		PaperType paperType = getEntity(id);
		if(paperType.getCreateUserId().intValue() != getCurUser().getId().intValue()){
			throw new MyException("无操作权限");
		}
		
		// 更新权限
		if (!ValidateUtil.isValid(readUserIds)) {
			paperType.setReadUserIds(String.format(",%s,", paperType.getCreateUserId()));// 如果没有，默认就是创建人（查询的时候方便）
		} else {
			paperType.setReadUserIds(String.format(",%s,", StringUtil.join(readUserIds)));
			if (!paperType.getReadUserIds().contains(String.format(",%s,", paperType.getCreateUserId()))) {// 如果页面没有选择创建人，添加创建人
				paperType.setReadUserIds(String.format("%s,%s", paperType.getReadUserIds(), paperType.getCreateUserId()));
			}
		}
		paperType.setUpdateTime(new Date());
		paperType.setUpdateUserId(getCurUser().getId());
		update(paperType);
		
		// 更新权限扩展
		paperTypeExService.auth(paperType, readUserIds);
	}

	@Override
	public boolean hasReadAuth(PaperType paperType) {
		return paperType.getReadUserIds().contains(String.format(",%s,", getCurUser().getId()));
	}

	@Override
	public boolean hasWriteAuth(PaperType paperType) {
		return paperType.getCreateUserId().intValue() == getCurUser().getId().intValue();
	}
}
