package com.wcpdoc.exam.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 公告实体
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
@Entity
@Table(name = "EXM_BULLETIN")
public class Bulletin {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	@Column(name = "TITLE")
	private String title;
	@Column(name = "IMG_FILE_ID")
	private String imgFileId;
	@Column(name = "VIDEO_FILE_ID")
	private String videoFileId;
	@Column(name = "CONTENT")
	private String content;
	@Column(name = "IMGS_HEIGHT")
	private Integer imgsHeight;
	@Column(name = "IMGS_WIDTH")
	private Integer imgsWidth;
	@Column(name = "URL")
	private String url;
	@Column(name = "TOP_STATE")
	private Integer topState;
	@Column(name = "NO")
	private Integer no;
	@Column(name = "STATE")
	private Integer state;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "UPDATE_TIME")
	private Date updateTime;
	@Column(name = "UPDATE_USER_ID")
	private Integer updateUserId;
	@Column(name = "READ_USER_IDS")
	private String readUserIds;
	@Column(name = "READ_ORG_IDS")
	private String readOrgIds;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
 
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getImgFileId() {
		return imgFileId;
	}

	public void setImgFileId(String imgFileId) {
		this.imgFileId = imgFileId;
	}

	public String getVideoFileId() {
		return videoFileId;
	}

	public void setVideoFileId(String videoFileId) {
		this.videoFileId = videoFileId;
	}

	public String getContent() {
		return content;
	}
 
	public void setContent(String content) {
		this.content = content;
	}
	
	public Integer getImgsHeight() {
		return imgsHeight;
	}

	public void setImgsHeight(Integer imgsHeight) {
		this.imgsHeight = imgsHeight;
	}
	
	public Integer getImgsWidth() {
		return imgsWidth;
	}

	public void setImgsWidth(Integer imgsWidth) {
		this.imgsWidth = imgsWidth;
	}
	
	public String getUrl() {
		return url;
	}
 
	public void setUrl(String url) {
		this.url = url;
	}
	
	public Integer getNo() {
		return no;
	}

	public void setNo(Integer no) {
		this.no = no;
	}
	
	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}
	
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	public Integer getUpdateUserId() {
		return updateUserId;
	}

	public void setUpdateUserId(Integer updateUserId) {
		this.updateUserId = updateUserId;
	}
	
	public String getReadUserIds() {
		return readUserIds;
	}
 
	public void setReadUserIds(String readUserIds) {
		this.readUserIds = readUserIds;
	}
	
	public String getReadOrgIds() {
		return readOrgIds;
	}
 
	public void setReadOrgIds(String readOrgIds) {
		this.readOrgIds = readOrgIds;
	}

	public Integer getTopState() {
		return topState;
	}

	public void setTopState(Integer topState) {
		this.topState = topState;
	}
}