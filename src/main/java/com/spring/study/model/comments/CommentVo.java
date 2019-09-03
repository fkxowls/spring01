package com.spring.study.model.comments;

import java.util.Date;

public class CommentVo {
	private String articleId;
	private String commentId;
	private int parentId;
	private String content;
	private Date writeDate;
	private String writeMemberId;
	private String secretTypeCd;
	private String totalCount;//댓글 카운트랑 글번호 같이 가져오려고 추가함
	
	@Override
	public String toString() {

		return "[articleId: " + articleId + ", replyId: " + commentId + ", parentNo: " + parentId + ",content:" + content
				+ ", writeDate" + writeDate + ", writeMemberId:" + writeMemberId + "]";
	}

	public CommentVo() {

	}
	
	
	
	public String getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public String getArticleId() {
		return articleId;
	}

	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}

	public String getCommentId() {
		return commentId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public void setParentNo(int parentId) {
		this.parentId = parentId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getWriteDate() {
		return writeDate;
	}

	public void setWriteDate(Date string) {
		this.writeDate = string;
	}

	public String getWriteMemberId() {
		return writeMemberId;
	}

	public void setWriteMemberId(String writeMemberId) {
		this.writeMemberId = writeMemberId;
	}

	public String getSecretTypeCd() {
		return secretTypeCd;
	}

	public void setSecretTypeCd(String secretTypeCd) {
		this.secretTypeCd = secretTypeCd;
	}

}
