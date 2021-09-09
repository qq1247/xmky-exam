package com.wcpdoc.exam.api.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.base.cache.DictCache;
import com.wcpdoc.exam.core.constant.ConstantManager;
import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.QuestionAnswerService;
import com.wcpdoc.exam.core.service.QuestionOptionService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionTypeService;
import com.wcpdoc.exam.file.service.FileService;

/**
 * 试题控制层
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
@Controller
@RequestMapping("/api/question")
public class ApiQuestionController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiQuestionController.class);

	@Resource
	private QuestionService questionService;
	@Resource
	private QuestionTypeService questionTypeService;
	@Resource
	private QuestionOptionService questionOptionService;
	@Resource
	private QuestionAnswerService questionAnswerService;
	@Resource
	private FileService fileService;
	
	/**
	 * 试题列表 
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @return pageOut
	 */
	@RequestMapping("/listpage")
	@ResponseBody
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
	public PageResult listpage() {
		try {
			PageIn pageIn = new PageIn(request);
			if(!ConstantManager.ADMIN_LOGIN_NAME.equals(getCurUser().getLoginName())) {
				pageIn.addAttr("curUserId", getCurUser().getId());
			}
			
			PageOut listpage = questionService.getListpage(pageIn);
			for(Map<String, Object> map : listpage.getList()){
				map.put("typeName", DictCache.getDictValue("QUESTION_TYPE", map.get("type").toString()));
				map.put("difficultyName", DictCache.getDictValue("QUESTION_DIFFICULTY", map.get("difficulty").toString()));
				
				if (map.get("type").toString().equals("1") || map.get("type").toString().equals("2")) {
					List<QuestionOption> optionList = questionOptionService.getList(Integer.valueOf(map.get("id").toString()));
					map.put("option", optionList);
				}
			}
			return PageResultEx.ok().data(listpage);
		} catch (Exception e) {
			log.error("试题列表错误：", e);
			return PageResult.err();
		}
	}
	
	
	
	/**
	 * 随机试题列表 
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @return pageOut
	 */
	@RequestMapping("/randomListpage")
	@ResponseBody
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
	public PageResult randomListpage() {
		try {
			PageIn pageIn = new PageIn(request);
			PageOut pageOut = questionService.randomListpage(pageIn);
			for(Map<String, Object> map : pageOut.getList()){
				map.put("typeName", DictCache.getDictValue("QUESTION_TYPE", map.get("type").toString()));
				map.put("difficultyName", DictCache.getDictValue("QUESTION_DIFFICULTY", map.get("difficulty").toString()));
				
				if (map.get("type").toString().equals("1") || map.get("type").toString().equals("2")) {
					List<QuestionOption> optionList = questionOptionService.getList(Integer.valueOf(map.get("id").toString()));
					map.put("option", optionList);
				}
			}
			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("试题列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 添加试题
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @return pageOut
	 */
	@RequestMapping("/add")
	@ResponseBody
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
	public PageResult add(Question question, Integer[] scoreOptions, String[] answers, String[] options, BigDecimal[] scores) {
		try {
			questionService.addAndUpdate(question, scoreOptions, answers, options, scores);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("添加试题错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		}  catch (Exception e) {
			log.error("添加试题错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 修改试题
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @param question
	 * @param answers
	 * @param options
	 * @param newVer
	 * @return PageResult
	 */
	@RequestMapping("/edit")
	@ResponseBody
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
	public PageResult edit(Question question, Integer[] scoreOptions, String[] answers, String[] options, BigDecimal[] scores) {  //, boolean newVer
		try {
			questionService.updateAndUpdate(question, scoreOptions, answers, options, scores); //, newVer
			return PageResult.ok();
		} catch (MyException e) {
			log.error("修改试题错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		}  catch (Exception e) {
			log.error("修改试题错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 删除试题
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @param id
	 * @return pageOut
	 */
	@RequestMapping("/del")
	@ResponseBody
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
	public PageResult del(Integer id) {
		try {
			questionService.delAndUpdate(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("删除试题错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		}  catch (Exception e) {
			log.error("删除试题错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 获取试题
	 * 拥有读写权限才显示答案字段
	 * 
	 * v1.0 zhanghc 2021年7月1日下午2:18:05
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/get")
	@ResponseBody
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
	public PageResult get(Integer id) {
		try {
			Question question = questionService.getEntity(id);
			List<String> optionList = new ArrayList<>();
			if (question.getType() == 1 || question.getType() == 2) {
				List<QuestionOption> questionOptionList = questionOptionService.getList(question.getId());
				for (QuestionOption questionOption : questionOptionList) {
					optionList.add(questionOption.getOptions());
				}
			}
			QuestionType questionType = questionTypeService.getEntity(question.getQuestionTypeId());
			boolean writeAuth = questionTypeService.hasWriteAuth(questionType, getCurUser().getId());		
			boolean readAuth = questionTypeService.hasReadAuth(questionType, getCurUser().getId());
			
			List<QuestionAnswer> questionAnswerList = questionAnswerService.getList(question.getId());
			List<Map<String, Object>> questionAnswerSplitList = new ArrayList<Map<String, Object>>();
			if (question.getType() == 3) {
				for(QuestionAnswer questionAnswer : questionAnswerList){
					Map<String, Object> map = new HashMap<String, Object>();
					String[] split = questionAnswer.getAnswer().split("\n");
					map.put("id", questionAnswer.getId());
					map.put("answer", split);
					map.put("score", questionAnswer.getScore());
					map.put("questionId", questionAnswer.getQuestionId());
					questionAnswerSplitList.add(map);
				}
			} else if (question.getType() == 5 && question.getAi() == 1) {
				for(QuestionAnswer questionAnswer : questionAnswerList){					
					Map<String, Object> map = new HashMap<String, Object>();
					String[] split = questionAnswer.getAnswer().split("\n");
					map.put("id", questionAnswer.getId());
					map.put("answer", split);
					map.put("score", questionAnswer.getScore());
					map.put("questionId", questionAnswer.getQuestionId());
					questionAnswerSplitList.add(map);
				}
			} else {
				for(QuestionAnswer questionAnswer : questionAnswerList){
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", questionAnswer.getId());
					map.put("answer", questionAnswer.getAnswer());
					map.put("score", questionAnswer.getScore());
					map.put("questionId", questionAnswer.getQuestionId());
					questionAnswerSplitList.add(map);
				}
			}
			PageResultEx pageResult = PageResultEx.ok()
					.addAttr("id", question.getId())
					.addAttr("type", question.getType())
					.addAttr("ai", question.getAi())
					.addAttr("difficulty", question.getDifficulty())
					.addAttr("title", question.getTitle())
					.addAttr("analysis", question.getAnalysis())
					.addAttr("state", question.getState())
					.addAttr("questionTypeId", question.getQuestionTypeId())
					.addAttr("score", question.getScore())
					.addAttr("scoreOptions", question.getScoreOptions())
					.addAttr("no", question.getNo())
					.addAttr("options", optionList.toArray(new String[optionList.size()]))
					.addAttr("answers", (writeAuth || readAuth) ? questionAnswerSplitList : new String[0]);
			return pageResult;
		} catch (MyException e) {
			log.error("获取试题错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		}  catch (Exception e) {
			log.error("获取试题错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 拷贝试题
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/copy")
	@ResponseBody
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
	public PageResult copy(Integer id) {
		try {
			questionService.copy(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("复制试题错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		}  catch (Exception e) {
			log.error("复制试题错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * word试题导入
	 * 
	 * v1.0 zhanghc 2019年8月10日下午5:12:53
	 * @param file
	 * @param questionTypeId
	 * @return PageResult
	 */
	@RequestMapping("/wordImp")
	@ResponseBody
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
	public PageResult wordImp(Integer fileId, Integer questionTypeId) {
		try {
			String processBarId = UUID.randomUUID().toString().replaceAll("-", "");
			
			/*new Thread(new Runnable() {
				public void run() {
					SpringUtil.getBean(QuestionService.class).wordImp(fileId, questionTypeId, processBarId);
				}
			}).start();*/
			
			questionService.wordImp(fileId, questionTypeId, processBarId);
			return PageResultEx.ok().data(processBarId);
		} catch (MyException e) {
			log.error("导入试题错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("导入试题错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * word模板导出
	 * 
	 * v1.0 zhanghc 2019年8月14日下午5:33:20 
	 * void
	 */
	@RequestMapping(value = "/wordTemplateExport")
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
	public void wordTemplateExport() {
		try {
			fileService.exportTemplate("试题模板.docx");
		} catch (MyException e) {
			log.error("下载模板失败：{}", e.getMessage());
		} catch (Exception e) {
			log.error("下载模板失败：", e);
		}
	}
	
	/**
	 * 发布
	 * 
	 * v1.0 zhanghc 2018年11月24日上午9:13:22
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/publish")
	@ResponseBody
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
	public PageResult publish(Integer id) {
		try {
			questionService.publish(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("发布错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("发布错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * word试题导出
	 * 
	 * v1.0 zhanghc 2019年8月14日下午5:33:20 
	 * void
	 */
//	@RequestMapping(value = "/wordQuestionExport")
//	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
//	public Map<String, String> wordQuestionExport(String ids) {
//		try {
//		/*if (ids != null) {
//		Map<String, String> map = new HashMap<>();
//			throw new RuntimeException("参数无效：dbAddr");
//		}*/
//	
//		Question entity = questionService.getEntity(26);
//		List<QuestionAnswer> questionAnswerList = questionAnswerService.getList(entity.getId());
//		
//		Map<String, Object> mapList = new HashMap<String, Object>();
//		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("typeName", "单选");
//		map.put("difficultyName", "简单");
//		map.put("title", entity.getTitle());
//		
//		map.put("optionA", "AAAAAAAAA");
//		map.put("optionB", "BBBBBBBBB");
//		map.put("optionC", "CCCCCCCCC");
//		map.put("optionD", "DDDDDDDDD");
//		map.put("optionE", "EEEEEEEEEE");
//		map.put("optionF", "FFFFFFFFFF");
//		map.put("optionG", "GGGGGGGGGG");
//		
//		map.put("answer", questionAnswerList);
//		map.put("score", "3.0");
//		map.put("scoreOptions", "半对半错");
//		map.put("analysis", entity.getAnalysis());
//		list.add(map);
//		mapList.put("list", list);
//		
//		Template template = null;
//		try {
//			//设置模板相对路径
//			File file = new File(this.getClass().getResource("/").toURI().getPath() +"/res/");
//			
//			Configuration configuration =  new Configuration(Configuration.VERSION_2_3_22);
//			configuration.setTemplateLoader(new FileTemplateLoader(file));
//			configuration.setDefaultEncoding("UTF-8");
//			configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
//			configuration.setLogTemplateExceptions(false);
//			configuration.setWrapUncheckedExceptions(true);
//			
//			template = configuration.getTemplate("template.doc.ftl");
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		File outFile = new File("D:/outFile/outFile" + Math.random() * 10000 + ".docx"); // 导出文件
//		Writer out = null;
//		try {
//			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile)));
//		} catch (FileNotFoundException e1) {
//			e1.printStackTrace();
//		}
//
//		try {
//			template.process(mapList, out); // 将填充数据填入模板文件并输出到目标文件
//		} catch (TemplateException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//			return null;
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
//			return null;
//	}
	
	/**
	 * 试题统计（类型，难易程度）
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @param id
	 * @return pageOut
	 */
	@RequestMapping("/statistics")
	@ResponseBody
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
	public PageResult statistics(Integer questionTypeId) {
		try {
			return PageResultEx.ok().data(questionService.statisticsTypeDifficulty(questionTypeId));
		} catch (MyException e) {
			log.error("试题统计错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		}  catch (Exception e) {
			log.error("试题统计错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 试题准确率
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @param id
	 * @return pageOut
	 */
	@RequestMapping("/accuracy")
	@ResponseBody
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
	public PageResult accuracy(Integer examId) {
		try {
			return PageResultEx.ok().data(questionService.accuracy(examId));
		} catch (MyException e) {
			log.error("试题统计错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		}  catch (Exception e) {
			log.error("试题统计错误：", e);
			return PageResult.err();
		}
	}
}