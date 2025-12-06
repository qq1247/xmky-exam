package com.wcpdoc.exam.core.entity;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wcpdoc.core.mybatis.IntTypeHandler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 我的评论实体
 * 
 * v1.0 chenyun 2021年8月31日上午9:46:45
 */
@Data
@TableName(value = "EXM_MY_COMMENT", autoResultMap = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyComment {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private Integer userId;
	private Integer questionId;
	private String content;
	@TableField(typeHandler = IntTypeHandler.class)
	private List<Integer> likeUserIds;
	private Integer likeNum;
	private Integer state;
	private Integer replyUserId;
	private Integer parentId;
	private Integer rootId;
	private Integer updateUserId;
	private Date updateTime;
}
