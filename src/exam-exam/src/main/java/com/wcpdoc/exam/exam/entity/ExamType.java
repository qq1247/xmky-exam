package com.wcpdoc.exam.exam.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import com.wcpdoc.exam.core.util.DateUtil;

/**
 * 考试分类实体
 * 
 * v1.0 zhanghc 2017-06-28 21:34:41
 */
@Entity
@Table(name = "EXM_EXAM_TYPE")
public class ExamType {
	@Id
	@GeneratedValue(generator = "generator")
	@GenericGenerator(name = "generator", strategy = "identity")
	@Column(name = "ID")
	private Integer id;
	@Column(name = "NAME")
	private String name;
	@Column(name = "PARENT_ID")
	private Integer parentId;
	@Column(name = "PARENT_SUB")
	private String parentSub;
	@Column(name = "UPDATE_USER_ID")
	private Integer updateUserId;
	@Column(name = "UPDATE_TIME")
	@DateTimeFormat(pattern = DateUtil.FORMAT_DATE_TIME)
	private Date updateTime;
	@Column(name = "STATE")
	private Integer state;
	@Column(name = "NO")
	private Integer no;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getParentSub() {
		return parentSub;
	}

	public void setParentSub(String parentSub) {
		this.parentSub = parentSub;
	}

	public Integer getUpdateUserId() {
		return updateUserId;
	}

	public void setUpdateUserId(Integer updateUserId) {
		this.updateUserId = updateUserId;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getNo() {
		return no;
	}

	public void setNo(Integer no) {
		this.no = no;
	}
}
