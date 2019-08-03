package com.spring.study.member.vo;

import org.apache.commons.lang3.StringUtils;

public class Member {
	private String memberId;
	private int memberLevel;
	
	public Member() {}
	
	public Member(String memberId,int memberLevel) {
		this.memberId=memberId;
		this.memberLevel=memberLevel;
	}
	
	public boolean isLogin() {
		return StringUtils.isNotEmpty(this.memberId);
	}
	
	public String getMemberId() {
		return memberId;
	}
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	public int getMemberLevel() {
		return memberLevel;
	}
	public void setMemberLevel(int memberLevel) {
		this.memberLevel = memberLevel;
	}


	
	
}
