package com.wcpdoc.exam.core.entity;

import java.util.List;
import java.util.Map;

public class PageOut {
	private int total;// 总记录条数
	private List<Map<String, Object>> rows;// 结果集

	public PageOut() {

	}

	public PageOut(List<Map<String, Object>> rows, int total) {
		this.total = total;
		this.rows = rows;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<Map<String, Object>> getRows() {
		return rows;
	}

	public void setRows(List<Map<String, Object>> rows) {
		this.rows = rows;
	}
}
