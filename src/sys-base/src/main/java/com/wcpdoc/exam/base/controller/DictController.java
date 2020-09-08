package com.wcpdoc.exam.base.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.base.entity.Dict;
import com.wcpdoc.exam.base.service.DictService;
import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.exception.MyException;

/**
 * 数据字典控制层
 * 
 * v1.0 zhanghc 2016-11-3下午9:03:40
 */
@Controller
@RequestMapping("/dict")
public class DictController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(DictController.class);

	@Resource
	private DictService dictService;

	/**
	 * 到达数据字典列表页面
	 * 
	 * v1.0 zhanghc 2016-11-3下午9:03:40
	 * 
	 * @return String
	 */
	@RequestMapping("/toList")
	public String toList() {
		try {
			return "base/dict/dictList";
		} catch (Exception e) {
			log.error("到达数据字典列表页面错误：", e);
			return "base/dict/dictList";
		}
	}

	/**
	 * 数据字典列表
	 * 
	 * v1.0 zhanghc 2016-11-3下午9:03:40
	 * 
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/list")
	@ResponseBody
	public PageResult list(PageIn pageIn) {
		try {
			return new PageResultEx(true, "查询成功", dictService.getListpage(pageIn));
		} catch (Exception e) {
			log.error("数据字典列表错误：", e);
			return new PageResult(false, "查询失败");
		}
	}

	/**
	 * 到达添加数据字典页面
	 * 
	 * v1.0 zhanghc 2016-11-3下午9:03:40
	 * 
	 * @return String
	 */
	@RequestMapping("/toAdd")
	public String toAdd() {
		try {
			return "base/dict/dictEdit";
		} catch (Exception e) {
			log.error("到达添加数据字典页面错误：", e);
			return "base/dict/dictEdit";
		}
	}

	/**
	 * 完成添加数据字典
	 * 
	 * v1.0 zhanghc 2016-11-3下午9:03:40
	 * 
	 * @param dict
	 * @return PageResult
	 */
	@RequestMapping("/doAdd")
	@ResponseBody
	public PageResult doAdd(Dict dict) {
		try {
			dictService.addAndUpdate(dict);
			return new PageResult(true, "添加成功");
		} catch (MyException e) {
			log.error("完成添加数据字典错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成添加数据字典错误：", e);
			return new PageResult(false, "未知异常！");
		}
	}

	/**
	 * 到达修改数据字典页面
	 * 
	 * v1.0 zhanghc 2016-11-3下午9:03:40
	 * 
	 * @param model
	 * @param id
	 * @return String
	 */
	@RequestMapping("/toEdit")
	public String toEdit(Model model, Integer id) {
		try {
			Dict dict = dictService.getEntity(id);
			model.addAttribute("dict", dict);
			return "base/dict/dictEdit";
		} catch (Exception e) {
			log.error("到达修改数据字典页面错误：", e);
			return "base/dict/dictEdit";
		}
	}

	/**
	 * 完成修改数据字典
	 * 
	 * v1.0 zhanghc 2016-11-3下午9:03:40
	 * 
	 * @param dict
	 * @return PageResult
	 */
	@RequestMapping("/doEdit")
	@ResponseBody
	public PageResult doEdit(Dict dict) {
		try {
			dictService.updateAndUpdate(dict);
			return new PageResult(true, "修改成功");
		} catch (MyException e) {
			log.error("完成修改数据字典错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成修改数据字典错误：", e);
			return new PageResult(false, "未知异常！");
		}
	}

	/**
	 * 完成删除数据字典
	 * 
	 * v1.0 zhanghc 2016-11-3下午9:03:40
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/doDel")
	@ResponseBody
	public PageResult doDel(Integer id) {
		try {
			dictService.delAndUpdate(id);
			return new PageResult(true, "删除成功");
		} catch (MyException e) {
			log.error("完成删除数据字典错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成删除数据字典错误：", e);
			return new PageResult(false, "未知异常！");
		}
	}
}
