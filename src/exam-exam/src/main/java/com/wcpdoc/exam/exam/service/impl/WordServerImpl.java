package com.wcpdoc.exam.exam.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import com.wcpdoc.exam.core.util.StringUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.exam.entity.Question;
import com.wcpdoc.exam.exam.service.WordServer;

/**
 * word服务实现
 * 
 * v1.0 zhanghc 2019年7月19日下午11:15:52
 */
public class WordServerImpl extends WordServer {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T doDecoder(List<Node> rowNodes) {
		List<Node> singleQuestion = new ArrayList<>();
		List<Question> questionList = new ArrayList<>();
		for (Node node : rowNodes) {
			if (node instanceof TextNode) {
				continue;
			}

			String rowTxt = Jsoup.clean(node.outerHtml(), Whitelist.none()).trim();
			if (rowTxt.length() > 7) {
				String typeName = rowTxt.substring(1, 3);
				String difficultyName = rowTxt.substring(5, 7);
				if (containType(typeName) && containDifficulty(difficultyName)) {
					if (!singleQuestion.isEmpty()) {
						questionList.add(parseQuestion(singleQuestion));
						singleQuestion = new ArrayList<>();
					}
				}
			}

			singleQuestion.add(node);
		}

		if (!singleQuestion.isEmpty()) {
			questionList.add(parseQuestion(singleQuestion));
		}
		return (T) questionList;
	}

	private Question parseQuestion(List<Node> singleQuestion) {
		int answerIndex = 0;
		int analysisIndex = 0;
		int curIndex = 0;
		Question question = new Question();
		for (Node node : singleQuestion) {
			String rowTxt = Jsoup.clean(node.outerHtml(), Whitelist.none()).trim();
			if (rowTxt.startsWith("【答案】")) {
				answerIndex = curIndex;
			} else if (rowTxt.startsWith("【解析】")) {
				analysisIndex = curIndex;
			}

			curIndex++;
		}

		if (answerIndex == 0) {
			throw new RuntimeException("不能从试题找到【答案】：【" + singleQuestion.toString() + "】");
		}
		if (analysisIndex == 0) {
			throw new RuntimeException("不能从试题找到【解析】：【" + singleQuestion.toString() + "】");
		}
		if (answerIndex >= analysisIndex) {
			throw new RuntimeException("【答案】必须在【解析】之前：【" + singleQuestion.toString() + "】");
		}

		String titleTxt = Jsoup.clean(singleQuestion.get(0).outerHtml(), Whitelist.none()).trim();
		String typeName = titleTxt.substring(1, 3);
		Integer type = getType(typeName);
		if (type == null) {
			throw new RuntimeException("不能从试题找到【类型】：【" + singleQuestion.toString() + "】");
		}

		String difficultyName = titleTxt.substring(5, 7);
		Integer difficulty = getDifficulty(difficultyName);
		if (difficulty == null) {
			throw new RuntimeException("不能从试题找到【难度】：【" + singleQuestion.toString() + "】");
		}

		List<Node> titleNodeList = singleQuestion.subList(0, answerIndex);
		Element titleElement = ((Element) titleNodeList.get(0)).child(0);
		titleElement.html(titleElement.html().substring(8));
		
		List<Integer> optionIndexs = new ArrayList<>();
		List<String> optionList = new ArrayList<>();
		if (type == 1 || type == 2) {//单选或多选
			curIndex = 0;
			for (Node node : titleNodeList) {
				curIndex++;
				String rowTxt = Jsoup.clean(node.outerHtml(), Whitelist.none()).trim();
				if (rowTxt.isEmpty()) {
					continue;
				}
				if (rowTxt.startsWith("A") || rowTxt.startsWith("a") 
						|| rowTxt.startsWith("B") || rowTxt.startsWith("b") 
						|| rowTxt.startsWith("C") || rowTxt.startsWith("c") 
						|| rowTxt.startsWith("D") || rowTxt.startsWith("d") 
						|| rowTxt.startsWith("E") || rowTxt.startsWith("e") 
						|| rowTxt.startsWith("F") || rowTxt.startsWith("f") 
						|| rowTxt.startsWith("G") || rowTxt.startsWith("g") ) {
					optionIndexs.add(curIndex - 1);
				}
			}
			optionIndexs.add(curIndex);
			Map<String, String> optionMap = new HashMap<>();
			for (int i = 0; i < optionIndexs.size() - 1; i++) {
				List<Node> subList = titleNodeList.subList(optionIndexs.get(i), optionIndexs.get(i + 1));
				Element element = (Element) subList.get(0).childNode(1);
				String text = element.html().substring(0, 2);
				if (text.endsWith(",") || text.endsWith("，") || text.endsWith(".") || text.endsWith("。")
						|| text.endsWith("、")) {
					element.html(element.html().substring(2));
				} else {
					element.html(element.html().substring(1));
				}
				optionMap.put(text.substring(0, 1).toUpperCase(), getTxt(titleNodeList, optionIndexs.get(i), optionIndexs.get(i + 1)));
			}

			optionList.add("A");
			optionList.add("B");
			optionList.add("C");
			optionList.add("D");
			optionList.add("E");
			optionList.add("F");
			optionList.add("G");
			optionList = optionList.subList(0, optionIndexs.size() - 1);
			for (String option : optionList) {
				if (optionMap.get(option) == null) {
					throw new RuntimeException("不能从" + titleNodeList + "发现【" + option + "】选项");
				}
			}
			
			if(optionList.size() >= 1){
				question.setOptionA(optionMap.get("A"));
			}
			if(optionList.size() >= 2){
				question.setOptionB(optionMap.get("B"));
			}
			if(optionList.size() >= 3){
				question.setOptionC(optionMap.get("C"));
			}
			if(optionList.size() >= 4){
				question.setOptionD(optionMap.get("D"));
			}
			if(optionList.size() >= 5){
				question.setOptionE(optionMap.get("E"));
			}
			if(optionList.size() >= 6){
				question.setOptionF(optionMap.get("F"));
			}
			if(optionList.size() >= 7){
				question.setOptionG(optionMap.get("G"));
			}
		}

		String title = "";
		if (type == 1 || type == 2) {//单选或多选
			title = getTxt(singleQuestion, 0, optionIndexs.get(0));
		} else {
			title = getTxt(singleQuestion, 0, answerIndex);
		}
		
		String answer = Jsoup.clean(getTxt(singleQuestion, answerIndex, analysisIndex), Whitelist.none())
				.replaceAll(" ", "").replaceAll("\r", "").replaceAll("\n", "").substring(4);
		if (type == 1 || type == 2) {
			Set<String> set = new LinkedHashSet<>(Arrays.asList(answer.split("")));
			set.remove("");
			Set<String> set2 = new HashSet<>(optionList);
			
			if (!set2.containsAll(set)) {
				throw new RuntimeException("选项和答案不匹配：【"+singleQuestion.toString()+"】");
			}
			answer = StringUtil.join(set, ",");
		} else if (type == 3) {
			answer = "";
			List<Node> subList = singleQuestion.subList(answerIndex, analysisIndex);
			for (Node node : subList) {
				String an = Jsoup.clean(node.outerHtml(), Whitelist.none())
						.replaceAll(" ", "").replaceAll("\r", "").replaceAll("\n", "");
				if(an.startsWith("【答案】")){
					an = an.substring(4);
				}
				if (!ValidateUtil.isValid(an)) {
					continue;
				}
				
				if (!answer.isEmpty()) {
					answer += "\n";
				}
				
				answer += an;
			}
		} else if (type == 4) {
			if (answer.length() != 1) {
				throw new RuntimeException("答案只能是一个：【"+singleQuestion.toString()+"】");
			}
			
			if ("对是√".contains(answer)) {
				answer = "对";
			} else if ("否错×".contains(answer)) {
				answer = "错";
			} else {
				throw new RuntimeException("答案只能填：对错是否√×【"+singleQuestion.toString()+"】");
			}
		}

		Elements analysisElement = ((Element) singleQuestion.get(analysisIndex)).getElementsByTag("span");
		analysisElement.html(analysisElement.html().substring(4));
		String analysis = getTxt(singleQuestion, analysisIndex, singleQuestion.size());;
		String analysis2 = Jsoup.clean(analysis, Whitelist.none());//不填写回只返回样式，特殊处理一下
		if (!ValidateUtil.isValid(analysis2)) { 
			analysis = null;
		}

		question.setTitle(title);
		question.setType(type);
		question.setDifficulty(getDifficulty(difficultyName));
		question.setAnswer(answer);
		question.setAnalysis(analysis);
		return question;
	}

	private String getTxt(List<Node> nodeList, int startIdx, int endIdx) {
		List<Node> subList = nodeList.subList(startIdx, endIdx);
		StringBuilder txt = new StringBuilder();
		for (Node node : subList) {
			txt.append(node.outerHtml());
		}
		
		return txt.toString();
	}

	private boolean containDifficulty(String difficultyName) {
		return getDifficulty(difficultyName) != null;
	}

	private Integer getDifficulty(String difficultyName) {
		if ("极易".equals(difficultyName)) {
			return 1;
		}
		if ("简单".equals(difficultyName)) {
			return 2;
		}
		if ("适中".equals(difficultyName)) {
			return 3;
		}
		if ("困难".equals(difficultyName)) {
			return 4;
		}
		if ("极难".equals(difficultyName)) {
			return 5;
		}
		return null;
	}

	private boolean containType(String typeName) {
		return getType(typeName) != null;
	}

	private Integer getType(String typeName) {
		if ("单选".equals(typeName)) {
			return 1;
		}
		if ("多选".equals(typeName)) {
			return 2;
		}
		if ("填空".equals(typeName)) {
			return 3;
		}
		if ("判断".equals(typeName)) {
			return 4;
		}
		if ("问答".equals(typeName)) {
			return 5;
		}
		return null;
	}
}
