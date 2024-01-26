package com.wcpdoc.core.entity;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 页面输出
 * 
 * v1.0 zhanghc 2015-6-19下午08:30:16
 * 
 * @param <T>
 */
@Data
@AllArgsConstructor
public class PageOut {
	private List<Map<String, Object>> list;// 结果列表
	private long total;// 总记录条数
}
