package com.spring.study.model.article;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class NoticeVo {
	private String noticeId;
	private String articleId;
	private String displayStartDate;
	private String displayEndDate;
	
	public String getNoticeId() {
		return noticeId;
	}
	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
	}
	public String getArticleId() {
		return articleId;
	}
	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}
	public String getDisplayStartDate() {
		return displayStartDate;
	}
	public void setDisplayStartDate(String enforcementDate) {
		this.displayStartDate = enforcementDate;
	}
	public String getDisplayEndDate() {
		return displayEndDate;
	}
	public void setDisplayEndDate(String displayEndDate) {
		this.displayEndDate = displayEndDate;
	}

	
	
}
