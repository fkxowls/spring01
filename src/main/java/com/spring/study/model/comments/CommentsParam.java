package com.spring.study.model.comments;

import java.util.Date;

import com.spring.study.common.model.BaseParam;
import com.spring.study.model.article.ArticleParam;
import com.spring.study.model.article.ArticleParam.Builder;

/**
 * @author jnty0
 *
 */
public class CommentsParam extends BaseParam{
	private String articleId;
	private String writeMemberId;
	private String userId;
	
	public static class Builder extends BaseParam.Builder<Builder>{
		private String articleId;
		private String writeMemberId;
		private String userId;
		
		public Builder(String writerId) {
			this.writeMemberId = writeMemberId;
		}
		
		public Builder(int page, int pageSize, String articleId) {
			super(page,pageSize);
			this.articleId = articleId;
			
		}
		
		public Builder writeMemberId(String writeMemberId) {
			this.writeMemberId = writeMemberId;
			return this;
		}
		
		public Builder userId(String userId) {
			this.userId = userId;
			return this;
		}
		
		public CommentsParam build() {
			super.build();
			return new CommentsParam(this);
		}
		
	}

	public CommentsParam(Builder builder) {
		super(builder);
		this.articleId = builder.articleId;
		this.writeMemberId = builder.writeMemberId;
		this.userId = builder.userId;
	}

	public String getWriteMemberId() {
		return writeMemberId;
	}

	public String getArticleId() {
		return articleId;
	}

	protected final String getUserId() {
		return userId;
	}
}
