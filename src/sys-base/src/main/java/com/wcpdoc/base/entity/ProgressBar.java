package com.wcpdoc.base.entity;

import com.wcpdoc.core.util.BigDecimalUtil;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 进度条
 * 
 * v1.0 zhanghc 2020年10月13日下午12:03:02
 */
@Data
@AllArgsConstructor
public class ProgressBar {
	private String id;
	private Double curNum;
	private Double totalNum;
	private Integer code;
	private String msg;
	private Object data;

	/**
	 * 获取百分比
	 * 
	 * v1.0 zhanghc 2020年10月13日下午12:06:48
	 * 
	 * @return Double
	 */
	public Double getPercent() {
		return BigDecimalUtil.newInstance(curNum).div(totalNum, 4).mul(100).getResult().doubleValue();
	}
}
