package com.spring.study.board.vo;

import java.util.Date;

public class ArticleReplyVo {
	private String articleId;
	private String replyId;
	private int parentId;
	private String content;
	private Date writeDate;
	private String writeMemberId;
	private int level;
	private int secretChkFlag;
    
    @Override
    public String toString() {
    	
    	return "[articleId: " +articleId+", replyId: " +replyId+", parentNo: " + parentId
    			+",content:"+ content+", writeDate"+ writeDate +", writeMemberId:"+writeMemberId +"]";
    }
    
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public int getSecretChkFlag() {
		return secretChkFlag;
	}
	public void setSecretChkFlag(int secretChkFlag) {
		this.secretChkFlag = secretChkFlag;
	}
	public String getArticleId() {
		return articleId;
	}
	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}
	public String getReplyId() {
		return replyId;
	}
	public void setReplyId(String replyId) {
		this.replyId = replyId;
	}
	public int getParentNo() {
		return parentId;
	}
	public void setParentNo(int parentId) {
		this.parentId = parentId;
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
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
    
    
}
