package com.spring.study.board.controller;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spring.study.board.model.Article;
import com.spring.study.board.model.ArticleDto;
import com.spring.study.board.model.ArticleParam;
import com.spring.study.board.model.ArticleVo;
import com.spring.study.board.model.BoardRequestDto;
import com.spring.study.board.service.ArticleService;
import com.spring.study.comment.model.CommentPageList;
import com.spring.study.comment.model.CommentsRequestDto;
import com.spring.study.comment.service.CommentsService;
import com.spring.study.common.model.BaseParam;
import com.spring.study.common.model.PageList;
import com.spring.study.member.model.Member;

import javassist.NotFoundException;
import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;

//TODO 파라미터-> Dto로 / Article객체, Comments객체 생성-> 해당하는 로직 작성
//TODO 코멘트 비밀댓글 처리  -> db에서, 글작성자 db에서 가져온다(Oarcle 자체 세션으로 바로 재사용가능 함)
//TODO 정렬(쿼리),CASE문 사용
//TODO 대강 정했던 throw Exception을 구체적으로 Excption클래스 만들어서 던지기, Path코드 공통코드로 분리
@Controller
public class ArticleController {
	private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);
	private static final int pageSize = 10;

	@Autowired
	private ArticleService articleService;
	@Autowired
	private CommentsService commentsService;
	
	// endPage restAPI
	@RequestMapping(value = "/board/article/{page}/list")
	
	public String getArticleList(@PathVariable int page, Model model) {			
		BaseParam requestParam = new BaseParam.Builder(page, pageSize).build();
		List<Article> articleList = null;
		
		if (1 == page) {
			PageList<Article> returnPageList = articleService.getArticlePageListWithCount(requestParam);
			articleList = returnPageList.getList();
			model.addAttribute("totalPage", returnPageList.getTotalPage());
		} else {
			articleList = articleService.getArticleList(requestParam);
			//result.put("nextPage", "0");
			//result.put("totalPage", "0");
			//result.put("articleList", articleList);
		}
		
//		List<ArticleDto> articleHeader = articleList.stream()
//		.map(Article::displayTitle)
//		.collect(Collectors.toList());//XXX 여기서 캐스팅 오류가 나는 이유는 무엇일까여
		
		model.addAttribute("articleList", articleList);
		model.addAttribute("writeArticleForm","/board/writeArticleForm");
		
		return "board/listArticle2";
	}
		
	// hasNext
	@RequestMapping(value = "/board/article2/{page}/list")
	public @ResponseBody Map<String, Object> getArticleList(Model model, @PathVariable int page) {
		BaseParam req = new BaseParam.Builder(page, pageSize).useMoreView(true).build();
	
		PageList<Article> pageListDto = articleService.getArticlePageList(req);
		List<Article> articleList = pageListDto.getList();
		
//		List<ArticleDto> articleHeader = articleList.stream()
//										.map(Article::displayTitle)
//										.collect(Collectors.toList());
//		
		Map<String, Object> result = new HashMap<>();
		result.put("articleList", articleList);
		result.put("hasNext", String.valueOf(pageListDto.getHasNext()));

		return result;
	}
	
	@RequestMapping(value = "/board/{articleId}", method = RequestMethod.GET)
	public String viewArticle(Model model, @PathVariable String articleId, Member member) {
		ArticleVo articleVo = new ArticleVo();
		Map<String, Object> resultState = new HashMap<>();
		String returnURI = "";

		try {
			//XXX 아래와 같은 경우에도 Parameter객체에 추가해서 보내야하는건지??
			articleVo = articleService.getArticle(articleId, member);
			resultState.put("code",HttpStatus.OK);
			resultState.put("msg", "로그인 성공");	
			
			model.addAttribute("articleVo", articleVo);				
			returnURI = "board/viewArticle";
		} catch (SQLException e) {
			resultState.put("code", HttpStatus.FORBIDDEN);
			resultState.put("msg", e.getMessage());
			resultState.put("redirect","/board/listArticleForm");
			e.printStackTrace();
		} catch (NotFoundException e) {
			resultState.put("code", HttpStatus.UNAUTHORIZED);
			resultState.put("msg", e.getMessage());
			resultState.put("redirect","/member/loginForm.do");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			model.addAttribute("resultState", resultState);
			model.addAttribute("modificationForm","/board/modifyForm");
			model.addAttribute("replyFormPath","/board/replyForm?articleId"+articleId);
			model.addAttribute("articleDeletePath","/board/"+articleId);
		}
		
		return returnURI;
	}
	
	@RequestMapping(value = {"/board/writeArticleForm", "/board/replyForm"})
	public String moveWriteForm(@RequestParam(required = false) String articleId, Model model, Member user, HttpServletRequest req) {
		ArticleVo returnVo = new ArticleVo();

		if(user == null) { return "redirect:/member/loginForm"; }
		
		if(req.getRequestURI().equals("/board/writeReplyForm")) {
			returnVo.setArticleId(articleId);				
			returnVo.setTitle("[Re]: ");
			
			model.addAttribute("returnVo", returnVo);
			model.addAttribute("path", "/board/"+articleId+"reply");
		}else {
			model.addAttribute("path","/board/article");
		}
		
		model.addAttribute("writeMemberId", user.getMemberId());
		return "board/addArticleForm";
	}
	
	@RequestMapping(value = "/board/article", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> writeArticle(@RequestBody ArticleDto articleDto, HttpServletRequest req){
		Map<String, Object> resultMap = new HashMap<>();
		//XXX insert와 update같은 애들도 파라미터 객체로 보내서 하는게 맞는건지????? 아니면 컨트롤러로 들어오는 파라미터성격에 애들만 파라미터 객체로 만들어서 사용하는건지???? 
		ArticleParam articleParam = new ArticleParam.Builder(articleDto.getTitle(), articleDto.getContent(), articleDto.getWriteMemberId(), articleDto.getArticleTypeCd())
													.displayStartDate(articleDto.getDisplayStartDate())
													.displayEndDate(articleDto.getDisplayEndDate())
													.build();
		try {
			req.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String writenArticleId = articleService.writeArticle(articleParam);
	
		resultMap.put("code", HttpStatus.OK);
		resultMap.put("msg", "글 등록이 완료되었습니다.");
		resultMap.put("redirect", "/board/" + writenArticleId);
		
		return resultMap;
	}
	
	@RequestMapping(value = "/board/{parentId}/reply", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> writeReply(@RequestBody ArticleVo articleVo, @PathVariable String parentId) {
		Map<String, Object> result = new HashMap<>();

		if(5 != parentId.length()) { 
			result.put("code", HttpStatus.BAD_REQUEST);
			result.put("msg", "입력 값이 올바르지 않습니다. 다시 확인 해주세요.");
			return result;
		}
		articleVo.setParentId(parentId);
		
		try {
			String msg = articleService.writeReply(articleVo);
			result.put("code",HttpStatus.OK);
			result.put("msg", msg);
		} catch (NotFoundException e) {
			result.put("code", HttpStatus.NOT_FOUND);
			result.put("msg", e.getMessage());
			e.printStackTrace();
		} catch (InternalException e) {
			result.put("code", HttpStatus.INTERNAL_SERVER_ERROR);
			result.put("msg", e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			result.put("code", HttpStatus.INTERNAL_SERVER_ERROR);
			result.put("msg", "알 수 없는 오류가 발생 했습니다. 다시 시도 해주세요");
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping(value = "/board/modifyForm")
	public String moveModificationForm(@ModelAttribute ArticleDto articleDto, Model model, Member user) {
		if(null == user) {
			articleService.isEqualsWriterId(articleDto, user);
			model.addAttribute("articleVo", articleDto);
		}else {
			return "redirect:/member/loginForm";
		}
		
		return "board/modifyForm";
	}
	
	@RequestMapping(value = "/board/{articleNo}", method = RequestMethod.PUT)
	public @ResponseBody Map<String, Object> modifyArticle(@RequestBody ArticleVo articleVo,
			@PathVariable String articleNo, Member member) {
		Map<String, Object> resultState = new HashMap<>();

		try {
			articleService.modifyArticle(articleVo, member);
			resultState.put("code",HttpStatus.OK);
		} catch (NotFoundException e) {
			resultState.put("msg", e.getMessage());
			resultState.put("code", HttpStatus.NOT_FOUND);
			e.printStackTrace();
		} catch (NullPointerException e) {
			resultState.put("msg", e.getMessage());
			resultState.put("code", HttpStatus.FORBIDDEN);
			e.printStackTrace();
		} catch (Exception e) {
			resultState.put("msg", e.getMessage());
			resultState.put("code", HttpStatus.SERVICE_UNAVAILABLE);
			e.printStackTrace();
		}  finally {
			resultState.put("redirect", "/board/viewArticle.do?articleId=" + articleVo.getArticleId());
		}

		return resultState;
	}
	
	@RequestMapping(value = "/board/{articleNo}/comments", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> writeComment(@RequestBody CommentsRequestDto commentsRequestDto, Member member) {
		Map<String, Object> returnMap = new HashMap<>();

		System.out.println(member.getMemberId());
		try {
			String msg = commentsService.writeComment(commentsRequestDto, member);
			returnMap.put("code",HttpStatus.OK);
			returnMap.put("msg",msg);
		} catch (NotFoundException e) {
			returnMap.put("code", HttpStatus.NOT_FOUND);
			returnMap.put("msg",e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			returnMap.put("code", HttpStatus.SERVICE_UNAVAILABLE);
			returnMap.put("msg", "알 수 없는 오류가 발생 했습니다. 다시 시도 해주세요");
			e.printStackTrace();
		}
			return returnMap;
	}
	//글작성자를 어떻게 보내야 하는가..... 해당 글번호로 조회해서 글작성자 아이디를 가져온다?? 
	@RequestMapping(value = "/board/{articleId}/comments/{commentsPage}")
	public @ResponseBody CommentPageList getCommentsList(@RequestParam("writeMemberId") String articleWriterId, @PathVariable("articleId") String articleId, @PathVariable("commentsPage") int commentsPage, Member user) {
		CommentPageList commentPageList = new CommentPageList();
		String userId;
		
		if(null != user) {
			userId = user.getMemberId();
			commentPageList = commentsService.getCommentsPageList(articleId, commentsPage, userId, articleWriterId);
		}else {//여기 수정해야함
			System.out.println("aaaa");
			userId ="";
			commentPageList = commentsService.getCommentsPageList(articleId, commentsPage, userId, articleWriterId);
		}
		
		
		return commentPageList;
	}

	@RequestMapping(value = "/board/{articleNo}", method = RequestMethod.DELETE)
	public @ResponseBody Map<String, Object> deleteArticle(@RequestBody ArticleVo articleVo) {
		Map<String, Object> resultState = new HashMap<>();

		try {
			articleService.deleteArticle(articleVo);
			resultState.put("msg",HttpStatus.OK);
			resultState.put("path","/board/listArticleForm");
		} catch (DataIntegrityViolationException e) {
			resultState.put("msg", "댓글이 달린 글은 삭제할 수 없습니다.");
			resultState.put("code", HttpStatus.CONFLICT);
			e.printStackTrace();
		}  catch (NotFoundException e) {
			resultState.put("msg", e.getMessage());
			resultState.put("code", HttpStatus.NOT_FOUND);
			e.printStackTrace();
		} catch (Exception e) {
			resultState.put("msg", e.getMessage());
			resultState.put("code", HttpStatus.SERVICE_UNAVAILABLE);
			e.printStackTrace();
		} 

		return resultState;
	}
	
	
}
