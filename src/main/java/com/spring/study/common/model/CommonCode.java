package com.spring.study.common.model;

import org.apache.commons.lang.StringUtils;

public enum CommonCode {
	SECRET_TYPE_CD_SECRET_Y("10"),
	SECRET_TYPE_CD_SECRET_N("20"),
	
	ARTICLE_TYPE_CD_NOTICE_Y("10"),
	ARTICLE_TYPE_CD_NOTICE_N("20"),
	
	USER_LEVEL_CD_UNKNOW("00"),
	USER_LEVER_CD_ADMIN("10"),
	USER_LEVER_CD_GOLD("20"),
	USER_LEVEL_CD_NOMAL("30");
	
	private String code;
	
	CommonCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
	
	public boolean equels(String input) {
		return StringUtils.equals(this.code, input);
	}
}
