package com.spring.study.member.vo;

public class MemberDTO {
	private String memberId;
	private int memberLevel;
	
	public MemberDTO(){
		
	}
	
	public MemberDTO(String memberId,int memberLevel){
		this.memberId=memberId;
		this.memberLevel=memberLevel;
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
