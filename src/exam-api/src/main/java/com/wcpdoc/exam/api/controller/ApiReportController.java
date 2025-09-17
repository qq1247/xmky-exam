package com.wcpdoc.exam.api.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.base.entity.Dict;
import com.wcpdoc.base.entity.Org;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.base.service.OrgService;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.base.util.CurLoginUserUtil;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.SpringUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.Exer;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyExerTrackMonthly;
import com.wcpdoc.exam.core.entity.QuestionBank;
import com.wcpdoc.exam.core.entity.ex.PaperPart;
import com.wcpdoc.exam.core.entity.ex.QuestionPart;
import com.wcpdoc.exam.core.service.ExamCacheService;
import com.wcpdoc.exam.core.service.ExerService;
import com.wcpdoc.exam.core.service.MyExerQuestionService;
import com.wcpdoc.exam.core.service.MyExerTrackMonthlyService;
import com.wcpdoc.exam.core.service.MyPaperService;
import com.wcpdoc.exam.core.service.QuestionBankService;
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
	@Resource
	private ExerService exerService;
	@Resource
	private UserService userService;
	@Resource
	private QuestionBankService questionBankService;
	@Resource
	private MyExerTrackMonthlyService myExerTrackMonthlyService;
	@Resource
	private OrgService orgService;
	@Resource
	private MyExerQuestionService myExerQuestionService;

	@Value("classpath:templates/prism.js")
	private org.springframework.core.io.Resource prismJsResource;
	@Value("classpath:templates/prism.css")
	private org.springframework.core.io.Resource prismCssResource;

	static Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
	static {
		cfg.setClassLoaderForTemplateLoading(ApiReportController.class.getClassLoader(), "/templates");
		// cfg.setDirectoryForTemplateLoading(new
		// File("D:\\soft\\eclipse\\workspace\\EXAM\\exam\\h5\\public"));
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		cfg.setLogTemplateExceptions(true);
		cfg.setWrapUncheckedExceptions(true);
	}

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

	/**
	 * 用户试卷导出
	 * 
	 * v1.0 zhanghc 2025年3月21日下午12:05:18
	 * 
	 * @param examId
	 * @param userId
	 * @return PageResult
	 */
	@RequestMapping("/paper/exportPDF")
	public void paperExportPDF(Integer examId, Integer userId) {
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
			data.put("host", String.format("http://127.0.0.1:%s",
					SpringUtil.getBean(Environment.class).getProperty("server.port", String.class)));

			File tempDir = fileService.createTempDirWithYMD();
			String fileName = UUID.randomUUID().toString();
			File tempHtmlFile = new File(tempDir, fileName + ".html");
			Template template = cfg.getTemplate("user-paper.html");

			try (Writer writer = new OutputStreamWriter(new FileOutputStream(tempHtmlFile), StandardCharsets.UTF_8)) {// 手动关闭流
				template.process(data, writer);
			}

			File prismJs = new File(tempDir, "prism.js");
			File prismCss = new File(tempDir, "prism.css");
			if (!prismJs.exists()) {
				FileUtils.copyInputStreamToFile(prismJsResource.getInputStream(), prismJs);
			}
			if (!prismCss.exists()) {
				FileUtils.copyInputStreamToFile(prismCssResource.getInputStream(), prismCss);
			}

			{// 如果有代码片段，则高亮并带行号（wkhtmltopdf对js支持不友好，比如代码高亮的背景色、行号，它更适合纯html解析
				Document doc = Jsoup.parse(new String(Files.readAllBytes(tempHtmlFile.toPath())));
				Elements titles = doc.select(".question__title");

				ScriptEngineManager manager = new ScriptEngineManager();
				ScriptEngine engine = manager.getEngineByName("nashorn");
				try (FileReader reader = new FileReader(prismJs)) {
					engine.eval(reader);

					for (Element title : titles) {
						Matcher matcher = Pattern.compile("```([a-z]*)<br>([\\s\\S]*?)<br>```").matcher(title.html());
						StringBuffer result = new StringBuffer();

						while (matcher.find()) {
							String lang = matcher.group(1);
							String code = matcher.group(2).replaceAll("<br>", "\n");

							engine.put("lang", lang);
							engine.put("code", code);
							String higCode = (String) engine.eval(
									"Prism.highlight(code, Prism.languages[lang] || Prism.languages.plaintext, lang);");
							int lineCount = higCode.split("\n").length;
							StringBuilder lineNumbersRows = new StringBuilder();
							lineNumbersRows.append("<span aria-hidden=\"true\" class=\"line-numbers-rows\">");
							for (int i = 0; i < lineCount; i++) {
								lineNumbersRows.append("<span></span>");
							}
							lineNumbersRows.append("</span>");
							matcher.appendReplacement(result,
									"<pre class=\"line-numbers language-java\"><code class=\"match-braces language-java\">"
											+ higCode + lineNumbersRows.toString() + "</code></pre>");
						}
						matcher.appendTail(result);
						title.html(result.toString());
					}
				}
				Files.write(tempHtmlFile.toPath(), doc.outerHtml().getBytes(StandardCharsets.UTF_8));
			}

			// html文件转pdf
			String WK_PATH = "wkhtmltopdf";
			File tempPdfFile = new File(tempDir, fileName + ".pdf");
			ProcessBuilder processBuilder = new ProcessBuilder(WK_PATH, "--enable-local-file-access",
					tempHtmlFile.getAbsolutePath(), tempPdfFile.getAbsolutePath());
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
			String encodedName = URLEncoder
					.encode(String.format("%s（%s）.pdf", exam.getPaperName(), user.getName()), "UTF-8")
					.replace("+", "%20");
			response.setHeader("Content-Disposition", "attachment;filename=" + encodedName);

			response.setContentType("application/force-download");
			response.setCharacterEncoding("UTF-8");
			try (OutputStream output = response.getOutputStream()) {
				FileUtils.copyFile(tempPdfFile, output);
			} catch (Exception e) {
				throw new MyException("拷贝文件错误");
			}

		} catch (MyException e) {
			log.error("用户试卷导出错误：{}", e.getMessage());
		} catch (Exception e) {
			if (e.getMessage().contains("wkhtmltopdf")) {
				log.error("用户试卷导出错误：{}", "请联系管理员安装wkhtmltopdf");
				return;
			}

			log.error("用户试卷导出错误：", e);
		}
	}

	/**
	 * 考试排名导出
	 * 
	 * v1.0 zhanghc 2025年4月2日下午5:05:31
	 * 
	 * @param examId void
	 */
	@RequestMapping("/rank/exportPDF")
	public void rankExportPDF(Integer examId, String userName, String orgName) {
		try {
			// 数据校验
			Exam exam = examCacheService.getExam(examId);
			if (exam.getMarkState() == 1) {
				throw new MyException("未阅卷");
			}
			if (exam.getMarkState() == 2) {
				throw new MyException("阅卷中");
			}
			if (getCurUser().getType() == 2 && exam.getCreateUserId().intValue() != getCurUser().getId().intValue()) {
				throw new MyException("无权限");// 只有0、2有url权限，这里判断的是子管理员是否有数据权限
			}

			// 获取考试排名数据
			PageIn pageIn = new PageIn();
			pageIn.addParm("examId", examId);
			if (ValidateUtil.isValid(userName)) {
				pageIn.addParm("userName", userName);
			}
			if (ValidateUtil.isValid(orgName)) {
				pageIn.addParm("orgName", orgName);
			}
			List<Map<String, Object>> rankList = new ArrayList<>();
			int curPage = 1;
			Map<Integer, String> examStateCache = baseCacheService.getDictList().stream()
					.filter(dict -> "EXAM_STATE".equals(dict.getDictIndex()))
					.collect(Collectors.toMap(dict -> Integer.parseInt(dict.getDictKey()), Dict::getDictValue));
			Map<Integer, String> markStateCache = baseCacheService.getDictList().stream()
					.filter(dict -> "MARK_STATE".equals(dict.getDictIndex()))
					.collect(Collectors.toMap(dict -> Integer.parseInt(dict.getDictKey()), Dict::getDictValue));
			Map<Integer, String> answerStateCache = baseCacheService.getDictList().stream()
					.filter(dict -> "ANSWER_STATE".equals(dict.getDictIndex()))
					.collect(Collectors.toMap(dict -> Integer.parseInt(dict.getDictKey()), Dict::getDictValue));
			while (true) {
				pageIn.setCurPage(curPage++);
				pageIn.setPageSize(100);
				PageOut pageOut = reportService.examRankListpage(pageIn);
				rankList.addAll(pageOut.getList().stream().map(data -> {
					data.put("myExamAnswerStateName", answerStateCache.get(data.get("myExamAnswerState")));
					data.put("myExamStateName", examStateCache.get(data.get("myExamState")));
					data.put("myExamMarkStateName", markStateCache.get(data.get("myExamMarkState")));
					if (data.get("myExamAnswerStartTime") != null && data.get("myExamAnswerEndTime") != null) {
						data.put("answerTime", DateUtil.diffMinute(
								Date.from(
										((LocalDateTime) data.get("myExamAnswerStartTime")).toInstant(ZoneOffset.UTC)),
								Date.from(((LocalDateTime) data.get("myExamAnswerStartTime"))
										.toInstant(ZoneOffset.UTC))));
					} else {
						data.put("answerTime", "-");
					}

					if (data.get("myExamStartMarkime") != null && data.get("myExamEndMarkime") != null) {
						data.put("answerTime", DateUtil.diffMinute(
								Date.from(((LocalDateTime) data.get("myExamStartMarkime")).toInstant(ZoneOffset.UTC)),
								Date.from(((LocalDateTime) data.get("myExamEndMarkime")).toInstant(ZoneOffset.UTC))));
					} else {
						data.put("markTime", "-");
					}

					return data;
				}).collect(Collectors.toList()));

				if (rankList.size() >= pageOut.getTotal()) {
					break;
				}
			}

			// 填充模板
			Map<String, Object> data = new HashMap<>();
			data.put("exam", exam);
			data.put("rankList", rankList);

			File tempDir = fileService.createTempDirWithYMD();
			String fileName = UUID.randomUUID().toString();
			File tempHtmlFile = new File(tempDir, fileName + ".html");
			Template template = cfg.getTemplate("exam-rank.html");

			try (Writer writer = new OutputStreamWriter(new FileOutputStream(tempHtmlFile), StandardCharsets.UTF_8)) {// 手动关闭流
				template.process(data, writer);
			}

			// html文件转pdf
			String WK_PATH = "wkhtmltopdf";
			File tempPdfFile = new File(tempDir, fileName + ".pdf");
			ProcessBuilder processBuilder = new ProcessBuilder(WK_PATH, "--enable-local-file-access",
					tempHtmlFile.getAbsolutePath(), tempPdfFile.getAbsolutePath());
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
			String encodedName = URLEncoder.encode(String.format("%s（%s）.pdf", exam.getName(), "考试排名"), "UTF-8")
					.replace("+", "%20");
			response.setHeader("Content-Disposition", "attachment;filename=" + encodedName);

			response.setContentType("application/force-download");
			response.setCharacterEncoding("UTF-8");
			try (OutputStream output = response.getOutputStream()) {
				FileUtils.copyFile(tempPdfFile, output);
			} catch (Exception e) {
				throw new MyException("拷贝文件错误");
			}

		} catch (MyException e) {
			log.error("导出考试排名错误：{}", e.getMessage());
		} catch (Exception e) {
			if (e.getMessage().contains("wkhtmltopdf")) {
				log.error("导出考试排名错误：{}", "请联系管理员安装wkhtmltopdf");
				return;
			}

			log.error("导出考试排名错误：", e);
		}
	}

	/**
	 * 练习跟踪列表
	 * 
	 * v1.0 zhanghc 2025年9月13日下午10:20:16
	 * 
	 * @param examId
	 * @param userId void
	 */
	@RequestMapping("/exer/trackList")
	public PageResult exerTrackList(Integer exerId, String startYm, String endYm) {
		try {
			// 数据校验
			if (!ValidateUtil.isValid(exerId)) {
				throw new MyException("参数错误：exerId");
			}
			if (!ValidateUtil.isValid(startYm)) {
				throw new MyException("参数错误：startYm");
			}
			if (!ValidateUtil.isValid(endYm)) {
				throw new MyException("参数错误：endYm");
			}
			Date startDate = DateUtil.getDate(startYm + "-01");
			Date endDate = DateUtil.getDate(endYm + "-01");
			int diffDay = DateUtil.diffMonth(startDate, endDate);
			if (diffDay > 12) {
				throw new MyException("查询时间范围不得超过12个月");
			}

			Exer exer = exerService.getById(exerId);
			QuestionBank questionBank = questionBankService.getById(exer.getQuestionBankId());
			if (CurLoginUserUtil.isExamUser() || CurLoginUserUtil.isMarkUser()) {
				throw new MyException("无操作权限");
			}
			if (CurLoginUserUtil.isSubAdmin()
					&& getCurUser().getId().intValue() != questionBank.getCreateUserId().intValue()) {
				throw new MyException("无操作权限");
			}

			// 查询我的练习跟踪月度列表
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
			YearMonth start = YearMonth.parse(startYm, formatter);
			YearMonth end = YearMonth.parse(endYm, formatter);
			List<Integer> allYmList = new ArrayList<>();
			for (YearMonth ym = start; !ym.isAfter(end); ym = ym.plusMonths(1)) {
				allYmList.add(Integer.parseInt(ym.format(DateTimeFormatter.ofPattern("yyyyMM")))); // [202409,202405...202509]
			}

			List<MyExerTrackMonthly> myExerTrackMonthlyList = myExerTrackMonthlyService.getList(exerId,
					Integer.parseInt(startYm.replace("-", "")), Integer.parseInt(endYm.replace("-", "")));
			Map<Integer, List<MyExerTrackMonthly>> myExerTrackMonthlyGroup = myExerTrackMonthlyList.stream()
					.collect(Collectors.groupingBy(MyExerTrackMonthly::getUserId));// [2:[{ym:202405,minuteCount:6,updateUserId:1},{ym:202407,minuteCount:5,updateUserId:1}]]

			Map<Integer, List<Map<String, Object>>> result = new HashMap<>();
			for (Map.Entry<Integer, List<MyExerTrackMonthly>> entry : myExerTrackMonthlyGroup.entrySet()) {
				Integer userId = entry.getKey();
				List<MyExerTrackMonthly> _myExerTrackMonthly = entry.getValue();
				Map<Integer, MyExerTrackMonthly> _myExerTrackMonthlyCache = _myExerTrackMonthly.stream()
						.collect(Collectors.toMap(MyExerTrackMonthly::getYm, r -> r));
				List<Map<String, Object>> resultList = new ArrayList<>();
				for (Integer ym : allYmList) {
					MyExerTrackMonthly myExerTrackMonthly = _myExerTrackMonthlyCache.get(ym);
					Map<String, Object> data = new HashMap<>();
					data.put("ym", String.format("%06d", ym).replaceAll("(\\d{4})(\\d{2})", "$1-$2"));
					data.put("minuteCount", myExerTrackMonthly == null ? 0 : myExerTrackMonthly.getMinuteCount());
					resultList.add(data);
				}
				result.put(userId, resultList);
			}

			List<User> exerUserList = userService.getList().stream()//
					.filter(user -> exer.getUserIds().contains(user.getId())
							|| exer.getOrgIds().contains(user.getOrgId()))//
					.collect(Collectors.toList()); // 获取这个练习的所有用户

			List<Org> orgList = orgService.getList();
			Map<Integer, Org> idOrgCache = orgList.stream().collect(Collectors.toMap(Org::getId, Function.identity()));

			List<Map<String, Object>> emptyList = new ArrayList<>();
			for (Integer ym : allYmList) {
				Map<String, Object> data = new HashMap<>();
				data.put("ym", String.format("%06d", ym).replaceAll("(\\d{4})(\\d{2})", "$1-$2"));
				data.put("minuteCount", 0);
				emptyList.add(data);
			}

			List<Map<String, Object>> resultList = exerUserList.stream().map(exerUser -> {
				Map<String, Object> map = new HashMap<>();
				map.put("userId", exerUser.getId());
				map.put("userName", exerUser.getName());
				map.put("orgName", idOrgCache.get(exerUser.getOrgId()).getName());
				map.put("tracks", result.get(exerUser.getId()) == null ? emptyList : result.get(exerUser.getId()));
				return map;
			}).collect(Collectors.toList());

			return PageResultEx.ok().data(resultList);
		} catch (MyException e) {
			log.error("练习跟踪列表错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("练习跟踪列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 练习答错数量列表
	 * 
	 * v1.0 zhanghc 2025年9月15日上午9:30:06
	 * 
	 * @param exerId
	 * @return PageResult
	 */
	@RequestMapping("/exer/wrongAnswerNumList")
	public PageResult exerWrongQuestionList(Integer exerId) {
		try {
			// 数据校验
			if (!ValidateUtil.isValid(exerId)) {
				throw new MyException("参数错误：exerId");
			}

			Exer exer = exerService.getById(exerId);
			QuestionBank questionBank = questionBankService.getById(exer.getQuestionBankId());
			if (CurLoginUserUtil.isExamUser() || CurLoginUserUtil.isMarkUser()) {
				throw new MyException("无操作权限");
			}
			if (CurLoginUserUtil.isSubAdmin()
					&& getCurUser().getId().intValue() != questionBank.getCreateUserId().intValue()) {
				throw new MyException("无操作权限");
			}

			// 查询练习错题列表
			Map<Integer, Map<String, Object>> wrongAnswerCountCache = myExerQuestionService
					.getWrongAnswerNumList(exerId).stream()
					.collect(Collectors.toMap(row -> ((Integer) row.get("userId")), row -> row));

			List<User> exerUserList = userService.getList().stream()//
					.filter(user -> exer.getUserIds().contains(user.getId())
							|| exer.getOrgIds().contains(user.getOrgId()))//
					.collect(Collectors.toList()); // 获取这个练习的所有用户

			List<Org> orgList = orgService.getList();
			Map<Integer, Org> idOrgCache = orgList.stream().collect(Collectors.toMap(Org::getId, Function.identity()));

			List<Map<String, Object>> resultList = exerUserList.stream().map(exerUser -> {
				Map<String, Object> map = new HashMap<>();
				map.put("userId", exerUser.getId());
				map.put("userName", exerUser.getName());
				map.put("orgName", idOrgCache.get(exerUser.getOrgId()).getName());
				map.put("wrongAnswerNum", wrongAnswerCountCache.get(exerUser.getId()) == null ? 0
						: wrongAnswerCountCache.get(exerUser.getId()).get("wrongAnswerNum"));
				map.put("totalQuestionNum", wrongAnswerCountCache.get(exerUser.getId()) == null ? 0
						: wrongAnswerCountCache.get(exerUser.getId()).get("totalQuestionNum"));
				map.put("answeredNum", wrongAnswerCountCache.get(exerUser.getId()) == null ? 0
						: wrongAnswerCountCache.get(exerUser.getId()).get("answeredNum"));
				return map;
			}).collect(Collectors.toList());

			return PageResultEx.ok().data(resultList);
		} catch (MyException e) {
			log.error("练习答错数量错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("练习答错数量错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 练习试题列表
	 * 
	 * v1.0 zhanghc 2025年9月15日下午3:17:03
	 * 
	 * @param pageIn
	 * @return PageResult
	 */
	@RequestMapping("/exer/questionListpage")
	public PageResult wrongQuestionListpage(PageIn pageIn) {
		try {
			PageOut pageOut = myExerQuestionService.getListpage(pageIn);
			return PageResultEx.ok().data(pageOut);
		} catch (MyException e) {
			log.error("练习试题列表错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("练习试题列表错误：", e);
			return PageResult.err();
		}
	}

	public static void main(String[] args) {
		String input = "1、素质教育理念的核心是()（直接文本写入）<br>```java<br>File prismJs = new File(tempDir, \"prism.js\");<br>File prismCss = new File(tempDir, \"prism.css\");<br>```（4分）";
		input = "<br>```java<br>File prismJs = new File(tempDir, \"prism.js\");<br>File prismCss = new File(tempDir, \"prism.css\");<br>```（4分）";
		Matcher matcher = Pattern.compile("```([a-z]*)<br>([\\s\\S]*?)<br>```").matcher(input);
		StringBuffer result = new StringBuffer();

		while (matcher.find()) {
			String lang = matcher.group(1);
			String code = matcher.group(2);

			String processedLang = lang;
			String processedCode = code;

			matcher.appendReplacement(result, processedLang + ":" + processedCode);
		}
		matcher.appendTail(result);

		System.out.println("处理后结果: " + result.toString());
	}

}