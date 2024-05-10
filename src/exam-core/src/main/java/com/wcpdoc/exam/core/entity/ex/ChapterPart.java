package com.wcpdoc.exam.core.entity.ex;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 试卷章节部分
 * 
 * v1.0 zhanghc 2024年2月28日下午7:20:35
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ChapterPart extends PaperPart {
	private String chapterName;
	private String chapterTxt;
}