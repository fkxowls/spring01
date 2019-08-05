package com.spring.study.comment.model;

import java.util.Date;

public class CommentsVo {
	private String articleId;
	private String replyId;
	private int parentId;
	private String content;
	private Date writeDate;
	private String writeMemberId;
	private int level;
	private String secretTypeCd;
	private int commentPage;
	private int startNum;
	private int endNum;

	@Override
	public String toString() {

		return "[articleId: " + articleId + ", replyId: " + replyId + ", parentNo: " + parentId + ",content:" + content
				+ ", writeDate" + writeDate + ", writeMemberId:" + writeMemberId + "]";
	}

	public CommentsVo() {

	}

	public CommentsVo(int commentPage) {
		this.commentPage = commentPage;
	}

	public void setStartNum(int startNum) {
		this.startNum = startNum;
	}

	public void setEndNum(int endNum) {
		this.endNum = endNum;
	}

	public int getStartNum() {
		return startNum;
	}

	public int getEndNum() {
		return endNum;
	}

	public int getCommentPage() {
		return commentPage;
	}

	public void setCommentPage(int commentPage) {
		this.commentPage = commentPage;
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

	public String getReplyId() {
		return replyId;
	}

	public void setReplyId(String replyId) {
		this.replyId = replyId;
	}

	public int getParentNo() {
		return parentId;
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

	public void setWriteDate(Date writeDate) {
		this.writeDate = writeDate;
	}

	public String getWriteMemberId() {
		return writeMemberId;
	}

	public void setWriteMemberId(String writeMemberId) {
		this.writeMemberId = writeMemberId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getSecretTypeCd() {
		return secretTypeCd;
	}

	public void setSecretTypeCd(String secretTypeCd) {
		this.secretTypeCd = secretTypeCd;
	}

}
