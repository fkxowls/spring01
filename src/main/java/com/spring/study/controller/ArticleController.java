package com.spring.study.controller;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.spring.study.model.article.ArticleParam2;
import com.spring.study.model.comments.CommentsDto;
import com.spring.study.model.comments.CommentsParam;
import com.spring.study.model.comments.CommentsVo;
import com.spring.study.model.member.User;
import com.spring.study.service.ArticleService;
import com.spring.study.service.CommentsService;

import javassist.NotFoundException;
import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;



//TODO 파라미터-> Dto로 / Article객체, Comments객체 생성-> 해당하는 로직 작성
//TODO 코멘트 비밀댓글 처리  -> db에서 case문사용, 글작성자 db에서 가져온다(Oarcle 자체 세션으로 바로 재사용가능 함)
//TODO 정렬(쿼리),CASE문 사용
//TODO 대강 정했던 throw Exception을 구체적으로 Excption클래스 만들어서 던지기, Path코드 공통코드로 분리

//TODO 목요일 할거 코멘트 비밀댓글 처리, 코멘트 객체만들기
@Controller
public class ArticleController {
	private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);
	private static final int pageSize = 10;
	private static final int commentPageSize = 10;
	@Autowired
	private ArticleService articleService;
	@Autowired
	private CommentsService commentsService;
	
	// endPage restAPI
	@RequestMapping(value = "/board/article/{page}/list")
	
	public String getArticleList(@PathVariable int page, Model model,@RequestParam(required = false, defaultValue = "old") String sort) {			
		//BaseParam requestParam = new BaseParam.Builder(page, pageSize).build();
		ArticleParam2 reqParam = new ArticleParam2.Builder(page, pageSize).sort("old").build();
		List<Article> articleList = null;
		
		if (1 == page) {
			PageList<Article> returnPageList = articleService.getArticlePageListWithCount(reqParam);
			articleList = returnPageList.getList();
			model.addAttribute("totalPage", returnPageList.getTotalPage());
		} else {
			articleList = articleService.getArticleList(reqParam);
			
		}
		
//		List<ArticleDto> articleHeader = articleList.stream()
//		.map(Article::displayTitle)
//		.collect(Collectors.toList());//XXX 3 여기서 캐스팅 오류가 나는 이유는 무엇일까여
		
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
		result.put("hasNext", pageListDto.getHasNext());

		return result;
	}
	
	//XXX 1 viewArticle 로직을 한번 봐주시면 감사하겠습니다
	@RequestMapping(value = "/board/{articleId}", method = RequestMethod.GET)
	public String viewArticle(Model model, @PathVariable String articleId, User user) {
		Map<String, Object> resultState = new HashMap<>();

		Article article = null;
		boolean isNotice = articleService.isNoticeArticle(articleId);
		if(isNotice) {
			try {
				article = articleService.getNoticeArticle(articleId, user);
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
			}
		}else {
			article = articleService.getArticle(articleId);
		}
		model.addAttribute("articleVo", article.showArticle());
		model.addAttribute("modificationForm","/board/"+articleId+"/modifyForm");
		model.addAttribute("replyFormPath","/board/"+ articleId +"/replyForm");
		model.addAttribute("articleDeletePath","/board/"+articleId);
				
		return "board/viewArticle";
	}
														
	@RequestMapping(value = {"/board/writeArticleForm", "/board/{parentId}/replyForm"})
	public String moveWriteForm(@PathVariable String parentId, Model model, User user, HttpServletRequest req) {
		ArticleDto articleDto = user.getUserInfo();
		
		if(!user.isLogon()) { return "redirect:/member/loginForm"; }
		
		if(req.getRequestURI().equals("/board/"+parentId+"/replyForm")) {
			//XXX 공지글 답글달기 클릭시 팅겨내고 싶은데 그럴려면 이 메서드에 리턴값을 map으로 해서 해야하는지(= 폼이동에서 리턴값을 String이 아니라 map으로도 해도 되는건지 궁굼
			Article parentArticleInfo = articleService.getArticle(parentId);
			ArticleDto parentArticleDto = parentArticleInfo.showArticle();
			
			articleDto.setArticleId(parentArticleDto.getArticleId());				
			articleDto.setTitle("[Re]: " + parentArticleDto.getTitle());
			
			model.addAttribute("articleDto", articleDto);
			model.addAttribute("path", "/board/"+parentId+"/reply");
		}else {
			model.addAttribute("path","/board/article");
		}
		
		model.addAttribute("writeMemberId", user.getMemberId());
		return "board/addArticleForm";
	}
	
	@RequestMapping(value = "/board/article", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> writeArticle(@RequestBody ArticleDto articleDto, HttpServletRequest req){
		Map<String, Object> resultState = new HashMap<>();
		//XXX insert와 update같은 애들도 파라미터 객체로 보내서 하는게 맞는건지????? 아니면 컨트롤러로 들어오는 파라미터성격에 애들만 파라미터 객체로 만들어서 사용하는건지???
		//얘네는 파라미터 객체로 보낼게 아닌듯함 다시 dto로 보내기
		ArticleParam articleParam = new ArticleParam.Builder(articleDto.getTitle(), articleDto.getContent(), articleDto.getWriteMemberId(), articleDto.getArticleTypeCd())
													.displayStartDate(articleDto.getDisplayStartDate())
													.displayEndDate(articleDto.getDisplayEndDate())
													.build();
		try {
			req.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String writenArticleId = "";
		try {
			writenArticleId = articleService.writeArticle(articleParam);
		} catch (SQLException e) {
			resultState.put("code", HttpStatus.INTERNAL_SERVER_ERROR);
			resultState.put("msg",e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			resultState.put("code", HttpStatus.SERVICE_UNAVAILABLE);
			resultState.put("msg","알 수 없는 오류 발생");
			e.printStackTrace();
		}
	
		resultState.put("code", HttpStatus.OK);
		resultState.put("msg", "글 등록이 완료되었습니다.");
		resultState.put("redirect", "/board/" + writenArticleId);
		
		return resultState;
	}
	//XXX 이런 상황에서 파라미터객체를 쓰는것인지??
	@RequestMapping(value = "/board/{parentId}/reply", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> writeReply(@RequestBody ArticleDto articleDto, @PathVariable String parentId) {
		Map<String, Object> result = new HashMap<>();
	
		if(5 != parentId.length()) { //Article객체로 
			result.put("code", HttpStatus.BAD_REQUEST);
			result.put("msg", "입력 값이 올바르지 않습니다. 다시 확인 해주세요.");
			return result;
		}		
		
		try {
			String msg = articleService.writeReply(articleDto);
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
	
	@RequestMapping(value = "/board/{articleId}/modifyForm")//글번호만 가져오고 내용은 db에서 가져오는걸고 바꾸기
	public String moveModificationForm(@PathVariable String articleId, Model model, User user) {
		if(!user.isLogon()) { return "redirect:/member/loginForm"; }
		
		String writerId = articleService.getWriterId(articleId);
		boolean isEqualsWriterId = user.isEqualsUserId(writerId);
		
		String returnPath ="";
		if(isEqualsWriterId) {
			Article article = articleService.getArticle(articleId);
			model.addAttribute("articleVo", article);
			returnPath = "board/modifyForm";
		}else {
			model.addAttribute("msg", "해당 작성자만 가능합니다");
			model.addAttribute("code", HttpStatus.BAD_REQUEST);
			returnPath = "redirect:/board/"+articleId;
		}

		return returnPath;
	}
	
	@RequestMapping(value = "/board/{articleNo}", method = RequestMethod.PUT)
	public @ResponseBody Map<String, Object> modifyArticle(@RequestBody ArticleDto articleDto,
			@PathVariable String articleNo, User user) {
		Map<String, Object> resultState = new HashMap<>();
	
		if(!user.isLogon()) {
			resultState.put("msg", "로그인 세션 만료");
			resultState.put("code", HttpStatus.FORBIDDEN);
			return resultState;
		}
		
		try {
			articleService.modifyArticle(articleDto, user);
			resultState.put("code",HttpStatus.OK);
		} catch (NotFoundException e) {
			resultState.put("msg", e.getMessage());
			resultState.put("code", HttpStatus.NOT_FOUND);
			e.printStackTrace();
		} catch (Exception e) {
			resultState.put("msg", e.getMessage());
			resultState.put("code", HttpStatus.SERVICE_UNAVAILABLE);
			e.printStackTrace();
		}  finally {
			resultState.put("redirect", "/board/" + articleDto.getArticleId());
		}

		return resultState;
	}
	
	@RequestMapping(value = "/board/{articleNo}/comments", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> writeComment(@RequestBody CommentsDto commentsDto, User user) {
		Map<String, Object> returnMap = new HashMap<>();

		try {
			String msg = commentsService.writeComment(commentsDto, user);
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
	
	@RequestMapping(value = "/board/{articleId}/comments/{commentsPage}")
	public @ResponseBody Map<String, Object> getCommentsList(@RequestParam("writeMemberId") String articleWriterId, @PathVariable("articleId") String articleId, @PathVariable("commentsPage") int commentsPage, User user) {
		Map<String, Object> returnMap = new HashMap<>();
		String userId = user.getMemberId();
		if(!user.isLogon()) { userId = ""; }
		
		CommentsParam commentsParam = new CommentsParam.Builder(commentsPage, commentPageSize,articleId)
										 .writeMemberId(articleWriterId)//쿼리에서 처리하므로 글작성자는 주입 받을 필요가 없어짐 쿼리 검토 후 이상없으면 삭제
										 .userId(userId).build();
		PageList<CommentsVo> commentsPageList = commentsService.getCommentsPageList(commentsParam);
		
		returnMap.put("commentsList", commentsPageList.getList());
		returnMap.put("totalPage", commentsPageList.getTotalPage());
		return returnMap;
	}

	@RequestMapping(value = "/board/{articleNo}", method = RequestMethod.DELETE)
	public @ResponseBody Map<String, Object> deleteArticle(@RequestBody ArticleDto articleVo) {
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