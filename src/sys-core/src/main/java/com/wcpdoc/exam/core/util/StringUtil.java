package com.wcpdoc.exam.core.util;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

public class StringUtil {
	/**
	 * 获取最后N行字符串
	 * 
	 * v1.0 zhanghc 2017年4月11日下午11:09:35
	 * 
	 * @param file 读取文件
	 * @param readNum 读取行数
	 * @param charset 字符集
	 * @return List<String>
	 */
	public static List<String> getLastLine(File file, int readNum, String charset) {
		// 校验数据有效性
		if (!file.exists() || file.isDirectory() || !file.canRead()) {
			throw new RuntimeException("参数无效：file");
		}
		if (readNum <= 0) {
			throw new RuntimeException("参数无效：readNum");
		}
		if (!ValidateUtil.isValid(charset)) {
			throw new RuntimeException("参数无效：charset");
		}

		// 读取最后N行记录
		long currentReadNum = 0;
		RandomAccessFile fileRead = null;
		List<String> result = new ArrayList<String>();
		try {
			fileRead = new RandomAccessFile(file, "r");// 只读模式
			long fileByteLength = fileRead.length();
			if (fileByteLength == 0L) {
				return result;
			}

			long pos = fileByteLength - 1;
			while (pos > 0) {
				pos--;
				fileRead.seek(pos);
				if (fileRead.readByte() == '\n') {
					String line = new String(fileRead.readLine().getBytes("iso-8859-1"), charset);
					result.add(line);
					currentReadNum++;
					if (currentReadNum == readNum) {
						break;
					}
				}
			}
			if (pos == 0) {
				fileRead.seek(0);
				result.add(new String(fileRead.readLine().getBytes("iso-8859-1"), charset));
			}

			Collections.reverse(result);
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(fileRead);
		}
	}

	/**
	 * 获取最后N行字符串
	 * 
	 * v1.0 zhanghc 2017年4月11日下午11:09:35
	 * 
	 * @param file 读取文件
	 * @param readNum 读取行数
	 * @return List<String>
	 */
	public static List<String> getLastLine(File file, int readNum) {
		return getLastLine(file, readNum, "utf-8");
	}

	/**
	 * 去除html标签
	 * 
	 * v1.0 zhanghc 2017年5月27日下午5:25:50
	 * 
	 * @param htmlStr
	 * @return String
	 */
	public static String delHTMLTag(String htmlStr) {
		String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
		String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
		String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

		Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
		Matcher m_script = p_script.matcher(htmlStr);
		htmlStr = m_script.replaceAll(""); // 过滤script标签

		Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
		Matcher m_style = p_style.matcher(htmlStr);
		htmlStr = m_style.replaceAll(""); // 过滤style标签

		Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
		Matcher m_html = p_html.matcher(htmlStr);
		htmlStr = m_html.replaceAll(""); // 过滤html标签

		return htmlStr; // 返回文本字符串。不要trim，比如第一个字符为\n会过滤掉
	}

	/**
	 * 字符串连接
	 * 
	 * v1.0 zhanghc 2017年6月26日下午1:52:55
	 * 
	 * @param strArr
	 * @param separator
	 * @return String
	 */
	public static String join(String[] strArr, String separator) {
		if (!ValidateUtil.isValid(strArr)) {
			throw new RuntimeException("无法获取参数：strArr");
		}

		StringBuilder sb = new StringBuilder();
		for (String str : strArr) {
			if (sb.length() > 0) {
				sb.append(separator);
			}
			sb.append(str);
		}
		return sb.toString();
	}

	/**
	 * 字符串连接
	 * 
	 * v1.0 zhanghc 2019年9月7日上午10:32:40
	 * 
	 * @param list
	 * @param separator
	 * @return String
	 */
	public static String join(Collection<String> collection, String separator) {
		if (!ValidateUtil.isValid(collection)) {
			throw new RuntimeException("无法获取参数：collection");
		}

		StringBuilder sb = new StringBuilder();
		for (String str : collection) {
			if (sb.length() > 0) {
				sb.append(separator);
			}
			sb.append(str);
		}
		return sb.toString();
	}

	/**
	 * 字符串连接
	 * 
	 * v1.0 zhanghc 2017年6月26日下午1:52:55
	 * 
	 * @param strArr 默认逗号分隔
	 * @return String
	 */
	public static String join(String[] strArr) {
		return join(strArr, ",");
	}

	/**
	 * 获取随机字符串（大小写字母+数字）
	 * 
	 * v1.0 zhanghc 2017年7月13日下午5:59:34
	 * 
	 * @param length
	 * @return String
	 */
	public static String getRandomStr(int length) {
		StringBuilder value = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			boolean isNum = random.nextInt(2) % 2 == 0;
			if (isNum) {
				value.append(random.nextInt(10));
				continue;
			}

			boolean lower = random.nextInt(2) % 2 == 0;
			if (lower) {
				value.append((char) (random.nextInt(26) + 65));
				continue;
			}

			value.append((char) (random.nextInt(26) + 97));
		}

		return value.toString().replaceAll("0", "2").replaceAll("o", "3").replaceAll("O", "4")
				.replaceAll("1", "5").replaceAll("l", "6").replaceAll("L", "7");
	}
}
