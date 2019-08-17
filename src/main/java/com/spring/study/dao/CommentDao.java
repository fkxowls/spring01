package com.spring.study.dao;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.spring.study.common.model.BaseDao;
import com.spring.study.common.model.PageList;
import com.spring.study.controller.ArticleController;
import com.spring.study.model.comments.CommentsDto;
import com.spring.study.model.comments.CommentsParam;
import com.spring.study.model.comments.CommentsVo;
import com.spring.study.model.member.User;


@Repository("commentDAO")
public class CommentDao  extends BaseDao{
	private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);
	private static String mapper = "mapper.article";
	
	@Autowired
	SqlSession sqlSession;
	
	public boolean isExistsComment(String replyId) {
		String result = sqlSession.selectOne("mapper.comment.isExistComment", replyId);
		
		return false;
	}
	
	public PageList<CommentsVo> commentsList(CommentsParam commentsParam) {
		return super.selectPageDto("mapper.comment.listComment", commentsParam);
	}
		
	public int writeComment(CommentsDto crd, User user) {
		CommentsVo vo = new CommentsVo();	
		vo.setWriteMemberId(user.getMemberId());
		vo.setArticleId(crd.getArticleId());
		vo.setParentId(crd.getParentId());
		vo.setContent(crd.getContent());
		vo.setSecretTypeCd(crd.getSecretTypeCd());
		
		return sqlSession.insert("mapper.comment.insertComment", vo);

	}
	
	public String getWriterOfArticle(String articleId) {
		// 글작성자 가져오는 쿼리 날리기
		return null;
	}
	
	
}
