package com.wcpdoc.exam.core.entity.ex;

/**
 * 考试规则扩展
 * 
 * v1.0 chenyun 2022年3月8日上午10:55:29
 */
public class ExamRuleEx {
	private Integer type;
	private Integer markType;
	private Integer num;
	
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public Integer getType() {
		return type;
	}
	public Integer getMarkType() {
		return markType;
	}
	public ExamRuleEx(Integer type, Integer markType) {
		this.type = type;
		this.markType = markType;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((markType == null) ? 0 : markType.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExamRuleEx other = (ExamRuleEx) obj;
		if (markType == null) {
			if (other.markType != null)
				return false;
		} else if (!markType.equals(other.markType))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}
