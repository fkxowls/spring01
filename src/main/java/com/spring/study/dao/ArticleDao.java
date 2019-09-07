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
	public PageList<ArticleVo> getArticlePageListWithCount(ArticleParam vo) {
		return super.selectPageList(mapper + "listArticle2", mapper + "totalArticle", vo);
	}
	
/*********************************************************************************/
	@AddComments(useMore = true, useTotal = true)
	@CacheInSession(name = "ArticleDao.getArticlePageListWithTotalCount", key = "userId,page,pageSize,sort", type = "com.spring.study.model.article.ArticleParam")
	public PageList<Article> getArticlePageListWithTotalCount(ArticleParam vo) {
		//return exampleDtoCaching(vo, "endPaging");
		return super.selectPageList(mapper + "listArticle2", mapper + "totalArticle", vo);
	}
	
	@AddComments(useMore = true, useTotal = true)
	@CacheInSession(name = "ArticleDao.getMoreListArticle", key = "userId,page,pageSize,sort", type = "com.spring.study.model.article.ArticleParam")
	public List<Article> getMoreListArticle(ArticleParam vo) {
		return sqlSession.selectList(mapper + "listArticle2", vo);
	}
	//hasNext
	@AddComments(useMore = true, useTotal = true)
	public PageList<Article> getArticlePageList(ArticleParam vo) {
		//return exampleDtoCaching(vo, "hsaNextPaging");
		return super.selectPageList(mapper + "listArticle2", vo);
	}
/*********************************************************************************/	
	

	public boolean isExistsArticle(String articleId) {
		String result = sqlSession.selectOne(mapper + "isarticleId", articleId);
		if (result.equals("Y")) {
			System.out.println("true");
			return true;
		}

		return false;
	}

	//@AddComments(useMore = true, useTotal = true)
	public Article viewArticle(String aritcleNo) {
		return sqlSession.selectOne(mapper + "viewArticle", aritcleNo);

	}

	public int insertArticle(String articleId, ArticleDto articleDto, User user) {
		ArticleVo articleVo = new ArticleVo();//TODO 직접 만들지 말것
		articleVo.setArticleId(articleId);
		articleVo.setWriteMemberId(user.getUserId());
		articleVo.setTitle(articleDto.getTitle());
		articleVo.setContent(articleDto.getContent());
		articleVo.setWriteMemberId(articleDto.getWriteMemberId());
		
		return sqlSession.insert(mapper + "insertArticle", articleVo);

	}

	public int updateArticle(ArticleDto articleDto, User user) {
		ArticleVo articleVo = new ArticleVo();
		articleVo.setArticleId(articleDto.getArticleId());
		articleVo.setWriteMemberId(user.getUserId());
		articleVo.setTitle(articleDto.getTitle());
		articleVo.setContent(articleDto.getContent());
		articleVo.setModifyMemberId(articleDto.getModifyMemberId());
		
		return sqlSession.update(mapper + "updateArticle", articleDto);
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

	public int replyArticle(ArticleDto articleDto) {
		ArticleVo articleVo = new ArticleVo();
		articleVo.setArticleId(this.getNextArticleId());
		articleVo.setParentId(articleDto.getParentId());
		articleVo.setTitle(articleDto.getTitle());
		articleVo.setContent(articleDto.getContent());
		articleVo.setWriteMemberId(articleDto.getWriteMemberId());
		
		return sqlSession.insert(mapper + "insertReply", articleVo);
	}

	public boolean equalsWriterId(String articleId) {
		String result = sqlSession.selectOne(mapper + "equalsWriterId", articleId);
		if (result.equals("Y")) {
			return true;
		}
		return false;
	}

	public Article getWriterId(String articleId) {
		return sqlSession.selectOne(mapper+"getWriterId", articleId);
	}
	
	public String getNextArticleId() {

		return sqlSession.selectOne(mapper + "articleId");
	}

	public List<ArticleVo> getNoticeList() {
		return sqlSession.selectList(mapper + "noticeList");

	}
	
	public void addArticleReadCount(String articleId) {
		sqlSession.insert(mapper + "addArticleReadCount", articleId);
	}

	public int registerNotice(String articleId, ArticleDto articleDto) {
		NoticeVo noticeArticleVo = new NoticeVo();
		noticeArticleVo.setArticleId(articleId);
		noticeArticleVo.setDisplayStartDate(articleDto.getDisplayStartDate());
		noticeArticleVo.setDisplayEndDate(articleDto.getDisplayEndDate());
		
		return sqlSession.insert(mapper + "registerNotice", noticeArticleVo);
	}

	public List<ArticleVo> getMyArticleList(String userId) {

		return sqlSession.selectList(mapper + "getMyArticleList", userId);
	}

	public boolean isNoticeId(String articleId) {
		String result = sqlSession.selectOne(mapper + "isNoticeId", articleId);
		if (result.equals("Y")) {
			return true;
		}
		return false;
	}

	public int getMemberLevel(Member memberDTO) {
		int result = sqlSession.selectOne(mapper + "chkMemberLevel", memberDTO);

		return result;

	}
	
//	public PageList<Article> exampleDtoCaching(ArticleParam vo, String pagingType) {
//		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//		HttpServletResponse resp = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
//				.getResponse();
//		HttpSession session = req.getSession();
//		
//		PageList<Article> list = null;
//		List<Article> tmpList = null;
//		{							  
//			Date curDate = new Date();
//			String key = ArticleDao.class.getName() + ".ListArticleTest" + "&page=" + vo.getPage() + "&pageSize=" + vo.getPageSize();
//			String expireDate = (String) session.getAttribute("expire" + key);
//			
//			if(!expiresDate(curDate,expireDate)) {
//				tmpList = (List<Article>) session.getAttribute(key);
//				list = new PageList<Article>(tmpList);
//				return list;
//			}
//		}
//		
//		if(pagingType.equals("endPaging")) {
//			list = super.selectPageDto(mapper + "listArticle2", mapper + "totalArticle", vo);
//		}else if(pagingType.equals("endPagingMoreView")) {
//			List selectList = sqlSession.selectList(mapper + "listArticle2", vo);
//			list = new PageList<>(selectList);
//		}else if(pagingType.equals("hsaNextPaging")) {
//			list = super.selectPageDto(mapper + "listArticle2", vo);
//		}
//		tmpList = list.getList();
//		{
//			Date curDate = new Date();
//			String key = ArticleDao.class.getName() + ".ListArticleTest" + "&page=" + vo.getPage() + "&pageSize=" + vo.getPageSize();
//			String ttl = "3600"; // 1시간 // TODO 커스텀 ttl 만큼 날짜 추가해줌
//			Date expireTime = DateUtils.addSeconds(curDate, Integer.parseInt(ttl));
//		
//			String expireDate = fdf.format(expireTime);
//			session.setAttribute(key, tmpList);
//			session.setAttribute("expire" + key, expireDate);
//		}
//
//		return list;
//	}

	@CacheInSession(name = "ArticleDao.listArticleTest", key = "userId,page,pageSize", type = "com.spring.study.model.article.ArticleParam")
	public List<Article> listArticleTest(ArticleParam vo) {
		List<Article> list = null;
//		if(pagingType.equals("endPaging")) {
//			list = super.selectPageDto(mapper + "listArticle2", mapper + "totalArticle", vo);
//		}else if(pagingType.equals("endPagingMoreView")) {
			list = sqlSession.selectList(mapper + "listArticle2", vo);
//		}else if(pagingType.equals("hsaNextPaging")) {
//			list = super.selectPageDto(mapper + "listArticle2", vo);
//		}
		return list;
	}
	
	private boolean expiresDate(Date curTime, String expireDate) {
		if(null == expireDate) {
			return true;
		}
		
		try {
			Date expireTime = DateUtils.parseDate(expireDate, fdf.getPattern());
			
			// 뒤날짜가 크면 -1 작으면 1
			int expire = DateUtils.truncatedCompareTo(curTime, expireTime, Calendar.SECOND);
			
			if(expire > 0) {
				return true;
			}
		} catch (ParseException e) {
			return true;
		}
		
		return false;
	}
	@AddComments(useMore = true, useTotal = true)
	public PageList<Article> getMyClipboard(ArticleParam reqParam) {
		return super.selectPageList(mapper + "getClipboard", reqParam);
		
	}

	public List<Article> getTopLevelArticles(String articleNumbers) {
		return sqlSession.selectList(mapper + "rootArticleList", articleNumbers);
		
	}
	//배치 로직
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
