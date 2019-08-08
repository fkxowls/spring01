package com.spring.study.board.service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.study.board.controller.AticleController;
import com.spring.study.board.dao.ArticleDao;
import com.spring.study.board.dao.CommentDao;
import com.spring.study.board.model.ArticleVo;
import com.spring.study.board.model.CommonRequestDto;
import com.spring.study.comment.model.CommentPageList;
import com.spring.study.comment.model.CommentsRequestDto;
import com.spring.study.comment.model.CommentsVo;
import com.spring.study.common.model.CommonCode;
import com.spring.study.common.model.PageList;
import com.spring.study.member.model.Member;

import javassist.NotFoundException;
import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;

@Service("ArticleService")
public class ArticleService {

	private static final Logger logger = LoggerFactory.getLogger(AticleController.class);

	private FastDateFormat fdf = FastDateFormat.getInstance("yyyy-MM-dd");
	private SimpleDateFormat sdf = new SimpleDateFormat();

	@Autowired
	private ArticleDao articleDao;
	@Autowired
	private CommentDao commentDao;

	///
	public ArticleVo getArticleContents(String articleId, Member member) throws Exception {
		// 공지글 수는 전체글수에 비해 엄청 적으니까 공지글테이블에 있는 글번호인지 확인
		boolean isNoticeId = articleDao.isNoticeId(articleId);
		ArticleVo returnVo = null;

		if (isNoticeId) {// 공지글 테이블에 글이있을 시
			//이것도 체크해야함
			String userLevel = member.getMemberLevel();

			// session에 멤버정보가 없다 = 로그인 x인 상태 = 예외발생시켜서 로그인페이지로 보낸다
			if (null == member) {
				throw new NotFoundException("로그인 후 이용가능합니다");
			}
			//userLevel = articleDao.getMemberLevel(member);
			/*
			 * if (0 == memberLevel) { throw new NotFoundException("로그인 후 이용가능합니다"); }
			 */
			if (CommonCode.USER_LEVEL_CD_BRONZE.getCode().equals(userLevel)) {
			//if (20 == userLevel || 10 == userLevel) {
				throw new SQLException("우수회원부터 접근 가능합니다");
			} else {
				returnVo = articleDao.viewArticle(articleId);
			}

		} else {// 공지글 테이블에 글이 없을때 = 그냥 일반 글 일 때
			returnVo = articleDao.viewArticle(articleId);
		}

		/****************************************
		 * 위 흐름을 쿼리로만 가능한가???????? 예외를 강제하는 건 잘못된게 아닌가??? 사용자 예외를 만들어 예외를 발생시 고려해야할
		 * 부분??-런타임예외?? 체크예외??
		 ****************************************/
		return returnVo;
	}

	// 트랜잭션 적용하기 //여기서 일어날 오류??
	public String writeArticle(ArticleVo articleVo) {
		String articleId = this.giveArticleId();
		articleVo.setArticleId(articleId);

		Date curDate = new Date();
		articleVo.setWriteDate(curDate);

		articleDao.insertArticle(articleVo);

		// if - NOTICE_TABLE에 데이터 입력중에 ARTICLE테이블에 등록된 글 정보가 꺠진다면?
		if (true == articleVo.isNotice()) {
			articleVo.getNoticeArticle().setArticleId(articleId);
			articleDao.registerNotice(articleVo.getNoticeArticle());
		}

		return articleId;
	}

	public void modifyArticle(ArticleVo articleVo, Member memberDTO) throws Exception {

		try {
			String userId = memberDTO.getMemberId();
			// 수정자 입력
			articleVo.setModifyMemberId(userId);
			// 널포인트 체크는 try문 쓰지말고 if로 처리
		} catch (NullPointerException e) {
			throw new NullPointerException("로그인 세션 만료");
		}

		boolean isExistsArticle = articleDao.isExistsArticle(articleVo.getArticleId());

		if (!isExistsArticle) {
			new NotFoundException("권한없는 접근입니다");
		}

		// 수정일자 입력
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date();

		articleVo.setModifyDate(curDate);
		System.out.println(articleVo.getModifyDate() + "--====---=-=-=-=-=-===-=-=");
		articleDao.updateArticle(articleVo);
	}

	public void deleteArticle(ArticleVo vo) throws Exception {
		boolean result = articleDao.equalsWriterId(vo);

		if (!result) {
			throw new NotFoundException("권한없는 접근입니다");
		}

		articleDao.deleteArticle(vo);
	}

	// transaction
	public String writeReply(ArticleVo articleVo) throws Exception {
		// 답글쓰기전 부모글 체크
		boolean isExistsArticle = articleDao.isExistsArticle(articleVo.getArticleId());
		if (!isExistsArticle) {
			throw new NotFoundException("답변하려는 글이 존재하지 않습니다.");
		}

		articleVo.setArticleId(this.giveArticleId());

		int result = articleDao.replyArticle(articleVo);
		if (0 == result) {
			throw new InternalException("서버 오류입니다. 다시 시도해주세요.");
		}

		isExistsArticle = articleDao.isExistsArticle(articleVo.getArticleId());
		if (!isExistsArticle) {
			throw new NotFoundException("답변하려는 글이 존재하지 않습니다.");
		}

		return "답글 작성에 성공 했습니다.";
	}

	// 글 번호 조회
	/****************************
	 * 답글말고 일반 글쓰기에서 현재 시퀀스 조회후 + 1 을 하는건 좋지 않은 방법인가??
	 ****************************/
	public String giveArticleId() {
		String result = articleDao.getNextArticleId();
		return result;
	}

	public CommentPageList getComments(CommentsRequestDto req) {
		CommentPageList commentPageList = this.getComments(req, null);
		return commentPageList;
	}
	//commentPage정보 받아와야한다
	public CommentPageList getComments(CommentsRequestDto req, String userId) {
		//final String userId = user;
		String articleWriterId = req.getWriteMemberId();
		
		CommentPageList commentPageList = commentDao.commentsList(req);
		List<CommentsVo> commentsList = commentPageList.getCommentsList();
		
		commentsList.stream()
				.filter(comment -> CommonCode.SECRET_TYPE_CD_SECRET_Y.getCode().equals(comment.getSecretTypeCd()))
				.filter(comment -> !StringUtils.equals(userId, articleWriterId)) 
				.filter(comment -> !StringUtils.equals(userId, comment.getWriteMemberId()))
				.forEach(comment -> comment.setContent("비밀 댓글 입니다"));

		return commentPageList;
	}
	
	public CommentPageList getCommentsPageList(String articleId, int commentsPage, String userId) {
		//final String userId = user.getMemberId();
		String articleWriterId = commentDao.getWriterOfArticle(articleId);
		
		CommentsRequestDto commentsRequestDto = new CommentsRequestDto(articleId, commentsPage, userId);
		commentsRequestDto.setArticleId(articleId);
		commentsRequestDto.setCommentsPage(commentsPage);

		
		CommentPageList commentPageList = commentDao.commentsList(commentsRequestDto);
		List<CommentsVo> commentsList = commentPageList.getCommentsList();
		
		commentsList.stream()
				.filter(comment -> CommonCode.SECRET_TYPE_CD_SECRET_Y.getCode().equals(comment.getSecretTypeCd()))
				.filter(comment -> !StringUtils.equals(userId, articleWriterId)) 
				.filter(comment -> !StringUtils.equals(userId, comment.getWriteMemberId()))
				.forEach(comment -> comment.setContent("비밀 댓글 입니다"));

		return commentPageList;
	}

	public String writeComment(CommentsRequestDto commentsRequestDto, Member member) throws Exception {
		
		boolean isExistsArticle = articleDao.isExistsArticle(commentsRequestDto.getArticleId());
		
		if (!isExistsArticle) {
			throw new NotFoundException("글이 존재하지 않습니다.");
		}

		if (commentsRequestDto.getParentId() != 0) {
			boolean isExistsComment = commentDao.isExistsComment(commentsRequestDto.getReplyId());
			if (!isExistsComment) {
				throw new NotFoundException("댓글이 존재하지 않습니다.");
			}
		}

		commentDao.writeComment(commentsRequestDto, member);

		return "댓글 작성에 성공 했습니다.";
	}

	/*
	 * public void insertReComment(CommentsVo replyVo) {
	 * commentDAO.writeComment(replyVo); }
	 */

	/****************************************************************************************************
	 ****************************************************************************************************
	 ****************************************************************************************************/
	public PageList<ArticleVo> getArticlePageListWithCount(CommonRequestDto req) {
		PageList<ArticleVo> pageList = articleDao.getArticlePageListWithCount(req);
		return pageList;
	}

	public List<ArticleVo> getArticleList(CommonRequestDto req) {
//		List<ArticleVo> list = articleDao.ListArticle(req);
		List<ArticleVo> list = articleDao.ListArticleTest(req);
		return list;
	}

	public PageList<ArticleVo> getArticlePageList(CommonRequestDto req) {
		PageList<ArticleVo> resp = articleDao.getArticlePageList(req);
		return resp;
	}

	/****************************************************************************************************
	 ****************************************************************************************************
	 ****************************************************************************************************/
	/*
	 * public PageList<ArticleVo> hasNextPagingMore(int page, int pageSize) {
	 * CommonRequestDto req = new CommonRequestDto.Builder(page, pageSize).build();
	 * PageList<ArticleVo> resp = articleDAO.getArticlePageList(req);
	 * 
	 * return resp; }
	 */

	// endPage 관련 restAPI 서비스 (FeedType)
	public PageList<ArticleVo> endPaginationByTotalCount(int page, int pageSize) {
		CommonRequestDto req = new CommonRequestDto.Builder(page, pageSize).build();
		PageList<ArticleVo> resp = articleDao.getArticlePageListWithCountAddComments(req);

		// List<ArticleVo> list = articleDAO.ListArticle(req);
		// int totalCount = articleDAO.getTotalArticles();

		return resp;
	}

	public boolean isEqualsWriterId(ArticleVo articleVo, Member sessionMemberInfo) {
		String userId = "";
		try {
			userId = sessionMemberInfo.getMemberId();
		} catch (NullPointerException e) {
			throw new NullPointerException("해당 작성자만 접근 가능합니다(로그인 해주세요)");
		}

		if (!articleVo.getwriteMemberId().equals(userId)) {

			return false;
		}

		return true;
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
