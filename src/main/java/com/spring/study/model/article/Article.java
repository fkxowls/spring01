package com.spring.study.model.article;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.spring.study.common.model.PageList;
import com.spring.study.dao.ArticleDao;
import com.spring.study.model.comments.Comment;
import com.spring.study.model.comments.CommentDto;
import com.spring.study.model.comments.CommentVo;
import com.spring.study.model.user.User;

public class Article extends ArticleVo {
	private static final int articleIdLength = 5;
	private List<Comment> commentsList;
	private NoticeVo noticeArticle;
	private ArticleReadCountVo articleReadCountVo;

	public static final boolean checkId(String articleId) {
		if (articleIdLength != articleId.length()) {
			return false;
		}
		return true;
	}

	private List<String> accessLevelList; // Board의 하위테이블에 적재된 데이터

	public ArticleDto displayTitle() {
		ArticleDto articleDto = new ArticleDto();
		articleDto.setArticleId(getArticleId());
		articleDto.setTitle(getTitle());
		articleDto.setWriteMemberId(getWriteMemberId());
		articleDto.setWriteDate(getWriteDate());
		articleDto.setPath("/board/" + super.getArticleId());
		// articleDto.setReadCount();//XXX 조회수필드를 articleVo에 추가해야하는건가요??
		// articleDto.setCommentsList(getCommentsList().stream().map(Comment::showComment).collect(Collectors.toList()));

		if (null != commentsList) {
			List<CommentDto> commentDtoList = getCommentsList().stream().map(Comment::showComment)
					.collect(Collectors.toList());
			articleDto.setCommentsList(commentDtoList);
		}
		return articleDto;
	}

	public ArticleDto showArticle() {
		ArticleDto articleDto = new ArticleDto();
		articleDto.setArticleId(getArticleId());
		articleDto.setParentId(getParentId());
		articleDto.setTitle(getTitle());
		articleDto.setContent(getContent());
		articleDto.setWriteMemberId(getWriteMemberId());
		articleDto.setWriteDate(getWriteDate());
		// TODO
		return articleDto;
	}

	public boolean checkUserId(String userId) {
		if (StringUtils.equals(userId, super.getWriteMemberId())) {
			return true;
		}

		return false;
	}

	public List<String> getAccessLevelList() {
		if (null == accessLevelList) {
			accessLevelList = new ArrayList<>();
		}

		return accessLevelList;
	}

	public void setAccessLevelList(List<String> accessLevelList) {
		this.accessLevelList = accessLevelList;
	}

	public boolean checkAccessLevel(User user) {
		if (this.getAccessLevelList().contains(user.getUserLevel())) {
			return true;
		}

		return false;
	}

	// aop에서 comment주입하려고
	public List<Comment> getCommentsList() {
		return commentsList;
	}

	public void setCommentsList(List<Comment> commentsList) {
		this.commentsList = commentsList;
	}

	// notice 혹시나 쓸일이 있을것같아서
	public NoticeVo getNoticeArticle() {
		return noticeArticle;
	}

	public void setNoticeArticle(NoticeVo noticeArticle) {
		this.noticeArticle = noticeArticle;
	}

}
