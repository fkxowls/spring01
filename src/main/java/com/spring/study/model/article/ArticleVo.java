package com.spring.study.model.article;

import java.util.Date;

import com.spring.study.common.model.BaseVo;

public class ArticleVo {

	private String articleId;
	private String parentId;
	private String title;
	private String content;
	private Date writeDate;
	private String writeMemberId;
	private String modifyMemberId;
	private Date modifyDate;

	//Article로 옮기기 Vo는 컬럼과 1:1매칭
//	private List<CommentVo> commentsList;
//	private NoticeArticleVo noticeArticle;
//
	
	
//	public NoticeArticleVo getNoticeArticle() {
//		return noticeArticle;
//	}
//
//	public void setNoticeArticle(NoticeArticleVo noticeArticle) {
//		this.noticeArticle = noticeArticle;
//	}
//
//	public List<CommentVo> getCommentsList() {
//		if (null == commentsList) {
//			commentsList = new ArrayList<CommentVo>();
//		}
//		return commentsList;
//	}
//	public void setCommentsList(List<CommentVo> commentsList) {
//		this.commentsList = commentsList;
//	}


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

	public String getWriteMemberId() {
		return writeMemberId;
	}

	public void setWriteMemberId(String writeMemberId) {
		this.writeMemberId = writeMemberId;
	}

	
//	@Override
//  	public String toString() {
//		ObjectMapper mapper = new ObjectMapper();
//		return mapper.writeValueAsString(this);
//	}
 

}
