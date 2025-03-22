package com.wcpdoc.exam.api.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.ex.PaperPart;
import com.wcpdoc.exam.core.entity.ex.QuestionPart;
import com.wcpdoc.exam.core.service.ExamCacheService;
import com.wcpdoc.exam.core.service.MyPaperService;
import com.wcpdoc.exam.report.service.ReportService;
import com.wcpdoc.file.service.FileService;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 成绩报表控制层
 * 
 * v1.0 zhanghc 2017年8月29日下午2:03:36
 */
@RestController
@RequestMapping("/api/report")
@Slf4j
public class ApiReportController extends BaseController {

	@Resource
	private ReportService reportService;
	@Resource
	private BaseCacheService baseCacheService;
	@Resource
	private ExamCacheService examCacheService;
	@Resource
	private MyPaperService myPaperService;
	@Resource
	private FileService fileService;

	/**
	 * 用户首页
	 * 
	 * v1.0 chenyun 2021年12月10日上午10:14:34
	 * 
	 * @return PageResult
	 */
	@RequestMapping("/user/home")
	public PageResult userHome() {
		try {
			return PageResultEx.ok().data(reportService.userHome());
		} catch (MyException e) {
			log.error("用户首页错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("用户首页错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 管理员首页
	 * 
	 * v1.0 chenyun 2021年12月10日上午10:14:34
	 * 
	 * @return PageResult
	 */
	@RequestMapping("/admin/home")
	public PageResult adminHome() {
		try {
			return PageResultEx.ok().data(reportService.adminHome());
		} catch (MyException e) {
			log.error("管理员首页错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("管理员首页错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 子管理员首页
	 * 
	 * v1.0 chenyun 2021年12月10日上午10:14:34
	 * 
	 * @return PageResult
	 */
	@RequestMapping("/subAdmin/home")
	public PageResult subAdminHome() {
		try {
			return PageResultEx.ok().data(reportService.subAdminHome());
		} catch (MyException e) {
			log.error("子管理员首页错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("子管理员首页错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 阅卷用户首页
	 * 
	 * v1.0 chenyun 2021年12月10日上午10:14:34
	 * 
	 * @return PageResult
	 */
	@RequestMapping("/markUser/home")
	public PageResult markUserHome() {
		try {
			return PageResultEx.ok().data(reportService.markUserHome());
		} catch (MyException e) {
			log.error("阅卷用户首页错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("阅卷用户首页错误：", e);
			return PageResult.err();
		}
	}

//    /**
//     * 服务器参数 // 第三方dll有bug
//     * 
//     * v1.0 chenyun 2021年12月10日上午10:14:34
//     * @return PageResult
//     */
//    @RequestMapping("/server/parm")
//    public PageResult serverParm() {
//        try {
//        	return null;
////            return PageResultEx.ok().data(serverParmService.getList());
//        } catch (MyException e) {
//            log.error("首页服务器参数统计错误：{}", e.getMessage());
//            return PageResult.err().msg(e.getMessage());
//        } catch (Exception e) {
//            log.error("首页服务器参数统计错误：", e);
//            return PageResult.err();
//        }
//    }

//	/**
//	 * 慢接口日志
//	 * 
//	 * v1.0 chenyun 2021-12-15 13:49:29
//	 * 
//	 * @return PageResult
//	 */
//	@RequestMapping("/server/log")
//	public PageResult serverLog() {
//		try {
//			return PageResultEx.ok().data(reportService.serverLog());
//		} catch (MyException e) {
//			log.error("首页慢接口日志统计错误：{}", e.getMessage());
//			return PageResult.err().msg(e.getMessage());
//		} catch (Exception e) {
//			log.error("首页慢接口日志统计错误：", e);
//			return PageResult.err();
//		}
//	}

	/**
	 * 试题统计
	 * 
	 * v1.0 chenyun 2021-12-15 13:44:47
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/question/statis")
	public PageResult questionStatis(Integer questionBankId) {
		try {
			return PageResultEx.ok().data(reportService.questionStatis(questionBankId));
		} catch (MyException e) {
			log.error("试题统计错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("试题统计错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 考试统计
	 * 
	 * v1.0 chenyun 2021-12-15 13:44:47
	 * 
	 * @param examId
	 * @return PageResult
	 */
	@RequestMapping("/exam/statis")
	public PageResult examStatis(Integer examId) {
		try {
			return PageResultEx.ok().data(reportService.examStatis(examId));
		} catch (MyException e) {
			log.error("考试统计错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("考试统计错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 考试排名
	 * 
	 * v1.0 chenyun 2021-12-15 13:44:47
	 * 
	 * @return PageResult
	 */
	@RequestMapping("/exam/rankListpage")
	public PageResult examRankListpage(PageIn pageIn) {
		try {// 不校验，任何时候都能查询
			PageOut pageOut = reportService.examRankListpage(pageIn);
			for (Map<String, Object> map : pageOut.getList()) {
				Integer examMarkType = (Integer) map.get("examMarkType");
				Integer examMarkState = (Integer) map.get("examMarkState");
				if (examMarkType == 2 && examMarkState != 3) {// 如果是主观题试卷，且考试未结束，不显示分数
					map.put("myExamTotalScore", null);// 阅完一张会打分，但是会二次修改分数，不应该显示中间状态
				}
			}
			return PageResultEx.ok().data(pageOut);
		} catch (MyException e) {
			log.error("考试排名错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("考试排名错误：", e);
			return PageResult.err();
		}
	}

//	/**
//	 * 错题分析
//	 * 
//	 * v1.0 chenyun 2021-12-15 13:44:47
//	 * 
//	 * @return PageResult
//	 */
//    @RequestMapping("/exam/questionErrList")
//    public PageResult questionErrList(Integer examId) {
//        try {
//            return PageResultEx.ok().data(reportService.questionErrList(examId));
//        } catch (MyException e) {
//            log.error("错题分析统计错误：{}", e.getMessage());
//            return PageResult.err().msg(e.getMessage());
//        } catch (Exception e) {
//            log.error("错题分析统计错误：", e);
//            return PageResult.err();
//        }
//    }

	/**
	 * 导出试卷
	 * 
	 * v1.0 zhanghc 2025年3月21日下午12:05:18
	 * 
	 * @param examId
	 * @param userId
	 * @return PageResult
	 */
	@RequestMapping("/paper/exportPDF")
	public void paper(Integer examId, Integer userId) {
		try {
			// 数据校验
			MyExam myExam = examCacheService.getMyExam(examId, userId);
			if (myExam.getMarkState() == 1) {
				throw new MyException("未阅卷");
			}
			if (myExam.getMarkState() == 2) {
				throw new MyException("阅卷中");
			}
			Exam exam = examCacheService.getExam(examId);
			if (getCurUser().getType() == 2 && exam.getCreateUserId().intValue() != getCurUser().getId().intValue()) {
				throw new MyException("无权限");
			}

			// 获取试卷数据
			Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
			cfg.setClassLoaderForTemplateLoading(this.getClass().getClassLoader(), "/templates");
			// cfg.setDirectoryForTemplateLoading(new
			// File("D:\\soft\\eclipse\\workspace\\EXAM\\exam\\h5\\public"));
			cfg.setDefaultEncoding("UTF-8");
			cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
			cfg.setLogTemplateExceptions(true);
			cfg.setWrapUncheckedExceptions(true);

			User user = baseCacheService.getUser(userId);
			List<PaperPart> paperPartList = myPaperService.generatePaper(examId, userId, true, false);
			for (PaperPart paperPart : paperPartList) {
				if (paperPart.getType() == 2) {
					QuestionPart questionPart = (QuestionPart) paperPart;
					if (questionPart.getQuestionType() == 3) {
						Pattern pattern = Pattern.compile("_{5,}");
						Matcher matcher = pattern.matcher(questionPart.getTitle());
						StringBuffer title = new StringBuffer();
						int pos = 0;
						while (matcher.find()) {
							String userAnswer = "";
							if (questionPart.getUserAnswers().size() > pos) {
								userAnswer = questionPart.getUserAnswers().get(pos);
							}
							Integer inputLen = (matcher.end() - matcher.start()) * 14;
							String replacement = String.format(
									"<div class=\"fillblank\" style=\"width: %dpx;\"><input class=\"fillblank__txt\" value=\"%s\" /><div class=\"fillblank__underline\"></div></div>",
									inputLen, userAnswer);
							matcher.appendReplacement(title, replacement);
							pos++;
						}
						matcher.appendTail(title);
						questionPart.setTitle(title.toString());
					}
				}
			}

			// 填充模板
			Map<String, Object> data = new HashMap<>();
			data.put("user", user);
			data.put("exam", exam);
			data.put("myExam", myExam);
			data.put("paperPartList", paperPartList);

			File tempDir = fileService.createTempDirWithYMD();
			String fileName = UUID.randomUUID().toString();
			File tempHtmlFile = new File(tempDir, fileName + ".html");
			Template template = cfg.getTemplate("paper.html");
			
			try (Writer writer = new FileWriter(tempHtmlFile)) {// 手动关闭流
				template.process(data, writer);
			}

			// html文件转pdf
			String WK_PATH = "wkhtmltopdf";
			File tempPdfFile = new File(tempDir, fileName + ".pdf");
			ProcessBuilder processBuilder = new ProcessBuilder(WK_PATH, tempHtmlFile.getAbsolutePath(),
					tempPdfFile.getAbsolutePath());
			processBuilder.redirectErrorStream(true);
			Process process = processBuilder.start();
			boolean success = process.waitFor(10, TimeUnit.SECONDS);
			if (!success) {
				throw new MyException("PDF生成超时");
			}

			StringBuilder consoleLog = new StringBuilder();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					consoleLog.append(line);
				}
			}
			if (!consoleLog.toString().contains("Done")) {
				throw new MyException(consoleLog.toString());
			}

			// 下载到客户端
			response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
			String encodedName = URLEncoder.encode(String.format("%s（%s）.pdf", exam.getPaperName(), user.getName()), "UTF-8").replace("+", "%20"); 
			response.setHeader("Content-Disposition", "attachment;filename=" + encodedName);

			response.setContentType("application/force-download");
			response.setCharacterEncoding("UTF-8");
			try (OutputStream output = response.getOutputStream()) {
				FileUtils.copyFile(tempPdfFile, output);
			} catch (Exception e) {
				throw new MyException("拷贝文件错误");
			}

		} catch (MyException e) {
			log.error("导出试卷错误：{}", e.getMessage());
		} catch (Exception e) {
			if (e.getMessage().contains("Cannot run program \"wkhtmltopdf\"")) {
				log.error("导出试卷错误：{}", "请联系管理员安装wkhtmltopdf");
			}

			log.error("导出试卷错误：", e);
		}
	}

}