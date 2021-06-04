package com.wcpdoc.exam.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.dao.PaperRemarkDao;
import com.wcpdoc.exam.core.entity.PaperRemark;
import com.wcpdoc.exam.core.service.PaperRemarkService;

/**
 * 试卷评语服务层实现
 * 
 * v1.0 chenyun 2021-03-10 13:48:34
 */
@Service
public class PaperRemarkServiceImpl extends BaseServiceImp<PaperRemark> implements PaperRemarkService {
	@Resource
	private PaperRemarkDao paperRemarkDao;

	@Override
	@Resource(name = "paperRemarkDaoImpl")
	public void setDao(BaseDao<PaperRemark> dao) {
		super.dao = dao;
	}
	
	@Override
	public void delAndUpdate(Integer id) {
		// 校验数据有效性
		PaperRemark entity = getEntity(id);
		del(entity);
	}

	@Override
	public List<PaperRemark> getPaperRemarkList(Integer paperId) {
		return paperRemarkDao.getPaperRemarkList(paperId);
	}

	@Override
	public void removePaperRemark(Integer paperId) {
		List<PaperRemark> paperRemark = paperRemarkDao.getPaperRemarkList(paperId);
		if(paperRemark == null || paperRemark.size() == 0 ){
			return;
		}
		for(PaperRemark  paperRemarkId :  paperRemark){
			del(paperRemarkId.getId());
		}
	}
}
