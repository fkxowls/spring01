package com.spring.study.board.dao;

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
import com.spring.study.board.model.Article;
import com.spring.study.board.model.ArticleDto;
import com.spring.study.board.model.ArticleParam;
import com.spring.study.board.model.ArticleVo;
import com.spring.study.board.model.NoticeArticleVo;
import com.spring.study.common.aop.AddComments;
import com.spring.study.common.model.BaseParam;
import com.spring.study.common.model.PageList;
import com.spring.study.member.model.Member;

@Repository("articleDAO")
public class ArticleDao extends BaseDao {
	private static final FastDateFormat fdf = FastDateFormat.getInstance("yyyyMMddHHmmss", Locale.getDefault());
	private static final Logger logger = LoggerFactory.getLogger(ArticleDao.class);
	private static String mapper = "mapper.article."; 

	@Autowired
	SqlSession sqlSession;

	@AddComments
	public PageList<Article> getArticlePageListWithCountAddComments(BaseParam vo) {
		return super.selectPageDto(mapper + "listArticle2", mapper + "totalArticle", vo);
	}
	
	@AddComments
	public List<ArticleVo> getListArticleAddComments(BaseParam vo) {

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

	@AddComments
	public ArticleVo viewArticle(String aritcleNo) {
		return sqlSession.selectOne(mapper + "viewArticle", aritcleNo);

	}
	//여기서는 Vo로 해야할거같은데 Service단 수정 후 ㄱㄱㄱ
	public void insertArticle(String articleId, ArticleParam articleParam) {
		ArticleVo articleVo = new ArticleVo();
		articleVo.setArticleId(articleId);
		articleVo.setTitle(articleParam.getTitle());
		articleVo.setContent(articleParam.getContents());
		articleVo.setWriteMemberId(articleParam.getWriterId());
		articleVo.setWriteDate(articleParam.getWriteDate());
		
		sqlSession.insert(mapper + "insertArticle", articleVo);

	}

	public void updateArticle(ArticleVo articleVo, Date curDate) {
		articleVo.setModifyDate(curDate);
		sqlSession.update(mapper + "updateArticle", articleVo);
	}

	public int getTotalArticles() {

		return sqlSession.selectOne(mapper + "totalArticle");
	}

	public int getSequence() {

		return sqlSession.selectOne(mapper + "getSequence");
	}

	public void deleteArticle(ArticleVo vo) {
		sqlSession.delete(mapper + "deleteArticle", vo);

	}

	public int replyArticle(ArticleVo articleVo) {
		return sqlSession.insert(mapper + "insertReply", articleVo);
	}

	public boolean equalsWriterId(ArticleVo vo) {
		String result = sqlSession.selectOne(mapper + "equalsWriterId", vo);
		if (result.equals("Y")) {
			return true;
		}
		return false;
	}

	public String getNextArticleId() {

		return sqlSession.selectOne(mapper + "articleId");
	}

	public List<ArticleVo> getNoticeList() {
		return sqlSession.selectList(mapper + "noticeList");

	}

	public void registerNotice(String articleId, ArticleParam articleParam) {
		NoticeArticleVo noticeArticleVo = new NoticeArticleVo();
		noticeArticleVo.setArticleId(articleId);
		noticeArticleVo.setDisplayStartDate(articleParam.getDisplayStartDate());
		noticeArticleVo.setDisplayEndDate(articleParam.getDisplayEndDate());
		
		sqlSession.insert(mapper + "registerNotice", noticeArticleVo);
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
	

}
