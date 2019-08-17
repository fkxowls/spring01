package com.spring.study.model.article;

import com.spring.study.common.model.BaseParam;

public class ArticleParam2 extends BaseParam{
	private String writerId;
	private String articleId;
	private String sort;
	
	public static class Builder extends BaseParam.Builder<Builder>{
		private String writerId;
		private String articleId;
		private String sort;
		
		public Builder(String articleId) {
			this.articleId = articleId;
		}
		
		public Builder(int page, int pageSize) {
			super(page,pageSize);
		}
		
		public Builder sort(String sort) {
			this.sort = sort;
			return this;
		}
		
		public ArticleParam2 build() {
			super.build();
			return new ArticleParam2(this);
		}
		
	}

	public ArticleParam2(Builder builder) {
		super(builder);
		this.articleId = builder.articleId;
		this.writerId = builder.writerId;
		this.sort = builder.sort;
	}

	public String getArticleId() {
		return articleId;
	}

	protected final String getWriterId() {
		return writerId;
	}

	protected final String getSort() {
		return sort;
	}
	

}
