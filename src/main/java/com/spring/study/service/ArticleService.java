package com.spring.study.service;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.spring.study.common.model.BaseParam;
import com.spring.study.common.model.CommonCode;
import com.spring.study.common.model.PageList;
import com.spring.study.dao.ArticleDao;
import com.spring.study.model.article.Article;
import com.spring.study.model.article.ArticleDto;
import com.spring.study.model.article.ArticleParam;
import com.spring.study.model.article.ArticleParam;
import com.spring.study.model.article.ArticleVo;
import com.spring.study.model.user.Member;
import com.spring.study.model.user.User;
import com.spring.study.model.user.UserVo;

import javassist.NotFoundException;
import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;

@Service("ArticleService")
public class ArticleService {

	private static final Logger logger = LoggerFactory.getLogger(ArticleService.class);

	@Autowired
	private ArticleDao articleDao;
	
	public boolean isNotice(String articleId) {
		return articleDao.isNoticeId(articleId);
	}

	public Article getArticle(String articleId, User user) throws RuntimeException {
		boolean isNoticeId = articleDao.isNoticeId(articleId);
		if(isNoticeId && !user.isLogon()) {
			throw new RuntimeException("로그인 후 이용가능합니다");
		}
		
		Article article = articleDao.viewArticle(articleId);
		
		if(article.checkAccessLevel(user)) {
			throw new RuntimeException("접근 권한이 없습니다");
		}
		
		return article;
	}

	@Transactional(rollbackFor = Exception.class)
	public void writeArticle(ArticleDto articleDto, User user) throws SQLException {
		String articleId = this.getArticleIdNextSeq();

		int result = articleDao.insertArticle(articleId, articleDto, user);
		if(1 != result) { throw new SQLException(" 글 등록중 오류가 발생하였습니다. "); }
		
		if (CommonCode.ARTICLE_TYPE_CD_NOTICE_Y.getCode().equals(articleDto.getArticleTypeCd())) {
			result = articleDao.registerNotice(articleId, articleDto);
			if(1 != result) { throw new SQLException(" 글 등록중 오류가 발생하였습니다. "); }
		}
		
	}

	public void modifyArticle(ArticleDto articleDto, User user) throws NotFoundException, SQLException {
		boolean isExistsArticle = articleDao.isExistsArticle(articleDto.getArticleId());

		if (!isExistsArticle) {
			new NotFoundException("해당 글이 존재하지 않습니다.");
		}
		
		int result = articleDao.updateArticle(articleDto, user);
		if(1 != result) {
			throw new SQLException("글 수정 중 오류가 발생하였습니다.");
		}
		
	}

	public void deleteArticle(String articleid) throws Exception {
		boolean isEquals = articleDao.equalsWriterId(articleid);
		if (!isEquals) {
			throw new IllegalAccessException("작성자만 삭제할 수 있습니다.");
		}
		
		int result = articleDao.deleteArticle(articleid);
		if(1 != result) { throw new SQLException(" 오류가 발생하였습니다. "); }
	}
	
	// transaction
	public void writeReply(ArticleDto articleDto) throws NotFoundException, SQLException {
		// 답글쓰기전 부모글 체크
		boolean isExistsArticle = articleDao.isExistsArticle(articleDto.getParentId());
		if (!isExistsArticle) {
			throw new NotFoundException("답변하려는 글이 존재하지 않습니다.");
		}

		int result = articleDao.replyArticle(articleDto);
		if (0 == result) {
			throw new SQLException("답글 쓰기중에 오류가 발생하였습니다. 잠시후 다시 시도해주세요");
		}

		isExistsArticle = articleDao.isExistsArticle(articleDto.getParentId());
		if (!isExistsArticle) {
			throw new NotFoundException("답변하려는 글이 존재하지 않습니다.");
		}

	}

	// 글 번호 조회
	public String getArticleIdNextSeq() {
		String result = articleDao.getNextArticleId();
		return result;
	}
	
	public Article getWriterId(String articleId) {
		return articleDao.getWriterId(articleId);
	}

	/*
	 * public void insertReComment(CommentsVo replyVo) {
	 * commentDAO.writeComment(replyVo); }
	 */

	/****************************************************************************************************
	 ****************************************************************************************************
	 ****************************************************************************************************/
	public PageList<Article> getArticlePageListWithCount(ArticleParam req) {
		// PageList<ArticleVo> pageList = articleDao.getArticlePageListWithCount(req);
		// feed형으로 받을 시
		PageList<Article> feedTypePageList = articleDao.getArticlePageListWithCountAddComments(req);
		return feedTypePageList;
	}

	public List<Article> getArticleList(BaseParam req) {
		List<Article> list = articleDao.getListArticleAddComments(req);
		//List<Article> list = articleDao.ListArticleTest(req);
		return list;
	}

	public PageList<Article> getArticlePageList(BaseParam req) {
		PageList<Article> resp = articleDao.getArticlePageList(req);
		return resp;
	}

	/****************************************************************************************************
	 ****************************************************************************************************
	 ****************************************************************************************************/

	//아래 메서드 삭제
	public boolean isEqualsWriterId(String writerId, UserVo user) {
		if (user.getUserId().equals(writerId)) {
			return true;
		}

		return false;
	}

	public int getTotalArticles() {
		return articleDao.getTotalArticles();
	}

	public int getSequence() {
		return articleDao.getSequence();
	}

	public List<ArticleVo> getNoticeList() {
		return articleDao.getNoticeList();

	}

	public List<ArticleVo> getMyArticleList(Member member) {
		String userId = "";

		try {
			userId = member.getMemberId();
		} catch (NullPointerException e) {
			throw new NullPointerException("로그인 세션 만료");
		}

		List<ArticleVo> myArticleList = articleDao.getMyArticleList(userId);
		return myArticleList;
	}

}
