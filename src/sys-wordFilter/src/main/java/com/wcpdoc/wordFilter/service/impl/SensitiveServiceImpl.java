package com.wcpdoc.wordFilter.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.startx.http.wordfilter.WordContext;
import com.startx.http.wordfilter.WordFilter;
import com.startx.http.wordfilter.WordType;
import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.wordFilter.dao.SensitiveDao;
import com.wcpdoc.wordFilter.entity.Sensitive;
import com.wcpdoc.wordFilter.service.SensitiveService;

/**
 * 公告服务层实现
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
@Service
public class SensitiveServiceImpl extends BaseServiceImp<Sensitive> implements SensitiveService {
	@Resource
	private SensitiveDao sensitiveDao;
    
	@Override
	@Resource(name = "sensitiveDaoImpl")
	public void setDao(BaseDao<Sensitive> dao) {
		super.dao = dao;
	}

    /**
     * 词库上下文环境
     */
    private final static WordContext context = new WordContext();
    private final static WordFilter filter = new WordFilter(context);
	

	@Override
	public void initialize() {
		Sensitive entity = sensitiveDao.getEntity(1);
		if (entity != null) {			
			init(entity);
		}
	}
    
	@Override
	public void updateAndUpdate(Sensitive sensitive) {
		if (!ValidateUtil.isValid(sensitive.getBlackList())) {
			sensitive.setBlackList(null);
		}
		if (!ValidateUtil.isValid(sensitive.getWhiteList())) {
			sensitive.setWhiteList(null);
		}
		
		Sensitive entity;
		if (sensitive.getId() == null) { //添加
			entity = new Sensitive();
			entity.setBlackList(sensitive.getBlackList());
			entity.setWhiteList(sensitive.getWhiteList());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(getCurUser().getId());
			sensitiveDao.add(entity);
		}else{			                 //修改
			entity = sensitiveDao.getEntity(sensitive.getId());
			entity.setBlackList(sensitive.getBlackList());
			entity.setWhiteList(sensitive.getWhiteList());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(getCurUser().getId());
			sensitiveDao.update(entity);
		}
		//初始化
		init(sensitive);
	}

	private void init(Sensitive sensitive) {
		// 初始化
		context.delWordMap();
		Set<String> set = new HashSet<>();
        if (ValidateUtil.isValid(sensitive.getBlackList())) {			
        	String[] blackSplit = sensitive.getBlackList().split("\n");
        	for(String s : blackSplit){        	
        		set.add(s);
        	}
        	context.addWord(set, WordType.BLACK);
		}
        set = new HashSet<>();
        if (ValidateUtil.isValid(sensitive.getWhiteList())) {
        	String[] whiteSplit = sensitive.getWhiteList().split("\n");
        	for(String s : whiteSplit){        	
        		set.add(s);
        	}
        	context.addWord(set, WordType.WHITE);			
		}
	}

	@Override
	public String replace(String content) {
		return filter.replace(content);
	}
}
