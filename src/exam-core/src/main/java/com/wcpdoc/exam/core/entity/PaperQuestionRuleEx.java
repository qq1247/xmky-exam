package com.wcpdoc.exam.core.entity;

/**
 * 随机试题扩展
 * 
 * v1.0 chenyun 2022年3月8日上午10:55:29
 */
public class PaperQuestionRuleEx {
	private Integer type;
	private Integer difficulty;
	private Integer ai;
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
	public Integer getDifficulty() {
		return difficulty;
	}
	public Integer getAi() {
		return ai;
	}
	public PaperQuestionRuleEx(Integer type, Integer difficulty, Integer ai) {
		this.type = type;
		this.difficulty = difficulty;
		this.ai = ai;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ai == null) ? 0 : ai.hashCode());
		result = prime * result + ((difficulty == null) ? 0 : difficulty.hashCode());
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
		PaperQuestionRuleEx other = (PaperQuestionRuleEx) obj;
		if (ai == null) {
			if (other.ai != null)
				return false;
		} else if (!ai.equals(other.ai))
			return false;
		if (difficulty == null) {
			if (other.difficulty != null)
				return false;
		} else if (!difficulty.equals(other.difficulty))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}
