package com.spring.study.board.vo;

public class HasNextPaging {
	int startNum;
	int endNum;
	int pagePerCount;
	int beginNum;
	boolean isNext;
	
	
	
	public boolean isNext() {
		return isNext;
	}
	public void setNext(boolean isNext) {
		this.isNext = isNext;
	}
	
	public int getBeginNum() {
		return beginNum;
	}
	
	public void setBeginNum(int beginNum) {
		this.beginNum = beginNum;
	}
	
	public int getPagePerCount() {
		return pagePerCount;
	}
	
	public void setPagePerCount(int pagePerCount) {
		this.pagePerCount = pagePerCount;
	}
	
	public int getStartNum() {
		return startNum;
	}
	
	public void setStartNum(int num) {
		startNum = num;
	}
	
	public int getEndNum() {
		return endNum;
	}
	//
	public void setEndNum(int num) {
		endNum = num+pagePerCount;
	}
	
}
