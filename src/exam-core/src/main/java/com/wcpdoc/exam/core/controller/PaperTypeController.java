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
	 * 到达试卷分类列表页面 
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return String
	 */
	@RequestMapping("/toList")
	public String toList() {
		try {
			return "exam/paperType/paperTypeList";
		} catch (Exception e) {
			log.error("到达试卷分类列表页面错误：", e);
			return "exam/paperType/paperTypeList";
		}
	}
	
	/**
	 * 试卷分类树
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/treeList")
	@ResponseBody
	public PageResult treeList() {
		try {
			return new PageResultEx(true, "查询成功", paperTypeService.getTreeList());
		} catch (Exception e) {
			log.error("试卷分类树错误：", e);
			return new PageResult(false, "查询失败");
		}
	}
	
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
			return new PageResultEx(true, "查询成功", paperTypeService.getListpage(pageIn));
		} catch (Exception e) {
			log.error("试卷分类列表错误：", e);
			return new PageResult(false, "查询失败");
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
			return new PageResult(true, "添加成功");
		} catch (MyException e) {
			log.error("完成添加试卷分类错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成添加试卷分类错误：", e);
			return new PageResult(false, "未知异常");
		}
	}
	
	/**
	 * 到达修改试卷分类页面 
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return String
	 */
	@RequestMapping("/toEdit")
	public String toEdit(Model model, Integer id) {
		try {
			PaperType paperType = paperTypeService.getEntity(id);
			model.addAttribute("paperType", paperType);
			PaperType parentPaperType = paperTypeService.getEntity(paperType.getParentId());
			if (parentPaperType != null) {
				model.addAttribute("parentPaperType", paperTypeService.getEntity(paperType.getParentId()));
			}
			return "exam/paperType/paperTypeEdit";
		} catch (Exception e) {
			log.error("到达修改试卷分类页面错误", e);
			return "exam/paperType/paperTypeEdit";
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
			entity.setNo(paperType.getNo());
			paperTypeService.update(entity);
			return new PageResult(true, "修改成功");
		} catch (MyException e) {
			log.error("完成修改试卷分类错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成修改试卷分类错误：", e);
			return new PageResult(false, "未知异常");
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
			return new PageResult(true, "删除成功");
		} catch (MyException e) {
			log.error("完成删除试卷分类错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成删除试卷分类错误：", e);
			return new PageResult(false, "未知异常");
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
	 * 移动试卷分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @param sourceId
	 * @param targetId
	 * @return PageResult
	 */
	@RequestMapping("/doMove")
	@ResponseBody
	public PageResult doMove(Integer sourceId, Integer targetId) {
		try {
			paperTypeService.doMove(sourceId, targetId);
			return new PageResult(true, "移动成功");
		} catch (MyException e) {
			log.error("完成移动试卷分类错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成移动试卷分类错误：", e);
			return new PageResult(false, "未知异常");
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
	 * 权限用户列表 
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/authUserList")
	@ResponseBody
	public PageResult authUserList(PageIn pageIn) {
		try {
			return new PageResultEx(true, "查询成功", paperTypeService.getAuthUserListpage(pageIn));
		} catch (Exception e) {
			log.error("权限用户列表错误：", e);
			return new PageResult(false, "查询失败");
		}
	}
	
	/**
	 * 权限岗位列表 
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/authPostList")
	@ResponseBody
	public PageResult authPostList(PageIn pageIn) {
		try {
			return new PageResultEx(true, "查询成功", paperTypeService.getAuthPostListpage(pageIn));
		} catch (Exception e) {
			log.error("权限岗位列表错误：", e);
			return new PageResult(false, "查询失败");
		}
	}
	
	/**
	 * 权限机构列表 
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/authOrgList")
	@ResponseBody
	public PageResult authOrgList(PageIn pageIn) {
		try {
			return new PageResultEx(true, "查询成功", paperTypeService.getAuthOrgListpage(pageIn));
		} catch (Exception e) {
			log.error("权限机构列表错误：", e);
			return new PageResult(false, "查询失败");
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
	public PageResult doAuth(Integer id, Integer[] userIds, Integer[] postIds, Integer[] orgIds, boolean syn2Sub) {
		try {
			paperTypeService.doAuth(id, userIds, postIds, orgIds, syn2Sub);
			return new PageResult(true, "添加成功");
		} catch (MyException e) {
			log.error("完成添加权限用户错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成添加权限用户错误：", e);
			return new PageResult(false, "未知异常！");
		}
	}
}
