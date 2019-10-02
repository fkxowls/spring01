package com.spring.study.model.article;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spring.study.common.model.PageList;
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
	private String writeDate;//TODO Article로
	private String modifyDate;
	private String displayStartDate;
	private String displayEndDate;
	private String path;
	private PageList<CommentDto> commentPageList;
	private ArticleDto rootArticle; //root글 붙이려고
	

	public ArticleDto getRootArticle() {
		return rootArticle;
	}
	public PageList<CommentDto> getCommentPageList() {
		return commentPageList;
	}
	public void setCommentPageList(PageList<CommentDto> commentPageList) {
		this.commentPageList = commentPageList;
	}
	public void setRootArticle(ArticleDto rootArticle) {
		this.rootArticle = rootArticle;
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
	public String getWriteDate() {
		return writeDate;
	}
	public void setWriteDate(String string) {
		this.writeDate = string;
	}
	public String getModifyDate() {
		return modifyDate;
	}
	public void setModifyDate(String modifyDate) {
		this.modifyDate = modifyDate;
	}

	public String getDisplayStartDate() {
		return displayStartDate;
	}
	public void setDisplayStartDate(String displayStartDate) {
		this.displayStartDate = displayStartDate;
	}
	public String getDisplayEndDate() {
		return displayEndDate;
	}
	public void setDisplayEndDate(String displayEndDate) {
		this.displayEndDate = displayEndDate;
	}
	

}
