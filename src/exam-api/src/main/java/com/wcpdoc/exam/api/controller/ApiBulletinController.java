package com.wcpdoc.exam.api.controller;

import java.util.Date;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.entity.Bulletin;
import com.wcpdoc.exam.core.service.BulletinService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 公告控制层
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
@RestController
@RequestMapping("/api/bulletin")
@RequiredArgsConstructor
@Slf4j
public class ApiBulletinController extends BaseController {

	private final BulletinService bulletinService;

	/**
	 * 公告列表
	 * 
	 * v1.0 chenyun 2021-03-24 13:39:37
	 * 
	 * @param pageIn
	 * @return PageResult
	 */
	@RequestMapping("/listpage")
	public PageResult listpage(PageIn pageIn) {
		try {
			pageIn.addParm("curUserId", getCurUser().getId());
			return PageResultEx.ok().data(bulletinService.getListpage(pageIn));
		} catch (Exception e) {
			log.error("公告列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 公告添加
	 * 
	 * v1.0 chenyun 2021-03-24 13:39:37
	 * 
	 * @param bulletin
	 * @return PageResult
	 */
	@RequestMapping("/add")
	public PageResult add(Bulletin bulletin) {
		try {
			// 数据校验
			if (!ValidateUtil.isValid(bulletin.getStartTime())) {
				throw new MyException("参数错误：startTime");
			}
			if (!ValidateUtil.isValid(bulletin.getEndTime())) {
				throw new MyException("参数错误：endTime");
			}
			if (!ValidateUtil.isValid(bulletin.getTitle())) {
				throw new MyException("参数错误：title");
			}

			// 公告添加
			bulletin.setState(1);
			bulletin.setUpdateTime(new Date());
			bulletin.setUpdateUserId(getCurUser().getId());
			bulletinService.save(bulletin);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("公告添加错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("公告添加错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 公告修改
	 * 
	 * v1.0 chenyun 2021-03-24 13:39:37
	 * 
	 * @param bulletin
	 * @return PageResult
	 */
	@RequestMapping("/edit")
	public PageResult edit(Bulletin bulletin) {
		try {
			// 数据校验
			if (!ValidateUtil.isValid(bulletin.getStartTime())) {
				throw new MyException("参数错误：startTime");
			}
			if (!ValidateUtil.isValid(bulletin.getEndTime())) {
				throw new MyException("参数错误：endTime");
			}
			if (!ValidateUtil.isValid(bulletin.getTitle())) {
				throw new MyException("参数错误：title");
			}

			// 公告修改
			Bulletin entity = bulletinService.getById(bulletin.getId());
			entity.setStartTime(bulletin.getStartTime());
			entity.setEndTime(bulletin.getEndTime());
			entity.setTitle(bulletin.getTitle());
			entity.setContent(bulletin.getContent());
			entity.setUpdateUserId(getCurUser().getId());
			entity.setUpdateTime(new Date());
			bulletinService.updateById(entity);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("公告修改错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("公告修改错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 公告删除
	 * 
	 * v1.0 chenyun 2021-03-24 13:39:37
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/del")
	public PageResult del(Integer id) {
		try {
			Bulletin entity = bulletinService.getById(id);
			entity.setState(0);
			entity.setUpdateUserId(getCurUser().getId());
			entity.setUpdateTime(new Date());
			bulletinService.updateById(entity);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("公告删除错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("公告删除错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 公告获取
	 * 
	 * v1.0 chenyun 2021-03-04 15:02:18
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/get")
	public PageResult get(Integer id) {
		try {
			Bulletin entity = bulletinService.getById(id);
			return PageResultEx.ok()//
					.addAttr("id", entity.getId())//
					.addAttr("startTime", entity.getStartTime())//
					.addAttr("endTime", entity.getEndTime())//
					.addAttr("title", entity.getTitle())//
					.addAttr("content", entity.getContent())//
			;
		} catch (MyException e) {
			log.error("公告获取错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("公告获取错误：", e);
			return PageResult.err();
		}
	}
}
