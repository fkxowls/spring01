package com.spring.study.board.vo;

import java.util.Date;

public class ArticleReplyVo {
	int articleNo;
    int replyNo;
    int parentNo;
    String content;
    Date writeDate;
    String writeMemberId;
    int level;
    
    @Override
    public String toString() {
    	
    	return "[articleNo: " +articleNo+", replyNo: " +replyNo+", parentNo: " + parentNo
    			+",content:"+ content+", writeDate"+ writeDate +", writeMemberId:"+writeMemberId +"]";
    }
    
	public int getArticleNo() {
		return articleNo;
	}
	public void setArticleNo(int articleNo) {
		this.articleNo = articleNo;
	}
	public int getReplyNo() {
		return replyNo;
	}
	public void setReplyNo(int replyNo) {
		this.replyNo = replyNo;
	}
	public int getParentNo() {
		return parentNo;
	}
	public void setParentNo(int parentNo) {
		this.parentNo = parentNo;
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
