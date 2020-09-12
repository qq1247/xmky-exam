package com.wcpdoc.exam.base.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.base.entity.Res;
import com.wcpdoc.exam.base.service.ResService;
import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.exception.MyException;

/**
 * 资源控制层
 * 
 * v1.0 zhanghc 2016-6-11下午8:57:40
 */
@Controller
@RequestMapping("/res")
public class ResController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ResController.class);

	@Resource
	private ResService resService;

	/**
	 * 到达资源列表页面
	 * 
	 * v1.0 zhanghc 2016-6-11下午8:57:40
	 * 
	 * @param model
	 * @return String
	 */
	@RequestMapping("/toList")
	public String toList(Model model) {
		try {
			return "base/res/resList";
		}catch (Exception e) {
			log.error("到达资源列表页面错误：", e);
			return "base/res/resList";
		}
	}

	/**
	 * 获取资源树列表
	 * 
	 * v1.0 zhanghc 2016-6-11下午8:57:40
	 * 
	 * @param type
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/treeList")
	@ResponseBody
	public PageResult treeList(Integer type) {
		try {
			return new PageResultEx(true, "查询成功", resService.getTreeList(type));
		}catch (Exception e) {
			log.error("获取资源树列表错误：", e);
			return new PageResult(false, "查询失败");
		}
	}

	/**
	 * 资源列表
	 * 
	 * v1.0 zhanghc 2016-6-11下午8:57:40
	 * 
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/list")
	@ResponseBody
	public PageResult list(PageIn pageIn) {
		try {
			return new PageResultEx(true, "查询成功", resService.getListpage(pageIn));
		}catch (Exception e) {
			log.error("资源列表错误：", e);
			return new PageResult(false, "查询失败");
		}
	}

	/**
	 * 到达添加资源页面
	 * 
	 * v1.0 zhanghc 2016-6-11下午8:57:40
	 * 
	 * @return String
	 */
	@RequestMapping("/toAdd")
	public String toAdd() {
		try {
			return "base/res/resEdit";
		} catch (Exception e) {
			log.error("到达添加资源页面错误：", e);
			return "base/res/resEdit";
		}
	}

	/**
	 * 完成添加资源
	 * 
	 * v1.0 zhanghc 2016-6-11下午8:57:40
	 * 
	 * @param res
	 * @return PageResult
	 */
	@RequestMapping("/doAdd")
	@ResponseBody
	public PageResult doAdd(Res res) {
		try {
			resService.addAndUpdate(res);
			return new PageResult(true, "添加成功");
		} catch (MyException e) {
			log.error("完成添加资源错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成添加资源错误：", e);
			return new PageResult(false, "未知异常！");
		}
	}

	/**
	 * 到达修改资源页面
	 * 
	 * v1.0 zhanghc 2016-6-11下午8:57:40
	 * 
	 * @param model
	 * @param id
	 * @return String
	 */
	@RequestMapping("/toEdit")
	public String toEdit(Model model, Integer id) {
		try {
			Res res = resService.getEntity(id);
			model.addAttribute("res", res);
			if (res.getParentId() != null) {
				model.addAttribute("parentRes", resService.getEntity(res.getParentId()));
			}
			return "base/res/resEdit";
		} catch (Exception e) {
			log.error("到达修改资源页面错误：", e);
			return "base/res/resEdit";
		}
	}

	/**
	 * 完成修改资源
	 * 
	 * v1.0 zhanghc 2016-6-11下午8:57:40
	 * 
	 * @param res
	 * @return PageResult
	 */
	@RequestMapping("/doEdit")
	@ResponseBody
	public PageResult doEdit(Res res) {
		try {
			resService.editAndUpdate(res);
			return new PageResult(true, "修改成功");
		} catch (MyException e) {
			log.error("完成修改资源错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成修改资源错误：", e);
			return new PageResult(false, "未知异常！");
		}
	}

	/**
	 * 完成删除资源
	 * 
	 * v1.0 zhanghc 2016-6-11下午8:57:40
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/doDel")
	@ResponseBody
	public PageResult doDel(Integer id) {
		try {
			resService.delAndUpdate(id);
			return new PageResult(true, "删除成功");
		} catch (MyException e) {
			log.error("完成删除资源错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成删除资源错误：", e);
			return new PageResult(false, "未知异常！");
		}
	}

	/**
	 * 到达移动资源页面
	 * 
	 * v1.0 zhanghc 2016-6-11下午8:57:40
	 * 
	 * @return String
	 */
	@RequestMapping("/toMove")
	public String toMove() {
		try {
			return "base/res/resMove";
		} catch (Exception e) {
			log.error("到达移动资源页面错误", e);
			return "base/res/resMove";
		}
	}

	/**
	 * 完成移动资源
	 * 
	 * v1.0 zhanghc 2016-6-11下午8:57:40
	 * 
	 * @param sourceId
	 * @param targetId
	 * @return PageResult
	 */
	@RequestMapping("/doMove")
	@ResponseBody
	public PageResult doMove(Integer sourceId, Integer targetId) {
		try {
			resService.doMove(sourceId, targetId);
			return new PageResult(true, "移动成功");
		} catch (MyException e) {
			log.error("完成移动资源错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成移动资源错误：", e);
			return new PageResult(false, "未知异常！");
		}
	}
}
