package com.spring.study.model.user;

import org.apache.commons.lang3.StringUtils;

import com.spring.study.model.article.ArticleDto;

public class User extends UserVo{
	
	public boolean isLogon() {
		return StringUtils.isNotEmpty(super.getUserId());
	}
	
	public boolean checkUserId(String writerId) {
		if(StringUtils.equals(writerId, super.getUserId())) {
			return true;
		}
		return false;
	}
	
	public ArticleDto getUserInfo() {
		ArticleDto articleDto = new ArticleDto();
		articleDto.setWriteMemberId(super.getUserId());
		
		return articleDto;
	}
}
