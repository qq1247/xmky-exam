package com.wcpdoc.exam.wordFilter.util;

import java.util.List;

import com.startx.http.wordfilter.WordContext;
import com.startx.http.wordfilter.WordFilter;

/**
 * 敏感词过滤
 * 
 * v1.0 chenyun 2021年9月3日下午3:06:11
 */
public class SensitiveUtil {
	
    /**
     * 词库上下文环境
     */
    private final static WordContext context = new WordContext();
    private final static WordFilter filter = new WordFilter(context);
    
    /**
     * 测试替换敏感词
     */
    public static String replace(String text) {
    	return filter.replace(text);
    }

    /**
     * 测试是否包含敏感词
     */
    public static boolean include(String text) {
        return filter.include(text);
    }

    /**
     * 获取敏感词数
     */
    public static int wordCount(String text) {
        return filter.wordCount(text);
    }

    /**
     * 获取敏感词列表
     */
    public static List<String> wordList(String text) {
        return filter.wordList(text);
    }
}
