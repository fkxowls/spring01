package com.spring.study.controller;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
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

import com.spring.study.common.model.PageList;
import com.spring.study.model.article.Article;
import com.spring.study.model.article.ArticleDto;
import com.spring.study.model.article.ArticleParam;
import com.spring.study.model.comments.CommentDto;
import com.spring.study.model.user.User;
import com.spring.study.service.ArticleService;
import com.spring.study.service.CommentsService;
import com.spring.study.service.UserFollowService;

import defalut.ArticleController;
import javassist.NotFoundException;

@Controller
public class NewArticleController {
	private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);
	private static final int pageSize = 10;
	private static final int commentPageSize = 10;
	@Autowired
	private ArticleService articleService;
	@Autowired
	private CommentsService commentsService;
	@Autowired
	private UserFollowService userFollowService;
	
	@RequestMapping(value = "/article/{page}/endPaging")
	//TODO Resolver가 작동을 안함 오류찾아내기
	public @ResponseBody Map<String, Object> getArticleList(@PathVariable int page, Model model, @RequestParam(defaultValue = "old") String sort) {																					
		ArticleParam reqParam = new ArticleParam.Builder(pageSize)
										.page(page)
										.useTotal(true)
										.sort(sort)
										.build();
		
		Map<String, Object> resultMap = new HashMap<>();
		List<Article> articleList;		
		
		if (1 == page) {
			PageList<Article> pageList = articleService.getArticlePageListWithCount(reqParam);
			articleList = pageList.getList();
			resultMap.put("totalPage", String.valueOf(pageList.getTotalPage()));
			resultMap.put("page", String.valueOf(pageList.getPage()));
			resultMap.put("pageSize", String.valueOf(pageList.getPageSize()));
		} else {
			articleList = articleService.getArticleList(reqParam);
		}
		
		List<ArticleDto> articleHeader = articleList.stream()
				.map(Article::displayArticles)
				.collect(Collectors.toList());
		resultMap.put("code", HttpStatus.OK.value());
		resultMap.put("msg", HttpStatus.OK.getReasonPhrase());
		resultMap.put("articleHeader", articleHeader);
		
		return resultMap;
	}
	
	@RequestMapping(value = "/article/{page}/hasNext")
	public @ResponseBody Map<String, Object> getArticleList(Model model, @PathVariable int page,@RequestParam(required = false, defaultValue = "old") String sort) {
		ArticleParam reqParam = new ArticleParam.Builder(pageSize)
										.page(page)
										.useMore(true)
										.sort(sort)
										.build();
		Map<String, Object> resultMap = new HashMap<>();
		
		PageList<Article> pageListDto = articleService.getArticlePageList(reqParam);
		List<Article> articleList = pageListDto.getList();
		List<ArticleDto> articleHeader = articleList.stream()
										.map(Article::displayArticles)
										.collect(Collectors.toList());
		
		resultMap.put("code", HttpStatus.OK.value());
		resultMap.put("msg", HttpStatus.OK.getReasonPhrase());
		resultMap.put("articleList", articleHeader);
		resultMap.put("hasNext", pageListDto.getHasNext());
		
		return resultMap;
	}

	@RequestMapping(value = "/board/clipboard/{userId}", method = RequestMethod.GET)
	@ResponseBody Map<String, Object> clipboard(User user, @RequestParam(defaultValue = "0") int page, @PathVariable String userId) {
		Map<String, Object> map = new HashMap<>();
		
		ArticleParam articleParam = new ArticleParam
				.Builder(pageSize)
				.useMore(true)
				.userId(user.getUserId())
				.targetUserId(userId)
				.build();
		
		PageList<Article> articlePageList = articleService.getClipboardList(articleParam);
		List<Article> articleList = articlePageList.getList();
		int totalPage = articlePageList.getTotalPage();
		int totalCount = articlePageList.getTotalCount();
		boolean hasNext = articlePageList.getHasNext();
		
		List<ArticleDto> articleDtoList = articleList.stream()
				.map(Article::displayArticles)
				.collect(Collectors.toList());
			
		map.put("articleList", articleDtoList);
		map.put("totalPage", totalPage);
		map.put("totalCount", totalCount);
		map.put("hasNext", hasNext);
		
		return map;
	}
	
	@RequestMapping(value = "/{articleId}/detail", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> viewArticle(@PathVariable String articleId, User user) {
		Map<String, Object> resultMap = new HashMap<>();
		
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
	
	@RequestMapping(value = "/article", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> writeArticle(@RequestBody ArticleDto articleDto, HttpServletRequest req, User user){
		Map<String, Object> resultMap = new HashMap<>();
			
		try {
			req.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		try {
			articleService.writeArticle(articleDto, user);//Dto -> Article
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
	
	@RequestMapping(value = "/{articleId}/reply", method = RequestMethod.POST)
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
	
	@RequestMapping(value = "/{articleId}", method = RequestMethod.PUT)
	public @ResponseBody Map<String, Object> modifyArticle(@RequestBody ArticleDto articleDto, User user) {
		Map<String, Object> resultMap = new HashMap<>();
		
		if(!user.isLogon()) {
			resultMap.put("code", HttpStatus.FORBIDDEN);
			resultMap.put("msg", "로그인 세션 만료");
			return resultMap;
		}
		
		if(!Article.checkId(articleDto.getArticleId())) { 
			resultMap.put("code", HttpStatus.BAD_REQUEST.getReasonPhrase());
			resultMap.put("msg", "잘못된 요청입니다.");
			return resultMap;
		}
		
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
	
	@RequestMapping(value = "/{articleId}", method = RequestMethod.DELETE)
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
	
	@RequestMapping(value = "/{articleId}/comments", method = RequestMethod.POST)
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
	
	//TODO 오류수정 및 효율성고민 트랜잭션범위까지 고려해보기
	@RequestMapping(value ="/testMethod2")
	public @ResponseBody Map<String, String> testMethod2() {
		Map<String, String> result = new HashMap<>();
		try {
			articleService.sortArticleRank();
			result.put("result","success");
		} catch(Exception e) {
			result.put("result","fail");
		}
		return result;
	}
	
	//TODO 쿼리 및 기능의 목적이 무엇인지 다시 생각해보기
	@RequestMapping(value = "/board/feedList/{page}", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> feedList(User user, @PathVariable int page) {
		Map<String, Object> map = new HashMap<>();
		
		List<String> followeeIdList = userFollowService.getFolloweeIds(user.getUserId());
		String followeeIds = StringUtils.join(followeeIdList, ",");
		
		ArticleParam articleParam = new ArticleParam
				.Builder(pageSize)
				.page(page)
				.useMore(true)
				.userId(user.getUserId())
				.targetUserId(followeeIds)
				.build();
		
		PageList<Article> pageList = articleService.getFeedList(articleParam); 
		
		List<Article> articleList = pageList.getList();
		int totalPage = pageList.getTotalPage();
//		int totalCount = pageList.getTotalCount();
		boolean hasNext = pageList.getHasNext();
		
		List<ArticleDto> displayArticles = articleList.stream()
			.map(Article::displayArticles)
			.collect(Collectors.toList());
		
		map.put("articleList", displayArticles);
		map.put("totalPage", totalPage);
//		map.put("totalCount", totalCount);
		map.put("hasNext", hasNext);
		
		return map;
	}
}
