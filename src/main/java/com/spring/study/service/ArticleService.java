package com.spring.study.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.study.common.model.CommonCode;
import com.spring.study.common.model.PageList;
import com.spring.study.dao.ArticleDao;
import com.spring.study.model.article.Article;
import com.spring.study.model.article.ArticleDto;
import com.spring.study.model.article.ArticleParam;
import com.spring.study.model.article.ArticleRankVo;
import com.spring.study.model.article.ArticleReadCountVo;
import com.spring.study.model.article.ArticleVo;
import com.spring.study.model.article.NoticeVo;
import com.spring.study.model.comments.Comment;
import com.spring.study.model.user.User;

import javassist.NotFoundException;

@Service("ArticleService")
public class ArticleService {

	private static final Logger logger = LoggerFactory.getLogger(ArticleService.class);

	@Autowired
	private ArticleDao articleDao;

	public Article getArticle(String articleId, User user) throws RuntimeException {
		boolean isNoticeId = articleDao.isNoticeId(articleId);
		if(isNoticeId && !user.isLogon()) {
			throw new RuntimeException("로그인 후 이용가능합니다");
		}
		
		Article article = articleDao.viewArticle(articleId);
		
		if(article.checkAccessLevel(user)) {
			throw new RuntimeException("접근 권한이 없습니다");
		}
		
		articleDao.addArticleReadCount(articleId);
		
		return article;
	}

	@Transactional(rollbackFor = Exception.class)
	public void writeArticle(ArticleDto articleDto, User user) throws SQLException {
		String articleId = this.getArticleIdNextSeq();
		
		ArticleVo articleVo = new ArticleVo();
		articleVo.setArticleId(articleId);
		articleVo.setWriteMemberId(user.getUserId());
		articleVo.setTitle(articleDto.getTitle());
		articleVo.setContent(articleDto.getContent());
		articleVo.setWriteMemberId(articleDto.getWriteMemberId());

		int result = articleDao.insertArticle(articleVo);
		if(1 != result) { throw new SQLException(" 글 등록중 오류가 발생하였습니다. "); }
		
		if (CommonCode.ARTICLE_TYPE_CD_NOTICE_Y.getCode().equals(articleDto.getArticleTypeCd())) {
			NoticeVo noticeArticleVo = new NoticeVo();
			noticeArticleVo.setArticleId(articleId);
			noticeArticleVo.setDisplayStartDate(articleDto.getDisplayStartDate());
			noticeArticleVo.setDisplayEndDate(articleDto.getDisplayEndDate());
			
			result = articleDao.registerNotice(noticeArticleVo);
			if(1 != result) { throw new SQLException(" 글 등록중 오류가 발생하였습니다. "); }
		}
		
	}

	public void modifyArticle(ArticleDto articleDto, User user) throws NotFoundException, SQLException {
		ArticleVo articleVo = new ArticleVo();
		articleVo.setArticleId(articleDto.getArticleId());
		articleVo.setWriteMemberId(user.getUserId());
		articleVo.setTitle(articleDto.getTitle());
		articleVo.setContent(articleDto.getContent());
		articleVo.setModifyMemberId(articleDto.getModifyMemberId());
		
		boolean isExistsArticle = articleDao.isExistsArticle(articleDto.getArticleId());
		if (!isExistsArticle) {
			new NotFoundException("해당 글이 존재하지 않습니다.");
		}
		
		int result = articleDao.updateArticle(articleVo);
		if(1 != result) {
			throw new SQLException("글 수정 중 오류가 발생하였습니다.");
		}
		
	}

	public void deleteArticle(String articleid) throws Exception {
		boolean isEquals = articleDao.equalsWriterId(articleid);
		if (!isEquals) {
			throw new IllegalAccessException("작성자만 삭제할 수 있습니다.");
		}
		
		int result = articleDao.deleteArticle(articleid);
		if(1 != result) { throw new SQLException(" 오류가 발생하였습니다. "); }
	}
	
	// transaction
	public void writeReply(ArticleDto articleDto) throws NotFoundException, SQLException {
		ArticleVo articleVo = new ArticleVo();
		articleVo.setArticleId(articleDao.getNextArticleId());
		articleVo.setParentId(articleDto.getParentId());
		articleVo.setTitle(articleDto.getTitle());
		articleVo.setContent(articleDto.getContent());
		articleVo.setWriteMemberId(articleDto.getWriteMemberId());
		
		boolean isExistsArticle = articleDao.isExistsArticle(articleDto.getParentId());
		if (!isExistsArticle) {
			throw new NotFoundException("답변하려는 글이 존재하지 않습니다.");
		}

		int result = articleDao.replyArticle(articleVo);
		if (0 == result) {
			throw new SQLException("답글 쓰기중에 오류가 발생하였습니다. 잠시후 다시 시도해주세요");
		}

		isExistsArticle = articleDao.isExistsArticle(articleDto.getParentId());
		if (!isExistsArticle) {
			throw new NotFoundException("답변하려는 글이 존재하지 않습니다.");
		}

	}

	// 글 번호 조회
	public String getArticleIdNextSeq() {
		String result = articleDao.getNextArticleId();
		return result;
	}
	
	public Article getWriterId(String writeMemberId) {
		return articleDao.getWriterId(writeMemberId);
	}

	public int getTotalArticles() {
		return articleDao.getTotalArticles();
	}

	public int getSequence() {
		return articleDao.getSequence();
	}
/**********************************************************************************************************/	
	//EndPaging TotalCount포함에서 가져오는 메서드
	public PageList<Article> getArticlePageListWithCount(ArticleParam req) {
		PageList<Article> feedTypePageList = articleDao.getArticlePageListWithTotalCount(req);
		return feedTypePageList;
	}
	//EndPaging 2페이부터 글리스트만 가져오는 메서드
	public List<Article> getArticleList(ArticleParam req) {
		List<Article> list = articleDao.getMoreListArticle(req);
		return list;
	}
	//hasNextPaing
	public PageList<Article> getArticlePageList(ArticleParam req) {
		PageList<Article> resp = articleDao.getArticlePageListByHasNext(req);
		return resp;
	}	
/**********************************************************************************************************/	
	public PageList<Article> getClipboardList(ArticleParam articleParam) {
		PageList<Article> clipboardList = articleDao.getClipboardListAddCommentsMore(articleParam);
		List<Article> articleList = clipboardList.getList();
		
		String articleIds = articleList.stream()
				.map(article -> article.getRootId())
				.collect(Collectors.joining(","));
		
		List<Article> parentArticleList = articleDao.selectParentArticleList(articleIds);
		
		//TODO 오류 방어 로직 생각해보기
		Map<String, Object> parentArticleMap = new HashMap<>();
		for(Article article : parentArticleList) {
			parentArticleMap.put(article.getArticleId(), article);
		}
		
		for(int i=0; i<articleList.size(); i++) {
			Article article = articleList.get(i);
			String key = article.getArticleId();
			article.setRootArticle((ArticleDto) parentArticleMap.get(key));
		}
		return clipboardList;
	}
	//미구현 
	public PageList<Article> getFeedList(ArticleParam articleParam) {
		return articleDao.getFeedArticleListAddCommentsMore(articleParam);
	}
	
/**********************************************************************************************************/		
	
	//배치 로직
	public void sortArticleRank() {
		List<ArticleRankVo> allArticleList = articleDao.getAllArticleIds();
		List<Comment> commentsCntList = articleDao.getCommentsCntList();
		List<ArticleReadCountVo> readCntList = articleDao.getReadCntList();
		
		Map<String, Integer> commentsCntListMap = commentsCntList.stream()
				.collect(Collectors.toMap(Comment::getArticleId, Comment::getCommentsCnt));
		Map<String, Integer> readCntListMap = readCntList.stream()
				.collect(Collectors.toMap(ArticleReadCountVo::getArticleId, ArticleReadCountVo::getReadCount));
	
		allArticleList.stream()
				.forEach(vo -> {
					final String id = vo.getArticleId();
					
		            int commentCnt = MapUtils.getInteger(commentsCntListMap, id, 0);
		            int readCnt = MapUtils.getInteger(readCntListMap, id, 0);
		  			int recommend = commentCnt * readCnt;
		  			
		  			vo.setCommentCnt(commentCnt);
		  			vo.setReadCnt(readCnt);
		  			vo.setRecommend(recommend);
				});
		articleDao.deleteArticleRank();
		articleDao.insertArticleRank(allArticleList);
	}


	

}
