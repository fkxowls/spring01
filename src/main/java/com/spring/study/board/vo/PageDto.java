package com.spring.study.board.vo;

import java.util.List;

public class PageDto<E> {
	private boolean hasNext = false;
	
	private int page;
	private int pageSize;
	private int totalCount;
	private Boolean test1;
	private Boolean test2;
	private Boolean test3;
	private Boolean test4;
	private Boolean test5;
	private int startNum;
	private int endNum;

    public static class Builder {
    	private int page;
    	private int pageSize;
    	
    	
    	private Boolean test1;
    	private Boolean test2;
    	private Boolean test3;
    	private Boolean test4;
    	private Boolean test5;

        public Builder(int page, int pageSize) {
            this.page = page;
            this.pageSize = pageSize;
        }
        
        public Builder test1(boolean test1) {
            this.test1 = test1;
            return this;
        }
        public Builder test2(boolean test2) {
            this.test2 = test2;
            return this;
        }
        public Builder test3(boolean test3) {
            this.test3 = test3;
            return this;
        }
        public Builder test4(boolean test4) {
            this.test4 = test4;
            return this;
        }
        public Builder test5(boolean test5) {
            this.test5 = test5;
            return this;
        }
        public PageDto build() {
            return new PageDto(this);
        }
    }
    
    private PageDto(Builder builder) { // 리퀘스트 객체
        this.page  = builder.page;
        this.pageSize     = builder.pageSize;
		this.test1 = builder.test1;
		this.test2 = builder.test2;
		this.test3 = builder.test3;
		this.test4 = builder.test4;
		this.test5 = builder.test5;
    }
    
    // 이 위로는 리퀘스트
    // 이 밑으로는 리스폰스

	public PageDto(int page, int pageSize, int totalCount, List<E> list/*, Boolean test1, Boolean test2, Boolean test3, Boolean test4, Boolean test5*/) { // 리스폰스 객체
		this.page = page;
		this.pageSize = pageSize;
		this.totalCount = totalCount;
		this.list = list;
		
		this.test1 = test1;
		this.test2 = test2;
		this.test3 = test3;
		this.test4 = test4;
		this.test5 = test5;
	}

	public Boolean getTest1() {
		return test1;
	}

	public void setTest1(Boolean test1) {
		this.test1 = test1;
	}

	public Boolean getTest2() {
		return test2;
	}

	public void setTest2(Boolean test2) {
		this.test2 = test2;
	}

	public Boolean getTest3() {
		return test3;
	}

	public void setTest3(Boolean test3) {
		this.test3 = test3;
	}

	public Boolean getTest4() {
		return test4;
	}

	public void setTest4(Boolean test4) {
		this.test4 = test4;
	}

	public Boolean getTest5() {
		return test5;
	}

	public void setTest5(Boolean test5) {
		this.test5 = test5;
	}



	private List<E> list;

	public int getNextPage() {
		return page + 1;
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

	public void setStartNum(int startNum) {
		this.startNum = startNum;
	}

	public int getEndNum() {
		return endNum;
	}

	public void setEndNum(int endNum) {
		this.endNum = endNum;
	}
	
}
