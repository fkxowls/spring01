package com.spring.study.model.article;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class NoticeVo {
	private String noticeId;
	private String articleId;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+9")
	private Date displayStartDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+9")
	private Date displayEndDate;
	
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
	public Date getDisplayStartDate() {
		return displayStartDate;
	}
	public void setDisplayStartDate(Date enforcementDate) {
		this.displayStartDate = enforcementDate;
	}
	public Date getDisplayEndDate() {
		return displayEndDate;
	}
	public void setDisplayEndDate(Date displayEndDate) {
		this.displayEndDate = displayEndDate;
	}

	
	
}
