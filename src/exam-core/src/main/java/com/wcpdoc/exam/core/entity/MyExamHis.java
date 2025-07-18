package com.wcpdoc.exam.core.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 我的考试历史实体
 * 
 * v1.0 zhanghc 2025年7月12日下午1:53:07
 */
@Data
@TableName(value = "EXM_MY_EXAM_HIS", autoResultMap = true)
public class MyExamHis {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private Integer examId;
	private Integer userId;
	private Integer markUserId;
	private Date answerStartTime;
	private Date answerEndTime;
	private Date markStartTime;
	private Date markEndTime;
	private BigDecimal objectiveScore;
	private BigDecimal totalScore;
	private Integer state;
	private Integer markState;
	private Integer answerState;
	private Integer no;
	private Integer ver;
	private Integer updateUserId;
	private Date updateTime;

	/**
	 * 答题用时（毫秒）<br/>
	 * 分数排名专用
	 * 
	 * v1.0 zhanghc 2025年7月12日下午1:53:07
	 * 
	 * @return Long 毫秒值
	 */
	public Long getAnswerMs() {
		if (this.answerStartTime == null || this.answerEndTime == null) {
			return Long.MAX_VALUE;
		}
		return this.answerEndTime.getTime() - this.answerStartTime.getTime();
	}
}
