package com.spring.study.board.vo;

//리퀘스용 DTO
public class CommonRequestDto {
	private int startNum;
	private int endNum;
	private int page;
	private int pageSize;
	private boolean moreView;
	private String writeMemberId;
	
	public static class Builder {
		private int page;
		private int pageSize;
		private int startNum;
		private int endNum;
		private boolean moreView;
		private String writeMemberId;
		

		//필수 요소
		public Builder() {
		}
		public Builder(int page, int pageSize) {
			this.page     = page;
			this.pageSize = pageSize;
		}
		//선택적인 요소
		public Builder bothStartNumEndNum(int startNum, int endNum) {
			this.startNum = startNum;
			this.endNum = endNum;
			return this;
		}
		public Builder useMoreView(boolean moreView) {
			this.moreView = moreView;
			return this;
		}
		
		public Builder setWriteMemberId(String writeMemberId) {
			this.writeMemberId = writeMemberId;
			return this;
		}
		
		public CommonRequestDto build() {
			return new CommonRequestDto(this);
		}
	}
	
	CommonRequestDto() {
		
	}

	protected CommonRequestDto(Builder builder) { // 리퀘스트 객체
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
		this.writeMemberId = builder.writeMemberId;
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

	public String getWriteMemberId() {
		return writeMemberId;
	}
	
}
