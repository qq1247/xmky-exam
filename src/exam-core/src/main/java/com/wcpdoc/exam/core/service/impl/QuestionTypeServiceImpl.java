package com.wcpdoc.exam.core.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.wcpdoc.exam.base.service.UserService;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.dao.QuestionTypeDao;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.QuestionTypeExService;
import com.wcpdoc.exam.core.service.QuestionTypeService;
import com.wcpdoc.exam.core.util.StringUtil;
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
	public void addAndUpdate(String name, MultipartFile file) {
		//校验数据有效性
		if (!ValidateUtil.isValid(name)) {
			throw new MyException("参数错误：name");
		}
		
		// 添加试题分类
		QuestionType questionType = new QuestionType();
		questionType.setName(name);
		if (existName(questionType)) {
			throw new MyException("名称已存在！");
		}
		
		String[] allowTypes = { "jpg", "gif", "png" };
		MultipartFile[] files = {file};
		String doTempUpload = fileService.doTempUpload(files, allowTypes);
		
		//永久保存
		fileService.doUpload(Integer.valueOf(doTempUpload));

		questionType.setImg(Integer.valueOf(doTempUpload));
		questionType.setCreateUserId(getCurUser().getId());
		questionType.setCreateTime(new Date());
		questionType.setState(1);
		add(questionType);
	}

	@Override
	public void delAndUpdate(Integer id) {
		// 校验数据有效性
		if (id == 1) { //不包括根试题分类
			return;
		}
		List<QuestionType> questionTypeList = questionTypeDao.getList(id);
		if (ValidateUtil.isValid(questionTypeList)) {
			throw new MyException("请先删除子试题分类！");
		}
		
		// 删除试题分类
		QuestionType questionType = getEntity(id);
		questionTypeExService.delAndUpdate(questionType);
		
		questionType.setState(0);
		questionType.setUpdateTime(new Date());
		questionType.setUpdateUserId(getCurUser().getId());
		update(questionType);
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
	public void doAuth(Integer id, Integer[] readUserIds, Integer[] writeUserIds, boolean rwState) {		
		QuestionType entity = getEntity(id);
		if (!ValidateUtil.isValid(readUserIds)) {
			entity.setReadUserIds(null);
		} else {
			entity.setReadUserIds(String.format(",%s,", StringUtil.join(readUserIds)));
		}
		if (!ValidateUtil.isValid(writeUserIds)) {
			entity.setWriteUserIds(null);
		} else {
			entity.setWriteUserIds(String.format(",%s,", StringUtil.join(writeUserIds)));
		}
		if(rwState){
			entity.setRwState(1);
		}else{
			entity.setRwState(0);
		}
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		update(entity);
	}

	@Override
	public PageOut getUserListpage(PageIn pageIn) {
		return questionTypeDao.getUserListpage(pageIn);
	}
}
