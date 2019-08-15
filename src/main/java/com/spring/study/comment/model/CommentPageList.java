package com.spring.study.comment.model;

import java.util.List;



public class CommentPageList {
	private List<CommentsVo> commentsList;
	private int page;
	private int pageSize;
	private int totalPage;
	/**
	 * totalPage는 데이터블록 계산후 어떤방식으로 할지 결정
	 **/
	
	public CommentPageList() {
		
	}
	
	public CommentPageList(int page, int pageSize, List<CommentsVo> commentsList) {
		this.page 		  = page;
		this.pageSize 	  = pageSize;
		this.commentsList = commentsList;
	}
	
	public List<CommentsVo> getCommentsList() {
		return commentsList;
	}
	public int getPage() {
		return page;
	}
	public int getPageSize() {
		return pageSize;
	}
	public int getTotalPage() {
		return totalPage;
	}
	
	

}
