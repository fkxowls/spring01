package com.spring.study.board.service;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.study.ListPagingVo;
import com.spring.study.board.controller.AticleController;
import com.spring.study.board.dao.ArticleDAO;
import com.spring.study.board.vo.ArticleReplyVo;
import com.spring.study.board.vo.AticleVo;
import com.spring.study.board.vo.EndPagePaging;
import com.spring.study.board.vo.HasNextPaging;

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
	
	//endPage 페이지나누는 부분
	public EndPagePaging EndPaging(int page) {
		EndPagePaging vo = new EndPagePaging();
		
		if(page == 1) {
			int totalCount = articleDAO.getTotalArticles();
			logger.info("======					여기 오냐 안오냐");
			vo.setTotalCount(totalCount);
		}
		vo.setStartNum(page);
		vo.setEndNum(page);
		vo.setPageSize(10);
		vo.setPage(page);
		List<AticleVo> list = articleDAO.ListArticle(vo);
		logger.info("===========		{}",vo.getTotalPage());
		vo.setList(list);
		
		return vo;
	}
	
	//페이지 첫글과 마지막글
	public EndPagePaging setStartNum(int page) {
		EndPagePaging vo = new EndPagePaging();
		vo.setStartNum(page);
		vo.setEndNum(page);
		return vo;
	}
	
	public HasNextPaging hasNextPaging(int page) {
		HasNextPaging vo = new  HasNextPaging();
		
		vo.setStartNum(page);
		vo.setEndNum(page);
		vo.setPageSize(10);
		List<AticleVo> list = articleDAO.ArticleList(vo);
		vo.setList(list,list.size());
		if(list.size() <= vo.getPageSize())
			vo.setHasNext(false);
		else
			vo.setHasNext(true);
		logger.info("=========== 		hasNext(): {}",vo.isHasNext());	

		return vo;
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
