package com.wcpdoc.exam.core.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.entity.QuestionEx;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.WordServer;
import com.wcpdoc.exam.core.util.BigDecimalUtil;
import com.wcpdoc.exam.core.util.SpringUtil;
import com.wcpdoc.exam.core.util.StringUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.file.service.FileService;

/**
 * word服务实现
 * 
 * v1.0 zhanghc 2019年7月19日下午11:15:52
 * 
 */
public class WordServerImpl extends WordServer {
	private static final Logger log = LoggerFactory.getLogger(WordServerImpl.class);
	
	private String[] types = new String[]{"【单选】", "【多选】", "【填空】", "【判断】", "【问答】"};
	private String[] difficultys = new String[]{"【极易】", "【简单】", "【适中】", "【困难】", "【极难】"};
	private String[] options = new String[]{"A", "B", "C", "D", "E", "F", "G"};

	@SuppressWarnings("unchecked")
	@Override
	public <T> T decoder(List<Node> nodeList) {
		// 图片本地路径转上传路径
		log.debug("word解析试题：图片转上传路径开始");
		long startTime = System.currentTimeMillis();
		transformImgUrl(nodeList);
		long endTime = System.currentTimeMillis();
		log.debug("word解析试题：图片转上传路径结束，用时{}毫秒", endTime - startTime);
		
		// 从多行中分隔出不同的题
		log.debug("word解析试题：从多行中分隔出不同的题开始", endTime - startTime);
		startTime = System.currentTimeMillis();
		List<Node> singleQuestion = new ArrayList<>();// 一道题的内容
		List<List<Node>> singleQuestionList = new ArrayList<>();// 所有题的内容
		ListIterator<Node> nodeIterator = nodeList.listIterator();
		boolean splitFinish = false;// 分隔完成标识位
		while (nodeIterator.hasNext()) {
			
			Node node = nodeIterator.next();
			if (node instanceof TextNode) {// 计算开始时间不能从这里开始，因为continue后再次进来会重新计时
				continue;
			}

			String rowTxt = Jsoup.clean(node.outerHtml(), Whitelist.none()).trim();
			if (!ValidateUtil.isValid(rowTxt)) {
				continue;
			}
			if (startsWithType(rowTxt)) {// 如果开始字符串包含试题类型字符串
				rowTxt = rowTxt.substring(4);// 向后位移4个字符
				if (startsWithDifficulty(rowTxt)) {// 如果开始字符串包含难度类型字符串
					splitFinish = true;// 表示分隔到一道完整的试题
				}
			}

			if (splitFinish && ValidateUtil.isValid(singleQuestion)) {
				singleQuestionList.add(singleQuestion);
				singleQuestion = new ArrayList<>();// 重新初始化对象
			}

			splitFinish = false;
			singleQuestion.add(node);
		}
		singleQuestionList.add(singleQuestion);// 最后一道题处理（按头标识分隔，最后一道题下面没有分隔了）
		endTime = System.currentTimeMillis();
		log.debug("word解析试题：从多行中分隔出不同的题结束，用时{}毫秒", endTime - startTime);
		
		// 解析出每道题，供业务层使用
		log.debug("word解析试题：试题内容解析成java对象开始", endTime - startTime);
		startTime = System.currentTimeMillis();
		List<QuestionEx> questionList = new ArrayList<>();// 解析成试题对象的列表
		for (List<Node> _singleQuestion : singleQuestionList) {
			QuestionEx questionEx = null;
			questionEx = parseQuestion(_singleQuestion);
			questionList.add(questionEx);
		}
		endTime = System.currentTimeMillis();
		log.debug("word解析试题：试题内容解析成java对象结束，用时{}毫秒", endTime - startTime);
		return (T) questionList;
	}

	/**
	 * 图片本地路径转上传路径
	 * 
	 * v1.0 zhanghc 2021年7月27日下午1:26:39
	 * @param nodeList 
	 * void
	 */
	private void transformImgUrl(List<Node> nodeList) {
		String[] allowTypes = { "jpg", "gif", "png"};
		
		for (Node node : nodeList) {
			if (node instanceof TextNode) {
				continue;
			}
			ListIterator<Element> imgListIterator = ((Element)node).getElementsByTag("img").listIterator();
			while (imgListIterator.hasNext()) {
				Element imgElement = imgListIterator.next();
				File imgFile = new File(imgElement.attr("src"));
				if (imgFile.exists()) {
					MultipartFile multipartFile = fileToMultipartFile(imgFile);
					FileService fileService = SpringUtil.getBean(FileService.class);
					String fileId = fileService.doTempUpload(new MultipartFile[]{multipartFile}, allowTypes, null);
					imgElement.attr("src", String.format("/api/file/download?id=%s", fileId));
				}
			}
		}
	}
	
	public MultipartFile fileToMultipartFile(File file) {
        FileItem fileItem = createFileItem(file);
        MultipartFile multipartFile = new CommonsMultipartFile(fileItem);
        return multipartFile;
    }

    private static FileItem createFileItem(File file) {
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        FileItem item = factory.createItem("textField", "text/plain", true, file.getName());
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        try {
            FileInputStream fis = new FileInputStream(file);
            OutputStream os = item.getOutputStream();
            while ((bytesRead = fis.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return item;
    }

	/**
	 * 解析试题
	 * 
	 * v1.0 zhanghc 2019年7月19日下午11:15:52
	 * 
	 * v3.0 zhanghc 2021年7月21日下午7:15:03
	 * 重新实现，结构化，增强可读性
	 * 
	 * @param singleQuestion
	 * @return QuestionEx
	 */
	private QuestionEx parseQuestion(List<Node> singleQuestion) {
		/**
		 * word转html后代码
		 * <p class="a DocDefaults "><span class="a0 " style="">【单选】【极易】我是一道单选题的</span></p> 
			 <p class="a DocDefaults "><span class="a0 " style="">题干<img height="56" id="rId8" src="D:/bak/file/temp/c066c0cc-54a6-4c1c-bf56-f34d49239852image1.png" width="48">，</span><span class="a0 " style="white-space:pre-wrap;"> </span><span class="a0 " style="">难度为</span><span class="a0 " style="color: #00B050;">极易</span><span class="a0 " style="">。</span></p> 
			 <p class="a DocDefaults "><span class="a0 " style="font-family: 'SimSun';">A.</span><span class="a0 " style="">单选题的</span><span class="a0 " style="font-family: 'SimSun';">A</span><span class="a0 " style="">选项</span></p> 
			 <p class="a DocDefaults "><span class="a0 " style="font-family: 'SimSun';">B</span><span class="a0 " style="">。单选题的</span><span class="a0 " style="font-family: 'SimSun';">B</span><span class="a0 " style="">选项</span></p> 
			 <p class="a DocDefaults "><span class="a0 " style="font-family: 'SimSun';">C</span><span class="a0 " style="">、单选题的</span><span class="a0 " style="font-family: 'SimSun';">C</span><span class="a0 " style="">选项</span></p> 
			 <p class="a DocDefaults "><span class="a0 " style="font-family: 'SimSun';">D</span><span class="a0 " style="">单选题的</span><span class="a0 " style="font-family: 'SimSun';">D</span><span class="a0 " style="">选项</span></p> 
			 <p class="a DocDefaults "><span class="a0 " style="">【答案：</span><span class="a0 " style="font-family: 'SimSun';">B</span><span class="a0 " style="">】【分值：</span><span class="a0 " style="font-family: 'SimSun';">2</span><span class="a0 " style="">】</span></p> 
			 <p class="a DocDefaults "><span class="a0 " style="">【智能阅卷<span class="" style="">：是</span>】</span></p> 
			 <p class="a DocDefaults "><span class="a0 " style="">【解析】我是单选题的解析</span></p> 
			 <p class="a DocDefaults ">&nbsp;</p> 
			 <p class="a DocDefaults "><span class="a0 " style=""><span class="" style="">【多选】【简单】我是一道多选题的题干，难度为简单。</span></span></p> 
			 <p class="a DocDefaults "><span class="a0 " style="font-family: 'SimSun';white-space:pre-wrap;">A. </span><span class="a0 " style="">多选题的</span><span class="a0 " style="font-family: 'SimSun';">A</span><span class="a0 " style="">选项。</span></p> 
			 <p class="a DocDefaults "><span class="a0 " style="font-family: 'SimSun';white-space:pre-wrap;">C. </span><span class="a0 " style="">多选题的</span><span class="a0 " style="font-family: 'SimSun';">C</span><span class="a0 " style="">选项。</span></p> 
			 <p class="a DocDefaults "><span class="a0 " style="font-family: 'SimSun';white-space:pre-wrap;">D. </span><span class="a0 " style="">多选题的</span><span class="a0 " style="font-family: 'SimSun';">D</span><span class="a0 " style="">选项。</span></p> 
			 <p class="a DocDefaults "><span class="a0 " style="font-family: 'SimSun';white-space:pre-wrap;">B. </span><span class="a0 " style="">多选题的</span><span class="a0 " style="font-family: 'SimSun';">B</span><span class="a0 " style="">选项。</span></p> 
			 <p class="a DocDefaults "><span class="a0 " style="">【答案：</span><span class="a0 " style="font-family: 'SimSun';">BC</span><span class="a0 " style="">】【分值：</span><span class="a0 " style="font-family: 'SimSun';">2</span><span class="a0 " style="">】</span></p> 
			 <p class="a DocDefaults "><span class="a0 " style=""><span class="" style="">【智能阅卷：是】【漏选得分：</span></span><span class="a0 " style="font-family: 'SimSun';">1</span><span class="a0 " style="">】</span></p> 
			 <p class="a DocDefaults "><span class="a0 " style="">【解析】我是多选题的解析</span></p> 
			 <p class="a DocDefaults ">&nbsp;</p> 
			 <p class="a DocDefaults "><span class="a0 " style="">【填空】【适中】我是一道填空题的</span><span class="a0 " style="font-family: 'SimSun';">_________</span><span class="a0 " style=""><span class="" style="">，难度为</span></span><span class="a0 " style="font-family: 'SimSun';">_________</span><span class="a0 " style="">。</span></p> 
			 <p class="a DocDefaults "><span class="a0 " style="">【答案：山西</span><span class="a0 " style="white-space:pre-wrap;"> </span><span class="a0 " style="">晋】【分值：</span><span class="a0 " style="font-family: 'Tahoma';">1</span><span class="a0 " style="">】</span></p> 
			 <p class="a DocDefaults "><span class="a0 " style="">【答案：一般</span><span class="a0 " style="white-space:pre-wrap;"> </span><span class="a0 " style="">通常</span><span class="a0 " style="white-space:pre-wrap;"> </span><span class="a0 " style="">普遍】【分值：</span><span class="a0 " style="font-family: 'SimSun';">1</span><span class="a0 " style="">】</span></p> 
			 <p class="a DocDefaults "><span class="a5 a0 " style=""></span><span class="a0 " style=""><span class="" style="">【智能阅卷：是】【答案</span>有<span class="" style="">顺序：</span>否】【大小写<span class="" style="">敏感：</span>否】</span></p> 
			 <p class="a DocDefaults "><span class="a0 " style="">【解析】我是填空题的解析</span></p> 
			 <p class="a DocDefaults ">&nbsp;</p> 
			 <p class="a DocDefaults "><span class="a0 " style=""><span class="" style="">【判断】【困难】我是一道判断题的题干，难度为困难。</span></span></p> 
			 <p class="a DocDefaults "><span class="a0 " style="">【答案：对】【分值：</span><span class="a0 " style="font-family: 'SimSun';">2</span><span class="a0 " style="">】</span></p> 
			 <p class="a DocDefaults "><span class="a0 " style=""><span class="" style="">【智能阅卷：是】</span></span></p> 
			 <p class="a DocDefaults "><span class="a0 " style="">【解析】我是判断题的解析</span></p> 
			 <p class="a DocDefaults ">&nbsp;</p> 
			 <p class="a DocDefaults "><span class="a0 " style=""><span class="" style="">【问答】【极难】我是一道问答题的题干，难度为极难。</span></span></p> 
			 <p class="a DocDefaults "><span class="a0 " style="">【答案：山西</span><span class="a0 " style="white-space:pre-wrap;"> </span><span class="a0 " style="">晋】【分值：</span><span class="a0 " style="font-family: 'Tahoma';">2</span><span class="a0 " style="">】</span></p> 
			 <p class="a DocDefaults "><span class="a0 " style="">【答案：一般</span><span class="a0 " style="white-space:pre-wrap;"> </span><span class="a0 " style="">通常</span><span class="a0 " style="white-space:pre-wrap;"> </span><span class="a0 " style="">普遍】【分值：</span><span class="a0 " style="font-family: 'SimSun';">3</span><span class="a0 " style="">】</span></p> 
			 <p class="a DocDefaults "><span class="a0 " style=""><span class="" style="">【智能阅卷：</span>否<span class="" style="">】【大小写敏感：否】</span></span></p>
			</div> 
			<div class="footnotes"> 
			 <p class="a DocDefaults " style="margin-bottom: 0in;">&nbsp;</p>
			</div> 
		 */
		
		// 解析文本数据，文本格式校验
			/** 解析文本数据，文本格式校验
			【单选】【极易】蛋黄的颜色主要取决于其中_____的含量()
			A、 叶黄素
			B、 胡萝卜素
			C、 核黄素
			D、 姜黄素
			【答案：B】【分值：2】
			【智能阅卷：是】【漏选得分：是】【漏选分值：1】
			【解析】
		*/
		String singleHtml = Jsoup.clean(singleQuestion.toString(), Whitelist.none()).trim();
		int answerIndex = singleHtml.indexOf("【答案");
		int aiIndex = singleHtml.indexOf("【智能阅卷");
		int analysisIndex = singleHtml.indexOf("【解析");
		if (answerIndex > aiIndex) {
			throw new MyException(String.format("答案和智能阅卷顺序错误：%s】", StringUtil.delHTMLTag(singleQuestion.toString())));
		}
		if (aiIndex > analysisIndex) {
			throw new MyException(String.format("智能阅卷和解析顺序错误：%s】", StringUtil.delHTMLTag(singleQuestion.toString())));
		}
		
		List<Node> titleRows = parseTitleRows(singleQuestion);
		List<Node> optionRows = parseOptionRows(singleQuestion);
		List<Node> answerRows = parseAnswerRows(singleQuestion);
		List<Node> aiRows = parseAiRows(singleQuestion);
		List<Node> analysisRows = parseAnalysisRows(singleQuestion);
		
		int type = parseType(titleRows);
		int difficulty = parseDifficulty(titleRows);
		String title = parseTitle(titleRows);
		List<QuestionOption> questionOptionList = parseQuestionOptionList(optionRows, type);
		AI ai = parseAi(aiRows);
		List<QuestionAnswer> questionAnswerList = parseAnswer(answerRows, type, ai);
		String analysis = parseAnalysis(analysisRows);
		
		// 逻辑错误校验
		String answer = questionAnswerList.get(0).getAnswer();
		if (type == 1) {//单选
			if (answer.length() != 1 || getOption(answer) == -1) {
				throw new MyException(String.format("答案和选项不匹配：%s】", StringUtil.delHTMLTag(singleQuestion.toString())));
			}
		}
		if (type == 2) {//多选
			if (answer.length() == 0 || answer.length() > options.length) {//最多7个选项
				throw new MyException(String.format("答案和选项不匹配：%s】", StringUtil.delHTMLTag(singleQuestion.toString())));
			}
			for (String an : answer.split("")) {// 每个答案都校验一次
				if (getOption(an) == -1) {
					throw new MyException(String.format("答案和选项不匹配：%s】", StringUtil.delHTMLTag(singleQuestion.toString())));
				}
			}
		} 
		if (type == 4) {// 判断
			if (answer.length() != 1 || !"对是√否错×".contains(answer)) {
				throw new MyException(String.format("答案和选项不匹配：%s】", StringUtil.delHTMLTag(singleQuestion.toString())));
			}
		}
		
		// 转换成需要的试题对象
		QuestionEx questionEx = new QuestionEx();
		questionEx.getQuestion().setType(type);// 类型
		questionEx.getQuestion().setDifficulty(difficulty);// 难度
		questionEx.getQuestion().setTitle(title);// 题干
		if (type == 1 || type == 2) {// 选项
			for (int i = 0; i < questionOptionList.size(); i++) {
				QuestionOption questionOption = questionOptionList.get(i);
				questionOption.setNo(i + 1);
			}
			questionEx.setQuestionOptionList(questionOptionList);
		}
		
		if (type == 1 || type == 2 || type == 4) {// 总分数
			questionEx.getQuestion().setScore(new BigDecimal(questionAnswerList.get(0).getScore().toString()));// 只看第一行
		} else if (type == 3 || (type == 5 && ai.getAi() == 1)) {// 如果是填空或问答智能阅卷，每项分加一起就是总分
			BigDecimalUtil bigDecimalUtil = BigDecimalUtil.newInstance(0);
			for (QuestionAnswer questionAnswer : questionAnswerList) {
				bigDecimalUtil.add(questionAnswer.getScore());
			}
			questionEx.getQuestion().setScore(bigDecimalUtil.getResult());
		} else if (type == 5 && ai.getAi() == 2) {// 如果是问答非智能阅卷，分值从智能阅卷行问答分值取
			questionEx.getQuestion().setScore(new BigDecimal(ai.getQaScore().toString()));
		}

		if (type == 1 || type == 4) {// 答案和分数
			QuestionAnswer questionAnswer = new QuestionAnswer();
			questionAnswer.setAnswer(questionAnswerList.get(0).getAnswer());
			questionAnswer.setScore(new BigDecimal(questionAnswerList.get(0).getScore().toString()));// 和分值一样
			questionEx.getQuestionAnswerList().add(questionAnswer);
		} else if (type == 3 || (type == 5 && ai.getAi() == 1)) {
			for (QuestionAnswer questionAnswer : questionAnswerList) {
				questionEx.getQuestionAnswerList().add(questionAnswer);
			}
		} else if (type == 2) {
			QuestionAnswer questionAnswer = new QuestionAnswer();
			questionAnswer.setAnswer(questionAnswerList.get(0).getAnswer());
			questionAnswer.setScore(new BigDecimal(ai.getMissScore().toString()));//从漏选分值取
			questionEx.getQuestionAnswerList().add(questionAnswer);
		} else if (type == 5 && ai.getAi() == 2) {
			QuestionAnswer questionAnswer = new QuestionAnswer();
			questionAnswer.setAnswer(questionAnswerList.get(0).getAnswer());
			questionAnswer.setScore(new BigDecimal(ai.getQaScore().toString()));//从问答分值取
			questionEx.getQuestionAnswerList().add(questionAnswer);
		}
		
		questionEx.getQuestion().setAi(ai.getAi());
		if (ai.getAi() == 1) {// 如果智能阅卷是开启的
			if (type == 1 || type == 4) {// 单选、判断不需要
				
			} else if (type == 2) {// 多选 1：漏选得分；
				if (ai.getScoreOptions().toString().contains("1")) {
					questionEx.getQuestion().setScoreOptions("1");
				}
			} else if (type == 3) {// 填空问答 2：答案无顺序；3：大小写不敏感；
				String sos = "";
				
				if (ai.getScoreOptions().toString().contains("2")) {
					sos += sos.isEmpty() ? "2" : ",2";
				}
				if (ai.getScoreOptions().toString().contains("3")) {
					sos += sos.isEmpty() ? "3" : ",3";
				}
				questionEx.getQuestion().setScoreOptions(sos);
			} else if (type == 5) {// 问答 3：大小写不敏感；
				if (ai.getScoreOptions().toString().contains("3")) {
					questionEx.getQuestion().setScoreOptions("3");
				}
			}
		}
		questionEx.getQuestion().setAnalysis(analysis);
		return questionEx;
	}
	
	private String parseAnalysis(List<Node> analysisRows) {
		return getTxt(analysisRows, 0, analysisRows.size()).replaceAll("【解析】", "");
	}

	private AI parseAi(List<Node> aiRows) {
		String aiTxt = Jsoup.clean(aiRows.get(0).outerHtml(), Whitelist.none()).trim();
		int lxdfIndex = aiTxt.indexOf("【漏选得分：");//临时变量可以用汉字首字母
		int dayxxIndex = aiTxt.indexOf("【答案无顺序：");
		int dxxmgIndex = aiTxt.indexOf("【大小写不敏感：");
		int lxfzIndex = aiTxt.indexOf("【漏选分值：");
		int wdIndex = aiTxt.indexOf("【问答分值：");
		
		AI ai = new AI();
		ai.setAi(aiTxt.substring(6, 6 + 1).equals("是") ? 1 : 2);
		if (lxdfIndex != -1 && aiTxt.substring(lxdfIndex + 6, lxdfIndex + 6 + 1).equals("是")) {
			ai.getScoreOptions().add(1);
		}
		if (dayxxIndex != -1 && aiTxt.substring(dayxxIndex + 7, dayxxIndex + 7 + 1).equals("是")) {
			ai.getScoreOptions().add(2);
		}
		if (dxxmgIndex != -1 && aiTxt.substring(dxxmgIndex + 8, dxxmgIndex + 8 + 1).equals("是")) {
			ai.getScoreOptions().add(3);
		}
		if (lxfzIndex != -1) {
			try {
				Double missScore = Double.parseDouble(aiTxt.substring(lxfzIndex + 6, aiTxt.indexOf("】", lxfzIndex + 6)));
				ai.setMissScore(missScore);
			} catch (NumberFormatException e) {
				throw new MyException(String.format("不能从试题解析【漏选分值】：%s】", StringUtil.delHTMLTag(aiRows.toString())));
			}
		}
		if (wdIndex != -1) {
			try {
				Double qaScore = Double.parseDouble(aiTxt.substring(wdIndex + 6, aiTxt.indexOf("】", wdIndex + 6)));
				ai.setQaScore(qaScore);
			} catch (NumberFormatException e) {
				throw new MyException(String.format("不能从试题解析【问答分值】：%s】", StringUtil.delHTMLTag(aiRows.toString())));
			}
		}
		return ai;
	}

	private List<QuestionAnswer> parseAnswer(List<Node> answerNodeList, int type, AI ai) {
		List<QuestionAnswer> answerScoreList = new ArrayList<>();// 解析答案和分值，如果是填空或问答，答案可能是多行
		if (type == 1 || type == 2 || type == 3 || type == 4 || (type == 5 && ai.getAi() == 1)) {
			for (Node answerNode : answerNodeList) {
				String answerTxt = Jsoup.clean(answerNode.outerHtml(), Whitelist.none()); // 【答案：B】【分值：2】 
				String answer = answerTxt.substring(4, answerTxt.indexOf("】【分值：")).trim();
				String scoreStr = answerTxt.substring(answerTxt.length() - 2, answerTxt.length() - 1).trim(); 
				Double score = null;
				try {
					score = Double.parseDouble(scoreStr);
				} catch (NumberFormatException e) {
					throw new MyException(String.format("不能从试题找到【分值】：%s】", StringUtil.delHTMLTag(answerNodeList.toString())));
				}
				QuestionAnswer questionAnswer = new QuestionAnswer();
				questionAnswer.setAnswer(answer);// 多选按逗号分隔
				questionAnswer.setScore(new BigDecimal(score.toString()));
				answerScoreList.add(questionAnswer);
			}
		} else if (type == 5 && ai.getAi() == 2) {// 问答、非智能阅卷
			String answerTxt = getTxt(answerNodeList, 0, answerNodeList.size());
			answerTxt.replace("【答案", "").replace("：", "");
			int lastIndex = answerTxt.lastIndexOf("】");
			if (lastIndex == -1) {
				throw new MyException(String.format("答案格式不正确：%s】", StringUtil.delHTMLTag(answerNodeList.toString())));
			}
			answerTxt = answerTxt.substring(0, lastIndex) + answerTxt.substring(lastIndex + 1, answerTxt.length());
			
			QuestionAnswer questionAnswer = new QuestionAnswer();
			questionAnswer.setAnswer(answerTxt);
			questionAnswer.setScore(null);// 分值在智能阅卷行
			answerScoreList.add(questionAnswer);
		}
		
		return answerScoreList;
	}

	private List<QuestionOption> parseQuestionOptionList(List<Node> optionRows, Integer type) {
		if (type != 1 && type != 2) {
			return null;
		}
		List<List<Node>> optionList = new ArrayList<>();// 选项节点（每个选项可以有多行）
		for (int i = 0; i < options.length; i++) {
			optionList.add(new ArrayList<>(0));// 先初始化后根据选项位置替换，在剔除最后空白项
		}
		
		int startIndex = 0, endIndex = 0, curIndex = 0;
		for (Node optionNode : optionRows) {
			String optionTxt = Jsoup.clean(optionNode.outerHtml(), Whitelist.none()).trim();
			/*
			 * B。单选题的B选项 
			 * 单选题的B选项
			 * 
			 * A.单选题的A选项 单选题的A选项
			 * 
			 * C、单选题的C选项 
			 * D单选题的D选项 单选题的D选项
			 */
			if (startsWithOption(optionTxt)) {
				endIndex = curIndex;
			}
			if (startIndex < endIndex) {
				Node startNode = optionRows.get(startIndex);
				String startTxt = Jsoup.clean(startNode.outerHtml(), Whitelist.none()).trim();
				int optionIndex = getOption(startTxt);
				optionList.set(optionIndex, optionRows.subList(startIndex, endIndex));// 根据第一个字符（ABCDEFG)添加到对应的位置，相当于排序
				startIndex = curIndex;
			}
			
			curIndex++;
		}
		
		Node startNode = optionRows.get(startIndex); // 最后一个选项处理
		String startTxt = Jsoup.clean(startNode.outerHtml(), Whitelist.none()).trim();
		int optionIndex = getOption(startTxt);
		optionList.set(optionIndex, optionRows.subList(startIndex, optionRows.size()));
		
		ListIterator<List<Node>> listIterator = optionList.listIterator();// 从后往前循环，去掉多余的空白选项
		while (listIterator.hasNext()) {
			listIterator.next();
		}
		while (listIterator.hasPrevious()) {
			List<Node> nodeList = listIterator.previous();
			if (!nodeList.isEmpty()) {// 最后一个不为空，停止
				break;
			}
			listIterator.remove();
		}
		
		if (optionList.size() < 2) {
			throw new MyException(String.format("单选或多选最少需要两个选项：%s】", StringUtil.delHTMLTag(optionRows.toString())));
		}
		
		List<QuestionOption> questionOptionList = new ArrayList<>();// 返回解析后的对象
		for (List<Node> options : optionList) {
			QuestionOption questionOption = parseQuestionOption(options);
			questionOptionList.add(questionOption);
		}
		return questionOptionList;
	}

	private String parseTitle(List<Node> titleRows) {
		String txt = getTxt(titleRows, 0, titleRows.size());
		for (String type : types) {
			if (txt.contains(type)) {// 找到只替换第一个
				txt = txt.replace(type, "");
				break;
			}
		}
		for (String difficulty : difficultys) {
			if (txt.contains(difficulty)) {// 找到只替换第一个
				txt = txt.replace(difficulty, "");
				break;
			}
		}
		return txt;
	}

	private int parseDifficulty(List<Node> titleRows) {
		String titleTxt = Jsoup.clean(titleRows.get(0).outerHtml(), Whitelist.none()).trim();
		titleTxt = titleTxt.substring(4);
		if (!startsWithDifficulty(titleTxt)) {
			throw new MyException(String.format("不能从题干找到%s标签：%s】", StringUtil.join(difficultys), StringUtil.delHTMLTag(titleRows.toString())));
		}
		return getDifficulty(titleTxt);
	}

	private int parseType(List<Node> titleRows) {
		String titleTxt = Jsoup.clean(titleRows.get(0).outerHtml(), Whitelist.none()).trim();
		if (!startsWithType(titleTxt)) {
			throw new MyException(String.format("不能从题干找到%s标签：%s】", StringUtil.join(types), StringUtil.delHTMLTag(titleRows.toString())));
		}
		return getType(titleTxt);
	}

	private List<Node> parseAnalysisRows(List<Node> singleQuestion) {
		int startIndex = 0, endIndex = 0, curIndex = 0;// 标记起始行和结束行
		for (Node node : singleQuestion) {
			String rowTxt = Jsoup.clean(node.outerHtml(), Whitelist.none()).trim();
			if (rowTxt.startsWith("【解析")) {
				startIndex = curIndex;
				endIndex = singleQuestion.size();// 剩余部分全部是解析
				break;
			}
			curIndex++;
		}
		
		if (startIndex == 0 || endIndex == 0) {
			throw new MyException(String.format("不能从试题找到【解析】：【%s】", StringUtil.delHTMLTag(singleQuestion.toString())));
		}
		
		return singleQuestion.subList(startIndex, endIndex);
	}

	private List<Node> parseAiRows(List<Node> singleQuestion) {
		int startIndex = 0, endIndex = 0, curIndex = 0;// 标记起始行和结束行
		for (Node node : singleQuestion) {
			String rowTxt = Jsoup.clean(node.outerHtml(), Whitelist.none()).trim();
			if (rowTxt.startsWith("【智能阅卷")) {
				startIndex = curIndex;
				endIndex = curIndex + 1;// 只有一行，索引+1就是结束位置
				break;
			}
			curIndex++;
		}
		
		if (startIndex == 0 || endIndex == 0) {
			throw new MyException(String.format("不能从试题找到【智能阅卷】：【%s】", StringUtil.delHTMLTag(singleQuestion.toString())));
		}
		
		return singleQuestion.subList(startIndex, endIndex);
	}

	private List<Node> parseAnswerRows(List<Node> singleQuestion) {
		int startIndex = 0, endIndex = 0, curIndex = 0;// 标记起始行和结束行
		for (Node node : singleQuestion) {
			String rowTxt = Jsoup.clean(node.outerHtml(), Whitelist.none()).trim();
			if (rowTxt.startsWith("【答案")) {
				if (startIndex == 0) {// 答案可能有多行，只取第一行的索引
					startIndex = curIndex;
				}
			} else if (rowTxt.startsWith("【智能阅卷")) {
				endIndex = curIndex;
				break;//找到就不在循环了
			}
			curIndex++;
		}
		
		if (startIndex == 0 || endIndex == 0) {
			throw new MyException(String.format("不能从试题找到【答案】：【%s】", StringUtil.delHTMLTag(singleQuestion.toString())));
		}
		
		if (startIndex >= endIndex) {
			throw new MyException(String.format("试题格式错误：【%s】", StringUtil.delHTMLTag(singleQuestion.toString())));
		}
		
		return singleQuestion.subList(startIndex, endIndex);
	}

	private List<Node> parseOptionRows(List<Node> singleQuestion) {
		int startIndex = 0, endIndex = 0;// 标记起始行和结束行
		for (Node node : singleQuestion) {
			String rowTxt = Jsoup.clean(node.outerHtml(), Whitelist.none()).trim();
			if (rowTxt.startsWith("【答案")) {
				break;
			}
			endIndex++;
		}
		
		if (endIndex == 0) {
			throw new MyException(String.format("不能从试题找到【题干】：【%s】", StringUtil.delHTMLTag(singleQuestion.toString())));
		}
		
		List<Node> titleNodeList = singleQuestion.subList(startIndex, endIndex);
		String rowTxt = Jsoup.clean(titleNodeList.get(0).outerHtml(), Whitelist.none()).trim();
		int type = getType(rowTxt);
		if (type == 1 || type == 2) {// 如果是单选或多选，过滤掉题干
			for (Node optionNode : titleNodeList) {
				String optionTxt = Jsoup.clean(optionNode.outerHtml(), Whitelist.none()).trim();
				if (startsWithOption(optionTxt)) {
					break;
				}
				startIndex++;
			}
		}
		
		return singleQuestion.subList(startIndex, endIndex);
	}

	private List<Node> parseTitleRows(List<Node> singleQuestion) {
		int startIndex = 0, endIndex = 0;// 标记起始行和结束行
		for (Node node : singleQuestion) {
			String rowTxt = Jsoup.clean(node.outerHtml(), Whitelist.none()).trim();
			if (rowTxt.startsWith("【答案")) {
				break;
			}
			endIndex++;
		}
		
		if (endIndex == 0) {
			throw new MyException(String.format("不能从试题找到【题干】：【%s】", StringUtil.delHTMLTag(singleQuestion.toString())));
		}
		
		List<Node> titleNodeList = singleQuestion.subList(startIndex, endIndex);
		String rowTxt = Jsoup.clean(titleNodeList.get(0).outerHtml(), Whitelist.none()).trim();
		int type = getType(rowTxt);
		if (type == 1 || type == 2) {// 如果是单选或多选，过滤掉选项
			endIndex = 0;
			for (Node optionNode : titleNodeList) {
				String optionTxt = Jsoup.clean(optionNode.outerHtml(), Whitelist.none()).trim();
				if (startsWithOption(optionTxt)) {
					break;
				}
				endIndex++;
			}
		}
		
		return singleQuestion.subList(startIndex, endIndex);
	}

	/**
	 * 获取试题选项
	 * 
	 * v1.0 zhanghc 2021年7月24日上午10:57:39
	 * @param options2
	 * @return QuestionOption
	 */
	private QuestionOption parseQuestionOption(List<Node> options) {
		//<p class="a DocDefaults "><span class="a0 " style="font-family: 'SimSun';">A.</span><span class="a0 " style="">单选题的</span><span class="a0 " style="font-family: 'SimSun';">A</span><span class="a0 " style="">选项</span></p> 
		//<p class="a DocDefaults "><span class="a0 " style="font-family: 'SimSun';">B</span><span class="a0 " style="">。单选题的</span><span class="a0 " style="font-family: 'SimSun';">B</span><span class="a0 " style="">选项</span></p> 
		Element element = (Element) options.get(0).childNodes().get(0);
		element.html(element.html().substring(1));//截取第一个字符串（前面已经校验，这里肯定已ABCDEFG开头
		if (element.html().length() > 0) {//如果第一个span第二个字符串包含.。、则剔除
			if (element.html().startsWith(".") || element.html().startsWith("。") || element.html().startsWith("、")) {
				element.html(element.html().substring(1));
			}
		} else if (options.get(0).childNodes().size() >= 2) {//否则从第二个span找
			element = (Element) options.get(0).childNodes().get(1);
			if (element.html().length() > 0) {
				if (element.html().startsWith(".") || element.html().startsWith("。") || element.html().startsWith("、")) {
					element.html(element.html().substring(1));
				}
			}
		}
		QuestionOption questionOption = new QuestionOption();
		questionOption.setOptions(getTxt(options, 0, options.size()));
		return questionOption;
	}

	/**
	 * 获取文本
	 * 
	 * v1.0 zhanghc 2021年7月26日下午4:00:50
	 * @param nodeList
	 * @param startIndex
	 * @param endIndex
	 * @return String
	 */
	private String getTxt(List<Node> nodeList, int startIndex, int endIndex) {
		List<Node> subList = nodeList.subList(startIndex, endIndex);
		StringBuilder txt = new StringBuilder();
		for (Node node : subList) {
			txt.append(node.outerHtml());
		}
		
		return txt.toString();
	}
	
	/**
	 * 获取难度类型
	 * 
	 * v1.0 zhanghc 2021年7月22日上午10:03:58
	 * @param rowTxt
	 * @return boolean
	 */
	private int getDifficulty(String rowTxt) {
		for (int i = 0; i < difficultys.length; i++) {
			if (rowTxt.startsWith(difficultys[i])) {
				return i + 1;
			}
		}

		return 0;
	}
	
	/**
	 * 起始包含难度字符串
	 * 
	 * v1.0 zhanghc 2021年7月22日上午10:03:58
	 * @param rowTxt
	 * @return boolean
	 */
	private boolean startsWithDifficulty(String rowTxt) {
		return getDifficulty(rowTxt) > 0;
	}

	/**
	 * 获取试题类型
	 * 
	 * v1.0 zhanghc 2021年7月22日上午10:04:14
	 * @param rowTxt
	 * @return boolean
	 */
	private int getType(String rowTxt) {
		for (int i = 0; i < types.length; i++) {
			if (rowTxt.startsWith(types[i])) {
				return i + 1;
			}
		}

		return 0;
	}
	/**
	 * 起始包含类型字符串
	 * 
	 * v1.0 zhanghc 2021年7月22日上午10:04:14
	 * @param rowTxt
	 * @return boolean
	 */
	private boolean startsWithType(String rowTxt) {
		return getType(rowTxt) > 0;
	}
	
	/**
	 * 获取试题选项
	 * 
	 * v1.0 zhanghc 2021年7月22日上午10:04:14
	 * @param rowTxt
	 * @return boolean
	 */
	private int getOption(String rowTxt) {
		for (int i = 0; i < options.length; i++) {
			if (rowTxt.substring(0, 1).equalsIgnoreCase(options[i])) {
				return i;
			}
		}

		return -1;
	}
	
	/**
	 * 起始包含试题选项字符串
	 * 
	 * v1.0 zhanghc 2021年7月22日下午3:58:08
	 * @param rowTxt
	 * @return boolean
	 */
	private boolean startsWithOption(String rowTxt) {
		return getOption(rowTxt) > -1;
	}
	
	private class AI {
		private Integer ai;
		private List<Integer> scoreOptions = new ArrayList<>();
		private Double missScore;
		private Double qaScore;

		public Integer getAi() {
			return ai;
		}

		public void setAi(Integer ai) {
			this.ai = ai;
		}

		public List<Integer> getScoreOptions() {
			return scoreOptions;
		}

		public Double getMissScore() {
			return missScore;
		}

		public void setMissScore(Double missScore) {
			this.missScore = missScore;
		}

		public Double getQaScore() {
			return qaScore;
		}

		public void setQaScore(Double qaScore) {
			this.qaScore = qaScore;
		}
	}
}
