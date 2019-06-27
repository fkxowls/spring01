package com.spring.study.board.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.spring.study.ListPagingVo;
import com.spring.study.board.controller.AticleController;
import com.spring.study.board.vo.ArticleReplyVo;
import com.spring.study.board.vo.AticleVo;
import com.spring.study.board.vo.EndPagePaging;
import com.spring.study.board.vo.HasNextPaging;

@Repository("articleDAO")
public class ArticleDAO {
	private static final Logger logger = LoggerFactory.getLogger(AticleController.class);

	@Autowired
	SqlSession sqlSession;

	public AticleVo viewArticle(int aritcleNo) {
		return sqlSession.selectOne("mapper.article.viewArticle", aritcleNo);

	}

	public void insertArticle(AticleVo articleVo) {
		System.out.println("=======================================				articleVo:");
		System.out.println("=======================================			             : " + articleVo.getContent());
		sqlSession.insert("mapper.article.insertArticle", articleVo);

	}

	public void updateArticle(AticleVo articleVo) {
		sqlSession.update("mapper.article.updateArticle", articleVo);

	}

	public int getTotalArticles() {

		return sqlSession.selectOne("mapper.article.totalArticle");
	}

	public int getSequence() {

		return sqlSession.selectOne("mapper.article.getSequence");
	}

	public void deleteArticle(Integer num) {
		sqlSession.delete("mapper.article.deleteArticle", num);

	}

	public int replyArticle(AticleVo articleVo) {
		int num = sqlSession.insert("mapper.article.insertReply", articleVo);
		logger.info("===============넘어온 num:{}", num);
		return num;

	}

	public List<ArticleReplyVo> listComment(int articleNo) {
		List<ArticleReplyVo> list;
		// TODO DTO캐싱 로직 구현 - 읽기 get
		{
			list = sqlSession.selectList("mapper.comment.listComment", articleNo);
			// TODO DTO캐싱 로직 구현 - 쓰기 set
		}
		return list;
	}

	public int insertComment(ArticleReplyVo replyVo) {
		logger.info("=================== 		DAO insertComment:{}");
		logger.info("parentNo: {}", replyVo.getParentNo());
		return sqlSession.insert("mapper.comment.insertComment", replyVo);

	}

	// endPage ArticlesList
	public List<AticleVo> ListArticle(EndPagePaging vo) {
		logger.info("=========            startNum:{}", vo.getStartNum());

		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		HttpServletResponse resp = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getResponse();
		HttpSession session = req.getSession();

		List<AticleVo> list = null;
		Cookie cookie = null;
		Cookie[] cookieArr = req.getCookies();

		Date date = new Date();
		String curDateTime = dateFormat(date);

		long timeSpent = -1;

		System.out.println("쿠키목록확인");
		// TODO DTO캐싱 로직 구현 - 읽기 get
		if (cookieArr != null) {
			System.out.println("if문");
			for (int i = 0; i < cookieArr.length; i++) {
				System.out.println("for문");
				if (cookieArr[i].getName().equals("issueDate")) {
					System.out.println("if문2");
					cookie = cookieArr[i];
					System.out.println("쿠키 value 비교");
					String cookieDate = cookie.getValue();
					long pastDate = Long.parseLong(cookieDate);
					long curDate = Long.parseLong(curDateTime);
					timeSpent = (curDate - pastDate) / 60000;
					break;
				} else {
					cookie = new Cookie("issueDate", curDateTime);
					cookie.setComment("DTO쿠키 저장 시간");
					cookie.setMaxAge(60 * 10);
					resp.addCookie(cookie);
				}
			}
		} else {
			cookie = new Cookie("issueDate", curDateTime);
			cookie.setComment("DTO쿠키 저장 시간");
			cookie.setMaxAge(60 * 10);
			resp.addCookie(cookie);
		}
		
		// TODO DTO캐싱 로직 구현 - 쓰기 set
		if (timeSpent > 10) {
			System.out.println("쿠키만료됐으면");
			list = sqlSession.selectList("mapper.article.listArticle2", vo);
			session.setAttribute("sessionArticleList", list);
			cookie = new Cookie("issueDate", curDateTime);
			cookie.setComment("DTO쿠키 저장 시간");
			cookie.setMaxAge(60 * 10);
			resp.addCookie(cookie);
		} else if (timeSpent == -1) {
			System.out.println("첫진입시");
			list = sqlSession.selectList("mapper.article.listArticle2", vo);
			session.setAttribute("sessionArticleList", list);
		} else {
			System.out.println("쿠키가 만료안됐을때");
			list = (List<AticleVo>) session.getAttribute("sessionArticleList");
		}
		return list;
	}

	// hasNext ArticleList
	public List<AticleVo> ArticleList(HasNextPaging vo) {

		return sqlSession.selectList("mapper.article.listArticle2", vo);
	}

	public List<AticleVo> listArticle2(HasNextPaging Vo) {

		return sqlSession.selectList("mapper.article.listArticle2", Vo);
	}

	private String dateFormat(Date curDate) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("HHmm");

		try {
			curDate = dateFormat.parse(dateFormat.format(curDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long curDateTime = curDate.getTime();
		String curDateStr = String.valueOf(curDateTime);

		return curDateStr;
	}
}
