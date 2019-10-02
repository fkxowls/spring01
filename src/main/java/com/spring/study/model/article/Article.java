package com.spring.study.model.article;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.commons.lang3.StringUtils;

import com.spring.study.common.model.PageList;
import com.spring.study.model.comments.Comment;
import com.spring.study.model.comments.CommentDto;
import com.spring.study.model.user.User;

public class Article extends ArticleVo {
	private static final int articleIdLength = 5;
	private static final FastDateFormat fdf = FastDateFormat.getInstance("yyyy-MM-dd HH:mm");
	private PageList<Comment> commentsList;
	private ArticleReadCountVo articleReadCountVo;
	private List<String> articleIds;
	private String rootId;
	private ArticleDto rootArticle;//현재 articleDto와는 목적이 다름 

	public static final boolean checkId(String articleId) {
		if (articleIdLength != articleId.length()) {
			return false;
		}
		return true;
	}

	private List<String> accessLevelList; 
	//Feed형으로 출력할때
	public ArticleDto displayArticles() {
		ArticleDto articleDto = new ArticleDto();
		articleDto.setArticleId(getArticleId());
		articleDto.setTitle(getTitle());
		articleDto.setWriteMemberId(getWriteMemberId());
		articleDto.setWriteDate(fdf.format(getWriteDate()));
		articleDto.setPath("/board/" + super.getArticleId());
		articleDto.setReadCount(articleReadCountVo.getReadCount());
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
		articleDto.setWriteDate(fdf.format(getWriteDate()));
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
	
	public ArticleDto getRootArticle() {
		return rootArticle;
	}

	public String getRootId() {
		return rootId;
	}

	public void setRootId(String rootId) {
		this.rootId = rootId;
	}

	public void setRootArticle(ArticleDto rootArticle) {
		this.rootArticle = rootArticle;
	}
	
	
//
//	public void setRootArticle(ArticleDto rootArticle) {
//		this.rootArticle = rootArticle;
//	}	
	
}
