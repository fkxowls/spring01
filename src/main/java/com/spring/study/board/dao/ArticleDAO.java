package com.spring.study.board.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.spring.study.board.controller.AticleController;
import com.spring.study.board.vo.ArticleReplyVo;
import com.spring.study.board.vo.ArticleVo;
import com.spring.study.board.vo.CommonRequestDto;
import com.spring.study.board.vo.HasNextPaging;
import com.spring.study.board.vo.PagingResponseDTO;
import com.spring.study.common.aop.AddComments;

@Repository("articleDAO")
public class ArticleDAO extends BaseDAO {
	private static final Logger logger = LoggerFactory.getLogger(AticleController.class);

	@Autowired
	SqlSession sqlSession;

	/********************************************************************
	 * 기존의 코드 public PagingResponseDTO<ArticleVo> ListArticle2(PageDto vo) { return
	 * super.selectPageDto("mapper.article.listArticle2",
	 * "mapper.article.totalArticle", vo); } endPage와 hsaNext 페이징을 BaseDAO로 분리 후
	 * totalCount여부로 분기하기때문에 totalCount를 제외하고 코드가 공통됨 => endPage용 DAO와 hasNext용 다오의
	 * 차이점은 totalCount를 가져오나 안오냐의 차이
	 ***********************************************************************/

	@AddComments // TODO PageDto인지 List인지 단일VO인지 체크해서 -> 각 VO에 reply 필드에 reply
	public PagingResponseDTO<ArticleVo> getArticleByTotalCountAddComments(Object vo) {
		return super.selectPageDto("mapper.article.listArticle2", "mapper.article.totalArticle", vo);
	}
	
//	@AddComments
	public PagingResponseDTO<ArticleVo> getArticleByTotalCount(Object vo) {
		return super.selectPageDto("mapper.article.listArticle2", "mapper.article.totalArticle", vo);
	}

	public PagingResponseDTO<ArticleVo> getArticleByHasNext(CommonRequestDto vo) {
		return super.selectPageDto("mapper.article.listArticle2", vo);
	}

	public boolean isExistsArticle(String articleNo) {
		String result = sqlSession.selectOne("mapper.article.isArticleNo", articleNo);
		if (result.equals("Y")) {
			return true;
		}
		return false;

	}

	// 순수 게시글 리스트만 가져오는 DAO 페이징정보 DAO는 getArticleByTotalCount/getArticleByHasNext
	public List<ArticleVo> ListArticle(CommonRequestDto vo) {

		return sqlSession.selectList("mapper.article.listArticle2", vo);
	}

	public ArticleVo viewArticle(String aritcleNo) {
		return sqlSession.selectOne("mapper.article.viewArticle", aritcleNo);

	}

	public void insertArticle(ArticleVo articleVo) {
		System.out.println("=======================================				articleVo:");
		System.out.println("=======================================			             : " + articleVo.getContent());
		sqlSession.insert("mapper.article.insertArticle", articleVo);

	}

	public void updateArticle(ArticleVo articleVo) {
		sqlSession.update("mapper.article.updateArticle", articleVo);

	}

	public int getTotalArticles() {

		return sqlSession.selectOne("mapper.article.totalArticle");
	}

	public int getSequence() {

		return sqlSession.selectOne("mapper.article.getSequence");
	}

	public void deleteArticle(ArticleVo vo) {
		sqlSession.delete("mapper.article.deleteArticle", vo);

	}

	public int replyArticle(ArticleVo articleVo) {
		return sqlSession.insert("mapper.article.insertReply", articleVo);
	}

	public List<ArticleReplyVo> commentsList(String articleNos) {
		return sqlSession.selectList("mapper.comment.listComment", articleNos);
	}

	public boolean equalsWriterId(ArticleVo vo) {
		String result = sqlSession.selectOne("mapper.article.equalsWriterId", vo);
		if (result.equals("Y")) {
			return true;
		}
		return false;
	}

	public int insertComment(ArticleReplyVo replyVo) {
		logger.info("=================== 		DAO insertComment:{}");
		logger.info("parentNo: {}", replyVo.getParentNo());
		return sqlSession.insert("mapper.comment.insertComment", replyVo);

	}

	// hasNext ArticleList
	public List<ArticleVo> ArticleList(HasNextPaging vo) {

		return sqlSession.selectList("mapper.article.listArticle2", vo);
	}

	public List<ArticleVo> listArticle2(HasNextPaging Vo) {

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

	public String getArticleNo() {

		return sqlSession.selectOne("mapper.article.articleNo");
	}

	public Map<String, List<ArticleReplyVo>> temporaryMethod() {

		return null;
	}

//	public List<ArticleVo> ListArticle(PageDto vo) {
//	logger.info("=========            startNum:{}", vo.getStartNum());

//	HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//	HttpServletResponse resp = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
//			.getResponse();
//	HttpSession session = req.getSession();
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

	// TODO DTO캐싱 로직 구현 - 쓰기 set
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
//	return list;
//}

}
