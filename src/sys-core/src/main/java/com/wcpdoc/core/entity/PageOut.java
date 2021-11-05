package com.wcpdoc.core.entity;

import java.util.List;
import java.util.Map;

/**
 * 页面输出
 * 
 * v1.0 zhanghc 2015-6-19下午08:30:16
 * @param <T>
 */
public class PageOut {
	private int total;// 总记录条数
	private List<Map<String, Object>> list;// 结果列表

	public PageOut() {

	}

	public PageOut(List<Map<String, Object>> list, int total) {
		this.total = total;
		this.list = list;
	}

	public int getTotal() {
		return total;
	}

	public PageOut setTotal(int total) {
		this.total = total;
		return this;
	}

	public List<Map<String, Object>> getList() {
		return list;
	}

	public PageOut setList(List<Map<String, Object>> list) {
		this.list = list;
		return this;
	}
}
