package com.spring.study.dao;

import java.util.Map;
import java.util.function.Function;

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
	private static String mapper = "mapper.comment.";
	
	@Autowired
	SqlSession sqlSession;
	
	public boolean isExistsComment(String commentId) {
		String result = sqlSession.selectOne(mapper + "isExistComment", commentId);
		
		return false;
	}
	 
	public PageList<Comment> getCommentsList(CommentParam commentsParam) {
		return super.getPageListMore(mapper + "listComment", commentsParam);
	}
	 
	public Map<String, PageList<Comment>> getCommentsListMap(CommentParam commentParam, Function<Comment, String> groupById) {
		return super.selectPageListMore(mapper + "listComment", commentParam, groupById);
	}
		
	public int writeComment(CommentDto dto, User user) {
		CommentVo vo = new CommentVo();	
		vo.setWriteMemberId(user.getUserId());
		vo.setArticleId(dto.getArticleId());
		vo.setParentId(dto.getParentId());
		vo.setContent(dto.getContent());
		vo.setSecretTypeCd(dto.getSecretTypeCd());
		
		return sqlSession.insert(mapper + "insertComment", vo);
	}


	
}
