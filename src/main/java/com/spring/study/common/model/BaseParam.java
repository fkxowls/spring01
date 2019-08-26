package com.spring.study.common.model;

//리퀘스용 DTO -> Parameter객체
public class BaseParam {
	private int startNum;
	private int endNum;
	private int page;
	private int pageSize;
	private boolean moreView;	
	
	public static class Builder<T extends Builder<T>> {
		private int page;
		private int pageSize;
		private int startNum;
		private int endNum;
		private boolean moreView;
		
		//필수 요소
		public Builder(int page, int pageSize) {
			this.page     = page;
			this.pageSize = pageSize;
		}
		//선택적인 요소
		public T bothStartNumEndNum(int startNum, int endNum) {
			this.startNum = startNum;
			this.endNum = endNum;
			return (T) this;
		}
		public T useMoreView(boolean moreView) {
			this.moreView = moreView;
			return (T) this;
		}
		
		public BaseParam build() {
			return new BaseParam(this);
		}
	}

	protected BaseParam(Builder<?> builder) { // 리퀘스트 객체
		if(0 == builder.startNum || 0 == builder.endNum) {
			builder.startNum = (builder.page - 1) * builder.pageSize + 1;
			builder.endNum = builder.page * builder.pageSize;
		}
		if(builder.moreView) {
			builder.endNum = builder.endNum + 1;
		}

		this.startNum = builder.startNum;
		this.endNum = builder.endNum;
		this.page = builder.page;
		this.pageSize = builder.pageSize;
		this.moreView = builder.moreView;
	}
	
	public int getStartNum() {
		return startNum;
	}

	public int getEndNum() {
		return endNum;
	}

	public boolean moreView() {
		return moreView;
	}

	public int getPage() {
		return page;
	}

	public int getPageSize() {
		return pageSize;
	}


	
}
