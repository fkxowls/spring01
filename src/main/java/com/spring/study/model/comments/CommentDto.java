package com.spring.study.model.comments;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class CommentDto {
	private String articleId;
	private String commentId;
	private int parentId;
	private String content;
	private String writeMemberId;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+9")
	private Date writeDate;
	private String secretTypeCd;
	private int page;
	private int pageSize;
	private boolean hasNext;
	
//	private int commentsPage;
//	private int pageSize;
//	private int startNum;
//	private int endNum;
	

	public CommentDto() {
	
	}

	public CommentDto(String articleId, String content, String secretTypeCd) {
		this.articleId    = articleId;
		this.content	  = content;//?? 여기에 왜 갑분컨???
		this.secretTypeCd =secretTypeCd;
	}
	
	public CommentDto(String articleId, int commentsPage, int pageSize) {
		this.articleId 	  = articleId;
		this.pageSize 	  = pageSize;
		this.page = commentsPage;
	}

	public CommentDto(String articleId, int commentsPage, int pageSize, String writeMemberId) {
		this.articleId 	      = articleId;
		this.page 	  = commentsPage;
		this.writeMemberId 	  = writeMemberId;
	}
	

	
	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}
	
	public String getContent() {
		return content;
	}
	public String getSecretTypeCd() {
		return secretTypeCd;
	}
	public String getCommentId() {
		return commentId;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	
	public int getPageSize() {
		return pageSize;
	}

	public String getWriteMemberId() {
		return writeMemberId;
	}

	public void setWriteMemberId(String writeMemberId) {
		this.writeMemberId = writeMemberId;
	}

	public String getArticleId() {
		return articleId;
	}

//	public int getCommentsPage() {
//		return page;
//	}
//	public void setCommentsPage(int commentsPage) {
//		this.page = commentsPage;
//	}
	public Date getWriteDate() {
		return writeDate;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setWriteDate(Date writeDate) {
		this.writeDate = writeDate;
	}

	public void setSecretTypeCd(String secretTypeCd) {
		this.secretTypeCd = secretTypeCd;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public boolean isHasNext() {
		return hasNext;
	}

	public void setHasNext(boolean hasNext) {
		this.hasNext = hasNext;
	}
	
	
	
	
}
