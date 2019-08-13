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
import com.spring.study.board.model.ArticleVo;
import com.spring.study.common.model.CommonCode;
import com.spring.study.common.model.CommonParamter;
import com.spring.study.common.model.PageList;
import com.spring.study.member.model.Member;

import javassist.NotFoundException;
import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;

@Service("ArticleService")
public class ArticleService {

	private static final Logger logger = LoggerFactory.getLogger(ArticleService.class);
	
	@Autowired
	private ArticleDao articleDao;

	public ArticleVo getArticleContents(String articleId, Member member) throws Exception {
		boolean isNoticeId = articleDao.isNoticeId(articleId);
		ArticleVo returnVo = null;

		if (isNoticeId) {// 공지글 테이블에 글이있을 시
			if (null == member) {
				throw new NotFoundException("로그인 후 이용가능합니다");
			}
			String userLevel = member.getMemberLevel();
			//userLevel = articleDao.getMemberLevel(member);
			/*
			 * if (0 == memberLevel) { throw new NotFoundException("로그인 후 이용가능합니다"); }
			 */
			if (CommonCode.USER_LEVEL_CD_NOMAL.getCode().equals(userLevel)) {
				throw new SQLException("우수회원부터 접근 가능합니다");
			} else {
				returnVo = articleDao.viewArticle(articleId);
			}

		} else {
			returnVo = articleDao.viewArticle(articleId);
		}

		/****************************************
		 * 위 흐름을 쿼리로만 가능한가???????? 예외를 강제하는 건 잘못된게 아닌가??? 사용자 예외를 만들어 예외를 발생시 고려해야할
		 * 부분??-런타임예외?? 체크예외??
		 ****************************************/
		return returnVo;
	}

	//여기서 일어날 오류??
	@Transactional(rollbackFor = Exception.class)
	public String writeArticle(ArticleDto articleDto) {
		String articleId = this.giveArticleId();
		articleDto.setArticleId(articleId);
	
		articleDao.insertArticle(articleDto);

		// if - NOTICE_TABLE에 데이터 입력중에 ARTICLE테이블에 등록된 글 정보가 꺠진다면?
		if (CommonCode.ARTICLE_TYPE_CD_NOTICE_N.getCode().equals(articleDto.getArticleTypeCd())) {
			articleDao.registerNotice(articleDto);//articleDto은 다량의 필드를 가지고 있음 - 이 dto를 동시에 여러명이 접속하여 주고받을때 부하가 심하게 걸리지 않는지
		}

		return articleId;
	}

	public void modifyArticle(ArticleVo articleVo, Member user) throws Exception {
		if(user == null) {	throw new NullPointerException("로그인 세션 만료"); }
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
	public PageList<Article> getArticlePageListWithCount(CommonParamter req) {
		//PageList<ArticleVo> pageList = articleDao.getArticlePageListWithCount(req);
		//feed형으로 받을 시
		PageList<Article> feedTypePageList = articleDao.getArticlePageListWithCountAddComments(req);
		return feedTypePageList;
	}

	public List<Article> getArticleList(CommonParamter req) {
		//List<ArticleVo> list = articleDao.getListArticleAddComments(req);
		List<Article> list = articleDao.ListArticleTest(req);
		return list;
	}

	public PageList<ArticleVo> getArticlePageList(CommonParamter req) {
		PageList<ArticleVo> resp = articleDao.getArticlePageList(req);
		return resp;
	}

	/****************************************************************************************************
	 ****************************************************************************************************
	 ****************************************************************************************************/
	
	public boolean isEqualsWriterId(ArticleVo articleVo, Member user) {
		if(user.getMemberId().equals(articleVo.getWriteMemberId())) {
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
