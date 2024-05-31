package com.wcpdoc.core.util;

import java.io.File;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import com.wcpdoc.core.exception.MyException;

/**
 * 字符串工具类
 * 
 * @author zhanghc 2015-3-27下午04:58:24
 */
public class StringUtil {
	private static final Random random = new Random();

	/**
	 * 获取字符串
	 * 
	 * v1.0 zhanghc 2017年4月11日下午11:09:35
	 * 
	 * @param file      读取文件
	 * @param startByte 起始字节
	 * @param endByte   结束字节
	 * @param charset   字符集
	 * @return List<String>
	 */
	public static List<String> getString(File file, long startByte, long endByte, String charset) {
		// 数据校验
		if (!file.exists() || file.isDirectory() || !file.canRead()) {
			throw new MyException("参数无效：file");
		}
		if (startByte < 0) {
			throw new MyException("参数无效：startByte");
		}
		if (endByte < 0 || endByte < startByte) {
			throw new MyException("参数无效：endByte");
		}
		if (!ValidateUtil.isValid(charset)) {
			throw new MyException("参数无效：charset");
		}
		if (endByte - startByte > 100000) {
			throw new MyException("不能大于10w个字节");
		}

		// 读取指定位置的字符串
		RandomAccessFile fileRead = null;
		List<String> result = new ArrayList<String>();
		try {
			fileRead = new RandomAccessFile(file, "r");// 只读模式
			long fileByteLength = fileRead.length();
			if (fileByteLength == 0L) {
				return result;
			}
			if (startByte > fileByteLength) {
				return result;
			}

			StringBuilder str = new StringBuilder();
			for (long i = startByte; i < endByte; i++) {
				fileRead.seek(i);
				str.append((char) fileRead.read());
			}

			String[] strArr = new String(str.toString().getBytes("iso-8859-1"), charset).replaceAll("\r", "")
					.split("\n");
			return Arrays.asList(strArr);
		} catch (Exception e) {
			throw new MyException(e);
		} finally {
			IOUtils.closeQuietly(fileRead);
		}
	}

	/**
	 * 获取字符串
	 * 
	 * v1.0 zhanghc 2017年4月11日下午11:09:35
	 * 
	 * @param file      读取文件
	 * @param startByte 起始字节
	 * @param endByte   结束字节
	 * @return List<String>
	 */
	public static List<String> getString(File file, long startByte, long endByte) {
		return getString(file, startByte, endByte, "utf-8");
	}

	/**
	 * 获取最后N行字符串
	 * 
	 * v1.0 zhanghc 2017年4月11日下午11:09:35
	 * 
	 * @param file    读取文件
	 * @param readNum 读取行数
	 * @param charset 字符集
	 * @return List<String>
	 */
	public static List<String> getLastLine(File file, int readNum, String charset) {
		// 数据校验
		if (!file.exists() || file.isDirectory() || !file.canRead()) {
			throw new MyException("参数无效：file");
		}
		if (readNum <= 0) {
			throw new MyException("参数无效：readNum");
		}
		if (!ValidateUtil.isValid(charset)) {
			throw new MyException("参数无效：charset");
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
			throw new MyException(e);
		} finally {
			IOUtils.closeQuietly(fileRead);
		}
	}

	/**
	 * 获取最后N行字符串
	 * 
	 * v1.0 zhanghc 2017年4月11日下午11:09:35
	 * 
	 * @param file    读取文件
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
	public static String join(Object[] strArr, String separator) {
		if (!ValidateUtil.isValid(strArr)) {
			throw new MyException("参数错误：strArr");
		}

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Object str : strArr) {
//			if (sb.length() > 0) {// bug：如果第一个值为空字符串，会不执行
//				sb.append(separator);
//			}
			if (first) {
				first = false;
			} else {
				sb.append(separator);
			}
			sb.append(str);
		}
		return sb.toString();
	}

	/**
	 * 字符串连接
	 * 
	 * v1.0 zhanghc 2021年7月27日下午4:05:56
	 * 
	 * @param strArr
	 * @param separator
	 * @return String
	 */
	public static String join(Object[] strArr, char separator) {
//		if (!ValidateUtil.isValid(strArr)) {
		if (strArr == null) {
			throw new MyException("参数错误：strArr");
		}

		StringBuilder sb = new StringBuilder();
		for (Object str : strArr) {
//			if (sb.length() > 0) {// bug：数组第一个是空字符串，会导致添加不上分隔符
//				sb.append(separator);
//			}
			sb.append(str).append(separator);
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}

	/**
	 * 字符串连接
	 * 
	 * v1.0 zhanghc 2019年9月7日上午10:32:40
	 * 
	 * @param <T>
	 * 
	 * @param list
	 * @param separator
	 * @return String
	 */
	public static <T> String join(Collection<T> collection, String separator) {
		if (!ValidateUtil.isValid(collection)) {
			throw new MyException("参数错误：collection");
		}

		StringBuilder sb = new StringBuilder();
		for (Object str : collection) {
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
	 * @param strArr 默认英文逗号分隔
	 * @return String
	 */
	public static String join(Object[] strArr) {
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
		String value = getRandom(length);

		return value.replaceAll("0", "2").replaceAll("o", "3").replaceAll("O", "4").replaceAll("1", "5")
				.replaceAll("l", "6").replaceAll("L", "7");
	}

	/**
	 * 获取随机字符串（大小写字母+数字）
	 * 
	 * v1.0 zhanghc 2017年7月13日下午5:59:34
	 * 
	 * @param length
	 * @return String
	 */
	public static String getRandom(int length) {
		StringBuilder value = new StringBuilder();
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

		return value.toString();
	}

	/**
	 * 获取随机数字
	 * 
	 * v1.0 zhanghc 2017年7月13日下午5:59:34
	 * 
	 * @param length
	 * @return String
	 */
	public static String getRandomNum(int length) {
		StringBuilder value = new StringBuilder();
		for (int i = 0; i < length; i++) {
			value.append(random.nextInt(10));
		}

		return value.toString();
	}

	/**
	 * 字符串转int数组
	 * 
	 * v1.0 zhanghc 2021年7月29日下午6:07:14
	 * 
	 * @param strArr
	 * @return List<Integer>
	 */
	public static List<Integer> toIntList(String str) {
		if (!ValidateUtil.isValid(str)) {
			return new ArrayList<Integer>(0);
		}
		if (str.startsWith(",")) {
			str = str.substring(1);
		}
		if (str.endsWith(",")) {
			str = str.substring(0, str.length() - 1);
		}

		String[] strArr = str.split(",");
		List<Integer> list = new ArrayList<>(strArr.length);
		for (int i = 0; i < strArr.length; i++) {
			list.add(Integer.parseInt(strArr[i]));
		}

		return list;
	}

	public static List<BigDecimal> toBigDecimalList(String str) {
		if (!ValidateUtil.isValid(str)) {
			return new ArrayList<BigDecimal>(0);
		}
		if (str.startsWith(",")) {
			str = str.substring(1);
		}
		if (str.endsWith(",")) {
			str = str.substring(0, str.length() - 1);
		}

		String[] strArr = str.split(",");
		List<BigDecimal> list = new ArrayList<>(strArr.length);
		for (int i = 0; i < strArr.length; i++) {
			list.add(new BigDecimal(strArr[i]));
		}

		return list;
	}

	public static <T> String toStr(List<T> list) {
		if (!ValidateUtil.isValid(list)) {
			return null;
		}
		StringBuilder str = new StringBuilder();
		str.append(",");
		for (Object i : list) {
			str.append(i).append(",");
		}

		return str.toString();
	}
}
