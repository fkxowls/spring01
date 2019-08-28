package com.spring.study.controller;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spring.study.common.model.BaseParam;
import com.spring.study.common.model.PageList;
import com.spring.study.model.article.Article;
import com.spring.study.model.article.ArticleDto;
import com.spring.study.model.article.ArticleParam;
import com.spring.study.model.comments.Comment;
import com.spring.study.model.comments.CommentDto;
import com.spring.study.model.comments.CommentParam;
import com.spring.study.model.comments.CommentVo;
import com.spring.study.model.user.User;
import com.spring.study.service.ArticleService;
import com.spring.study.service.CommentsService;

import defalut.ArticleController;
import javassist.NotFoundException;
import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;

//     controller코드 정리 ㅇ  addCommentAspect수정 ㅇ
// 	   articleParam 수정 ㅇ , union ㅇ, 조회수 증가시키기 ㅇ  
//	     캐싱 aop로 분리, 캐싱 리턴값 직렬화
//	   Dao정리
//     interceptor적용할 코드가 있는지 다시보기
//     람다?? 물어보기
@Controller
public class NewArticleController {
	private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);
	private static final int pageSize = 10;
	private static final int commentPageSize = 10;
	@Autowired
	private ArticleService articleService;
	@Autowired
	private CommentsService commentsService;
	
	@RequestMapping(value = "/board/article/{page}/list")
	public @ResponseBody Map<String, Object> getArticleList(@PathVariable int page, Model model,@RequestParam(required = false, defaultValue = "old") String sort, User user) {																					
		ArticleParam reqParam = new ArticleParam.Builder(page, pageSize).sort(sort).build();
		Map<String, Object> resultMap = new HashMap<>();
		List<Article> articleList = null;		
		
		if (1 == page) {
			PageList<Article> pageList = articleService.getArticlePageListWithCount(reqParam);
			articleList = pageList.getList();
			resultMap.put("totalPage", pageList.getTotalPage());
		} else {
			articleList = articleService.getArticleList(reqParam);
		}
		
		List<ArticleDto> articleHeader = articleList.stream()
		.map(Article::displayTitle)
		.collect(Collectors.toList());
		//결과상태
		resultMap.put("code", HttpStatus.OK.value());
		resultMap.put("msg", HttpStatus.OK.getReasonPhrase());
		resultMap.put("articleHeader", articleHeader);
		
		return resultMap;
	}
	
	@RequestMapping(value = "/board/article2/{page}/list")
	public @ResponseBody Map<String, Object> getArticleList(Model model, @PathVariable int page,@RequestParam(required = false, defaultValue = "old") String sort) {
		BaseParam reqParam = new BaseParam.Builder(page, pageSize).useMoreView(true).build();
		Map<String, Object> resultMap = new HashMap<>();
		
		PageList<Article> pageListDto = articleService.getArticlePageList(reqParam);
		List<Article> articleList = pageListDto.getList();
		List<ArticleDto> articleHeader = articleList.stream()
										.map(Article::displayTitle)
										.collect(Collectors.toList());
		
		resultMap.put("code", HttpStatus.OK.value());
		resultMap.put("msg", HttpStatus.OK.getReasonPhrase());
		resultMap.put("articleList", articleHeader);
		resultMap.put("hasNext", pageListDto.getHasNext());

		return resultMap;
	}
	
	@RequestMapping(value = "/board/{articleId}/detail", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> viewArticle(@PathVariable String articleId, User user) {
		Map<String, Object> resultMap = new HashMap<>();
		//글상세보기안에서 댓글 처리?? 
		try {
			Article article = articleService.getArticle(articleId, user);
			resultMap.put("code", HttpStatus.OK.value());
			resultMap.put("msg", HttpStatus.OK.getReasonPhrase());
			resultMap.put("article", article.showArticle());
		} catch (RuntimeException e) {
			resultMap.put("code", HttpStatus.FORBIDDEN.value());
			resultMap.put("msg", e.getMessage());
			resultMap.put("article", Collections.EMPTY_MAP);
		} catch (Exception e) {
			resultMap.put("code", HttpStatus.SERVICE_UNAVAILABLE.value());
			resultMap.put("msg","알 수 없는 오류 발생");
			resultMap.put("article", Collections.EMPTY_MAP);
			e.printStackTrace();
		}
				
		return resultMap;
	}
	
	@RequestMapping(value = "/board/article", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> writeArticle(@RequestBody ArticleDto articleDto, HttpServletRequest req, User user){
		Map<String, Object> resultMap = new HashMap<>();
		//XXX 로그인세션이 만료되어 현재 접속자가 Null이 될 가능성을 생각하는것도 하지않는게 맞는건가요?
		try {
			req.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		try {
			articleService.writeArticle(articleDto, user);
			resultMap.put("code", HttpStatus.OK.value());
			resultMap.put("msg", HttpStatus.OK.getReasonPhrase());
		} catch (SQLException e) {
			resultMap.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
			resultMap.put("msg",e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			resultMap.put("code", HttpStatus.SERVICE_UNAVAILABLE.value());
			resultMap.put("msg","알 수 없는 오류 발생");
			e.printStackTrace();
		}
		return resultMap;
	}
	
	@RequestMapping(value = "/board/{articleId}/reply", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> writeReply(@RequestBody ArticleDto articleDto, User user) {
		Map<String, Object> resultMap = new HashMap<>();
		
		if(Article.checkId(articleDto.getArticleId())) { 
			resultMap.put("code", HttpStatus.BAD_REQUEST.value());
			resultMap.put("msg", "입력 값이 올바르지 않습니다. 다시 확인 해주세요.");
			return resultMap;
		}	
		
		try {
			articleService.writeReply(articleDto);
			resultMap.put("code",HttpStatus.OK);
			resultMap.put("msg", HttpStatus.OK.getReasonPhrase());
		} catch (NotFoundException e) {
			resultMap.put("code", HttpStatus.NOT_FOUND.value());
			resultMap.put("msg", e.getMessage());
		} catch (SQLException e) {
			resultMap.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
			resultMap.put("msg", e.getMessage());
		} catch (Exception e) {
			resultMap.put("code", HttpStatus.SERVICE_UNAVAILABLE.value());
			resultMap.put("msg", "알 수 없는 오류가 발생 했습니다. 다시 시도 해주세요");
			e.printStackTrace();
		}

		return resultMap;
	}
	
	@RequestMapping(value = "/board/{articleId}", method = RequestMethod.PUT)
	public @ResponseBody Map<String, Object> modifyArticle(@RequestBody ArticleDto articleDto, User user) {
		Map<String, Object> resultMap = new HashMap<>();
		
//		if(!user.isLogon()) {
//			resultState.put("msg", "로그인 세션 만료");
//			resultState.put("code", HttpStatus.FORBIDDEN);
//			return resultState;
//		}
		
		if(!Article.checkId(articleDto.getArticleId())) { 
			resultMap.put("code", HttpStatus.BAD_REQUEST.getReasonPhrase());
			resultMap.put("msg", "잘못된 요청입니다.");
			return resultMap;
		}
		//XXX 요청시 들어온 uesr의 null여부 equals여부는 판단해도 되는건가요??
		Article article = articleService.getWriterId(articleDto.getArticleId());
		if(!article.checkUserId(user.getUserId())) {
			resultMap.put("code", HttpStatus.FORBIDDEN.getReasonPhrase());
			resultMap.put("msg", "작성자만 수정 가능합니다.");
			return resultMap;
		}
		
		try {
			articleService.modifyArticle(articleDto, user);
			resultMap.put("code",HttpStatus.OK.value());
			resultMap.put("msg",HttpStatus.OK.getReasonPhrase());
		} catch (NotFoundException e) {
			resultMap.put("msg", e.getMessage());
			resultMap.put("code", HttpStatus.NOT_FOUND.value());
			e.printStackTrace();
		} catch (Exception e) {
			resultMap.put("msg", e.getMessage());
			resultMap.put("code", HttpStatus.SERVICE_UNAVAILABLE.value());
			e.printStackTrace();
		}
		
		return resultMap;  
	}
	
	@RequestMapping(value = "/board/{articleId}", method = RequestMethod.DELETE)
	public @ResponseBody Map<String, Object> deleteArticle(@PathVariable String articleId, User user) {
		Map<String, Object> resultMap = new HashMap<>();
		
		try {
			articleService.deleteArticle(articleId);
		} catch (DataIntegrityViolationException e) {
			resultMap.put("code", HttpStatus.CONFLICT);
			resultMap.put("msg", "댓글이 달린 글은 삭제할 수 없습니다.");
		} catch (NotFoundException e) {
			resultMap.put("code", HttpStatus.NOT_FOUND.value());
			resultMap.put("msg", e.getMessage());
		} catch (Exception e) {
			resultMap.put("code", HttpStatus.SERVICE_UNAVAILABLE.value());
			resultMap.put("msg", e.getMessage());
			e.printStackTrace();
		}
		
		return resultMap;
	}
	
	@RequestMapping(value = "/board/{articleId}/comments/{page}/list")
	public @ResponseBody Map<String, Object> getCommentList(@PathVariable String articleId, @PathVariable int page, User user) {
		Map<String, Object> resultMap = new HashMap<>();
		CommentParam commentsParam = new CommentParam.Builder(page, commentPageSize,articleId)
										 			 .userId(user.getUserId()).build();
		PageList<Comment> commentsPageList = commentsService.getCommentsPageList(commentsParam);
		List<CommentDto> commentList = commentsPageList.getList()
													   .stream()
													   .map(Comment::showComment)
													   .collect(Collectors.toList());
		//TODO 결과상태
		resultMap.put("commentList", commentList);
		resultMap.put("totalPage", commentsPageList.getTotalPage());
		
		
		return resultMap;
	}
	
	@RequestMapping(value = "/board/{articleId}/comments", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> writeComment(@RequestBody CommentDto commentsDto, User user) {
		Map<String, Object> resultMap = new HashMap<>();
		
		try {
			commentsService.writeComment(commentsDto, user);
			resultMap.put("code", HttpStatus.OK.value());
			resultMap.put("msg", HttpStatus.OK.getReasonPhrase());
		} catch (NotFoundException e) {
			resultMap.put("msg", e.getMessage());
			resultMap.put("code", HttpStatus.NOT_FOUND.value());
		} catch (SQLException e) {
			resultMap.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
			resultMap.put("msg", e.getMessage());
		} catch (Exception e) {
			resultMap.put("msg", e.getMessage());
			resultMap.put("code", HttpStatus.SERVICE_UNAVAILABLE.value());
			e.printStackTrace();
		}
		
		return resultMap;
	}
	
}