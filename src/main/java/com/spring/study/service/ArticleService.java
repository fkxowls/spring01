package com.spring.study.service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.study.common.model.CommonCode;
import com.spring.study.common.model.BaseParam;
import com.spring.study.common.model.PageList;
import com.spring.study.dao.ArticleDao;
import com.spring.study.model.article.Article;
import com.spring.study.model.article.ArticleDto;
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
	
	public boolean isNoticeArticle(String articleId) {
		return articleDao.isNoticeId(articleId);
	}
	//XXX 2 User, Article같은 객체를 서비스단까지 끌어와서 사용해도 되는건가요?
	public Article getNoticeArticle(String articleId, User user) throws Exception{
		Article returnArticle = null;
		if (!user.isLogon()) {//XXX 6 비로그인일경우 아래 예외를 던져서 메세지를 출력하고싶은데 그러지않고 NullException으로 바로 가는 이유가 있나요?? 
			throw new NotFoundException("로그인 후 이용가능합니다");
		}
		String userLevel = user.getMemberLevel();

		if (user.isAccessRestriction(CommonCode.USER_LEVEL_CD_NOMAL.getCode())) {
			throw new SQLException("우수회원부터 접근 가능합니다");
		} else {
			returnArticle = articleDao.viewArticle(articleId);//공지글만 조회하는 쿼리로 바꿀예정
		}
		return returnArticle;
	}

	public Article getArticle(String articleId) {
		Article returnArticle = articleDao.viewArticle(articleId);

		return returnArticle;
	}

	public Article getArticle(String articleId, User user) throws Exception {
		boolean isNoticeId = articleDao.isNoticeId(articleId);
		Article returnArticle = null;
		//공지글 - 일반회원은 접근 못함
		if (isNoticeId) {//XXX 4-1 이거에 대한 판단도 article객체가 해야하는건지??? 그렇다면 article객체에 dao를 주입해서 써도 되는지???
			if (!user.isLogon()) {
				throw new NotFoundException("로그인 후 이용가능합니다");
			}
			String userLevel = user.getMemberLevel();
			//CommonCode.USER_LEVEL_CD_NOMAL.getCode().equals(userLevel)
			if (user.isAccessRestriction(userLevel)) {
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
	public String writeArticle(ArticleParam articleParam) throws SQLException {
		String articleId = this.getArticleIdNextSeq();

		int result = articleDao.insertArticle(articleId, articleParam);
		if(1 != result) { throw new SQLException(" 글 등록중 오류가 발생하였습니다. "); }
		
		if (CommonCode.ARTICLE_TYPE_CD_NOTICE_Y.getCode().equals(articleParam.getArticleTypeCd())) {
			result = articleDao.registerNotice(articleId, articleParam);
			if(1 != result) { throw new SQLException(" 글 등록중 오류가 발생하였습니다. "); }
		}
		
		return articleId;
	}

	public void modifyArticle(ArticleDto articleDto, UserVo user) throws Exception {
		articleDto.setModifyMemberId(user.getMemberId());

		boolean isExistsArticle = articleDao.isExistsArticle(articleDto.getArticleId());

		if (!isExistsArticle) {
			new NotFoundException("권한없는 접근입니다");
		}
		//이거에 대한 결과값 리턴하기
		int result = articleDao.updateArticle(articleDto);
	}

	public void deleteArticle(ArticleDto article) throws Exception {
		boolean result = articleDao.equalsWriterId(article);

		if (!result) {
			throw new NotFoundException("권한없는 접근입니다");
		}

		articleDao.deleteArticle(article);
	}

	// transaction
	public String writeReply(ArticleDto articleDto) throws Exception {
		// 답글쓰기전 부모글 체크
		boolean isExistsArticle = articleDao.isExistsArticle(articleDto.getParentId());
		if (!isExistsArticle) {
			throw new NotFoundException("답변하려는 글이 존재하지 않습니다.");
		}

		int result = articleDao.replyArticle(articleDto);
		if (0 == result) {
			throw new InternalException("서버 오류입니다. 다시 시도해주세요.");
		}

		isExistsArticle = articleDao.isExistsArticle(articleDto.getParentId());
		if (!isExistsArticle) {
			throw new NotFoundException("답변하려는 글이 존재하지 않습니다.");
		}

		return "답글 작성에 성공 했습니다.";
	}

	// 글 번호 조회
	public String getArticleIdNextSeq() {
		String result = articleDao.getNextArticleId();
		return result;
	}
	
	public String getWriterId(String articleId) {
		return articleDao.getWriterId(articleId);
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
