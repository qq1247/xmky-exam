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
import com.wcpdoc.exam.sys.cache.DictCache;
import com.wcpdoc.exam.sys.entity.Res;
import com.wcpdoc.exam.sys.service.ResService;

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
			model.addAttribute("RES_TYPE", DictCache.getIndexDictlistMap().get("RES_TYPE"));
			return "/sys/res/resList";
		} catch (Exception e) {
			log.error("到达资源列表页面错误：", e);
			return "/sys/res/resList";
		}
	}

	/**
	 * 获取资源数据
	 * 
	 * v1.0 zhanghc 2016-6-11下午8:57:40
	 * 
	 * @param type
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/treeList")
	@ResponseBody
	public List<Map<String, Object>> treeList(Integer type) {
		try {
			return resService.getTreeList(type);
		} catch (Exception e) {
			log.error("获取资源数据错误：", e);
			return new ArrayList<Map<String, Object>>();
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
	public PageOut list(PageIn pageIn) {
		try {
			return resService.getListpage(pageIn);
		} catch (Exception e) {
			log.error("资源列表错误：", e);
			return new PageOut();
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
			return "/sys/res/resEdit";
		} catch (Exception e) {
			log.error("到达添加资源页面错误：", e);
			return "/sys/res/resEdit";
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
			res.setUpdateUserId(getCurUser().getId());
			res.setUpdateTime(new Date());
			resService.addAndUpdate(res);
			return new PageResult(true, "添加成功");
		} catch (Exception e) {
			log.error("完成添加资源错误：", e);
			return new PageResult(false, "添加失败：" + e.getMessage());
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
			Res pRes = resService.getEntity(res.getParentId());
			if (pRes != null) {
				model.addAttribute("pRes", resService.getEntity(res.getParentId()));
			}
			return "/sys/res/resEdit";
		} catch (Exception e) {
			log.error("到达修改资源页面错误：", e);
			return "/sys/res/resEdit";
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
			Res entity = resService.getEntity(res.getId());
			entity.setName(res.getName());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(getCurUser().getId());
			entity.setUrl(res.getUrl());
			entity.setNo(res.getNo());
			entity.setIcon(res.getIcon());
			resService.editAndUpdate(entity);
			return new PageResult(true, "修改成功");
		} catch (Exception e) {
			log.error("完成修改资源错误：", e);
			return new PageResult(false, "修改失败：" + e.getMessage());
		}
	}

	/**
	 * 完成删除资源
	 * 
	 * v1.0 zhanghc 2016-6-11下午8:57:40
	 * 
	 * @param ids
	 * @return PageResult
	 */
	@RequestMapping("/doDel")
	@ResponseBody
	public PageResult doDel(Integer[] ids) {
		try {
			resService.delAndUpdate(ids);
			return new PageResult(true, "删除成功");
		} catch (Exception e) {
			log.error("完成删除资源错误：", e);
			return new PageResult(false, "删除失败：" + e.getMessage());
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
			return "/sys/res/resMove";
		} catch (Exception e) {
			log.error("到达移动资源页面错误", e);
			return "/sys/res/resMove";
		}
	}

	/**
	 * 获取资源树
	 * 
	 * v1.0 zhanghc 2016-6-11下午8:57:40
	 * 
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/moveResTreeList")
	@ResponseBody
	public List<Map<String, Object>> moveResTreeList() {
		try {
			return resService.getTreeList(1);
		} catch (Exception e) {
			log.error("获取资源树错误：", e);
			return new ArrayList<Map<String, Object>>();
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
		} catch (Exception e) {
			log.error("完成移动资源错误：", e);
			return new PageResult(false, "移动失败：" + e.getMessage());
		}
	}
}
