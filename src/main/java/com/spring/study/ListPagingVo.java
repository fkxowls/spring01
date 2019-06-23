package com.spring.study;

//Vo와 DTO둘다 만든다 DTO는 Vo를 상속받아 몇가지 예외처리 및 계산처리를 해야한다
public class ListPagingVo {
	private int totalCount;
	private int startNum;
	private int endNum;
	private int endPage;
	private int pagePerCount;
	// private int 더보기 버튼 //jsp에서 if문으로 처리???? 뷰단에 jstl이 많아도 상관이 없는가?

	public int getPagePerCount() {
		return pagePerCount;
	}

	public int getEndNum() {
		return endNum;
	}

	public void setEndNum(int endNum) {
		this.endNum = endNum;
	}

	public void setPagePerCount(int pagePerCount) {
		this.pagePerCount = pagePerCount;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getStartNum() {
		return startNum;
	}

	// ??
	public void setStartNum(int startNum) {
		if (startNum > totalCount) {
			
			this.startNum = startNum - pagePerCount;
		} else {
			this.startNum = startNum;
		}
	}

	public int getEndPage() {
		return endPage;
	}

	public void setEndPage(int endPage) {
		this.endPage = endPage;
	}

	// vo에서 이렇게 해도 되는가???
	public void setEndPage(int totalCount, int pagePerCount) {
		if (totalCount % pagePerCount > 0) {
			this.endPage = (totalCount / pagePerCount) + 1;

		} else {
			this.endPage = totalCount / pagePerCount;
		}
	}

}
