package com.wcpdoc.exam.api.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
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
@RequestMapping("/api/dict")
public class ApiDictController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiDictController.class);

	@Resource
	private DictService dictService;

	/**
	 * 数据字典列表
	 * 
	 * v1.0 zhanghc 2016-11-3下午9:03:40
	 * 
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/listpage")
	@ResponseBody
	public PageResult listpage() {
		try {
			return PageResultEx.ok().data(dictService.getListpage(new PageIn(request)));
		} catch (Exception e) {
			log.error("数据字典列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 添加数据字典
	 * 
	 * v1.0 zhanghc 2016-11-3下午9:03:40
	 * 
	 * @param dict
	 * @return PageResult
	 */
	@RequestMapping("/add")
	@ResponseBody
	public PageResult add(Dict dict) {
		try {
			dictService.addAndUpdate(dict);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("添加数据字典错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("添加数据字典错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 修改数据字典
	 * 
	 * v1.0 zhanghc 2016-11-3下午9:03:40
	 * 
	 * @param dict
	 * @return PageResult
	 */
	@RequestMapping("/edit")
	@ResponseBody
	public PageResult edit(Dict dict) {
		try {
			dictService.updateAndUpdate(dict);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("修改数据字典错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("修改数据字典错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 删除数据字典
	 * 
	 * v1.0 zhanghc 2016-11-3下午9:03:40
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/del")
	@ResponseBody
	public PageResult del(Integer id) {
		try {
			dictService.delAndUpdate(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("删除数据字典错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("删除数据字典错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 获取数据字典
	 * 
	 * v1.0 zhanghc 2021年5月27日下午4:27:54
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/get")
	@ResponseBody
	public PageResult get(Integer id) {
		try {
			Dict dict = dictService.getEntity(id);
			return PageResultEx.ok()
					.addAttr("id", dict.getId())
					.addAttr("dictIndex", dict.getDictIndex())
					.addAttr("dictKey", dict.getDictKey())
					.addAttr("dictValue", dict.getDictValue())
					.addAttr("no", dict.getNo());
		} catch (MyException e) {
			log.error("删除数据字典错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("删除数据字典错误：", e);
			return PageResult.err();
		}
	}
}
