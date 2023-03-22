package com.wcpdoc.exam.api.controller;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.base.service.UserService;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.entity.Bulletin;
import com.wcpdoc.exam.core.service.BulletinService;

/**
 * 公告控制层
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
@Controller
@RequestMapping("/api/bulletin")
public class ApiBulletinController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiBulletinController.class);
	
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
	@ResponseBody
	public PageResult listpage() {
		try {
			PageIn pageIn = new PageIn(request);
			pageIn.addAttr("curUserId", getCurUser().getId());
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
	@ResponseBody
	public PageResult add(Bulletin bulletin) {
		try {
			// 校验数据有效性
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
			bulletinService.add(bulletin);
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
	@ResponseBody
	public PageResult edit(Bulletin bulletin) {
		try {
			// 校验数据有效性
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
			bulletinService.update(bulletin);
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
	@ResponseBody
	public PageResult del(Integer id) {
		try {
			Bulletin bulletin = bulletinService.getEntity(id);
			bulletin.setState(0);
			bulletinService.update(bulletin);
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
	@ResponseBody
	public PageResult get(Integer id) {		
		try {
			Bulletin bulletin = bulletinService.getEntity(id);
			return PageResultEx.ok()
					.addAttr("id", bulletin.getId())
					.addAttr("startTime", DateUtil.formatDateTime(bulletin.getStartTime()))
					.addAttr("endTime", DateUtil.formatDateTime(bulletin.getEndTime()))
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
