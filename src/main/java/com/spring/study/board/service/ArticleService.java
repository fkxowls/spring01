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

import javassist.NotFoundException;
import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;

@Service("ArticleService")
public class ArticleService {

	private static final Logger logger = LoggerFactory.getLogger(ArticleService.class);

	@Autowired
	private ArticleDao articleDao;

	public ArticleVo getArticle(String articleId, Member member) throws Exception {
		boolean isNoticeId = articleDao.isNoticeId(articleId);
		// XXX 리턴객체가 Vo와 같을때엔 그냥 Vo로 리턴하는것이 가능한건가여
		ArticleVo returnVo = null;
		//공지글 - 일반회원은 접근 못함
		if (isNoticeId) {
			if (null == member) {
				throw new NotFoundException("로그인 후 이용가능합니다");
			}
			String userLevel = member.getMemberLevel();

			if (CommonCode.USER_LEVEL_CD_NOMAL.getCode().equals(userLevel)) {
				throw new SQLException("우수회원부터 접근 가능합니다");
			} else {
				returnVo = articleDao.viewArticle(articleId);
			}
		} else {
			returnVo = articleDao.viewArticle(articleId);
		}

		return returnVo;
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

	public void modifyArticle(ArticleVo articleVo, Member user) throws Exception {
		if (user == null) {
			throw new NullPointerException("로그인 세션 만료");
		}
		String userId = user.getMemberId();
		articleVo.setModifyMemberId(userId);

		boolean isExistsArticle = articleDao.isExistsArticle(articleVo.getArticleId());

		if (!isExistsArticle) {
			new NotFoundException("권한없는 접근입니다");
		}

		// 수정일자 입력
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date();

		System.out.println(articleVo.getModifyDate() + "--====---=-=-=-=-=-===-=-=");
		articleDao.updateArticle(articleVo, curDate);
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

	public boolean isEqualsWriterId(ArticleVo articleVo, Member user) {
		if (user.getMemberId().equals(articleVo.getWriteMemberId())) {
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
