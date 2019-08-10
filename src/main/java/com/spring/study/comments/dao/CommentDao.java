package com.spring.study.comments.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.spring.study.board.controller.AticleController;
import com.spring.study.comment.model.CommentPageList;
import com.spring.study.comment.model.CommentsRequestDto;
import com.spring.study.comment.model.CommentsVo;
import com.spring.study.member.model.Member;


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
	
	public CommentPageList commentsList(CommentsRequestDto commentDto) {
		CommentsVo vo = new CommentsVo(commentDto.getCommentsPage());
		vo.setArticleId(commentDto.getArticleId());
		vo.setStartNum(commentDto.getStartNum());
		vo.setEndNum(commentDto.getEndNum());
				
		List<CommentsVo> commentList = sqlSession.selectList("mapper.comment.listComment", vo);
		return new CommentPageList(commentDto.getCommentsPage(), commentDto.getPageSize(), commentList); 
	}
		
	public int writeComment(CommentsRequestDto crd,  Member member) {
		CommentsVo vo = new CommentsVo();	
		vo.setWriteMemberId(member.getMemberId());
		vo.setArticleId(crd.getArticleId());
		vo.setParentId(crd.getParentId());
		vo.setContent(crd.getContent());
		vo.setSecretTypeCd(crd.getSecretTypeCd());
		
		return sqlSession.insert("mapper.comment.insertComment", vo);

	}
	
	public String getWriterOfArticle(String articleId) {
		// TODO 글작성자 가져오는 쿼리 날리기
		return null;
	}
	
	
}
