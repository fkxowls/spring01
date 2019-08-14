package com.spring.study.board.service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.study.board.dao.ArticleDao;
import com.spring.study.board.model.Article;
import com.spring.study.board.model.ArticleDto;
import com.spring.study.board.model.ArticleParam;
import com.spring.study.board.model.ArticleVo;
import com.spring.study.common.model.CommonCode;
import com.spring.study.common.model.BaseParam;
import com.spring.study.common.model.PageList;
import com.spring.study.member.model.Member;
import com.spring.study.member.model.User;
import com.spring.study.member.model.UserVo;

import javassist.NotFoundException;
import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;

@Service("ArticleService")
public class ArticleService {

	private static final Logger logger = LoggerFactory.getLogger(ArticleService.class);

	@Autowired
	private ArticleDao articleDao;
	
	//XXX 일반회원은 공지글을 못본다고 할 경우 아래 user등급 체크는 여기있는게 맞는지 아니면 컨트롤러에있는게 맞는지
	public Article getArticle(String articleId, User user) throws Exception {
		boolean isNoticeId = articleDao.isNoticeId(articleId);
		Article returnArticle = null;
		//공지글 - 일반회원은 접근 못함
		if (isNoticeId) {//XXX 이거에 대한 판단도 article객체가 해야하는건지???
			if (null == user) {
				throw new NotFoundException("로그인 후 이용가능합니다");
			}
			String userLevel = user.getMemberLevel();

			if (CommonCode.USER_LEVEL_CD_NOMAL.getCode().equals(userLevel)) {
				throw new SQLException("우수회원부터 접근 가능합니다");
			} else {
				returnArticle = articleDao.viewArticle(articleId);
			}
		} else {
			returnArticle = articleDao.viewArticle(articleId);
		}

		return returnArticle;
	}

	@Transactional(rollbackFor = Exception.class)
	public String writeArticle(ArticleParam articleParam) {
		String articleId = this.giveArticleId();
		//articleParam.setArticleId(articleId);

		articleDao.insertArticle(articleId, articleParam);

		if (CommonCode.ARTICLE_TYPE_CD_NOTICE_N.getCode().equals(articleParam.getArticleTypeCd())) {
			articleDao.registerNotice(articleId, articleParam);
		}

		return articleId;
	}

	public void modifyArticle(ArticleDto articleDto, UserVo user) throws Exception {
		String userId = user.getMemberId();
		articleDto.setModifyMemberId(userId);

		boolean isExistsArticle = articleDao.isExistsArticle(articleDto.getArticleId());

		if (!isExistsArticle) {
			new NotFoundException("권한없는 접근입니다");
		}
		
		articleDao.updateArticle(articleDto);
	}

	public void deleteArticle(ArticleVo vo) throws Exception {
		boolean result = articleDao.equalsWriterId(vo);

		if (!result) {
			throw new NotFoundException("권한없는 접근입니다");
		}

		articleDao.deleteArticle(vo);
	}

	// transaction
	public String writeReply(ArticleVo articleVo) throws Exception {
		// 답글쓰기전 부모글 체크
		boolean isExistsArticle = articleDao.isExistsArticle(articleVo.getArticleId());
		if (!isExistsArticle) {
			throw new NotFoundException("답변하려는 글이 존재하지 않습니다.");
		}

		articleVo.setArticleId(this.giveArticleId());

		int result = articleDao.replyArticle(articleVo);
		if (0 == result) {
			throw new InternalException("서버 오류입니다. 다시 시도해주세요.");
		}

		isExistsArticle = articleDao.isExistsArticle(articleVo.getArticleId());
		if (!isExistsArticle) {
			throw new NotFoundException("답변하려는 글이 존재하지 않습니다.");
		}

		return "답글 작성에 성공 했습니다.";
	}

	// 글 번호 조회
	/****************************
	 * 답글말고 일반 글쓰기에서 현재 시퀀스 조회후 + 1 을 하는건 좋지 않은 방법인가??
	 ****************************/
	public String giveArticleId() {
		String result = articleDao.getNextArticleId();
		return result;
	}

	/*
	 * public void insertReComment(CommentsVo replyVo) {
	 * commentDAO.writeComment(replyVo); }
	 */

	/****************************************************************************************************
	 ****************************************************************************************************
	 ****************************************************************************************************/
	public PageList<Article> getArticlePageListWithCount(BaseParam req) {
		// PageList<ArticleVo> pageList = articleDao.getArticlePageListWithCount(req);
		// feed형으로 받을 시
		PageList<Article> feedTypePageList = articleDao.getArticlePageListWithCountAddComments(req);
		return feedTypePageList;
	}

	public List<Article> getArticleList(BaseParam req) {
		// List<ArticleVo> list = articleDao.getListArticleAddComments(req);
		List<Article> list = articleDao.ListArticleTest(req);
		return list;
	}

	public PageList<Article> getArticlePageList(BaseParam req) {
		PageList<Article> resp = articleDao.getArticlePageList(req);
		return resp;
	}

	/****************************************************************************************************
	 ****************************************************************************************************
	 ****************************************************************************************************/

	public boolean isEqualsWriterId(String writerId, UserVo user) {
		if (user.getMemberId().equals(writerId)) {
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
