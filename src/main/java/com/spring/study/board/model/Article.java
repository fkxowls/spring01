package com.spring.study.board.model;

import java.util.Date;

import org.apache.commons.lang3.time.FastDateFormat;

public class Article extends ArticleVo{
	public ArticleDto displayTitle() {
		ArticleDto articleDto= new ArticleDto();
		articleDto.setArticleId(getArticleId());
		articleDto.setTitle(getTitle());
		articleDto.setWriteMemberId(getWriteMemberId());//writer로 이름 바꾸기
		articleDto.setWriteDate(getWriteDate());
		articleDto.setPath("/board/"+super.getArticleId());
		//조회수 추가하기
		return articleDto;
	}
}
