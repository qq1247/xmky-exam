package com.wcpdoc.exam.core.util;

import java.math.BigDecimal;

/**
 * 计算工具类
 * 
 * v1.0 zhanghc 2015-6-19下午08:30:16
 */
public class BigDecimalUtil {
	/**
	 * 提供精确加法计算的add方法
	 * 
	 * @param value1  被加数
	 * @param value2 加数
	 * @return 两个参数的和
	 */
	public static String add(String value1, String value2) {
		BigDecimal b1 = new BigDecimal(value1);
		BigDecimal b2 = new BigDecimal(value2);
		return b1.add(b2).toString();
	}

	/**
	 * 提供精确减法运算的sub方法
	 * 
	 * @param value1 被减数
	 * @param value2 减数
	 * @return 两个参数的差
	 */
	public static String sub(String value1, String value2) {
		BigDecimal b1 = new BigDecimal(value1);
		BigDecimal b2 = new BigDecimal(value2);
		return b1.subtract(b2).toString();
	}

	/**
	 * 提供精确乘法运算的mul方法
	 * 
	 * @param value1 被乘数
	 * @param value2 乘数
	 * @return 两个参数的积
	 */
	public static String mul(String value1, String value2) {
		BigDecimal b1 = new BigDecimal(value1);
		BigDecimal b2 = new BigDecimal(value2);
		return b1.multiply(b2).toString();
	}

	/**
	 * 提供精确的除法运算方法div
	 * 
	 * @param value1 被除数
	 * @param value2 除数
	 * @param scale 精确范围
	 * @return 两个参数的商
	 * @throws IllegalAccessException
	 */
	public static String div(String value1, String value2, int scale) {
		if (scale < 0) {
			throw new RuntimeException("精确度不能小于0");
		}
		BigDecimal b1 = new BigDecimal(value1);
		BigDecimal b2 = new BigDecimal(value2);
		return b1.divide(b2, scale).toString();
	}
}