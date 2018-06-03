package com.wcpdoc.exam.home.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.exam.entity.QuestionType;
import com.wcpdoc.exam.exam.entity.QuestionTypeAuth;
import com.wcpdoc.exam.exam.service.QuestionTypeService;
import com.wcpdoc.exam.home.service.HomeQuestionService;
import com.wcpdoc.exam.sys.entity.Org;
import com.wcpdoc.exam.sys.entity.Post;
import com.wcpdoc.exam.sys.service.UserService;

/**
 * 试题服务层实现
 * 
 * v1.0 zhanghc 2018年6月3日上午10:34:21
 */
@Service
public class HomeQuestionServiceImpl extends BaseServiceImp<Object> implements HomeQuestionService {
	@Resource
	private QuestionTypeService questionTypeService;
	@Resource
	private UserService userService;

	@Override
	public void setDao(BaseDao<Object> dao) {
		
	}

	@Override
	public List<Map<String, Object>> getQuestionTypeTreeList(Integer userId) {
		//获取授权相关数据
		Org org = userService.getOrg(userId);
		List<Post> postList = userService.getPostList(userId);
		List<QuestionType> questionTypeList = questionTypeService.getList();
		List<QuestionTypeAuth> questionTypeAuthList = questionTypeService.getQuestionTypeAuthList();
		
		Map<Integer, QuestionTypeAuth> questionTypeAuthCache = new HashMap<Integer, QuestionTypeAuth>();
		for(QuestionTypeAuth questionTypeAuth : questionTypeAuthList){
			questionTypeAuthCache.put(questionTypeAuth.getId(), questionTypeAuth);
		}
		
		//封装成树形数据
		List<Map<String, Object>> questionTypeTreeList = new ArrayList<Map<String,Object>>();
		for(QuestionType questionType : questionTypeList){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("ID", questionType.getId());
			map.put("NAME", questionType.getName());
			map.put("PARENT_ID", questionType.getParentId());
			map.put("DISABLED", false);
			questionTypeTreeList.add(map);
			
			if(userId == 1){//如果是系统管理员
				map.put("DISABLED", true);
				continue;
			}
			
			QuestionTypeAuth questionTypeAuth = questionTypeAuthCache.get(questionType.getId());
			if(questionTypeAuth == null){//如果没有设置权限
				continue;
			}
			
			if(questionTypeAuth.getUserIds() != null 
					&& questionTypeAuth.getUserIds().contains(userId.toString())){//有用户权限
				map.put("DISABLED", true);
				continue;
			}
			if(questionTypeAuth.getOrgIds() != null 
					&& questionTypeAuth.getOrgIds().contains(org.getId().toString())){//有机构权限
				map.put("DISABLED", true);
				continue;
			}
			
			for(Post post : postList){
				if(questionTypeAuth.getPostIds() != null 
						&& questionTypeAuth.getPostIds().contains(post.getId().toString())){//有岗位权限
					map.put("DISABLED", true);
					break;
				}
			}
		}
		
		return questionTypeTreeList;
	}
}
