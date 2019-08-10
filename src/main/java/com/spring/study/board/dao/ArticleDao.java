package com.spring.study.board.dao;

import java.io.IOException;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.study.board.controller.ArticleController;import com.spring.study.board.model.ArticleVo;
import com.spring.study.board.model.CommonRequestDto;
import com.spring.study.board.model.HasNextPaging;
import com.spring.study.board.model.NoticeArticleVo;
import com.spring.study.common.aop.AddComments;
import com.spring.study.common.model.PageList;
import com.spring.study.member.model.Member;

@Repository("articleDAO")
public class ArticleDao extends BaseDao {
	private static final Format fdf = FastDateFormat.getInstance("yyyyMMdd", Locale.getDefault());
	private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);
	private static String mapper = "mapper.article."; // TODO .을 여기로 옮겨오고, 이름에서는 .을 맨 앞에꺼 뺀다

	@Autowired
	SqlSession sqlSession;

	@AddComments
	public PageList<ArticleVo> getArticlePageListWithCountAddComments(CommonRequestDto vo) {
		return super.selectPageDto(mapper + "listArticle2", mapper + "totalArticle", vo);
	}

	public PageList<ArticleVo> getArticlePageListWithCount(CommonRequestDto vo) {
		return super.selectPageDto(mapper + "listArticle2", mapper + "totalArticle", vo);
	}

	public PageList<ArticleVo> getArticlePageList(CommonRequestDto vo) {
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
	public List<ArticleVo> ListArticle(CommonRequestDto vo) {

		return sqlSession.selectList(mapper + "listArticle2", vo);
	}

	@AddComments
	public ArticleVo viewArticle(String aritcleNo) {
		return sqlSession.selectOne(mapper + "viewArticle", aritcleNo);

	}

	public void insertArticle(ArticleVo articleVo) {
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

	/*
	 * public List<ArticleReplyVo> commentsList(String articleIds) { return
	 * sqlSession.selectList("mapper.comment.listComment", articleIds); }
	 */

	public boolean equalsWriterId(ArticleVo vo) {
		String result = sqlSession.selectOne(mapper + "equalsWriterId", vo);
		if (result.equals("Y")) {
			return true;
		}
		return false;
	}

	// hasNext ArticleList
	public List<ArticleVo> ArticleList(HasNextPaging vo) {

		return sqlSession.selectList(mapper + "listArticle2", vo);
	}

	public List<ArticleVo> listArticle2(HasNextPaging Vo) {

		return sqlSession.selectList(mapper + "listArticle2", Vo);
	}

	public String getNextArticleId() {

		return sqlSession.selectOne(mapper + "articleId");
	}

	public List<ArticleVo> getNoticeList() {
		return sqlSession.selectList(mapper + "noticeList");

	}

	public void registerNotice(NoticeArticleVo articleVo) {
		sqlSession.insert(mapper + "registerNotice", articleVo);
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

	public List<ArticleVo> ListArticleTest(CommonRequestDto vo) {

		List<ArticleVo> list = sqlSession.selectList("mapper.article.listArticle2", vo);

		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		HttpServletResponse resp = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getResponse();
		HttpSession session = req.getSession();
		// TODO 입력 -> key, value, ttl, expire(옵셔널)
		// 표준포맷 : "2019/08/09 23:59:59"

		String key = "com.spring.study.board.dao.ArticleDAO.ListArticleTest" + "&page=" + vo.getPage() + "&pageSize="
				+ vo.getPageSize();

		
		String date = fdf.format(new Date()); // TODO ttl 만큼 날짜 추가해줌	
		String expireDate = (String) session.getAttribute("expire" + key);
		String value = "";
		ObjectMapper mapper = new ObjectMapper();
		
		boolean isExpire =  compareExpireDate(date,expireDate);
		if(isExpire) {
			String ttl = "3600"; // 1시간
			int intDate = Integer.parseInt(date)+Integer.parseInt(ttl);
			expireDate = Integer.toString(intDate);
		
			try {
				value = mapper.writeValueAsString(list);
			} catch (JsonProcessingException e) {
				// TODO 로그남기기
				e.printStackTrace();
			}
		
			session.setAttribute("expire" + key, expireDate);
			session.setAttribute(key, value);
		}
		
		expireDate = (String) session.getAttribute("expire" + key);
		value = (String) session.getAttribute(key);
		//직렬화?? 역직렬화??? stream.map으로??? ????
		try {
			list = mapper.readValue(value, List.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // TODO 테스트 필요
		

		return list;
	}
	//util패키지를 만들어서 거기로 빼야하나???
	private boolean compareExpireDate(String curDate, String expireDate) {
		if(null == expireDate) { expireDate = "0"; }
		
		long curTime = Integer.parseInt(curDate);
		long expireTime = Integer.parseInt(expireDate);
		
		if(curTime < expireTime) { return false; }
		else { return true; }
		
	}

	public static void main(String[] args) {
		String expireDate = fdf.format(new Date());
		System.out.println(expireDate);
		System.out.println(Integer.parseInt(expireDate)+1600);
	}

}

// TODO 넣을거 -> key+value, key+expire

//
//	List<ArticleVo> list;
//	Cookie cookie = null;
//	Cookie[] cookieArr = req.getCookies();
//
//	Date date = new Date();
//	String curDateTime = dateFormat(date);
//
//	long timeSpent = -1;
//
//	System.out.println("쿠키목록확인");
//	// TODO DTO캐싱 로직 구현 - 읽기 get
//	if (cookieArr != null) {
//		for (int i = 0; i < cookieArr.length; i++) {
//			if (cookieArr[i].getName().equals("issueDate")) {
//				cookie = cookieArr[i];
//				String cookieDate = cookie.getValue();
//				long pastDate = Long.parseLong(cookieDate);
//				long curDate = Long.parseLong(curDateTime);
//				timeSpent = (curDate - pastDate) / 60000;
//				break;
//			} else {
//				cookie = new Cookie("issueDate", curDateTime);
//				cookie.setComment("DTO쿠키 저장 시간");
//				cookie.setMaxAge(60 * 10);
//				resp.addCookie(cookie);
//			}
//		}
//	} else {
//		cookie = new Cookie("issueDate", curDateTime);
//		cookie.setComment("DTO쿠키 저장 시간");
//		cookie.setMaxAge(60 * 10);
//		resp.addCookie(cookie);
//	}
//
//	// TODO DTO캐싱 로직 구현 - 쓰기 set
//	if (timeSpent > 10) {
//		list = sqlSession.selectList("mapper.article.listArticle2", vo);
//		session.setAttribute("sessionArticleList", list);
//		cookie = new Cookie("issueDate", curDateTime);
//		cookie.setMaxAge(60 * 10);
//		resp.addCookie(cookie);
//	} else if (timeSpent == -1) {
//	list = sqlSession.selectList("mapper.article.listArticle2", vo);
//		session.setAttribute("sessionArticleList", list);
//	} else {
//		list = (List<ArticleVo>) session.getAttribute("sessionArticleList");
//	}
