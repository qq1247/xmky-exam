package com.wcpdoc.exam.core.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.base.service.UserService;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.dao.QuestionTypeDao;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionTypeExService;
import com.wcpdoc.exam.core.service.QuestionTypeService;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.file.service.FileService;
/**
 * 试题分类服务层实现
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
@Service
public class QuestionTypeServiceImpl extends BaseServiceImp<QuestionType> implements QuestionTypeService {
	@Resource
	private QuestionTypeDao questionTypeDao;
	@Resource
	private QuestionService questionService;
	@Resource
	private QuestionTypeExService questionTypeExService;
	@Resource
	private UserService userService;
	@Resource
	private FileService fileService;
	

	@Override
	@Resource(name = "questionTypeDaoImpl")
	public void setDao(BaseDao<QuestionType> dao) {
		super.dao = dao;
	}

	@Override
	public void addAndUpdate(String name, Integer imgId) {
		//校验数据有效性
		if (!ValidateUtil.isValid(name)) {
			throw new MyException("参数错误：name");
		}
		
		// 添加试题分类
		QuestionType questionType = new QuestionType();
		questionType.setName(name);
		questionType.setImgId(imgId);
		/*if (existName(questionType)) {
			throw new MyException("名称已存在！");
		}*/
		questionType.setReadUserIds(","+getCurUser().getId()+",");
		questionType.setWriteUserIds(","+getCurUser().getId()+",");
		questionType.setCreateUserId(getCurUser().getId());
		questionType.setCreateTime(new Date());
		questionType.setState(1);
		questionTypeDao.add(questionType);
		
		//保存图片
		//fileService.doUpload(imgId);
	}

	@Override
	public void editAndUpdate(Integer id, String name, Integer imgId) {
		//校验数据有效性
		if(id == null) {
			throw new MyException("参数错误：id");
		}
		if(!ValidateUtil.isValid(name)) {
			throw new MyException("参数错误：name");
		}
		
		QuestionType entity = questionTypeDao.getEntity(id);
		entity.setName(name);
		/*if(questionTypeService.existName(entity)) {
			throw new MyException("名称已存在！");
		}*/
		entity.setImgId(imgId);
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		questionTypeDao.update(entity);
		
		//保存图片
		//fileService.doUpload(imgId);
	}
	
	@Override
	public void delAndUpdate(Integer id) {
		// 校验数据有效性
		if (id == 1) { //不包括根试题分类
			throw new MyException("此试题分类不能被删除！");
		}
		
		List<Question> list = questionService.getList(id);
		if (ValidateUtil.isValid(list)) {
			throw new MyException("请先删除试题分类下所有的试题！");
		}
		
		// 删除试题分类
		QuestionType questionType = getEntity(id);
		questionType.setState(0);
		questionType.setUpdateTime(new Date());
		questionType.setUpdateUserId(getCurUser().getId());
		questionTypeDao.update(questionType);
	}

	@Override
	public boolean existName(QuestionType questionType) {
		return questionTypeDao.existName(questionType.getName(), questionType.getId());
	}

	@Override
	public List<QuestionType> getList() {
		return questionTypeDao.getList();
	}

	@Override
	public void auth(Integer id, String readUserIds, String writeUserIds) {
		QuestionType entity = getEntity(id);
		if (ValidateUtil.isValid(readUserIds)) {
			entity.setReadUserIds(","+readUserIds+",");
		}
		if (ValidateUtil.isValid(writeUserIds)) {
			entity.setWriteUserIds(","+writeUserIds+",");
		}
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		update(entity);
	}

	@Override
	public PageOut authUserListpage(PageIn pageIn) {
		return questionTypeDao.authUserListpage(pageIn);
	}

	@Override
	public boolean hasWriteAuth(QuestionType questionType, Integer userId) {
		return questionType.getWriteUserIds().contains(String.format(",%s,", userId));
	}

	@Override
	public boolean hasReadAuth(QuestionType questionType, Integer userId) {
		return questionType.getReadUserIds().contains(String.format(",%s,", userId));
	}
}
