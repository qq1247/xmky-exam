package com.wcpdoc.exam.core.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.base.service.OrgService;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.PaperTypeDao;
import com.wcpdoc.exam.core.entity.Paper;
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
public class PaperTypeServiceImpl extends BaseServiceImp<PaperType> implements PaperTypeService{
	@Resource
	private PaperTypeDao paperTypeDao;
	@Resource
	private PaperTypeExService paperTypeExService;
	@Resource
	private OrgService orgService;
	@Resource
	private UserService userService;
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
		
		/*if (existName(paperType)) {
			throw new MyException("名称已存在！");
		}*/
				
		// 添加试卷分类
		paperType.setReadUserIds(","+getCurUser().getId()+",");
		paperType.setWriteUserIds(","+getCurUser().getId()+",");
		paperType.setCreateUserId(getCurUser().getId());
		paperType.setCreateTime(new Date());
		paperType.setUpdateUserId(getCurUser().getId());
		paperType.setUpdateTime(new Date());
		paperType.setState(1);
		add(paperType);
		
		//保存图片
		//fileService.doUpload(paperType.getImgId());
	}

	@Override
	public void editAndUpdate(PaperType paperType) {
		//校验数据有效性
		if(!ValidateUtil.isValid(paperType.getName())) {
			throw new MyException("参数错误：name");
		}
		/*if(existName(paperType)) {
			throw new MyException("名称已存在！");
		}*/
		PaperType entity = paperTypeDao.getEntity(paperType.getId());
		if (entity.getCreateUserId() != getCurUser().getId()) {
			throw new MyException("权限不足！");
		}
		
		//修改试卷分类
		entity.setName(paperType.getName());
		entity.setImgFileId(paperType.getImgFileId());
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		paperTypeDao.update(entity);
	}
	
	@Override
	public void delAndUpdate(Integer id) {
		// 校验数据有效性
		if (id == 1) { //不包括根试卷分类
			throw new MyException("此试卷分类不能被删除！");
		}
		List<Paper> paperList = paperService.getList(id);
		if (ValidateUtil.isValid(paperList)) {
			throw new MyException("请先删除试卷分类下所有的试卷！");
		}
		PaperType paperType = getEntity(id);
		if (paperType.getCreateUserId() != getCurUser().getId()) {
			throw new MyException("权限不足！");
		}
		
		// 删除试卷分类
		paperType.setState(0);
		paperType.setUpdateTime(new Date());
		paperType.setUpdateUserId(getCurUser().getId());
		update(paperType);
	}

	@Override
	public boolean existName(PaperType paperType) {
		return paperTypeDao.existName(paperType.getName(), paperType.getId());
	}

	@Override
	public List<PaperType> getList() {
		return paperTypeDao.getList();
	}

	@Override
	public void doAuth(Integer id, String readUserIds, String writeUserIds) {		
		PaperType entity = getEntity(id);
		if(entity.getCreateUserId().intValue() != getCurUser().getId()){
			throw new MyException("非法操作！");
		}
		if (ValidateUtil.isValid(readUserIds)) {
			entity.setReadUserIds(","+readUserIds+",");
		}
		if (ValidateUtil.isValid(writeUserIds)) {
			entity.setWriteUserIds(","+writeUserIds+",");
		}
		if(!entity.getReadUserIds().contains(","+getCurUser().getId()+",")){
			throw new MyException("创建者不能被删除！");
		}
		if(!entity.getWriteUserIds().contains(","+getCurUser().getId()+",")){
			throw new MyException("创建者不能被删除！");
		}
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		update(entity);
	}
	
	@Override
	public PageOut authUserListpage(PageIn pageIn) {
		return paperTypeDao.authUserListpage(pageIn);
	}
}
