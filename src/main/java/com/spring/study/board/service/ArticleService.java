package com.spring.study.board.service;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
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

	public AticleVo viewArticle(int num) {
		return articleDAO.viewArticle(num);

	}

	public void writeArticle(AticleVo articleVo) {

		articleDAO.insertArticle(articleVo);

	}

	public void modifyArticle(AticleVo articleVo) throws Exception {
		int isArticleNo = articleDAO.selectArticle(articleVo.getArticleNo());
		if (isArticleNo == 0) {	throw new Exception(); }
		articleDAO.updateArticle(articleVo);
	}

	public int getTotalArticles() {
		return articleDAO.getTotalArticles();
	}

	public int getSequence() {
		return articleDAO.getSequence();
	}

	public void deleteArticle(Integer num) throws Exception {
		articleDAO.deleteArticle(num);
	}

	// transaction1
	public void replyArticle(AticleVo articleVo) throws Exception {

		// 답글쓰기전 부모글 체크
		int nowParentNo = articleDAO.selectArticle(articleVo.getArticleNo());
		articleVo.setParentNo(nowParentNo);
		// 현재 등록할 글의 글번호를 시퀀스조회를 통해 가져옴
		// 글쓰기 오류가 나면 시퀀스가 원래대로 돌아가야함 근데 안됨
		articleVo.setArticleNo(giveArticleNo());
		// 답글 db에 입력
		// int result =
		articleDAO.replyArticle(articleVo);
		// 답글 입력후 부모글 체크 (0이면 예외발생)
		// parentNo = 0;
		nowParentNo = articleDAO.selectArticle(articleVo.getArticleNo());
		if (nowParentNo == 0) {
			throw new Exception();
		}
		// return result;

	}

	public int giveArticleNo() {
		int result = articleDAO.setArticleNo();
		return result;
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

	/***************************************************************************/

	public PageDto<AticleVo> EndPagination(int page, int pageSize) {
		PageDto<AticleVo> req = new PageDto.Builder(page, pageSize).build();

		return articleDAO.ListArticle2(req);
	}

	public List<AticleVo> EndPagingMore(int page, int pageSize) {
		PageDto<AticleVo> req = new PageDto.Builder(page, pageSize).build();
		return articleDAO.ListArticle(req);
	}

	/*******************************************************************************/

	public PageDto<AticleVo> EndPaging(int page, int pageSize) {
		PageDto.Builder builder = new PageDto.Builder(page, pageSize);

		PageDto<AticleVo> req = new PageDto.Builder(page, pageSize).build();
		List<AticleVo> list = articleDAO.ListArticle(req);
		int totalCount = articleDAO.getTotalArticles();
		return new PageDto<AticleVo>(page, pageSize, totalCount, list, false);
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



	public List<AticleVo> listArticle2(HasNextPaging vo) {
		return articleDAO.ArticleList(vo);
	}

	public boolean isNext(List<AticleVo> list) {
		if (list.size() != 11) {
			return false;
		} else
			return true;

	}

}
