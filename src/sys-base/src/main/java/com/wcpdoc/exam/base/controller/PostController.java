package com.wcpdoc.exam.base.controller;

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

import com.wcpdoc.exam.base.entity.Org;
import com.wcpdoc.exam.base.entity.Post;
import com.wcpdoc.exam.base.service.OrgService;
import com.wcpdoc.exam.base.service.PostService;
import com.wcpdoc.exam.base.service.ResService;
import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.util.StringUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;

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
	@Resource
	private OrgService orgService;
	@Resource
	private ResService resService;

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
			return "base/post/postList";
		} catch (Exception e) {
			log.error("到达岗位列表页面错误：", e);
			return "base/post/postList";
		}
	}

	/**
	 * 组织机构树
	 * 
	 * v1.0 zhanghc 2016-5-19下午9:32:43
	 * 
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/orgTreeList")
	@ResponseBody
	public PageResult orgTreeList() {
		try {
			return PageResultEx.ok().data(orgService.getTreeList());
		} catch (Exception e) {
			log.error("组织机构树错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 权限树
	 * 
	 * v1.0 zhanghc 2016-5-19下午9:32:43
	 * 
	 * @param id
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/resTreeList")
	@ResponseBody
	public PageResult resTreeList(Integer id) {
		try {
			List<Map<String, Object>> resTreeList = resService.getTreeList(1);
			Post post = postService.getEntity(id);
			for (Map<String, Object> map : resTreeList) {
				if (ValidateUtil.isValid(post.getResIds()) && post.getResIds().contains(String.format(",%s,", map.get("ID")))) {
					map.put("CHECKED", true);
				}
			}
			
			return PageResultEx.ok().data(resTreeList);
		} catch (Exception e) {
			log.error("权限树错误：", e);
			return PageResult.err();
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
	public PageResult list(PageIn pageIn) {
		try {
			return PageResultEx.ok().data(postService.getListpage(pageIn));
		} catch (Exception e) {
			log.error("岗位列表错误：", e);
			return PageResult.err();
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
			return "base/post/postEdit";
		} catch (Exception e) {
			log.error("到达添加岗位页面错误：", e);
			return "base/post/postEdit";
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
	public PageResult doAdd(Post post) {
		try {
			// 校验数据有效性
			if (post.getOrgId() == null) {
				throw new MyException("参数错误：orgId");
			}
			if (!ValidateUtil.isValid(post.getName())) {
				throw new MyException("参数错误：name");
			}
			if (postService.existName(post)) {
				throw new MyException("名称已存在！");
			}
			if (postService.existCode(post)) {
				throw new MyException("编码已存在！");
			}

			// 添加岗位
			post.setUpdateTime(new Date());
			post.setUpdateUserId(getCurUser().getId());
			post.setState(1);
			post.setResIds(null);
			postService.add(post);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成添加岗位错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成添加岗位错误：", e);
			return PageResult.err();
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
			Org org = orgService.getEntity(id);
			model.addAttribute("post", post);
			model.addAttribute("org", org);
			return "base/post/postEdit";
		} catch (Exception e) {
			log.error("到达修改岗位页面错误：", e);
			return "base/post/postEdit";
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
			// 校验数据有效性
			if (!ValidateUtil.isValid(post.getName())) {
				throw new MyException("参数错误：name");
			}
			if (postService.existName(post)) {
				throw new MyException("名称已存在！");
			}
			if (postService.existCode(post)) {
				throw new MyException("编码已存在！");
			}

			// 修改岗位
			Post entity = postService.getEntity(post.getId());
			entity.setName(post.getName());
			entity.setCode(post.getCode());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(getCurUser().getId());
			postService.update(entity);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成修改岗位错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成修改岗位错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 完成删除岗位
	 * 
	 * v1.0 zhanghc 2016-5-19下午9:32:43
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/doDel")
	@ResponseBody
	public PageResult doDel(Integer id) {
		try {
			Post post = postService.getEntity(id);
			post.setState(0);
			post.setUpdateTime(new Date());
			post.setUpdateUserId(getCurUser().getId());
			postService.update(post);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成删除岗位错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成删除岗位错误：", e);
			return PageResult.err();
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
			return "base/post/postResUpdate";
		} catch (Exception e) {
			log.error("到达设置权限页面错误：", e);
			return "base/post/postResUpdate";
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
			// 校验数据有效性
			if (id == null) {
				throw new MyException("参数错误：id");
			}
			
			// 完成设置权限
			Post post = postService.getEntity(id);
			if (!ValidateUtil.isValid(resIds)) {
				post.setResIds(null);
			} else {
				post.setResIds(String.format(",%s,", StringUtil.join(resIds)));
			}
			postService.update(post);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成设置权限错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成设置权限错误：", e);
			return PageResult.err();
		}
	}
}
