package com.wcpdoc.exam.sys.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.sys.entity.Org;
import com.wcpdoc.exam.sys.entity.Post;
import com.wcpdoc.exam.sys.service.PostService;

/**
 * 岗位控制层
 * 
 * v1.0 zhanghc 2016-5-19下午9:32:43
 */
@Controller
@RequestMapping("/post")
public class PostController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(PostController.class);

	@Resource
	private PostService postService;

	/**
	 * 到达岗位列表页面
	 * 
	 * v1.0 zhanghc 2016-5-19下午9:32:43
	 * 
	 * @return String
	 */
	@RequestMapping("/toList")
	public String toList() {
		try {
			return "/sys/post/postList";
		} catch (Exception e) {
			log.error("到达岗位列表页面错误：", e);
			return "/sys/post/postList";
		}
	}

	/**
	 * 获取机构岗位树
	 * 
	 * v1.0 zhanghc 2016-5-19下午9:32:43
	 * 
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/orgPostTreeList")
	@ResponseBody
	public List<Map<String, Object>> orgPostTreeList() {
		try {
			return postService.getOrgPostTreeList();
		} catch (Exception e) {
			log.error("获取机构岗位树错误：", e);
			return new ArrayList<Map<String, Object>>();
		}
	}

	/**
	 * 岗位列表
	 * 
	 * v1.0 zhanghc 2016-5-19下午9:32:43
	 * 
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/list")
	@ResponseBody
	public PageOut list(PageIn pageIn) {
		try {
			return postService.getListpage(pageIn);
		} catch (Exception e) {
			log.error("岗位列表错误：", e);
			return new PageOut();
		}
	}

	/**
	 * 到达添加岗位页面
	 * 
	 * v1.0 zhanghc 2016-5-19下午9:32:43
	 * 
	 * @return String
	 */
	@RequestMapping("/toAdd")
	public String toAdd() {
		try {
			return "/sys/post/postEdit";
		} catch (Exception e) {
			log.error("到达添加岗位页面错误：", e);
			return "/sys/post/postEdit";
		}
	}

	/**
	 * 完成添加岗位
	 * 
	 * v1.0 zhanghc 2016-5-19下午9:32:43
	 * 
	 * @param post
	 * @param orgId
	 * @return PageResult
	 */
	@RequestMapping("/doAdd")
	@ResponseBody
	public PageResult doAdd(Post post, Integer orgId) {
		try {
			post.setUpdateTime(new Date());
			post.setUpdateUserId(getCurUser().getId());
			post.setState(1);
			postService.addAndUpdate(post, orgId);
			return new PageResult(true, "添加成功");
		} catch (Exception e) {
			log.error("完成添加岗位错误：", e);
			return new PageResult(false, "添加失败：" + e.getMessage());
		}
	}

	/**
	 * 到达修改岗位页面
	 * 
	 * v1.0 zhanghc 2016-5-19下午9:32:43
	 * 
	 * @param model
	 * @param id
	 * @return String
	 */
	@RequestMapping("/toEdit")
	public String toEdit(Model model, Integer id) {
		try {
			Post post = postService.getEntity(id);
			Org org = postService.getOrg(id);
			model.addAttribute("post", post);
			model.addAttribute("org", org);
			return "/sys/post/postEdit";
		} catch (Exception e) {
			log.error("到达修改岗位页面错误：", e);
			return "/sys/post/postEdit";
		}
	}

	/**
	 * 完成修改岗位
	 * 
	 * v1.0 zhanghc 2016-5-19下午9:32:43
	 * 
	 * @param post
	 * @return PageResult
	 */
	@RequestMapping("/doEdit")
	@ResponseBody
	public PageResult doEdit(Post post) {
		try {
			Post entity = postService.getEntity(post.getId());
			entity.setName(post.getName());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(getCurUser().getId());
			postService.editAndUpdate(entity);
			return new PageResult(true, "修改成功");
		} catch (Exception e) {
			log.error("完成修改岗位错误：", e);
			return new PageResult(false, "修改失败：" + e.getMessage());
		}
	}

	/**
	 * 完成删除岗位
	 * 
	 * v1.0 zhanghc 2016-5-19下午9:32:43
	 * 
	 * @param ids
	 * @return PageResult
	 */
	@RequestMapping("/doDel")
	@ResponseBody
	public PageResult doDel(Integer[] ids) {
		try {
			postService.delAndUpdate(ids);
			return new PageResult(true, "删除成功");
		} catch (Exception e) {
			log.error("完成删除岗位错误：", e);
			return new PageResult(false, "删除失败：" + e.getMessage());
		}
	}

	/**
	 * 到达设置权限页面
	 * 
	 * v1.0 zhanghc 2016-5-19下午9:32:43
	 * 
	 * @return String
	 */
	@RequestMapping("/toResUpdate")
	public String toResUpdate() {
		try {
			return "/sys/post/postResUpdate";
		} catch (Exception e) {
			log.error("到达设置权限页面错误：", e);
			return "/sys/post/postResUpdate";
		}
	}

	/**
	 * 获取设置权限树
	 * 
	 * v1.0 zhanghc 2016-5-19下午9:32:43
	 * 
	 * @param id 岗位ID
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/resUpdateResTreeList")
	@ResponseBody
	public List<Map<String, Object>> resUpdateResTreeList(Integer id) {
		try {
			return postService.getResUpdateResTreeList(id);
		} catch (Exception e) {
			log.error("获取设置权限树错误：", e);
			return new ArrayList<Map<String, Object>>();
		}
	}

	/**
	 * 完成设置权限
	 * 
	 * v1.0 zhanghc 2016-5-19下午9:32:43
	 * 
	 * @param id
	 * @param resIds
	 * @return PageResult
	 */
	@RequestMapping("/doResUpdate")
	@ResponseBody
	public PageResult doResUpdate(Integer id, Integer[] resIds) {
		try {
			postService.doResUpdate(id, resIds);
			return new PageResult(true, "设置成功");
		} catch (Exception e) {
			log.error("完成设置权限错误：", e);
			return new PageResult(false, "设置失败：" + e.getMessage());
		}
	}
}
