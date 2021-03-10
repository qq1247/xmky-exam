package com.wcpdoc.exam.core.controller;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.base.entity.User;
import com.wcpdoc.exam.base.service.OrgService;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.entity.PaperType;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.PaperTypeService;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 试卷分类控制层
 * 
 * v1.0 zhanghc 2017-05-25 16:34:59
 */
@Controller
@RequestMapping("/paperType")
public class PaperTypeController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(PaperTypeController.class);
	
	@Resource
	private PaperTypeService paperTypeService;
	@Resource
	private OrgService orgService;
	
	/**
	 * 试卷分类列表 
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/list")
	@ResponseBody
	public PageResult list(PageIn pageIn) {
		try {
			return PageResultEx.ok().data(paperTypeService.getListpage(pageIn));
		} catch (Exception e) {
			log.error("试卷分类列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 到达添加试卷分类页面 
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return String
	 */
	@RequestMapping("/toAdd")
	public String toAdd() {
		try {
			return "exam/paperType/paperTypeEdit";
		} catch (Exception e) {
			log.error("到达添加试卷分类页面错误", e);
			return "exam/paperType/paperTypeEdit";
		}
	}
	
	/**
	 * 完成添加试卷分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/doAdd")
	@ResponseBody
	public PageResult doAdd(PaperType paperType) {
		try {
			paperTypeService.addAndUpdate(paperType);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成添加试卷分类错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成添加试卷分类错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 完成修改试卷分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/doEdit")
	@ResponseBody
	public PageResult doEdit(PaperType paperType) {
		try {
			//校验数据有效性
			if(!ValidateUtil.isValid(paperType.getName())) {
				throw new MyException("参数错误：name");
			}
			if(paperTypeService.existName(paperType)) {
				throw new MyException("名称已存在！");
			}
			
			//修改试卷分类
			PaperType entity = paperTypeService.getEntity(paperType.getId());
			entity.setName(paperType.getName());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(((User)getCurUser()).getId());
			paperTypeService.update(entity);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成修改试卷分类错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成修改试卷分类错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 完成删除试卷分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/doDel")
	@ResponseBody
	public PageResult doDel(Integer id) {
		try {
			paperTypeService.delAndUpdate(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成删除试卷分类错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成删除试卷分类错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 到达移动试卷分类页面
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * @return String
	 */
	@RequestMapping("/toMove")
	public String toMove() {
		try {
			return "exam/paperType/paperTypeMove";
		} catch (Exception e) {
			log.error("到达移动试卷分类页面错误", e);
			return "exam/paperType/paperTypeMove";
		}
	}
	
	/**
	 * 到达权限列表页面
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @return String
	 */
	@RequestMapping("/toAuth")
	public String toAuth(Model model, Integer id) {
		try {
			PaperType paperType = paperTypeService.getEntity(id);
			model.addAttribute("paperType", paperType);
			return "exam/paperType/paperTypeAuthList";
		} catch (Exception e) {
			log.error("到达权限列表页面错误：", e);
			return "exam/paperType/paperTypeAuthList";
		}
	}
	
	/**
	 * 完成添加权限
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param id
	 * @param userIds
	 * @param postIds
	 * @param orgIds
	 * @param syn2Sub true ： 同步授权到子分类
	 * @return PageResult
	 */
	@RequestMapping("/doAuth")
	@ResponseBody
	public PageResult doAuth(Integer id, String readUserIds, String writeUserIds, boolean rwState) {
		try {
			paperTypeService.doAuth(id, readUserIds, writeUserIds, rwState);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成添加权限用户错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成添加权限用户错误：", e);
			return PageResult.err();
		}
	}
}
