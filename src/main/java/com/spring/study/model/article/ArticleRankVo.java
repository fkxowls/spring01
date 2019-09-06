package com.spring.study.model.article;

public class ArticleRankVo {
	private String articleId;
	private int commentCnt;
	private int readCnt;
	private int recommend;
	
	public String getArticleId() {
		System.out.println("articleId");
		System.out.println(articleId);
		return articleId;
	}
	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}
	public int getCommentCnt() {
		return commentCnt;
	}
	public void setCommentCnt(int commentCnt) {
		this.commentCnt = commentCnt;
	}
	public int getReadCnt() {
		return readCnt;
	}
	public void setReadCnt(int readCnt) {
		this.readCnt = readCnt;
	}
	public int getRecommend() {
		return recommend;
	}
	public void setRecommend(int recommend) {
		this.recommend = recommend;
	}
	
	
}
