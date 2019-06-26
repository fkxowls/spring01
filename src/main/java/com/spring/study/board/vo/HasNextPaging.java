package com.spring.study.board.vo;

import java.util.List;

public class HasNextPaging {
	private int startNum;
	private int endNum;
	private int pagePerCount;
	private int pageSize;
	private boolean isNext;
	
	private List<AticleVo> list;
	
	
	
	
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public List<AticleVo> getList() {
		return list;
	}
	public void setList(List<AticleVo> list, int listSize) {
		System.out.println("listSize() 		");
		System.out.print(listSize);
		this.list = list.subList(0, listSize);
	}
	public boolean isNext() {
		return isNext;
	}
	public boolean setNext(int listSize) {
		if(listSize < 11)
			return false;
		else
			return true;
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
	
	public void setStartNum(int page) {
		this.startNum = (page-1)*10+1;
	}
	
	public int getEndNum() {
		return endNum;
	}
	//
	public void setEndNum(int page) {
		this.endNum = page*10;
	}
	
}
