package com.spring.study.board.vo;

import java.sql.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class AticleVo {

	private String articleNo;
	private String parentNo;
	private String title;
	private String content;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+9")
	private Date writeDate;
	private String writeMemberId;
	private int rnum;
	private List<ArticleReplyVo> commentsList;
	
	private CommonRequestDto articleReplyVo;
	private PagingResponseDTO<ArticleReplyVo> articleReplyVo2;
	
	
	public List<ArticleReplyVo> getCommentsList() {
		return commentsList;
	}

	public void setCommentsList(List<ArticleReplyVo> commentsList) {
		this.commentsList = commentsList;
	}

	public PagingResponseDTO<ArticleReplyVo> getArticleReplyVo2() {
		return articleReplyVo2;
	}

	public void setArticleReplyVo2(PagingResponseDTO<ArticleReplyVo> articleReplyVo2) {
		this.articleReplyVo2 = articleReplyVo2;
	}

	public CommonRequestDto getArticleReplyVo() {
		return articleReplyVo;
	}

	public void setArticleReplyVo(CommonRequestDto articleReplyVo) {
		this.articleReplyVo = articleReplyVo;
	}

	public int getRnum() {
		return rnum;
	}

	public void setRnum(int rnum) {
		this.rnum = rnum;
	}

	public String getArticleNo() {
		return articleNo;
	}

	public void setArticleNo(String articleNo) {
		this.articleNo = articleNo;
	}

	public String getParentNo() {
		return parentNo;
	}

	public void setParentNo(String parentNo) {
		this.parentNo = parentNo;
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

	/*
	 * @Override public String toString() {
	 * 
	 * return "제목: " + title + ", 내용: " + content +"댓글: " + commentsList; }
	 */

}
