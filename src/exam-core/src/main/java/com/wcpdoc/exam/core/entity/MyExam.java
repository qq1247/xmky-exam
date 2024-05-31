package com.wcpdoc.exam.core.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 我的考试实体
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Data
@TableName(value = "EXM_MY_EXAM", autoResultMap = true)
public class MyExam {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private Integer examId;
	private Integer userId;
	private Integer markUserId;
	private Date examStartTime;
	private Date examEndTime;
	private Date markStartTime;
	private Date markEndTime;
	private BigDecimal objectiveScore;
	private BigDecimal totalScore;
	private Integer state;
	private Integer markState;
	private Integer answerState;
	private Integer updateUserId;
	private Date updateTime;
	private Integer no;

	/**
	 * 答题用时（毫秒）<br/>
	 * 分数排名专用
	 * 
	 * v1.0 zhanghc 2023年5月10日上午9:22:57
	 * 
	 * @return Long 毫秒值
	 */
	public Long getAnswerMs() {
		if (this.examStartTime == null || this.examEndTime == null) {
			return Long.MAX_VALUE;
		}
		return this.examEndTime.getTime() - this.examStartTime.getTime();
	}
}
