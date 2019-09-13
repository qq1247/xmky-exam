package com.wcpdoc.exam.sys.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 岗位用户实体
 * 
 * v1.0 zhanghc 2016-6-25下午10:47:45
 */
@Entity
@Table(name = "SYS_POST_USER")
public class PostUser {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	@Column(name = "POST_ID")
	private Integer postId;
	@Column(name = "USER_ID")
	private Integer userId;

	public PostUser() {
	}

	public PostUser(Integer postId, Integer userId) {
		this.postId = postId;
		this.userId = userId;
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

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}
}
