package com.wcpdoc.exam.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 试题选项实体
 * 
 * v1.0 chenyun 2021-03-10 16:11:06
 */
@Data
@TableName(value = "EXM_QUESTION_OPTION", autoResultMap = true)
public class QuestionOption {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private String options;
	private Integer no;
	private Integer questionId;

}