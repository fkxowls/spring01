package com.spring.study.board.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.study.board.controller.AticleController;
import com.spring.study.board.dao.ArticleDAO;
import com.spring.study.board.vo.ArticleReplyVo;
import com.spring.study.board.vo.AticleVo;
import com.spring.study.board.vo.HasNextPaging;
import com.spring.study.board.vo.PageDto;
import com.spring.study.board.vo.PagingResponseDTO;

import javassist.NotFoundException;
import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;

@Service("ArticleService")
public class ArticleService {

	private static final Logger logger = LoggerFactory.getLogger(AticleController.class);

	@Autowired
	private ArticleDAO articleDAO;

	public AticleVo viewArticle(String num) {
		return articleDAO.viewArticle(num);

	}

	public void writeArticle(AticleVo articleVo) {

		articleDAO.insertArticle(articleVo);

	}

	public void modifyArticle(AticleVo articleVo) throws Exception {
		boolean isExistsArticle = articleDAO.isExistsArticle(articleVo.getArticleNo());
		if (isExistsArticle) {	throw new Exception(); }
		articleDAO.updateArticle(articleVo);
	}

	public int getTotalArticles() {
		return articleDAO.getTotalArticles();
	}

	public int getSequence() {
		return articleDAO.getSequence();
	}

	public void deleteArticle(String num) throws Exception {
		articleDAO.deleteArticle(num);
	}

	// transaction1
	public String replyArticle(AticleVo articleVo) throws Exception {

		// 답글쓰기전 부모글 체크
		boolean isExistsArticle = articleDAO.isExistsArticle(articleVo.getArticleNo());
		if(isExistsArticle) { throw new NotFoundException("답변하려는 글이 존재하지 않습니다."); }
		// 현재 등록할 글의 글번호를 시퀀스조회를 통해 가져옴
		// 글쓰기 오류가 나면 시퀀스가 원래대로 돌아가야함 근데 안됨
		articleVo.setArticleNo(this.giveArticleNo());
		// 답글 db에 입력
		int result = articleDAO.replyArticle(articleVo);
		if(0 == result) { throw new InternalException("서버 오류입니다. 다시 시도해주세요."); }
		// 답글 입력후 부모글 체크 (0이면 예외발생)
		isExistsArticle = articleDAO.isExistsArticle(articleVo.getArticleNo());
		if(isExistsArticle) { throw new NotFoundException("답변하려는 글이 존재하지 않습니다."); }
		
		return "답글 작성에 성공 했습니다.";
	}

	public String giveArticleNo() {
		String result = articleDAO.getArticleNo();
		return result;
	}

	public List<ArticleReplyVo> listComment(String articleNo) {
		return articleDAO.listComment(articleNo);
	}

	public int insertComment(ArticleReplyVo replyVo) {
		logger.info("=================== 		Service insertComment:{}");
		return articleDAO.insertComment(replyVo);

	}

	public void insertReComment(ArticleReplyVo replyVo) {
		articleDAO.insertComment(replyVo);

	}

	
	//endPage 관련 일반 서비스
	public PagingResponseDTO<AticleVo> EndPagination(int page, int pageSize) {
		PageDto req = new PageDto.Builder(page, pageSize).build();
		//											  메서드 이름 바꾸기(페이지정보와 글리스트를 가져온다)
		//PagingResponseDTO<AticleVo> resp = articleDAO.ListArticle2(req);
		PagingResponseDTO<AticleVo> resp = articleDAO.getArticleByTotalCount(req);
		return resp;
	}
	public List<AticleVo> EndPagingMore(int page, int pageSize) {
		/***************************************
		 DTO를 request와 response로 나누고, setter를 제거하고
		 BaseDAO에서 분기처리를 하려니 상황에따라 endNum를 조작할 수 없어
		 dto객체를 재생성 후 startNum과 endNum를 추가적으로 넣어줌
		 (setter제거 이유 = 불변객체로 유지하고싶어서)  
		 BaseDAO에서도 아래의 로직이 반복됨
		 묶어서 처리를 어떻게 해야할까
		 ***************************************/
		int startNum = (page - 1) * pageSize + 1;
		int endNum = page * pageSize;
		PageDto req = new PageDto.Builder(page, pageSize).startNum(startNum).endNum(endNum).build();
		//				메서드 이름 바꾸기(글리스트만가져온다)
		List<AticleVo> list =  articleDAO.ListArticle(req);
		return list; 
	}

	
	//endPage 관련 restAPI 서비스
	public PagingResponseDTO<AticleVo> EndPaging(int page, int pageSize) {
		PageDto.Builder builder = new PageDto.Builder(page, pageSize);

		//PageDto<AticleVo> req = new PageDto.Builder(page, pageSize).build();
		//int startNum = (page - 1) * pageSize + 1;
		//int endNum = page * pageSize;
		PageDto req = new PageDto.Builder(page, pageSize).build();
		PagingResponseDTO<AticleVo> resp = articleDAO.getArticleByTotalCount(req);
		
		//List<AticleVo> list = articleDAO.ListArticle(req);
		//int totalCount = articleDAO.getTotalArticles();
		
		return resp;
	}

	public PagingResponseDTO<AticleVo> hasNextPaging(int page, int pageSize) {

		PageDto<AticleVo> req = new PageDto.Builder(page, pageSize).build();
		PagingResponseDTO<AticleVo> resp =  articleDAO.getArticleByHasNext(req);

		return resp;
	}

	public PagingResponseDTO<AticleVo> hasNextPagingMore(int page, int pageSize) {
		
		PageDto<AticleVo> req = new PageDto.Builder(page, pageSize).build();
		PagingResponseDTO<AticleVo> resp =  articleDAO.getArticleByHasNext(req);

		return resp;
	}
	/*
	 public PagingResponseDTO<AticleVo> hasNextPagingMore(int page, int pageSize) {

		int startNum = (page - 1) * pageSize + 1;
		int endNum = page * pageSize + 1;
		
		PageDto req = new PageDto.Builder(page, pageSize).startNum(startNum).endNum(endNum).build();
		List<AticleVo> list = articleDAO.ListArticle(req);

		int totalCount = 0;

		return new PagingResponseDTO<AticleVo>(page, pageSize, totalCount, list);
	}
	 */



	public List<AticleVo> listArticle2(HasNextPaging vo) {
		return articleDAO.ArticleList(vo);
	}

	public boolean isNext(List<AticleVo> list) {
		if (list.size() != 11) {
			return false;
		} else
			return true;

	}

}
