package com.spring.study.board.vo;

import java.util.List;

//리퀘스용 DTO
public class PageDto<E> {
	private int page;
	private int pageSize;
	private int startNum;
	private int endNum;

	public static class Builder {
		private int page;
		private int pageSize;
		private int startNum;
		private int endNum;

		//필수 요소
		public Builder(int page, int pageSize) {
			this.page     = page;
			this.pageSize = pageSize;
		}
		//선택적인 요소
		public Builder startNum(int num) {
			startNum = num;
			return this;
		}
		
		public Builder endNum(int num) {
			endNum = num;
			return this;
		}

		public PageDto build() {
			return new PageDto(this);
		}
	}

	private PageDto(Builder builder) { // 리퀘스트 객체
		this.page	  = builder.page;
		this.pageSize = builder.pageSize;
		this.startNum = builder.startNum;
		this.endNum   = builder.endNum;
		/*
		 * this.startNum = builder.startNum; this.endNum = builder.endNum;
		 */
	}
	
	public int getStartNum() {
		return startNum;
	}

	public int getPage() {
		return page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getEndNum() {
		return endNum;
	}
	

}
