package com.wcpdoc.exam.core.service;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.binary.Base64;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

/**
 * word服务
 * 
 * v1.0 zhanghc 2019年7月19日下午11:15:52
 */
public abstract class WordServer {
	public WordServer() {

	}

	/**
	 * 文件处理
	 * 
	 * v1.0 zhanghc 2019年7月25日上午8:25:49
	 * 
	 * @param word
	 * @return List<T>
	 */
	public <T> List<T> handle(InputStream in) {
		Document document = Jsoup.parse(word2Html(in));
		headStyle2ElementStyle(document);
		List<Node> rowNodes = document.body().childNodes();
		return doDecoder(rowNodes);
	}

	/**
	 * 完成解码
	 * 
	 * v1.0 zhanghc 2019年7月25日上午8:25:56
	 * 
	 * @param rowNodes
	 * @return T
	 */
	public abstract <T> T doDecoder(List<Node> rowNodes);

	/**
	 * 头部样式分配到元素（页面展示数据的时候带头部样式不合适）
	 * 
	 * v1.0 zhanghc 2019年7月25日上午8:26:08
	 * 
	 * @param document
	 *            void
	 */
	private void headStyle2ElementStyle(Document document) {
		String styleTxt = document.head().select("style").html();
		String[] styleArr = styleTxt.split("\r\n");
		Map<String, String> cssAttr = new HashMap<>();
		for (String style : styleArr) {
			String[] split2 = style.split("\\{|\\}");
			if(split2[0].startsWith(".p")) {
				cssAttr.put(split2[0], split2[1] + "display: inline;");
			} else {
				cssAttr.put(split2[0], split2[1]);
			}
		}

		Element body = document.body();
		for (String key : cssAttr.keySet()) {
			Elements element = body.select(key);
			element.removeAttr("class");
			element.attr("style", cssAttr.get(key)).outerHtml();
		}
	}

	/**
	 * word转html
	 * 
	 * v1.0 zhanghc 2019年7月25日上午8:29:38
	 * 
	 * @param word
	 * @return String
	 */
	private String word2Html(InputStream word) {
		try {
			HWPFDocument wordDocument = new HWPFDocument(word);
			WordToHtmlConverter converter = new WordToHtmlConverter(
					DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
			converter.setPicturesManager(new PicturesManager() {
				@Override
				public String savePicture(byte[] content, PictureType pictureType, String suggestedName,
						float widthInches, float heightInches) {
					String type = pictureType.name();
					return "data:image/" + type + ";base64," + Base64.encodeBase64String(content);
				}
			});
			converter.processDocument(wordDocument);

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "html");
			StringWriter fos = new StringWriter();
			transformer.transform(new DOMSource(converter.getDocument()), new StreamResult(fos));
			// fos.flush();
			wordDocument.close();
			return fos.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
