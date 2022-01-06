package com.wcpdoc.exam.api.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionTypeService;

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
	 * 
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @return pageOut
	 */
	@RequestMapping("/listpage")
	@ResponseBody
	public PageResult listpage() {
		try {
			PageIn pageIn = new PageIn(request);
			pageIn.addAttr("curUserId", getCurUser().getId());
			PageOut pageOut = questionTypeService.getListpage(pageIn);
			for (Map<String, Object> result : pageOut.getList()) {
				if (!ValidateUtil.isValid((String)result.get("writeUserIds"))) {
					result.put("writeUserIds", new Integer[0]);
					result.put("writeUserNames", new String[0]);
					continue;
				}
				
				Set<Integer> writeUserIdSet = new HashSet<>();
				for (String id : ((String)result.get("writeUserIds")).split(",")) {
					if (!ValidateUtil.isValid(id)) {// ,2,3,23, 有空值
						continue;
					}
					writeUserIdSet.add(Integer.parseInt(id));
				}
				
				Integer[] writeUserIds = writeUserIdSet.toArray(new Integer[0]);
				List<User> writeUserList = userService.getList(writeUserIds);
				String[] writeUserNames = new String[writeUserList.size()];
				for (int i = 0; i < writeUserList.size(); i++) {
					writeUserNames[i] = writeUserList.get(i).getName();
				}
				result.put("writeUserIds", writeUserIds);
				result.put("writeUserNames", writeUserNames);
			}
			return PageResultEx.ok().data(pageOut);
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
	public PageResult add(QuestionType questionType) {
		try {
			questionTypeService.addAndUpdate(questionType);
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
	 * 修改试题分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/edit")
	@ResponseBody
	public PageResult edit(QuestionType questionType) {
		try {
			questionTypeService.editAndUpdate(questionType);
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
	 * 获取试题分类
	 * 
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @return pageOut
	 */
	@RequestMapping("/get")
	@ResponseBody
	public PageResult get(Integer id) {
		try {
			QuestionType entity = questionTypeService.getEntity(id);
			
			Set<Integer> writeUserIdSet = new HashSet<>();
			for (String writeUserId : entity.getWriteUserIds().split(",")) {
				if (!ValidateUtil.isValid(writeUserId)) {// ,2,3,23, 有空值
					continue;
				}
				writeUserIdSet.add(Integer.parseInt(writeUserId));
			}
			
			Integer[] writeUserIds = writeUserIdSet.toArray(new Integer[0]);
			List<User> writeUserList = userService.getList(writeUserIds);
			String[] writeUserNames = new String[writeUserList.size()];
			for (int i = 0; i < writeUserList.size(); i++) {
				writeUserNames[i] = writeUserList.get(i).getName();
			}
			
			return PageResultEx.ok()
					.addAttr("id", entity.getId())
					.addAttr("name", entity.getName())
					.addAttr("createUserId", entity.getCreateUserId())
					.addAttr("createUserName", userService.getEntity(entity.getCreateUserId()).getName())
					.addAttr("createTime", DateUtil.formatDateTime(entity.getCreateTime()))
					.addAttr("writeUserIds", writeUserIds)
					.addAttr("writeUserNames", writeUserNames);
		} catch (MyException e) {
			log.error("获取试题分类错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("获取试题分类错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 添加组用户
	 * 
	 * v1.0 chenyun 2021年3月2日上午10:18:26
	 * @param id	
	 * @param writeUserIds
	 * @return PageResult
	 */
	@RequestMapping("/auth")
	@ResponseBody
	public PageResult auth(Integer id, Integer[] writeUserIds) {
		try {
			questionTypeService.auth(id, writeUserIds);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("添加组用户错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("添加组用户错误：", e);
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
	public PageResult move(Integer sourceId, Integer targetId) {
		try {
			questionService.move(sourceId, targetId);
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
