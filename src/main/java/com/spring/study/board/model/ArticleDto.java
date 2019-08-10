package com.spring.study.board.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ArticleDto {
	private String articleId;
	private String parentId;
	private String title;
	private String content;
	private String writeMemberId;
	private String modifyMemberId;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+9")
	private Date writeDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+9")
	private Date modifyDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+9")
	private Date startDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+9")
	private Date endDate;
	private boolean isNotice;
	public String getArticleId() {
		return articleId;
	}
	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getWriteMemberId() {
		return writeMemberId;
	}
	public void setWriteMemberId(String writeMemberId) {
		this.writeMemberId = writeMemberId;
	}
	public String getModifyMemberId() {
		return modifyMemberId;
	}
	public void setModifyMemberId(String modifyMemberId) {
		this.modifyMemberId = modifyMemberId;
	}
	public Date getWriteDate() {
		return writeDate;
	}
	public void setWriteDate(Date writeDate) {
		this.writeDate = writeDate;
	}
	public Date getModifyDate() {
		return modifyDate;
	}
	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public boolean isNotice() {
		return isNotice;
	}
	public void setNotice(boolean isNotice) {
		this.isNotice = isNotice;
	}
	
	
}
