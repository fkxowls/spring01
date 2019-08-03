package com.spring.study.board.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.spring.study.board.controller.AticleController;
import com.spring.study.board.vo.ArticleReplyVo;


@Repository("commentDAO")
public class CommentDao {
	private static final Logger logger = LoggerFactory.getLogger(AticleController.class);
	private static String mapper = "mapper.article";
	
	@Autowired
	SqlSession sqlSession;
	
	public boolean isExistsComment(String replyId) {
		String result = sqlSession.selectOne("mapper.comment.isExistComment", replyId);
		
		return false;
	}
	
	public List<ArticleReplyVo> commentsList(String articleIds) {
		return sqlSession.selectList("mapper.comment.listComment", articleIds);
	}
	
	public int writeComment(ArticleReplyVo replyVo) {
		return sqlSession.insert("mapper.comment.insertComment", replyVo);

	}
	
	
}
