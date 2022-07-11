package com.wcpdoc.core.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperType;
import com.wcpdoc.exam.core.service.PaperService;
import com.wcpdoc.exam.core.service.PaperTypeExService;

/**
 * 试卷分类扩展服务层实现
 * 
 * v1.0 zhanghc 2017-05-25 16:34:59
 */
@Service
public class PaperTypeExServiceImpl extends BaseServiceImp<PaperType> implements PaperTypeExService {

	@Resource
	private PaperService paperService;

	@Override
	public void delAndUpdate(PaperType paperType) {
		List<Paper> paperList = paperService.getList(paperType.getId());
		if (ValidateUtil.isValid(paperList)) {
			throw new MyException("该试卷分类下有试卷，不允许删除");
		}
	}

	@Override
	public void auth(PaperType paperType, Integer[] readUserIds) {
		List<Paper> paperList = paperService.getList(paperType.getId());
		for (Paper paper : paperList) {// 同步更新分类下试卷权限
			paper.setReadUserIds(paperType.getReadUserIds());
			paper.setUpdateTime(new Date());
			paper.setUpdateUserId(getCurUser().getId());
			paperService.update(paper);
		}
	}

	@Override
	public void setDao(BaseDao<PaperType> dao) {
		
	}
}
