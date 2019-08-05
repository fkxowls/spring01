package com.spring.study.common.model;

import java.util.List;

public class PageList<E> {
	private boolean hasNext = false;	
	private int page;
	private int pageSize;
	private int totalCount;
	private int startNum;
	private int endNum;
	private List<E> list;
	
	public PageList(int page, int pageSize, List<E> list, int totalCount, boolean hasNext) { // 리스폰스 객체
		this.page = page;
		this.pageSize = pageSize;
		this.list = list;	
		this.totalCount = totalCount;
		this.hasNext = hasNext;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public int getTotalPage() {
		if(totalCount % pageSize == 0) {
			return totalCount / pageSize;
		} else {
			return totalCount / pageSize + 1;
		}
	}

	public boolean getHasNext() {
		return hasNext;
	}

	public int getPageSize() {
		return pageSize;
	}

	public List<E> getList() {
		return list;
	}

	public int getPage() {
		return page;
	}

	public int getStartNum() {
		return startNum;
	}


	public int getEndNum() {
		return endNum;
	}
	/*
	 * public void setStartNum(int startNum) { this.startNum = startNum; }
	 * 
	 * public void setEndNum(int endNum) { this.endNum = endNum; }
	 */}