package com.wcpdoc.exam.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperType;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.PaperService;
import com.wcpdoc.exam.core.service.PaperTypeExService;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 试卷分类扩展服务层实现
 * 
 * v1.0 zhanghc 2017-05-25 16:34:59
 */
@Service
public class PaperTypeExServiceImpl implements PaperTypeExService {

	@Resource
	private PaperService paperService;

	@Override
	public void delAndUpdate(PaperType paperType) {
		List<Paper> paperList = paperService.getList(paperType.getId());
		if (ValidateUtil.isValid(paperList)) {
			throw new MyException("该试卷分类下有试卷，不允许删除！");
		}
	}
}
