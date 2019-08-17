package com.spring.study.model.article;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.spring.study.dao.ArticleDao;

public class Article extends ArticleVo{
	
	@Autowired
	ArticleDao dao;
	
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
	
	public ArticleDto showArticle() {
		ArticleDto articleDto = new ArticleDto();
		articleDto.setArticleId(getArticleId());
		articleDto.setTitle(getTitle());
		articleDto.setContent(getContent());
		articleDto.setWriteMemberId(getWriteMemberId());
		articleDto.setWriteDate(getWriteDate());
		//dao.viewArticle("10150");
		
		return articleDto;
	}
	
	public boolean isEqualsWriterId(String userId) {
		if(StringUtils.equals(userId, super.getWriteMemberId())) {
			return true;
		}
		return false;
	}
}
