package com.spring.study.model.article;

import com.spring.study.common.model.BaseParam;

//리퀘스용 DTO -> Parameter객체(공통코드)
public class BoardRequestDto extends BaseParam {
	private String boardId;

	private BoardRequestDto(Builder builder) { // 리퀘스트 객체
		super(builder);
		this.boardId = builder.boardId;
	}
	
	public static class Builder extends BaseParam.Builder {
		private String boardId;
		public Builder boardId(String boardId) {
			this.boardId = boardId;
			return this;
		}
		
		public BoardRequestDto build() {
			return new BoardRequestDto(this);
		}
	}

	public String getBoardId() {
		return boardId;
	}

	public void setBoardId(String boardId) {
		this.boardId = boardId;
	}
	
	
}
