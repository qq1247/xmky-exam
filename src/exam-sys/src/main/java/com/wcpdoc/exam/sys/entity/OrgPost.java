package com.wcpdoc.exam.sys.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * 机构岗位实体
 * 
 * v1.0 zhanghc 2016-5-19下午9:32:43
 */
@Entity
@Table(name = "SYS_ORG_POST")
public class OrgPost {
	@Id
	@GeneratedValue(generator = "generator")
	@GenericGenerator(name = "generator", strategy = "identity")
	@Column(name = "ID")
	private Integer id;
	@Column(name = "ORG_ID")
	private Integer orgId;
	@Column(name = "POST_ID")
	private Integer postId;

	public OrgPost() {
	}
	
	public OrgPost(Integer orgId, Integer postId) {
		this.orgId = orgId;
		this.postId = postId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getOrgId() {
		return orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	public Integer getPostId() {
		return postId;
	}

	public void setPostId(Integer postId) {
		this.postId = postId;
	}
}
