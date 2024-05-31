package com.wcpdoc.api.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.base.entity.Dict;
import com.wcpdoc.base.service.DictService;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;

import lombok.extern.slf4j.Slf4j;

/**
 * 数据字典控制层
 * 
 * v1.0 zhanghc 2016-11-3下午9:03:40
 */
@RestController
@RequestMapping("/api/dict")
@Slf4j
public class ApiDictController extends BaseController {

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
	public PageResult listpage(PageIn pageIn) {
		try {
			return PageResultEx.ok().data(dictService.getListpage(pageIn));
		} catch (Exception e) {
			log.error("数据字典列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 数据字典添加
	 * 
	 * v1.0 zhanghc 2016-11-3下午9:03:40
	 * 
	 * @param dict
	 * @return PageResult
	 */
	@RequestMapping("/add")
	public PageResult add(Dict dict) {
		try {
			dictService.save(dict);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("数据字典添加错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("数据字典添加错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 数据字典修改
	 * 
	 * v1.0 zhanghc 2016-11-3下午9:03:40
	 * 
	 * @param dict
	 * @return PageResult
	 */
	@RequestMapping("/edit")
	public PageResult edit(Dict dict) {
		try {
			Dict entity = dictService.getById(dict.getId());
			entity.setDictIndex(dict.getDictIndex());
			entity.setDictKey(dict.getDictKey());
			entity.setDictValue(dict.getDictValue());
			entity.setNo(dict.getNo());
			dictService.updateById(entity);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("数据字典修改错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("数据字典修改错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 数据字典删除
	 * 
	 * v1.0 zhanghc 2016-11-3下午9:03:40
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/del")
	public PageResult del(Integer id) {
		try {
			dictService.removeById(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("数据字典删除错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("数据字典删除错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 数据字典获取
	 * 
	 * v1.0 zhanghc 2021年5月27日下午4:27:54
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/get")
	public PageResult get(Integer id) {
		try {
			Dict dict = dictService.getById(id);
			return PageResultEx.ok()//
					.addAttr("id", dict.getId())//
					.addAttr("dictIndex", dict.getDictIndex())//
					.addAttr("dictKey", dict.getDictKey())//
					.addAttr("dictValue", dict.getDictValue())//
					.addAttr("no", dict.getNo());
		} catch (MyException e) {
			log.error("数据字典获取错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("数据字典获取错误：", e);
			return PageResult.err();
		}
	}
}
