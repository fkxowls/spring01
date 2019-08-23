package com.spring.study.model.article;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.spring.study.dao.ArticleDao;
import com.spring.study.model.user.User;

public class Article extends ArticleVo {
	private static final int articleIdLength = 5;
	
	public static final boolean checkId(String articleId) {
		if(articleIdLength != articleId.length()) {
			return false;
		}
		return true;
	}
	
	@Autowired
	ArticleDao dao;
	
	private List<String> accessLevelList; // Board의 하위테이블에 적재된 데이터
	
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
		articleDto.setParentId(getParentId());//오라클 충돌로 현재 확인 못해봄
		articleDto.setTitle(getTitle());
		articleDto.setContent(getContent());
		articleDto.setWriteMemberId(getWriteMemberId());
		articleDto.setWriteDate(getWriteDate());
		//dao.viewArticle("10150");
		
		return articleDto;
	}
	
	public boolean checkUserId(String userId) {
		if(StringUtils.equals(userId, super.getWriteMemberId())) {
			return true;
		}
		
		return false;
	}

	public List<String> getAccessLevelList() {
		if(null == accessLevelList) {
			accessLevelList = new ArrayList<>();
		}
		
		return accessLevelList;
	}

	public void setAccessLevelList(List<String> accessLevelList) {
		this.accessLevelList = accessLevelList;
	}
	
	public boolean checkAccessLevel(User user) {
		if(this.getAccessLevelList().contains(user.getMemberLevel())) {
			return true;
		}
		
		return false;
	}
}
