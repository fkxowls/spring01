package com.spring.study.comment.model;

public class CommentsDto {
	private String articleId;
	private int pageSize;
	private int commentsPage;
	private int startNum;
	private int endNum;
	private String writeMemberId;
	private String content;
	private String secretTypeCd;
	private int parentId;
	private String replyId;
	

	public CommentsDto() {
	
	}

	public CommentsDto(String articleId, String content, String secretTypeCd) {
		this.articleId    = articleId;
		this.content	  = content;//?? 여기에 왜 갑분컨???
		this.secretTypeCd =secretTypeCd;
	}
	
	public CommentsDto(String articleId, int commentsPage, int pageSize) {
		this.articleId 	  = articleId;
		this.pageSize 	  = pageSize;
		this.commentsPage = commentsPage;
	}

	public CommentsDto(String articleId, int commentsPage, int pageSize, String writeMemberId) {
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
	public String getReplyId() {
		return replyId;
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
	
	
}
