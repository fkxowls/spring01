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
import com.spring.study.model.article.ArticleParam;
import com.spring.study.model.article.ArticleParam;
import com.spring.study.model.article.ArticleVo;
import com.spring.study.model.article.NoticeVo;
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
	public PageList<ArticleVo> getArticlePageListWithCount(BaseParam vo) {
		return super.selectPageDto(mapper + "listArticle2", mapper + "totalArticle", vo);
	}
	
/*********************************************************************************/
	//@DaoCaching
	//@AddComments
	public PageList<Article> getArticlePageListWithTotalCount(ArticleParam vo) {
		return exampleDtoCaching(vo, "endPaging");
		//return super.selectPageDto(mapper + "listArticle2", mapper + "totalArticle", vo);
	}
	
//	@AddComments
	public List<Article> getMoreListArticle(ArticleParam vo) {
		return exampleDtoCaching(vo, "endPagingMoreView");
		//return sqlSession.selectList(mapper + "listArticle2", vo);
	}
	//hasNext
	//@AddComments
	public PageList<Article> getArticlePageList(ArticleParam vo) {
		return exampleDtoCaching(vo, "hsaNextPaging");
		//return super.selectPageDto(mapper + "listArticle2", vo);
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

	//@AddComments
	public Article viewArticle(String aritcleNo) {
		return sqlSession.selectOne(mapper + "viewArticle", aritcleNo);

	}

	public int insertArticle(String articleId, ArticleDto articleDto, User user) {
		ArticleVo articleVo = new ArticleVo();
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
		//return "admin";
		return sqlSession.selectOne(mapper+"getWriterId", articleId);
	}
	
	public String getNextArticleId() {

		return sqlSession.selectOne(mapper + "articleId");
	}

	public List<ArticleVo> getNoticeList() {
		return sqlSession.selectList(mapper + "noticeList");

	}
	
	public void addArticleReadCount(String articleId) {
		// 증가 쿼리는 아직 안함
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
	//XXX aop로 어떻게 분리해야할까요
	//endPaging같은 경우 getMoreListArticle의 리턴타입은 List<Article>, 
	//getArticlePageListWithTotalCount은 PageList<Article>
	//Session에 담을때 PageList<Article>타입으로 넣어야할까요??
	//List<Article>타입으로 저장해야한다면 첫페이지 진입시에 페이징정보(totalPage)는 어떻게 해야할까여
	public PageList<Article> exampleDtoCaching(ArticleParam vo, String pagingType) {
		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		HttpServletResponse resp = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getResponse();
		HttpSession session = req.getSession();
		
		PageList<Article> list = null;
		List<Article> tmpList = null;
		{							  
			Date curDate = new Date();
			String key = ArticleDao.class.getName() + ".ListArticleTest" + "&page=" + vo.getPage() + "&pageSize=" + vo.getPageSize();
			String expireDate = (String) session.getAttribute("expire" + key);
			
			if(!expiresDate(curDate,expireDate)) {
				tmpList = (List<Article>) session.getAttribute(key);
				list = new PageList<Article>(tmpList);
				return list;
			}
		}
		
		if(pagingType.equals("endPaging")) {
			list = super.selectPageDto(mapper + "listArticle2", mapper + "totalArticle", vo);
		}else if(pagingType.equals("endPagingMoreView")) {
			List selectList = sqlSession.selectList(mapper + "listArticle2", vo);
			list = new PageList<>(selectList);
		}else if(pagingType.equals("hsaNextPaging")) {
			list = super.selectPageDto(mapper + "listArticle2", vo);
		}
		
		{
			Date curDate = new Date();
			String key = ArticleDao.class.getName() + ".ListArticleTest" + "&page=" + vo.getPage() + "&pageSize=" + vo.getPageSize();
			String ttl = "3600"; // 1시간 // TODO 커스텀 ttl 만큼 날짜 추가해줌
			Date expireTime = DateUtils.addSeconds(curDate, Integer.parseInt(ttl));
		
			String expireDate = fdf.format(expireTime);
			session.setAttribute(key, list.getList());
			session.setAttribute("expire" + key, expireDate);
		}

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
	
	


}
