package com.spring.study.model.article;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spring.study.model.comments.Comment;
import com.spring.study.model.comments.CommentDto;

public class ArticleDto {
	private String articleId;
	private String parentId;
	private String title;
	private String content;
	private int readCount;
	private String writeMemberId;
	private String modifyMemberId;
	private String articleTypeCd;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+9")
	private Date writeDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+9")
	private Date modifyDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+9")
	private Date displayStartDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+9")
	private Date displayEndDate;
	private String path;
	private List<CommentDto> commentsList;
	
	
	
	public List<CommentDto> getCommentsList() {
		return commentsList;
	}
	public void setCommentsList(List<CommentDto> commentsList) {
		this.commentsList = commentsList;
	}
	public int getReadCount() {
		return readCount;
	}
	public void setReadCount(int readCount) {
		this.readCount = readCount;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getArticleTypeCd() {
		return articleTypeCd;
	}
	public void setArticleTypeCd(String articleTypeCd) {
		this.articleTypeCd = articleTypeCd;
	}
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

	public Date getDisplayStartDate() {
		return displayStartDate;
	}
	public void setDisplayStartDate(Date displayStartDate) {
		this.displayStartDate = displayStartDate;
	}
	public Date getDisplayEndDate() {
		return displayEndDate;
	}
	public void setDisplayEndDate(Date displayEndDate) {
		this.displayEndDate = displayEndDate;
	}
	

}
