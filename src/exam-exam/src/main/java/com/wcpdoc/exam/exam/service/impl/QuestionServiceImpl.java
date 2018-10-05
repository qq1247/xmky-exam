package com.wcpdoc.exam.exam.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.exam.dao.QuestionDao;
import com.wcpdoc.exam.exam.entity.Question;
import com.wcpdoc.exam.exam.entity.QuestionType;
import com.wcpdoc.exam.exam.service.QuestionService;
import com.wcpdoc.exam.exam.service.QuestionTypeService;
import com.wcpdoc.exam.sys.entity.Org;
import com.wcpdoc.exam.sys.entity.Post;
import com.wcpdoc.exam.sys.service.UserService;

/**
 * 试题服务层实现
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
@Service
public class QuestionServiceImpl extends BaseServiceImp<Question> implements QuestionService {
	@Resource
	private QuestionDao questionDao;
	@Resource
	private QuestionTypeService questionTypeService;
	@Resource
	private UserService userService;

	@Override
	@Resource(name = "questionDaoImpl")
	public void setDao(BaseDao<Question> dao) {
		super.dao = dao;
	}

	@Override
	public List<Map<String, Object>> getQuestionTypeTreeList(Integer  userId) {
		Org org = userService.getOrg(userId);
		List<Post> postList = userService.getPostList(userId);
		List<QuestionType> questionTypeList = questionTypeService.getList();
		List<Map<String, Object>> questionTypeTreeList = new ArrayList<Map<String,Object>>();
		
		for(QuestionType questionType : questionTypeList){
			if(userId != 1){//如果不是系统管理员
				if(!(questionType.getUserIds() != null 
						&& questionType.getUserIds().contains(userId.toString()))){//没有用户权限
					continue;
				}
				if(!(questionType.getOrgIds() != null 
						&& questionType.getOrgIds().contains(org.getId().toString()))){//没有机构权限
					continue;
				}
				
				boolean hasPostAuth = false;
				for(Post post : postList){
					if(questionType.getPostIds() != null 
							&& questionType.getPostIds().contains(post.getId().toString())){//没有岗位权限
						hasPostAuth = true;
						break;
					}
				}
				
				if(!hasPostAuth){
					continue;
				}
			}
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("ID", questionType.getId());
			map.put("NAME", questionType.getName());
			map.put("PARENT_ID", questionType.getParentId());
			//map.put("DISABLED", true);
			//map.put("EXPANDED", true);
			questionTypeTreeList.add(map);
		}
		
		return questionTypeTreeList;
	}

	@Override
	public QuestionType getQuestionType(Integer id) {
		return questionDao.getQuestionType(id);
	}

	@Override
	public void doQuestionTypeUpdate(Integer[] ids, Integer questionTypeId) {
		//校验数据有效性
		if(!ValidateUtil.isValid(ids)){
			throw new RuntimeException("无法获取参数：ids");
		}
		if(questionTypeId == null){
			throw new RuntimeException("无法获取参数：questionTypeId");
		}
		
		//完成设置试题
		for(Integer id : ids){
			Question question = questionDao.getEntity(id);
			if(question.getQuestionTypeId() == questionTypeId){
				continue;
			}
			
			question.setQuestionTypeId(questionTypeId);
			questionDao.update(question);
		}
	}

	@Override
	public List<Question> getList(Integer questionTypeId) {
		return questionDao.getList(questionTypeId);
	}

	@Override
	public void delAndUpdate(Integer[] ids) {
		//校验数据有效性
		if(!ValidateUtil.isValid(ids)){
			throw new RuntimeException("无法获取参数：ids");
		}
		
		//删除试题
		for(Integer id : ids){
			Question question = getEntity(id);
			question.setState(0);
			update(question);
		}
	}
}
