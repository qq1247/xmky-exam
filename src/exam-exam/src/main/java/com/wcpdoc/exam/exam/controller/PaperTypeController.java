package com.wcpdoc.exam.exam.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.exam.entity.PaperType;
import com.wcpdoc.exam.exam.entity.PaperTypeAuth;
import com.wcpdoc.exam.exam.service.PaperTypeService;
import com.wcpdoc.exam.sys.entity.User;

/**
 * 试卷分类控制层
 * 
 * v1.0 zhanghc 2017-05-25 16:34:59
 */
@Controller
@RequestMapping("/paperType")
public class PaperTypeController extends BaseController{
	private static final Logger log = LoggerFactory.getLogger(PaperTypeController.class);
	
	@Resource
	private PaperTypeService paperTypeService;
	
	/**
	 * 到达试卷分类列表页面 
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return String
	 */
	@RequestMapping("/toList")
	public String toList() {
		try {
			return "/WEB-INF/jsp/exam/paperType/paperTypeList.jsp";
		} catch (Exception e) {
			log.error("到达试卷分类列表页面错误：", e);
			return "/WEB-INF/jsp/exam/paperType/paperTypeList.jsp";
		}
	}
	
	/**
	 * 获取试卷分类树
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/treeList")
	@ResponseBody
	public List<Map<String, Object>> treeList() {
		try {
			return paperTypeService.getTreeList();
		} catch (Exception e) {
			log.error("获取试卷分类树错误：", e);
			return new ArrayList<Map<String,Object>>();
		}
	}
	
	/**
	 * 试卷分类列表 
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return pageOut
	 */
	@RequestMapping("/list")
	@ResponseBody
	public PageOut list(PageIn pageIn) {
		try {
			return paperTypeService.getListpage(pageIn);
		} catch (Exception e) {
			log.error("试卷分类列表错误：", e);
			return new PageOut();
		}
	}
	
	/**
	 * 到达添加试卷分类页面 
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return String
	 */
	@RequestMapping("/toAdd")
	public String toAdd() {
		try {
			return "/WEB-INF/jsp/exam/paperType/paperTypeEdit.jsp";
		} catch (Exception e) {
			log.error("到达添加试卷分类页面错误", e);
			return "/WEB-INF/jsp/exam/paperType/paperTypeEdit.jsp";
		}
	}
	
	/**
	 * 完成添加试卷分类
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return pageOut
	 */
	@RequestMapping("/doAdd")
	@ResponseBody
	public PageResult doAdd(PaperType paperType) {
		try {
			paperType.setUpdateUserId(getCurrentUser().getId());
			paperType.setUpdateTime(new Date());
			paperType.setState(1);
			paperTypeService.saveAndUpdate(paperType);
			return new PageResult(true, "添加成功");
		} catch (Exception e) {
			log.error("完成添加试卷分类错误：", e);
			return new PageResult(false, "添加失败：" + e.getMessage());
		}
	}
	
	/**
	 * 到达修改试卷分类页面 
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return String
	 */
	@RequestMapping("/toEdit")
	public String toEdit(Model model, Integer id) {
		try {
			PaperType paperType = paperTypeService.getEntity(id);
			model.addAttribute("paperType", paperType);
			
			PaperType pPaperType = paperTypeService.getEntity(paperType.getParentId());
			model.addAttribute("pPaperType", pPaperType);
			return "/WEB-INF/jsp/exam/paperType/paperTypeEdit.jsp";
		} catch (Exception e) {
			log.error("到达修改试卷分类页面错误", e);
			return "/WEB-INF/jsp/exam/paperType/paperTypeEdit.jsp";
		}
	}
	
	/**
	 * 完成修改试卷分类
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return pageOut
	 */
	@RequestMapping("/doEdit")
	@ResponseBody
	public PageResult doEdit(PaperType paperType) {
		try {
			PaperType entity = paperTypeService.getEntity(paperType.getId());
			entity.setName(paperType.getName());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(((User)getCurrentUser()).getId());
			entity.setNo(paperType.getNo());
			paperTypeService.editAndUpdate(entity);
			return new PageResult(true, "修改成功");
		} catch (Exception e) {
			log.error("完成修改试卷分类错误：", e);
			return new PageResult(false, "修改失败：" + e.getMessage());
		}
	}
	
	/**
	 * 完成删除试卷分类
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return pageOut
	 */
	@RequestMapping("/doDel")
	@ResponseBody
	public PageResult doDel(Integer[] ids) {
		try {
			paperTypeService.delAndUpdate(ids);
			return new PageResult(true, "删除成功");
		} catch (Exception e) {
			log.error("完成删除试卷分类错误：", e);
			return new PageResult(false, "删除失败：" + e.getMessage());
		}
	}
	
	/**
	 * 到达移动试卷分类页面
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return String
	 */
	@RequestMapping("/toMove")
	public String toMove() {
		try {
			return "/WEB-INF/jsp/exam/paperType/paperTypeMove.jsp";
		} catch (Exception e) {
			log.error("到达移动试卷分类页面错误", e);
			return "/WEB-INF/jsp/exam/paperType/paperTypeMove.jsp";
		}
	}
	
	/**
	 * 获取试卷分类树
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/movePaperTypeTreeList")
	@ResponseBody
	public List<Map<String, Object>> movePaperTypeTreeList() {
		try {
			return paperTypeService.getTreeList();
		} catch (Exception e) {
			log.error("获取试卷分类树错误：", e);
			return new ArrayList<Map<String,Object>>();
		}
	}
	
	/**
	 * 移动试卷分类
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
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
		} catch (Exception e) {
			log.error("移动试卷分类错误：", e);
			return new PageResult(false, "移动失败：" + e.getMessage());
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
			model.addAttribute("id", id);
			return "/WEB-INF/jsp/exam/paperType/paperTypeAuthList.jsp";
		} catch (Exception e) {
			log.error("到达权限列表页面错误：", e);
			return "/WEB-INF/jsp/exam/paperType/paperTypeAuthList.jsp";
		}
	}
	
	/**
	 * 获取组织机构树
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/authUserOrgTreeList")
	@ResponseBody
	public List<Map<String, Object>> authUserOrgTreeList() {
		try {
			return paperTypeService.getOrgTreeList();
		} catch (Exception e) {
			log.error("获取组织机构树错误：", e);
			return new ArrayList<Map<String,Object>>();
		}
	}
	
	/**
	 * 权限用户列表 
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/authUserList")
	@ResponseBody
	public PageOut authUserList(PageIn pageIn) {
		try {
			return paperTypeService.getAuthUserListpage(pageIn);
		} catch (Exception e) {
			log.error("权限用户列表错误：", e);
			return new PageOut();
		}
	}
	
	/**
	 * 到达添加权限用户列表页面
	 * 
	 * v1.0 zhanghc 2017年6月19日上午7:37:14
	 * @param id
	 * @param model
	 * @return String
	 */
	@RequestMapping("/toAuthUserAddList")
	public String toAuthUserAddList(Model model, Integer id) {
		try {
			model.addAttribute("id", id);
			return "/WEB-INF/jsp/exam/paperType/paperTypeAuthUserAddList.jsp";
		} catch (Exception e) {
			log.error("到达添加权限用户列表页面错误：", e);
			return "/WEB-INF/jsp/exam/paperType/paperTypeAuthUserAddList.jsp";
		}
	}
	
	/**
	 * 权限用户添加列表 
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/authUserAddList")
	@ResponseBody
	public PageOut authUserAddList(PageIn pageIn) {
		try {
			return paperTypeService.getAuthUserAddList(pageIn);
		} catch (Exception e) {
			log.error("权限用户添加列表错误：", e);
			return new PageOut();
		}
	}
	
	/**
	 * 完成添加权限用户
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param id
	 * @param userids
	 * @param syn2Sub
	 * @return PageResult
	 */
	@RequestMapping("/doAuthUserAdd")
	@ResponseBody
	public PageResult doAuthUserAdd(Integer id, Integer[] userIds, boolean syn2Sub) {
		try {
			paperTypeService.doAuthUserAdd(id, userIds, syn2Sub, getCurrentUser());
			return new PageResult(true, "添加成功");
		} catch (Exception e) {
			log.error("完成添加权限用户错误：", e);
			return new PageResult(false, "添加失败：" + e.getMessage());
		}
	}
	
	/**
	 * 完成删除权限用户
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param id
	 * @param userIds
	 * @param syn2Sub
	 * @return PageResult
	 */
	@RequestMapping("/doAuthUserDel")
	@ResponseBody
	public PageResult doAuthUserDel(Integer id, Integer[] userIds, boolean syn2Sub) {
		try {
			paperTypeService.doAuthUserDel(id, userIds, syn2Sub, getCurrentUser());
			return new PageResult(true, "删除成功");
		} catch (Exception e) {
			log.error("完成删除权限用户错误：", e);
			return new PageResult(false, "删除失败：" + e.getMessage());
		}
	}
	
	/**
	 * 完成保存权限机构
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param id
	 * @param orgIds
	 * @param syn2Sub
	 * @return PageResult
	 */
	@RequestMapping("/doAuthOrgUpdate")
	@ResponseBody
	public PageResult doAuthOrgUpdate(Integer id, Integer[] orgIds, boolean syn2Sub) {
		try {
			paperTypeService.doAuthOrgUpdate(id, orgIds, syn2Sub, getCurrentUser());
			return new PageResult(true, "保存成功");
		} catch (Exception e) {
			log.error("完成保存权限机构错误：", e);
			return new PageResult(false, "保存失败：" + e.getMessage());
		}
	}
	
	/**
	 * 获取组织机构树
	 * 
	 * v1.0 zhanghc 2018年5月31日下午10:07:39
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/authOrgOrgTreeList")
	@ResponseBody
	public List<Map<String, Object>> authOrgOrgTreeList(Integer id) {
		try {
			List<Map<String, Object>> orgTreeList = paperTypeService.getOrgTreeList();
			PaperTypeAuth paperTypeAuth = paperTypeService.getPaperTypeAuth(id);
			if(paperTypeAuth == null){
				return orgTreeList;
			}
			
			String orgIds = paperTypeAuth.getOrgIds();
			if(!ValidateUtil.isValid(orgIds)){
				return orgTreeList;
			}
			
			for(Map<String, Object> map : orgTreeList){
				String orgId = map.get("ID").toString();
				if(orgIds.contains("," + orgId + ",")){
					map.put("CHECKED", true);
				}
			}
			return orgTreeList;
		} catch (Exception e) {
			log.error("获取组织机构树错误：", e);
			return new ArrayList<Map<String,Object>>();
		}
	}
	
	/**
	 * 获取机构岗位树
	 * 
	 * v1.0 zhanghc 2018年5月31日下午10:07:39
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/authPostOrgTreeList")
	@ResponseBody
	public List<Map<String, Object>> authPostOrgTreeList(Integer id) {
		try {
			List<Map<String, Object>> orgPostTree = paperTypeService.getOrgPostTreeList();
			PaperTypeAuth paperTypeAuth = paperTypeService.getPaperTypeAuth(id);
			if(paperTypeAuth == null){
				return orgPostTree;
			}
			
			String postIds = paperTypeAuth.getPostIds();
			if(!ValidateUtil.isValid(postIds)){
				return orgPostTree;
			}
			
			for(Map<String, Object> map : orgPostTree){
				String type = map.get("TYPE").toString();
				if(!"POST".equals(type)){
					continue;
				}
				
				String postId = map.get("ID").toString();
				if(postIds.contains("," + postId + ",")){
					map.put("CHECKED", true);
				}
			}
			
			return orgPostTree;
		} catch (Exception e) {
			log.error("获取机构岗位树错误：", e);
			return new ArrayList<Map<String,Object>>();
		}
	}
	
	/**
	 * 完成保存权限岗位
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param id
	 * @param orgIds
	 * @param syn2Sub
	 * @return PageResult
	 */
	@RequestMapping("/doAuthPostUpdate")
	@ResponseBody
	public PageResult doAuthPostUpdate(Integer id, Integer[] postIds, boolean syn2Sub) {
		try {
			paperTypeService.doAuthPostUpdate(id, postIds, syn2Sub, getCurrentUser());
			return new PageResult(true, "保存成功");
		} catch (Exception e) {
			log.error("完成保存权限岗位错误：", e);
			return new PageResult(false, "保存失败：" + e.getMessage());
		}
	}
}
