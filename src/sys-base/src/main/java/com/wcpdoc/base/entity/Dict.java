package com.wcpdoc.base.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 数据字典实体
 * 
 * v1.0 zhanghc 2016-11-3下午9:03:40
 */
@Entity
@Table(name = "SYS_DICT")
public class Dict {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	@Column(name = "DICT_INDEX")
	private String dictIndex;
	@Column(name = "DICT_KEY")
	private String dictKey;
	@Column(name = "DICT_VALUE")
	private String dictValue;
	@Column(name = "NO")
	private Integer no;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDictIndex() {
		return dictIndex;
	}

	public void setDictIndex(String dictIndex) {
		this.dictIndex = dictIndex;
	}

	public String getDictKey() {
		return dictKey;
	}

	public void setDictKey(String dictKey) {
		this.dictKey = dictKey;
	}

	public String getDictValue() {
		return dictValue;
	}

	public void setDictValue(String dictValue) {
		this.dictValue = dictValue;
	}

	public Integer getNo() {
		return no;
	}

	public void setNo(Integer no) {
		this.no = no;
	}
}
