package com.wcpdoc.exam.core.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import org.docx4j.Docx4J;
import org.docx4j.Docx4jProperties;
import org.docx4j.convert.out.ConversionFeatures;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

import com.wcpdoc.exam.core.exception.MyException;

/**
 * word服务
 * 
 * v1.0 zhanghc 2019年7月19日下午11:15:52
 */
public abstract class WordServer {

	/**
	 * 文件处理
	 * 
	 * v1.0 zhanghc 2019年7月25日上午8:25:49
	 * 
	 * @param word word流
	 * @param imgDir 保存word图片路径
	 * @return List<T> 实现类decoder()返回值
	 */
	public <T> List<T> handle(InputStream word, String imgDir) {
		String html = word2Html(word, imgDir);
		Document document = Jsoup.parse(html);
		headStyle2ElementStyle(document);
		List<Node> rowNodes = document.getElementsByClass("document").get(0).childNodes();
		return decoder(rowNodes);
	}
	
	/**
	 * 头部样式分配到元素（页面展示数据的时候带头部样式不合适）
	 * 
	 * v1.0 zhanghc 2019年7月25日上午8:26:08
	 * 
	 * @param document
	 * void
	 */
	private void headStyle2ElementStyle(Document document) {
//		String styleTxt = document.head().select("style").html();
//		String[] styleArr = styleTxt.split("\n");
//		Map<String, String> cssAttr = new HashMap<>();
//		for (String style : styleArr) {
//			String[] split2 = style.split("\\{|\\}");
//			cssAttr.put(split2[0], split2[1]);
//		}
//
//		Element body = document.body();
//		for (String key : cssAttr.keySet()) {
//			Elements element = body.select(key);
//			element.removeAttr("class");
//			element.attr("style", cssAttr.get(key)).outerHtml();
//		}
	}

	/**
	 * 解码
	 * 
	 * v1.0 zhanghc 2019年7月25日上午8:25:56
	 * 
	 * @param nodeList
	 * @return T
	 */
	public abstract <T> T decoder(List<Node> nodeList);

	/**
	 * word转html
	 * 
	 * v1.0 zhanghc 2019年7月25日上午8:29:38
	 * 
	 * v3.0 zhanghc 2021-07-21 17:10:00
	 * poi切换为docx4j
	 * 参考官方文档：https://github.com/plutext/docx4j/blob/master/docx4j-samples-docx4j/src/main/java/org/docx4j/samples/ConvertOutHtml.java
	 * 
	 * @param word
	 * @param imgDir
	 * @return String
	 */
	private String word2Html(InputStream word, String imgDir) {
		WordprocessingMLPackage wordMLPackage;
		try {
			wordMLPackage = Docx4J.load(word);// 加载word流
		} catch (Docx4JException e) {
			throw new MyException(e.getMessage());
		}
		
		HTMLSettings htmlSettings = Docx4J.createHTMLSettings();// 自定义属性
		htmlSettings.setImageDirPath(imgDir);// 设置图片目录
		htmlSettings.setImageTargetUri(imgDir);// 设置图片链接目录。如：<img src="D:/bak/file/temp/966a5f76-e15c-4d43-a809-bf91ae393838image1.png"/>
		// htmlSettings.setImageHandler(null); // 提取图片处理耦合性太大，使用默认保存方式，业务层通过二次解析html提取图片处理
		htmlSettings.setOpcPackage(wordMLPackage);// 静态打开
    	htmlSettings.getFeatures().remove(ConversionFeatures.PP_HTML_COLLECT_LISTS);// 移除嵌套列表，如OL,UL
    	Docx4jProperties.setProperty("docx4j.Convert.Out.HTML.OutputMethodXML", true);
    	
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			Docx4J.toHTML(htmlSettings, os, Docx4J.FLAG_EXPORT_PREFER_XSL);// 写入到输出流
			return new String(os.toByteArray(), "utf-8");
		} catch (Exception e) {
    		throw new MyException(e.getMessage());
		}
	}
}
