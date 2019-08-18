package com.spring.study.model.member;

import org.apache.commons.lang3.StringUtils;

import com.spring.study.model.article.ArticleDto;

public class User extends UserVo{
	
	public boolean isLogon() {
		return StringUtils.isNotEmpty(super.getMemberId());
	}
	
	public boolean isEqualsUserId(String writerId) {
		if(StringUtils.equals(writerId, super.getMemberId())) {
			return true;
		}
		return false;
	}
	
	public ArticleDto getUserInfo() {
		ArticleDto articleDto = new ArticleDto();
		articleDto.setWriteMemberId(super.getMemberId());
		
		return articleDto;
	}
	
	public boolean isAccessRestriction(String userLevel) {
		return super.getMemberLevel().equals(userLevel);
	}
}
