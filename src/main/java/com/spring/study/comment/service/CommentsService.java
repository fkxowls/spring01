package com.spring.study.comment.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.study.board.dao.ArticleDao;
import com.spring.study.board.dao.BaseDao;
import com.spring.study.comment.dao.CommentDao;
import com.spring.study.comment.model.CommentPageList;
import com.spring.study.comment.model.CommentsDto;
import com.spring.study.comment.model.CommentsParam;
import com.spring.study.comment.model.CommentsVo;
import com.spring.study.common.model.CommonCode;
import com.spring.study.common.model.PageList;
import com.spring.study.member.model.Member;
import com.spring.study.member.model.User;

import javassist.NotFoundException;

@Service
public class CommentsService{
	private static final Logger logger = LoggerFactory.getLogger(CommentsService.class);
	
	@Autowired
	private CommentDao commentDao;
	@Autowired
	private ArticleDao articleDao;
	
//	public CommentPageList getComments(CommentsDto req) {
//		CommentPageList commentPageList = this.getComments(req, null);
//		return commentPageList;
//	}
	//commentPage정보 받아와야한다
//	public CommentPageList getComments(CommentsDto req, String userId) {
//		//final String userId = user;
//		String articleWriterId = req.getWriteMemberId();
//		
//		PageList<CommentsVo> commentPageList = commentDao.commentsList(req);
//		List<CommentsVo> commentsList = commentPageList.getCommentsList();
//		
//		commentsList.stream()
//				.filter(comment -> CommonCode.SECRET_TYPE_CD_SECRET_Y.getCode().equals(comment.getSecretTypeCd()))
//				.filter(comment -> !StringUtils.equals(userId, articleWriterId)) 
//				.filter(comment -> !StringUtils.equals(userId, comment.getWriteMemberId()))
//				.forEach(comment -> comment.setContent("비밀 댓글 입니다"));
//
//		return commentPageList;
//	}
	
	public PageList<CommentsVo> getCommentsPageList(CommentsParam commentsParam) {
		PageList<CommentsVo> commentPageList = commentDao.commentsList(commentsParam);
		/*		
		List<CommentsVo> commentsList = commentPageList.getCommentsList();
		commentsList.stream()
				.filter(comment -> CommonCode.SECRET_TYPE_CD_SECRET_Y.getCode().equals(comment.getSecretTypeCd()))
				.filter(comment -> !StringUtils.equals(userId, articleWriterId)) 
				.filter(comment -> !StringUtils.equals(userId, comment.getWriteMemberId()))
				.forEach(comment -> comment.setContent("비밀 댓글 입니다"));
		 */
		return commentPageList;
	}

	public String writeComment(CommentsDto commentsRequestDto, User user) throws Exception {
		
		boolean isExistsArticle = articleDao.isExistsArticle(commentsRequestDto.getArticleId());
		//XXX Article객체로 보내야하는건지??
		if (!isExistsArticle) {
			throw new NotFoundException("글이 존재하지 않습니다.");
		}
		//XXX Comments 객체에서 해야하는건지???
		if (commentsRequestDto.getParentId() != 0) {
			boolean isExistsComment = commentDao.isExistsComment(commentsRequestDto.getReplyId());
			if (!isExistsComment) {
				throw new NotFoundException("댓글이 존재하지 않습니다.");
			}
		}

		commentDao.writeComment(commentsRequestDto, user);

		return "댓글 작성에 성공 했습니다.";
	}
}
