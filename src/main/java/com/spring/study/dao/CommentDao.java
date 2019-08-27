package com.spring.study.dao;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.spring.study.common.model.BaseDao;
import com.spring.study.common.model.PageList;
import com.spring.study.model.comments.Comment;
import com.spring.study.model.comments.CommentDto;
import com.spring.study.model.comments.CommentParam;
import com.spring.study.model.comments.CommentVo;
import com.spring.study.model.user.User;

import defalut.ArticleController;


@Repository("commentDAO")
public class CommentDao  extends BaseDao{
	private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);
	private static String mapper = "mapper.article";
	
	@Autowired
	SqlSession sqlSession;
	
	public boolean isExistsComment(String commentId) {
		String result = sqlSession.selectOne("mapper.comment.isExistComment", commentId);
		
		return false;
	}
	
	public PageList<Comment> commentsList(CommentParam commentsParam) {
		return super.selectPageDto("mapper.comment.listComment", commentsParam);
	}
		
	public int writeComment(CommentDto dto, User user) {
		CommentVo vo = new CommentVo();	
		vo.setWriteMemberId(user.getUserId());
		vo.setArticleId(dto.getArticleId());
		vo.setParentId(dto.getParentId());
		vo.setContent(dto.getContent());
		vo.setSecretTypeCd(dto.getSecretTypeCd());
		
		return sqlSession.insert("mapper.comment.insertComment", vo);

	}
	
	public String getWriterOfArticle(String articleId) {
		// 글작성자 가져오는 쿼리 날리기
		return null;
	}
	
	
}
