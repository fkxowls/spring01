package com.spring.study.model.comments;

import java.util.Date;

public class CommentDto {
	private String articleId;
	private String commentId;
	private int parentId;
	private String content;
	private String writeMemberId;
	private String writeDate;
	private String secretTypeCd;
	
	private int commentsPage;
	private int startNum;
	private int endNum;
	private int pageSize;
	

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
		this.commentsPage = commentsPage;
	}

	public CommentDto(String articleId, int commentsPage, int pageSize, String writeMemberId) {
		this.articleId 	      = articleId;
		this.commentsPage 	  = commentsPage;
		this.writeMemberId 	  = writeMemberId;
	}
	

	
	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}
	public void setCommentsPage(int commentsPage) {
		this.commentsPage = commentsPage;
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

	public int getCommentsPage() {
		return commentsPage;
	}

	public int getStartNum() {
		this.startNum = (this.commentsPage - 1) * 10 + 1;
		return startNum;
	}

	public int getEndNum() {
		this.endNum = this.commentsPage * 10;
		return endNum;
	}

	public String getWriteDate() {
		return writeDate;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setWriteDate(String string) {
		this.writeDate = string;
	}

	public void setSecretTypeCd(String secretTypeCd) {
		this.secretTypeCd = secretTypeCd;
	}

	public void setStartNum(int startNum) {
		this.startNum = startNum;
	}

	public void setEndNum(int endNum) {
		this.endNum = endNum;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	
}
