package com.wcpdoc.exam.api.controller;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.base.service.UserService;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.entity.Bulletin;
import com.wcpdoc.exam.core.service.BulletinService;

import lombok.extern.slf4j.Slf4j;

/**
 * 公告控制层
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
@RestController
@RequestMapping("/api/bulletin")
@Slf4j
public class ApiBulletinController extends BaseController {
	
	@Resource
	private BulletinService bulletinService;
	@Resource
	private UserService userService;
	
	/**
	 * 公告列表
	 * 
	 * v1.0 chenyun 2021-03-24 13:39:37
	 * @return pageOut
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
	 * 添加公告
	 * 
	 * v1.0 chenyun 2021-03-24 13:39:37
	 * @return pageOut
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
			
			// 添加公告
			bulletin.setState(1);
			bulletin.setUpdateTime(new Date());
			bulletin.setUpdateUserId(getCurUser().getId());
			bulletinService.save(bulletin);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("添加公告错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("添加公告错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 修改公告
	 * 
	 * v1.0 chenyun 2021-03-24 13:39:37
	 * @return pageOut
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
			
			// 添加公告
			bulletin.setState(1);
			bulletin.setUpdateTime(new Date());
			bulletin.setUpdateUserId(getCurUser().getId());
			bulletinService.updateById(bulletin);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("修改公告错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("修改公告错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 删除公告
	 * 
	 * v1.0 chenyun 2021-03-24 13:39:37
	 * @return pageOut
	 */
	@RequestMapping("/del")
	public PageResult del(Integer id) {
		try {
			Bulletin bulletin = bulletinService.getById(id);
			bulletin.setState(0);
			bulletinService.updateById(bulletin);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("删除公告错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("删除公告错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 获取公告
	 * 
	 * v1.0 chenyun 2021-03-04 15:02:18
	 * @return pageOut
	 */
	@RequestMapping("/get")
	public PageResult get(Integer id) {		
		try {
			Bulletin bulletin = bulletinService.getById(id);
			return PageResultEx.ok()
					.addAttr("id", bulletin.getId())
					.addAttr("startTime", bulletin.getStartTime())
					.addAttr("endTime", bulletin.getEndTime())
					.addAttr("title", bulletin.getTitle())
					.addAttr("content", bulletin.getContent());
		} catch (MyException e) {
			log.error("获取公告错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("获取公告错误：", e);
			return PageResult.err();
		}
	}
}
