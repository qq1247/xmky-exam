package com.wcpdoc.exam.core.entity;

/**
 * 页面输入
 * 
 * v1.0 zhanghc 2015-6-19下午08:30:16
 */
public class PageIn {
	/* 分页信息 */
	private int curPage = 1;//当前第几页
	private int pageSize = 20;//每页多少条，最大100条；

	/* 查询参数 */
	private String one;
	private String two;
	private String three;
	private String four;
	private String five;
	private String six;
	private String seven;
	private String eight;
	private String nine;
	private String ten;
	
	/* 排序参数 */
	private String sortOne;
	private String sortTwo;
	private String sortThree;
	private String sortFour;
	private String sortFive;
	private String sortSix;
	private String sortSeven;
	private String sortEight;
	private String sortNine;
	private String sortTen;

	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize > 100 ? 100 : pageSize;
	}

	public String getOne() {
		return one;
	}

	public void setOne(String one) {
		this.one = one;
	}

	public String getTwo() {
		return two;
	}

	public void setTwo(String two) {
		this.two = two;
	}

	public String getThree() {
		return three;
	}

	public void setThree(String three) {
		this.three = three;
	}

	public String getFour() {
		return four;
	}

	public void setFour(String four) {
		this.four = four;
	}

	public String getFive() {
		return five;
	}

	public void setFive(String five) {
		this.five = five;
	}

	public String getSix() {
		return six;
	}

	public void setSix(String six) {
		this.six = six;
	}

	public String getSeven() {
		return seven;
	}

	public void setSeven(String seven) {
		this.seven = seven;
	}

	public String getEight() {
		return eight;
	}

	public void setEight(String eight) {
		this.eight = eight;
	}

	public String getNine() {
		return nine;
	}

	public void setNine(String nine) {
		this.nine = nine;
	}

	public String getTen() {
		return ten;
	}

	public void setTen(String ten) {
		this.ten = ten;
	}

	public String getSortOne() {
		return sortOne;
	}

	public void setSortOne(String sortOne) {
		this.sortOne = sortOne;
	}

	public String getSortTwo() {
		return sortTwo;
	}

	public void setSortTwo(String sortTwo) {
		this.sortTwo = sortTwo;
	}

	public String getSortThree() {
		return sortThree;
	}

	public void setSortThree(String sortThree) {
		this.sortThree = sortThree;
	}

	public String getSortFour() {
		return sortFour;
	}

	public void setSortFour(String sortFour) {
		this.sortFour = sortFour;
	}

	public String getSortFive() {
		return sortFive;
	}

	public void setSortFive(String sortFive) {
		this.sortFive = sortFive;
	}

	public String getSortSix() {
		return sortSix;
	}

	public void setSortSix(String sortSix) {
		this.sortSix = sortSix;
	}

	public String getSortSeven() {
		return sortSeven;
	}

	public void setSortSeven(String sortSeven) {
		this.sortSeven = sortSeven;
	}

	public String getSortEight() {
		return sortEight;
	}

	public void setSortEight(String sortEight) {
		this.sortEight = sortEight;
	}

	public String getSortNine() {
		return sortNine;
	}

	public void setSortNine(String sortNine) {
		this.sortNine = sortNine;
	}

	public String getSortTen() {
		return sortTen;
	}

	public void setSortTen(String sortTen) {
		this.sortTen = sortTen;
	}
}
