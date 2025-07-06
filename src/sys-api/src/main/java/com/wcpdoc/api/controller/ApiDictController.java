package com.wcpdoc.api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.base.entity.Dict;
import com.wcpdoc.base.service.BaseCacheService;
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
	@Resource
	private BaseCacheService baseCacheService;

	/**
	 * 数据字典列表
	 * 
	 * v1.0 zhanghc 2016-11-3下午9:03:40
	 * 
	 * @param pageIn
	 * @return PageResult
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
			dictService.add(dict);
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
			dictService.editEx(dict);
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
			dictService.del(id);
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

	/**
	 * 数据字典索引列表
	 * 
	 * v1.0 zhanghc 2024年6月21日上午11:03:12
	 * 
	 * @return PageResult
	 */
	@RequestMapping("/indexList")
	public PageResult indexList() {
		try {
			List<Map<String, Object>> result = baseCacheService.getDictList().stream()//
					.map(dict -> {
						Map<String, Object> data = new HashMap<>();
						data.put("dictIndex", dict.getDictIndex());
						data.put("dictKey", dict.getDictKey());
						data.put("dictValue", dict.getDictValue());
						data.put("no", dict.getNo());
						return data;
					}).collect(Collectors.toList());

//			dictList.sort(Comparator.comparingInt(Dict::getNo));
//			Map<String, List<Map<String, String>>> result = dictList.stream()
//					.collect(Collectors.groupingBy(Dict::getDictIndex, Collectors.mapping(dict -> {
//						Map<String, String> keyValueMap = new HashMap<>();
//						keyValueMap.put("key", dict.getDictKey());
//						keyValueMap.put("value", dict.getDictValue());
//						return keyValueMap;
//					}, Collectors.toList())));
			return PageResultEx.ok().data(result);
		} catch (MyException e) {
			log.error("数据字典索引列表错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("数据字典索引列表错误：", e);
			return PageResult.err();
		}
	}
}
