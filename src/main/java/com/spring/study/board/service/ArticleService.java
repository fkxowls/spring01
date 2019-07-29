package com.spring.study.board.service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.study.board.controller.AticleController;
import com.spring.study.board.dao.ArticleDAO;
import com.spring.study.board.vo.ArticleReplyVo;
import com.spring.study.board.vo.ArticleVo;
import com.spring.study.board.vo.HasNextPaging;
import com.spring.study.board.vo.NoticeArticleVo;
import com.spring.study.board.vo.CommonRequestDto;
import com.spring.study.board.vo.PagingResponseDTO;
import com.spring.study.member.vo.MemberDTO;

import javassist.NotFoundException;
import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;

@Service("ArticleService")
public class ArticleService {

	private static final Logger logger = LoggerFactory.getLogger(AticleController.class);

	@Autowired
	private ArticleDAO articleDAO;

	///
	public ArticleVo viewArticle(String articleId, MemberDTO memberDTO) throws Exception {
		// 공지글 수는 전체글수에 비해 엄청 적으니까 공지글테이블에 있는 글번호인지 확인
		boolean isNoticeId = articleDAO.isNoticeId(articleId);
		ArticleVo returnVo = null;

		if (isNoticeId) {// 공지글 테이블에 글이있을 시
			// 공지글에 있는 것을 확인 후 회원레벨 10(관리자),20(우수회원)이면 접근 30(일반회원)이면 팅겨냄
			int memberLevel = 0;
			try {
				// session에 멤버정보가 없다 = 로그인 x인 상태 = 예외발생시켜서 로그인페이지로 보낸다
				memberLevel = articleDAO.getMemberLevel(memberDTO);
			} catch (Exception e) {
				throw new NotFoundException("로그인 후 이용가능합니다");
			}

			if (20 == memberLevel || 10 == memberLevel) {
				// 글 상세보기
				returnVo = articleDAO.viewArticle(articleId);
			} else {
				// 상위회원만 접근 가능하다고 오류를 던진다
				throw new SQLException("우수회원부터 접근 가능합니다");
			}

		} else {// 공지글 테이블에 글이 없을때 = 그냥 일반 글 일 때
			returnVo = articleDAO.viewArticle(articleId);
		}

		/****************************************
		 * 위 흐름을 쿼리로만 가능한가???????? 예외를 강제하는 건 잘못된게 아닌가??? 사용자 예외를 만들어 예외를 발생시 고려해야할
		 * 부분??-런타임예외?? 체크예외??
		 ****************************************/
		return returnVo;
	}

	// 트랜잭션 적용하기
	public String writeArticle(ArticleVo articleVo) {
		// 새로 작성될 글의 번호는 시퀀스로 통해 가져옴
		String articleId = this.giveArticleId();
		articleVo.setArticleId(articleId);

		// 현재 시각 작성일컬럼에 넣기
		Date curDate = new Date();
		articleVo.setWriteDate(curDate);

		// ARTICLE_TABLE에 먼저 데이터 입력
		// 여기서 예외 처리 해야하나???
		articleDAO.insertArticle(articleVo);

		// ARTICLE_TABLE에 정상 등록되고 ARTICLE_CHK_FLAG가 1이면
		// NOTICE_TABLE 해당 글 정보 입력
		// if - NOTICE_TABLE에 데이터 입력중에 ARTICLE테이블에 등록된 글 정보가 꺠진다면?
		if (1 == articleVo.getNoticeChkFlag()) {
			articleVo.getNoticeArticle().setArticleId(articleId);
			articleDAO.registerNotice(articleVo.getNoticeArticle());
		}

		return articleId;
	}

	public void modifyArticle(ArticleVo articleVo, MemberDTO memberDTO) throws Exception {
		try {
			String userId = memberDTO.getMemberId();
			// 수정자 입력
			articleVo.setModifyMemberId(userId);
		} catch (NullPointerException e) {
			throw new NullPointerException("로그인 세션 만료");
		}

		boolean isExistsArticle = articleDAO.isExistsArticle(articleVo.getArticleId());

		if (!isExistsArticle) {
			new NotFoundException("권한없는 접근입니다");
		}

		// 수정일자 입력
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date();

		articleVo.setModifyDate(curDate);
		System.out.println(articleVo.getModifyDate() + "--====---=-=-=-=-=-===-=-=");
		articleDAO.updateArticle(articleVo);
	}

	public void deleteArticle(ArticleVo vo) throws Exception {
		boolean result = articleDAO.equalsWriterId(vo);

		if (!result) {
			throw new NotFoundException("권한없는 접근입니다");
		}

		articleDAO.deleteArticle(vo);
	}

	// transaction
	public String replyArticle(ArticleVo articleVo) throws Exception {
		// 답글쓰기전 부모글 체크
		boolean isExistsArticle = articleDAO.isExistsArticle(articleVo.getArticleId());
		if (!isExistsArticle) {
			throw new NotFoundException("답변하려는 글이 존재하지 않습니다.");
		}
		// 현재 등록할 글의 글번호를 시퀀스조회를 통해 가져옴
		// 글쓰기 오류가 나면 시퀀스가 원래대로 돌아가야함 근데 안됨(시퀀스는 롤백인 안됨)
		articleVo.setArticleId(this.giveArticleId());
		// 답글 db에 입력
		int result = articleDAO.replyArticle(articleVo);
		if (0 == result) {
			throw new InternalException("서버 오류입니다. 다시 시도해주세요.");
		}
		// 답글 입력후 부모글 체크 (0이면 예외발생)
		isExistsArticle = articleDAO.isExistsArticle(articleVo.getArticleId());
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
		String result = articleDAO.getNextArticleId();

		return result;
	}

	public List<ArticleReplyVo> getComments(String articleId, MemberDTO member) {
		String userId;
		try {
			userId = member.getMemberId();
		} catch (NullPointerException e) {
			userId = "NullPointetException";
		}
		
		List<ArticleReplyVo> commentsList = articleDAO.commentsList(articleId);
		for (int i = 0; i < commentsList.size(); i++) {
			if (1 == commentsList.get(i).getSecretChkFlag() && !userId.contentEquals(commentsList.get(i).getWriteMemberId())) {
				commentsList.get(i).setContent("비밀 댓글 입니다");
			}
		}
		return commentsList;
	}

	public String insertComment(ArticleReplyVo replyVo) throws Exception {
		boolean isExistsArticle = articleDAO.isExistsArticle(replyVo.getArticleId());

		if (!isExistsArticle) {
			throw new NotFoundException("글이 존재하지 않습니다.");
		}

		if (replyVo.getParentNo() != 0) {
			boolean isExistsComment = articleDAO.isExistsComment(replyVo.getReplyId());
			if (!isExistsComment) {
				throw new NotFoundException("댓글이 존재하지 않습니다.");
			}
		}

		int result = articleDAO.insertComment(replyVo);

		return "댓글 작성에 성공 했습니다.";
	}

	public void insertReComment(ArticleReplyVo replyVo) {
		articleDAO.insertComment(replyVo);
	}

	// endPage 관련 일반 서비스
	public PagingResponseDTO<ArticleVo> EndPagination(int page, int pageSize) {
		CommonRequestDto req = new CommonRequestDto.Builder(page, pageSize).build();
		// 메서드 이름 바꾸기(페이지정보와 글리스트를 가져온다)
		// PagingResponseDTO<ArticleVo> resp = articleDAO.ListArticle2(req);
		PagingResponseDTO<ArticleVo> resp = articleDAO.getArticleByTotalCount(req);
		return resp;
	}

	public List<ArticleVo> EndPagingMore(int page, int pageSize) {
		int startNum = (page - 1) * pageSize + 1;
		int endNum = page * pageSize;

		CommonRequestDto req = new CommonRequestDto.Builder(page, pageSize).bothStartNumEndNum(startNum, endNum)
				.build();
		// 메서드 이름 바꾸기(글리스트만가져온다)
		List<ArticleVo> list = articleDAO.ListArticle(req);
		return list;
	}

	// endPage 관련 restAPI 서비스
	public PagingResponseDTO<ArticleVo> EndPaging(int page, int pageSize) {
		CommonRequestDto req = new CommonRequestDto.Builder(page, pageSize).build();
		PagingResponseDTO<ArticleVo> resp = articleDAO.getArticleByTotalCountAddComments(req);

		// List<ArticleVo> list = articleDAO.ListArticle(req);
		// int totalCount = articleDAO.getTotalArticles();

		return resp;
	}

	public PagingResponseDTO<ArticleVo> hasNextPaging(int page, int pageSize) {

		CommonRequestDto req = new CommonRequestDto.Builder(page, pageSize).useMoreView(true).build();
		PagingResponseDTO<ArticleVo> resp = articleDAO.getArticleByHasNext(req);

		return resp;
	}

	public PagingResponseDTO<ArticleVo> hasNextPagingMore(int page, int pageSize) {
		CommonRequestDto req = new CommonRequestDto.Builder(page, pageSize).build();
		PagingResponseDTO<ArticleVo> resp = articleDAO.getArticleByHasNext(req);

		return resp;
	}

	public boolean isEqualsWriterId(ArticleVo articleVo, MemberDTO sessionMemberInfo) {
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
		return articleDAO.getTotalArticles();
	}

	public int getSequence() {
		return articleDAO.getSequence();
	}

	public List<ArticleVo> getNoticeList() {
		return articleDAO.getNoticeList();

	}

	public List<ArticleVo> getMyArticleList(MemberDTO member) {
		String userId = "";

		try {
			userId = member.getMemberId();
		} catch (NullPointerException e) {
			throw new NullPointerException("로그인 세션 만료");
		}

		List<ArticleVo> myArticleList = articleDAO.getMyArticleList(userId);
		return myArticleList;
	}

	// Session에 있는 아이디와 레벨 가져오는 코드 메서드로 뺌
	public MemberDTO checkSessionMemberValue(MemberDTO member) {
		String userId = "";
		int userLevel = 0;

		try {
			userId = member.getMemberId();
		} catch (NullPointerException e) {
			throw new NullPointerException("로그인 세션 만료");
		}

		try {
			userLevel = member.getMemberLevel();
		} catch (NullPointerException e) {
			throw new NullPointerException("로그인 세션 만료");
		}
		return null;
	}

}
