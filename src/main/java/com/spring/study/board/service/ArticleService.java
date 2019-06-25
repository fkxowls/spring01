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

	//endPage 요소들 처리하는 메서드
	public EndPagePaging EndPaging() {
		EndPagePaging vo = new EndPagePaging();
		
		int totalCount = articleDAO.getTotalArticles();
		vo.setPageSize(10);
		vo.setTotalCount(totalCount);
		List<AticleVo> list = articleDAO.ListArticle();
		vo.setList(list);
		
		return vo;
	}
	
	//endPage
	public ListPagingVo paging(String num) {
		ListPagingVo Vo = new ListPagingVo();
		Vo.setPagePerCount(10);
		Vo.setTotalCount(articleDAO.getTotalArticles());
	
		

		return Vo;

	}

	public HasNextPaging paging2(String endNum) {
		HasNextPaging vo = new HasNextPaging();
		if (endNum == null || endNum.equals("")) {
			vo.setStartNum(0);
		} else {
			int _endNum = Integer.parseInt(endNum);
			vo.setStartNum(_endNum);
		}
		vo.setPagePerCount(10);
		vo.setEndNum(vo.getStartNum());

		return vo;
	}
	
	//hasNext
	public HasNextPaging paging2(String endNum, String isNext) {
		System.out.println("paging2 1");
		HasNextPaging vo = new HasNextPaging();
		int _endNum = endNum == null ? 1 : Integer.parseInt(endNum);
		String _isNext = isNext == null ? "true" : isNext;
		System.out.println("paging2 2");
		if(_isNext.equals("false")) {
			System.out.println("paging2 3");
			_endNum -= 10;
		}
		System.out.println("paging2 4");
		vo.setStartNum(_endNum);
		vo.setPagePerCount(10);
		vo.setEndNum(vo.getStartNum());
		
		return vo;
	}
/*
	public List<AticleVo> listArticle(ListPagingVo paging) {

		return articleDAO.ListArticle(paging);
	}
*/
	public List<AticleVo> listArticle() {
		//ListPagingVo paging = paging(num);
		return articleDAO.ListArticle();
	}
	public List<AticleVo> listArticle2(HasNextPaging paging) {
		List<AticleVo> list = articleDAO.listArticle2(paging);

		return list;
	}

	public boolean isNext(List<AticleVo> list) {
		if (list.size() != 11) {
			return false;
		} else
			return true;

	}

}
