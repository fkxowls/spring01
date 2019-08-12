package com.spring.study.board.model;

import com.spring.study.common.model.CommonRequestDto;

//리퀘스용 DTO
public class BoardRequestDto extends CommonRequestDto {
	private String boardId;

	private BoardRequestDto(Builder builder) { // 리퀘스트 객체
		super(builder);
		this.boardId = builder.boardId;
	}
	
	public static class Builder extends CommonRequestDto.Builder {
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
