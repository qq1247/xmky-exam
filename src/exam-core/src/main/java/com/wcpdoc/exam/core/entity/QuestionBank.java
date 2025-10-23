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
@TableName(value = "EXM_QUESTION_BANK", autoResultMap = true)
public class QuestionBank {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private String name;
	private Integer objectiveNum;
	private Integer subjectiveNum;
	private Integer singleNum;
	private Integer multipleNum;
	private Integer fillBlankSubNum;
	private Integer fillBlankObjNum;
	private Integer judgeNum;
	private Integer qaSubNum;
	private Integer qaObjNum;
	private Integer questionNum;
	private Integer state;
	private Integer createUserId;
	private Integer updateUserId;
	private Date updateTime;
}
