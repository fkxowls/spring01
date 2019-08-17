package com.spring.study.model.member;

import org.apache.commons.lang3.StringUtils;

public class Member {
	private String memberId;
	private String memberLevel;
	
	public Member() {}
	
	public Member(String memberId,String memberLevel) {
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
	public String getMemberLevel() {
		return memberLevel;
	}
	public void setMemberLevel(String memberLevel) {
		this.memberLevel = memberLevel;
	}
	
	
}
