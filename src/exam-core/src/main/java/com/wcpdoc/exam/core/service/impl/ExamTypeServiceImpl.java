package com.wcpdoc.exam.core.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.ExamTypeDao;
import com.wcpdoc.exam.core.entity.ExamType;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.ExamTypeExService;
import com.wcpdoc.exam.core.service.ExamTypeService;
import com.wcpdoc.file.service.FileService;
/**
 * 考试分类服务层实现
 * 
 * v1.0 zhanghc 2017-06-28 21:34:41
 */
@Service
public class ExamTypeServiceImpl extends BaseServiceImp<ExamType> implements ExamTypeService {
	@Resource
	private ExamTypeDao examTypeDao;
	@Resource
	private ExamTypeExService examTypeExService;
	@Resource
	private FileService fileService;
	@Resource
	private ExamService examService;
	
	
	@Override
	@Resource(name = "examTypeDaoImpl")
	public void setDao(BaseDao<ExamType> dao) {
		super.dao = dao;
	}

	@Override
	public Integer addAndUpdate(ExamType examType) {
		//校验数据有效性
		if (!ValidateUtil.isValid(examType.getName())) {
			throw new MyException("参数错误：name");
		}
		//if (existName(questionType)) {
		//	throw new MyException("名称已存在");
		//} // 不同的子管理员添加可以重复
		
		// 添加试题分类
		examType.setCreateUserId(getCurUser().getId());
		examType.setCreateTime(new Date());
		examType.setUpdateTime(new Date());
		examType.setUpdateUserId(getCurUser().getId());
		examType.setState(1);
		add(examType);
		
		//保存图片
		//fileService.doUpload(imgId);
		return examType.getId(); //快速创建考试需要用id查找信息
	}

	@Override
	public void editAndUpdate(ExamType examType) {
		//校验数据有效性
		if(!ValidateUtil.isValid(examType.getName())) {
			throw new MyException("参数错误：name");
		}
		ExamType entity = getEntity(examType.getId());
		if (entity.getCreateUserId() != getCurUser().getId()) {
			throw new MyException("无操作权限");
		}
		
		//保存图片
		//if (entity.getImgFileId().intValue() != questionType.getImgFileId().intValue()) {
		//	fileService.doUpload(imgId);
		//}
		
		// 保存试题分类
		entity.setName(examType.getName());
		entity.setImgFileId(examType.getImgFileId());
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		update(entity);
	}
	
	@Override
	public void delAndUpdate(Integer id) {
		// 校验数据有效性
		ExamType examType = getEntity(id);
		if (examType.getCreateUserId() != getCurUser().getId()) {
			throw new MyException("无操作权限");
		}
		
		// 删除试题分类
		examType.setState(0);
		examType.setUpdateTime(new Date());
		examType.setUpdateUserId(getCurUser().getId());
		update(examType);
		
		// 删除试题分类扩展
		examTypeExService.delAndUpdate(examType);
	}
}
