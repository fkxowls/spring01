package com.spring.study.board.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.study.board.controller.AticleController;
import com.spring.study.board.dao.ArticleDAO;
import com.spring.study.board.vo.ArticleReplyVo;
import com.spring.study.board.vo.AticleVo;
import com.spring.study.board.vo.HasNextPaging;
import com.spring.study.board.vo.PageDto;

@Service("ArticleService")
public class ArticleService {

	private static final Logger logger = LoggerFactory.getLogger(AticleController.class);

	@Autowired
	ArticleDAO articleDAO;

	/*
	 * //과거의 리스트페이징 public List<AticleVo> listArticle(int num) { List articleList =
	 * articleDAO.ListArticle(num); return articleList; }
	 */
	public AticleVo viewArticle(int num) {
		return articleDAO.viewArticle(num);

	}
	// ??
	/*
	 * public PageNumVo2 pageNum(HttpServletRequest req) {
	 * logger.info("==========			pageNum() start		===============");
	 * 
	 * String num = req.getParameter("pageNum"); int pageNum =
	 * Integer.parseInt(((num == null) ? "1" : num)); int startNum = pageNum/10+1;
	 * PageNumVo2 pnv = new PageNumVo2(pageNum,startNum);
	 * 
	 * return pnv; }
	 */

	public void writeArticle(AticleVo articleVo) {

		articleDAO.insertArticle(articleVo);

	}

	public void modifyArticle(AticleVo articleVo) {

		articleDAO.updateArticle(articleVo);
	}

	public int getTotalArticles() {
		return articleDAO.getTotalArticles();
	}

	public int getSequence() {
		return articleDAO.getSequence();
	}

	public void deleteArticle(Integer num) {
		articleDAO.deleteArticle(num);

	}

	public int replyArticle(AticleVo articleVo) {
		articleVo.setParentNo(articleVo.getArticleNo());
		logger.info("=================== parentNo:{}", articleVo.getParentNo());
		return articleDAO.replyArticle(articleVo);

	}

	public List<ArticleReplyVo> listComment(int articleNo) {
		return articleDAO.listComment(articleNo);
	}

	public int insertComment(ArticleReplyVo replyVo) {
		logger.info("=================== 		Service insertComment:{}");
		return articleDAO.insertComment(replyVo);

	}

	public void insertReComment(ArticleReplyVo replyVo) {
		articleDAO.insertComment(replyVo);

	}
	
	public PageDto<AticleVo> EndPaging(int page, int pageSize) {
		PageDto.Builder builder = new PageDto.Builder(page, pageSize);
		builder.test2(true).test3(true).build();
		builder.test5(true).build();
		builder.build();
		builder.test1(true).test2(true).test3(true).build();
		builder.test1(true).test2(true).test3(true).test4(true).test5(true).build();
		
		PageDto<AticleVo> req = new PageDto.Builder(page, pageSize).build();
		List<AticleVo> list = articleDAO.ListArticle(req);
		int totalCount = articleDAO.getTotalArticles();
		return new PageDto<AticleVo>(page, pageSize, totalCount, list, false);
	}
	
	public List<AticleVo> EndPagingMore(int page, int pageSize) {
		PageDto<AticleVo> req = new PageDto.Builder(page, pageSize).build();
		return articleDAO.ListArticle(req);
	}
	
	public PageDto<AticleVo> hasNextPaging(int page, int pageSize) {
		
		PageDto<AticleVo> req = new PageDto.Builder(page, pageSize + 1).useMoreView(true).build();
		List<AticleVo> list = articleDAO.ListArticle(req);
		
		int totalCount = 0;
		
		return new PageDto<AticleVo>(page, pageSize, totalCount, list, true);
	}
	
	public PageDto<AticleVo> hasNextPagingMore(int page, int pageSize) {
		
		PageDto<AticleVo> req = new PageDto.Builder(page, pageSize + 1).useMoreView(true).build();
		List<AticleVo> list = articleDAO.ListArticle(req);
		
		int totalCount = 0;
		
		return new PageDto<AticleVo>(page, pageSize, totalCount, list, true);
	}
	
	
	public List<AticleVo> listArticle2(HasNextPaging vo){
		return articleDAO.ArticleList(vo);
	}

	public boolean isNext(List<AticleVo> list) {
		if (list.size() != 11) {
			return false;
		} else
			return true;

	}

	

}
