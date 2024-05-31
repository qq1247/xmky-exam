package com.wcpdoc.exam.core.constant;

/**
 * 考试常量
 * 
 * v1.0 zhanghc 2016年9月6日下午6:45:12
 */
public class ExamConstant {
	/**
	 * 试题缓存<br/>
	 * 1、考前缓存试题信息，提高整体考试性能
	 */
	public static final String QUESTION_CACHE = "QUESTION_CACHE";
	public static final String QUESTION_KEY_PRE = "'QUESTION:' + ";
	public static final String QUESTION_OPTION_KEY_PRE = "'QUESTION:OPTION:' + ";
	public static final String QUESTION_ANSWER_KEY_PRE = "'QUESTION:ANSWER:' + ";

	/**
	 * 考试缓存<br/>
	 * 1、考前缓存考试信息，提高整体考试性能<br/>
	 * 2、检测考试结束时，自动结束考试<br/>
	 */
	public static final String EXAM_CACHE = "EXAM_CACHE";
	public static final String EXAM_KEY_PRE = "'EXAM:' + ";
	public static final String EXAMING_LIST_KEY = "'EXAMING_LIST'";

	/**
	 * 我的考试缓存<br/>
	 * 1、考前缓存用户考试信息，提高整体考试性能 <br/>
	 * 2、检测用户交卷、用户考试结束时间到，自动批阅试卷<br/>
	 */
	public static final String MYEXAM_CACHE = "MYEXAM_CACHE";
	public static final String MYEXAM_KEY_PRE = "'MYEXAM:' + ";
	public static final String MYEXAM_LIST_KEY_PRE = "'MYEXAM:LIST:' + ";
	public static final String MYEXAM_UNMARK_LIST_KEY = "'UNMARK_LIST'";

	/**
	 * 我的试题缓存<br/>
	 * 1、考前缓存试卷信息，提高整体考试性能
	 */
	public static final String MYQUESTION_CACHE = "MYQUESTION_CACHE";
	public static final String MYQUESTION_LIST_KEY_PRE = "'MYQUESTION_LIST:' + ";
}
