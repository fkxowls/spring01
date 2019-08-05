package com.spring.study.board.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.study.comment.model.CommentsVo;

public class ArticleVo {

	private String articleId;
	private String parentId;
	private String title;
	private String content;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+9")
	private Date writeDate;
	private String writeMemberId;
	private int rnum;
	private boolean isNotice;
	
	private String modifyMemberId;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+9")
	private Date modifyDate;
	private List<CommentsVo> commentsList;
	private NoticeArticleVo noticeArticle;

	public NoticeArticleVo getNoticeArticle() {
		return noticeArticle;
	}

	public void setNoticeArticle(NoticeArticleVo noticeArticle) {
		this.noticeArticle = noticeArticle;
	}

	public List<CommentsVo> getCommentsList() {
		if (null == commentsList) {
			commentsList = new ArrayList<CommentsVo>();
		}
		return commentsList;
	}

	public String getModifyMemberId() {
		return modifyMemberId;
	}

	public void setModifyMemberId(String modifyMemberId) {
		this.modifyMemberId = modifyMemberId;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public boolean isNotice() {
		return isNotice;
	}

	public void setNotice(boolean isNotice) {
		this.isNotice = isNotice;
	}

	public void setCommentsList(List<CommentsVo> commentsList) {
		this.commentsList = commentsList;
	}

	public int getRnum() {
		return rnum;
	}

	public void setRnum(int rnum) {
		this.rnum = rnum;
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

	public Date getWriteDate() {
		return writeDate;
	}

	public void setWriteDate(Date writeDate) {
		this.writeDate = writeDate;
	}

	public String getwriteMemberId() {
		return writeMemberId;
	}

	public void setwriteMemberId(String writeMemberId) {
		this.writeMemberId = writeMemberId;
	}

	
//	@Override
//  	public String toString() {
//		ObjectMapper mapper = new ObjectMapper();
//		return mapper.writeValueAsString(this);
//	}
 

}
