package com.spring.study.model.article;

import com.spring.study.common.model.BaseParam;

public class ArticleParam extends BaseParam{
	private String writeMemberId;
	private String articleId;
	private String userId;
	private String sort;
	private String targetUserId;
	
	public static class Builder extends BaseParam.Builder<Builder>{
		private String writeMemberId;
		private String articleId;
		private String userId;
		private String sort;
		private String targetUserId;
		
		public Builder(int pageSize) {
			super(pageSize);
		}
		public Builder(int startNum, int endNum) {
			super(startNum, endNum);
		}
		
		public Builder sort(String sort) {
			this.sort = sort;
			return this;
		}
		
		public Builder userId(String userId) {
			this.userId = userId;
			return this;
		}
		
		public Builder targetUserId(String targetUserId) {
			this.targetUserId = targetUserId;
			return this;
		}
		
		public ArticleParam build() {
			super.build();
			return new ArticleParam(this);
		}
		
	}

	public ArticleParam(Builder builder) {
		super(builder);
		this.articleId = builder.articleId;
		this.writeMemberId = builder.writeMemberId;
		this.userId = builder.userId;
		this.sort = builder.sort;
		this.targetUserId = builder.targetUserId;
	}

	public String getArticleId() {
		return articleId;
	}

	public final String getWriteMemberId() {
		return writeMemberId;
	}

	public final String getSort() {
		return sort;
	}

	public final String getTargetUserId() {
		return targetUserId;
	}

	public final String getUserId() {
		return userId;
	}
	
		

}
