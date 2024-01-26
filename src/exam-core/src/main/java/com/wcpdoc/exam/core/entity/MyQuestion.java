package com.wcpdoc.exam.core.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wcpdoc.core.mybatis.BigDecimalTypeHandler;
import com.wcpdoc.core.mybatis.IntTypeHandler;

import lombok.Data;

/**
 * 我的试题实体
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Data
@TableName(value = "EXM_MY_QUESTION", autoResultMap = true)
public class MyQuestion {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private String chapterName;
	private String chapterTxt;
	private Integer type;
	private BigDecimal score;
	@TableField(typeHandler = BigDecimalTypeHandler.class)
	private List<BigDecimal> scores;
	@TableField(typeHandler = IntTypeHandler.class)
	private List<Integer> markOptions;
	private Integer examId;
	private Integer questionId;
	private Integer userId;
	private Integer no;
	@TableField(typeHandler = IntTypeHandler.class)
	private List<Integer> optionsNo;
	private Date answerTime;
	private String userAnswer;
	private BigDecimal userScore;
	private Integer markUserId;
	private Date markTime;
	private Integer updateUserId;
	private Date updateTime;

	public Map<String, String> getOptionsNoCache() {// 4,1,3,2 => {D:A, A:B, C:C, B:D}
		Map<String, String> cache = new HashMap<>();
		for (int i = 0; i < getOptionsNo().size(); i++) {
			cache.put(String.format("%s", (char) (64 + getOptionsNo().get(i))), String.format("%s", (char) (65 + i)));
		}
		return cache;
	}

	public Map<String, String> getOptionsNoCacheOfAnswer() {// 4,1,3,2 => {A:D, B:A, C:C, D:B}
		Map<String, String> cache = new HashMap<>();
		for (int i = 0; i < getOptionsNo().size(); i++) {
			cache.put(String.format("%s", (char) (65 + i)), String.format("%s", (char) (64 + getOptionsNo().get(i))));
		}
		return cache;
	}
}
