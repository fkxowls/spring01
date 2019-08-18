package com.spring.study.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.study.common.model.PageList;
import com.spring.study.dao.ArticleDao;
import com.spring.study.dao.CommentDao;
import com.spring.study.model.comments.CommentsDto;
import com.spring.study.model.comments.CommentsParam;
import com.spring.study.model.comments.CommentsVo;
import com.spring.study.model.user.User;

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
		//XXX 4-2 Article객체로 보내야하는건지??
		if (!isExistsArticle) {
			throw new NotFoundException("글이 존재하지 않습니다.");
		}
		//XXX 4-3 Comments 객체에서 해야하는건지???
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
