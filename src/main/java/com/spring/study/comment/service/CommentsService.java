package com.spring.study.comment.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.study.board.dao.ArticleDao;
import com.spring.study.comment.dao.CommentDao;
import com.spring.study.comment.model.CommentPageList;
import com.spring.study.comment.model.CommentsRequestDto;
import com.spring.study.comment.model.CommentsVo;
import com.spring.study.common.model.CommonCode;
import com.spring.study.member.model.Member;

import javassist.NotFoundException;

@Service
public class CommentsService {
	private static final Logger logger = LoggerFactory.getLogger(CommentsService.class);
	private static final int commentPageSize = 10;
	
	@Autowired
	private CommentDao commentDao;
	@Autowired
	private ArticleDao articleDao;
	
	public CommentPageList getComments(CommentsRequestDto req) {
		CommentPageList commentPageList = this.getComments(req, null);
		return commentPageList;
	}
	//commentPage정보 받아와야한다
	public CommentPageList getComments(CommentsRequestDto req, String userId) {
		//final String userId = user;
		String articleWriterId = req.getWriteMemberId();
		
		CommentPageList commentPageList = commentDao.commentsList(req);
		List<CommentsVo> commentsList = commentPageList.getCommentsList();
		
		commentsList.stream()
				.filter(comment -> CommonCode.SECRET_TYPE_CD_SECRET_Y.getCode().equals(comment.getSecretTypeCd()))
				.filter(comment -> !StringUtils.equals(userId, articleWriterId)) 
				.filter(comment -> !StringUtils.equals(userId, comment.getWriteMemberId()))
				.forEach(comment -> comment.setContent("비밀 댓글 입니다"));

		return commentPageList;
	}
	
	public CommentPageList getCommentsPageList(String articleId, int commentsPage, String userId, String articleWriterId) {
		//final String userId = user.getMemberId();
		//String articleWriterId = commentDao.getWriterOfArticle(articleId);
		CommentsRequestDto commentsRequestDto = new CommentsRequestDto(articleId, commentsPage, commentPageSize, userId);
		commentsRequestDto.setArticleId(articleId);
		commentsRequestDto.setCommentsPage(commentsPage);

		
		CommentPageList commentPageList = commentDao.commentsList(commentsRequestDto);
		List<CommentsVo> commentsList = commentPageList.getCommentsList();
		
		commentsList.stream()
				.filter(comment -> CommonCode.SECRET_TYPE_CD_SECRET_Y.getCode().equals(comment.getSecretTypeCd()))
				.filter(comment -> !StringUtils.equals(userId, articleWriterId)) 
				.filter(comment -> !StringUtils.equals(userId, comment.getWriteMemberId()))
				.forEach(comment -> comment.setContent("비밀 댓글 입니다"));

		return commentPageList;
	}

	public String writeComment(CommentsRequestDto commentsRequestDto, Member member) throws Exception {
		
		boolean isExistsArticle = articleDao.isExistsArticle(commentsRequestDto.getArticleId());
		
		if (!isExistsArticle) {
			throw new NotFoundException("글이 존재하지 않습니다.");
		}

		if (commentsRequestDto.getParentId() != 0) {
			boolean isExistsComment = commentDao.isExistsComment(commentsRequestDto.getReplyId());
			if (!isExistsComment) {
				throw new NotFoundException("댓글이 존재하지 않습니다.");
			}
		}

		commentDao.writeComment(commentsRequestDto, member);

		return "댓글 작성에 성공 했습니다.";
	}
}
