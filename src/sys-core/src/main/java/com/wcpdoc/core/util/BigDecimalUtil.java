package com.wcpdoc.core.util;

import java.math.BigDecimal;

/**
 * 计算工具类
 * 
 * v1.0 zhanghc 2015-6-19下午08:30:16
 */
public class BigDecimalUtil {
	private BigDecimal result = null;

	private BigDecimalUtil(BigDecimal curValue) {
		this.result = curValue;
	}

	/**
	 * 获取实例
	 * 
	 * v1.0 zhanghc 2020年5月14日上午12:05:25
	 * 
	 * @param curValue
	 * @return BigDecimalUtil
	 */
	public static BigDecimalUtil newInstance(Object curValue) {
		validateValue(curValue);
		return new BigDecimalUtil(new BigDecimal(curValue.toString()));
	}

	private static void validateValue(Object curValue) {
		if (!(curValue instanceof String || curValue instanceof Integer || curValue instanceof Long
				|| curValue instanceof Double || curValue instanceof Float || curValue instanceof BigDecimal)) {
			throw new RuntimeException("参数错误：curValue");
		}
	}

	/**
	 * 相加
	 * 
	 * v1.0 zhanghc 2020年5月14日上午12:05:25
	 * 
	 * @param value 加数
	 * @return BigDecimalUtil
	 */
	public BigDecimalUtil add(Object value) {
		validateValue(value);
		result = result.add(new BigDecimal(value.toString()));
		return this;
	}

	/**
	 * 相减
	 * 
	 * v1.0 zhanghc 2020年5月14日上午12:05:25
	 * 
	 * @param value 减数
	 * @return BigDecimalUtil
	 */
	public BigDecimalUtil sub(Object value) {
		validateValue(value);
		result = result.subtract(new BigDecimal(value.toString()));
		return this;
	}

	/**
	 * 相乘
	 * 
	 * v1.0 zhanghc 2020年5月14日上午12:05:25
	 * 
	 * @param value乘数
	 * @return BigDecimalUtil
	 */
	public BigDecimalUtil mul(Object value) {
		validateValue(value);
		result = result.multiply(new BigDecimal(value.toString()));
		return this;
	}

	/**
	 * 相除
	 * 
	 * v1.0 zhanghc 2020年5月14日上午12:05:25
	 * 
	 * @param value 除数
	 * @param scale 保留小数位
	 * @return BigDecimalUtil
	 */
	public BigDecimalUtil div(Object value, int scale) {
		if (scale < 0) {
			throw new RuntimeException("参数错误：scale");
		}
		validateValue(value);
		result = result.divide(new BigDecimal(value.toString()), scale, BigDecimal.ROUND_HALF_UP);
		return this;
	}

	/**
	 * 获取结果
	 * 
	 * v1.0 zhanghc 2020年5月14日上午12:05:25
	 * 
	 * @return BigDecimal
	 */
	public BigDecimal getResult() {
		return result;
	}
}