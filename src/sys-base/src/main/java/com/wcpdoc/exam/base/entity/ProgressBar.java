package com.wcpdoc.exam.base.entity;

import com.wcpdoc.exam.core.util.BigDecimalUtil;

/**
 * 进度条
 * 
 * v1.0 zhanghc 2020年10月13日下午12:03:02
 */
public class ProgressBar {
	private String id;
	private Double curNum;
	private Double totalNum;
	private String msg;
	
	public ProgressBar() {
	}

	public ProgressBar(String id, Double curNum, Double totalNum, String msg) {
		this.id = id;
		this.curNum = curNum;
		this.totalNum = totalNum;
		this.msg = msg;
	}

	/**
	 * 获取百分比
	 * 
	 * v1.0 zhanghc 2020年10月13日下午12:06:48
	 * @return Double
	 */
	public Double getPercent() {
		return BigDecimalUtil.newInstance(curNum).div(totalNum, 4).mul(100).getResult().doubleValue();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Double getCurNum() {
		return curNum;
	}

	public void setCurNum(Double curNum) {
		this.curNum = curNum;
	}

	public Double getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(Double totalNum) {
		this.totalNum = totalNum;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
