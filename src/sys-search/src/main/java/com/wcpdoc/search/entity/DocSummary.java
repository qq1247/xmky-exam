package com.wcpdoc.search.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文档摘要实体
 * 
 * v1.0 zhanghc 2026年1月7日下午11:33:10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocSummary {
	private String id;
	private String title;
	private List<String> summaryList;
}
