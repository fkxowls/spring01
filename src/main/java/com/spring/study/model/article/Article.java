package com.spring.study.model.article;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.spring.study.common.model.PageList;
import com.spring.study.model.comments.Comment;
import com.spring.study.model.comments.CommentDto;
import com.spring.study.model.user.User;

public class Article extends ArticleVo {
	private static final int articleIdLength = 5;
	private PageList<Comment> commentsList;
	private Article rootArticle;
	private NoticeVo noticeArticle;
	private ArticleReadCountVo articleReadCountVo;
	private List<String> articleIds;
	private List<Comment> commentsCntList;
	private List<ArticleReadCountVo> readCntList;

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
		articleDto.setRootArticle(getRootArticle());
		
		List<CommentDto> commentDtoList = this.getCommentsList().getList().stream()
				.map(Comment::showComment)
				.collect(Collectors.toList());
		PageList<CommentDto> commentDtoPageList = new PageList<CommentDto>(this.getCommentsList().getPage(), this.getCommentsList().getPageSize(), commentDtoList, this.getCommentsList().getTotalCount(), this.getCommentsList().getHasNext());
		articleDto.setCommentPageList(commentDtoPageList);
		
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

	public PageList<Comment> getCommentsList() {
		if(null == commentsList) {
			return new PageList<Comment>(Collections.EMPTY_LIST);
		}
		return commentsList;
	}

	public void setCommentsList(PageList<Comment> commentsList) {
		this.commentsList = commentsList;
	}

	public Article getRootArticle() {
		return rootArticle;
	}

	public void setRootArticle(Article rootArticle) {
		this.rootArticle = rootArticle;
	}

	// notice 혹시나 쓸일이 있을것같아서
	public NoticeVo getNoticeArticle() {
		return noticeArticle;
	}

	public void setNoticeArticle(NoticeVo noticeArticle) {
		this.noticeArticle = noticeArticle;
	}

	public ArticleReadCountVo getArticleReadCountVo() {
		return articleReadCountVo;
	}

	public void setArticleReadCountVo(ArticleReadCountVo articleReadCountVo) {
		this.articleReadCountVo = articleReadCountVo;
	}

	public List<String> getArticleIds() {
		return articleIds;
	}

	public void setArticleIds(List<String> articleIds) {
		this.articleIds = articleIds;
	}

	public List<Comment> getCommentsCntList() {
		return commentsCntList;
	}

	public void setCommentsCntList(List<Comment> commentsCntList) {
		this.commentsCntList = commentsCntList;
	}

	public List<ArticleReadCountVo> getReadCntList() {
		return readCntList;
	}

	public void setReadCntList(List<ArticleReadCountVo> readCntList) {
		this.readCntList = readCntList;
	}
	
}
