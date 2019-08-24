package com.spring.study.model.comments;

import org.apache.commons.lang.time.FastDateFormat;

public class Comment extends CommentVo{
	private static final FastDateFormat fdf = FastDateFormat.getInstance("yyyy-MM-dd HH:mm");
	
	public CommentDto showComment() {
		CommentDto dto = new CommentDto();
		dto.setArticleId(getArticleId());
		dto.setCommentId(getCommentId());
		dto.setParentId(getParentId());
		dto.setContent(getContent());//"비밀댓글입니다" 처리
		dto.setWriteMemberId(getWriteMemberId());
		dto.setWriteDate(fdf.format(getWriteDate()));
		
		return dto;
	}
}
