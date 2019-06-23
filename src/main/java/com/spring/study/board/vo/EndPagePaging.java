package com.spring.study.board.vo;

public class EndPagePaging {
	private int page;
	private int countList;
	private int totalCount;
	private int totalPage;
	private int startNum;
	private int endNum;
	
	public EndPagePaging(int page, int countList, int totalCount) {
		this.page=page;
		this.countList=countList;
		this.totalCount=totalCount;
	}
	
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getCountList() {
		return countList;
	}

	public void setCountList(int countList) {
		if (countList < 1)
			countList = 0;
		this.countList = countList;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getStartNum() {
		return (page-1)*countList +1;
	}

	public void setStartNum(int startNum) {
		this.startNum = startNum;
	}

	public int getEndNum() {
		 endNum = startNum + countList -1;
		 if(endNum>totalCount)
			 endNum = totalCount;
		return endNum;
	}

	public void setEndNum(int endNum) {
		this.endNum = endNum;
	}
	
	

}
