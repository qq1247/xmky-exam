package com.wcpdoc.exam.sys.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 岗位资源实体
 * 
 * v1.0 zhanghc 2016-6-11下午8:57:40
 */
@Entity
@Table(name = "SYS_POST_RES")
public class PostRes {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	@Column(name = "POST_ID")
	private Integer postId;
	@Column(name = "RES_ID")
	private Integer resId;

	public PostRes() {
	}

	public PostRes(Integer postId, Integer resId) {
		this.postId = postId;
		this.resId = resId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getPostId() {
		return postId;
	}

	public void setPostId(Integer postId) {
		this.postId = postId;
	}

	public Integer getResId() {
		return resId;
	}

	public void setResId(Integer resId) {
		this.resId = resId;
	}
}
