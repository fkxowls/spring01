package com.spring.study.dao;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.study.common.aop.AddComments;
import com.spring.study.common.model.BaseDao;
import com.spring.study.common.model.BaseParam;
import com.spring.study.common.model.PageList;
import com.spring.study.model.article.Article;
import com.spring.study.model.article.ArticleDto;
import com.spring.study.model.article.ArticleParam2;
import com.spring.study.model.article.ArticleParam2;
import com.spring.study.model.article.ArticleVo;
import com.spring.study.model.article.NoticeArticleVo;
import com.spring.study.model.user.Member;
import com.spring.study.model.user.User;

@Repository("articleDAO")
public class ArticleDao extends BaseDao {
	private static final FastDateFormat fdf = FastDateFormat.getInstance("yyyyMMddHHmmss", Locale.getDefault());
	private static final Logger logger = LoggerFactory.getLogger(ArticleDao.class);
	private static String mapper = "mapper.article."; 

	@Autowired
	SqlSession sqlSession;

	@AddComments
	public PageList<Article> getArticlePageListWithCountAddComments(ArticleParam2 vo) {
		return super.selectPageDto(mapper + "listArticle2", mapper + "totalArticle", vo);
	}
	
	//@AddComments
	public List<Article> getListArticleAddComments(BaseParam vo) {

		return sqlSession.selectList(mapper + "listArticle2", vo);
	}
	
	public PageList<ArticleVo> getArticlePageListWithCount(BaseParam vo) {
		return super.selectPageDto(mapper + "listArticle2", mapper + "totalArticle", vo);
	}

	public PageList<Article> getArticlePageList(BaseParam vo) {
		return super.selectPageDto(mapper + "listArticle2", vo);
	}
	
	

	public boolean isExistsArticle(String articleId) {
		String result = sqlSession.selectOne(mapper + "isarticleId", articleId);
		if (result.equals("Y")) {
			System.out.println("true");
			return true;
		}

		return false;
	}

	//@AddComments
	public Article viewArticle(String aritcleNo) {
		return sqlSession.selectOne(mapper + "viewArticle", aritcleNo);

	}

	public int insertArticle(String articleId, ArticleDto articleDto, User user) {
		ArticleVo articleVo = new ArticleVo();
		articleVo.setArticleId(articleId);
		articleVo.setWriteMemberId(user.getMemberId());
		articleVo.setTitle(articleDto.getTitle());
		articleVo.setContent(articleDto.getContent());
		articleVo.setWriteMemberId(articleDto.getWriteMemberId());
		
		return sqlSession.insert(mapper + "insertArticle", articleVo);

	}

	public int updateArticle(ArticleDto articleDto) {
		ArticleVo articleVo = new ArticleVo();
		articleVo.setArticleId(articleDto.getArticleId());
		articleVo.setWriteMemberId(articleDto.getWriteMemberId());
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

	public void deleteArticle(ArticleDto vo) {
		sqlSession.delete(mapper + "deleteArticle", vo);

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

	public boolean equalsWriterId(ArticleDto article) {
		String result = sqlSession.selectOne(mapper + "equalsWriterId", article);
		if (result.equals("Y")) {
			return true;
		}
		return false;
	}

	public Article getWriterId(String articleId) {
		//return "admin";
		return sqlSession.selectOne(mapper+"getWriterId", articleId);
	}
	
	public String getNextArticleId() {

		return sqlSession.selectOne(mapper + "articleId");
	}

	public List<ArticleVo> getNoticeList() {
		return sqlSession.selectList(mapper + "noticeList");

	}

	public int registerNotice(String articleId, ArticleDto articleDto) {
		NoticeArticleVo noticeArticleVo = new NoticeArticleVo();
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
	//TODO AOP로 분리 
	public List<Article> ListArticleTest(BaseParam vo) {
		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		HttpServletResponse resp = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getResponse();
		HttpSession session = req.getSession();
		
		{
			Date curDate = new Date();
			String key = ArticleDao.class.getName() + ".ListArticleTest" + "&page=" + vo.getPage() + "&pageSize=" + vo.getPageSize();
			String expireDate = (String) session.getAttribute("expire" + key);
			
			if(!expiresDate(curDate,expireDate)) {
				List<Article> list = (List<Article>) session.getAttribute(key);
				return list;
			}
		}

		List<Article> list = sqlSession.selectList("mapper.article.listArticle2", vo);
		
		{
			Date curDate = new Date();
			String key = ArticleDao.class.getName() + ".ListArticleTest" + "&page=" + vo.getPage() + "&pageSize=" + vo.getPageSize();
			String ttl = "3600"; // 1시간 // TODO 커스텀 ttl 만큼 날짜 추가해줌
			Date expireTime = DateUtils.addSeconds(curDate, Integer.parseInt(ttl));
		
			String expireDate = fdf.format(expireTime);
			session.setAttribute(key, list);
			session.setAttribute("expire" + key, expireDate);
		}

		return list;
	}
	//util패키지를 만들어서 거기로 빼야하나???
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
	
	public void test() {
		System.out.println("테스트용");
	}



}
