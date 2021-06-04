package com.wcpdoc.exam.api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.base.entity.User;
import com.wcpdoc.exam.base.service.UserService;
import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionTypeService;
import com.wcpdoc.exam.core.util.DateUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 试题分类控制层
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
@Controller
@RequestMapping("/api/questionType")
public class ApiQuestionTypeController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiQuestionTypeController.class);
	
	@Resource
	private QuestionTypeService questionTypeService;
	@Resource
	private QuestionService questionService;
	@Resource
	private UserService userService;
	
	/**
	 * 试题分类列表 
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/list")
	@ResponseBody
	@RequiresRoles("OP")
	public PageResult list(PageIn pageIn, String name) {
		try {
			if (ValidateUtil.isValid(name)) {
				pageIn.setTwo(name);
			}
			PageOut listpage = questionTypeService.getListpage(pageIn);
			List<Map<String, Object>> rows = listpage.getRows();
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			for(Map<String, Object> mapList : rows){
				if ("0".equals(mapList.get("CREATE_USER_ID").toString())) {
					continue;
				}
				
				User user = userService.getEntity(Integer.parseInt(mapList.get("CREATE_USER_ID").toString()));
				mapList.put("CREATE_USER_NAME", user.getName());
				list.add(mapList);
			}
			listpage.setRows(list);
			return PageResultEx.ok().data(listpage);
		} catch (Exception e) {
			log.error("试题分类列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 添加试题分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/add")
	@ResponseBody
	@RequiresRoles("OP")
	public PageResult add(String name, Integer imgId) {
		try {
			questionTypeService.addAndUpdate(name, imgId);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("添加试题分类错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("添加试题分类错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 获取试题分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/get")
	@ResponseBody
	@RequiresRoles("OP")
	public PageResult get(Integer id) {
		try {
			QuestionType entity = questionTypeService.getEntity(id);
			return PageResultEx.ok()
					.addAttr("id", entity.getId())
					.addAttr("name", entity.getName())
					.addAttr("imgId", entity.getImgId())
					.addAttr("createUserId", entity.getCreateUserId())
					.addAttr("createTime", DateUtil.formatDateTime(entity.getCreateTime()))
					.addAttr("rwState", entity.getRwState())
					.addAttr("readUserIds", entity.getReadUserIds())
					.addAttr("writeUserIds", entity.getWriteUserIds());
		} catch (MyException e) {
			log.error("获取试题分类错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("获取试题分类错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 修改试题分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/edit")
	@ResponseBody
	@RequiresRoles("OP")
	public PageResult edit(Integer id, String name, Integer imgId) {
		try {
			questionTypeService.editAndUpdate(id, name, imgId);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("修改试题分类错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("修改试题分类错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 删除试题分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/del")
	@ResponseBody
	@RequiresRoles("OP")
	public PageResult del(Integer id) {
		try {
			questionTypeService.delAndUpdate(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("删除试题分类错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("删除试题分类错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 获取人员列表 
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/authUserList")
	@ResponseBody
	@RequiresRoles("OP")
	public PageResult userList(PageIn pageIn, String name, Integer id) {  //Two - name (userName)  Ten - id
		try {
			if(ValidateUtil.isValid(name)){
				pageIn.setTwo(name);
			}
			if(id != null){
				pageIn.setTen(id.toString());
			}
			return PageResultEx.ok().data(questionTypeService.getUserListpage(pageIn));
		} catch (Exception e) {
			log.error("权限用户列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 添加权限
	 * 
	 * v1.0 chenyun 2021年3月2日上午10:18:26
	 * @param id	
	 * @param readUserIds
	 * @param writeUserIds
	 * @param rwState
	 * @return PageResult
	 */
	@RequestMapping("/auth")
	@ResponseBody
	@RequiresRoles("OP")
	public PageResult auth(Integer id, String readUserIds, String writeUserIds, boolean rwState) {
		try {
			questionTypeService.doAuth(id, readUserIds, writeUserIds, rwState);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("添加权限用户错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("添加权限用户错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 试题分类合并
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @param id
	 * @return pageOut
	 */
	@RequestMapping("/move")
	@ResponseBody
	@RequiresRoles("OP")
	public PageResult move(Integer id, Integer sourceId, Integer targetId) {
		try {
			questionService.move(id, sourceId, targetId);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("合并试题错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		}  catch (Exception e) {
			log.error("合并试题错误：", e);
			return PageResult.err();
		}
	}
}
