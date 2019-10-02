package com.spring.study.dao;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.ibatis.session.SqlSession;
import org.aspectj.org.eclipse.jdt.internal.compiler.batch.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.spring.study.common.aop.AddComments;
import com.spring.study.common.aop.CacheInSession;
import com.spring.study.common.model.BaseDao;
import com.spring.study.common.model.PageList;
import com.spring.study.model.article.Article;
import com.spring.study.model.article.ArticleDto;
import com.spring.study.model.article.ArticleParam;
import com.spring.study.model.article.ArticleRankVo;
import com.spring.study.model.article.ArticleReadCountVo;
import com.spring.study.model.article.ArticleVo;
import com.spring.study.model.article.NoticeVo;
import com.spring.study.model.comments.Comment;
import com.spring.study.model.user.Member;
import com.spring.study.model.user.User;

@Repository("articleDAO")
public class ArticleDao extends BaseDao {
	private static final FastDateFormat fdf = FastDateFormat.getInstance("yyyyMMddHHmmss", Locale.getDefault());
	private static final Logger logger = LoggerFactory.getLogger(ArticleDao.class);
	private static String mapper = "mapper.article.";

	@Autowired
	SqlSession sqlSession;

	//단순 글리스트
	public PageList<ArticleVo> getArticlePageListWithCount(ArticleParam parameter) {
		return super.getPageList(mapper + "listArticle2", mapper + "totalArticle", parameter);
	}

	@AddComments(useMore = true)
	@CacheInSession(name = "ArticleDao.getArticlePageListWithTotalCount", key = "userId,page,pageSize,sort", type = "com.spring.study.model.article.ArticleParam")
	public PageList<Article> getArticlePageListWithTotalCount(ArticleParam parameter) {
		return super.getPageList(mapper + "listArticle2", mapper + "totalArticle", parameter);
	}

	@AddComments(useMore = true)
	@CacheInSession(name = "ArticleDao.getMoreListArticle", key = "userId,page,pageSize,sort", type = "com.spring.study.model.article.ArticleParam")
	public List<Article> getMoreListArticle(ArticleParam parameter) {
		return sqlSession.selectList(mapper + "listArticle2", parameter);
	}

	@AddComments(useMore = true)
	@CacheInSession(name = "ArticleDao.getArticlePageList", key = "userId,page,pageSize,sort", type = "com.spring.study.model.article.ArticleParam")
	public PageList<Article> getArticlePageListByHasNext(ArticleParam parameter) {
		return super.getPageListMore(mapper + "listArticle2", parameter);
	}
/*************************************************************************************************************************/
	//clipboard
	@AddComments(useMore = true, useTotal = true)
	@CacheInSession(name = "BoardDao.getClipboardListAddCommentsMore", key = "userId,page,pageSize,sort", type = "com.kkk26kkk.bbs.model.ArticleParam")
	public PageList<Article> getClipboardListAddCommentsMore(ArticleParam articleParam) {
		return super.getPageListMore("getClipboardList", articleParam);
	}
	
	public List<Article> selectParentArticleList(String articleIds) {
		return sqlSession.selectList("selectParentArticleList", articleIds);
	}
	//Feed 미구현
	@AddComments(useMore = true, useTotal = true)
	@CacheInSession(name = "ArticleDao.getFeedArticleListAddCommentsMore", key = "userId,page,pageSize,sort", type = "com.spring.study.model.article.ArticleParam")
	public PageList<Article> getFeedArticleListAddCommentsMore(ArticleParam articleParam) {
		return super.getPageListMore(mapper + "getFeedArticleList", articleParam);
	}
/*************************************************************************************************************************/
	
	// @AddComments(useMore = true, useTotal = true)
	public Article viewArticle(String aritcleNo) {
		return sqlSession.selectOne(mapper + "viewArticle", aritcleNo);

	}

	public int insertArticle(ArticleVo articleVo) {
		return sqlSession.insert(mapper + "insertArticle", articleVo);
	}

	public int updateArticle(ArticleVo articleVo) {
		return sqlSession.update(mapper + "updateArticle", articleVo);
	}

	public int getTotalArticles() {
		return sqlSession.selectOne(mapper + "totalArticle");
	}

	public int getSequence() {
		return sqlSession.selectOne(mapper + "getSequence");
	}

	public int deleteArticle(String articleId) {
		return sqlSession.delete(mapper + "deleteArticle", articleId);

	}

	public int replyArticle(ArticleVo articleVo) {
		return sqlSession.insert(mapper + "insertReply", articleVo);
	}

	public boolean equalsWriterId(String articleId) {
		String result = sqlSession.selectOne(mapper + "equalsWriterId", articleId);
		if (result.equals("Y")) {
			return true;
		}
		return false;
	}

	public Article getWriterId(String writeMemberId) {
		return sqlSession.selectOne(mapper + "getWriterId", writeMemberId);
	}

	public String getNextArticleId() {
		return sqlSession.selectOne(mapper + "articleId");
	}

	public void addArticleReadCount(String articleId) {
		sqlSession.insert(mapper + "addArticleReadCount", articleId);
	}

	public int registerNotice(NoticeVo noticeArticleVo) {
		return sqlSession.insert(mapper + "registerNotice", noticeArticleVo);
	}

	public boolean isNoticeId(String articleId) {
		String result = sqlSession.selectOne(mapper + "isNoticeId", articleId);
		if (result.equals("Y")) {
			return true;
		}
		return false;
	}
	
	public boolean isExistsArticle(String articleId) {
		String result = sqlSession.selectOne(mapper + "isarticleId", articleId);
		if (result.equals("Y")) {
			System.out.println("true");
			return true;
		}

		return false;
	}
/*************************************************************************************************************************/	
	// 배치 로직
	public List<ArticleRankVo> getAllArticleIds() {
		return sqlSession.selectList(mapper + "getAllArticleIds");
	}

	public List<Comment> getCommentsCntList() {
		return sqlSession.selectList(mapper + "getCommentsCntList");
	}

	public List<ArticleReadCountVo> getReadCntList() {
		return sqlSession.selectList(mapper + "getReadCntList");
	}

	public void deleteArticleRank() {
		sqlSession.delete(mapper + "deleteArticleRank");

	}

	public void insertArticleRank(List<ArticleRankVo> allArticleIds) {
		sqlSession.insert(mapper + "insertArticleRank", allArticleIds);
	}


	


}
