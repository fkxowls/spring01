package com.spring.study.board.model;

import java.text.SimpleDateFormat;
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
	private Date displayStartDate;
	private Date displayEndDate;
	
	public static class Builder extends BaseParam.Builder<Builder>{
		private String articleId;
		private String title;
		private String contents;
		private String writerId;
		private String modifierId;
		private Date writeDate;
		private Date modifyDate;
		private String articleTypeCd;
		private Date displayStartDate;
		private Date displayEndDate;
		
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
		public Builder displayStartDate(Date displayStartDate) {
			this.displayStartDate = displayStartDate;
			return this;
		}
		public Builder displayEndDate(Date displayEndDate) {
			this.displayEndDate = displayEndDate;
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
		if(null != this.writerId) {
			this.writeDate = new Date();
		}
		this.modifierId = builder.modifierId;
		if(null != this.modifierId) {
			this.modifyDate = new Date();
		}
		this.articleTypeCd = builder.articleTypeCd;
		this.displayStartDate = displayStartDate;
		this.displayEndDate = displayEndDate;
		
	}


	public String getArticleId() {
		return articleId;
	}


	public String getTitle() {
		return title;
	}


	public String getContents() {
		return contents;
	}


	public String getWriterId() {
		return writerId;
	}


	public String getModifierId() {
		return modifierId;
	}


	public Date getModifyDate() {
		return modifyDate;
	}


	public String getArticleTypeCd() {
		return articleTypeCd;
	}


	public Date getWriteDate() {
		return writeDate;
	}

	public Date getDisplayStartDate() {
		return displayStartDate;
	}

	public Date getDisplayEndDate() {
		return displayEndDate;
	}	
	
		

}
