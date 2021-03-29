package com.wcpdoc.exam.core.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.base.service.OrgService;
import com.wcpdoc.exam.base.service.PostService;
import com.wcpdoc.exam.base.service.UserService;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.dao.PaperTypeDao;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperType;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.PaperService;
import com.wcpdoc.exam.core.service.PaperTypeExService;
import com.wcpdoc.exam.core.service.PaperTypeService;
import com.wcpdoc.exam.core.util.ValidateUtil;
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
	private PostService postService;
	@Resource
	private UserService userService;
	@Resource
	private PaperService paperService;

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
		
		if (existName(paperType)) {
			throw new MyException("名称已存在！");
		}
				
		// 添加试卷分类
		paperType.setCreateUserId(getCurUser().getId());
		paperType.setCreateTime(new Date());
		paperType.setUpdateUserId(getCurUser().getId());
		paperType.setUpdateTime(new Date());
		paperType.setState(1);
		add(paperType);
	}

	@Override
	public void delAndUpdate(Integer id) {
		// 校验数据有效性
		if (id == 1) { //不包括根试卷分类
			return;
		}
		List<Paper> paperList = paperService.getList(id);
		if (ValidateUtil.isValid(paperList)) {
			throw new MyException("请先删除试卷分类下所有的试卷！");
		}
		
		// 删除试卷分类
		PaperType paperType = getEntity(id);
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
	public void doAuth(Integer id, String readUserIds, String writeUserIds, boolean rwState) {		
		PaperType entity = getEntity(id);
		if (!ValidateUtil.isValid(readUserIds)) {
			entity.setReadUserIds(null);
		} else {
			entity.setReadUserIds(readUserIds);
		}
		if (!ValidateUtil.isValid(writeUserIds)) {
			entity.setWriteUserIds(null);
		} else {
			entity.setWriteUserIds(writeUserIds);
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
}
