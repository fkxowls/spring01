package com.spring.study.common.model;

public class BaseParam {
	private int startNum;
	private int endNum;
	private int page;
	private int pageSize;
	private boolean useMore;	
	private boolean useTotal;
	
	public static class Builder<T extends Builder<T>> {
		private int page;
		private int pageSize;
		private int startNum;
		private int endNum;
		private boolean useMore;
		private boolean useTotal;
		
		public Builder(int pageSize) {
			this.pageSize = pageSize;
		}
		public Builder(int startNum, int endNum) {
			this.startNum = startNum;
			this.endNum = endNum;
		}
		public T page(int page) {
			this.page = page;
			return (T) this;
		}
		public T useMore(boolean useMore) {
			this.useMore = useMore;
			return (T) this;
		}
		public T useTotal(boolean useTotal) {
			this.useTotal = useTotal;
			return (T) this;
		}
		public BaseParam build() {
			return new BaseParam(this);
		}
	}

	protected BaseParam(Builder<?> builder) { 
		if(0 == builder.page) {
			builder.page = 1;
		}
		
		if(0 == builder.startNum && 0 == builder.endNum) {
			builder.startNum = (builder.page - 1) * builder.pageSize + 1;
			builder.endNum = builder.page * builder.pageSize;
		}
		
		if(0 == builder.pageSize) {
			builder.pageSize = builder.endNum - builder.startNum + 1;
		}
		
		if(builder.useMore) {
			builder.endNum = builder.endNum + 1;
		}

		this.startNum = builder.startNum;
		this.endNum = builder.endNum;
		this.page = builder.page;
		this.pageSize = builder.pageSize;
		this.useMore = builder.useMore;
		this.useTotal = builder.useTotal;
	}
	
	public int getStartNum() {
		return startNum;
	}

	public int getEndNum() {
		return endNum;
	}

	public boolean useMore() {
		return useMore;
	}
	
	public boolean useTotal() {
		return useTotal;
	}

	public Integer getPage() { // TODO 세션캐싱에서 Object로 캐스팅하기 위함, 다른 방법 알아보고 걷어내야 함
		return page;
	}

	public Integer getPageSize() { // TODO 세션캐싱에서 Object로 캐스팅하기 위함, 다른 방법 알아보고 걷어내야 함
		return pageSize;
	}


	
}
