package com.wcpdoc.exam.core.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 题库实体
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
@Data
@TableName(value = "EXM_QUESTION_TYPE", autoResultMap = true)
public class QuestionType {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private String name;
	private Integer createUserId;
	private Integer updateUserId;
	private Date updateTime;
	/** 客观题数量*/
	private Integer objectiveNum;
	/** 主观题数量*/
	private Integer subjectiveNum;
	/** 单选题数量*/
	private Integer singleNum;
	/** 多选题数量*/
	private Integer multipleNum;
	/** 判断题数量*/
	private Integer judgeNum;
	/** 填空题数量*/
	private Integer blankNum;
	/** 简答题数量*/
	private Integer shortAnswerNum;
	/** 试题总数量*/
	private Integer questionNum;
}
