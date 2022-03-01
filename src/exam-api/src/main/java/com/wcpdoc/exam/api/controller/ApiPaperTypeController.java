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
import com.wcpdoc.exam.core.entity.PaperType;
import com.wcpdoc.exam.core.service.PaperService;
import com.wcpdoc.exam.core.service.PaperTypeService;

/**
 * 试卷分类控制层
 * 
 * v1.0 zhanghc 2017-05-25 16:34:59
 */
@Controller
@RequestMapping("/api/paperType")
public class ApiPaperTypeController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiPaperTypeController.class);
	
	@Resource
	private PaperTypeService paperTypeService;
	@Resource
	private PaperService paperService;
	@Resource
	private UserService userService;
	
	/**
	 * 试卷分类列表 
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/listpage")
	@ResponseBody
	public PageResult listpage() {
		try {
			PageIn pageIn = new PageIn(request);
			pageIn.addAttr("curUserId", getCurUser().getId());
			PageOut pageOut = paperTypeService.getListpage(pageIn);
			for (Map<String, Object> result : pageOut.getList()) {
				if (!ValidateUtil.isValid((String)result.get("readUserIds"))) {
					result.put("readUserIds", new Integer[0]);
					result.put("readUserNames", new String[0]);
					continue;
				}
				
				Set<Integer> readUserIdSet = new HashSet<>();
				for (String id : ((String)result.get("readUserIds")).split(",")) {
					if (!ValidateUtil.isValid(id)) {// ,2,3,23, 有空值
						continue;
					}
					readUserIdSet.add(Integer.parseInt(id));
				}
				
				Integer[] readUserIds = readUserIdSet.toArray(new Integer[0]);
				List<User> readUserList = userService.getList(readUserIds);
				String[] readUserNames = new String[readUserList.size()];
				for (int i = 0; i < readUserList.size(); i++) {
					readUserNames[i] = readUserList.get(i).getName();
				}
				result.put("readUserIds", readUserIds);
				result.put("readUserNames", readUserNames);
			}
			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("试卷分类列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 添加试卷分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/add")
	@ResponseBody
	public PageResult add(PaperType paperType) {
		try {
			return PageResultEx.ok().data(paperTypeService.addAndUpdate(paperType));
		} catch (MyException e) {
			log.error("添加试卷分类错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("添加试卷分类错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 修改试卷分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/edit")
	@ResponseBody
	public PageResult edit(PaperType paperType) {
		try {
			paperTypeService.editAndUpdate(paperType);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("修改试卷分类错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("修改试卷分类错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 删除试卷分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/del")
	@ResponseBody
	public PageResult del(Integer id) {
		try {
			paperTypeService.delAndUpdate(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("删除试卷分类错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("删除试卷分类错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 获取试卷分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/get")
	@ResponseBody
	public PageResult get(Integer id) {
		try {
			PaperType entity = paperTypeService.getEntity(id);
			
			Set<Integer> readUserIdSet = new HashSet<>();
			for (String readUserId : entity.getReadUserIds().split(",")) {
				if (!ValidateUtil.isValid(readUserId)) {// ,2,3,23, 有空值
					continue;
				}
				readUserIdSet.add(Integer.parseInt(readUserId));
			}
			
			Integer[] readUserIds = readUserIdSet.toArray(new Integer[0]);
			List<User> readUserList = userService.getList(readUserIds);
			String[] readUserNames = new String[readUserList.size()];
			for (int i = 0; i < readUserList.size(); i++) {
				readUserNames[i] = readUserList.get(i).getName();
			}
			
			return PageResultEx.ok()
					.addAttr("id", entity.getId())
					.addAttr("name", entity.getName())
					.addAttr("createUserId", entity.getCreateUserId())
					.addAttr("createUserName", userService.getEntity(entity.getCreateUserId()).getName())
					.addAttr("createTime", DateUtil.formatDateTime(entity.getCreateTime()))
					.addAttr("readUserIds", readUserIds)
					.addAttr("readUserNames", readUserNames);
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
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param id
	 * @param readUserIds
	 * @return PageResult
	 */
	@RequestMapping("/auth")
	@ResponseBody
	public PageResult auth(Integer id, Integer[] readUserIds) {
		try {
			paperTypeService.auth(id, readUserIds);
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
			paperService.move(sourceId, targetId);
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
