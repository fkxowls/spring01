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
	
	private List<String> accessLevelList; // Board의 하위테이블에 적재된 데이터
	
	public ArticleDto displayTitle() {
		ArticleDto articleDto= new ArticleDto();
		articleDto.setArticleId(getArticleId());
		articleDto.setTitle(getTitle());
		articleDto.setWriteMemberId(getWriteMemberId());
		articleDto.setWriteDate(getWriteDate());
		articleDto.setPath("/board/"+super.getArticleId());
		//TODO 조회수 추가하기
		return articleDto;
	}
	
	public ArticleDto showArticle() {
		ArticleDto articleDto = new ArticleDto();
		articleDto.setArticleId(getArticleId());
		articleDto.setParentId(getParentId());
		articleDto.setTitle(getTitle());
		articleDto.setContent(getContent());
		articleDto.setWriteMemberId(getWriteMemberId());
		articleDto.setWriteDate(getWriteDate());
		
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
		if(this.getAccessLevelList().contains(user.getUserLevel())) {
			return true;
		}
		
		return false;
	}
}
