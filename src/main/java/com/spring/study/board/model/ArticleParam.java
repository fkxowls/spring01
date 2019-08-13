package com.spring.study.board.model;

import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.time.FastDateFormat;

import com.spring.study.common.model.BaseParam;

public class ArticleParam extends BaseParam{
	private String articleId;
	private String title;
	private String contents;
	private String writerId;
	private Date writeDate;
	private String modifierId;
	private Date modifyDate;
	private String articleTypeCd;
	
	public static class Builder extends BaseParam.Builder<Builder>{
		private String articleId;
		private String title;
		private String contents;
		private String writerId;
		private String modifierId;
		private Date writeDate;
		private Date modifyDate;
		private String articleTypeCd;
		
		public Builder(String articleId) {
			super();
			this.articleId = articleId;
		}
		
		public Builder(String title, String contents, String writerId, String articleTypeCd) {
			super();
			this.title = title;
			this.contents = contents;
			this.writerId = writerId;
			this.articleTypeCd = articleTypeCd;
		}
		
		public Builder modifierId(String modifierId) {
			this.modifierId = modifierId;
			return this;
		}
		public Builder modifyDate(Date modifyDate) {
			this.modifyDate = modifyDate;
			return this;
		}
		public Builder articleTypeCd(String articleTypeCd) {
			this.articleTypeCd = articleTypeCd;
			return this;
		}
		public ArticleParam build() {
			super.build();
			return new ArticleParam(this);
		}
	}
	
	
	protected ArticleParam(Builder builder) {
		super(builder);
		this.articleId = builder.articleId;
		this.title = builder.title;
		this.contents = builder.contents;
		this.writerId = builder.writerId;
		this.modifierId = builder.modifierId;
		if(null != this.modifierId) {
			this.modifyDate = new Date();
		}
		this.articleTypeCd = builder.articleTypeCd;
		
	}


	protected String getArticleId() {
		return articleId;
	}


	protected String getTitle() {
		return title;
	}


	protected String getContents() {
		return contents;
	}


	protected String getWriterId() {
		return writerId;
	}


	protected String getModifierId() {
		return modifierId;
	}


	protected Date getModifyDate() {
		return modifyDate;
	}


	protected String getArticleTypeCd() {
		return articleTypeCd;
	}
	
		

}
