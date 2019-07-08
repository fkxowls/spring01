package com.spring.study.board.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.spring.study.board.controller.AticleController;
import com.spring.study.board.vo.ArticleReplyVo;
import com.spring.study.board.vo.AticleVo;
import com.spring.study.board.vo.HasNextPaging;
import com.spring.study.board.vo.PageDto;

@Repository("articleDAO")
public class ArticleDAO extends BaseDAO {
	private static final Logger logger = LoggerFactory.getLogger(AticleController.class);

	@Autowired
	SqlSession sqlSession;
	
	public PageDto<AticleVo> ListArticle2(PageDto vo) {
		return super.selectPageDto("mapper.article.listArticle2", "mapper.article.totalArticle", vo);
	}
	
//	@AddReply // TODO PageDto인지 List인지 단일VO인지 체크해서 -> 각 VO에 reply 필드에 reply 넣어주도록 AOP 작업
	public PageDto<AticleVo> getArticleByTotalCount(PageDto vo) {
		return super.selectPageDto("mapper.article.listArticle2", "mapper.article.totalArticle", vo);
	}
	
	public PageDto<AticleVo> getArticleByHasNext(PageDto vo) {
		return super.selectPageDto("mapper.article.listArticle2", vo);
	}
	
	public boolean isExistsArticle(Integer num) {
		Integer result = sqlSession.selectOne("mapper.article.isArticleNo",num);
		if(1 == result) { return true; }
		return false;
		
	}
	
	public List<AticleVo> ListArticle(PageDto vo) {
		logger.info("=========            startNum:{}", vo.getStartNum());
		
		return sqlSession.selectList("mapper.article.listArticle2", vo);
		
	}

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
		return sqlSession.insert("mapper.article.insertReply", articleVo);
	}

	public List<ArticleReplyVo> listComment(int articleNo) {
		List<ArticleReplyVo> list;

		list = sqlSession.selectList("mapper.comment.listComment", articleNo);

		return list;
	}

	public int insertComment(ArticleReplyVo replyVo) {
		logger.info("=================== 		DAO insertComment:{}");
		logger.info("parentNo: {}", replyVo.getParentNo());
		return sqlSession.insert("mapper.comment.insertComment", replyVo);

	}

//	public List<AticleVo> ListArticle(PageDto vo) {
//		logger.info("=========            startNum:{}", vo.getStartNum());

//		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//		HttpServletResponse resp = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
//				.getResponse();
//		HttpSession session = req.getSession();
//
//		List<AticleVo> list;
//		Cookie cookie = null;
//		Cookie[] cookieArr = req.getCookies();
//
//		Date date = new Date();
//		String curDateTime = dateFormat(date);
//
//		long timeSpent = -1;
//
//		System.out.println("쿠키목록확인");
//		// TODO DTO캐싱 로직 구현 - 읽기 get
//		if (cookieArr != null) {
//			for (int i = 0; i < cookieArr.length; i++) {
//				if (cookieArr[i].getName().equals("issueDate")) {
//					cookie = cookieArr[i];
//					String cookieDate = cookie.getValue();
//					long pastDate = Long.parseLong(cookieDate);
//					long curDate = Long.parseLong(curDateTime);
//					timeSpent = (curDate - pastDate) / 60000;
//					break;
//				} else {
//					cookie = new Cookie("issueDate", curDateTime);
//					cookie.setComment("DTO쿠키 저장 시간");
//					cookie.setMaxAge(60 * 10);
//					resp.addCookie(cookie);
//				}
//			}
//		} else {
//			cookie = new Cookie("issueDate", curDateTime);
//			cookie.setComment("DTO쿠키 저장 시간");
//			cookie.setMaxAge(60 * 10);
//			resp.addCookie(cookie);
//		}

		// TODO DTO캐싱 로직 구현 - 쓰기 set
//		if (timeSpent > 10) {
//			list = sqlSession.selectList("mapper.article.listArticle2", vo);
//			session.setAttribute("sessionArticleList", list);
//			cookie = new Cookie("issueDate", curDateTime);
//			cookie.setMaxAge(60 * 10);
//			resp.addCookie(cookie);
//		} else if (timeSpent == -1) {
//		list = sqlSession.selectList("mapper.article.listArticle2", vo);
//			session.setAttribute("sessionArticleList", list);
//		} else {
//			list = (List<AticleVo>) session.getAttribute("sessionArticleList");
//		}
//		return list;
//	}

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

	public int getArticleNo() {
		
		return sqlSession.selectOne("mapper.article.articleNo");
	}

	
}
