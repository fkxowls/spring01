package com.spring.study.board.dao;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.spring.study.ListPagingVo;
import com.spring.study.board.controller.AticleController;
import com.spring.study.board.vo.ArticleReplyVo;
import com.spring.study.board.vo.AticleVo;
import com.spring.study.board.vo.HasNextPaging;

@Repository("articleDAO")
public class ArticleDAO {
	private static final Logger logger = LoggerFactory.getLogger(AticleController.class);

	@Autowired
	SqlSession session;

	public AticleVo viewArticle(int aritcleNo) {
		return session.selectOne("mapper.article.viewArticle", aritcleNo);

	}

	public void insertArticle(AticleVo articleVo) {
		System.out.println("=======================================				articleVo:");
		System.out.println("=======================================			             : " + articleVo.getContent());
		session.insert("mapper.article.insertArticle", articleVo);

	}

	public void updateArticle(AticleVo articleVo) {
		session.update("mapper.article.updateArticle", articleVo);

	}

	public int getTotalArticles() {

		return session.selectOne("mapper.article.totalArticle");
	}

	public int getSequence() {

		return session.selectOne("mapper.article.getSequence");
	}

	public void deleteArticle(Integer num) {
		session.delete("mapper.article.deleteArticle", num);

	}

	public int replyArticle(AticleVo articleVo) {
		int num = session.insert("mapper.article.insertReply", articleVo);
		logger.info("===============넘어온 num:{}", num);
		return num;

	}


	public List<ArticleReplyVo> listComment(int articleNo) {
		List<ArticleReplyVo> list;
		// TODO DTO캐싱 로직 구현 - 읽기 get
		{
			list = session.selectList("mapper.comment.listComment", articleNo);
			// TODO DTO캐싱 로직 구현 - 쓰기 set
		}
		return list;
	}

	public int insertComment(ArticleReplyVo replyVo) {
		logger.info("=================== 		DAO insertComment:{}");
		logger.info("parentNo: {}",replyVo.getParentNo());
		return session.insert("mapper.comment.insertComment", replyVo);

	}

	public List<AticleVo> ListArticle() {

		return session.selectList("mapper.article.listArticle");
	}
	
	/*
	 * public List<AticleVo> ListArticle() {
	 * 
	 * return session.selectList("mapper.article.listArticle", Vo); }
	 */

	public List<AticleVo> listArticle2(HasNextPaging Vo) {
		
		return session.selectList("mapper.article.listArticle2", Vo);
	}
}
