package com.spring.study.board.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class NoticeArticleVo {
	private String noticeId;
	private String articleId;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+9")
	private Date enforcementDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+9")
	private Date expirationDate;
	
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
	public Date getEnforcementDate() {
		return enforcementDate;
	}
	public void setEnforcementDate(Date enforcementDate) {
		this.enforcementDate = enforcementDate;
	}
	public Date getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	
}
