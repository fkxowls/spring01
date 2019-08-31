package com.spring.study.model.user;

//필드이름 변경 해야함
public class UserVo {
	String userId;
    String userPwd;
    String userLevel; 
    
	public String getUserId() {
//		if(null == userId) {
//			userId = "";
//		}
		return this.userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserPwd() {
		return userPwd;
	}
	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}
	public String getUserLevel() {
		return userLevel;
	}
	public void setUserLevel(String userLevel) {
		this.userLevel =userLevel;
	}
    
    
    
	
    
}
