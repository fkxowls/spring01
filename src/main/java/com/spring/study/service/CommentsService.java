package com.spring.study.service;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.study.common.model.PageList;
import com.spring.study.dao.ArticleDao;
import com.spring.study.dao.CommentDao;
import com.spring.study.model.comments.Comment;
import com.spring.study.model.comments.CommentDto;
import com.spring.study.model.comments.CommentParam;
import com.spring.study.model.comments.CommentVo;
import com.spring.study.model.user.User;

import javassist.NotFoundException;

@Service
public class CommentsService{
	private static final Logger logger = LoggerFactory.getLogger(CommentsService.class);
	
	@Autowired
	private CommentDao commentDao;
	@Autowired
	private ArticleDao articleDao;
	
	public PageList<Comment> getCommentsPageList(CommentParam commentsParam) {
		PageList<Comment> commentPageList = commentDao.getCommentsList(commentsParam);
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

	public String writeComment(CommentDto commentsRequestDto, User user) throws Exception {
		boolean isExistsArticle = articleDao.isExistsArticle(commentsRequestDto.getArticleId());
		if (!isExistsArticle) {
			throw new NotFoundException("글이 존재하지 않습니다.");
		}
		if (commentsRequestDto.getParentId() != 0) {
			boolean isExistsComment = commentDao.isExistsComment(commentsRequestDto.getCommentId());
			if (!isExistsComment) {
				throw new NotFoundException("댓글이 존재하지 않습니다.");
			}
		}
		int result = commentDao.writeComment(commentsRequestDto, user);
		if(1 != result) { throw new SQLException(" 댓글 등록 중 오류가 발생하였습니다. "); }
		
		return "댓글 작성에 성공 했습니다.";
	}
}
