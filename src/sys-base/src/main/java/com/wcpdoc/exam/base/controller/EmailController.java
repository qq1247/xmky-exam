package com.wcpdoc.exam.base.controller;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.base.entity.Email;
import com.wcpdoc.exam.base.service.EmailService;
import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.exception.MyException;
/**
 * 邮箱控制层
 * 
 * v1.0 chenyun 2021-03-04 15:02:18
 */
@Controller
@RequestMapping("/email")
public class EmailController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(EmailController.class);
	
	@Resource
	private EmailService emailService;
	
	/**
	 * 到达邮箱列表页面
	 * 
	 * v1.0 chenyun 2021-03-04 15:02:18
	 * @return String
	 */
	@RequestMapping("/toList")
	public String toList() {
		try {
			return "base/email/emailList";
		} catch (Exception e) {
			log.error("到达邮箱列表页面错误：", e);
			return "base/email/emailList";
		}
	}
	
	/**
	 * 邮箱列表
	 * 
	 * v1.0 chenyun 2021-03-04 15:02:18
	 * @return pageOut
	 */
	@RequestMapping("/list")
	@ResponseBody
	public PageResult list(PageIn pageIn) {
		try {
			return PageResultEx.ok().data(emailService.getListpage(pageIn));
		} catch (Exception e) {
			log.error("邮箱列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 到达添加邮箱页面
	 * 
	 * v1.0 chenyun 2021-03-04 15:02:18
	 * @return String
	 */
	@RequestMapping("/toAdd")
	public String toAdd(Model model) {
		try {
			return "base/email/emailEdit";
		} catch (Exception e) {
			log.error("到达添加邮箱页面错误：", e);
			return "base/email/emailEdit";
		}
	}
	
	/**
	 * 完成添加邮箱
	 * 
	 * v1.0 chenyun 2021-03-04 15:02:18
	 * @return pageOut
	 */
	@RequestMapping("/doAdd")
	@ResponseBody
	public PageResult doAdd(Email email) {
		try {
			email.setUpdateTime(new Date());
			email.setUpdateUserId(getCurUser().getId());
			emailService.add(email);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成添加邮箱错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成添加邮箱错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 到达修改邮箱页面
	 * 
	 * v1.0 chenyun 2021-03-04 15:02:18
	 * @return String
	 */
	@RequestMapping("/toEdit")
	public String toEdit(Model model, Integer id) {
		try {
			Email email = emailService.getEntity(id);
			model.addAttribute("email", email);
			return "base/email/emailEdit";
		} catch (Exception e) {
			log.error("到达修改邮箱页面错误：", e);
			return "base/email/emailEdit";
		}
	}
	
	/**
	 * 完成修改邮箱
	 * 
	 * v1.0 chenyun 2021-03-04 15:02:18
	 * @return pageOut
	 */
	@RequestMapping("/doEdit")
	@ResponseBody
	public PageResult doEdit(Email email) {
		try {
			Email entity = emailService.getEmail(getCurUser().getId());
			entity.setEmailHost(email.getEmailHost());
			entity.setEmailName(email.getEmailName());
			entity.setEmailPwd(email.getEmailPwd());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(getCurUser().getId());
			emailService.update(entity);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成修改邮箱错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成修改邮箱错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 完成删除邮箱
	 * 
	 * v1.0 chenyun 2021-03-04 15:02:18
	 * @return pageOut
	 */
	@RequestMapping("/doDel")
	@ResponseBody
	public PageResult doDel(Integer id) {
		try {
			emailService.delAndUpdate(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成删除邮箱错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成删除邮箱错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 发送邮箱
	 * 
	 * v1.0 chenyun 2021-03-04 15:02:18
	 * @return pageOut
	 */
	@RequestMapping("/send")
	@ResponseBody
	public PageResult send(String receiverName, String receiverTitle, String receiverContent) {
		try {
			emailService.sendEmail(receiverName, receiverTitle, receiverContent);
			return null;
		} catch (MyException e) {
			log.error("完成发送邮箱错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成发送邮箱错误：", e);
			return PageResult.err();
		}
	}
}
