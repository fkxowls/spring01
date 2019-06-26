package com.spring.study.board.vo;

import java.util.List;

public class EndPagePaging {
	private int page;
	private int totalCount;
	private int totalPage;
	private int pageSize;
	private int startNum;
	private int endNum;
	
	private boolean hasNext;

	private List<EndPagePaging> epp;
	private List<AticleVo> list;
	
	

	public List<EndPagePaging> getEpp() {
		return epp;
	}

	public void setEpp(List<EndPagePaging> epp) {
		this.epp = epp;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getTotalPage() {
		if (totalCount % pageSize == 0)
			totalPage = totalCount / pageSize;
		else
			totalPage = totalCount/pageSize+1;

		return totalPage;
	}
	/*
	 * public void setTotalPage(int totalPage) {
	 * 
	 * this.totalPage = totalPage; }
	 */

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

	public List<AticleVo> getList() {
		return list;
	}

	public void setList(List<AticleVo> list) {
		this.list = list;
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

	public void setEndNum(int page) {
		this.endNum = page*10;
	}

	
}
