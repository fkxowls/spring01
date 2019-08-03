package com.spring.study.board.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class NoticeArticleVo {
	private String noticeId;
	private String articleId;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+9")
	private Date startDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+9")
	private Date endDate;
	
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
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date enforcementDate) {
		this.startDate = enforcementDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date expirationDate) {
		this.endDate = expirationDate;
	}
	
	
}
