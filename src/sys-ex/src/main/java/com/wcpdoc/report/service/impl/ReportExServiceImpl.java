package com.wcpdoc.report.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.cache.OnlineUserCache;
import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.entity.OnlineUser;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.report.service.ReportExService;


/**
 * 定时任务扩展服务层实现
 * 
 * v1.0 zhanghc 2016-6-11下午8:57:40
 */
@Service
public class ReportExServiceImpl extends BaseServiceImp<Object> implements ReportExService {

	@Override
	public void setDao(BaseDao<Object> dao) {}

	@Override
	public Integer OnlineNum() {
		List<Integer> keys = OnlineUserCache.getKeys();
		Integer num = 0;
		
		if (keys == null || keys.size() < 1) {
			return num;
		}
		for(Integer key : keys){
			OnlineUser onlineUser = OnlineUserCache.get(key);
			if (onlineUser.getState()) {
				num ++;
			}
		}
		return num;
	}
}
