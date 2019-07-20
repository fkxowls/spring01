package com.spring.study.board.vo;

import java.util.List;

public class HasNextPaging {
	private int startNum;
	private int endNum;
	private int pagePerCount;
	private int pageSize;
	private boolean hasNext;
	
	private List<ArticleVo> list;
	
	
	
	
	public boolean isHasNext() {
		return hasNext;
	}
	public void setHasNext(boolean hasNext) {
		this.hasNext = hasNext;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public List<ArticleVo> getList() {
		return list;
	}
	public void setList(List<ArticleVo> list, int listSize) {
		System.out.println("pageSize() 		");
		System.out.print(this.pageSize);
		System.out.println();
		if(listSize > this.pageSize) {
			System.out.println("같다");
			this.list = list.subList(0, listSize-1);
		}else {
			System.out.println("작다");
			this.list = list.subList(0, listSize);
		}
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
		this.endNum = page*10+1;
	}
	
}
